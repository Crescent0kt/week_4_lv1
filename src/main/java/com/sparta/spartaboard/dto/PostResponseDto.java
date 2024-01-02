package com.sparta.spartaboard.dto;

import com.sparta.spartaboard.entity.Post;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.Date;
@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String username;
    private String content;
    private Timestamp datetime;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.username = post.getUsername();
        this.content = post.getContent();
        this.datetime = post.getDatetime();
    }

    public PostResponseDto(Long id, String title, String username, String contents, Timestamp datetime) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.content = contents;
        this.datetime = datetime;
    }
}
