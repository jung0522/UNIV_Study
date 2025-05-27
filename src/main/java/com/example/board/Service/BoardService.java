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
        return postRepository.findAll()
                .stream()
                .map(post -> toDto(post))
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
        postRepository.save(updatedPost);
        return toDto(updatedPost);
    }

    public void deletePost(Long id, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Post deletedPost = post.toBuilder()
                .isDeleted(true)
                .user(user)
                .build();
        postRepository.save(deletedPost);  // 변경된 삭제 상태 저장
    }

    private PostDto toDto(Post post) {
        int likeCount = post.getLikes() == null ? 0 : post.getLikes().size();
        int scrapCount = post.getScraps() == null ? 0 : post.getScraps().size();
        return new PostDto(
                post.getId(),
                post.getUser().getNickname(), // postDto 레코드의 Sting author
                post.getTitle(),
                post.getContent(),
                post.isDeleted(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                likeCount,
                scrapCount
        );
    }
}
