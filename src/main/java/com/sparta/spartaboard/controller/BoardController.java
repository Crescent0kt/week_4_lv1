package com.sparta.spartaboard.controller;

import com.sparta.spartaboard.dto.PostDeleteRequestDto;
import com.sparta.spartaboard.dto.PostRequestDto;
import com.sparta.spartaboard.dto.PostResponseDto;
import com.sparta.spartaboard.entity.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.*;
import java.util.*;
import java.util.Date;

@RestController
@RequestMapping("/board")
public class BoardController {

    private final JdbcTemplate jdbcTemplate;

    public BoardController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/posts")
    public List<PostResponseDto> getPosts() {
        String sql = "SELECT * FROM board ORDER BY id DESC";
        return jdbcTemplate.query(sql, new RowMapper<PostResponseDto>() {
            @Override
            public PostResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {

                Long id = rs.getLong("id");
                String title = rs.getString("title");
                String username = rs.getString("username");
                String contents = rs.getString("contents");
                Timestamp datetime = rs.getTimestamp("datetime");
                return new PostResponseDto(id, title,username, contents, datetime);
            }
        });
    }




    @GetMapping("/posts/{id}")
    public PostResponseDto getPost(@PathVariable Long id) {
        String sql = "SELECT * FROM board WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new RowMapper<PostResponseDto>() {
            @Override
            public PostResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long postId = rs.getLong("id");
                String title = rs.getString("title");
                String username = rs.getString("username");
                String contents = rs.getString("contents");
                Timestamp datetime = rs.getTimestamp("datetime");
                return new PostResponseDto(postId, title,username, contents, datetime);
            }
        }, id);
    }


    @PostMapping("/posts")
    public PostResponseDto createPost(@RequestBody PostRequestDto requestDto) {
        Post post = new Post(requestDto);
        Date now = new Date();
        Timestamp sqlTimestamp = new Timestamp(now.getTime());
        KeyHolder keyHolder = new GeneratedKeyHolder(); // 기본 키를 반환받기 위한 객체

        String sql = "INSERT INTO board (title,username, contents,password, datetime) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, post.getTitle());
                    preparedStatement.setString(2,post.getUsername());
                    preparedStatement.setString(3, post.getContent());
                    preparedStatement.setString(4, post.getPassword());

                    preparedStatement.setTimestamp(5, sqlTimestamp);
                    return preparedStatement;
                },
                keyHolder);


        Long id = keyHolder.getKey().longValue();
        post.setId(id);
        post.setDatetime(sqlTimestamp);
        PostResponseDto postResponseDto = new PostResponseDto(post);
        return postResponseDto;
    }


    @PutMapping("/posts/{id}")
    public PostResponseDto updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        Post post = findById(id);

        if (post != null) {
            if (Objects.equals(post.getPassword(), requestDto.getPassword())) {
                String sql = "UPDATE board SET title = ?, username = ?, contents = ? WHERE id = ?";
                jdbcTemplate.update(sql, requestDto.getTitle(), requestDto.getUsername(), requestDto.getContent(), id);
                post.setId(id);
                post.setContent(requestDto.getContent());
                post.setUsername(requestDto.getUsername());
                post.setTitle(requestDto.getTitle());
                PostResponseDto postResponseDto = new PostResponseDto(post);
                return postResponseDto;
            } else {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "비밀번호가 맞지 않습니다."
                );
            }
        } else {
            throw new IllegalArgumentException("없는 글.");
        }

    }

    @DeleteMapping("/posts/{id}")
    public Long deletePost(@PathVariable Long id, @RequestBody PostDeleteRequestDto requestDto) {
        Post post = findById(id);
        if (post != null) {
            if (Objects.equals(requestDto.getPassword(), post.getPassword())) {
                String sql = "DELETE FROM board WHERE id = ?";
                jdbcTemplate.update(sql, id);
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


    private Post findById(Long id) {

        String sql = "SELECT * FROM board WHERE id = ?";

        return jdbcTemplate.query(sql, resultSet -> {
            if (resultSet.next()) {
                Post post = new Post();
                post.setTitle(resultSet.getString("title"));
                post.setUsername(resultSet.getString("username"));
                post.setContent(resultSet.getString("contents"));
                post.setPassword(resultSet.getString("password"));
                post.setDatetime(resultSet.getTimestamp("datetime"));
                return post;
            } else {
                return null;
            }
        }, id);
    }
}
