package com.example.todos.simpletodoservice.dto;

import com.example.todos.simpletodoservice.domain.TodoStatus;

import java.time.Instant;
import java.util.UUID;

public record TodoResponse(UUID id,
                           String description,
                           TodoStatus status,
                           Instant createdAt,
                           Instant updatedAt,
                           Instant dueAt,
                           Instant doneAt) {
}
