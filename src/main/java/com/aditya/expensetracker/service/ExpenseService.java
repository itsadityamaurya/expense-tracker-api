package com.aditya.expensetracker.service;

import com.aditya.expensetracker.dto.*;
import com.aditya.expensetracker.exception.ExpenseNotFoundException;
import com.aditya.expensetracker.model.Category;
import com.aditya.expensetracker.model.Expense;
import com.aditya.expensetracker.repository.CategoryTotal;
import com.aditya.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository repository;

    @Transactional
    public ExpenseResponse create(ExpenseRequest request) {
        Expense expense = Expense.builder()
                .title(request.title().trim())
                .amount(request.amount())
                .category(request.category())
                .expenseDate(request.expenseDate())
                .note(request.note())
                .build();
        return ExpenseResponse.from(repository.save(expense));
    }

    @Transactional(readOnly = true)
    public ExpenseResponse get(Long id) {
        return ExpenseResponse.from(find(id));
    }

    @Transactional(readOnly = true)
    public PagedResponse<ExpenseResponse> search(Category category, LocalDate from, LocalDate to, Pageable pageable) {
        Specification<Expense> spec = (root, query, cb) -> cb.conjunction();
        if (category != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }
        if (from != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("expenseDate"), from));
        }
        if (to != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("expenseDate"), to));
        }
        return PagedResponse.from(repository.findAll(spec, pageable).map(ExpenseResponse::from));
    }

    @Transactional
    public ExpenseResponse update(Long id, ExpenseRequest request) {
        Expense expense = find(id);
        expense.setTitle(request.title().trim());
        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        expense.setExpenseDate(request.expenseDate());
        expense.setNote(request.note());
        return ExpenseResponse.from(repository.save(expense));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
    }

    @Transactional(readOnly = true)
    public SummaryResponse summary(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from must not be after to");
        }
        Map<Category, BigDecimal> byCategory = new EnumMap<>(Category.class);
        BigDecimal total = BigDecimal.ZERO;
        for (CategoryTotal row : repository.totalsByCategory(from, to)) {
            byCategory.put(row.getCategory(), row.getTotal());
            total = total.add(row.getTotal());
        }
        return new SummaryResponse(from, to, total, byCategory);
    }

    private Expense find(Long id) {
        return repository.findById(id).orElseThrow(() -> new ExpenseNotFoundException(id));
    }
}
