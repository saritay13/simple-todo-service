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

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(updatable = true)
    private Instant updatedAt;

    protected TodoItem(){
        //JPA only
    }

    public TodoItem(String description, Instant dueAt) {
        this.description = description;
        this.status = TodoStatus.NOT_DONE;
        this.dueAt = dueAt;
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

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
