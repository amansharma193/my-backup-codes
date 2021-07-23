package com.transportervendor.beans;

import java.util.*;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transporter {

	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("transporterId")
	@Expose
	private String transporterId;
	@SerializedName("type")
	@Expose
	private String type;
	@SerializedName("imageUrl")
	@Expose
	private String imageUrl;
	@SerializedName("contactNumber")
	@Expose
	private String contactNumber;
	@SerializedName("address")
	@Expose
	private String address;
	@SerializedName("aadharCardNumber")
	@Expose
	private String aadharCardNumber;
	@SerializedName("gstNumber")
	@Expose
	private String gstNumber;
	@SerializedName("rating")
	@Expose
	private String rating;
	@SerializedName("token")
	@Expose
	private String token;

	@SerializedName("vehicleList")
	@Expose
	private ArrayList<Vehicle> vehicleList;


	public Transporter() {
	}

	public Transporter(String name, String transporterId, String type, String imageUrl, String contactNumber, String address, String aadharCardNumber, String gstNumber, String rating, String token, ArrayList<Vehicle> vehicleList) {
		this.name = name;
		this.transporterId = transporterId;
		this.type = type;
		this.imageUrl = imageUrl;
		this.contactNumber = contactNumber;
		this.address = address;
		this.aadharCardNumber = aadharCardNumber;
		this.gstNumber = gstNumber;
		this.rating = rating;
		this.token = token;
		this.vehicleList = vehicleList;
	}

	public ArrayList<Vehicle> getVehicleList() {
		return vehicleList;
	}

	public void setVehicleList(ArrayList<Vehicle> vehicleList) {
		this.vehicleList = vehicleList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTransporterId() {
		return transporterId;
	}

	public void setTransporterId(String transporterId) {
		this.transporterId = transporterId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getImage() {
		return imageUrl;
	}

	public void setImage(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAadharCardNumber() {
		return aadharCardNumber;
	}

	public void setAadharCardNumber(String aadharCardNumber) {
		this.aadharCardNumber = aadharCardNumber;
	}

	public String getGstNumber() {
		return gstNumber;
	}

	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}