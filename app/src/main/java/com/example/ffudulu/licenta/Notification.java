package com.example.ffudulu.licenta;

import java.io.Serializable;

import users.UserMedic;
import users.UserPersonalData;

/**
 * Created by ffudulu on 12-Jun-17.
 */

public class Notification implements Serializable {
    private String message;
    private UserMedic userMedic;
    private UserPersonalData userPacient;
    private String acces;
    private String priority;
    private String type;
    private String handeled;
    private String dateAndTime;

    public Notification(String message, UserPersonalData userPacient, String acces, String priority,
                        String type, String dateAndTime) {
        this.message = message;
        this.userPacient = userPacient;
        this.acces = acces;
        this.priority = priority;
        this.type = type;
        this.dateAndTime = dateAndTime;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public Notification(String message, UserMedic userMedic, String acces, String priority,
                        String type, String dateAndTime) {

        this.message = message;
        this.userMedic = userMedic;
        this.acces = acces;
        this.priority = priority;
        this.type = type;
        this.dateAndTime = dateAndTime;

    }

    public Notification() {
    }

    public String getMessage() {
        return message;
    }

    public UserMedic getUserMedic() {
        return userMedic;
    }

    public UserPersonalData getUserPacient() {
        return userPacient;
    }

    public String getAcces() {
        return acces;
    }

    public String getPriority() {
        return priority;
    }

    public String getType() {
        return type;
    }

    public String getHandeled() {
        return handeled;
    }

    public void setHandeled(String handeled) {
        this.handeled = handeled;
    }
}
