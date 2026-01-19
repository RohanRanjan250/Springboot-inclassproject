# E-Commerce Backend API - Setup & Testing Guide

## Quick Start (5 minutes)

### Prerequisites Check
```bash
# Check Java version (need Java 17+)
java -version

# Check if MongoDB is available
mongosh --version  # or mongo --version
```

### Step 1: Start MongoDB (if not running)
```bash
# Option A: Using Homebrew (macOS)
brew services start mongodb-community

# Option B: Using Docker
docker run -d -p 27017:27017 --name mongodb mongo

# Option C: Verify MongoDB is running
mongosh --eval "db.version()"
```

### Step 2: Build and Run the Application
```bash
cd "/Users/rohanranjan/IdeaProjects/in-class assignment"

# Clean build
./mvnw clean package -DskipTests

# Run the application
./mvnw spring-boot:run
```

**Expected Output:**
```
...
[INFO] Started InClassAssignmentApplication in 2.345 seconds (JVM running for 2.567)
```

The application will be available at: `http://localhost:8080`

### Step 3: Test with Postman

1. **Import Postman Collection:**
   - Open Postman
   - Click "Import" → "Upload Files"
   - Select `Postman_Collection.json`

2. **Set Variables:**
   - Click environment dropdown → "Edit"
   - Set `BASE_URL`: `http://localhost:8080`
   - Set `USER_ID`: `user123` (or any custom value)

3. **Run Complete Flow:**
   - Go to "Complete Order Flow" folder
   - Execute requests in order:
     1. Create Product
     2. Copy returned `id` and set as `{{PRODUCT_ID}}`
     3. Add to Cart
     4. View Cart
     5. Create Order
     6. Copy returned `id` and set as `{{ORDER_ID}}`
     7. Create Payment
     8. **Wait 3 seconds** (mock payment processing)
     9. Check Order Status (status should be "PAID")

## API Testing Examples

### Complete Order Flow (Manual Testing)

#### 1. Create a Product
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

**Response:**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "name": "Laptop",
  "description": "Gaming Laptop",
  "price": 50000.0,
  "stock": 10
}
```

**Save the `id` value!**

#### 2. Add to Cart
```bash
curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "productId": "507f1f77bcf86cd799439011",
    "quantity": 2
  }'
```

#### 3. View Cart
```bash
curl http://localhost:8080/api/cart/user123
```

#### 4. Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId": "user123"}'
```

**Response:**
```json
{
  "id": "507f1f77bcf86cd799439012",
  "userId": "user123",
  "totalAmount": 100000.0,
  "status": "CREATED",
  "items": [...]
}
```

**Save the `id` value!**

#### 5. Create Payment
```bash
curl -X POST http://localhost:8080/api/payments/create \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "507f1f77bcf86cd799439012",
    "amount": 100000.0
  }'
```

#### 6. Wait 3 Seconds (Mock Payment Processing)
```bash
sleep 3
```

#### 7. Check Order Status
```bash
curl http://localhost:8080/api/orders/507f1f77bcf86cd799439012
```

**You should see:**
- `status`: "PAID"
- `payment.status`: "SUCCESS"

## Troubleshooting

### Issue: "Unable to connect to MongoDB"

**Solution:**
1. Verify MongoDB is running:
   ```bash
   mongosh --eval "db.version()"
   ```

2. Check MongoDB is on port 27017:
   ```bash
   lsof -i :27017
   ```

3. If MongoDB isn't running, start it:
   ```bash
   brew services start mongodb-community  # macOS
   # or
   docker run -d -p 27017:27017 mongo    # Docker
   ```

4. Verify connection string in `application.properties`:
   ```
   spring.data.mongodb.uri=mongodb://localhost:27017/ecommerce
   ```

### Issue: Port 8080 Already in Use

**Solution:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process (replace PID with actual process ID)
kill -9 PID

# Or change port in application.properties
# server.port=8081
```

### Issue: "Cart is empty" when creating order

**Solution:**
1. Add products to cart first:
   ```bash
   curl -X POST http://localhost:8080/api/cart/add \
     -H "Content-Type: application/json" \
     -d '{
       "userId": "user123",
       "productId": "PRODUCT_ID",
       "quantity": 1
     }'
   ```

2. Verify cart has items:
   ```bash
   curl http://localhost:8080/api/cart/user123
   ```

### Issue: Payment status not updating

**Solution:**
1. Wait at least 3 seconds after creating payment
2. Check payment status:
   ```bash
   curl http://localhost:8080/api/payments/PAYMENT_ID
   ```
3. Check order status:
   ```bash
   curl http://localhost:8080/api/orders/ORDER_ID
   ```

### Issue: "Insufficient stock" error

**Solution:**
- Create more products with higher stock
- Or reduce quantity in cart

### Issue: Application won't start

**Solution:**
1. Check Java version (need 17+):
   ```bash
   java -version
   ```

2. Check for compilation errors:
   ```bash
   ./mvnw clean compile
   ```

3. Check application logs for errors

## MongoDB Database Management

### View Collections
```bash
mongosh
use ecommerce
show collections
```

### View Sample Data
```bash
# View all products
db.products.find()

# View all orders
db.orders.find()

# View all payments
db.payments.find()

# View specific order with payment
db.orders.findOne({_id: "ORDER_ID"})
```

### Clear All Data (Fresh Start)
```bash
mongosh
use ecommerce
db.dropDatabase()
```

### Check Database Size
```bash
mongosh
use ecommerce
db.stats()
```

## Performance Monitoring

### Check Application Logs
The application logs are printed to console. Look for:
- `DEBUG` level logs for application operations
- `ERROR` level logs for failures

### Monitor Database Operations
```bash
mongosh
use ecommerce

# Count documents in each collection
db.products.countDocuments()
db.orders.countDocuments()
db.payments.countDocuments()
db.cart_items.countDocuments()
```

## Key Test Scenarios

### Scenario 1: Single Product Order
1. Create 1 product
2. Add 1 item to cart
3. Create order
4. Create payment
5. Wait 3 seconds
6. Verify order status is "PAID"

### Scenario 2: Multiple Items Order
1. Create 3 different products
2. Add all 3 products to cart
3. Create order
4. Verify order total = sum of (price * quantity)
5. Create payment
6. Verify payment status updates

### Scenario 3: Stock Management
1. Create product with stock=5
2. Add 6 items to cart
3. Try to create order → should fail with "Insufficient stock"
4. Clear cart
5. Add 5 items to cart
6. Create order → should succeed
7. Check product stock is now 0

### Scenario 4: Error Handling
1. Try to create order with non-existent user → should work (creates new user)
2. Try to add invalid product → should fail
3. Try to create payment for non-existent order → should fail
4. Try to create payment twice for same order → second should fail

## Postman Collection Usage

### Import Collection
```
Postman → Import → Upload Files → Select Postman_Collection.json
```

### Collection Structure
```
E-Commerce Backend API
├── Product Management
│   ├── Create Product - Laptop
│   ├── Create Product - Mouse
│   ├── Create Product - Keyboard
│   ├── Get All Products
│   └── Search Products
├── Cart Management
│   ├── Add Item to Cart
│   ├── Get User Cart
│   └── Clear Cart
├── Order Management
│   ├── Create Order
│   ├── Get Order by ID
│   └── Get User Orders
├── Payment Processing
│   ├── Create Payment
│   ├── Get Payment by ID
│   └── Get Payment by Order ID
└── Complete Order Flow
    ├── 1. Create Product
    ├── 2. Add to Cart
    ├── 3. View Cart
    ├── 4. Create Order
    ├── 5. Create Payment
    └── 6. Check Order Status
```

### Environment Variables
```
BASE_URL = http://localhost:8080
USER_ID = user123
PRODUCT_ID = (copy from create product response)
ORDER_ID = (copy from create order response)
PAYMENT_ID = (copy from create payment response)
```

## Production Deployment Checklist

- ⚠️ **Note**: This is a learning application, not production-ready
- Configure MongoDB with proper authentication
- Use HTTPS in production
- Implement proper authentication/authorization
- Add request validation and error handling
- Configure CORS properly (currently allows all origins)
- Use environment variables for configuration
- Implement logging and monitoring
- Add rate limiting
- Test with load balancing

## Testing Checklist

- ✅ All Product APIs working
- ✅ All Cart APIs working
- ✅ All Order APIs working
- ✅ Payment creation working
- ✅ Webhook processing working
- ✅ Order status updating after payment
- ✅ Stock management working
- ✅ Error handling working
- ✅ MongoDB persistence working

## Common Questions

**Q: How long does the mock payment take?**
A: 3 seconds. This simulates real payment processing time.

**Q: Can I use multiple users?**
A: Yes! Use different `userId` values in requests.

**Q: What happens if I restart the app?**
A: MongoDB data persists, but in-memory operations are reset.

**Q: Can I integrate real Razorpay?**
A: Yes! The `PaymentService` can be modified to call Razorpay API instead.

**Q: How do I debug issues?**
A: Check the console logs and MongoDB data using mongosh.

## Files Overview

```
in-class assignment/
├── src/main/java/com/example/inclassassignment/
│   ├── controller/             # REST API endpoints
│   ├── service/                # Business logic
│   ├── repository/             # Data access layer
│   ├── model/                  # Entity classes
│   ├── dto/                    # Request/response objects
│   ├── webhook/                # Webhook handlers
│   ├── config/                 # Configuration
│   └── InClassAssignmentApplication.java
├── src/main/resources/
│   └── application.properties   # Configuration file
├── pom.xml                      # Maven dependencies
├── README.md                    # Full documentation
├── SETUP_GUIDE.md              # This file
├── Postman_Collection.json     # Postman test collection
└── target/                      # Built artifacts
```

## Need Help?

1. **Check the README.md** for detailed documentation
2. **Review application logs** for error messages
3. **Verify MongoDB is running** - most errors are DB connection issues
4. **Check variable values** in Postman
5. **Ensure all IDs are correct** when making requests

---

**Last Updated**: January 19, 2026
**Status**: Ready for Testing ✅
