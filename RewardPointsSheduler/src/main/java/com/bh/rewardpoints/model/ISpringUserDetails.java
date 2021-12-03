package com.bh.rewardpoints.model;

import java.util.List;

public class ISpringUserDetails {

	private String role;
	private String roleId;
	private String userId;
	private String departmentId;
	private int status;
	private List<Fields> fields;
	private String addedDate;
	private String lastLoginDate;
	private List<String> groups;
	private List<String> manageableDepartmentIds;
	private List<UserRoles> userRoles;
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public List<Fields> getFields() {
		return fields;
	}
	public void setFields(List<Fields> fields) {
		this.fields = fields;
	}
	public String getAddedDate() {
		return addedDate;
	}
	public void setAddedDate(String addedDate) {
		this.addedDate = addedDate;
	}
	public String getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(String lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public List<String> getGroups() {
		return groups;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	public List<String> getManageableDepartmentIds() {
		return manageableDepartmentIds;
	}
	public void setManageableDepartmentIds(List<String> manageableDepartmentIds) {
		this.manageableDepartmentIds = manageableDepartmentIds;
	}
	public List<UserRoles> getUserRoles() {
		return userRoles;
	}
	public void setUserRoles(List<UserRoles> userRoles) {
		this.userRoles = userRoles;
	}
	
	
}
