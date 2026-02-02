package com.example.todos.simpletodoservice.constants;



public final class ErrorMessages {

    private ErrorMessages(){
        // prevent instantiation
    }

    public static final String TODO_ITEM_NOT_FOUND = "Todo item not found: ";
    public static final String DESCRIPTION_MUST_NOT_BE_BLANK = "description must not be blank";
    public static final String DUE_AT_MUST_NOT_BE_NULL = "dueAt must not be null";
    public static final String PAST_DUE_ITEMS_CANNOT_BE_MODIFIED = "past due items cannot be modified";
}
