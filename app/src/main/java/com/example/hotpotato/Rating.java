package com.example.hotpotato;

public class Rating {
    public String Desc;

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public int getRatingNum() {
        return ratingNum;
    }

    public void setRatingNum(int ratingNum) {
        this.ratingNum = ratingNum;
    }

    public int ratingNum;

    public Rating(String Desc, int ratingNum) {
        this.Desc = Desc;
        this.ratingNum = ratingNum;
    }

}
