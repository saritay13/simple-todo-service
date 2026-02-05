package com.example.todos.simpletodoservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateDescriptionRequest(@NotBlank @Size(max = 500) String description) {
}
