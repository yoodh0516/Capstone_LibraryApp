package com.example.libraryapp.community;

public class Comment {
    private String id;      // 댓글의 고유 ID
    private String postId;  // 이 댓글이 '어떤 게시글'에 달린 건지 구별하기 위함
    private String author;  // 작성자 (닉네임)
    private String content; // 댓글 내용
    private String date;    // 날짜

    public Comment() {} // 파이어베이스용 빈 생성자

    public Comment(String id, String postId, String author, String content, String date) {
        this.id = id;
        this.postId = postId;
        this.author = author;
        this.content = content;
        this.date = date;
    }

    public String getPostId() { return postId; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public String getDate() { return date; }
}