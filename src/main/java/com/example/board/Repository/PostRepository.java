package com.example.board.Repository;

import com.example.board.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes l LEFT JOIN FETCH p.scraps s WHERE p.isDeleted = false")
    List<Post> findAllWithLikeAndScrapCount();
}
