package com.example.libraryapp.seokwon.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 파일 위치: app/src/main/java/com/example/mapandhelloworld/data/model/BookDetailResponse.java
 * 설명: 도서관 정보나루의 도서 상세 조회(srchDtlList) API의 JSON 구조를 처리하는 래퍼(Wrapper) 클래스입니다.
 * 구조: { "response": { "detail": [ { "book": { Book... } } ] } }
 */
public class BookDetailResponse {

    @SerializedName("response")
    public ResponseData response;

    // 1단계: "response" 객체
    public static class ResponseData {
        @SerializedName("detail")
        public List<DetailWrapper> detail = new ArrayList<>();
    }

    // 2단계: "detail" 리스트 안의 각 아이템은 "book"이라는 키로 감싸져 있음
    public static class DetailWrapper {
        @SerializedName("book")
        public Book book;
    }
}
