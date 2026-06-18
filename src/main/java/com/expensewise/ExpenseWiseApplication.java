package com.expensewise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ExpenseWise - Personal Expense Management System
 * Entry point for the Spring Boot application.
 */
@SpringBootApplication
public class ExpenseWiseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseWiseApplication.class, args);
        System.out.println("✅ ExpenseWise API is running on http://localhost:8080");
    }
}
