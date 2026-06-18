<div align="center">

<h1>💸 ExpenseWise</h1>

<p><strong>A production-quality Personal Expense Management REST API</strong><br/>
Built with Java 17 · Spring Boot 3 · MongoDB · JWT Authentication</p>

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-47A248?style=for-the-badge&logo=mongodb&logoColor=white)](https://www.mongodb.com/)
[![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![JWT](https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

</div>

---

## 📖 Overview

**ExpenseWise** is a fully-featured RESTful backend for a personal finance management system. It allows users to track income and expenses, set category-wise budgets, and generate insightful financial reports — all with secure JWT-based authentication and strict per-user data isolation.

This project demonstrates real-world backend engineering practices:
- Clean layered architecture (Controller → Service → Repository)
- JWT stateless security with Spring Security 6
- MongoDB document modeling with Spring Data
- Business logic: budget tracking, threshold alerts, and analytics
- Standard `ApiResponse<T>` wrapper for all endpoints
- Global exception handling with proper HTTP status codes

---

## ✨ Features

| Feature | Details |
|---|---|
| 🔐 **Auth** | Register, Login, JWT token, Profile management |
| 📂 **Categories** | Custom expense/income categories with emoji & color |
| 🌱 **Auto-seeding** | 14 default categories created on every registration |
| 💸 **Expenses** | CRUD, pagination, filter by month/year/category/payment mode, keyword search |
| 💰 **Income** | CRUD, pagination, filter by month/year/source |
| 🎯 **Budgets** | Category-wise monthly budgets with real-time spend tracking |
| ⚠️ **Budget Alerts** | Warning response when spending crosses alert threshold % |
| 📊 **Reports** | 7 analytics endpoints covering monthly, yearly, trends, breakdowns |

---

## 🏗️ Architecture

```
expensewise/
├── config/          # SecurityConfig, MongoConfig
├── controller/      # REST controllers (6 controllers, 30+ endpoints)
├── service/         # Business logic layer
├── repository/      # Spring Data MongoDB repositories
├── model/           # MongoDB document models
├── dto/
│   ├── request/     # Input DTOs with @Valid constraints
│   └── response/    # Output DTOs including ApiResponse<T> wrapper
├── security/        # JWT utility, auth filter, UserDetailsService
└── exception/       # Global exception handler + custom exceptions
```

---

## 🗄️ MongoDB Collections

| Collection | Description |
|---|---|
| `users` | User accounts with BCrypt-hashed passwords |
| `categories` | Per-user expense/income categories |
| `expenses` | Expense records with tags, payment mode, month/year |
| `incomes` | Income records with source type |
| `budgets` | Category-wise monthly budgets with live spend tracking |

---

## 🔌 API Endpoints

### Auth — `/api/auth`
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/register` | Register new user (seeds 14 default categories) |
| POST | `/login` | Login and receive JWT token |
| GET | `/profile` | Get logged-in user profile |
| PUT | `/profile` | Update name, currency, income goal |

### Categories — `/api/categories`
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Create custom category |
| GET | `/` | Get all categories for user |
| GET | `/{id}` | Get category by ID |
| GET | `/type/{type}` | Filter by EXPENSE or INCOME |
| PUT | `/{id}` | Update category |
| DELETE | `/{id}` | Delete category |

### Expenses — `/api/expenses`
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Add expense (triggers budget recalculation) |
| GET | `/` | Get all (paginated) |
| GET | `/{id}` | Get by ID |
| GET | `/filter` | Filter by `month+year`, `categoryId`, or `paymentMode` |
| GET | `/search?keyword=` | Search by title or tags (regex) |
| PUT | `/{id}` | Update expense |
| DELETE | `/{id}` | Delete expense |

### Income — `/api/incomes`
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Add income |
| GET | `/` | Get all (paginated) |
| GET | `/{id}` | Get by ID |
| GET | `/filter` | Filter by `month+year` or `source` |
| PUT | `/{id}` | Update income |
| DELETE | `/{id}` | Delete income |

### Budgets — `/api/budgets`
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Set budget for a category/month |
| GET | `/` | Get all budgets |
| GET | `/month?month=&year=` | Get budgets for a specific month |
| GET | `/{id}` | Get budget by ID |
| PUT | `/{id}` | Update budget amount/threshold |
| DELETE | `/{id}` | Delete budget |

### Reports — `/api/reports`
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/summary?month=&year=` | Monthly summary (income, expense, savings, budget status) |
| GET | `/yearly?year=` | Yearly summary with best/worst months |
| GET | `/category-breakdown?month=&year=` | Spending % per category, sorted descending |
| GET | `/income-vs-expense?month=&year=` | Side-by-side income vs expense |
| GET | `/top-expenses?month=&year=&limit=` | Top N expenses by amount |
| GET | `/savings-trend?year=` | Month-by-month savings for full year |
| GET | `/payment-mode-breakdown?month=&year=` | Spend by CASH/UPI/CARD/NETBANKING |

---

## ⚙️ Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Core language |
| Spring Boot | 3.2.5 | Application framework |
| Spring Security | 6.x | Authentication & authorization |
| Spring Data MongoDB | 3.x | Database ORM |
| MongoDB | 7.x (local) | NoSQL document database |
| JJWT | 0.11.5 | JWT generation & validation |
| Lombok | Latest | Boilerplate reduction |
| Jakarta Validation | 3.x | Request DTO validation |
| Maven | 3.9 | Build tool |

---

## 🚀 Getting Started

### Prerequisites

- Java 17+ installed (`java -version`)
- Maven 3.9+ installed (`mvn -version`)
- MongoDB running locally on port `27017`

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/expensewise.git
cd expensewise
```

### 2. Configure Application Properties

```bash
cp src/main/resources/application.properties.example \
   src/main/resources/application.properties
```

Open `application.properties` and set your values:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/expensewise_db
jwt.secret=YOUR_STRONG_SECRET_KEY_MINIMUM_32_CHARACTERS
jwt.expiration=86400000
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The API will start at **`http://localhost:8080`**

You should see:
```
✅ ExpenseWise API is running on http://localhost:8080
```

### 4. Verify

```bash
# Health check (should return 401 — server is running)
curl http://localhost:8080/api/expenses
```

---

## 🧪 Testing with Postman

### Quick Start

1. Import the collection or create requests manually
2. Set environment variables:
   - `base_url` = `http://localhost:8080`
   - `token` = *(set after login)*

### Test Flow

```
1. POST /api/auth/register       → creates user + 14 default categories
2. POST /api/auth/login          → get JWT token
3. GET  /api/categories          → view seeded categories, copy Food & Salary IDs
4. POST /api/expenses            → add expense (use Food category ID)
5. POST /api/incomes             → add income (use Salary category ID)
6. POST /api/budgets             → set budget (spentAmount auto-calculated!)
7. POST /api/expenses            → add large expense → triggers ⚠️ warning
8. GET  /api/reports/summary     → full monthly summary
9. GET  /api/reports/category-breakdown  → spending by category
10. GET /api/reports/savings-trend?year=2026
```

### Sample Register Request

```json
POST /api/auth/register
{
  "name": "Akhil Kumar",
  "username": "akhil123",
  "email": "akhil@example.com",
  "password": "secret123",
  "currency": "INR",
  "monthlyIncomeGoal": 100000
}
```

### Sample Expense Request

```json
POST /api/expenses
Authorization: Bearer <token>

{
  "title": "Lunch at Swiggy",
  "amount": 350.50,
  "categoryId": "<food-category-id>",
  "paymentMode": "UPI",
  "note": "Biryani order",
  "date": "2026-06-15",
  "tags": ["food", "swiggy", "lunch"]
}
```

---

## 💡 Business Logic Highlights

### Budget Recalculation
Every time an expense is **added, updated, or deleted**, the system:
1. Finds the matching budget (same `userId + categoryId + month + year`)
2. Sums all expenses for that scope
3. Updates `spentAmount`, `remainingAmount`, and `isExceeded`
4. Returns a warning if `usedPercent >= alertThreshold`

### Budget Warning Response
```json
{
  "success": true,
  "message": "Expense added",
  "data": {
    "budgetWarning": "⚠️ Warning: You have used 82.1% of your budget for Food"
  }
}
```

### Monthly Summary Report Sample
```json
{
  "month": 6, "year": 2026,
  "totalIncome": 85000.0,
  "totalExpense": 4150.5,
  "netSavings": 80849.5,
  "savingsPercentage": 95.12,
  "topSpendingCategory": "Food",
  "budgetStatus": [
    { "categoryName": "Food", "budgetAmount": 5000, "spentAmount": 4150.5, "status": "WARNING" }
  ]
}
```

### Default Categories (auto-created on registration)

| Type | Categories |
|------|-----------|
| EXPENSE | 🍕 Food, 🚗 Transport, 🏠 Rent, 🛍 Shopping, 🎬 Entertainment, 💊 Health, 📚 Education, 💡 Utilities, 📦 Other |
| INCOME | 💰 Salary, 💻 Freelance, 📈 Investment, 🎁 Gift, 💵 Other |

---

## 📁 Standard API Response Format

All endpoints return a consistent wrapper:

```json
{
  "success": true,
  "message": "Descriptive message",
  "data": { ... }
}
```

Error responses:
```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

### HTTP Status Codes

| Code | Meaning |
|------|---------|
| `200` | OK — successful GET/PUT/DELETE |
| `201` | Created — successful POST |
| `400` | Bad Request — validation failure |
| `401` | Unauthorized — missing or invalid JWT |
| `404` | Not Found — resource doesn't exist |
| `409` | Conflict — duplicate username/email/budget |
| `500` | Internal Server Error |

---

## 🔐 Security

- Passwords hashed with **BCrypt** (strength 10)
- **JWT tokens** valid for 24 hours (configurable via `jwt.expiration`)
- Every request is authenticated via the `Authorization: Bearer <token>` header
- All data queries are **user-scoped** — users can never access another user's data
- `application.properties` is excluded from Git via `.gitignore`

---

## 🗂️ Project Structure

```
src/main/java/com/expensewise/
├── ExpenseWiseApplication.java
├── config/
│   ├── SecurityConfig.java        # Spring Security + JWT filter chain
│   └── MongoConfig.java           # MongoDB client configuration
├── controller/
│   ├── AuthController.java        # /api/auth/**
│   ├── CategoryController.java    # /api/categories/**
│   ├── ExpenseController.java     # /api/expenses/**
│   ├── IncomeController.java      # /api/incomes/**
│   ├── BudgetController.java      # /api/budgets/**
│   └── ReportController.java      # /api/reports/**
├── service/
│   ├── AuthService.java
│   ├── CategoryService.java       # Includes default category seeding
│   ├── ExpenseService.java        # Budget recalculation on every write
│   ├── IncomeService.java
│   ├── BudgetService.java
│   └── ReportService.java         # All 7 analytics computations
├── repository/                    # MongoRepository interfaces
├── model/                         # @Document MongoDB models
├── dto/
│   ├── request/                   # @Valid annotated request DTOs
│   └── response/                  # Response DTOs + ApiResponse<T>
├── security/
│   ├── JwtUtil.java               # Token generation & validation
│   ├── JwtAuthFilter.java         # Per-request JWT extraction
│   └── UserDetailsServiceImpl.java
└── exception/
    ├── GlobalExceptionHandler.java # @RestControllerAdvice
    ├── ResourceNotFoundException.java
    ├── DuplicateResourceException.java
    └── BudgetExceededException.java
```

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 👤 Author

**Akhil Kumar**

[![GitHub](https://img.shields.io/badge/GitHub-akhilkumar--dot-181717?style=flat&logo=github)](https://github.com/akhilkumar-dot)

---

<div align="center">
<sub>Built with ❤️ using Spring Boot & MongoDB</sub>
</div>
