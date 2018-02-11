package com.graphycode.farmconnectdemo.Models;

/**
 * Created by kay on 12/5/17.
 */

public class User {
    private String User_id, FirstName, LastName, Phone, Profile_img;

    public User() {
    }

    public User(String user_id, String firstName, String lastName, String phone, String profile_img) {
        User_id = user_id;
        FirstName = firstName;
        LastName = lastName;
        Phone = phone;
        Profile_img = profile_img;
    }

    public String getUser_id() {
        return User_id;
    }

    public void setUser_id(String user_id) {
        User_id = user_id;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getProfile_img() {
        return Profile_img;
    }

    public void setProfile_img(String profile_img) {
        Profile_img = profile_img;
    }
}
