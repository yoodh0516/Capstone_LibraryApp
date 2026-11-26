package com.example.libraryapp.seokwon.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * FavoriteLibrary: 즐겨찾기에 저장된 도서관 정보를 담는 엔티티입니다.
 * 인터넷이 없는 환경에서도 정보를 볼 수 있도록 상세 정보를 포함합니다.
 */
@Entity(tableName = "favorite_libraries")
public class FavoriteLibrary {

    @PrimaryKey
    @NonNull
    private String name;

    private String address;
    private double latitude;
    private double longitude;
    private String tel;

    // 상세 화면 표시를 위한 추가 정보 (오프라인 지원용)
    private String homepage;
    private String operatingTime;
    private String closedDays;

    public FavoriteLibrary(@NonNull String name, String address, double latitude, double longitude, String tel, String homepage, String operatingTime, String closedDays) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tel = tel;
        this.homepage = homepage;
        this.operatingTime = operatingTime;
        this.closedDays = closedDays;
    }

    @NonNull
    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getTel() { return tel; }
    public String getHomepage() { return homepage; }
    public String getOperatingTime() { return operatingTime; }
    public String getClosedDays() { return closedDays; }
}
