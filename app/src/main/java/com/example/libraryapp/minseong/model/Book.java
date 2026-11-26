package com.example.libraryapp.minseong.model;

import com.google.gson.annotations.SerializedName;

public class Book {
    // API의 변수명(JSON Key)과 내 변수명을 연결
    @SerializedName("bookname")
    private String title;          // 책 제목

    @SerializedName("authors")
    private String author;         // 저자

    @SerializedName("publisher")
    private String publisher;      // 출판사

    @SerializedName("bookImageURL")
    private String imageUrl;       // 책 표지 이미지

    @SerializedName("isbn13")
    private String isbn;           // ISBN

    @SerializedName("libName")
    private String libraryName;    // 도서관 이름 (나중에 채울 공간)

    @SerializedName("loanAvailable")
    private String loanAvailable;  // 대출 가능 여부 (나중에 채울 공간)

    private boolean isChecking = false;

    // ==========================================
    // [Getter] 데이터를 가져오는 함수들
    // ==========================================
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public String getLoanAvailable() {
        return loanAvailable;
    }

    public boolean isChecking() {
        return isChecking;
    }

    // ==========================================
    // [Setter] 데이터를 수정하는 함수들 (추가됨!)
    // 어댑터에서 도서관 정보를 채워넣으려면 이게 꼭 필요합니다.
    // ==========================================
    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public void setLoanAvailable(String loanAvailable) {
        this.loanAvailable = loanAvailable;
    }

    public void setChecking(boolean checking) {
        this.isChecking = checking;
    }
}