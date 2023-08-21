package com.example.blooddonarfinder;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;


public class Chatlist {
    private String userID ;
    private String userName ;
    private String description ;
    private String dateTime ;
    private ParseFile parseFile ;

    public Chatlist() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String UserID) {
        this.userID = UserID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String UserName) {
        this.userName = UserName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String Description) {
        this.description = Description;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String DateTime) {
        this.dateTime = DateTime;
    }

    public ParseFile getImageProfile() {
        return parseFile;
    }

    public void setImageProfile(ParseFile ChatFile) {
        this.parseFile = ChatFile;
    }
}
