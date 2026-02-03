package com.example.todos.simpletodoservice.service;

import com.example.todos.simpletodoservice.domain.TodoItem;
import com.example.todos.simpletodoservice.domain.TodoStatus;
import com.example.todos.simpletodoservice.exception.NotFoundException;
import com.example.todos.simpletodoservice.exception.PastDueModificationException;
import com.example.todos.simpletodoservice.repository.TodoItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.example.todos.simpletodoservice.constants.ErrorMessages.*;

@Service
public class TodoService {

    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);
    private final TodoItemRepository repository;

    public TodoService(TodoItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TodoItem create(String description, Instant dueAt){
        if(description == null || description.isBlank()){
            throw new IllegalArgumentException(DESCRIPTION_MUST_NOT_BE_BLANK);
        }

        if(dueAt == null){
            throw new IllegalArgumentException(DUE_AT_MUST_NOT_BE_NULL);
        }

        logger.info("Creating todo item with dueAt={}", dueAt);
        TodoItem item = new TodoItem(description, dueAt);
        return repository.save(item);
    }

    @Transactional
    public TodoItem updateDescription(UUID id, String newDescription){

        if(newDescription == null || newDescription.isBlank()){
            throw new IllegalArgumentException(DESCRIPTION_MUST_NOT_BE_BLANK);
        }
        logger.info("Updating description for todo item {}", id);
        TodoItem item = getRefreshedStatus(id);
        ensureNotPastDue(item);
        item.setDescription(newDescription);
        return repository.save(item);
    }

    @Transactional
    public TodoItem markDone(UUID id) {
        logger.info("Marking todo item {} as done", id);
        TodoItem item = getRefreshedStatus(id);
        ensureNotPastDue(item);
        item.setStatus(TodoStatus.DONE);
        item.setDoneAt(Instant.now());
        return repository.save(item);
    }

    @Transactional
    public TodoItem markNotDone(UUID id) {
        logger.info("Marking todo item {} as not done", id);
        TodoItem item = getRefreshedStatus(id);
        ensureNotPastDue(item);
        item.setStatus(TodoStatus.NOT_DONE);
        item.setDoneAt(null);
        return repository.save(item);
    }

    @Transactional
    public TodoItem getById(UUID id) {
        logger.info("Fetching todo item {}", id);
        TodoItem item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(TODO_ITEM_NOT_FOUND + id));

        // "Read-time" refresh: update computed status
        return refreshStatusIfNeeded(item);
    }

    @Transactional
    public List<TodoItem> getItems(boolean includeDone) {
        logger.info("Listing todo items includeDone={}", includeDone);
        refreshPastDue();
        return includeDone
                ? repository.findAll()
                : repository.findAllByStatus(TodoStatus.NOT_DONE);
    }


    @Transactional
    private void refreshPastDue() {
        logger.info("updating todo items as PAST_DUE which are past the past due date");
        repository.markPastDue(
                TodoStatus.PAST_DUE,
                TodoStatus.DONE,
                Instant.now()
        );
    }

    private TodoItem getRefreshedStatus(UUID id){
        TodoItem item = repository.findById(id)
                .orElseThrow(()-> new NotFoundException(TODO_ITEM_NOT_FOUND + id));

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
            throw new PastDueModificationException(PAST_DUE_ITEMS_CANNOT_BE_MODIFIED);
    }
}
