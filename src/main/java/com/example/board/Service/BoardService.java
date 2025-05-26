package com.example.board.Service;

import com.example.board.Dto.PostDto;
import com.example.board.Entity.Post;
import com.example.board.Entity.User;
import com.example.board.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final PostRepository postRepository;

    public PostDto createPost(PostDto postDto, User user) {
        Post post = Post.builder()
                .title(postDto.title())
                .content(postDto.content())
                .user(user)
                .build();
        postRepository.save(post);
        return toDto(post);
    }


    public List<PostDto> getAllPosts() {
        return postRepository.findAllWithLikeAndScrapCount()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return toDto(post);
    }

    public PostDto updatePost(Long id, PostDto postDto, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Post updatedPost = post.toBuilder()
                .title(postDto.title())
                .content(postDto.content())
                .user(user)
                .build();
        return toDto(post);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setDeleted(true);
    }

    private PostDto toDto(Post post) {
        return new PostDto(
                post.getId(),
                post.getUser().getNickname(), // postDto 레코드의 Sting author
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.isDeleted(),
                post.getLikes() != null ? post.getLikes().size() : 0,
                post.getScraps() != null ? post.getScraps().size() : 0
        );
    }
}
