package com.bh.rewardpoints;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactory.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@EnableScheduling
public class RewardPointsShedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RewardPointsShedulerApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		
	//	mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		return mapper;
	}
}
