# Bookstore Backend

A backend service for managing an online bookstore, supporting inventory management, purchases with complex pricing rules, and customer loyalty points.

## Features

- **Inventory Management**
    - Add, update, delete books
    - View all books or specific book details

- **Purchase System**
    - Purchase one or more books
    - Apply dynamic pricing based on book type, bundle size, and loyalty points
    - Handles stock decrement with pessimistic locking

- **Customer Loyalty Points**
    - Tracks loyalty points per customer
    - Automatically applies discounts when points reach thresholds

- **Cart System (Optional Extension)**
    - Each customer has one active cart at a time
    - Supports adding books to cart, viewing cart, and checking out

## Technologies

- **Java 23**
- **Spring Boot 3.3**
- **Hibernate 6.5 / JPA**
- **PostgreSQL**
- **Flyway** for database migrations
- **SpringDoc OpenAPI** for API documentation
- **Jakarta Validation** for input validation
- **JUnit 5** + **Mockito** for testing
- **Zonky Embedded Postgres** for integration testing

## Running the Application

1. **Clone the repository:**

```bash
git clone https://github.com/your-repo/bookstore-backend.git
cd bookstore-backend
```

2. Start using docker-compose

```docker-compose up --build```

3. **API Documentation:**

Visit [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Key Design Decisions

### **1. Separation of Concerns**
- **Entities:** Represent DB structure (`BookEntity`, `CustomerEntity`, `PurchaseOrderEntity`).
- **DTOs (Requests/Responses):** Generated from OpenAPI specs, keeping external contracts stable.
- **Converters:** Implemented via Spring's `ConversionService` for flexible mapping between DTOs and Entities.

### **2. Pricing Logic Encapsulated**
- Implemented in **PriceCalculator** domain service, isolated from persistence logic.

### **3. Concurrency Control**
- **Pessimistic locking** on book stock updates to prevent overselling.

### **4. Loyalty Points Logic**
- 1 point awarded per purchased book.
- Once **10 points** are accumulated, the customer can get one **Regular** or **Old Edition** book for free. After application, points reset.

### **5. Cart Design (Draft)**
- Only **one active cart per customer** at a time.
- Cart includes:
    - `List<BookEntity>`
    - `checkedOut` flag
- **Can reuse to implement more complex checkout process**:

### **6. Database migration using Flyway**
- Enables clear versioning of the schema
- Helps to plug in DDL scripts into tests

### **7. Hibernate for ORM layer**
- Ideal for prototyping demo solution
- Can rework repositories to use JDBC Template for more control over queries

### **8. Docker Compose to deploy the solution**
- Easily launch the solution in one command
- Bundled with the database