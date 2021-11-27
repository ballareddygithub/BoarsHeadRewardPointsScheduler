package com.bh.rewardpoints.scheduler;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bh.rewardpoints.model.User;

@Component
public class RewardPointsScheduler {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${ispring.data.access.url}")
	private String ispringDataAccessUrl;
	
	@Value("${rewardpoints.management.service.url}")
	private String rewardPointsMngSrvUrl;
	
	@Scheduled(cron = "* * * 1 * ?")   // Cron for every month 1st 
	//@Scheduled(cron = "*/10 * * * * ?")   // Cron for every 10 seconds
	public void cronJobSch() {
		logger.info("Scheduler started ......");
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date now = new Date();
			String strDate = sdf.format(now);
			logger.info("Java cron job expression:: " , strDate);
			
			// Get Users and their Points from ISpringDataAccess Service
			ResponseEntity<?> responseEntity = restTemplate.getForEntity(ispringDataAccessUrl + "/rewardpoints/gamification/points", List.class);
			// Call RewardPointsManagementService
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			if(responseEntity != null)  {
				List<User> extractedUsersFromISpring = (List<User>) responseEntity.getBody();
				HttpEntity<List<User>> requestEntity = new HttpEntity<>(extractedUsersFromISpring, headers);
				URI url = new URI(rewardPointsMngSrvUrl + "/rewardpoints/scheduler/update");
				ResponseEntity<?> responseEntityPost= restTemplate.postForEntity(url, requestEntity, Map.class);
				if(responseEntityPost != null)  {
					logger.info("Users data Successfully Updated {}", responseEntityPost.getBody());
					Map<String, List<User>> allUsersMap = (Map<String, List<User>>) responseEntityPost.getBody();
					if(allUsersMap != null && allUsersMap.size() > 0) {
						List<User> existingUsersupdatedFromIspring = allUsersMap.get("NewUser");
						List<User> newUsersFromISpring = allUsersMap.get("ExistingUser");
						logger.info("existingUsersupdatedInDBFromIspring : ", existingUsersupdatedFromIspring);
						logger.info("newUsersFromISpring : ", newUsersFromISpring);
						logger.info("SuccessFully Updated Users Points in DB by RewardPointsManagementService..");
					}
				}
			}
		}catch(Exception e) {
			logger.error("Exception in Scheduler : {} ", e.getMessage());
		}
		logger.info("Scheduler end ......");
	}
}
