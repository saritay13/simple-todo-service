package com.example.todos.simpletodoservice.controller;

import com.example.todos.simpletodoservice.domain.TodoItem;
import com.example.todos.simpletodoservice.repository.TodoItemRepository;


import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.example.todos.simpletodoservice.constants.ErrorMessages.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerIntegrationTest {

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
    public static final String ROOT_URL = "/api/v1/todos";
    public static final String NOT_DONE = "NOT_DONE";
    public static final String DESCRIPTION_BUY_MILK = "Buy milk";
    public static final String DESCRIPTION_BUY_GROCERY = "Buy Grocery";
    public static final String DONE = "DONE";
    public static final int STATUS_400 = 400;
    public static final String BLANK_DESCRIPTION = "";
    public static final String NULL_DESCRIPTION = null;
    public static final String PAST_DUE = "PAST_DUE";
    public static final String $_ANY_STATUS = "$[*].status";
    public static final String DESCRIPTION = "description";
    public static final String DUE_AT = "dueAt";
    public static final String MUST_BE_BETWEEN_0_AND_500 = "description: size must be between 0 and 500";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoItemRepository repository;

    @Autowired
    ObjectMapper objectMapper;


    @BeforeEach
    void cleanDB(){
        repository.deleteAll();
    }


    @Test
    void create_shouldPersistValidRequest() throws Exception {
        String body = createRequestJson(DESCRIPTION_BUY_MILK, Instant.now().plusSeconds(120).toString());

        mockMvc.perform(post(ROOT_URL).
                contentType(MediaType.APPLICATION_JSON).
                content(body)).andExpect(status().isCreated()).
                andExpect(jsonPath($_ID).exists()).
                andExpect(jsonPath($_DESCRIPTION).value(DESCRIPTION_BUY_MILK)).
                andExpect(jsonPath($_DUE_AT).isNotEmpty()).
                andExpect(jsonPath($_CREATED_AT).isNotEmpty()).
                andExpect(jsonPath($_STATUS).value(NOT_DONE));
    }

    @Test
    void create_shoudRejectNullDescription() throws Exception {
        String body = createRequestJson(null, Instant.now().plusSeconds(120).toString());

        mockMvc.perform(post(ROOT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath($_STATUS).value(STATUS_400));
    }

    @Test
    void create_shouldRejectBlankDescription() throws Exception {
        String body = createRequestJson(BLANK_DESCRIPTION, Instant.now().plusSeconds(120).toString());

        mockMvc.perform(post(ROOT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath($_STATUS).value(STATUS_400));
    }

    @Test
    void create_shouldRejectNullDueAt() throws Exception {
        String body = createRequestJson(DESCRIPTION_BUY_MILK, null);
        mockMvc.perform(post(ROOT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath($_STATUS).value(STATUS_400));
    }

    @Test
    void create_shouldRejectInvalidDueAt() throws Exception {
        String body = createRequestJson(DESCRIPTION_BUY_MILK, "not-a-date");
        mockMvc.perform(post(ROOT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath($_STATUS).value(STATUS_400));
    }

    //Check
    @Test
    void create_shouldRejectPastDueAt() throws Exception {
        String body = createRequestJson(DESCRIPTION_BUY_MILK, Instant.now().minusSeconds(120).toString());
        mockMvc.perform(post(ROOT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath($_STATUS).value(STATUS_400));
    }

    @Test
    void create_shoudRejectLargeDescription() throws Exception {
        char[] chars = new char[600];
        Arrays.fill(chars, 'a');
        String result = new String(chars);
        String body = createRequestJson(result, Instant.now().plusSeconds(120).toString());

        mockMvc.perform(post(ROOT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath($_STATUS).value(STATUS_400))
                .andExpect(jsonPath($_MESSAGE)
                        .value(MUST_BE_BETWEEN_0_AND_500));;
    }

    @ParameterizedTest
    @CsvSource({
            "2027-02-04T10:00:00+05:30, 2027-02-04T04:30:00Z",
            "2027-02-04T01:00:00-04:00, 2027-02-04T05:00:00Z"
    })
    void create_shouldNormalizeDueAtToUtc(String input, String expectedUtc)
            throws Exception {

        String body = createRequestJson(DESCRIPTION_BUY_MILK, input);

        mockMvc.perform(post(ROOT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        TodoItem savedTodo = repository.findAll().get(0);

        assertEquals(Instant.parse(expectedUtc), savedTodo.getDueAt());
    }

    @Test
    void updateDescription_shouldPersistValidRequest() throws Exception {

        /* Test-only setup: creates a past-due item via the repository to simulate overdue state */
        TodoItem item = repository.save(
                new TodoItem(DESCRIPTION_BUY_MILK, Instant.now().plusSeconds(60))
        );

        String content = updateDescriptionRequestJson(DESCRIPTION_BUY_GROCERY);

        mockMvc.perform(put(ROOT_URL+"/" + item.getId() + "/description")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk()).
                andExpect(jsonPath($_DESCRIPTION).value(DESCRIPTION_BUY_GROCERY)).
                andExpect(jsonPath($_UPDATED_AT).isNotEmpty());

    }

    @Test
    void updateDescription_shouldRejectWhenTodoIsPastDue() throws Exception {

        /* Test-only setup: creates a past-due item via the repository to simulate overdue state */
        TodoItem item = repository.save(
                new TodoItem(DESCRIPTION_BUY_MILK, Instant.now().minusSeconds(60))
        );

        String content = updateDescriptionRequestJson(DESCRIPTION_BUY_GROCERY);

        mockMvc.perform(put(ROOT_URL+"/" + item.getId() + "/description")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isConflict())
                .andExpect(jsonPath($_MESSAGE).value(PAST_DUE_ITEMS_CANNOT_BE_MODIFIED));

    }

    @Test
    void updateDescription_shouldRejectNullDescription() throws Exception {
        String id = createTodoAndReturnId(DESCRIPTION_BUY_MILK, Instant.now().plusSeconds(60));

        String content = updateDescriptionRequestJson(NULL_DESCRIPTION);

        mockMvc.perform(put(ROOT_URL + "/{id}/description", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDescription_shouldRejectBlankDescription() throws Exception {
        String id = createTodoAndReturnId(DESCRIPTION_BUY_MILK, Instant.now().plusSeconds(60));

        String content = updateDescriptionRequestJson(BLANK_DESCRIPTION);

        mockMvc.perform(put(ROOT_URL + "/{id}/description", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDescription_shouldRejectWhenItemMissing() throws Exception {
        UUID id = UUID.randomUUID();
        String content = updateDescriptionRequestJson(DESCRIPTION_BUY_MILK);
        mockMvc.perform(put(ROOT_URL + "/{id}/description", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_shoudRejectLargeDescription() throws Exception {
        char[] chars = new char[600];
        Arrays.fill(chars, 'a');
        String result = new String(chars);

        String id = createTodoAndReturnId(DESCRIPTION_BUY_MILK, Instant.now().plusSeconds(60));

        String content = updateDescriptionRequestJson(result);

        mockMvc.perform(put(ROOT_URL + "/{id}/description", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath($_STATUS).value(STATUS_400))
                .andExpect(jsonPath($_MESSAGE)
                        .value(MUST_BE_BETWEEN_0_AND_500));;
    }


    @Test
    void markDoneNotDone_shouldUpdateStatusAndDoneAt() throws Exception {
        String id = createTodoAndReturnId(DESCRIPTION_BUY_GROCERY, Instant.now().plusSeconds(120));


        /* Marking Done */
        mockMvc.perform(put(ROOT_URL+"/{id}/done", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_ID).value(id))
                .andExpect(jsonPath($_STATUS).value(DONE))
                .andExpect(jsonPath($_DONE_AT).exists())
                .andExpect(jsonPath($_UPDATED_AT).isNotEmpty());

        /* Marking Not Done */
        mockMvc.perform(put(ROOT_URL+"/{id}/not-done", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_ID).value(id))
                .andExpect(jsonPath($_STATUS).value(NOT_DONE))
                .andExpect(jsonPath($_DONE_AT).isEmpty())
                .andExpect(jsonPath($_UPDATED_AT).isNotEmpty());
    }


    @Test
    void markDone_rejectsWhenPastDue() throws Exception {
        /* Test-only setup: creates a past-due item via the repository to simulate overdue state */
        TodoItem item = repository.save(
                new TodoItem(DESCRIPTION_BUY_MILK, Instant.now().minusSeconds(60))
        );

        mockMvc.perform(put(ROOT_URL + "/{id}/done", item.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath($_MESSAGE).value(PAST_DUE_ITEMS_CANNOT_BE_MODIFIED));;
    }


    @Test
    void markNotDone_rejectsWhenPastDue() throws Exception {
        /* Test-only setup: creates a past-due item via the repository to simulate overdue state */
        TodoItem item = repository.save(
                new TodoItem(DESCRIPTION_BUY_MILK, Instant.now().minusSeconds(60))
        );

        mockMvc.perform(put(ROOT_URL + "/{id}/not-done", item.getId()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath($_MESSAGE).value(PAST_DUE_ITEMS_CANNOT_BE_MODIFIED));;
    }


    @Test
    void getById_shouldReturnCreatedTodo() throws Exception {
        String id = createTodoAndReturnId(DESCRIPTION_BUY_MILK, Instant.now().plusSeconds(120));

        mockMvc.perform(get(ROOT_URL+"/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_ID).value(id))
                .andExpect(jsonPath($_DESCRIPTION).value(DESCRIPTION_BUY_MILK))
                .andExpect(jsonPath($_STATUS).value(NOT_DONE))
                .andExpect(jsonPath($_CREATED_AT).exists())
                .andExpect(jsonPath($_DUE_AT).exists());
    }

    @Test
    void getById_shouldReturn404_whenTodoMissing() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get(ROOT_URL + "/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath($_MESSAGE)
                        .value(TODO_ITEM_NOT_FOUND + id));
    }

    @Test
    void getById_refreshesPastDueStatus() throws Exception {
        /* Test-only setup: creates a past-due item via the repository to simulate overdue state */
        TodoItem item = repository.save(
                new TodoItem(DESCRIPTION_BUY_MILK, Instant.now().minusSeconds(60))
        );
        mockMvc.perform(get(ROOT_URL + "/{id}", item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_STATUS).value(PAST_DUE));
    }


    @Test
    void getAll_includeDoneShouldReturnAllItems() throws Exception {
        createTwoItemsAndMarkOneAsDone();

        // include done: should return both items
        mockMvc.perform(get(ROOT_URL).param("includeDone", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath($, hasSize(2)));
    }

    @Test
    void getAll_defaultShouldReturnOnlyNotDoneItems() throws Exception {
        String id2 = createTwoItemsAndMarkOneAsDone();

        mockMvc.perform(get(ROOT_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath($, hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id2))
                .andExpect(jsonPath("$[0].status").value(NOT_DONE));

    }

    @Test
    void getAll_refreshesPastDueStatus() throws Exception {
        /* Test-only setup: creates a past-due item via the repository to simulate overdue state */
        TodoItem past_due_item = repository.save(
                new TodoItem(DESCRIPTION_BUY_MILK, Instant.now().minusSeconds(60))
        );

        TodoItem not_done_item = repository.save(
                new TodoItem(DESCRIPTION_BUY_GROCERY, Instant.now().plusSeconds(60))
        );

        mockMvc.perform(get(ROOT_URL).param("includeDone", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_ANY_STATUS).value(hasItem(PAST_DUE)))
                .andExpect(jsonPath($_ANY_STATUS).value(hasItem(NOT_DONE)));
    }

    // -------- helpers --------

    private String createTodoAndReturnId(String description, Instant dueAt) throws Exception {

        String body = createRequestJson(description, dueAt.toString());

        String responseJson = mockMvc.perform(post(ROOT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(responseJson, $_ID);
    }

    String createRequestJson(String description, String dueAt) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put(DESCRIPTION, description);
        body.put(DUE_AT, dueAt);
        return objectMapper.writeValueAsString(body);
    }

    String updateDescriptionRequestJson(String description) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put(DESCRIPTION, description);
        return objectMapper.writeValueAsString(body);
    }

    private String createTwoItemsAndMarkOneAsDone() throws Exception {
        String id1 = createTodoAndReturnId(DESCRIPTION_BUY_MILK, Instant.now().plusSeconds(120));
        String id2 = createTodoAndReturnId(DESCRIPTION_BUY_GROCERY, Instant.now().plusSeconds(180));

        // mark one as DONE
        mockMvc.perform(put(ROOT_URL+"/{id}/done", id1))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_STATUS).value(DONE));

        return id2;
    }
}
