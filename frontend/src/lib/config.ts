// Backend API Configuration
// Update these URLs to match your microservices deployment

export const API_CONFIG = {
  // Local Development (default)
  //USER_SERVICE: 'http://localhost:8081/users',
 // ADMIN_SERVICE: 'http://localhost:8082/admin',
 // EVENT_SERVICE: 'http://localhost:8083/events',
  //BOOKING_SERVICE: 'http://localhost:8084/bookings',
  //PAYMENT_SERVICE: 'http://localhost:8085/payments',
 // TICKET_SERVICE: 'http://localhost:8086/tickets',

  // Alternative: If all services are on the same host with different paths
   USER_SERVICE: 'http://localhost:8080/user-service',
   ADMIN_SERVICE: 'http://localhost:8080/admin-service',
   EVENT_SERVICE: 'http://localhost:8080/event-service',
   BOOKING_SERVICE: 'http://localhost:8080/booking-service',
   PAYMENT_SERVICE: 'http://localhost:8080/payment-service',
   TICKET_SERVICE: 'http://localhost:8080/ticket-service',

  // Production (example)
  // USER_SERVICE: 'https://api.ticketbooking.com/users',
   //ADMIN_SERVICE: 'https://api.ticketbooking.com/admin',
  // EVENT_SERVICE: 'https://api.ticketbooking.com/events',
  // BOOKING_SERVICE: 'https://api.ticketbooking.com/bookings',
  // PAYMENT_SERVICE: 'https://api.ticketbooking.com/payments',
  // TICKET_SERVICE: 'https://api.ticketbooking.com/tickets',
};

// CORS Configuration Helper
export const CORS_CONFIG = {
  mode: 'cors' as RequestMode,
  credentials: 'include' as RequestCredentials,
};
