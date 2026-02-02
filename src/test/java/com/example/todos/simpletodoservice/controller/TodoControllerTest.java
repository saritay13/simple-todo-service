package com.example.todos.simpletodoservice.controller;

import com.example.todos.simpletodoservice.domain.TodoItem;
import com.example.todos.simpletodoservice.repository.TodoItemRepository;


import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.example.todos.simpletodoservice.constants.ErrorMessages.*;

import java.time.Instant;


@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerTest {

    public static final String $_ID = "$.id";
    public static final String $_DESCRIPTION = "$.description";
    public static final String $_STATUS = "$.status";
    public static final String $_CREATED_AT = "$.createdAt";
    public static final String $_DUE_AT = "$.dueAt";
    public static final String $_UPDATED_AT = "$.updatedAt";
    public static final String $_PATH = "$.path";
    public static final String $_MESSAGE = "$.message";
    public static final String $_DONE_AT = "$.doneAt";
    public static final String $ = "$";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoItemRepository repository;


    @BeforeEach
    void cleanDB(){
        repository.deleteAll();
    }


    @Test
    void create_shouldReturn200_andPersistTodo_whenValidRequest() throws Exception {
        String body = createRequestJson("Buy milk", Instant.now().plusSeconds(120).toString());

        mockMvc.perform(post("/todos").
                contentType(MediaType.APPLICATION_JSON).
                content(body)).andExpect(status().isOk()).
                andExpect(jsonPath($_ID).exists()).
                andExpect(jsonPath($_DESCRIPTION).value("Buy milk")).
                andExpect(jsonPath($_DUE_AT).exists()).
                andExpect(jsonPath($_CREATED_AT).exists()).
                andExpect(jsonPath($_STATUS).value("NOT_DONE"));
    }


    @Test
    void create_shouldReturn400_whenDueAtIsInPast() throws Exception {
        String body = createRequestJson("Old task", "2000-01-01T10:00:00Z");

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath($_STATUS).value(400))
                .andExpect(jsonPath($_PATH).value("/todos"));
    }

    @Test
    void getById_shouldReturn404_whenTodoDoesNotExist() throws Exception {
        String id = "00000000-0000-0000-0000-000000000000";
        mockMvc.perform(get("/todos/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath($_MESSAGE).value(TODO_ITEM_NOT_FOUND + id));
    }

    @Test
    void updateDescription_shouldReturn409_whenTodoIsPastDue() throws Exception {

        TodoItem item = repository.save(
                new TodoItem("past", Instant.now().minusSeconds(60))
        );

        String content = """
                    { "description" : "fail"}
                """;

        mockMvc.perform(put("/todos/" + item.getId() + "/description")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isConflict());

    }

    @Test
    void markDoneAndNotDone_shouldUpdateStatusAndDoneAtCorrectly() throws Exception {
        String id = createTodoAndReturnId("Do assignment", Instant.now().plusSeconds(120));

        // mark done
        mockMvc.perform(put("/todos/{id}/done", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_ID).value(id))
                .andExpect(jsonPath($_STATUS).value("DONE"))
                .andExpect(jsonPath($_DONE_AT).exists())
                .andExpect(jsonPath($_UPDATED_AT).exists());

        // mark not-done
        mockMvc.perform(put("/todos/{id}/not-done", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_ID).value(id))
                .andExpect(jsonPath($_STATUS).value("NOT_DONE"))
                .andExpect(jsonPath($_UPDATED_AT).exists());
    }

    @Test
    void getById_shouldReturnCreatedTodo() throws Exception {
        String id = createTodoAndReturnId("Buy milk", Instant.now().plusSeconds(120));

        mockMvc.perform(get("/todos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_ID).value(id))
                .andExpect(jsonPath($_DESCRIPTION).value("Buy milk"))
                .andExpect(jsonPath($_STATUS).value("NOT_DONE"))
                .andExpect(jsonPath($_CREATED_AT).exists())
                .andExpect(jsonPath($_DUE_AT).exists());
    }


    @Test
    void getAll_defaultShouldReturnOnlyNotDone_andIncludeDoneShouldReturnAll() throws Exception {
        String id1 = createTodoAndReturnId("Task A", Instant.now().plusSeconds(120));
        String id2 = createTodoAndReturnId("Task B", Instant.now().plusSeconds(180));

        // mark one as DONE
        mockMvc.perform(put("/todos/{id}/done", id1))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_STATUS).value("DONE"));

        // default list: includeDone=false -> should return only NOT_DONE items
        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath($, hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id2))
                .andExpect(jsonPath("$[0].status").value("NOT_DONE"));

        // include done: should return both items
        mockMvc.perform(get("/todos").param("includeDone", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath($, hasSize(2)));
    }


    // -------- helpers --------

    private String createTodoAndReturnId(String description, Instant dueAt) throws Exception {

        String body = createRequestJson(description, dueAt.toString());

        String responseJson = mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())   // change to isCreated() if you return 201 later
                .andExpect(jsonPath($_ID).exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(responseJson, $_ID);
    }

    String createRequestJson(String description, String dueAt){
        return """
                {
                  "description": "%s",
                  "dueAt": "%s"
                }
                """.formatted(description, dueAt);
    }

}
