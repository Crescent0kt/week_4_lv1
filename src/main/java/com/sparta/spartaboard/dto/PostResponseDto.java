package com.sparta.spartaboard.dto;

import com.sparta.spartaboard.entity.Post;
import lombok.Getter;

import java.util.Date;
@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private Date datetime;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.datetime = post.getDatetime();
    }

}
