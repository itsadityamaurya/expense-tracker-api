package com.aditya.expensetracker.repository;

import com.aditya.expensetracker.model.Category;

import java.math.BigDecimal;

public interface CategoryTotal {
    Category getCategory();

    BigDecimal getTotal();
}
