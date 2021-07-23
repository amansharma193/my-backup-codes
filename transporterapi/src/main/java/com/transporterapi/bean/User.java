package com.transporterapi.bean;

public class User {
	private String userId;
	private String name;
	private String address;
	private String contactNumber;
	private String imageUrl;
	private String token;

	public User() {
	}

	public User(String userId, String name, String address, String contactNumber, String imageUrl, String token) {
		super();
		this.userId = userId;
		this.name = name;
		this.address = address;
		this.contactNumber = contactNumber;
		this.imageUrl = imageUrl;
		this.token = token;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
