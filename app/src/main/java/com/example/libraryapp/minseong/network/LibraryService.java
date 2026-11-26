package com.example.libraryapp.minseong.network;

import com.example.libraryapp.minseong.model.BookExistResponse;
import com.example.libraryapp.minseong.model.BookResponse;
import com.example.libraryapp.minseong.model.LibraryResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LibraryService {

    // 1. 책 검색 기능 (키워드 검색)
    @GET("srchBooks")
    Call<BookResponse> getBookLoanStatus(
            @Query("title") String title,
            @Query("format") String format,
            @Query("pageSize") int pageSize // 🔥 [팀원 기능 추가] 한 번에 가져올 개수
    );

    // 2. 도서관 위치 찾기 기능 (이 책을 소장한 도서관 목록 찾기)
    @GET("libSrchByBook")
    Call<LibraryResponse> getLibrariesByBook(
            @Query("isbn") String isbn,
            @Query("region") String region,
            @Query("format") String format,
            @Query("pageSize") int pageSize // 🔥 [팀원 기능 추가] 한 번에 가져올 개수
    );

    // 3. 진짜 대출 가능 여부 확인 (특정 도서관에 재고 물어보기)
    @GET("bookExist")
    Call<BookExistResponse> getBookExistence(
            @Query("isbn13") String isbn,     // ISBN
            @Query("libCode") String libCode, // 도서관 코드 (필수!)
            @Query("format") String format
    );
}