package com.bh.rewardpoints.model;

import java.util.List;

public class UserRoles {

	private String roleId;
	private String roleType;
	private List<String> manageableDepartmentIds;
	
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public List<String> getManageableDepartmentIds() {
		return manageableDepartmentIds;
	}
	public void setManageableDepartmentIds(List<String> manageableDepartmentIds) {
		this.manageableDepartmentIds = manageableDepartmentIds;
	}
	
	
}
