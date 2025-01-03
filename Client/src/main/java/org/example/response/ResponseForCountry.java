package org.example.response;

import java.io.Serializable;

public class ResponseForCountry implements Serializable {
    int score;
    String country;

    public ResponseForCountry(int score, String country) {
        this.score = score;
        this.country = country;
    }

    public int getScore() {
        return score;
    }

    public String getCountry() {
        return country;
    }
}
