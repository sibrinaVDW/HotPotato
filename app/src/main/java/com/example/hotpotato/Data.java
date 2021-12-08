package com.example.hotpotato;

public class Data {
    public String userID;
    public String name;
    public String extraInfo;

    public String getAddress() {
        return extraInfo;
    }

    public void setAddress(String address) {
        this.extraInfo = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int imageId;

    Data(String name, String extraInfo, int imageId) {
        this.name = name;
        this.extraInfo = extraInfo;
        this.imageId = imageId;
    }

    Data(String userID, String name, String extraInfo, int imageId) {
        this.userID = userID;
        this.name = name;
        this.extraInfo = extraInfo;
        this.imageId = imageId;
    }
}
