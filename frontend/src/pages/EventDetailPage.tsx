import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Event } from '../types';
import { eventApi, bookingApi, paymentApi } from '../lib/api';
import { AuthService } from '../lib/auth';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardHeader } from '../components/ui/card';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Badge } from '../components/ui/badge';
import { Separator } from '../components/ui/separator';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '../components/ui/dialog';
import { Calendar, MapPin, Users, DollarSign, Ticket, ArrowLeft } from 'lucide-react';
import { toast } from 'sonner';
import { ImageWithFallback } from '../components/figma/ImageWithFallback';

const eventImages: Record<string, string> = {
  Music: 'https://images.unsplash.com/photo-1656283384093-1e227e621fad?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxtdXNpYyUyMGNvbmNlcnQlMjBjcm93ZHxlbnwxfHx8fDE3NjIxOTUzOTB8MA&ixlib=rb-4.1.0&q=80&w=1080',
  Conference: 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxidXNpbmVzcyUyMGNvbmZlcmVuY2V8ZW58MXx8fHwxNzYyMjU1NjUyfDA&ixlib=rb-4.1.0&q=80&w=1080',
  Comedy: 'https://images.unsplash.com/photo-1758726942669-92f7e499018c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjb21lZHklMjB0aGVhdGVyfGVufDF8fHx8MTc2MjE4NTEyNHww&ixlib=rb-4.1.0&q=80&w=1080',
  Art: 'https://images.unsplash.com/photo-1713779490284-a81ff6a8ffae?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxhcnQlMjBnYWxsZXJ5JTIwZXhoaWJpdGlvbnxlbnwxfHx8fDE3NjIyNTQ4Nzl8MA&ixlib=rb-4.1.0&q=80&w=1080',
};

export function EventDetailPage() {
  const { eventId } = useParams<{ eventId: string }>();
  const navigate = useNavigate();
  const [event, setEvent] = useState<Event | null>(null);
  const [loading, setLoading] = useState(true);
  const [showBookingModal, setShowBookingModal] = useState(false);
  const [quantity, setQuantity] = useState(1);
  const [upiId, setUpiId] = useState('');
  const [couponCode, setCouponCode] = useState('');
  const [processing, setProcessing] = useState(false);

  useEffect(() => {
    loadEvent();
  }, [eventId]);

  const loadEvent = async () => {
    if (!eventId) return;
    try {
      const data = await eventApi.getEvent(eventId);
      setEvent(data || null);
    } catch (error) {
      console.error('Failed to load event:', error);
      toast.error('Failed to load event details');
    } finally {
      setLoading(false);
    }
  };

  const handleBooking = async () => {
    if (!AuthService.isAuthenticated()) {
      toast.error('Please login to book tickets');
      navigate('/login');
      return;
    }

    const user = AuthService.getUser();
    if (!user) return;

    setProcessing(true);
    try {
      // Step 1: Create booking
      const booking = await bookingApi.createBooking({
        userId: user.id,
        eventId: event!.eventId,
        ticketType: 'General',
        quantity,
      });

      // Step 2: Process payment
      const totalAmount = event!.ticketPrice * quantity;
      const payment = await paymentApi.initiatePayment({
        bookingId: booking.bookingId,
        userId: user.id,
        amount: totalAmount,
        upiId,
        couponCode,
      });

      toast.success('Booking successful! Your tickets have been booked.');
      navigate(`/bookings`);
    } catch (error) {
      console.error('Booking failed:', error);
      const errorMessage = error instanceof Error ? error.message : 'Failed to complete booking';
      toast.error(errorMessage);
    } finally {
      setProcessing(false);
      setShowBookingModal(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-500">Loading event details...</p>
      </div>
    );
  }

  if (!event) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <p className="text-gray-500 mb-4">Event not found</p>
          <Button onClick={() => navigate('/events')}>Browse Events</Button>
        </div>
      </div>
    );
  }

  const availableTickets = event.totalTickets - event.ticketsSold;
  const eventDate = new Date(event.eventDate);
  const totalAmount = event.ticketPrice * quantity;

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="relative h-96 w-full bg-gray-200">
        <ImageWithFallback
          src={eventImages[event.eventType] || eventImages.Music}
          alt={event.title}
          className="w-full h-full object-cover"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />
        <div className="absolute bottom-0 left-0 right-0">
          <div className="container mx-auto px-4 pb-8">
            <Button
              variant="secondary"
              size="sm"
              onClick={() => navigate('/events')}
              className="mb-4 gap-2"
            >
              <ArrowLeft className="h-4 w-4" />
              Back to Events
            </Button>
            <h1 className="text-4xl text-white mb-2">{event.title}</h1>
            <Badge variant="secondary">{event.eventType}</Badge>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <div className="grid lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-6">
            <Card>
              <CardHeader>
                <h2 className="text-2xl">About This Event</h2>
              </CardHeader>
              <CardContent>
                <p className="text-gray-700 whitespace-pre-line">{event.description}</p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <h2 className="text-2xl">Event Details</h2>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex items-start gap-3">
                  <Calendar className="h-5 w-5 text-gray-400 mt-0.5" />
                  <div>
                    <p className="font-medium">Date & Time</p>
                    <p className="text-gray-600">
                      {eventDate.toLocaleDateString('en-US', { 
                        weekday: 'long', 
                        year: 'numeric', 
                        month: 'long', 
                        day: 'numeric' 
                      })}
                    </p>
                    <p className="text-gray-600">
                      {eventDate.toLocaleTimeString('en-US', { 
                        hour: '2-digit', 
                        minute: '2-digit' 
                      })}
                    </p>
                  </div>
                </div>

                <Separator />

                <div className="flex items-start gap-3">
                  <MapPin className="h-5 w-5 text-gray-400 mt-0.5" />
                  <div>
                    <p className="font-medium">Location</p>
                    <p className="text-gray-600">{event.location}</p>
                  </div>
                </div>

                <Separator />

                <div className="flex items-start gap-3">
                  <Users className="h-5 w-5 text-gray-400 mt-0.5" />
                  <div>
                    <p className="font-medium">Availability</p>
                    <p className="text-gray-600">
                      {availableTickets} of {event.totalTickets} tickets available
                    </p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          <div>
            <Card className="sticky top-4">
              <CardHeader>
                <h3 className="text-xl">Book Tickets</h3>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex items-center justify-between p-4 bg-blue-50 rounded-lg">
                  <div className="flex items-center gap-2">
                    <DollarSign className="h-5 w-5 text-blue-600" />
                    <span className="font-medium">Price per ticket</span>
                  </div>
                  <span className="text-2xl">${event.ticketPrice.toFixed(2)}</span>
                </div>

                <div>
                  <Label htmlFor="quantity">Number of Tickets</Label>
                  <Input
                    id="quantity"
                    type="number"
                    min={1}
                    max={Math.min(10, availableTickets)}
                    value={quantity}
                    onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 1))}
                  />
                </div>

                <Separator />

                <div className="flex items-center justify-between">
                  <span className="font-medium">Total Amount</span>
                  <span className="text-2xl">${totalAmount.toFixed(2)}</span>
                </div>

                <Button
                  className="w-full gap-2"
                  size="lg"
                  disabled={availableTickets === 0}
                  onClick={() => setShowBookingModal(true)}
                >
                  <Ticket className="h-5 w-5" />
                  {availableTickets === 0 ? 'Sold Out' : 'Book Now'}
                </Button>

                {availableTickets > 0 && availableTickets <= 10 && (
                  <p className="text-sm text-orange-600 text-center">
                    Hurry! Only {availableTickets} tickets left
                  </p>
                )}
              </CardContent>
            </Card>
          </div>
        </div>
      </div>

      <Dialog open={showBookingModal} onOpenChange={setShowBookingModal}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Complete Your Booking</DialogTitle>
          </DialogHeader>

          <div className="space-y-4">
            <div className="p-4 bg-gray-50 rounded-lg space-y-2">
              <div className="flex justify-between">
                <span>Tickets ({quantity})</span>
                <span>${(event.ticketPrice * quantity).toFixed(2)}</span>
              </div>
              <Separator />
              <div className="flex justify-between font-semibold">
                <span>Total</span>
                <span>${totalAmount.toFixed(2)}</span>
              </div>
            </div>

            <div>
              <Label htmlFor="upi">UPI ID (Optional)</Label>
              <Input
                id="upi"
                placeholder="yourname@upi"
                value={upiId}
                onChange={(e) => setUpiId(e.target.value)}
              />
            </div>

            <div>
              <Label htmlFor="coupon">Coupon Code (Optional)</Label>
              <Input
                id="coupon"
                placeholder="FREE100, DEVTEST, NEWUSER10"
                value={couponCode}
                onChange={(e) => setCouponCode(e.target.value.toUpperCase())}
              />
              <p className="text-xs text-gray-500 mt-1">
                Try: FREE100 (free), DEVTEST (free), NEWUSER10 (10% off)
              </p>
            </div>

            <Button
              className="w-full"
              onClick={handleBooking}
              disabled={processing}
            >
              {processing ? 'Processing...' : 'Confirm Booking'}
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
