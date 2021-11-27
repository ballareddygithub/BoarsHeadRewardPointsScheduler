package com.bh.rewardpoints.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "userProfile")
@JsonPropertyOrder({"email", "bhEntity", "balance"})
public class UserResponse {	
	
	private String email;	
	private String bhEntity;
	private Long balance;
	
	public String getBhEntity() {
		return bhEntity;
	}
	public void setBhEntity(String bhEntity) {
		this.bhEntity = bhEntity;
	}
	public Long getBalance() {
		return balance;
	}
	public void setBalance(Long balance) {
		this.balance = balance;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
