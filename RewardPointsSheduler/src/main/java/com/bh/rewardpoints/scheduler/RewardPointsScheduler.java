package com.bh.rewardpoints.scheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.bh.rewardpoints.exception.ISpringSchedulerException;
import com.bh.rewardpoints.model.ISpringUser;
import com.bh.rewardpoints.model.ISpringUserDetails;
import com.bh.rewardpoints.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Named
public class RewardPointsScheduler {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Inject
	private RestTemplate restTemplate;

	@Inject
	private ObjectMapper objectMapper;

	@Value("${ispring.data.access.url}")
	private String ispringDataAccessUrl;

	@Value("${rewardpoints.management.service.url}")
	private String rewardPointsMngSrvUrl;

	@Scheduled(cron = "${scheduler.cron.expression}")  
	public void cronJobSch() throws ISpringSchedulerException {
		logger.info("Scheduler started ......");
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date now = new Date();
			String strDate = sdf.format(now);
			logger.info("Java cron job expression:: {}" , strDate);
			// Get Users and their Points from ISpringDataAccess Service
			ResponseEntity<?> responseEntity = restTemplate.exchange(ispringDataAccessUrl + "/rewardpoints/gamification/points", HttpMethod.GET, entity, Object.class);
			List<User> usersList = getUserDetailsFromISpring(responseEntity, entity);
			updateUserDetailsInRewardPointsMngDbDatabase(usersList);
		}catch(Exception e) {
			logger.info("Scheduler end ...... {}", e.getCause());
			throw new ISpringSchedulerException("Exception in fetching data from ISpring Users and their Points");
		}
		logger.info("Scheduler end ......");
	}
	private List<User> getUserDetailsFromISpring(ResponseEntity<?> responseEntity, HttpEntity<String> entity) throws ISpringSchedulerException {
		List<User> usersFinalList = new ArrayList<>();
		try {
			if(responseEntity != null) {
				String respData = objectMapper.writeValueAsString(responseEntity.getBody());
				List<ISpringUser> iSpringUsersList = objectMapper.readValue(respData, new TypeReference<List<ISpringUser>>(){});
				logger.info("iSpringUsersList size : {}, ", iSpringUsersList.size());
				if(!CollectionUtils.isEmpty(iSpringUsersList)) {
					for(ISpringUser iSpringUser: iSpringUsersList) {
						Map<String, String> map = new HashMap<>();
						map.put("userId",  iSpringUser.getUserId());
						ResponseEntity<?> userResponseEntity = restTemplate.exchange(ispringDataAccessUrl + "/rewardpoints/user/{userId}", HttpMethod.GET, entity, Object.class, map);
						if(userResponseEntity != null) {
							String iSpringUserDetails = objectMapper.writeValueAsString(userResponseEntity.getBody());
							ISpringUserDetails iSpringUserDetailsObj = objectMapper.readValue(iSpringUserDetails, ISpringUserDetails.class);
							if(!StringUtils.isEmpty(iSpringUserDetailsObj.getFields().stream().filter(fields -> fields.getName().equals("USER_DEFINED_FIELD12")).map(f -> f.getValue()).findAny().get())) {
								if(iSpringUserDetails != null) {
									User userUpdate = new User();
									userUpdate.setUesrId(iSpringUser.getUserId());
									userUpdate.setBalance(Long.valueOf(iSpringUser.getPoints()));
									userUpdate.setCumulative(Long.valueOf(iSpringUser.getPoints()));
									userUpdate.setEmail(iSpringUserDetailsObj.getFields().stream().filter(fields -> fields.getName().equals("EMAIL")).map(f -> f.getValue()).findAny().get());
									userUpdate.setBhEntity(iSpringUserDetailsObj.getFields().stream().filter(fields -> fields.getName().equals("USER_DEFINED_FIELD12")).map(f -> f.getValue()).findAny().get());
									userUpdate.setRedeemed(0L);
									usersFinalList.add(userUpdate);	
									logger.info("User : {} ", userUpdate);
								}
							}
						}
					}
				}
			}
		}catch(Exception e) {
			logger.error("Exception : {}" + e.getCause());
			throw new ISpringSchedulerException("No Users updated/created in RewardPoints database");
		}
		return usersFinalList;
	}

	private void updateUserDetailsInRewardPointsMngDbDatabase(List<User> usersFinalList) throws ISpringSchedulerException {
		// Call RewardPointsManagementService
		try {
			if(!CollectionUtils.isEmpty(usersFinalList)) {
				String iSpringUserJson = objectMapper.writeValueAsString(usersFinalList);
				HttpHeaders pointsHeaders = new HttpHeaders();
				pointsHeaders.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<String> pointsEntity = new HttpEntity<String>(iSpringUserJson,pointsHeaders);
				// make a call to RewardPointsManagement Service.
				ResponseEntity<?> msRespEntity = restTemplate.exchange(rewardPointsMngSrvUrl + "/rewardpoints/scheduler/update/user", HttpMethod.PUT, pointsEntity, Object.class);
				if(msRespEntity != null) {
					Map<String, List<User>> totalUsersMap = (Map<String, List<User>>) msRespEntity.getBody();
					logger.info("Total No of Users Created in DB : {}", totalUsersMap.get("NewUsersCreated").size());
					logger.info("Total No of Users Updated in DB : {}", totalUsersMap.get("ExistingUserUpdated").size());
				}
			}
		}catch(Exception e) {
			logger.error("Exception : {}", e.getCause());
			throw new ISpringSchedulerException("Exception in updating Reward Points in DB");
		}

	}

}
