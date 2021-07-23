package com.transporterapi.bean;

public class SpecialRequirement {
	private String additionalMaterialType;
	private String pickupStreet;
	private String deliverystreet;
	private String remark;
	private boolean handelWithCare = false;
	public SpecialRequirement(String additionalMaterialType, String pickupStreet, String deliverystreet, String remark,
			boolean handelWithCare) {
		super();
		this.additionalMaterialType = additionalMaterialType;
		this.pickupStreet = pickupStreet;
		this.deliverystreet = deliverystreet;
		this.remark = remark;
		this.handelWithCare = handelWithCare;
	}
	
	public SpecialRequirement() {
		super();
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
	public boolean isHandelWithCare() {
		return handelWithCare;
	}
	public void setHandelWithCare(boolean handelWithCare) {
		this.handelWithCare = handelWithCare;
	}
	
	

}
