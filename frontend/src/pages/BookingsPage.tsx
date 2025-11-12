import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Booking, Event } from '../types';
import { bookingApi, eventApi, ticketApi } from '../lib/api';
import { AuthService } from '../lib/auth';
import { Card, CardContent, CardHeader } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import { Calendar, MapPin, Ticket, Download, XCircle, Loader2 } from 'lucide-react';
import { toast } from 'sonner';

export function BookingsPage() {
  const navigate = useNavigate();
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [events, setEvents] = useState<Record<string, Event>>({});
  const [loading, setLoading] = useState(true);

  // State to track which bookings have generated tickets
  const [generatedTickets, setGeneratedTickets] = useState<Record<string, boolean>>({});
  // State to track processing per booking
  const [processingTickets, setProcessingTickets] = useState<Record<string, boolean>>({});

  useEffect(() => {
    const user = AuthService.getUser();
    if (!user) {
      navigate('/login');
      return;
    }
    loadBookings(user.id);
  }, [navigate]);

  const loadBookings = async (userId: string) => {
    try {
      const bookingsData = await bookingApi.getUserBookings(userId);
      setBookings(bookingsData);

      const eventPromises = bookingsData.map((booking) =>
        eventApi.getEvent(booking.eventId)
      );
      const eventsData = await Promise.all(eventPromises);

      const eventsMap: Record<string, Event> = {};
      eventsData.forEach((event) => {
        if (event) {
          eventsMap[event.eventId] = event;
        }
      });
      setEvents(eventsMap);

      // Initialize generatedTickets state for existing bookings
      const initialGenerated: Record<string, boolean> = {};
      bookingsData.forEach((b) => {
        initialGenerated[b.bookingId] = false;
      });
      setGeneratedTickets(initialGenerated);

    } catch (error) {
      console.error('Failed to load bookings:', error);
      toast.error('Failed to load bookings');
    } finally {
      setLoading(false);
    }
  };

  const handleCancelBooking = async (bookingId: string) => {
    if (!confirm('Are you sure you want to cancel this booking?')) return;

    try {
      await bookingApi.cancelBooking(bookingId);
      toast.success('Booking cancelled successfully');
      const user = AuthService.getUser();
      if (user) loadBookings(user.id);
    } catch (error) {
      toast.error('Failed to cancel booking');
    }
  };

  // Generate Ticket
  const handleGenerateTicket = async (bookingId: string, userId: string, eventId: string) => {
    setProcessingTickets((prev) => ({ ...prev, [bookingId]: true }));
    try {
      await ticketApi.generateTicket(bookingId, userId, eventId);
      toast.success('Ticket generated successfully!');
      setGeneratedTickets((prev) => ({ ...prev, [bookingId]: true }));
    } catch (error) {
      console.error('Ticket generation failed:', error);
      toast.error('Failed to generate ticket.');
    } finally {
      setProcessingTickets((prev) => ({ ...prev, [bookingId]: false }));
    }
  };

  // Download Ticket
  const handleDownloadTicket = async (bookingId: string) => {
    try {
      toast.info('Preparing your ticket...');
      const blob = await ticketApi.downloadTicket(bookingId);
      const url = window.URL.createObjectURL(blob);

      const link = document.createElement('a');
      link.href = url;
      link.download = `ticket_${bookingId}.pdf`;
      document.body.appendChild(link);
      link.click();
      link.remove();

      window.URL.revokeObjectURL(url);
      toast.success('🎟️ Ticket downloaded successfully!');
    } catch (error) {
      console.error('Download failed:', error);
      toast.error('Failed to download ticket.');
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-500">Loading bookings...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        <div className="mb-8">
          <h1 className="text-3xl mb-2">My Bookings</h1>
          <p className="text-gray-600">View and manage your event bookings</p>
        </div>

        {bookings.length === 0 ? (
          <Card>
            <CardContent className="flex flex-col items-center justify-center py-12">
              <Ticket className="h-16 w-16 text-gray-300 mb-4" />
              <h3 className="text-xl mb-2">No bookings yet</h3>
              <p className="text-gray-600 mb-4">Start exploring events and book your tickets!</p>
              <Button onClick={() => navigate('/events')}>Browse Events</Button>
            </CardContent>
          </Card>
        ) : (
          <div className="space-y-4">
            {bookings.map((booking) => {
              const event = events[booking.eventId];
              if (!event) return null;

              const eventDate = new Date(event.eventDate);
              const totalAmount = event.ticketPrice * booking.quantity;

              const isGenerated = generatedTickets[booking.bookingId];
              const isProcessing = processingTickets[booking.bookingId];

              return (
                <Card key={booking.bookingId}>
                  <CardHeader>
                    <div className="flex items-start justify-between">
                      <div>
                        <h3 className="text-xl mb-1">{event.title}</h3>
                        <Badge
                          variant={
                            booking.status === 'CONFIRMED'
                              ? 'default'
                              : booking.status === 'CANCELLED'
                              ? 'destructive'
                              : 'secondary'
                          }
                        >
                          {booking.status}
                        </Badge>
                      </div>
                      <Badge variant="outline">{event.eventType}</Badge>
                    </div>
                  </CardHeader>

                  <CardContent>
                    <div className="grid md:grid-cols-2 gap-6">
                      <div className="space-y-3">
                        <div className="flex items-start gap-2">
                          <Calendar className="h-4 w-4 text-gray-400 mt-1" />
                          <div>
                            <p className="text-sm text-gray-500">Date & Time</p>
                            <p>
                              {eventDate.toLocaleDateString('en-US', {
                                weekday: 'short',
                                year: 'numeric',
                                month: 'short',
                                day: 'numeric',
                              })}
                            </p>
                            <p className="text-sm text-gray-600">
                              {eventDate.toLocaleTimeString('en-US', {
                                hour: '2-digit',
                                minute: '2-digit',
                              })}
                            </p>
                          </div>
                        </div>

                        <div className="flex items-start gap-2">
                          <MapPin className="h-4 w-4 text-gray-400 mt-1" />
                          <div>
                            <p className="text-sm text-gray-500">Location</p>
                            <p>{event.location}</p>
                          </div>
                        </div>
                      </div>

                      <div className="space-y-3">
                        <div className="flex items-start gap-2">
                          <Ticket className="h-4 w-4 text-gray-400 mt-1" />
                          <div>
                            <p className="text-sm text-gray-500">Tickets</p>
                            <p>
                              {booking.quantity} × ₹{event.ticketPrice.toFixed(2)}
                            </p>
                          </div>
                        </div>

                        <div className="p-3 bg-blue-50 rounded-lg">
                          <p className="text-sm text-gray-600">Total Amount</p>
                          <p className="text-2xl">₹{totalAmount.toFixed(2)}</p>
                        </div>
                      </div>
                    </div>

                    <div className="flex gap-2 mt-6">
                      {booking.status === 'CONFIRMED' && (
                        <Button
                          variant="outline"
                          className="gap-2"
                          onClick={() =>
                            isGenerated
                              ? handleDownloadTicket(booking.bookingId)
                              : handleGenerateTicket(
                                  booking.bookingId,
                                  AuthService.getUser()!.id,
                                  booking.eventId
                                )
                          }
                          disabled={isProcessing}
                        >
                          {isProcessing ? (
                            <Loader2 className="h-4 w-4 animate-spin" />
                          ) : isGenerated ? (
                            <>
                              <Download className="h-4 w-4" />
                              Download Ticket
                            </>
                          ) : (
                            <>
                              <Ticket className="h-4 w-4" />
                              Generate Ticket
                            </>
                          )}
                        </Button>
                      )}

                      {booking.status === 'PENDING' && (
                        <Button
                          variant="destructive"
                          className="gap-2"
                          onClick={() => handleCancelBooking(booking.bookingId)}
                        >
                          <XCircle className="h-4 w-4" />
                          Cancel Booking
                        </Button>
                      )}
                    </div>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
