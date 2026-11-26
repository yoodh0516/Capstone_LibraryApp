package com.example.libraryapp.home;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LibraryService {
    @GET("libSrch")
    Call<LibraryNaruResponse> getLibraries(
//            @Query("authKey") String authKey,
            @Query("libName") String libName,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize,
            @Query("format") String format
    );
}