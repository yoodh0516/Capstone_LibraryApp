package com.example.libraryapp.seokwon.data.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 파일 위치: app/src/main/java/com/example/mapandhelloworld/data/api/RetrofitClient.java
 * 수정일: 2025-01-23
 * 설명: Retrofit 객체를 생성하고 관리하는 싱글톤 클래스.
 * [수정] 디버깅을 위한 HttpLoggingInterceptor 추가 (통신 로그 출력)
 */
public class RetrofitClient {

    private static final String BASE_URL = "http://20.196.136.121/";
    private static Retrofit retrofit = null;

    public static LibraryApiService getService() {
        if (retrofit == null) {

            // 1. 로그를 찍어주는 인터셉터 생성
            // Level.BODY: 주고받는 데이터의 내용(JSON)까지 전부 보여줌
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 2. 위 인터셉터를 장착한 OkHttp 클라이언트 생성
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            // 3. Retrofit 생성 시 클라이언트 연결
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // [중요] 여기서 연결!
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(LibraryApiService.class);
    }
}