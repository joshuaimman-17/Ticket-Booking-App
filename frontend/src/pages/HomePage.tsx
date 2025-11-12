import { Link } from 'react-router-dom';
import { Button } from '../components/ui/button';
import { ArrowRight, Calendar, Shield, Zap } from 'lucide-react';
import { ImageWithFallback } from '../components/figma/ImageWithFallback';

export function HomePage() {
  return (
    <div>
      {/* Hero Section */}
      <section className="relative bg-gradient-to-br from-blue-600 to-purple-600 text-white">
        <div className="container mx-auto px-4 py-20">
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div className="space-y-6">
              <h1 className="text-5xl">
                Your Gateway to Unforgettable Events
              </h1>
              <p className="text-xl text-blue-100">
                Discover and book tickets for concerts, conferences, sports events, and more. 
                Secure, easy, and instant booking.
              </p>
              <div className="flex gap-4">
                <Link to="/events">
                  <Button size="lg" variant="secondary" className="gap-2">
                    Browse Events <ArrowRight className="h-4 w-4" />
                  </Button>
                </Link>
                <Link to="/signup">
                  <Button size="lg" variant="outline" className="bg-transparent border-white text-white hover:bg-white hover:text-blue-600">
                    Get Started
                  </Button>
                </Link>
              </div>
            </div>
            <div className="relative h-96 rounded-lg overflow-hidden shadow-2xl">
              <ImageWithFallback
                src="https://images.unsplash.com/photo-1545672968-3ef43aceabe3?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxldmVudCUyMHRpY2tldHN8ZW58MXx8fHwxNzYyMjc2MDgwfDA&ixlib=rb-4.1.0&q=80&w=1080"
                alt="Event tickets"
                className="w-full h-full object-cover"
              />
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 bg-gray-50">
        <div className="container mx-auto px-4">
          <div className="text-center mb-12">
            <h2 className="text-3xl mb-4">Why Choose TicketBooking?</h2>
            <p className="text-gray-600 max-w-2xl mx-auto">
              We provide a seamless ticket booking experience with features designed for both event-goers and organizers.
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            <div className="bg-white p-8 rounded-lg shadow-sm text-center">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-blue-100 rounded-full mb-4">
                <Zap className="h-8 w-8 text-blue-600" />
              </div>
              <h3 className="text-xl mb-2">Instant Booking</h3>
              <p className="text-gray-600">
                Book your tickets in seconds with our streamlined checkout process. No hassle, just fun.
              </p>
            </div>

            <div className="bg-white p-8 rounded-lg shadow-sm text-center">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-green-100 rounded-full mb-4">
                <Shield className="h-8 w-8 text-green-600" />
              </div>
              <h3 className="text-xl mb-2">Secure Payments</h3>
              <p className="text-gray-600">
                Your payment information is safe with our encrypted payment gateway and secure processing.
              </p>
            </div>

            <div className="bg-white p-8 rounded-lg shadow-sm text-center">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-purple-100 rounded-full mb-4">
                <Calendar className="h-8 w-8 text-purple-600" />
              </div>
              <h3 className="text-xl mb-2">Event Management</h3>
              <p className="text-gray-600">
                Become a host and manage your own events with our comprehensive event management tools.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-blue-600 text-white">
        <div className="container mx-auto px-4 text-center">
          <h2 className="text-3xl mb-4">Ready to Create Your Event?</h2>
          <p className="text-xl text-blue-100 mb-8 max-w-2xl mx-auto">
            Join thousands of event organizers who trust TicketBooking to manage their events and sell tickets.
          </p>
          <Link to="/signup">
            <Button size="lg" variant="secondary">
              Become a Host
            </Button>
          </Link>
        </div>
      </section>
    </div>
  );
}
