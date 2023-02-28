package com.example.socketchatroom.events;

import java.util.Date;

public class LoginEvent {

    private String username;
    private String chatroomName;
    private Date time;

    public LoginEvent(String username, String chatroomName) {
        this.username = username;
        this.chatroomName = chatroomName;
        time = new Date();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getChatroomName() {
        return chatroomName;
    }

    public void setChatroomName(String chatroomName) {
        this.chatroomName = chatroomName;
    }
}
