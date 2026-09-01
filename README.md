# Farmer-to-Retailer Trading Platform

A Java Spring Boot REST API for a farmer-to-retailer trading platform, with JWT-based
role authentication for three user types: **FARMER**, **RETAILER**, and **ADMIN**.

## Tech Stack
- Java 17
- Spring Boot 3.3 (Web, Data JPA, Security, Validation)
- Spring Security with JWT (jjwt)
- H2 in-memory database (zero setup — swap to MySQL any time, see below)
- Maven
- Lombok

## Features
- Registration/login per role, returning a JWT
- Farmers list, update, and deactivate products
- Retailers browse products and place orders
- Farmers see incoming orders on their products and update order status
  (PENDING → CONFIRMED → SHIPPED → DELIVERED, or CANCELLED)
- Admins can view all users, products, orders, and basic platform stats
- Role-based access enforced at both the Spring Security filter-chain level and
  the service layer (e.g., a retailer can't edit someone else's product listing)

## Project Structure
```
farmer-retailer-platform/
├── pom.xml
└── src/main/java/com/farmtrade/platform/
    ├── FarmTradeApplication.java
    ├── config/          # Spring Security + JWT filter setup
    ├── controller/       # REST endpoints
    ├── dto/               # Request/response payloads
    ├── exception/       # Centralized error handling
    ├── model/             # JPA entities (User, Product, Order, enums)
    ├── repository/     # Spring Data JPA repositories
    └── service/          # Business logic
```

## Running the project

### 1. Prerequisites
- JDK 17+
- Maven (or use the included Maven wrapper if you add one)
- VS Code with the "Extension Pack for Java" and "Spring Boot Extension Pack"

### 2. Import into VS Code
1. Unzip the project.
2. Open the folder in VS Code (`File > Open Folder`).
3. VS Code should auto-detect the Maven project and start downloading dependencies.
4. Wait for the Java language server to finish indexing (bottom status bar).

### 3. Run
- Open `FarmTradeApplication.java`.
- Click **Run** above the `main` method, or press `F5`.
- Or from a terminal in the project root:
  ```bash
  mvn spring-boot:run
  ```
- The API starts on **http://localhost:8080**.
- H2 console (to inspect data): **http://localhost:8080/h2-console**
  - JDBC URL: `jdbc:h2:mem:farmtradedb`, user `sa`, blank password.

### 4. Switch to MySQL later (optional)
In `application.properties`, comment out the H2 block and uncomment the MySQL block,
then uncomment the `mysql-connector-j` dependency in `pom.xml`.

## API Reference

### Auth (public)
| Method | Endpoint | Body |
|---|---|---|
| POST | `/api/auth/register` | `{ fullName, email, password, phone, role }` (`role`: FARMER/RETAILER/ADMIN) |
| POST | `/api/auth/login` | `{ email, password }` |

Both return `{ token, email, fullName, role }`. Send the token on every other request as:
```
Authorization: Bearer <token>
```

### Products
| Method | Endpoint | Role | Description |
|---|---|---|---|
| GET | `/api/products` | Public | List all active products (optional `?category=`) |
| GET | `/api/products/{id}` | Public | Product detail |
| GET | `/api/products/my-listings` | FARMER | My own listings |
| POST | `/api/products` | FARMER | Create listing |
| PUT | `/api/products/{id}` | FARMER (owner) / ADMIN | Update listing |
| DELETE | `/api/products/{id}` | FARMER (owner) / ADMIN | Deactivate listing |

### Orders
| Method | Endpoint | Role | Description |
|---|---|---|---|
| POST | `/api/orders` | RETAILER | Place an order (`{ productId, quantity }`) |
| GET | `/api/orders` | Any | Retailer → their orders; Farmer → incoming orders on their products; Admin → all |
| PATCH | `/api/orders/{id}/status` | FARMER (owner) / ADMIN | Update status (`{ status }`) |

### Admin
| Method | Endpoint |
|---|---|
| GET | `/api/admin/users` |
| GET | `/api/admin/products` |
| GET | `/api/admin/orders` |
| GET | `/api/admin/stats` |

## Quick test flow (curl)
```bash
# Register a farmer
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Ravi Kumar","email":"ravi@farm.com","password":"pass123","role":"FARMER"}'

# Register a retailer
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Sita Traders","email":"sita@retail.com","password":"pass123","role":"RETAILER"}'

# Farmer lists a product (use the token from farmer registration above)
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <FARMER_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Tomatoes","category":"Vegetables","pricePerUnit":25,"unit":"kg","quantityAvailable":500}'

# Retailer places an order
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <RETAILER_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":50}'
```

## Notes for your resume
This gives you real numbers to cite:
- 3 role-based dashboards (Farmer, Retailer, Admin) via role-scoped JWT auth
- 13 REST endpoints across auth, products, orders, and admin
- Order placement automatically decrements stock and computes total price server-side
