package com.example.libraryapp.seokwon.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * 파일 위치: app/src/main/java/com/example/mapandhelloworld/data/model/BookResponse.java
 * 작성일: 2025-01-23
 * 설명: 정보나루 API의 중첩된 JSON 구조를 처리하기 위한 래퍼(Wrapper) 클래스들입니다.
 * 구조: { "response": { "docs": [ { "doc": { Book... } } ] } }
 */
public class BookResponse {

    @SerializedName("response")
    public ResponseData response;

    // 1단계: "response" 객체
    public static class ResponseData {
        @SerializedName("docs")
        public List<DocWrapper> docs = new ArrayList<>();
    }

    // 2단계: "docs" 리스트 안의 각 아이템은 "doc"이라는 키로 한 번 더 감싸져 있음
    public static class DocWrapper {
        @SerializedName("doc")
        public Book book;
    }
}