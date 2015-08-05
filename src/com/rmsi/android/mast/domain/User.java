package com.rmsi.android.mast.domain;

public class User {
	// UserId INTEGER PRIMARY KEY AUTOINCREMENT, UserName TEXT, Password TEXT
	private Long userId;

	private String userName;

	private String password;

	private String newPass;

	private String cnfrmPass;

	private String roleName;

	public String getCnfrmPass() {
		return cnfrmPass;
	}

	public String getNewPass() {
		return newPass;
	}

	public String getPassword() {
		return password;
	}

	public String getRoleName() {
		return roleName;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setCnfrmPass(String cnfrmPass) {
		this.cnfrmPass = cnfrmPass;
	}

	public void setNewPass(String newPass) {
		this.newPass = newPass;
	}

	public void setPassword(String Password) {
		this.password = Password;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public void setUserId(Long UserId) {
		this.userId = UserId;
	}

	public void setUserName(String UserName) {
		this.userName = UserName;
	}
}
