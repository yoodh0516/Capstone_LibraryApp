package com.example.libraryapp.seokwon.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteLibraryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteLibrary library);

    @Delete
    void delete(FavoriteLibrary library);

    @Query("SELECT * FROM favorite_libraries")
    List<FavoriteLibrary> getAllFavoriteLibraries();

    @Query("SELECT * FROM favorite_libraries WHERE name = :name LIMIT 1")
    FavoriteLibrary getFavoriteLibraryByName(String name);

    @Query("DELETE FROM favorite_libraries WHERE name = :name")
    void deleteByName(String name);

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_libraries WHERE name = :name)")
    boolean isFavorite(String name);
}
