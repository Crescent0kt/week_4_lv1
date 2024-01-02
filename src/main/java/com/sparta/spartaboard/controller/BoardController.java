package com.sparta.spartaboard.controller;

import com.sparta.spartaboard.dto.PostRequestDto;
import com.sparta.spartaboard.dto.PostResponseDto;
import com.sparta.spartaboard.entity.Post;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/board")
public class BoardController {


    private final Map<Long,Post> postList = new HashMap<>();

    @PostMapping("/posts")
    public PostResponseDto createPost(@RequestBody PostRequestDto requestDto){
        Post post = new Post(requestDto);

        Long maxId = !postList.isEmpty() ? Collections.max(postList.keySet())+1 : 1;
        post.setId(maxId);

        Date now = new Date();
        post.setDatetime(now);

        postList.put(post.getId(), post);
        PostResponseDto postResponseDto = new PostResponseDto(post);

        return postResponseDto;
    }

    @GetMapping("/posts")
    public List<PostResponseDto> getPosts(){
        List<PostResponseDto> responseList = postList.values().stream()
                .map(PostResponseDto::new).toList();

        return responseList;
    }

    @GetMapping("/posts/{id}")
    public PostResponseDto getPost(@PathVariable Long id){
        if(postList.containsKey(id)){
            Post post = postList.get(id);
            PostResponseDto responseDto = new PostResponseDto(post);
            return responseDto;
        }else{
            throw new IllegalArgumentException("선택하신 게시글은 존재하지 않습니다.");
        }
    }

}
