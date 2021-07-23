package com.freelannceritservices.digitalsignature;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Family {
    private String wardNumber,colony,name,relative,relation,remarks,signUrl,id,gender,occupation,mobile,whatsapp,address,cast;

    public Family(String wardNumber, String colony, String name, String relative, String relation, String remarks, String signUrl, String id, String gender, String occupation, String mobile, String whatsapp, String address, String cast) {
        this.wardNumber = wardNumber;
        this.colony = colony;
        this.name = name;
        this.relative = relative;
        this.relation = relation;
        this.remarks = remarks;
        this.signUrl = signUrl;
        this.id = id;
        this.gender = gender;
        this.occupation = occupation;
        this.mobile = mobile;
        this.whatsapp = whatsapp;
        this.address = address;
        this.cast = cast;
    }
    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id);
            obj.put("wardNumber", wardNumber);
            obj.put("colony", colony);
            obj.put("name", name);
            obj.put("relative", relative);
            obj.put("relation", relation);
            obj.put("remarks", remarks);
            obj.put("signature", signUrl);
            obj.put("gender", gender);
            obj.put("occupation", occupation);
            obj.put("mobile", mobile);
            obj.put("whatsapp", whatsapp);
            obj.put("address", address);
            obj.put("caste", cast);

        } catch (JSONException e) {
            Log.e("spanshoe","error"+e.getMessage());
        }
        return obj;
    }
    public Family() {
    }

    public String getColony() {
        return colony;
    }

    public void setColony(String colony) {
        this.colony = colony;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelative() {
        return relative;
    }

    public void setRelative(String relative) {
        this.relative = relative;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWardNumber() {
        return wardNumber;
    }

    public void setWardNumber(String wardNumber) {
        this.wardNumber = wardNumber;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSignUrl() {
        return signUrl;
    }

    public void setSignUrl(String signUrl) {
        this.signUrl = signUrl;
    }
}
