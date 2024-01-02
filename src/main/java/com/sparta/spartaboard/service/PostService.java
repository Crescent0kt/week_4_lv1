package com.sparta.spartaboard.service;

import com.sparta.spartaboard.dto.PostDeleteRequestDto;
import com.sparta.spartaboard.dto.PostRequestDto;
import com.sparta.spartaboard.dto.PostResponseDto;
import com.sparta.spartaboard.entity.Post;
import com.sparta.spartaboard.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.server.ResponseStatusException;

import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PostService {

    private final JdbcTemplate jdbcTemplate;

    public PostService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public PostResponseDto createPost(PostRequestDto requestDto) {
        Post post = new Post(requestDto);
        Date now = new Date();
        Timestamp sqlTimestamp = new Timestamp(now.getTime());
        PostRepository postRepository = new PostRepository(jdbcTemplate);
        post.setDatetime(sqlTimestamp);

        Post savePost = postRepository.save(post);


        PostResponseDto postResponseDto = new PostResponseDto(savePost);
        return postResponseDto;
    }



    public List<PostResponseDto> getPosts() {
        PostRepository postRepository = new PostRepository(jdbcTemplate);
        return postRepository.findAll();


    }

    public PostResponseDto getPost(long id) {
        PostRepository postRepository = new PostRepository(jdbcTemplate);
        return postRepository.findOneById(id);

    }



    public PostResponseDto updatePost(Long id, PostRequestDto requestDto) {

        PostRepository postRepository = new PostRepository(jdbcTemplate);
        Post post = postRepository.findById(id);

        if (post != null) {
            if (Objects.equals(post.getPassword(), requestDto.getPassword())) {
                return postRepository.update(id, post, requestDto);

            } else {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "비밀번호가 맞지 않습니다."
                );
            }
        } else {
            throw new IllegalArgumentException("없는 글.");
        }

    }

    public Long deletePost(Long id, PostDeleteRequestDto requestDto) {
        PostRepository postRepository = new PostRepository(jdbcTemplate);
        Post post = postRepository.findById(id);
        if (post != null) {
            if (Objects.equals(requestDto.getPassword(), post.getPassword())) {
               postRepository.delete(id);
               return id;
            } else {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "비밀번호가 맞지 않습니다."
                );
            }
        } else {
            throw new IllegalArgumentException("선택하신 게시글은 존재하지 않습니다.");
        }
    }
}
