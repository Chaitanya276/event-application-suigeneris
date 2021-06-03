package com.example.application;

public class UserProfile {
    public UserProfile() {

    }

    public int getEventCount() {
        return eventCount;
    }

    public String user_email;
    public String user_name;
    public String user_post;
    public int eventCount;

    public  UserProfile(String user_name, String user_email, String user_post, int eventCount) {
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_post = user_post;
        this.eventCount = eventCount;

    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_post() {
        return user_post;
    }


}
