package com.example.libraryapp.minseong.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // 도서 정보 나루 API 기본 주소
    private static final String BASE_URL = "http://20.196.136.121/";
    private static Retrofit retrofit = null;

    // 이 함수를 부르면 통신 연결체를 가져다 줍니다. (싱글톤 패턴)
    public static LibraryService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // JSON을 자바 객체로 바꿔주는 기계 추가
                    .build();
        }
        return retrofit.create(LibraryService.class);
    }
}