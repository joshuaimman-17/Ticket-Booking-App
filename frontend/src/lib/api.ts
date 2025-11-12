import { User, Event, Booking, Payment, Admin } from '../types';
import { API_CONFIG } from './config';

// =======================================
// API Base URLs (Gateway Paths)
// =======================================
const USER_SERVICE = `${API_CONFIG.USER_SERVICE}/users`;
const ADMIN_SERVICE = `${API_CONFIG.ADMIN_SERVICE}/admin`;
const EVENT_SERVICE = `${API_CONFIG.EVENT_SERVICE}/events`;
const BOOKING_SERVICE = `${API_CONFIG.BOOKING_SERVICE}/bookings`;
const PAYMENT_SERVICE = `${API_CONFIG.PAYMENT_SERVICE}/payments`;
const TICKET_SERVICE = `${API_CONFIG.TICKET_SERVICE}/tickets`;

// =======================================
// Helper: Auth Headers
// =======================================
const getAuthHeaders = (isBinary = false) => {
  const admin = localStorage.getItem('admin');
  const user = localStorage.getItem('user');

  const headers: { [key: string]: string } = {};

  // Only include JSON header for non-binary requests
  if (!isBinary) headers['Content-Type'] = 'application/json';

  if (admin) {
    headers['X-User-Role'] = 'ADMIN';
  } else if (user) {
    const userData = JSON.parse(user);
    headers['X-User-Role'] = userData.role || 'USER';
  }

  return headers;
};

// =======================================
// Helper: Handle API responses
// =======================================
async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const error = await response.json().catch(() => ({ error: 'Request failed' }));
    throw new Error(error.error || error.message || 'Request failed');
  }
  return response.json();
}

// =======================================
// USER SERVICE
// =======================================
export const userApi = {
  signup: async (data: { name: string; email: string; password: string }): Promise<User> => {
    const response = await fetch(`${USER_SERVICE}/signup`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });
    return handleResponse<User>(response);
  },

  login: async (email: string, password: string): Promise<User> => {
    const response = await fetch(`${USER_SERVICE}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });

    if (!response.ok) throw new Error('Invalid credentials');
    const data = await response.json();

    return {
      id: data.userId,
      name: data.name,
      email: data.email,
      role: data.role,
      isVerified: true,
    };
  },

  applyHost: async (userId: string, data: { idProofUrl: string; paypalEmail: string }) => {
    const response = await fetch(`${USER_SERVICE}/${userId}/host/apply`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(data),
    });
    return handleResponse(response);
  },

  getPendingHosts: async (): Promise<User[]> => {
    const response = await fetch(`${USER_SERVICE}/admin/hosts/pending`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });
    return handleResponse<User[]>(response);
  },

  getUser: async (userId: string): Promise<User> => {
    const response = await fetch(`${USER_SERVICE}/${userId}`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });
    return handleResponse<User>(response);
  },
};

// =======================================
// ADMIN SERVICE
// =======================================
export const adminApi = {
  login: async (email: string, password: string): Promise<Admin> => {
    const response = await fetch(`${ADMIN_SERVICE}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });

    if (!response.ok) throw new Error('Invalid admin credentials');
    const data = await response.json();

    return {
      id: data.id,
      email: data.email,
      name: data.name,
      role: 'ADMIN',
    };
  },

  signup: async (data: { name: string; email: string; password: string }): Promise<Admin> => {
    const response = await fetch(`${ADMIN_SERVICE}/signup`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      const error = await response.json().catch(() => ({ error: 'Signup failed' }));
      throw new Error(error.error || 'Failed to create admin account');
    }

    const result = await response.json();
    return {
      id: result.id,
      email: result.email,
      name: result.name,
      role: 'ADMIN',
    };
  },

  approveHost: async (userId: string) => {
    const response = await fetch(`${ADMIN_SERVICE}/hosts/${userId}/approve`, {
      method: 'PATCH',
      headers: getAuthHeaders(),
    });
    return handleResponse(response);
  },

  rejectHost: async (userId: string) => {
    const response = await fetch(`${ADMIN_SERVICE}/hosts/${userId}/reject`, {
      method: 'PATCH',
      headers: getAuthHeaders(),
    });
    return handleResponse(response);
  },

  getPendingHosts: async (): Promise<User[]> => {
    const response = await fetch(`${ADMIN_SERVICE}/hosts/pending`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });
    return handleResponse<User[]>(response);
  },

  // ✅ NEW: Fetch approved hosts
  getApprovedHosts: async (): Promise<User[]> => {
    const response = await fetch(`${ADMIN_SERVICE}/hosts/approved`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });
    return handleResponse<User[]>(response);
  },

  deleteEvent: async (eventId: string) => {
    const response = await fetch(`${ADMIN_SERVICE}/events/${eventId}`, {
      method: 'DELETE',
      headers: getAuthHeaders(),
    });
    return handleResponse(response);
  },

  getStats: async () => {
    const response = await fetch(`${ADMIN_SERVICE}/stats`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });
    return handleResponse(response);
  },
};

// =======================================
// EVENT SERVICE
// =======================================
export const eventApi = {
  getAllEvents: async (): Promise<Event[]> => {
    const response = await fetch(`${EVENT_SERVICE}/allEvents`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
    });
    return handleResponse<Event[]>(response);
  },

  getEvent: async (eventId: string): Promise<Event | undefined> => {
    const response = await fetch(`${EVENT_SERVICE}/${eventId}`, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
    });
    if (response.status === 404) return undefined;
    return handleResponse<Event>(response);
  },

  createEvent: async (
    userId: string,
    event: Omit<Event, 'eventId' | 'ticketsSold'>
  ): Promise<Event> => {
    const response = await fetch(`${EVENT_SERVICE}/create/${userId}`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(event),
    });
    return handleResponse<Event>(response);
  },

  deleteEvent: async (eventId: string) => {
    const response = await fetch(`${EVENT_SERVICE}/${eventId}`, {
      method: 'DELETE',
      headers: getAuthHeaders(),
    });
    if (!response.ok) throw new Error('Failed to delete event');
    return { success: true };
  },

  updateTicketAvailability: async (eventId: string, ticketsSold: number): Promise<Event> => {
    const response = await fetch(
      `${EVENT_SERVICE}/${eventId}/tickets?ticketsSold=${ticketsSold}`,
      {
        method: 'PATCH',
        headers: getAuthHeaders(),
      }
    );
    return handleResponse<Event>(response);
  },
};

// =======================================
// BOOKING SERVICE
// =======================================
export const bookingApi = {
  createBooking: async (data: {
    userId: string;
    eventId: string;
    ticketType: string;
    quantity: number;
  }): Promise<Booking> => {
    const response = await fetch(`${BOOKING_SERVICE}/book`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(data),
    });
    return handleResponse<Booking>(response);
  },

  getUserBookings: async (userId: string): Promise<Booking[]> => {
    const response = await fetch(`${BOOKING_SERVICE}/user/${userId}`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });
    return handleResponse<Booking[]>(response);
  },

  confirmBooking: async (bookingId: string, paymentId: string): Promise<Booking> => {
    const response = await fetch(
      `${BOOKING_SERVICE}/${bookingId}/confirm?paymentId=${paymentId}`,
      { method: 'PUT', headers: getAuthHeaders() }
    );
    return handleResponse<Booking>(response);
  },

  cancelBooking: async (bookingId: string): Promise<Booking> => {
    const response = await fetch(`${BOOKING_SERVICE}/${bookingId}/cancel`, {
      method: 'PATCH',
      headers: getAuthHeaders(),
    });
    return handleResponse<Booking>(response);
  },

  getAdminStats: async () => {
    const response = await fetch(`${BOOKING_SERVICE}/admin/stats`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });
    return handleResponse(response);
  },
};

// =======================================
// PAYMENT SERVICE
// =======================================
export const paymentApi = {
  initiatePayment: async (data: {
    bookingId: string;
    userId: string;
    amount: number;
    upiId?: string;
    couponCode?: string;
  }) => {
    const response = await fetch(`${PAYMENT_SERVICE}`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(data),
    });
    return handleResponse(response);
  },

  confirmPayment: async (paymentRecordId: string, providerPaymentId: string, status: string) => {
    const response = await fetch(`${PAYMENT_SERVICE}/confirm`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({ paymentRecordId, providerPaymentId, status }),
    });
    return handleResponse(response);
  },

  getPaymentByBooking: async (bookingId: string) => {
    const response = await fetch(`${PAYMENT_SERVICE}/booking/${bookingId}`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });
    if (response.status === 404) return null;
    return handleResponse(response);
  },

  getUserPayments: async (userId: string): Promise<Payment[]> => {
    const response = await fetch(`${PAYMENT_SERVICE}/user/${userId}`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });
    return handleResponse<Payment[]>(response);
  },

  getAllPayments: async (): Promise<Payment[]> => {
    const response = await fetch(`${PAYMENT_SERVICE}`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });
    return handleResponse<Payment[]>(response);
  },

  getPaymentById: async (paymentId: string) => {
    const response = await fetch(`${PAYMENT_SERVICE}/${paymentId}`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });
    if (response.status === 404) return null;
    return handleResponse(response);
  },

  simulatePayment: async (paymentRecordId: string, status: string = 'SUCCESS') => {
    const response = await fetch(
      `${PAYMENT_SERVICE}/simulate?paymentRecordId=${paymentRecordId}&status=${status}`,
      { method: 'GET', headers: getAuthHeaders() }
    );
    return handleResponse(response);
  },
};
// =======================================
// TICKET SERVICE (Final Fixed Version)
// =======================================
export const ticketApi = {
  generateTicket: async (bookingId: string, userId: string, eventId: string) => {
    const response = await fetch(`${API_CONFIG.TICKET_SERVICE}/tickets/generate`, {
      method: 'POST',
      headers: { ...getAuthHeaders(), 'Content-Type': 'application/json' },
      body: JSON.stringify({ bookingId, userId, eventId }),
    });
    return handleResponse(response);
  },

  downloadTicket: async (bookingId: string): Promise<Blob> => {
    const response = await fetch(`${API_CONFIG.TICKET_SERVICE}/tickets/download/${bookingId}`, {
      method: 'GET',
      headers: { 'X-User-Role': 'USER' },
    });
    if (!response.ok) throw new Error(`Failed to download ticket: ${response.status}`);
    return response.blob();
  },
};

