package com.aditya.expensetracker.dto;

import com.aditya.expensetracker.model.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record SummaryResponse(LocalDate from, LocalDate to, BigDecimal total, Map<Category, BigDecimal> byCategory) {
}
