package org.example.response;

import org.example.Result;

import java.io.Serializable;
import java.util.List;

public class Response  implements Serializable {
    private ResponseType responseType;
    List<ResponseForCountry> data;
    List<Result> data_result_participanti;

    public Response(ResponseType responseType, List<ResponseForCountry> data, List<Result> data_result_participanti) {
        this.responseType = responseType;
        this.data = data;
        this.data_result_participanti = data_result_participanti;

    }

    public List<Result> getData_result_participanti() {
        return data_result_participanti;
    }

    public void setData_result_participanti(List<Result> data_result_participanti) {
        this.data_result_participanti = data_result_participanti;
    }

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



    public Response(ResponseType responseType, List<ResponseForCountry> responseResultList) {
        this.responseType = responseType;
        this.data = responseResultList;
    }


}
