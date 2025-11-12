import { Link } from 'react-router-dom';
import { Event } from '../types';
import { Card, CardContent, CardFooter, CardHeader } from './ui/card';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { Calendar, MapPin, Users, DollarSign } from 'lucide-react';
import { ImageWithFallback } from './figma/ImageWithFallback';

interface EventCardProps {
  event: Event;
}

const eventImages: Record<string, string> = {
  Music: 'https://images.unsplash.com/photo-1656283384093-1e227e621fad?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxtdXNpYyUyMGNvbmNlcnQlMjBjcm93ZHxlbnwxfHx8fDE3NjIxOTUzOTB8MA&ixlib=rb-4.1.0&q=80&w=1080',
  Conference: 'https://images.unsplash.com/photo-1540575467063-178a50c2df87?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxidXNpbmVzcyUyMGNvbmZlcmVuY2V8ZW58MXx8fHwxNzYyMjU1NjUyfDA&ixlib=rb-4.1.0&q=80&w=1080',
  Comedy: 'https://images.unsplash.com/photo-1758726942669-92f7e499018c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjb21lZHklMjB0aGVhdGVyfGVufDF8fHx8MTc2MjE4NTEyNHww&ixlib=rb-4.1.0&q=80&w=1080',
  Art: 'https://images.unsplash.com/photo-1713779490284-a81ff6a8ffae?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxhcnQlMjBnYWxsZXJ5JTIwZXhoaWJpdGlvbnxlbnwxfHx8fDE3NjIyNTQ4Nzl8MA&ixlib=rb-4.1.0&q=80&w=1080',
};

export function EventCard({ event }: EventCardProps) {
  const availableTickets = event.totalTickets - event.ticketsSold;
  const availabilityPercent = (availableTickets / event.totalTickets) * 100;
  const eventDate = new Date(event.eventDate);

  return (
    <Card className="overflow-hidden hover:shadow-lg transition-shadow">
      <div className="relative h-48 w-full overflow-hidden bg-gray-200">
        <ImageWithFallback
          src={eventImages[event.eventType] || eventImages.Music}
          alt={event.title}
          className="h-full w-full object-cover"
        />
        <div className="absolute top-2 right-2">
          <Badge variant={availabilityPercent > 20 ? 'default' : 'destructive'}>
            {availableTickets} left
          </Badge>
        </div>
      </div>

      <CardHeader>
        <div className="flex items-start justify-between gap-2">
          <h3 className="line-clamp-1">{event.title}</h3>
          <Badge variant="outline">{event.eventType}</Badge>
        </div>
      </CardHeader>

      <CardContent className="space-y-2">
        <p className="line-clamp-2 text-gray-600">{event.description}</p>
        
        <div className="space-y-1 text-sm text-gray-600">
          <div className="flex items-center gap-2">
            <Calendar className="h-4 w-4" />
            <span>{eventDate.toLocaleDateString('en-US', { 
              weekday: 'short', 
              year: 'numeric', 
              month: 'short', 
              day: 'numeric',
              hour: '2-digit',
              minute: '2-digit'
            })}</span>
          </div>
          
          <div className="flex items-center gap-2">
            <MapPin className="h-4 w-4" />
            <span className="line-clamp-1">{event.location}</span>
          </div>
          
          <div className="flex items-center gap-2">
            <Users className="h-4 w-4" />
            <span>{event.ticketsSold} / {event.totalTickets} tickets sold</span>
          </div>
          
          <div className="flex items-center gap-2">
            <DollarSign className="h-4 w-4" />
            <span className="font-semibold">${event.ticketPrice.toFixed(2)}</span>
          </div>
        </div>
      </CardContent>

      <CardFooter>
        <Link to={`/events/${event.eventId}`} className="w-full">
          <Button className="w-full" disabled={availableTickets === 0}>
            {availableTickets === 0 ? 'Sold Out' : 'Book Now'}
          </Button>
        </Link>
      </CardFooter>
    </Card>
  );
}
