package com.example.board.Repository;

import com.example.board.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PostRepository extends JpaRepository<Post, Long> {
    
    // 삭제되지 않은 게시글을 최신 순으로 조회
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Post> findAllByIsDeletedFalseOrderByCreatedAtDesc();
}
