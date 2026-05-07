# Supplier Management Module

A production-ready Spring Boot 3.x REST API for managing suppliers in a Stock Management system. Includes Kafka event publishing, PostgreSQL persistence, Spring Security, and Swagger UI.



## Technology Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2.x |
| Language | Java 17 |
| Database | PostgreSQL 15 |
| ORM | JPA / Hibernate |
| Messaging | Apache Kafka |
| Security | Spring Security (HTTP Basic) |
| API Docs | SpringDoc OpenAPI 3 (Swagger) |
| Build | Maven 3.9+ |
| Container | Docker + Docker Compose |



##  Project Structure


supplier-management/
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── pom.xml
├── README.md
└── src/
├── main/
│   ├── java/com/stockmanager/supplier/
│   │   ├── SupplierManagementApplication.java
│   │   ├── config/
│   │   │   ├── KafkaConfig.java
│   │   │   ├── OpenApiConfig.java
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   └── SupplierController.java
│   │   ├── dto/
│   │   │   └── SupplierDto.java
│   │   ├── entity/
│   │   │   └── Supplier.java
│   │   ├── exception/
│   │   │   ├── DuplicateSupplierException.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── SupplierNotFoundException.java
│   │   ├── kafka/
│   │   │   ├── SupplierEvent.java
│   │   │   └── SupplierKafkaProducer.java
│   │   ├── repository/
│   │   │   └── SupplierRepository.java
│   │   └── service/
│   │       ├── SupplierService.java
│   │       └── impl/
│   │           └── SupplierServiceImpl.java
│   └── resources/
│       └── application.properties
└── test/
└── java/com/stockmanager/supplier/
└── service/
└── SupplierServiceImplTest.java

## API Endpoints

| Method | Route | Description |
|--------|-------|-------------|
| `POST` | `/api/v1/suppliers/add` | Create a new supplier |
| `PUT` | `/api/v1/suppliers/update/{id}` | Update an existing supplier |
| `GET` | `/api/v1/suppliers/{id}` | Retrieve a supplier by ID |
| `DELETE` | `/api/v1/suppliers/delete/{id}` | Soft-delete a supplier |

All endpoints require **HTTP Basic Authentication**.

---

## Kafka Events

Every API operation publishes a `SupplierEvent` to the `supplier-events` topic:

| Operation | Event Type |
|-----------|------------|
| POST Add | `SUPPLIER_CREATED` |
| PUT Update | `SUPPLIER_UPDATED` |
| GET Retrieve | `SUPPLIER_RETRIEVED` |
| DELETE | `SUPPLIER_DELETED` |



## Running with Docker (Recommended)

### Prerequisites
- Docker 24+
- Docker Compose v2+

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/Sibilla-Heureuse/supplier-management.git
cd supplier-management

# 2. Create your environment file
cp .env.example .env

# 3. Build and start all services
docker compose up --build

# 4. Wait for all services to be healthy (60-90 seconds)
docker compose ps
```

The application will be available at:
- **API Base URL**: http://localhost:8080/api/v1/suppliers
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **PostgreSQL**: localhost:5432
- **Kafka**: localhost:9092

### Stop all services
```bash
docker compose down

# To also remove volumes (wipe data)
docker compose down -v
```



## Running Locally (Without Docker)

### Prerequisites
- Java 17+
- Maven 3.9+
- PostgreSQL running on localhost:5432
- Apache Kafka running on localhost:9092

### Steps

```bash
# 1. Create the database
psql -U postgres -c "CREATE DATABASE supplier_db;"

# 2. Set environment variables
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# 3. Build and run
mvn clean package -DskipTests
java -jar target/supplier-management-1.0.0.jar
```



## Authentication

The API uses **HTTP Basic Authentication**.

Default credentials:
- Username: `admin`
- Password: `admin123`

### Example with curl

```bash
# Create a supplier
curl -X POST http://localhost:8080/api/v1/suppliers/add \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "supplierCode": "SUP-001",
    "name": "Acme Corporation",
    "email": "contact@acme.com",
    "phone": "+1-800-555-0199",
    "country": "United States",
    "city": "New York",
    "contactPerson": "John Smith"
  }'

# Get a supplier
curl -X GET http://localhost:8080/api/v1/suppliers/1 \
  -u admin:admin123

# Update a supplier
curl -X PUT http://localhost:8080/api/v1/suppliers/update/1 \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Acme Corp Updated",
    "email": "new@acme.com",
    "country": "Canada"
  }'

# Delete a supplier
curl -X DELETE http://localhost:8080/api/v1/suppliers/delete/1 \
  -u admin:admin123
```



## Running Tests

```bash
mvn test
```



## Environment Variables Reference

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `supplier_db` | Database name |
| `DB_USERNAME` | `postgres` | DB username |
| `DB_PASSWORD` | `postgres` | DB password |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka brokers |
| `SECURITY_USERNAME` | `admin` | API username |
| `SECURITY_PASSWORD` | `admin123` | API password |