package com.transportervendor.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SpecialRequirement implements Serializable {

    @SerializedName("additionalMaterialType")
    @Expose
    private String additionalMaterialType;
    @SerializedName("pickupStreet")
    @Expose
    private String pickupStreet;
    @SerializedName("deliverystreet")
    @Expose
    private String deliverystreet;
    @SerializedName("remark")
    @Expose
    private String remark;
    @SerializedName("handelWithCare")
    @Expose
    private Boolean handelWithCare;

    public SpecialRequirement() {
    }

    public SpecialRequirement(String additionalMaterialType, String pickupStreet, String deliverystreet, String remark, Boolean handelWithCare) {
        this.additionalMaterialType = additionalMaterialType;
        this.pickupStreet = pickupStreet;
        this.deliverystreet = deliverystreet;
        this.remark = remark;
        this.handelWithCare = handelWithCare;
    }

    public String getAdditionalMaterialType() {
        return additionalMaterialType;
    }

    public void setAdditionalMaterialType(String additionalMaterialType) {
        this.additionalMaterialType = additionalMaterialType;
    }

    public String getPickupStreet() {
        return pickupStreet;
    }

    public void setPickupStreet(String pickupStreet) {
        this.pickupStreet = pickupStreet;
    }

    public String getDeliverystreet() {
        return deliverystreet;
    }

    public void setDeliverystreet(String deliverystreet) {
        this.deliverystreet = deliverystreet;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getHandelWithCare() {
        return handelWithCare;
    }

    public void setHandelWithCare(Boolean handelWithCare) {
        this.handelWithCare = handelWithCare;
    }

}
