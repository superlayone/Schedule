package com.example.schedule;

public class UserInfo {

	private String email;
	private String username;
	private String password;
	private String image;
	private String userId;
	public UserInfo() {
		// TODO Auto-generated constructor stub
		this.email = null;
		this.password = null;
		this.username = null;
		this.image = null;
		this.userId = null;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	

}
