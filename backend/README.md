# SpringEcom - E-Commerce Backend Service

A robust E-Commerce Backend Service built with **Spring Boot 3**, integrated with **Spring Security (JWT)**, **PostgreSQL + pgvector**, and **Spring AI (Google Gemini)** powering an intelligent AI Product Search & Sales Assistant.

---

## Tech Stack

### Core Framework & Libraries
* **Language & Runtime**: Java 17 / 21
* **Framework**: Spring Boot 3.x (Spring Web, Spring Data JPA, Spring AOP)
* **Security**: Spring Security, JWT (JSON Web Token), BCrypt Password Encoder
* **Database**: PostgreSQL 16 with `pgvector` extension
* **AI & Vector Search**: Spring AI Framework, Google GenAI API (`gemini-3.5-flash`), `pgvector` Vector Store
* **Cloud Storage**: Cloudinary SDK (Image Upload & CDN Management)
* **Build & Container**: Apache Maven (`mvnw`), Docker, Docker Compose

---

## Key Features

### 1. AI Sales Assistant & Semantic Search (RAG)
* **RAG (Retrieval-Augmented Generation)**: Similarity-based product retrieval powered by PostgreSQL (`pgvector`) distance metrics and summarized by Google Gemini.
* **Function Calling (Tool Calling)**: Integrated `AiSearchTool` enabling the AI assistant to directly query real-time DB prices, stock, and categories without hallucination.
* **Session Chat Memory**: Conversation context preservation using `sessionId`.
* **Vector Ingestion API**: Batch or single-item vector embedding ingestion endpoints to keep the vector database in sync.

### 2. Authentication & Authorization
* Customer registration for new accounts (`ROLE_USER`).
* Secure login with **BCrypt** password hashing and **JWT Access Token** generation.
* Custom `JwtFilter` for stateless token authentication on protected endpoints.
* `/api/auth/me` endpoint to retrieve current authenticated user profile.

### 3. Product & Media Management
* Product catalog listing, detail retrieval by ID, and keyword-based search.
* Full CRUD product operations with direct image uploads to **Cloudinary**.
* Image endpoint featuring automatic 302 HTTP redirection directly to Cloudinary CDN URLs.

### 4. Shopping Cart Management
* Per-user shopping cart persistence tied to JWT user identity.
* Add items to cart, update quantities, remove individual items, or clear the entire cart.
* Automatic total price and item count calculation.

### 5. Order Processing & Tracking
* Checkout workflow converting active cart items into persistent customer orders.
* Detailed line item recording (`OrderItem`) with shipping address, phone, and payment method details.
* Order history listing for authenticated users.

### 6. Data Seeding & Initialization
* `DataSeeder` automatically populates mock catalog data and generates initial Vector Store embeddings upon initial startup.

---

## API Documentation

### 1. AI Chatbot & Vector Ingestion (`/api/ai`)

| HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/ai/ask?question=...&sessionId=...` | Chat with AI Sales Assistant (RAG + Tools) | No |
| `POST` | `/api/ai/recommend?brand=...&category=...&sessionId=...` | Product recommendations by brand/category | No |
| `POST` | `/api/ai/ingest?productId=...` | Sync a single product to Vector Database | No |
| `POST` | `/api/ai/ingest-all` | Sync all existing products to Vector Database | No |

### 2. Authentication (`/api/auth`)

| HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register a new user account | No |
| `POST` | `/api/auth/login` | Authenticate user and receive JWT token | No |
| `GET` | `/api/auth/me` | Fetch current authenticated user profile | Yes (Bearer Token) |

### 3. Products (`/api`)

| HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/products` | Fetch all products list | No |
| `GET` | `/api/product/{id}` | Fetch product details by ID | No |
| `GET` | `/api/product/{id}/image` | Fetch product image URL (302 Redirect to Cloudinary) | No |
| `GET` | `/api/products/search?keyword=...` | Search products by keyword | No |
| `POST` | `/api/product` | Create a new product (with multipart image file) | Yes (Admin/User) |
| `PUT` | `/api/product/{id}` | Update product details and image | Yes (Admin/User) |
| `DELETE` | `/api/product/{id}` | Delete a product by ID | Yes (Admin/User) |

### 4. Shopping Cart (`/api/cart`)

| HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/cart` | Get current user's shopping cart | Yes (Bearer Token) |
| `POST` | `/api/cart/items` | Add product item to cart | Yes (Bearer Token) |
| `PUT` | `/api/cart/items/{itemId}` | Update product quantity in cart | Yes (Bearer Token) |
| `DELETE` | `/api/cart/items/{itemId}` | Remove item from cart | Yes (Bearer Token) |
| `DELETE` | `/api/cart` | Clear all items from cart | Yes (Bearer Token) |

### 5. Orders (`/api`)

| HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/orders/place` | Place order from current cart items | Yes (Bearer Token) |
| `GET` | `/api/orders` | Fetch user's order history | Yes (Bearer Token) |

---

## Project Structure

```text
ecom_app/
├── backend/                         # Spring Boot Backend Project
│   ├── src/main/java/com/example/springecom/
│   │   ├── aop/                     # Aspect-Oriented Programming (Logging, Performance)
│   │   ├── config/                  # System Configurations (AI, Security, Cloudinary, Seeder)
│   │   ├── controller/              # REST Controllers (Auth, Product, Cart, Order, AI Chat)
│   │   ├── model/                   # JPA Entities & Data Transfer Objects (DTOs)
│   │   ├── repo/                    # Spring Data JPA Repositories
│   │   ├── service/                 # Business Logic & AI Vector Services
│   │   └── exception/               # Global Exception Handler
│   ├── Dockerfile                   # Backend Docker build configuration
│   └── pom.xml                      # Maven dependencies
│
├── frontend/                        # React / Vite Frontend Project
│   ├── src/                         # React UI Components & Pages
│   ├── Dockerfile                   # Frontend Docker build configuration
│   └── package.json                 # Node.js dependencies
│
├── docker-compose.yml               # Production Full-Stack Docker Compose (FE + BE + DB)
├── docker-compose-dev.yml           # Local Dev Database Docker Compose (DB only)
├── .env                             # Environment Variables file
└── README.md                        # Project documentation
```

---

## Environment Variables

The application reads configuration settings from the `.env` file located in the root directory or system environment variables:

| Environment Variable | Default | Description |
| :--- | :--- | :--- |
| `DB_USER` | `user` | PostgreSQL username |
| `DB_PASSWORD` | `1234` | PostgreSQL password |
| `DB_NAME` | `ecom` | PostgreSQL database name |
| `GOOGLE_GENAI_API_KEY` | *(Required)* | Google Gemini AI API key |
| `GOOGLE_GENAI_PROJECT_ID` | *(Required)* | Google Cloud / GenAI Project ID |
| `CLOUDINARY_CLOUD_NAME` | *(Required)* | Cloudinary account Cloud Name |
| `CLOUDINARY_API_KEY` | *(Required)* | Cloudinary account API Key |
| `CLOUDINARY_API_SECRET` | *(Required)* | Cloudinary account API Secret |

---

## Installation & Getting Started

### Prerequisites
* **Java Development Kit (JDK)**: Version 17 or 21 (for building backend JAR)
* **Docker & Docker Desktop**: Installed and running on your system

---

### Option 1: Run Full Stack with Docker Compose (Recommended)

1. Clone the repository and navigate to the project root directory:
   ```powershell
   git clone <repository_url>
   cd ecom_app
   ```

2. Ensure your `.env` file exists at the root directory containing all environment variables (`DB_USER`, `DB_PASSWORD`, `GOOGLE_GENAI_...`, `CLOUDINARY_...`).

3. Build the Backend JAR package:
   ```powershell
   cd backend
   .\mvnw clean package -DskipTests
   cd ..
   ```

4. Build and start all 3 Docker containers (Database, Backend, Frontend) in detached mode:
   ```powershell
   docker compose up -d --build
   ```

5. Access the services:
   * **Frontend Application**: [http://localhost:3000](http://localhost:3000)
   * **Backend REST API**: [http://localhost:8080](http://localhost:8080)
   * **PostgreSQL Database**: `localhost:5432`

---

### Option 2: Run Database Container Only (Local Development Mode)

1. Start only the PostgreSQL + pgvector container for local development:
   ```powershell
   docker compose -f docker-compose-dev.yml up -d
   ```

2. Run the Spring Boot Backend from the `backend` directory:
   ```powershell
   cd backend
   .\mvnw spring-boot:run
   ```

3. Run the Frontend from the `frontend` directory:
   ```powershell
   cd frontend
   npm install
   npm run dev
   ```

---

### Useful Docker Commands

```powershell
# View real-time container logs for all services
docker compose logs -f

# View logs for backend container only
docker compose logs -f backend

# Stop all running containers
docker compose down

# Stop containers and remove persisted database volumes
docker compose down -v
```

---

## Security & Authorization

* All user passwords are encrypted using the **BCrypt** hashing algorithm prior to persistence.
* Protected API endpoints require a valid JWT Bearer token in the request header:
  ```text
  Authorization: Bearer <your_jwt_access_token>
  ```
* Cross-Origin Resource Sharing (**CORS**) is configured to allow requests from local frontend development environments (`http://localhost:3000`, `http://localhost:5173`).
