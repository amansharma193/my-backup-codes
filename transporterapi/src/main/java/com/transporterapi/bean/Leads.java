package com.transporterapi.bean;

import java.util.ArrayList;


public class Leads {
	private String userId,leadId,typeOfMaterial,weight,pickUpAddress,
		deliveryAddress,contactForPickup,contactForDelivery,dateOfCompletion,amount,
		timestamp,status,vehicleNumber,dealLockedWith,bidCount,transporterName="",km;

	private boolean active=true;

	private boolean rating = false;
	private boolean special=false;
	private SpecialRequirement specialRequirement;
	public Leads(boolean special) {
		super();
		this.special = special;
	}
	public String getKm() {
		return km;
	}
	public void setKm(String km) {
		this.km = km;
	}
	public Leads() {
		
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public boolean isRating() {
		return rating;
	}
	public void setRating(boolean rating) {
		this.rating = rating;
	}
	public boolean isSpecial() {
		return special;
	}
	public void setSpecial(boolean special) {
		this.special = special;
	}
	public Leads(String userId, String leadId, String typeOfMaterial, String weight, String pickUpAddress,
			String deliveryAddress, String contactForPickup, String contactForDelivery, String dateOfCompletion,
			String timestamp, String status, String vehicleNumber, String dealLockedWith, SpecialRequirement specialRequirement ,String bidCount,String transporterName,String amount,String km,boolean active) {
		super();
		this.userId = userId;
		this.leadId = leadId;
		this.typeOfMaterial = typeOfMaterial;
		this.weight = weight;
		this.pickUpAddress = pickUpAddress;
		this.deliveryAddress = deliveryAddress;
		this.contactForPickup = contactForPickup;
		this.contactForDelivery = contactForDelivery;
		this.dateOfCompletion = dateOfCompletion;
		this.timestamp = timestamp;
		this.status = status;
		this.vehicleNumber = vehicleNumber;
		this.dealLockedWith = dealLockedWith;
		this.bidCount = bidCount;
		this.transporterName = transporterName;
		this.amount = amount;
		this.km = km;
		this.active=active;
		this.specialRequirement=specialRequirement;
  }
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getTransporterName() {
		return transporterName;
	}
	public void setTransporterName(String transporterName) {
		this.transporterName = transporterName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getLeadId() {
		return leadId;
	}
	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}
	public String getTypeOfMaterial() {
		return typeOfMaterial;
	}
	public void setTypeOfMaterial(String typeOfMaterial) {
		this.typeOfMaterial = typeOfMaterial;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getPickUpAddress() {
		return pickUpAddress;
	}
	public void setPickUpAddress(String pickUpAddress) {
		this.pickUpAddress = pickUpAddress;
	}
	public String getDeliveryAddress() {
		return deliveryAddress;
	}
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	public String getContactForPickup() {
		return contactForPickup;
	}
	public void setContactForPickup(String contactForPickup) {
		this.contactForPickup = contactForPickup;
	}
	public String getContactForDelivery() {
		return contactForDelivery;
	}
	public void setContactForDelivery(String contactForDelivery) {
		this.contactForDelivery = contactForDelivery;
	}
	public String getDateOfCompletion() {
		return dateOfCompletion;
	}
	public void setDateOfCompletion(String dateOfCompletion) {
		this.dateOfCompletion = dateOfCompletion;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getVehicleNumber() {
		return vehicleNumber;
	}
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}
	public String getDealLockedWith() {
		return dealLockedWith;
	}
	public void setDealLockedWith(String dealLockedWith) {
		this.dealLockedWith = dealLockedWith;
	}
	public String getBidCount() {
		return bidCount;
	}
	public void setBidCount(String bidCount) {
		this.bidCount = bidCount;
	}
	public SpecialRequirement getSpecialRequirement() {
		return specialRequirement;
	}
	public void setSpecialRequirement(SpecialRequirement specialRequirement) {
		this.specialRequirement = specialRequirement;
	}
	
	
}
