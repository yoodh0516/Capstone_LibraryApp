package com.example.libraryapp.community;

import java.io.Serializable;

public class Post implements Serializable{
    private String id;      // 게시글 고유
    private String title;   // 제목
    private String author;  // 작성자
    private String date;    // 날짜
    private String content; // 본문 내용
    private String category;
    private long timestamp;
    private String uid;     // 🔥 [유지] 유저 고유 ID

    // 기본 생성자 (파이어베이스 사용 시 필수)
    public Post() {}

    // 데이터를 넣을 때 쓰는 생성자
    public Post(String id, String title, String author, String date, String content, String category, long timestamp, String uid) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.date = date;
        this.content = content;
        this.category = category;
        this.timestamp = timestamp;
        this.uid = uid; // 할당
    }

    // 데이터를 꺼내갈 수 있게 하는 Getter 들
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDate() { return date; }
    public String getContent() { return content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
}