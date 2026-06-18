package com.expensewise.repository;

import com.expensewise.model.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends MongoRepository<Budget, String> {

    List<Budget> findByUserId(String userId);

    List<Budget> findByUserIdAndMonthAndYear(String userId, int month, int year);

    Optional<Budget> findByIdAndUserId(String id, String userId);

    /** Used for budget recalculation when an expense is added */
    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(
            String userId, String categoryId, int month, int year);

    boolean existsByUserIdAndCategoryIdAndMonthAndYear(
            String userId, String categoryId, int month, int year);
}
