package com.example.findmyrides;

public class Message {

    private String userName;
    private String userDay;
    private String userTime;
    private String userFrom;
    private String userDes;
    private String acc;
    private String key;
    private String profileName;
    private String profileEmail;
    private String profilePhoNum;
    private String finalName;

    public Message(String userName, String userDay, String userTime, String userFrom, String userDes, String acc, String key) {
        this.userName = userName;
        this.userDay = userDay;
        this.userTime = userTime;
        this.userFrom = userFrom;
        this.userDes = userDes;
        this.acc = acc;
        this.key = key;
    }
    public Message(String profileName, String profileEmail, String profilePhoNum, String finalName){
        this.profileName = profileName;
        this.profileEmail = profileEmail;
        this.profilePhoNum = profilePhoNum;
        this.finalName = finalName;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserDay() {
        return userDay;
    }

    public String getUserTime() {
        return userTime;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public String getUserDes() {
        return userDes;
    }

    public String getAcc() {
        return acc;
    }

    public String getKey() {
        return key;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getProfileEmail() {
        return profileEmail;
    }

    public String getProfilePhoNum() {
        return profilePhoNum;
    }

    public String getFinalName() {
        return finalName;
    }
}
