package com.example.board.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
// toBuilder를 사용하려면 @Builder(toBuilder = true)로 설정해야 함
// 기존 객체를 기반으로 해서 변경하고 싶은 필드만 바꾼 새 객체를 생
@Builder(toBuilder = true)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String profileImage;

}
