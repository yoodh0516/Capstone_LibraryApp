package com.example.libraryapp.minseong.model

data class LibraryResponse(
    val response: LibResponseData
)

data class LibResponseData(
    val libs: List<LibraryWrapper>
)

data class LibraryWrapper(
    val lib: Library
)

data class Library(
    val libCode: String,
    val libName: String, // 도서관 이름
    val address: String, // 주소
    val tel: String,     // 전화번호
    val homepage: String, // 홈페이지
    val latitude: String,
    val longitude: String
)