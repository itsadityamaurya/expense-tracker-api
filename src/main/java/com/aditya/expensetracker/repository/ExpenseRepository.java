package com.aditya.expensetracker.repository;

import com.aditya.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

    @Query("""
            select e.category as category, sum(e.amount) as total
            from Expense e
            where e.expenseDate >= :from and e.expenseDate <= :to
            group by e.category
            order by sum(e.amount) desc
            """)
    List<CategoryTotal> totalsByCategory(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
