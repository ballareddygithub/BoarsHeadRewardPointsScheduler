package com.bh.rewardpoints.scheduler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.bh.rewardpoints.model.ISpringUser;
import com.bh.rewardpoints.model.ISpringUserDetails;
import com.bh.rewardpoints.model.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
//@RestController
//@RequestMapping("/v1")
//@CrossOrigin
public class RewardPointsScheduler {

	private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${ispring.data.access.url}")
	private String ispringDataAccessUrl;

	@Value("${rewardpoints.management.service.url}")
	private String rewardPointsMngSrvUrl;

	//@Scheduled(cron = "* * * 1 * ?")   // Cron for every month 1st 
	@Scheduled(cron = "*/30 * * * * ?")   // Cron for every 30 seconds
	@GetMapping("/testScheduler")
	public void cronJobSch() {
		logger.info("Scheduler started ......");
		try {
			//ResponseEntity<?> responseEntity = restTemplate.exchange(rwpointsApiBaseUrl + "/rewardpoints/users", HttpMethod.GET, entity, List.class);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date now = new Date();
			String strDate = sdf.format(now);
			logger.info("Java cron job expression:: " , strDate);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			// Get Users and their Points from ISpringDataAccess Service
			ResponseEntity<?> responseEntity = restTemplate.exchange(ispringDataAccessUrl + "/rewardpoints/gamification/points", HttpMethod.GET, entity,  Object.class);
			// Call RewardPointsManagementService
			if(responseEntity != null) {
				String respData = objectMapper.writeValueAsString(responseEntity.getBody());
				List<ISpringUser> iSpringUsersList = objectMapper.readValue(respData, new TypeReference<List<ISpringUser>>(){});
				logger.info("iSpringUsersList size : {}, ", iSpringUsersList.size());
				List<User> usersFinalList = new ArrayList<>();
				if(!CollectionUtils.isEmpty(iSpringUsersList)) {
					//iSpringUsersList.stream().forEach(user -> {
					int i = 0;
						for(ISpringUser iSpringUser: iSpringUsersList) {
						Map<String, String> map = new HashMap<>();
						map.put("userId",  iSpringUser.getUserId());
						ResponseEntity<?> userResponseEntity = restTemplate.exchange(ispringDataAccessUrl + "/rewardpoints/user/{userId}", HttpMethod.GET, entity, Object.class, map);
						if(userResponseEntity != null) {
							try {
								String iSpringUserDetails = objectMapper.writeValueAsString(userResponseEntity.getBody());
								ISpringUserDetails iSpringUserDetailsObj = objectMapper.readValue(iSpringUserDetails, ISpringUserDetails.class);
								if(iSpringUserDetails != null) {
									User userUpdate = new User();
									userUpdate.setUesrId(iSpringUser.getUserId());
									userUpdate.setBalance(Long.valueOf(iSpringUser.getPoints()));
									userUpdate.setCumulative(Long.valueOf(iSpringUser.getPoints()));
									userUpdate.setEmail(iSpringUserDetailsObj.getFields().stream().filter(fields -> fields.getName().equals("EMAIL")).map(f -> f.getValue()).findAny().get());
									userUpdate.setBhEntity(iSpringUserDetailsObj.getFields().stream().filter(fields -> fields.getName().equals("USER_DEFINED_FIELD7")).map(f -> f.getValue()).findAny().get());
									userUpdate.setRedeemed(0L);
									usersFinalList.add(userUpdate);	
									logger.info("User : {} ", userUpdate);
								}
							} catch (JsonProcessingException e) {
								e.printStackTrace();
							}
						}
						if(i == 5) {
							break;
						}
						i++;
					}
					//});
				}
				if(!CollectionUtils.isEmpty(usersFinalList)) {
					String iSpringUserJson = objectMapper.writeValueAsString(usersFinalList);
					HttpHeaders pointsHeaders = new HttpHeaders();
					pointsHeaders.setContentType(MediaType.APPLICATION_JSON);
					HttpEntity<String> pointsEntity = new HttpEntity<String>(iSpringUserJson,pointsHeaders);
					// make a call to RewardPointsManagement Service.
					ResponseEntity<?> msRespEntity = restTemplate.exchange(rewardPointsMngSrvUrl + "/rewardpoints/scheduler/update/user", HttpMethod.PUT, pointsEntity, Object.class);

					if(msRespEntity != null) {
						 Map<String, List<User>> totalUsersMap = (Map<String, List<User>>)msRespEntity.getBody();
						 logger.info("Total No of Users Created in DB : {}", totalUsersMap.get("NewUsersCreated").size());
						 logger.info("Total No of Users Updated in DB : {}", totalUsersMap.get("ExistingUserUpdated").size());
					}else {
						logger.info("No Users updated/created.....");
					}
				}

			}else {
				logger.info("Exception in fetching the User Points from ISpring Data Access Service");
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			logger.error("Exception in Scheduler : {} ", e.getMessage());
		}
		logger.info("Scheduler end ......");
	}

}
