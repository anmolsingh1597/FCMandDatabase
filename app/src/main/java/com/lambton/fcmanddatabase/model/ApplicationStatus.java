package com.lambton.fcmanddatabase.model;

import java.io.Serializable;

public class ApplicationStatus implements Serializable {

    private String applicationStatus;
    private String statusTimeStamp;

    public ApplicationStatus() {
    }

    public ApplicationStatus(String applicationStatus, String statusTimeStamp) {
        this.applicationStatus = applicationStatus;
        this.statusTimeStamp = statusTimeStamp;
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
                "applicationStatus='" + applicationStatus + '\'' +
                ", statusTimeStamp='" + statusTimeStamp + '\'' +
                '}';
    }
}
