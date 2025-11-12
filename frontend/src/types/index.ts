export interface User {
  id: string;
  name: string;
  email: string;
  role: 'USER' | 'HOST' | 'ADMIN';
  isVerified?: boolean;
  verified?: boolean;
  idProofUrl?: string;
  paypalEmail?: string;
}

export interface Event {
  eventId: string;
  title: string;
  description: string;
  location: string;
  eventDate: string;
  eventType: string;
  status: string;
  totalTickets: number;
  ticketPrice: number;
  ticketsSold: number;
}

export interface Booking {
  bookingId: string;
  userId: string;
  eventId: string;
  ticketType: string;
  quantity: number;
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED';
  holdExpiry?: string;
  paymentStatus?: string;
}

export interface Payment {
  id: string;
  bookingId: string;
  userId: string;
  amount: number;
  currency: string;
  status: 'PENDING' | 'SUCCESS' | 'FAILED';
  upiId?: string;
  couponCode?: string;
  providerPaymentId?: string;
}

export interface Admin {
  id: string;
  email: string;
  name: string;
  role: 'ADMIN';
}
