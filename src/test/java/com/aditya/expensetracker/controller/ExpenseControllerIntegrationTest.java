package com.aditya.expensetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExpenseControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private long createExpense(String title, String amount, String category) throws Exception {
        String body = "{\"title\":\"" + title + "\",\"amount\":" + amount + ",\"category\":\"" + category
                + "\",\"expenseDate\":\"2026-01-15\"}";
        String res = mvc.perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(res).get("id").asLong();
    }

    @Test
    void crudFlow() throws Exception {
        long id = createExpense("Groceries", "1200.50", "FOOD");
        mvc.perform(get("/api/expenses/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Groceries"));
        mvc.perform(put("/api/expenses/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Groceries v2\",\"amount\":999.00,\"category\":\"FOOD\",\"expenseDate\":\"2026-01-16\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Groceries v2"));
        mvc.perform(delete("/api/expenses/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/expenses/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void validationErrorsReturn400() throws Exception {
        mvc.perform(post("/api/expenses").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"amount\":-5,\"category\":\"FOOD\",\"expenseDate\":\"2026-01-15\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists())
                .andExpect(jsonPath("$.errors.amount").exists());
    }

    @Test
    void filterByCategoryAndSummary() throws Exception {
        createExpense("Train", "100.00", "TRAVEL");
        mvc.perform(get("/api/expenses").param("category", "TRAVEL").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].category").value("TRAVEL"));
        mvc.perform(get("/api/expenses/summary").param("from", "2026-01-01").param("to", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.byCategory.TRAVEL").exists());
    }

    @Test
    void invalidSortFieldReturns400() throws Exception {
        mvc.perform(get("/api/expenses").param("sortBy", "password")).andExpect(status().isBadRequest());
    }
}
