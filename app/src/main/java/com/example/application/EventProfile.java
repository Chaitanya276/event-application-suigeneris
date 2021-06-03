package com.example.application;

public class EventProfile {

    public String player_email;
    public String player_name;
    public String player_contact;
    public String player_amount;
    public String player_event;
    public String player_uid;

    public EventProfile() {

    }

    public EventProfile(String player_event, String player_name, String player_email, String player_contact, String player_amount, String player_uid) {
        this.player_event = player_event;
        this.player_name = player_name;
        this.player_email = player_email;
        this.player_contact = player_contact;
        this.player_amount = player_amount;
        this.player_uid = player_uid;

    }

    public String getPlayer_uid() {
        return player_uid;
    }

    public void setPlayer_uid(String player_uid) {
        this.player_uid = player_uid;
    }

    public String getPlayer_email() {
        return player_email;
    }

    public void setPlayer_email(String player_email) {
        this.player_email = player_email;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }

    public String getPlayer_contact() {
        return player_contact;
    }

    public void setPlayer_contact(String player_contact) {
        this.player_contact = player_contact;
    }

    public String getPlayer_amount() {
        return player_amount;
    }

    public void setPlayer_amount(String player_amount) {
        this.player_amount = player_amount;
    }

    public String getPlayer_event() {
        return player_event;
    }

    public void setPlayer_event(String player_event) {
        this.player_event = player_event;
    }


}
