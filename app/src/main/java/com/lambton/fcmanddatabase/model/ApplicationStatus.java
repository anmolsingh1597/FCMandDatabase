package com.lambton.fcmanddatabase.model;

import java.io.Serializable;

public class ApplicationStatus implements Serializable {

    private String userName;
    private String lat;
    private String lng;
    private String speed;
    private String applicationStatus;
    private String statusTimeStamp;

    public ApplicationStatus() {
    }

    public ApplicationStatus(String userName, String lat, String lng, String speed, String applicationStatus, String statusTimeStamp) {
        this.userName = userName;
        this.lat = lat;
        this.lng = lng;
        this.speed = speed;
        this.applicationStatus = applicationStatus;
        this.statusTimeStamp = statusTimeStamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getStatusTimeStamp() {
        return statusTimeStamp;
    }

    public void setStatusTimeStamp(String statusTimeStamp) {
        this.statusTimeStamp = statusTimeStamp;
    }

    @Override
    public String toString() {
        return "ApplicationStatus{" +
                "userName='" + userName + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", speed='" + speed + '\'' +
                ", applicationStatus='" + applicationStatus + '\'' +
                ", statusTimeStamp='" + statusTimeStamp + '\'' +
                '}';
    }
}
