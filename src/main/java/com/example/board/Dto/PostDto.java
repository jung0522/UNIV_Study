package com.example.board.Dto;

import java.time.LocalDateTime;
import com.example.board.Entity.User;
public record PostDto(
        Long id,
        String writer,
        String title,
        String content,
        boolean isDeleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int likes,
        int scraps

) {
}
