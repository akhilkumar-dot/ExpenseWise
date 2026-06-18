package com.expensewise.repository;

import com.expensewise.model.Income;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepository extends MongoRepository<Income, String> {

    Page<Income> findByUserId(String userId, Pageable pageable);

    Optional<Income> findByIdAndUserId(String id, String userId);

    List<Income> findByUserIdAndMonthAndYear(String userId, int month, int year);

    List<Income> findByUserIdAndSource(String userId, String source);

    List<Income> findByUserIdAndYear(String userId, int year);
}
