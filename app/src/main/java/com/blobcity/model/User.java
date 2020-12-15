package com.blobcity.model;

import java.io.Serializable;

public class User implements Serializable {

    public String createdon;
    public String deviceuniqueid;
    public String username;
    public String firebaseToken;


    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }

    public String getDeviceuniqueid() {
        return deviceuniqueid;
    }

    public void setDeviceuniqueid(String deviceuniqueid) {
        this.deviceuniqueid = deviceuniqueid;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}
