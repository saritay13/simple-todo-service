package com.example.todos.simpletodoservice.service;

import com.example.todos.simpletodoservice.domain.TodoItem;
import com.example.todos.simpletodoservice.domain.TodoStatus;
import com.example.todos.simpletodoservice.exception.PastDueModificationException;
import com.example.todos.simpletodoservice.repository.TodoItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TodoServiceTest {

    @Autowired
    private TodoService todoService;

    @Autowired
    private TodoItemRepository repository;

    @BeforeEach
    void cleanDB(){
        repository.deleteAll();
    }

    @Test
    void getById_shouldMarkItemAsPastDue_whenDueDateIsInPast(){
        //arrange
        Instant past = Instant.now().minusSeconds(60);
        TodoItem created = todoService.create("pay bills", past);

        //act
        TodoItem loaded = todoService.getById(created.getId());

        //assert
        assertEquals(TodoStatus.PAST_DUE, loaded.getStatus());
    }

    @Test
    void updateDescription_shouldThrow_whenItemIsPastDue() {
        // arrange
        Instant past = Instant.now().minusSeconds(60);
        TodoItem created = todoService.create("old task", past);

        //act and assert
        assertThrows(PastDueModificationException.class,
                ()-> todoService.updateDescription(created.getId(), "new Description"));
    }

    @Test
    void markDone_shouldSetDoneStatusAndDoneAt() {
        // arrange
        Instant future = Instant.now().plusSeconds(3600);
        TodoItem created = todoService.create("finish assignment", future);

        // act
        TodoItem done = todoService.markDone(created.getId());

        // assert
        assertEquals(TodoStatus.DONE, done.getStatus());
        assertNotNull(done.getDoneAt());
    }
}
