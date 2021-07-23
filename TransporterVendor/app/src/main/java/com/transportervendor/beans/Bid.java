package com.transportervendor.beans;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Bid implements Serializable {

	@SerializedName("bidId")
	@Expose
	private String bidId;
	@SerializedName("transporterId")
	@Expose
	private String transporterId;
	@SerializedName("leadId")
	@Expose
	private String leadId;
	@SerializedName("transporterName")
	@Expose
	private String transporterName;
	@SerializedName("amount")
	@Expose
	private String amount;
	@SerializedName("remark")
	@Expose
	private String remark;
	@SerializedName("estimatedDate")
	@Expose
	private String estimatedDate;
	public Bid() {
		
	}
	public Bid(String bidId, String leadId, String transporterId, String transporterName, String amount, String remark,
			String estimatedDate) {
		super();
		this.bidId = bidId;
		this.leadId = leadId;
		this.transporterId = transporterId;
		this.transporterName = transporterName;
		this.amount = amount;
		this.remark = remark;
		this.estimatedDate = estimatedDate;
	}
	public String getBidId() {
		return bidId;
	}
	public void setBidId(String bidId) {
		this.bidId = bidId;
	}
	public String getLeadId() {
		return leadId;
	}
	public void setLeadId(String leadId) {
		this.leadId = leadId;
	}
	public String getTransporterId() {
		return transporterId;
	}
	public void setTransporterId(String transporterId) {
		this.transporterId = transporterId;
	}
	public String getTransporterName() {
		return transporterName;
	}
	public void setTransporterName(String transporterName) {
		this.transporterName = transporterName;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getEstimatedDate() {
		return estimatedDate;
	}
	public void setEstimatedDate(String estimatedDate) {
		this.estimatedDate = estimatedDate;
	}	
}
