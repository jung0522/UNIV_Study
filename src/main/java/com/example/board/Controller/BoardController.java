package com.example.board.Controller;

import com.example.board.Dto.PostDto;
import com.example.board.Service.BoardService;
import com.example.board.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class BoardController {
    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto,
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails ) {
        return ResponseEntity.ok(boardService.createPost(postDto, customUserDetails.getUser()));

    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        return ResponseEntity.ok(boardService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getPostById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id,
            @RequestBody PostDto postDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        return ResponseEntity.ok(boardService.updatePost(id, postDto, customUserDetails.getUser()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        boardService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
