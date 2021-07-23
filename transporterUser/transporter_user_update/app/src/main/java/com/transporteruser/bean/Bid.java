package com.transporteruser.bean;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bid implements Serializable
{

    @SerializedName("bidId")
    @Expose
    private String bidId;
    @SerializedName("leadId")
    @Expose
    private String leadId;
    @SerializedName("transporterId")
    @Expose
    private String transporterId;
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
    private final static long serialVersionUID = 4882899390678335241L;

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
