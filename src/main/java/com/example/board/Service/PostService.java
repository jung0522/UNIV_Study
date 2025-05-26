/*
package com.example.board.Service;

import com.example.board.Dto.PostRequestDto;
import com.example.board.Dto.PostResponseDto;
import com.example.board.Entity.Post;
import com.example.board.Entity.User;
import com.example.board.Repository.PostRepository;
import com.example.board.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Post createPost(PostRequestDto postRequestDto, String email) {
        User writer = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Post post = Post.builder()
                .title(postRequestDto.title())
                .content(postRequestDto.content())
                .writer(writer)
                .build();
        return postRepository.save(post);


    }

    public List<PostResponseDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(post -> new PostResponseDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getWriter().getNickname(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()))
                .collect(Collectors.toList());
    }
    public PostResponseDto getPostById(Long id) {
        return postRepository.findById(id)
                .map(post -> new PostResponseDto(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getWriter().getNickname(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                        )).orElse(null);
    }
    public PostResponseDto updatePost(Long id, String email, PostRequestDto postRequestDto) {
        User writer = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return postRepository.findById(id)
                .map(post -> {
                    post.setTitle(postRequestDto.title());
                    post.setContent(postRequestDto.content());
                    Post updated = postRepository.save(post);
                    return new PostResponseDto(
                            updated.getId(),
                            updated.getTitle(),
                            updated.getContent(),
                            updated.getWriter().getNickname(),
                            updated.getCreatedAt(),
                            updated.getUpdatedAt());
                }).orElse(null);
    }
    public void deletePost(Long id, String email) {
        User writer = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        postRepository.deleteById(id);
    }
}
*/
