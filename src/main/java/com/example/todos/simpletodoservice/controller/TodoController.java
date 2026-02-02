package com.example.todos.simpletodoservice.controller;

import com.example.todos.simpletodoservice.domain.TodoItem;
import com.example.todos.simpletodoservice.dto.CreateTodoRequest;
import com.example.todos.simpletodoservice.dto.TodoResponse;
import com.example.todos.simpletodoservice.dto.UpdateDescriptionRequest;
import com.example.todos.simpletodoservice.mapper.TodoMapper;
import com.example.todos.simpletodoservice.service.TodoService;import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/todos")
@Tag(name = "Todos", description = "Manage to-do items")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService){
        this.todoService = todoService;
    }


    @PostMapping
    @Operation(summary = "Create a todo item")
    public TodoResponse item(@Valid @RequestBody CreateTodoRequest request){
        TodoItem created = todoService.create(request.description(), request.dueAt());
        return TodoMapper.toResponse(created);
    }


    @PutMapping("/{id}/description")
    @Operation(summary = "Update todo description")
    public TodoResponse updateDescription(@PathVariable UUID id, @Valid @RequestBody UpdateDescriptionRequest request){
        TodoItem updated = todoService.updateDescription(id, request.description());

        return TodoMapper.toResponse(updated);
    }


    @PutMapping("/{id}/done")
    @Operation(summary = "Mark todo as done")
    public TodoResponse markDone(@PathVariable UUID id){
        TodoItem updated = todoService.markDone(id);

        return TodoMapper.toResponse(updated);
    }


    @PutMapping("/{id}/not-done")
    @Operation(summary = "Mark todo as not done")
    public TodoResponse markNotDone(@PathVariable UUID id){
        TodoItem updated = todoService.markNotDone(id);

        return TodoMapper.toResponse(updated);
    }

    /* ) */
    @GetMapping
    @Operation(summary = "get all items that are (not done) - with option to retrieve all items")
    public List<TodoResponse> list(@RequestParam(defaultValue = "false") boolean includeDone){
        return todoService.getItems(includeDone).stream().map(TodoMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a todo item by id")
    public TodoResponse item(@PathVariable UUID id){
        TodoItem item =  todoService.getById(id);

        return TodoMapper.toResponse(item);
    }
}
