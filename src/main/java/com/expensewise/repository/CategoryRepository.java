package com.expensewise.repository;

import com.expensewise.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    List<Category> findByUserId(String userId);

    List<Category> findByUserIdAndType(String userId, String type);

    Optional<Category> findByIdAndUserId(String id, String userId);

    boolean existsByUserIdAndNameAndType(String userId, String name, String type);
}
