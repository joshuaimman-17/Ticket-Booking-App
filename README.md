# 🎟️ Ticket Booking Platform (Web + App)

A full-stack **ticket booking platform** built with **Spring Boot Microservices**, **React**, **Supabase**, and **Eureka Server**.  
Users can browse and book event tickets, and verified users can host their own events.  
This project is designed as a **2-week MVP** for a **3-member team**, focusing on a simple, scalable architecture.

---

## 🌐 Project Overview

The platform allows:
- **Users** to explore, book, and manage event tickets.
- **Hosts** (verified users) to create and manage their own events.
- **Admins** to verify hosts, approve payouts, and monitor system activities.

The system uses **Supabase Auth** for authentication and stores files (tickets, ID proofs) in **Supabase Storage**.

---

## 🚀 Features

### 👥 User Features
- User registration and login using **Supabase Auth**
- View all upcoming events (excluding past/ended ones)
- Book event tickets (payment through PayPal)
- View **booking history** and download ticket PDFs
- Cancel bookings up to **5 hours before event start**
- Receive email/SMS notifications with OTP

### 🎤 Host Features
- Apply for host verification (submit ID and PayPal info)
- Once verified, create and manage events
- Set ticket types, prices, and total ticket count
- View history of hosted events
- View host rating based on user feedback

### 🛠️ Admin Features
- Approve or reject host verification requests
- View all users, events, and bookings
- Manage refunds and payouts to hosts after event completion
- Access simple reports (revenue, total events, top hosts)

### 💬 AI Chatbot (GEMI)
- Integrated chatbot assistant on the frontend
- Helps users find upcoming events using **public endpoints**
- Suggests events by category, date, or location
- Prompts login when user tries to book

---

## 🧱 Tech Stack

### Backend
- **Spring Boot Microservices**
- **Eureka Server** for service discovery
- **OpenFeign** for inter-service communication
- **Supabase** (Auth + Storage)
- **PayPal API** for payments

### Frontend
- **React.js** (Responsive Web + PWA-ready)
- **Tailwind CSS** for UI styling
- **GEMI Chatbot** for event guidance

### Dev Tools
- **Postman** – API testing
- **GitHub Projects** – Task tracking
- **Docker (optional)** – Containerized deployment

---

## 📦 Services Overview

- **User Service** – Handles registration, profiles, and role management  
- **Event Service** – Manages event creation and listings  
- **Booking Service** – Handles ticket booking, cancellation, and history  
- **Payment Service** – PayPal integration for ticket payments  
- **Notification/OTP Service** – Sends OTPs, confirmations, and reminders  
- **Ticket Service** – Generates downloadable PDF/QR code tickets  
- **Admin Service** – Manages verifications, refunds, and reports  
- **Gateway Service** – Routes frontend requests securely  
- **Chatbot Service (GEMI)** – Publicly fetches event data for users  

---

## 🧩 Core Highlights
- Users can view events **only upcoming (starting after 30 mins)**.
- Hosts and users can still view their **own past events/bookings**.
- Tickets are **held for 5 mins** during booking until payment confirmation.
- Cancellations allowed **up to 5 hours before event start**.
- Admin releases payouts **after event completion**.

---
