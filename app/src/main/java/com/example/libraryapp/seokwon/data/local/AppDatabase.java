package com.example.libraryapp.seokwon.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * AppDatabase: Room을 사용한 메인 데이터베이스 클래스입니다.
 * 도서 및 도서관 즐겨찾기 테이블을 통합하여 관리합니다.
 * 버전 1에서 2로 업그레이드하여 즐겨찾기 상세 정보(ISBN, 홈페이지 등)를 지원합니다.
 */
@Database(entities = {FavoriteBook.class, FavoriteLibrary.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FavoriteBookDao favoriteBookDao();
    public abstract FavoriteLibraryDao favoriteLibraryDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "map_hello_world_db")
                            // 기존 코드와의 호환성을 위해 메인 스레드 쿼리를 허용합니다.
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}