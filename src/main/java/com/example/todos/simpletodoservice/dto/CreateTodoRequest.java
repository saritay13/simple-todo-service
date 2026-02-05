package com.example.todos.simpletodoservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateTodoRequest(@NotBlank @Size(max = 500) String description,
                                @NotNull @Future Instant dueAt) {

}
