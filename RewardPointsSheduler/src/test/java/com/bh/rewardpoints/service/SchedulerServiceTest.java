package com.bh.rewardpoints.service;

import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.bh.rewardpoints.exception.ISpringSchedulerException;
import com.bh.rewardpoints.model.Fields;
import com.bh.rewardpoints.model.ISpringUser;
import com.bh.rewardpoints.model.ISpringUserDetails;
import com.bh.rewardpoints.model.User;
import com.bh.rewardpoints.model.UserRoles;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerServiceTest {

	@InjectMocks
	private SchedulerService schedulerService;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private ObjectMapper objectMapper;

	private String ispringDataAccessUrl = "http://localhost:8083";

	private String rewardPointsMngSrvUrl = "http://localhost:8081";

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testSchedule() throws ISpringSchedulerException, JsonMappingException, JsonProcessingException {
		ResponseEntity usersAndPointsRE = ResponseEntity.ok("[{}]");
		Map<String, List<User>> usersMap = new HashMap<>();
		List<User> newUserList = new ArrayList<>();
		User user = new User();
		user.setUesrId("123");
		user.setBalance(123L);
		newUserList.add(user);
		User upuser = new User();
		upuser.setUesrId("300");
		upuser.setBalance(45L);
		List<User> updatedUserList = new ArrayList<>();
		updatedUserList.add(upuser);
		usersMap.put("NewUsersCreated", newUserList);
		usersMap.put("ExistingUserUpdated", updatedUserList);
		ResponseEntity userRE = ResponseEntity.ok(usersMap);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.eq(Object.class)))
		.thenReturn(usersAndPointsRE)
		.thenReturn(userRE);
		Mockito.when(objectMapper.writeValueAsString(Mockito.anyString())).thenReturn(getUserPointsISpringPayload());

		List<ISpringUser> iSpringUsersList = new ArrayList<>();
		ISpringUser iSpringUser = new ISpringUser();
		iSpringUser.setPoints(123);
		iSpringUser.setUserId("123-456-349");
		iSpringUsersList.add(iSpringUser);
		Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.any(TypeReference.class))).thenReturn(iSpringUsersList);

		ResponseEntity userDetailsRE = ResponseEntity.ok("{}");
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.eq(Object.class), Mockito.anyMap())).thenReturn(userDetailsRE);

		ISpringUserDetails iSpringUserDetailsObj = new ISpringUserDetails();
		List<Fields> fields = new ArrayList<>();
		Fields f1 = new Fields();
		f1.setName("USER_DEFINED_FIELD12");
		f1.setValue("Delicatessen Services Co., LLC");
		fields.add(f1);
		Fields f2 = new Fields();
		f2.setName("EMAIL");
		f2.setValue("Ricarda.Conely@boarshead.com");
		fields.add(f2);
		iSpringUserDetailsObj.setFields(fields);
		iSpringUserDetailsObj.setAddedDate(new Date().toString());
		iSpringUserDetailsObj.setDepartmentId("123");
		List<String> groups = new ArrayList<>();
		groups.add("wer");
		iSpringUserDetailsObj.setGroups(groups);
		iSpringUserDetailsObj.setLastLoginDate(new Date().toString());
		List<String> manageableDepartmentIds = new ArrayList<>();
		manageableDepartmentIds.add("rty");
		iSpringUserDetailsObj.setManageableDepartmentIds(manageableDepartmentIds);
		List<UserRoles> userRoles = new ArrayList<>();
		UserRoles userRole = new UserRoles();
		userRole.setManageableDepartmentIds(manageableDepartmentIds);
		userRole.setRoleId("1234-567-345");
		userRole.setRoleType("user");
		userRoles.add(userRole);
		iSpringUserDetailsObj.setUserRoles(userRoles);
		Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(ISpringUserDetails.class))).thenReturn(iSpringUserDetailsObj);
		schedulerService.schedule();
		Mockito.verify(objectMapper, times(1)).readValue(Mockito.anyString(), Mockito.eq(ISpringUserDetails.class));
	}
	@Test(expected = ISpringSchedulerException.class)
	public void testScheduleForException() throws ISpringSchedulerException, JsonMappingException, JsonProcessingException {
		ResponseEntity usersAndPointsRE = ResponseEntity.ok("[{}]");
		Map<String, List<User>> usersMap = new HashMap<>();
		List<User> newUserList = new ArrayList<>();
		User user = new User();
		user.setUesrId("123");
		user.setBalance(123L);
		newUserList.add(user);
		User upuser = new User();
		upuser.setUesrId("300");
		upuser.setBalance(45L);
		List<User> updatedUserList = new ArrayList<>();
		updatedUserList.add(upuser);
		usersMap.put("NewUsersCreated", newUserList);
		usersMap.put("ExistingUserUpdated", updatedUserList);
		ResponseEntity userRE = ResponseEntity.ok(usersMap);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.eq(Object.class))).thenThrow(new RuntimeException("Exception..."));
		schedulerService.schedule();
	}
	@Test(expected = ISpringSchedulerException.class)
	public void testScheduleForMapException() throws ISpringSchedulerException, JsonMappingException, JsonProcessingException {
		ResponseEntity usersAndPointsRE = ResponseEntity.ok("[{}]");
		Map<String, List<User>> usersMap = new HashMap<>();
		List<User> newUserList = new ArrayList<>();
		User user = new User();
		user.setUesrId("123");
		user.setBalance(123L);
		newUserList.add(user);
		User upuser = new User();
		upuser.setUesrId("300");
		upuser.setBalance(45L);
		List<User> updatedUserList = new ArrayList<>();
		updatedUserList.add(upuser);
		usersMap.put("NewUsersCreated", newUserList);
		usersMap.put("ExistingUserUpdated", updatedUserList);
		ResponseEntity userRE = ResponseEntity.ok(usersMap);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.eq(Object.class)))
		.thenReturn(usersAndPointsRE)
		.thenReturn(userRE);
		Mockito.when(objectMapper.writeValueAsString(Mockito.anyString())).thenReturn(getUserPointsISpringPayload());

		List<ISpringUser> iSpringUsersList = new ArrayList<>();
		ISpringUser iSpringUser = new ISpringUser();
		iSpringUser.setPoints(123);
		iSpringUser.setUserId("123-456-349");
		iSpringUsersList.add(iSpringUser);
		Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.any(TypeReference.class))).thenReturn(iSpringUsersList);

		ResponseEntity userDetailsRE = ResponseEntity.ok("{}");
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.eq(Object.class), Mockito.anyMap())).thenThrow(new RuntimeException("Exception..."));
		schedulerService.schedule();
	}
	@Test(expected = ISpringSchedulerException.class)
	public void testScheduleForFinalException() throws ISpringSchedulerException, JsonMappingException, JsonProcessingException {
		ResponseEntity usersAndPointsRE = ResponseEntity.ok("[{}]");
		Map<String, List<User>> usersMap = new HashMap<>();
		List<User> newUserList = new ArrayList<>();
		User user = new User();
		user.setUesrId("123");
		user.setBalance(123L);
		newUserList.add(user);
		User upuser = new User();
		upuser.setUesrId("300");
		upuser.setBalance(45L);
		List<User> updatedUserList = new ArrayList<>();
		updatedUserList.add(upuser);
		usersMap.put("NewUsersCreated", newUserList);
		usersMap.put("ExistingUserUpdated", updatedUserList);
		ResponseEntity userRE = ResponseEntity.ok(usersMap);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.eq(Object.class)))
		.thenReturn(usersAndPointsRE)
		.thenThrow(new RuntimeException("Exception..."));
		Mockito.when(objectMapper.writeValueAsString(Mockito.anyString())).thenReturn(getUserPointsISpringPayload());

		List<ISpringUser> iSpringUsersList = new ArrayList<>();
		ISpringUser iSpringUser = new ISpringUser();
		iSpringUser.setPoints(123);
		iSpringUser.setUserId("123-456-349");
		iSpringUsersList.add(iSpringUser);
		Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.any(TypeReference.class))).thenReturn(iSpringUsersList);

		ResponseEntity userDetailsRE = ResponseEntity.ok("{}");
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.eq(Object.class), Mockito.anyMap())).thenReturn(userDetailsRE);

		ISpringUserDetails iSpringUserDetailsObj = new ISpringUserDetails();
		List<Fields> fields = new ArrayList<>();
		Fields f1 = new Fields();
		f1.setName("USER_DEFINED_FIELD12");
		f1.setValue("Delicatessen Services Co., LLC");
		fields.add(f1);
		Fields f2 = new Fields();
		f2.setName("EMAIL");
		f2.setValue("Ricarda.Conely@boarshead.com");
		fields.add(f2);
		iSpringUserDetailsObj.setFields(fields);
		Mockito.when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(ISpringUserDetails.class))).thenReturn(iSpringUserDetailsObj);
		schedulerService.schedule();
	}
	private String getUserPointsISpringPayload() {
		return "[\r\n" + 
				"  {\r\n" + 
				"    \"userId\": \"2dfc2178-75b2-11ea-9ead-6ace4d08e47c\",\r\n" + 
				"    \"points\": 771\r\n" + 
				"  },\r\n" + 
				"  {\r\n" + 
				"    \"userId\": \"74fcf880-75e5-11ea-9eb7-42c5ded08f9d\",\r\n" + 
				"    \"points\": 865\r\n" + 
				"  }\r\n" + 
				"]";
	}
	private String getUserDetailsFromISpring() {
		return "{\r\n" + 
				"  \"role\": \"administrator\",\r\n" + 
				"  \"roleId\": \"eaf01662-2ae1-11e9-aa24-0242ac13000a\",\r\n" + 
				"  \"userId\": \"2dfc2178-75b2-11ea-9ead-6ace4d08e47c\",\r\n" + 
				"  \"departmentId\": \"71bcd598-c6ba-11ea-918e-82e47ebefe73\",\r\n" + 
				"  \"status\": 1,\r\n" + 
				"  \"fields\": [\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"USER_DEFINED_FIELD1\",\r\n" + 
				"      \"value\": \"113743\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"FIRST_NAME\",\r\n" + 
				"      \"value\": \"Ricarda\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"LAST_NAME\",\r\n" + 
				"      \"value\": \"Conely\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"LOGIN\",\r\n" + 
				"      \"value\": \"Ricarda.Conely@boarshead.com\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"EMAIL\",\r\n" + 
				"      \"value\": \"Ricarda.Conely@boarshead.com\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"JOB_TITLE\",\r\n" + 
				"      \"value\": \"Analyst III, Systems\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"USER_DEFINED_FIELD12\",\r\n" + 
				"      \"value\": \"Delicatessen Services Co., LLC\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"USER_DEFINED_FIELD2\",\r\n" + 
				"      \"value\": \"Sarasota - 200/300 Series\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"USER_DEFINED_FIELD9\",\r\n" + 
				"      \"value\": \"915025 Sarasota - Management Information Systems\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"USER_DEFINED_FIELD4\",\r\n" + 
				"      \"value\": \"Michelle Nixon\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"USER_DEFINED_FIELD13\",\r\n" + 
				"      \"value\": \"Castor\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"USER_DEFINED_FIELD11\",\r\n" + 
				"      \"value\": \"\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"USER_DEFINED_FIELD5\",\r\n" + 
				"      \"value\": \"\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"USER_DEFINED_FIELD10\",\r\n" + 
				"      \"value\": \"\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"USER_DEFINED_FIELD3\",\r\n" + 
				"      \"value\": \"\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"USER_DEFINED_FIELD8\",\r\n" + 
				"      \"value\": \"\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"USER_DEFINED_FIELD6\",\r\n" + 
				"      \"value\": \"Sarasota\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"USER_DEFINED_FIELD7\",\r\n" + 
				"      \"value\": \"FL\"\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"name\": \"COUNTRY\",\r\n" + 
				"      \"value\": \"224\"\r\n" + 
				"    }\r\n" + 
				"  ],\r\n" + 
				"  \"addedDate\": \"2020-04-03\",\r\n" + 
				"  \"lastLoginDate\": \"2021-11-30\",\r\n" + 
				"  \"groups\": [\r\n" + 
				"    \"c07fd7ac-ee15-11ea-a6c5-868d178ff8b2\",\r\n" + 
				"    \"541dd02e-bd80-11eb-bcd3-12833af9e441\",\r\n" + 
				"    \"39c38d6e-bd66-11eb-9787-b21998aee3c7\",\r\n" + 
				"    \"0c64e63a-bd69-11eb-91a8-e2ba74138d7f\"\r\n" + 
				"  ],\r\n" + 
				"  \"manageableDepartmentIds\": [\r\n" + 
				"    \"2dd1d670-75b2-11ea-b7d9-6ace4d08e47c\"\r\n" + 
				"  ],\r\n" + 
				"  \"userRoles\": [\r\n" + 
				"    {\r\n" + 
				"      \"roleId\": \"eaf01662-2ae1-11e9-aa24-0242ac13000a\",\r\n" + 
				"      \"roleType\": \"administrator\",\r\n" + 
				"      \"manageableDepartmentIds\": [\r\n" + 
				"        \"2dd1d670-75b2-11ea-b7d9-6ace4d08e47c\"\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"roleId\": \"eaefe76e-2ae1-11e9-b90a-0242ac13000a\",\r\n" + 
				"      \"roleType\": \"owner\",\r\n" + 
				"      \"manageableDepartmentIds\": [\r\n" + 
				"        \"2dd1d670-75b2-11ea-b7d9-6ace4d08e47c\"\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"roleId\": \"e0317452-d7e4-11ea-841f-0aad442090ab\",\r\n" + 
				"      \"roleType\": \"studio_author\"\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
	}
}
