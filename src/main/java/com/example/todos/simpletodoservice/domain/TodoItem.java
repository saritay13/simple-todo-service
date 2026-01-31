package com.example.todos.simpletodoservice.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "TodoItems")
public class TodoItem {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length= 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TodoStatus status;

    private Instant dueAt;

    private Instant doneAt;

    private Instant createAt;

    protected TodoItem(){
        //JPA only
    }

    public TodoItem(String description, Instant dueAt) {
        this.id = id;
        this.description = description;
        this.status = TodoStatus.NOT_DONE;
        this.dueAt = dueAt;
        this.createAt = Instant.now();
        this.doneAt = null;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TodoStatus status) {
        this.status = status;
    }

    public void setDueAt(Instant dueAt) {
        this.dueAt = dueAt;
    }

    public void setDoneAt(Instant doneAt) {
        this.doneAt = doneAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public TodoStatus getStatus() {
        return status;
    }

    public Instant getDueAt() {
        return dueAt;
    }

    public Instant getDoneAt() {
        return doneAt;
    }

    public Instant getCreateAt() {
        return createAt;
    }
}
