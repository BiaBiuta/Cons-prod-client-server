package org.example.request;

import org.example.Result;

import java.io.Serializable;
import java.util.List;

public class Request implements Serializable {
    private RequestType requestType;
    List<Result> data;
    String country;//fiecare client e cate o tara

    public Request(RequestType requestType, List<Result> data, String country) {
        this.requestType = requestType;
        this.data = data;
        this.country = country;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public List<Result> getData() {
        return data;
    }

    public void setData(List<Result> data) {
        this.data = data;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
