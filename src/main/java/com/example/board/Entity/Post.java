package com.example.board.Entity;


import com.example.board.Entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder(toBuilder = true) // setter 안 쓰도록, toBuilder 추가
@NoArgsConstructor
@AllArgsConstructor
// 엔티티의 생성 및 수정 시각을 자동으로 관리할 수 있게 해주는 어노테이션이다.
// 이 기능을 사용하면, 엔티티가 생성되거나 수정될 때 자동으로
// createdAt과 updatedAt 같은 필드에 시간 정보가 저장된다.
@EntityListeners(AuditingEntityListener.class)
public class Post {
    //    JPA에서 기본 키(primary key)를 자동 생성할 때 사용하는 설정이다.
//    이 설정은 데이터베이스가 자동 증가(auto-increment) 기능을 사용해서 값을 생성하도록 한다.
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String content;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostScrap> scraps = new ArrayList<>();



}
