# Backend Integration Testing Checklist

Use this checklist to verify your frontend is properly connected to the backend.

## Prerequisites
- [ ] All backend microservices are running
- [ ] CORS is enabled on all backend services
- [ ] Database is set up and accessible
- [ ] `/lib/config.ts` has correct backend URLs

## 1. Connection Test

Open browser console and run:
```javascript
window.testBackend()
```

Expected output:
```
Testing backend services...
User Service: ✅ Online (http://localhost:8081/users)
Admin Service: ✅ Online (http://localhost:8082/admin)
Event Service: ✅ Online (http://localhost:8083/events)
Booking Service: ✅ Online (http://localhost:8084/bookings)
Payment Service: ✅ Online (http://localhost:8085/payments)
Ticket Service: ✅ Online (http://localhost:8086/tickets)
```

## 2. Event Service Tests

### Browse Events
- [ ] Navigate to `/events`
- [ ] Events list loads successfully
- [ ] Can search events
- [ ] Can filter by category
- [ ] Network tab shows: `GET /events/allEvents` → 200 OK

### View Event Details
- [ ] Click on an event
- [ ] Event details page loads
- [ ] Images display correctly
- [ ] Ticket information shows
- [ ] Network tab shows: `GET /events/{id}` → 200 OK

## 3. User Service Tests

### User Signup
- [ ] Navigate to `/signup`
- [ ] Fill in signup form (user tab)
- [ ] Submit form
- [ ] Success message appears
- [ ] Redirected to dashboard
- [ ] Network tab shows: `POST /users/signup` → 201 Created
- [ ] LocalStorage has `user` key

### User Login
- [ ] Navigate to `/login`
- [ ] Use user credentials
- [ ] Submit form
- [ ] Success message appears
- [ ] Redirected appropriately
- [ ] Network tab shows: `POST /users/login` → 200 OK

### Apply to Become Host
- [ ] Login as user
- [ ] Navigate to `/dashboard`
- [ ] Click "Apply to Become a Host"
- [ ] Fill in ID proof URL and PayPal email
- [ ] Submit application
- [ ] Success message appears
- [ ] Network tab shows: `POST /users/{id}/host/apply` → 200 OK

## 4. Admin Service Tests

### Admin Signup
- [ ] Navigate to `/signup`
- [ ] Switch to "Admin Signup" tab
- [ ] Fill in admin details
- [ ] Submit form
- [ ] Success message appears
- [ ] Redirected to admin dashboard
- [ ] Network tab shows: `POST /admin/signup` → 200/201 OK

### Admin Login
- [ ] Navigate to `/login`
- [ ] Switch to "Admin Login" tab
- [ ] Use admin credentials
- [ ] Submit form
- [ ] Redirected to `/admin`
- [ ] Network tab shows: `POST /admin/login` → 200 OK

### Approve Host Application
- [ ] Login as admin
- [ ] Navigate to `/admin`
- [ ] See pending hosts list
- [ ] Click "Approve" on a host
- [ ] Success message appears
- [ ] Host removed from pending list
- [ ] Network tab shows: `PATCH /admin/hosts/{id}/approve` → 200 OK

### Reject Host Application
- [ ] Click "Reject" on a pending host
- [ ] Success message appears
- [ ] Host removed from list
- [ ] Network tab shows: `PATCH /admin/hosts/{id}/reject` → 200 OK

### Delete Event
- [ ] Switch to "All Events" tab
- [ ] Click "Delete" on an event
- [ ] Confirm deletion
- [ ] Event removed from list
- [ ] Network tab shows: `DELETE /admin/events/{id}` → 200/204 OK

### View Statistics
- [ ] Dashboard loads stats cards
- [ ] See total bookings, revenue, etc.
- [ ] Network tab shows: `GET /admin/stats` → 200 OK

## 5. Host Service Tests

### Create Event
- [ ] Login as approved host
- [ ] Navigate to `/host`
- [ ] Click "Create Event"
- [ ] Fill in all event details:
  - Title
  - Description
  - Location
  - Date & Time
  - Event Type
  - Total Tickets
  - Ticket Price
- [ ] Submit form
- [ ] Success message appears
- [ ] Event appears in list
- [ ] Network tab shows: `POST /events` → 201 Created

### View Host Dashboard
- [ ] Stats cards display correctly
- [ ] Total events count accurate
- [ ] Tickets sold count accurate
- [ ] Revenue calculated correctly
- [ ] Event list shows all host events

## 6. Booking Service Tests

### Create Booking
- [ ] Login as user
- [ ] Navigate to event details
- [ ] Select quantity
- [ ] Click "Book Now"
- [ ] Fill in booking details
- [ ] Submit booking
- [ ] Network tab shows: `POST /bookings/book` → 201 Created
- [ ] Redirected to bookings page

### View User Bookings
- [ ] Navigate to `/bookings`
- [ ] See list of bookings
- [ ] Booking details display correctly
- [ ] Status badges show correctly
- [ ] Network tab shows: `GET /bookings/user/{userId}` → 200 OK

### Cancel Booking
- [ ] Click "Cancel Booking" on pending booking
- [ ] Confirm cancellation
- [ ] Success message appears
- [ ] Booking status changes to CANCELLED
- [ ] Network tab shows: `PATCH /bookings/{id}/cancel` → 200 OK

## 7. Payment Service Tests

### Initiate Payment
- [ ] During booking flow
- [ ] Enter UPI ID (optional)
- [ ] Enter coupon code (optional)
- [ ] Submit payment
- [ ] Network tab shows: `POST /payments` → 200/201 OK

### Test Coupon Codes
- [ ] Use coupon "FREE100"
- [ ] Payment status becomes SUCCESS immediately
- [ ] Booking confirmed without payment gateway
- [ ] Use coupon "DEVTEST"
- [ ] Same as FREE100
- [ ] Use coupon "NEWUSER10"
- [ ] 10% discount applied
- [ ] Payment still requires processing

### Simulate Payment (Development)
- [ ] Note the paymentRecordId from payment response
- [ ] Call simulate endpoint (if exposed)
- [ ] Payment status updates to SUCCESS
- [ ] Booking confirmed

## 8. Ticket Service Tests

### Generate Ticket
- [ ] Complete a successful booking
- [ ] Ticket should be auto-generated
- [ ] Network tab shows: `POST /tickets/generate` → 200 OK

### Download Ticket
- [ ] Click "Download Tickets" on confirmed booking
- [ ] PDF download starts
- [ ] Network tab shows: `GET /tickets/download/{bookingId}` → 200 OK
- [ ] PDF opens/downloads correctly

## 9. Integration Flow Tests

### Complete Booking Flow
1. [ ] User signup
2. [ ] Browse events
3. [ ] Select event
4. [ ] Book tickets
5. [ ] Process payment
6. [ ] View booking in "My Bookings"
7. [ ] Download ticket

### Complete Host Flow
1. [ ] User signup
2. [ ] Apply to become host
3. [ ] Admin approves application
4. [ ] Host creates event
5. [ ] Event appears in browse
6. [ ] User books tickets for event
7. [ ] Host sees sales in dashboard

### Complete Admin Flow
1. [ ] Admin login
2. [ ] View pending hosts
3. [ ] Approve/reject hosts
4. [ ] View all events
5. [ ] Delete inappropriate event
6. [ ] View platform statistics

## 10. Error Handling Tests

### Network Errors
- [ ] Stop a backend service
- [ ] Try to use that feature
- [ ] Appropriate error message shows
- [ ] No app crash

### Invalid Data
- [ ] Submit form with invalid email
- [ ] Submit form with empty required fields
- [ ] Try booking more tickets than available
- [ ] Appropriate validation messages show

### Authentication Errors
- [ ] Try accessing admin page without login
- [ ] Try accessing host features as regular user
- [ ] Appropriate access denied messages

## 11. CORS Verification

- [ ] No CORS errors in browser console
- [ ] All API requests complete successfully
- [ ] Headers include X-User-Role when needed
- [ ] Credentials sent when required

## Common Issues & Solutions

### Issue: Events not loading
**Check:**
- Event service running?
- CORS enabled on event service?
- Correct URL in config.ts?
- Browser console for errors

### Issue: Login fails
**Check:**
- User service running?
- Correct credentials?
- Check backend logs
- CORS enabled?

### Issue: Booking fails
**Check:**
- All services running?
- Valid user ID?
- Valid event ID?
- Sufficient tickets available?

### Issue: Admin features not working
**Check:**
- Logged in as admin?
- X-User-Role header sent?
- Admin service running?
- Check isAdmin logic in backend

## Success Criteria

✅ All services responding
✅ CORS properly configured
✅ User can signup/login
✅ Events load and display
✅ Bookings can be created
✅ Payments process correctly
✅ Admin can manage system
✅ Hosts can create events
✅ No console errors
✅ Proper error messages shown

## Next Steps After Testing

1. Test with real payment gateway (if applicable)
2. Load testing with multiple users
3. Security testing
4. Performance optimization
5. Production deployment
