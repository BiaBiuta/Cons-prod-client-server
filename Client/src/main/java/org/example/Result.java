package org.example;

import java.io.Serializable;

public class Result implements Serializable {
    private int id;
    private int score;
    private String countryName;

    public Result(int id, int score, String countryName) {
        this.id = id;
        this.score = score;
        this.countryName = countryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
