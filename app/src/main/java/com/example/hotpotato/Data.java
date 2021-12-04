package com.example.hotpotato;

public class Data {
    public String name;
    public String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    Data(String name, int imageId) {
        this.name = name;
        //this.address = address;
        this.imageId = imageId;
    }
}
