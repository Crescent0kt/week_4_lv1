package com.sparta.spartaboard.repository;

import com.sparta.spartaboard.dto.PostRequestDto;
import com.sparta.spartaboard.dto.PostResponseDto;
import com.sparta.spartaboard.entity.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.List;

public class PostRepository {
    private final JdbcTemplate jdbcTemplate;

    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Post findById(Long id) {

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

    public Post save(Post post) {

        KeyHolder keyHolder = new GeneratedKeyHolder(); // 기본 키를 반환받기 위한 객체

        String sql = "INSERT INTO board (title,username, contents,password, datetime) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, post.getTitle());
                    preparedStatement.setString(2, post.getUsername());
                    preparedStatement.setString(3, post.getContent());
                    preparedStatement.setString(4, post.getPassword());
                    preparedStatement.setTimestamp(5, post.getDatetime());
                    return preparedStatement;
                },
                keyHolder);


        Long id = keyHolder.getKey().longValue();
        post.setId(id);
        return post;
    }

    public List<PostResponseDto> findAll() {
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

    public PostResponseDto findOneById(long id) {
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

    public PostResponseDto update(Long id,Post post,PostRequestDto requestDto) {
        String sql = "UPDATE board SET title = ?, username = ?, contents = ? WHERE id = ?";
        jdbcTemplate.update(sql, requestDto.getTitle(), requestDto.getUsername(), requestDto.getContent(), id);

        post.setId(id);
        post.setContent(requestDto.getContent());
        post.setUsername(requestDto.getUsername());
        post.setTitle(requestDto.getTitle());
        PostResponseDto postResponseDto = new PostResponseDto(post);
        return postResponseDto;
    }

    public void delete(Long id) {
        String sql = "DELETE FROM board WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}