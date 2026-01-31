package com.example.todos.simpletodoservice.repository;

import com.example.todos.simpletodoservice.domain.TodoItem;
import com.example.todos.simpletodoservice.domain.TodoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TodoItemRepository extends JpaRepository<TodoItem, UUID> {
    public List<TodoItem> findAllByStatus(TodoStatus status);
}
