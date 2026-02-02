package com.example.todos.simpletodoservice.mapper;

import com.example.todos.simpletodoservice.domain.TodoItem;
import com.example.todos.simpletodoservice.dto.TodoResponse;

public class TodoMapper {

    private TodoMapper(){}

    public static TodoResponse toResponse(TodoItem item){
        return new TodoResponse(item.getId(),
                item.getDescription(),
                item.getStatus(),
                item.getCreatedAt(),
                item.getUpdatedAt(),
                item.getDueAt(),
                item.getDoneAt());
    }
}
