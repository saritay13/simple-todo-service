package com.example.todos.simpletodoservice.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateDescriptionRequest(@NotBlank String description) {
}
