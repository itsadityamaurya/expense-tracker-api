package com.aditya.expensetracker.controller;

import com.aditya.expensetracker.dto.*;
import com.aditya.expensetracker.model.Category;
import com.aditya.expensetracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses")
public class ExpenseController {

    private final ExpenseService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an expense")
    public ExpenseResponse create(@Valid @RequestBody ExpenseRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an expense by id")
    public ExpenseResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    @Operation(summary = "List expenses with filters and pagination")
    public PagedResponse<ExpenseResponse> list(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "expenseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        if (!java.util.Set.of("expenseDate", "amount", "title", "category").contains(sortBy)) {
            throw new IllegalArgumentException("Unsupported sort field: " + sortBy);
        }
        Sort sort = Sort.by("asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        return service.search(category, from, to, PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), sort));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an expense")
    public ExpenseResponse update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an expense")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/summary")
    @Operation(summary = "Total spend per category between two dates")
    public SummaryResponse summary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.summary(from, to);
    }
}
