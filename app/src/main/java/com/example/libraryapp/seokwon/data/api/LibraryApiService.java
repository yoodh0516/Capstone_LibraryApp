package com.example.libraryapp.seokwon.data.api;

import com.example.libraryapp.seokwon.data.model.BookDetailResponse;
import com.example.libraryapp.seokwon.data.model.BookResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 파일 위치: app/src/main/java/com/example/mapandhelloworld/data/api/LibraryApiService.java
 * 작성일: 2025-01-23
 * 설명: 정보나루 API 서버에 어떤 요청을 보낼지 정의하는 인터페이스(메뉴판)입니다.
 * Retrofit 라이브러리가 이 코드를 보고 실제 통신 코드를 자동으로 만들어줍니다.
 */
public interface LibraryApiService {

    // 정보나루 - 인기도서(대출 급상승) 조회 API
    @GET("loanItemSrch")
    Call<BookResponse> getPopularBooks(
//            @Query("authKey") String authKey,  // 인증키
            @Query("startDt") String startDt,  // 검색 시작일 (YYYY-MM-DD)
            @Query("endDt") String endDt,      // 검색 종료일 (YYYY-MM-DD)
            @Query("format") String format     // 응답 형식 (json)
    );

    // 정보나루 - 도서 상세 조회 API (줄거리 등)
    @GET("srchDtlList")
    Call<BookDetailResponse> getBookDetail(
            @Query("isbn13") String isbn13,    // 13자리 ISBN
            @Query("format") String format     // 응답 형식 (json)
    );
}