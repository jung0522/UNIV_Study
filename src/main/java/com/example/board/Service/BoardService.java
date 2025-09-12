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
        // builder는 타입.builder로
        Post post = Post.builder()
                .title(postDto.title())
                .content(postDto.content())
                .user(user)
                // build()로 마무리
                .build();
        postRepository.save(post);
        return toDto(post);
    }


    public List<PostDto> getAllPosts() {
        return postRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(post -> toDto(post))
                .collect(Collectors.toList());
    }

    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // 삭제된 게시글인지 확인
        if (post.isDeleted()) {
            throw new RuntimeException("삭제된 게시글입니다.");
        }
        
        return toDto(post);
    }

    public PostDto updatePost(Long id, PostDto postDto, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // 삭제된 게시글인지 확인
        if (post.isDeleted()) {
            throw new RuntimeException("삭제된 게시글은 수정할 수 없습니다.");
        }
        
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
        return new PostDto(
                post.getId(),
                post.getUser().getNickname(), // postDto 레코드의 Sting author
                post.getTitle(),
                post.getContent(),
                post.isDeleted(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
