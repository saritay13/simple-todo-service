package com.example.todos.simpletodoservice.exception;

public class PastDueModificationException extends RuntimeException {

    public PastDueModificationException(String message) {
        super(message);
    }

}
