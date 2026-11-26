package com.example.libraryapp.seokwon.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * 파일 위치: app/src/main/java/com/example/mapandhelloworld/data/local/FavoriteBookDao.java
 * 설명: Room DB에 접근하여 즐겨찾기 데이터를 조작(삽입, 삭제, 조회)하는 인터페이스입니다.
 */
@Dao
public interface FavoriteBookDao {

    // 1. 즐겨찾기 추가 (이미 있는 책이면 덮어쓰기)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteBook favoriteBook);

    // 2. 즐겨찾기 삭제 (책 객체로 삭제)
    @Delete
    void delete(FavoriteBook favoriteBook);

    // 2-1. 즐겨찾기 삭제 (책 제목으로 찾아서 지우기) - 기존 유지
    @Query("DELETE FROM favorite_books WHERE title = :title")
    void deleteByTitle(String title);

    // 3. 즐겨찾기 전체 목록 조회
    @Query("SELECT * FROM favorite_books")
    List<FavoriteBook> getAllFavoriteBooks();

    // 4. 특정 책을 즐겨찾기에서 조회
    @Query("SELECT * FROM favorite_books WHERE title = :title LIMIT 1")
    FavoriteBook getFavoriteBookByTitle(String title);

    // 5. 이 책이 이미 즐겨찾기에 있는지 확인 (있으면 true, 없으면 false)
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_books WHERE title = :title)")
    boolean isFavorite(String title);
}