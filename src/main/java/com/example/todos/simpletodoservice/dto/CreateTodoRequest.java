package com.example.todos.simpletodoservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateTodoRequest(@NotBlank String description,
                                @NotNull @Future Instant dueAt) {

}
