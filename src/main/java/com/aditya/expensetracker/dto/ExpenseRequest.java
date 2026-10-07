package com.aditya.expensetracker.dto;

import com.aditya.expensetracker.model.Category;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotBlank(message = "title is required") @Size(max = 120) String title,
        @NotNull(message = "amount is required") @DecimalMin(value = "0.01", message = "amount must be greater than 0") BigDecimal amount,
        @NotNull(message = "category is required") Category category,
        @NotNull(message = "expenseDate is required") @PastOrPresent(message = "expenseDate cannot be in the future") LocalDate expenseDate,
        @Size(max = 255) String note) {
}
