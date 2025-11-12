# Backend Connection Setup Guide

This guide will help you connect the frontend to your Spring Boot microservices backend.

## 1. Configure Backend URLs

Edit `/lib/config.ts` and update the service URLs to match your backend deployment:

```typescript
export const API_CONFIG = {
  USER_SERVICE: 'http://localhost:8081/users',
  ADMIN_SERVICE: 'http://localhost:8082/admin',
  EVENT_SERVICE: 'http://localhost:8083/events',
  BOOKING_SERVICE: 'http://localhost:8084/bookings',
  PAYMENT_SERVICE: 'http://localhost:8085/payments',
  TICKET_SERVICE: 'http://localhost:8086/tickets',
};
```

### Common Deployment Patterns

#### Pattern 1: Separate Ports (Default)
Each microservice runs on a different port:
- User Service: `http://localhost:8081`
- Admin Service: `http://localhost:8082`
- Event Service: `http://localhost:8083`
- etc.

#### Pattern 2: API Gateway
All services behind a single gateway:
```typescript
USER_SERVICE: 'http://localhost:8080/user-service/users',
ADMIN_SERVICE: 'http://localhost:8080/admin-service/admin',
// etc.
```

#### Pattern 3: Production
```typescript
USER_SERVICE: 'https://api.yourdomain.com/users',
ADMIN_SERVICE: 'https://api.yourdomain.com/admin',
// etc.
```

## 2. Enable CORS on Backend

Each Spring Boot service needs CORS configuration. Add this to your controllers:

```java
@CrossOrigin(origins = "http://localhost:3000") // or your frontend URL
```

Or configure globally in each service:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("X-User-Role")
                .allowCredentials(true);
    }
}
```

## 3. Backend Service Ports

Make sure your Spring Boot services are running on these ports (or update config.ts):

```properties
# user-service/application.properties
server.port=8081

# admin-service/application.properties
server.port=8082

# event-service/application.properties
server.port=8083

# booking-service/application.properties
server.port=8084

# payment-service/application.properties
server.port=8085

# ticket-service/application.properties
server.port=8086
```

## 4. API Endpoints Mapping

The frontend expects these exact endpoints from your backend:

### User Service (Port 8081)
- `POST /users/signup` - User registration
- `POST /users/login` - User login
- `POST /users/{id}/host/apply` - Apply to become host
- `GET /users/admin/hosts/pending` - Get pending hosts (admin)
- `GET /users/{id}` - Get user details

### Admin Service (Port 8082)
- `POST /admin/signup` - Admin registration
- `POST /admin/login` - Admin login
- `GET /admin/hosts/pending` - Get pending host applications
- `PATCH /admin/hosts/{id}/approve` - Approve host
- `PATCH /admin/hosts/{id}/reject` - Reject host
- `DELETE /admin/events/{id}` - Delete event
- `GET /admin/stats` - Get booking statistics

### Event Service (Port 8083)
- `GET /events/allEvents` - Get all events
- `GET /events/{id}` - Get event by ID
- `POST /events` - Create event
- `DELETE /events/{id}` - Delete event
- `PATCH /events/{id}/tickets` - Update ticket availability

### Booking Service (Port 8084)
- `POST /bookings/book` - Create booking
- `GET /bookings/user/{userId}` - Get user bookings
- `PUT /bookings/{id}/confirm` - Confirm booking
- `PATCH /bookings/{id}/cancel` - Cancel booking
- `GET /bookings/admin/stats` - Get admin stats

### Payment Service (Port 8085)
- `POST /payments` - Initiate payment
- `POST /payments/confirm` - Confirm payment
- `GET /payments/booking/{bookingId}` - Get payment by booking
- `GET /payments/user/{userId}` - Get user payments
- `GET /payments` - Get all payments
- `GET /payments/{paymentId}` - Get payment by ID
- `GET /payments/simulate` - Simulate payment (dev/testing)

### Ticket Service (Port 8086)
- `POST /tickets/generate` - Generate ticket
- `GET /tickets/download/{bookingId}` - Download ticket PDF

## 5. Testing the Connection

### Start Backend Services
```bash
# Start each microservice
cd user-service && mvn spring-boot:run
cd admin-service && mvn spring-boot:run
cd event-service && mvn spring-boot:run
cd booking-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd ticket-service && mvn spring-boot:run
```

### Test API Endpoints
```bash
# Test event service
curl http://localhost:8083/events/allEvents

# Test user signup
curl -X POST http://localhost:8081/users/signup \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@example.com","password":"password123"}'
```

## 6. Common Issues

### CORS Errors
- Add `@CrossOrigin` annotation to controllers
- Or configure global CORS policy
- Ensure frontend URL is in allowed origins

### Connection Refused
- Check if backend service is running
- Verify port numbers match
- Check firewall settings

### 401/403 Errors
- Ensure X-User-Role header is being sent
- Check authentication logic in backend
- Verify user/admin is logged in on frontend

### JSON Parsing Errors
- Ensure DTOs match between frontend and backend
- Check request/response content types
- Verify UUID format (use strings in frontend, UUID in backend)

## 7. Development Workflow

1. Start all backend services
2. Start frontend: `npm run dev` (or equivalent)
3. Navigate to `http://localhost:3000` (or your frontend port)
4. Check browser console for API errors
5. Check backend logs for request errors

## 8. Production Deployment

For production, update `API_CONFIG` to use your production URLs:

```typescript
export const API_CONFIG = {
  USER_SERVICE: process.env.REACT_APP_USER_SERVICE || 'https://api.yourdomain.com/users',
  ADMIN_SERVICE: process.env.REACT_APP_ADMIN_SERVICE || 'https://api.yourdomain.com/admin',
  // etc.
};
```

Then set environment variables during build:
```bash
REACT_APP_USER_SERVICE=https://api.yourdomain.com/users npm run build
```
