package com.expensewise.repository;

import com.expensewise.model.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {

    Page<Expense> findByUserId(String userId, Pageable pageable);

    Optional<Expense> findByIdAndUserId(String id, String userId);

    /** Filter by month and year */
    List<Expense> findByUserIdAndMonthAndYear(String userId, int month, int year);

    /** Filter by category */
    List<Expense> findByUserIdAndCategoryId(String userId, String categoryId);

    /** Filter by payment mode */
    List<Expense> findByUserIdAndPaymentMode(String userId, String paymentMode);

    /** Filter by category + month + year (used for budget recalculation) */
    List<Expense> findByUserIdAndCategoryIdAndMonthAndYear(
            String userId, String categoryId, int month, int year);

    /** Full-text search across title and tags using MongoDB regex */
    @Query("{ 'user_id': ?0, $or: [ { 'title': { $regex: ?1, $options: 'i' } }, { 'tags': { $regex: ?1, $options: 'i' } } ] }")
    List<Expense> searchByKeyword(String userId, String keyword);

    /** Yearly aggregation support — find all expenses for a given year */
    List<Expense> findByUserIdAndYear(String userId, int year);
}
