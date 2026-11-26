package com.example.libraryapp.minseong.model;

import com.google.gson.annotations.SerializedName;

public class BookExistResponse {
    @SerializedName("response")
    private ResponseData response;

    public ResponseData getResponse() {
        return response;
    }

    public static class ResponseData {
        @SerializedName("result")
        private ResultData result;

        public ResultData getResult() {
            return result;
        }
    }

    public static class ResultData {
        @SerializedName("loanAvailable")
        private String loanAvailable; // "Y"면 가능, "N"이면 대출중(불가)

        public String getLoanAvailable() {
            return loanAvailable;
        }
    }
}