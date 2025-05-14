//package com.example.board.Controller;
//
//import com.example.board.Entity.Post;
//import com.example.board.Dto.PostRequestDto;
//import com.example.board.Dto.PostResponseDto;
//import com.example.board.Service.kakaoOAuth2Service;
//import com.example.board.Service.PostService;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/posts")
//@RequiredArgsConstructor
//public class PostController {
//    private final PostService postService;
//    private final kakaoOAuth2Service kakaoOAuth2Service;
//
//    // 게시글 작성
//    @PostMapping
//    public ResponseEntity<PostResponseDto> createPost(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody PostRequestDto postRequestDto) {
//
//        if (!token.startsWith("Bearer ")) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        String accessToken = token.substring(7);
//        String userEmail = kakaoOAuth2Service.getKakaoUserInfo(accessToken).email();
//
//
//        Post post = postService.createPost(postRequestDto, userEmail);
//        return ResponseEntity.ok(new PostResponseDto(
//                post.getId(),
//                post.getTitle(),
//                post.getContent(),
//                post.getWriter().getNickname(),
//                post.getCreatedAt(),
//                post.getUpdatedAt()
//        ));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
//        return ResponseEntity.ok(postService.getAllPosts());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long id) {
//        PostResponseDto post = postService.getPostById(id);
//        return post != null ? ResponseEntity.ok(post) : ResponseEntity.notFound().build();
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody PostRequestDto postRequestDto) {
//        if (!token.startsWith("Bearer ")) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        String accessToken = token.substring(7);
//        String userEmail = kakaoOAuth2Service.getKakaoUserInfo(accessToken).email();
//
//        PostResponseDto updatedPost = postService.updatePost(id, userEmail, postRequestDto);
//        return updatedPost != null ? ResponseEntity.ok(updatedPost) : ResponseEntity.notFound().build();
//
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletePost(@PathVariable Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
//        if (!token.startsWith("Bearer ")) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        String accessToken = token.substring(7);
//        String userEmail = kakaoOAuth2Service.getKakaoUserInfo(accessToken).email();
//
//        postService.deletePost(id, userEmail);
//        return ResponseEntity.noContent().build();
//    }
//
//
//
//
//}
