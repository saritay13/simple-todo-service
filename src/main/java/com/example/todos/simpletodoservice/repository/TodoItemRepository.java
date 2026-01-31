package com.example.todos.simpletodoservice.repository;

import com.example.todos.simpletodoservice.domain.TodoItem;
import com.example.todos.simpletodoservice.domain.TodoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TodoItemRepository extends JpaRepository<TodoItem, UUID> {
    public List<TodoItem> findAllByStatus(TodoStatus status);

    @Modifying
    @Query("""
        update TodoItem t
            set t.status = :pastDue
        where t.status <> :done
          and t.status <> :pastDue
          and t.dueAt < :now
    """)
    int markPastDue(
            @Param("pastDue") TodoStatus pastDue,
            @Param("done") TodoStatus done,
            @Param("now") Instant now
    );


}
