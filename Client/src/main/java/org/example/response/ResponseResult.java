package org.example.response;

import java.io.Serializable;

public class ResponseResult implements Serializable {
    String countryName;
    int score;
    public ResponseResult(String countryName, int score) {
        this.countryName = countryName;
        this.score = score;
    }
    public String getCountryName() {
        return countryName;
    }
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
    @Override
    public String toString() {
        return  countryName + ':' +
                score ;
    }
}
