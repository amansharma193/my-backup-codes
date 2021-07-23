package com.transporterapi.bean;
import java.util.ArrayList;

public class Transporter {
	private String transporterId;
	private String type;
	private String name;
	private String imageUrl;
	private String contactNumber;
	private String address;
	private String gstNumber = "";
	private String rating = "";
	private String token ;
	private String aadharCardNumber;
	private ArrayList<Vehicle> vehicleList;

	public Transporter() {

	}

	public String getAadharCardNumber() {
		return aadharCardNumber;
	}

	public void setAadharCardNumber(String aadharCardNumber) {
		this.aadharCardNumber = aadharCardNumber;
	}

	public Transporter(String transporterId, String type, String name, String imageUrl, String contactNumber,
			String address, String gstNumber, String rating,String token, ArrayList<Vehicle> vehicleList,String aadharCardNumber) {
		super();
		this.transporterId = transporterId;
		this.type = type;
		this.name = name;
		this.imageUrl = imageUrl;
		this.contactNumber = contactNumber;
		this.address = address;
		this.gstNumber = gstNumber;
		this.rating = rating;
		this.token = token;
		this.vehicleList = vehicleList;
		this.aadharCardNumber=aadharCardNumber;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
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

	public ArrayList<Vehicle> getVehicleList() {
		return vehicleList;
	}

	public void setVehicleList(ArrayList<Vehicle> vehicleList) {
		this.vehicleList = vehicleList;
	}

}
