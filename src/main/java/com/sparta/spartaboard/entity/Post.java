package com.sparta.spartaboard.entity;

import com.sparta.spartaboard.dto.PostRequestDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Post {
    private Long id;
    private String title;
    private String password;
    private String content;
    private Date datetime;

    public Post(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.password = requestDto.getPassword();
        this.content = requestDto.getContent();
    }
}
