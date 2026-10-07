package com.aditya.expensetracker.service;

import com.aditya.expensetracker.dto.ExpenseRequest;
import com.aditya.expensetracker.dto.ExpenseResponse;
import com.aditya.expensetracker.dto.SummaryResponse;
import com.aditya.expensetracker.exception.ExpenseNotFoundException;
import com.aditya.expensetracker.model.Category;
import com.aditya.expensetracker.model.Expense;
import com.aditya.expensetracker.repository.CategoryTotal;
import com.aditya.expensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository repository;

    @InjectMocks
    private ExpenseService service;

    @Test
    void createTrimsTitleAndReturnsResponse() {
        when(repository.save(any(Expense.class))).thenAnswer(inv -> {
            Expense e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });
        ExpenseResponse r = service.create(new ExpenseRequest("  Lunch ", new BigDecimal("250.50"),
                Category.FOOD, LocalDate.now(), null));
        assertEquals(1L, r.id());
        assertEquals("Lunch", r.title());
    }

    @Test
    void getThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ExpenseNotFoundException.class, () -> service.get(99L));
    }

    @Test
    void summaryAddsUpCategoryTotals() {
        when(repository.totalsByCategory(any(), any())).thenReturn(List.of(
                total(Category.FOOD, "300.00"), total(Category.TRAVEL, "200.00")));
        SummaryResponse s = service.summary(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
        assertEquals(new BigDecimal("500.00"), s.total());
        assertEquals(2, s.byCategory().size());
    }

    @Test
    void summaryRejectsInvalidRange() {
        assertThrows(IllegalArgumentException.class,
                () -> service.summary(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1)));
    }

    private CategoryTotal total(Category c, String amount) {
        return new CategoryTotal() {
            public Category getCategory() { return c; }
            public BigDecimal getTotal() { return new BigDecimal(amount); }
        };
    }
}
