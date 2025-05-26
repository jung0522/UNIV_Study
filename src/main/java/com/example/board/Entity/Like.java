package com.example.board.Entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
// user_id와 post_id 조합이 유일해야 함 (중복 좋아요 방지)
@Table(name = "post_like", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}))
@EntityListeners(AuditingEntityListener.class)
public class Like {
    @Id
    @GeneratedValue
    private Long id;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
