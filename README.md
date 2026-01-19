# E-Commerce Backend API

A minimal e-commerce backend system built with Spring Boot and MongoDB that demonstrates core e-commerce functionality including product management, shopping cart, order processing, and payment integration with webhook support.

## Project Overview

This application implements a complete e-commerce workflow with the following features:

- **Product Management**: Create and list products with inventory management
- **Shopping Cart**: Add items to cart, view cart, and clear cart functionality
- **Order Processing**: Create orders from cart items with automatic stock management
- **Payment Processing**: Mock payment service with webhook callbacks
- **Order Status Updates**: Automatic status updates based on payment results

## Architecture

```
┌─────────────────────┐
│   Client (Postman)  │
└──────────┬──────────┘
           │
           ▼
┌──────────────────────────────┐
│   Spring Boot REST API       │
│   Port: 8080                 │
├──────────────────────────────┤
│ • ProductController          │
│ • CartController             │
│ • OrderController            │
│ • PaymentController          │
│ • WebhookController          │
└──────────────────┬───────────┘
                   │
                   ▼
         ┌──────────────────┐
         │  MongoDB (Local) │
         │  Database        │
         └──────────────────┘
```

## Technology Stack

- **Framework**: Spring Boot 4.0.1
- **Database**: MongoDB
- **Language**: Java 17
- **Build Tool**: Maven
- **Additional Dependencies**:
  - Spring Data MongoDB
  - Lombok
  - Spring Web MVC

## Project Structure

```
com.example.inclassassignment
├── config/
│   └── RestTemplateConfig.java          # REST template configuration
├── controller/
│   ├── ProductController.java           # Product API endpoints
│   ├── CartController.java              # Cart API endpoints
│   ├── OrderController.java             # Order API endpoints
│   ├── PaymentController.java           # Payment API endpoints
├── webhook/
│   └── PaymentWebhookController.java    # Webhook handler
├── service/
│   ├── ProductService.java              # Product business logic
│   ├── CartService.java                 # Cart business logic
│   ├── OrderService.java                # Order business logic
│   ├── PaymentService.java              # Payment business logic
├── repository/
│   ├── ProductRepository.java
│   ├── CartRepository.java
│   ├── OrderRepository.java
│   ├── PaymentRepository.java
│   ├── UserRepository.java
├── model/
│   ├── User.java
│   ├── Product.java
│   ├── CartItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Payment.java
├── dto/
│   ├── AddToCartRequest.java
│   ├── CreateOrderRequest.java
│   ├── PaymentRequest.java
│   ├── PaymentWebhookRequest.java
│   ├── ProductResponse.java
│   ├── CartItemResponse.java
│   ├── OrderResponse.java
│   ├── OrderItemResponse.java
│   ├── PaymentResponse.java
└── InClassAssignmentApplication.java    # Main application
```

## Database Schema

### Collections

1. **users** - User information
2. **products** - Product catalog
3. **cart_items** - Shopping cart items
4. **orders** - Customer orders
5. **payments** - Payment records

### Entity Relationships

```
USER (1) ──── (N) CART_ITEM
USER (1) ──── (N) ORDER
PRODUCT (1) ──── (N) CART_ITEM
PRODUCT (1) ──── (N) ORDER_ITEM
ORDER (1) ──── (N) ORDER_ITEM
ORDER (1) ──── (1) PAYMENT
```

## Prerequisites

- Java 17 or higher
- MongoDB running locally on port 27017
- Postman (for API testing)
- Maven (or use the included mvnw wrapper)

## Installation & Setup

### 1. Start MongoDB

```bash
# Using Homebrew (macOS)
brew services start mongodb-community

# Or using Docker
docker run -d -p 27017:27017 --name mongodb mongo
```

### 2. Build the Project

```bash
cd "/Users/rohanranjan/IdeaProjects/in-class assignment"
./mvnw clean install
```

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Product APIs

#### Create Product
```
POST /api/products
Content-Type: application/json

{
  "name": "Laptop",
  "description": "Gaming Laptop",
  "price": 50000.0,
  "stock": 10
}
```

#### Get All Products
```
GET /api/products
```

#### Search Products
```
GET /api/products/search?q=laptop
```

### Cart APIs

#### Add to Cart
```
POST /api/cart/add
Content-Type: application/json

{
  "userId": "user123",
  "productId": "prod123",
  "quantity": 2
}
```

#### Get User's Cart
```
GET /api/cart/{userId}
```

#### Clear Cart
```
DELETE /api/cart/{userId}/clear
```

### Order APIs

#### Create Order
```
POST /api/orders
Content-Type: application/json

{
  "userId": "user123"
}
```

#### Get Order by ID
```
GET /api/orders/{orderId}
```

#### Get User's Orders
```
GET /api/orders/user/{userId}
```

### Payment APIs

#### Create Payment
```
POST /api/payments/create
Content-Type: application/json

{
  "orderId": "order123",
  "amount": 100000.0
}
```

#### Get Payment by ID
```
GET /api/payments/{paymentId}
```

#### Get Payment by Order ID
```
GET /api/payments/order/{orderId}
```

### Webhook API

#### Payment Webhook
```
POST /api/webhooks/payment
Content-Type: application/json

{
  "paymentId": "pay_xxxxx",
  "orderId": "order123",
  "status": "SUCCESS"
}
```

## Complete Order Flow

### Step-by-Step Example

1. **Create Products**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "Gaming Laptop",
    "price": 50000.0,
    "stock": 10
  }'
```
Save the returned `id` as `PRODUCT_ID`

2. **Add Items to Cart**
```bash
curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "productId": "PRODUCT_ID",
    "quantity": 2
  }'
```

3. **View Cart**
```bash
curl http://localhost:8080/api/cart/user123
```

4. **Create Order**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": "user123"}'
```
Save the returned `id` as `ORDER_ID`

5. **Create Payment**
```bash
curl -X POST http://localhost:8080/api/payments/create \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "ORDER_ID",
    "amount": 100000.0
  }'
```

6. **Wait for Mock Payment Processing**
The mock payment service automatically processes payment after 3 seconds and updates:
- Payment status → SUCCESS
- Order status → PAID

7. **Check Order Status** (after 3 seconds)
```bash
curl http://localhost:8080/api/orders/ORDER_ID
```

## Mock Payment Service

The application includes a mock payment service that:
- Accepts payment creation requests
- Processes payment asynchronously (3-second delay)
- Automatically calls the webhook endpoint
- Updates payment and order status

**No external configuration needed** - it works out of the box!

## Error Handling

The application handles various error scenarios:

- **Product not found**: Returns 404 when product doesn't exist
- **Insufficient stock**: Returns 400 when trying to order more than available
- **Empty cart**: Returns 400 when trying to create order from empty cart
- **Invalid payment**: Returns 400 when payment details are invalid

## Testing with Postman

1. Import the provided Postman collection
2. Set environment variables:
   - `BASE_URL`: http://localhost:8080
   - `USER_ID`: user123
   - `PRODUCT_ID`: (from create product response)
   - `ORDER_ID`: (from create order response)
   - `PAYMENT_ID`: (from create payment response)

3. Run the complete flow:
   - Create Products
   - Add to Cart
   - Get Cart
   - Create Order
   - Create Payment
   - Wait 3 seconds
   - Get Order Status

## Sample Data

### Products to Create
```json
[
  {
    "name": "Laptop",
    "description": "Gaming Laptop",
    "price": 50000.0,
    "stock": 10
  },
  {
    "name": "Mouse",
    "description": "Wireless Mouse",
    "price": 1000.0,
    "stock": 50
  },
  {
    "name": "Keyboard",
    "description": "Mechanical Keyboard",
    "price": 3000.0,
    "stock": 30
  }
]
```

## Database Collections

After running the application, MongoDB will automatically create these collections:

```
ecommerce/
├── users
├── products
├── cart_items
├── orders
└── payments
```

View data using MongoDB client:
```bash
# Connect to MongoDB
mongosh

# View collections
use ecommerce
db.products.find()
db.orders.find()
db.payments.find()
```

## Key Features Implemented

✅ **Product Management**
- Create products
- List all products
- Search products by name

✅ **Shopping Cart**
- Add items to cart
- View cart items with product details
- Clear entire cart
- Automatic quantity updates for duplicate items

✅ **Order Processing**
- Create orders from cart items
- Automatic stock deduction
- Calculate order totals
- Store order history
- Support multiple items per order

✅ **Payment Integration**
- Mock payment service (no external dependencies)
- Automatic webhook simulation after 3 seconds
- Payment status tracking
- Order status updates based on payment

✅ **Webhook Support**
- Webhook endpoint for payment callbacks
- Automatic order status updates
- Payment status updates
- Error handling and logging

## Bonus Features Implemented

✅ **Search Products**
- `GET /api/products/search?q=<query>` - Search products by name

✅ **Order History**
- `GET /api/orders/user/{userId}` - Get all orders for a user

✅ **Complete Order Details**
- Order response includes payment information
- Order response includes all order items

## Building and Testing

### Build JAR
```bash
./mvnw clean package
```

### Run Tests
```bash
./mvnw test
```

### Run Application
```bash
./mvnw spring-boot:run
```

## Logging

Check application logs for debugging:
- Location: Console output when running with `spring-boot:run`
- Log Level: DEBUG for application code, INFO for Spring framework

## Troubleshooting

### MongoDB Connection Error
**Issue**: "Unable to connect to MongoDB"
**Solution**: 
1. Ensure MongoDB is running on port 27017
2. Check connection string in `application.properties`

### Product Not Found in Cart
**Issue**: Adding product to cart fails
**Solution**: 
1. Create a product first
2. Use the returned `id` from create product response

### Order Creation Fails
**Issue**: "Cart is empty" error
**Solution**:
1. Add items to cart first
2. Verify cart has items using GET /api/cart/{userId}

### Payment Not Updating
**Issue**: Order status not changing to PAID
**Solution**:
1. Wait 3 seconds after creating payment
2. Check payment status using GET /api/payments/{paymentId}
3. Verify MongoDB is running

## Performance Considerations

- **Stock Updates**: Atomic operations on product stock
- **Cart Management**: Efficient lookup by userId and productId
- **Order History**: Indexed queries by userId
- **Async Processing**: Payment processing happens asynchronously

## Security Notes

- All endpoints have CORS enabled for testing
- Input validation on all API requests
- Error messages don't expose internal details
- MongoDB data is local and not exposed to network

## API Response Examples

### Product Response
```json
{
  "id": "507f1f77bcf86cd799439011",
  "name": "Laptop",
  "description": "Gaming Laptop",
  "price": 50000.0,
  "stock": 10
}
```

### Order Response
```json
{
  "id": "507f1f77bcf86cd799439012",
  "userId": "user123",
  "totalAmount": 100000.0,
  "status": "PAID",
  "items": [
    {
      "productId": "507f1f77bcf86cd799439011",
      "quantity": 2,
      "price": 50000.0
    }
  ],
  "payment": {
    "id": "507f1f77bcf86cd799439013",
    "orderId": "507f1f77bcf86cd799439012",
    "amount": 100000.0,
    "status": "SUCCESS",
    "paymentId": "pay_xxxxx"
  }
}
```


---
