package com.transporteruser.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Lead implements Serializable {

    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("leadId")
    @Expose
    private String leadId;
    @SerializedName("typeOfMaterial")
    @Expose
    private String typeOfMaterial;
    @SerializedName("weight")
    @Expose
    private String weight;
    @SerializedName("pickUpAddress")
    @Expose
    private String pickUpAddress;
    @SerializedName("deliveryAddress")
    @Expose
    private String deliveryAddress;
    @SerializedName("contactForPickup")
    @Expose
    private String contactForPickup;
    @SerializedName("contactForDelivery")
    @Expose
    private String contactForDelivery;
    @SerializedName("dateOfCompletion")
    @Expose
    private String dateOfCompletion;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("status")
    @Expose
    private String status = "";
    @SerializedName("vehicleNumber")
    @Expose
    private String vehicleNumber;
    @SerializedName("dealLockedWith")
    @Expose
    private String dealLockedWith="";
    @SerializedName("bidCount")
    @Expose
    private String bidCount="0";
    @SerializedName("transporterName")
    @Expose
    private String transporterName;
    @SerializedName("km")
    @Expose
    private String km="";
    @SerializedName("amount")
    @Expose
    private String amount="";

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

    public String getTransporterName() {
        return transporterName;
    }

    public void setTransporterName(String transporterName) {
        this.transporterName = transporterName;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
