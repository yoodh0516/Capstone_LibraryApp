package com.example.libraryapp.minseong.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class BookResponse {

    @SerializedName("response")
    private ResponseData response;

    // ▼▼▼ [중요] 이 함수가 없어서 에러가 난 겁니다! 꼭 있어야 해요. ▼▼▼
    public List<Book> getBooks() {
        List<Book> bookList = new ArrayList<>();
        if (response != null && response.docs != null) {
            for (BookDoc docWrapper : response.docs) {
                bookList.add(docWrapper.book);
            }
        }
        return bookList;
    }
    // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲

    // 내부 클래스 1
    private static class ResponseData {
        @SerializedName("docs")
        public List<BookDoc> docs;
    }

    // 내부 클래스 2
    private static class BookDoc {
        @SerializedName("doc")
        public Book book;
    }
}