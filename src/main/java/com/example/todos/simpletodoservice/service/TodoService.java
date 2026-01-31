package com.example.todos.simpletodoservice.service;

import com.example.todos.simpletodoservice.domain.TodoItem;
import com.example.todos.simpletodoservice.domain.TodoStatus;
import com.example.todos.simpletodoservice.exception.NotFoundException;
import com.example.todos.simpletodoservice.exception.PastDueModificationException;
import com.example.todos.simpletodoservice.repository.TodoItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TodoService {

    private final TodoItemRepository repository;

    public TodoService(TodoItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TodoItem create(String description, Instant dueAt){
        if(description == null || description.isBlank()){
            throw new IllegalArgumentException("description must not be blank");
        }

        if(dueAt == null){
            throw new IllegalArgumentException("dueAt must not be null");
        }

        TodoItem item = new TodoItem(description, dueAt);
        return repository.save(item);
    }

    @Transactional
    public TodoItem updateDescription(UUID id, String newDescription){

        if(newDescription == null || newDescription.isBlank()){
            throw new IllegalArgumentException("description must not be blank");
        }

        TodoItem item = getRefreshedStatus(id);
        ensureNotPastDue(item);
        item.setDescription(newDescription);
        return repository.save(item);
    }

    @Transactional
    public TodoItem markDone(UUID id) {
        TodoItem item = getRefreshedStatus(id);
        ensureNotPastDue(item);
        item.setStatus(TodoStatus.DONE);
        item.setDoneAt(Instant.now());
        return repository.save(item);
    }

    @Transactional
    public TodoItem markNotDone(UUID id) {
        TodoItem item = getRefreshedStatus(id);
        ensureNotPastDue(item);
        item.setStatus(TodoStatus.NOT_DONE);
        item.setDoneAt(null);
        return repository.save(item);
    }

    @Transactional
    public TodoItem getById(UUID id) {
        TodoItem item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Todo item not found: " + id));

        // "Read-time" refresh: update computed status
        return refreshStatusIfNeeded(item);
    }

    @Transactional
    public List<TodoItem> getItems(boolean includeDone) {
        refreshPastDue();
        return includeDone
                ? repository.findAll()
                : repository.findAllByStatus(TodoStatus.NOT_DONE);
    }


    @Transactional
    private void refreshPastDue() {
        repository.markPastDue(
                TodoStatus.PAST_DUE,
                TodoStatus.DONE,
                Instant.now()
        );
    }

    private TodoItem getRefreshedStatus(UUID id){
        TodoItem item = repository.findById(id)
                .orElseThrow(()-> new NotFoundException("todo item not found: "+ id));

        return refreshStatusIfNeeded(item);
    }

    private TodoItem refreshStatusIfNeeded(TodoItem item){
        if(item.getStatus() != TodoStatus.DONE
        && item.getDueAt().isBefore(Instant.now())){
            item.setStatus(TodoStatus.PAST_DUE);
        }
        return item;
    }


    private void ensureNotPastDue(TodoItem item){
        if(item.getStatus() == TodoStatus.PAST_DUE)
            throw new PastDueModificationException("past due items cannot be modified");
    }
}
