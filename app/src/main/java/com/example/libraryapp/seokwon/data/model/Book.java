package com.example.libraryapp.seokwon.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Book: 도서 정보를 담는 데이터 모델입니다.
 * 도서관 정보나루 API 응답 데이터를 앱 내부 필드와 매핑합니다.
 */
public class Book implements java.io.Serializable {

    @SerializedName("bookname")
    private String title;

    @SerializedName("authors")
    private String author;

    @SerializedName("bookImageURL")
    private String imageUrl;

    // 상세 조회를 위한 고유 식별자 (ISBN13)
    @SerializedName("isbn13")
    private String isbn13;

    // 상세 조회 시 별도로 가져오는 정보들
    @SerializedName("publisher")
    private String publisher;

    @SerializedName("description")
    private String description;

    // 도서 분류 체계 (KDC - 한국십진분류법)
    @SerializedName("class_no")
    private String classNo;

    // 도서 발행년도
    @SerializedName("publication_year")
    private String publicationYear;

    public Book() {}

    public Book(String title, String author, String imageUrl, String isbn13) {
        this.title = title;
        this.author = author;
        this.imageUrl = imageUrl;
        this.isbn13 = isbn13;
    }

    /* Getter 및 Setter 메소드 */
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getIsbn13() { return isbn13; }
    public void setIsbn13(String isbn13) { this.isbn13 = isbn13; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getClassNo() { return classNo; }
    public void setClassNo(String classNo) { this.classNo = classNo; }

    public String getPublicationYear() { return publicationYear; }
    public void setPublicationYear(String publicationYear) { this.publicationYear = publicationYear; }
}
