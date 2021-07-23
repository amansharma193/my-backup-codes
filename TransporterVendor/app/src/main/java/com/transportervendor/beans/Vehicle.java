package com.transportervendor.beans;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Vehicle implements Serializable {
	@SerializedName("vehicelId")
	@Expose
	private String vehicelId;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("count")
	@Expose
	private String count;
	@SerializedName("imgUrl")
	@Expose
	private String imgUrl;



	public String getVehicleId() {
		return vehicelId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicelId = vehicleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getImageUrl() {
		return imgUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imgUrl = imgUrl;
	}

	public Vehicle(String vehicelId, String name, String count, String imgUrl) {
		this.vehicelId = vehicelId;
		this.name = name;
		this.count = count;
		this.imgUrl = imgUrl;
	}

	public Vehicle() {
		super();
	}
	
}
