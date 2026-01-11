# Habesha Bites Restaurant API - Testing Guide

This document contains all API endpoints with example request bodies for testing.

**Base URL:** `http://localhost:8080`

**Note:** For authenticated endpoints, include the JWT token in the Authorization header:
```
Authorization: Bearer <your_jwt_token>
```

---

## Table of Contents
1. [Authentication APIs](#authentication-apis)
2. [Food APIs](#food-apis)
3. [Order APIs](#order-apis)
4. [User APIs](#user-apis)

---

## Authentication APIs

### 1. Register User
**Endpoint:** `POST /api/auth/register`  
**Access:** Public

**Request Body:**
```json
{
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1234567890",
  "password": "password123"
}
```

**Response Example:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### 2. Login
**Endpoint:** `POST /api/auth/login`  
**Access:** Public

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

**Response Example:**
```json
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Save the token from the response for authenticated requests!**

---

## Food APIs

### 3. Get All Foods
**Endpoint:** `GET /api/foods`  
**Access:** Public  
**Headers:** None required

**Request Body:** None

**Response Example:**
```json
[
  {
    "id": 1,
    "name": "Injera with Doro Wat",
    "description": "Traditional Ethiopian flatbread with spicy chicken stew",
    "price": 15.99,
    "category": "Main Course",
    "imageUrl": "https://example.com/images/injera-doro.jpg",
    "isAvailable": true
  }
]
```

---

### 4. Get Available Foods
**Endpoint:** `GET /api/foods/available`  
**Access:** Public  
**Headers:** None required

**Request Body:** None

---

### 5. Get Foods by Category
**Endpoint:** `GET /api/foods/category/{category}`  
**Access:** Public  
**Headers:** None required

**Example:** `GET /api/foods/category/Main Course`

**Request Body:** None

---

### 6. Get Food by ID
**Endpoint:** `GET /api/foods/{id}`  
**Access:** Public  
**Headers:** None required

**Example:** `GET /api/foods/1`

**Request Body:** None

---

### 7. Create Food (ADMIN ONLY)
**Endpoint:** `POST /api/foods`  
**Access:** Admin only  
**Headers:** 
```
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Tibs",
  "description": "Sautéed beef or lamb with vegetables and spices",
  "price": 18.99,
  "category": "Main Course",
  "imageUrl": "https://example.com/images/tibs.jpg",
  "isAvailable": true
}
```

**Minimal Request Body (isAvailable defaults to true):**
```json
{
  "name": "Shiro",
  "description": "Spiced chickpea stew",
  "price": 12.99,
  "category": "Main Course",
  "imageUrl": "https://example.com/images/shiro.jpg"
}
```

---

### 8. Update Food (ADMIN ONLY)
**Endpoint:** `PUT /api/foods/{id}`  
**Access:** Admin only  
**Headers:** 
```
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
```

**Example:** `PUT /api/foods/1`

**Request Body:**
```json
{
  "name": "Tibs (Updated)",
  "description": "Updated description for sautéed beef or lamb",
  "price": 19.99,
  "category": "Main Course",
  "imageUrl": "https://example.com/images/tibs-updated.jpg",
  "isAvailable": false
}
```

---

### 9. Delete Food (ADMIN ONLY)
**Endpoint:** `DELETE /api/foods/{id}`  
**Access:** Admin only  
**Headers:** 
```
Authorization: Bearer <admin_jwt_token>
```

**Example:** `DELETE /api/foods/1`

**Request Body:** None

**Response:** 204 No Content (on success)

---

## Order APIs

### 10. Create Order
**Endpoint:** `POST /api/orders`  
**Access:** Authenticated users  
**Headers:** 
```
Authorization: Bearer <user_jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "items": [
    {
      "foodId": 1,
      "quantity": 2
    },
    {
      "foodId": 3,
      "quantity": 1
    }
  ],
  "deliveryAddress": "123 Main Street, City, State 12345",
  "phoneNumber": "+1234567890"
}
```

**Request Body (phoneNumber optional - uses user's phone if not provided):**
```json
{
  "items": [
    {
      "foodId": 1,
      "quantity": 2
    }
  ],
  "deliveryAddress": "123 Main Street, City, State 12345"
}
```

**Response Example:**
```json
{
  "id": 1,
  "userId": 1,
  "userEmail": "john.doe@example.com",
  "items": [
    {
      "id": 1,
      "foodId": 1,
      "foodName": "Injera with Doro Wat",
      "quantity": 2,
      "price": 15.99
    }
  ],
  "totalAmount": 31.98,
  "status": "PENDING",
  "deliveryAddress": "123 Main Street, City, State 12345",
  "phoneNumber": "+1234567890",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

---

### 11. Get My Orders
**Endpoint:** `GET /api/orders/my-orders`  
**Access:** Authenticated users  
**Headers:** 
```
Authorization: Bearer <user_jwt_token>
```

**Request Body:** None

**Response Example:**
```json
[
  {
    "id": 1,
    "userId": 1,
    "userEmail": "john.doe@example.com",
    "items": [...],
    "totalAmount": 31.98,
    "status": "PENDING",
    "deliveryAddress": "123 Main Street, City, State 12345",
    "phoneNumber": "+1234567890",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
]
```

---

### 12. Get Order by ID
**Endpoint:** `GET /api/orders/{id}`  
**Access:** Authenticated users (own orders) or Admin (all orders)  
**Headers:** 
```
Authorization: Bearer <user_jwt_token>
```

**Example:** `GET /api/orders/1`

**Request Body:** None

---

### 13. Cancel Order
**Endpoint:** `PUT /api/orders/{id}/cancel`  
**Access:** Authenticated users (own orders) or Admin  
**Headers:** 
```
Authorization: Bearer <user_jwt_token>
```

**Example:** `PUT /api/orders/1/cancel`

**Request Body:** None

**Response:** 200 OK (on success)

---

### 14. Get All Orders (ADMIN ONLY)
**Endpoint:** `GET /api/orders`  
**Access:** Admin only  
**Headers:** 
```
Authorization: Bearer <admin_jwt_token>
```

**Request Body:** None

**Response Example:**
```json
[
  {
    "id": 1,
    "userId": 1,
    "userEmail": "john.doe@example.com",
    "items": [...],
    "totalAmount": 31.98,
    "status": "PENDING",
    "deliveryAddress": "123 Main Street, City, State 12345",
    "phoneNumber": "+1234567890",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
]
```

---

### 15. Get Orders by Status (ADMIN ONLY)
**Endpoint:** `GET /api/orders/status/{status}`  
**Access:** Admin only  
**Headers:** 
```
Authorization: Bearer <admin_jwt_token>
```

**Example:** `GET /api/orders/status/PENDING`

**Available Status Values:**
- `PENDING`
- `CONFIRMED`
- `PREPARING`
- `READY`
- `OUT_FOR_DELIVERY`
- `DELIVERED`
- `CANCELLED`

**Request Body:** None

---

### 16. Update Order Status (ADMIN ONLY)
**Endpoint:** `PUT /api/orders/{id}/status`  
**Access:** Admin only  
**Headers:** 
```
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json
```

**Example:** `PUT /api/orders/1/status`

**Request Body:**
```json
{
  "status": "CONFIRMED"
}
```

**Other status examples:**
```json
{
  "status": "PREPARING"
}
```

```json
{
  "status": "READY"
}
```

```json
{
  "status": "OUT_FOR_DELIVERY"
}
```

```json
{
  "status": "DELIVERED"
}
```

---

## User APIs

### 17. Get My Profile
**Endpoint:** `GET /api/users/profile`  
**Access:** Authenticated users  
**Headers:** 
```
Authorization: Bearer <user_jwt_token>
```

**Request Body:** None

**Response Example:**
```json
{
  "id": 1,
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1234567890",
  "role": "USER",
  "createdAt": "2024-01-15T08:00:00"
}
```

---

### 18. Get User by ID
**Endpoint:** `GET /api/users/{id}`  
**Access:** Authenticated users  
**Headers:** 
```
Authorization: Bearer <user_jwt_token>
```

**Example:** `GET /api/users/1`

**Request Body:** None

---

## Testing Workflow

### Step 1: Register a User
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "fullName": "Test User",
  "email": "test@example.com",
  "phone": "+1234567890",
  "password": "test123"
}
```
**Save the token from response!**

### Step 2: Login (Alternative)
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "test123"
}
```
**Save the token from response!**

### Step 3: Create Food Items (as Admin)
```bash
POST http://localhost:8080/api/foods
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "name": "Injera with Doro Wat",
  "description": "Traditional Ethiopian flatbread with spicy chicken stew",
  "price": 15.99,
  "category": "Main Course",
  "imageUrl": "https://example.com/images/injera-doro.jpg",
  "isAvailable": true
}
```

### Step 4: Get Available Foods
```bash
GET http://localhost:8080/api/foods/available
```

### Step 5: Create an Order
```bash
POST http://localhost:8080/api/orders
Authorization: Bearer <user_token>
Content-Type: application/json

{
  "items": [
    {
      "foodId": 1,
      "quantity": 2
    }
  ],
  "deliveryAddress": "123 Main Street, City, State 12345",
  "phoneNumber": "+1234567890"
}
```

### Step 6: View My Orders
```bash
GET http://localhost:8080/api/orders/my-orders
Authorization: Bearer <user_token>
```

### Step 7: Update Order Status (as Admin)
```bash
PUT http://localhost:8080/api/orders/1/status
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "status": "CONFIRMED"
}
```

---

## Using cURL Examples

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "phone": "+1234567890",
    "password": "password123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Create Food (Admin)
```bash
curl -X POST http://localhost:8080/api/foods \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tibs",
    "description": "Sautéed beef with vegetables",
    "price": 18.99,
    "category": "Main Course",
    "imageUrl": "https://example.com/tibs.jpg",
    "isAvailable": true
  }'
```

### Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer YOUR_USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "foodId": 1,
        "quantity": 2
      }
    ],
    "deliveryAddress": "123 Main St",
    "phoneNumber": "+1234567890"
  }'
```

### Get My Orders
```bash
curl -X GET http://localhost:8080/api/orders/my-orders \
  -H "Authorization: Bearer YOUR_USER_TOKEN"
```

---

## Using Postman/Insomnia

1. **Create a new request**
2. **Set the method** (GET, POST, PUT, DELETE)
3. **Enter the URL** (e.g., `http://localhost:8080/api/auth/register`)
4. **For POST/PUT requests:**
   - Go to "Body" tab
   - Select "raw" and "JSON"
   - Paste the JSON from the examples above
5. **For authenticated requests:**
   - Go to "Headers" tab
   - Add: `Authorization: Bearer <your_token>`
   - Add: `Content-Type: application/json`

---

## Common Response Codes

- **200 OK** - Request successful
- **201 Created** - Resource created successfully
- **204 No Content** - Request successful, no content to return
- **400 Bad Request** - Invalid request data
- **401 Unauthorized** - Missing or invalid authentication token
- **403 Forbidden** - Insufficient permissions (e.g., non-admin trying admin endpoint)
- **404 Not Found** - Resource not found
- **500 Internal Server Error** - Server error

---

## Notes

1. **JWT Token Expiration:** Tokens expire after 24 hours. Re-login to get a new token.

2. **Admin Access:** To test admin endpoints, you need to manually set a user's role to `ADMIN` in the database, or create an admin user registration endpoint.

3. **Order Status Flow:** 
   - PENDING → CONFIRMED → PREPARING → READY → OUT_FOR_DELIVERY → DELIVERED
   - Can be CANCELLED at any time before DELIVERED

4. **Food Categories:** Examples include "Main Course", "Appetizer", "Dessert", "Drink", etc.

5. **Phone Number Format:** Can be any string format, but recommended to use international format.

---

## Quick Test Checklist

- [ ] Register a new user
- [ ] Login and get token
- [ ] Get all foods (public)
- [ ] Get available foods (public)
- [ ] Get foods by category (public)
- [ ] Create food item (admin)
- [ ] Update food item (admin)
- [ ] Delete food item (admin)
- [ ] Create an order (authenticated)
- [ ] Get my orders (authenticated)
- [ ] Get order by ID (authenticated)
- [ ] Cancel order (authenticated)
- [ ] Get all orders (admin)
- [ ] Get orders by status (admin)
- [ ] Update order status (admin)
- [ ] Get my profile (authenticated)
- [ ] Get user by ID (authenticated)

