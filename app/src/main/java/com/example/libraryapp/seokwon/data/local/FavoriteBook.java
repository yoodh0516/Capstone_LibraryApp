package com.example.libraryapp.seokwon.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * FavoriteBook: 즐겨찾기에 저장된 도서 정보를 담는 엔티티입니다.
 */
@Entity(tableName = "favorite_books")
public class FavoriteBook {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String author;
    public String imageUrl;
    
    // API 상세 조회를 위해 고유 식별자(ISBN13)를 추가함
    public String isbn13;

    public FavoriteBook(String title, String author, String imageUrl, String isbn13) {
        this.title = title;
        this.author = author;
        this.imageUrl = imageUrl;
        this.isbn13 = isbn13;
    }
}