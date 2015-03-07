package com.pushamp.sdk;

/**
 * Created by Andrey Kovrov on 07.03.15
 * All rights reserved.
 */
public class Settings {

    String senderId;
    String apiKey;
    String vendorId;


    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }
}
