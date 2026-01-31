package com.example.todos.simpletodoservice.controller;

import com.example.todos.simpletodoservice.domain.TodoItem;
import com.example.todos.simpletodoservice.dto.CreateTodoRequest;
import com.example.todos.simpletodoservice.dto.TodoResponse;
import com.example.todos.simpletodoservice.dto.UpdateDescriptionRequest;
import com.example.todos.simpletodoservice.mapper.TodoMapper;
import com.example.todos.simpletodoservice.service.TodoService;
import jakarta.validation.Valid;
import org.hibernate.sql.Update;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService){
        this.todoService = todoService;
    }

    //add an item
    @PostMapping
    public TodoResponse item(@Valid @RequestBody CreateTodoRequest request){
        TodoItem created = todoService.create(request.description(), request.dueAt());
        return TodoMapper.toResponse(created);
    }

    //change description of an item
    @PutMapping("/{id}/description")
    public TodoResponse updateDescription(@PathVariable UUID id, @RequestBody UpdateDescriptionRequest request){
        TodoItem updated = todoService.updateDescription(id, request.description());

        return TodoMapper.toResponse(updated);
    }

    // mark as done
    @PutMapping("/{id}/done")
    public TodoResponse markDone(@PathVariable UUID id){
        TodoItem updated = todoService.markDone(id);

        return TodoMapper.toResponse(updated);
    }

    // mark as not done
    @PutMapping("/{id}/not-done")
    public TodoResponse markNotDone(@PathVariable UUID id){
        TodoItem updated = todoService.markNotDone(id);

        return TodoMapper.toResponse(updated);
    }

    // get all items that are "not done" (with option to retrieve all items)
    @GetMapping
    public List<TodoResponse> list(@RequestParam(defaultValue = "false") boolean includeDone){
        return todoService.getItems(includeDone).stream().map(TodoMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public TodoResponse item(@PathVariable UUID id){
        TodoItem item =  todoService.getById(id);

        return TodoMapper.toResponse(item);
    }
}
