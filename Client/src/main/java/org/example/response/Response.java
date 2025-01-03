package org.example.response;

import org.example.Result;

import java.io.Serializable;
import java.util.List;

public class Response  implements Serializable {
    private ResponseType responseType;
    List<ResponseForCountry> data;

    public List<ResponseForCountry> getData() {
        return data;
    }

    public void setData(List<ResponseForCountry> data) {
        this.data = data;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public List<ResponseResult> getResponseResultList() {
        return responseResultList;
    }

    public void setResponseResultList(List<ResponseResult> responseResultList) {
        this.responseResultList = responseResultList;
    }

    public Response(ResponseType responseType, List<ResponseForCountry> responseResultList) {
        this.responseType = responseType;
        this.data = responseResultList;
    }

    private List<ResponseResult> responseResultList;
}
