package com.example.board.Dto;

import java.time.LocalDateTime;
import com.example.board.Entity.User;

public record PostResponseDto(
        Long id,
        String title,
        String content,
        String writer, // username
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
