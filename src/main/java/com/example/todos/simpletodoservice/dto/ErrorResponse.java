package com.example.todos.simpletodoservice.dto;

import java.time.Instant;

public record ErrorResponse(Instant timesstamp,
                            int status,
                            String message,
                            String path) {
}
