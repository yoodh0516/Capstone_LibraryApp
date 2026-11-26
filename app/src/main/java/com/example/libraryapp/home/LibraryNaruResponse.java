package com.example.libraryapp.home;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LibraryNaruResponse {
    @SerializedName("response") public ResponseData response;

    public static class ResponseData {
        @SerializedName("libs") public List<LibWrapper> libs;
        @SerializedName("numFound") public int numFound;
    }

    public static class LibWrapper {
        @SerializedName("lib") public LibraryItem lib;
    }

    public static class LibraryItem {
        @SerializedName("libName") public String libName;
        @SerializedName("address") public String address;
        @SerializedName("tel") public String tel;
        @SerializedName("latitude") public String latitude;
        @SerializedName("longitude") public String longitude;
        @SerializedName("operatingTime") public String operatingTime;
        @SerializedName("closed") public String closed;
        @SerializedName("libCode") public String libCode;
        @SerializedName("homepage") public String homepage;
    }
}