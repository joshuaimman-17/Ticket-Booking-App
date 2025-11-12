# Quick Start Guide - Connecting to Backend

## Step 1: Update Backend URLs

Edit `/lib/config.ts` and set your backend service URLs:

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

## Step 2: Enable CORS in Your Spring Boot Services

Add to each microservice controller or create a global config:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("X-User-Role")
                .allowCredentials(true);
    }
}
```

## Step 3: Verify Backend Services are Running

Check each service is accessible:

```bash
# Event Service
curl http://localhost:8083/events/allEvents

# User Service (should return 401 or error, but means it's running)
curl http://localhost:8081/users/signup

# Admin Service
curl http://localhost:8082/admin/login
```

## Step 4: Test the Connection

1. Start the frontend application
2. Open browser console (F12)
3. Navigate to Events page
4. Check Network tab for API calls
5. Look for successful responses from your backend

## Common Port Configurations

### Default Setup (Separate Ports)
```
User Service:     http://localhost:8081
Admin Service:    http://localhost:8082
Event Service:    http://localhost:8083
Booking Service:  http://localhost:8084
Payment Service:  http://localhost:8085
Ticket Service:   http://localhost:8086
```

### API Gateway Setup
```
All Services:     http://localhost:8080/[service-name]/[endpoint]
```

Update `config.ts` accordingly:
```typescript
USER_SERVICE: 'http://localhost:8080/user-service/users',
ADMIN_SERVICE: 'http://localhost:8080/admin-service/admin',
// etc.
```

## Testing Features

### 1. Browse Events
- Go to `/events`
- Should fetch from `GET http://localhost:8083/events/allEvents`

### 2. User Signup/Login
- Go to `/signup`
- Create account
- Posts to `POST http://localhost:8081/users/signup`

### 3. Admin Login
- Go to `/login`
- Switch to "Admin Login" tab
- Posts to `POST http://localhost:8082/admin/login`

### 4. Create Event (as Host)
- Login as user
- Apply to be host in dashboard
- Admin approves host
- Create event from host dashboard
- Posts to `POST http://localhost:8083/events`

### 5. Book Ticket
- Browse events
- Click "Book Now"
- Complete booking
- Creates booking: `POST http://localhost:8084/bookings/book`
- Creates payment: `POST http://localhost:8085/payments`

## Troubleshooting

### CORS Error
```
Access to fetch at 'http://localhost:8083/events/allEvents' from origin 'http://localhost:3000' 
has been blocked by CORS policy
```
**Solution:** Add CORS configuration to your Spring Boot services (see Step 2)

### Connection Refused
```
Failed to fetch
net::ERR_CONNECTION_REFUSED
```
**Solution:** 
- Check backend service is running
- Verify port number matches in `config.ts`
- Check firewall settings

### 404 Not Found
```
GET http://localhost:8083/events/allEvents 404 (Not Found)
```
**Solution:**
- Verify controller mapping in backend
- Check service is running on correct port
- Ensure endpoint path matches exactly

### 401 Unauthorized
```
GET http://localhost:8082/admin/hosts/pending 401 (Unauthorized)
```
**Solution:**
- Login first to get authentication
- Check X-User-Role header is being sent
- Verify backend authentication logic

## Next Steps

Once connected:
1. Test user signup and login
2. Create admin account
3. Browse and search events
4. Create bookings
5. Test payment with coupon codes (FREE100, DEVTEST, NEWUSER10)
6. Test host application workflow
7. Test admin approval process

## Support

For detailed backend setup, see `BACKEND_SETUP.md`
