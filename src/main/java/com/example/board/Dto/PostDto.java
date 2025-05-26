package com.example.board.Dto;

import java.time.LocalDateTime;
import com.example.board.Entity.User;
public record PostDto(
        Long id,
        String writer,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean isDeleted,
        int likeCount,
        int scrapCount
) {
}
