package org.example;



import java.util.Objects;

public class Participant {
    private final int id;
    private int score;
    private final String country;

    public Participant(int id, int score, String country) {
        this.id = id;
        this.score = score;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participant )) return false;
        Participant that = (Participant) o;
        return Objects.equals(id, that.id) && Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, country);
    }
}