package org.example.response;

import org.example.Result;

import java.io.Serializable;
import java.util.List;

public class Response  implements Serializable {
    private ResponseType responseType;
    List<Result> data;

    public List<Result> getData() {
        return data;
    }

    public void setData(List<Result> data) {
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

    public Response(ResponseType responseType, List<Result> responseResultList) {
        this.responseType = responseType;
        this.data = responseResultList;
    }

    private List<ResponseResult> responseResultList;
}
