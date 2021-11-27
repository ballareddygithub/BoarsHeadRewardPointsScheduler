package com.bh.rewardpoints.model;

public class User {
	
	private String uesrId;
	
	private String email;
	
	private String bhEntity;
	
	private Long redeemed;
	
	private Long balance;
	
	private Long cumulative;

	public String getUesrId() {
		return uesrId;
	}

	public void setUesrId(String uesrId) {
		this.uesrId = uesrId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

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

	public Long getCumulative() {
		return cumulative;
	}

	public void setCumulative(Long cumulative) {
		this.cumulative = cumulative;
	}
	
	public Long getRedeemed() {
		return redeemed;
	}

	public void setRedeemed(Long redeemed) {
		this.redeemed = redeemed;
	}

	@Override
	public String toString() {
		return "User [uesrId=" + uesrId + ", email=" + email + ", bhEntity=" + bhEntity + ", redeemed=" + redeemed
				+ ", balance=" + balance + ", cumulative=" + cumulative + "]";
	}
	
	
}
