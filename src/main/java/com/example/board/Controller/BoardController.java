package com.example.board.Controller;

import com.example.board.Dto.PostDto;
import com.example.board.Service.BoardService;
import com.example.board.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
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
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        try {
            PostDto post = boardService.getPostById(id);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("삭제된 게시글")) {
                return ResponseEntity.status(HttpStatus.GONE).body(Map.of(
                    "success", false,
                    "message", "삭제된 게시글입니다.",
                    "error", "GONE"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "게시글을 찾을 수 없습니다.",
                    "error", "NOT_FOUND"
                ));
            }
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestBody PostDto postDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        try {
            PostDto updatedPost = boardService.updatePost(id, postDto, customUserDetails.getUser());
            return ResponseEntity.ok(updatedPost);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("삭제된 게시글")) {
                return ResponseEntity.status(HttpStatus.GONE).body(Map.of(
                    "success", false,
                    "message", "삭제된 게시글은 수정할 수 없습니다.",
                    "error", "GONE"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "게시글을 찾을 수 없습니다.",
                    "error", "NOT_FOUND"
                ));
            }
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id,
                                           @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        boardService.deletePost(id, customUserDetails.getUser());
        return ResponseEntity.noContent().build();
    }
}
