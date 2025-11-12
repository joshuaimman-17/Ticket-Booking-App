// API Utility Functions for Error Handling and Debugging

export class ApiError extends Error {
  constructor(
    message: string,
    public status?: number,
    public data?: any
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

// Enhanced response handler with detailed error messages
export async function handleApiResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    let errorMessage = `Request failed with status ${response.status}`;
    let errorData: any = null;

    try {
      errorData = await response.json();
      errorMessage = errorData.error || errorData.message || errorMessage;
    } catch {
      errorMessage = await response.text().catch(() => errorMessage);
    }

    console.error('API Error:', {
      status: response.status,
      url: response.url,
      message: errorMessage,
      data: errorData,
    });

    throw new ApiError(errorMessage, response.status, errorData);
  }

  try {
    return await response.json();
  } catch (error) {
    console.error('JSON Parse Error:', error);
    throw new ApiError('Invalid JSON response from server');
  }
}

// Log API requests in development
export function logApiRequest(method: string, url: string, data?: any) {
  if (process.env.NODE_ENV === 'development') {
    console.log(`[API ${method}]`, url, data || '');
  }
}

// Check if backend is reachable
export async function checkBackendHealth(serviceUrl: string): Promise<boolean> {
  try {
    const response = await fetch(serviceUrl, {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
    });
    return response.status !== 0; // Any response means service is reachable
  } catch (error) {
    console.error(`Backend health check failed for ${serviceUrl}:`, error);
    return false;
  }
}

// Retry failed requests
export async function retryRequest<T>(
  requestFn: () => Promise<T>,
  maxRetries: number = 3,
  delay: number = 1000
): Promise<T> {
  let lastError: Error;

  for (let i = 0; i < maxRetries; i++) {
    try {
      return await requestFn();
    } catch (error) {
      lastError = error as Error;
      console.warn(`Request failed (attempt ${i + 1}/${maxRetries}):`, error);

      if (i < maxRetries - 1) {
        await new Promise((resolve) => setTimeout(resolve, delay));
      }
    }
  }

  throw lastError!;
}

// Format UUIDs (backend uses UUID, frontend uses string)
export function formatUUID(id: string): string {
  // Remove dashes and ensure proper UUID format if needed
  return id.replace(/-/g, '');
}

// Parse backend date strings to JS Date
export function parseBackendDate(dateString: string): Date {
  return new Date(dateString);
}

// Format date for backend (LocalDateTime format)
export function formatDateForBackend(date: Date): string {
  return date.toISOString().slice(0, 19); // yyyy-MM-ddTHH:mm:ss
}

// Debug helper to test all services
export async function testAllServices() {
  const { API_CONFIG } = await import('./config');

  const services = [
    { name: 'User Service', url: API_CONFIG.USER_SERVICE },
    { name: 'Admin Service', url: API_CONFIG.ADMIN_SERVICE },
    { name: 'Event Service', url: API_CONFIG.EVENT_SERVICE },
    { name: 'Booking Service', url: API_CONFIG.BOOKING_SERVICE },
    { name: 'Payment Service', url: API_CONFIG.PAYMENT_SERVICE },
    { name: 'Ticket Service', url: API_CONFIG.TICKET_SERVICE },
  ];

  console.log('Testing backend services...');

  for (const service of services) {
    const isHealthy = await checkBackendHealth(service.url);
    console.log(
      `${service.name}: ${isHealthy ? '✅ Online' : '❌ Offline'} (${service.url})`
    );
  }
}

// Add to window for easy debugging in console
if (typeof window !== 'undefined') {
  (window as any).testBackend = testAllServices;
}
