import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthService } from '../lib/auth';
import { adminApi, eventApi } from '../lib/api';
import { User, Event } from '../types';
import { Card, CardContent, CardHeader } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs';
import { 
  Users, 
  Calendar, 
  DollarSign, 
  TrendingUp, 
  CheckCircle, 
  XCircle, 
  Trash2,
  UserCheck
} from 'lucide-react';
import { toast } from 'sonner';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '../components/ui/alert-dialog';

export function AdminDashboard() {
  const navigate = useNavigate();
  const [admin, setAdmin] = useState(AuthService.getAdmin());
  const [pendingHosts, setPendingHosts] = useState<User[]>([]);
  const [approvedHosts, setApprovedHosts] = useState<User[]>([]);
  const [events, setEvents] = useState<Event[]>([]);
  const [stats, setStats] = useState({
    totalBookings: 0,
    totalRevenue: 0,
    pendingBookings: 0,
    confirmedBookings: 0,
  });
  const [eventToDelete, setEventToDelete] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [hostFilter, setHostFilter] = useState<'pending' | 'approved'>('pending'); // filter toggle

  useEffect(() => {
    if (!admin) {
      toast.error('Access denied. Admin privileges required.');
      navigate('/login');
      return;
    }
    loadDashboardData();
  }, [admin, navigate]);

  const loadDashboardData = async () => {
    try {
      const [pendingHostsData, approvedHostsData, eventsData, statsData] = await Promise.all([
        adminApi.getPendingHosts(),
        adminApi.getApprovedHosts(),
        eventApi.getAllEvents(),
        adminApi.getStats(),
      ]);

      setPendingHosts(pendingHostsData);
      setApprovedHosts(approvedHostsData);
      setEvents(eventsData);
    } catch (error) {
      console.error('Failed to load dashboard data:', error);
      toast.error('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const handleApproveHost = async (userId: string) => {
    try {
      await adminApi.approveHost(userId);
      toast.success('Host approved successfully!');
      loadDashboardData();
    } catch (error) {
      toast.error('Failed to approve host');
    }
  };

  const handleRejectHost = async (userId: string) => {
    try {
      await adminApi.rejectHost(userId);
      toast.success('Host rejected successfully!');
      loadDashboardData();
    } catch (error) {
      toast.error('Failed to reject host');
    }
  };

  const handleDeleteEvent = async () => {
    if (!eventToDelete) return;

    try {
      await adminApi.deleteEvent(eventToDelete);
      toast.success('Event deleted successfully');
      setEventToDelete(null);
      loadDashboardData();
    } catch (error) {
      toast.error('Failed to delete event');
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-500">Loading admin dashboard...</p>
      </div>
    );
  }

  if (!admin) return null;

  // Determine which hosts to display based on filter
  const displayedHosts = hostFilter === 'pending' ? pendingHosts : approvedHosts;

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        <div className="mb-8">
          <h1 className="text-3xl mb-2">Admin Dashboard</h1>
          <p className="text-gray-600">Manage users, events, and monitor platform performance</p>
        </div>

        {/* Stats Overview */}
        <div className="grid md:grid-cols-4 gap-6 mb-8">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <h3>Total Bookings</h3>
                <Calendar className="h-5 w-5 text-blue-600" />
              </div>
            </CardHeader>
            <CardContent>
              <p className="text-3xl">{stats.totalBookings}</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <h3>Total Revenue</h3>
                <DollarSign className="h-5 w-5 text-green-600" />
              </div>
            </CardHeader>
            <CardContent>
              <p className="text-3xl">${stats.totalRevenue.toFixed(2)}</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <h3>Active Events</h3>
                <TrendingUp className="h-5 w-5 text-purple-600" />
              </div>
            </CardHeader>
            <CardContent>
              <p className="text-3xl">{events.length}</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <h3>Total Hosts</h3>
                <Users className="h-5 w-5 text-orange-600" />
              </div>
            </CardHeader>
            <CardContent>
              <p className="text-3xl">{pendingHosts.length + approvedHosts.length}</p>
            </CardContent>
          </Card>
        </div>

        {/* Tabs */}
        <Card>
          <Tabs defaultValue="hosts" className="w-full">
            <CardHeader>
              <TabsList className="grid w-full grid-cols-2">
                <TabsTrigger value="hosts">
                  Hosts ({displayedHosts.length})
                </TabsTrigger>
                <TabsTrigger value="events">
                  All Events ({events.length})
                </TabsTrigger>
              </TabsList>
            </CardHeader>

            <CardContent>
              {/* Hosts Tab */}
              <TabsContent value="hosts" className="mt-0">
                {/* Filter buttons */}
                <div className="flex gap-2 mb-4">
                  <Button
                    size="sm"
                    variant={hostFilter === 'pending' ? 'default' : 'outline'}
                    onClick={() => setHostFilter('pending')}
                  >
                    Pending ({pendingHosts.length})
                  </Button>
                  <Button
                    size="sm"
                    variant={hostFilter === 'approved' ? 'default' : 'outline'}
                    onClick={() => setHostFilter('approved')}
                  >
                    Approved ({approvedHosts.length})
                  </Button>
                </div>

                {displayedHosts.length === 0 ? (
                  <div className="text-center py-12">
                    <UserCheck className="h-16 w-16 text-gray-300 mx-auto mb-4" />
                    <h3 className="text-xl mb-2">
                      {hostFilter === 'pending' ? 'No pending applications' : 'No approved hosts'}
                    </h3>
                    <p className="text-gray-600">
                      {hostFilter === 'pending'
                        ? 'All host applications have been processed'
                        : 'Approved hosts will appear here'}
                    </p>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {displayedHosts.map((host) => (
                      <div
                        key={host.id}
                        className={`p-4 border rounded-lg hover:shadow-md transition-shadow ${
                          hostFilter === 'approved' ? 'bg-green-50' : ''
                        }`}
                      >
                        <div className="flex items-start justify-between">
                          <div className="flex-1">
                            <h3 className="text-lg font-medium mb-1">{host.name}</h3>
                            <p className="text-gray-600 mb-2">{host.email}</p>
                            <div className="space-y-1 text-sm">
                              {host.idProofUrl && hostFilter === 'pending' && (
                                <div>
                                  <span className="text-gray-500">ID Proof: </span>
                                  <a
                                    href={host.idProofUrl}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="text-blue-600 hover:underline"
                                  >
                                    View Document
                                  </a>
                                </div>
                              )}
                              {host.paypalEmail && (
                                <div>
                                  <span className="text-gray-500">PayPal: </span>
                                  <span>{host.paypalEmail}</span>
                                </div>
                              )}
                            </div>
                          </div>
                          <div className="flex gap-2">
                            {hostFilter === 'pending' && (
                              <Button
                                size="sm"
                                variant="default"
                                className="gap-2"
                                onClick={() => handleApproveHost(host.id)}
                              >
                                <CheckCircle className="h-4 w-4" />
                                Approve
                              </Button>
                            )}
                            <Button
                              size="sm"
                              variant="destructive"
                              className="gap-2"
                              onClick={() => handleRejectHost(host.id)}
                            >
                              <XCircle className="h-4 w-4" />
                              Reject
                            </Button>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </TabsContent>

              {/* Events Tab */}
              <TabsContent value="events" className="mt-0">
                {events.length === 0 ? (
                  <div className="text-center py-12">
                    <Calendar className="h-16 w-16 text-gray-300 mx-auto mb-4" />
                    <h3 className="text-xl mb-2">No events yet</h3>
                    <p className="text-gray-600">Events will appear here once created</p>
                  </div>
                ) : (
                  <div className="space-y-4">
                    {events.map((event) => (
                      <div
                        key={event.eventId}
                        className="p-4 border rounded-lg hover:shadow-md transition-shadow"
                      >
                        <div className="flex items-start justify-between">
                          <div className="flex-1">
                            <div className="flex items-center gap-2 mb-2">
                              <h3 className="text-lg font-medium">{event.title}</h3>
                              <Badge variant="outline">{event.eventType}</Badge>
                              <Badge
                                variant={event.status === 'ACTIVE' ? 'default' : 'secondary'}
                              >
                                {event.status}
                              </Badge>
                            </div>
                            <p className="text-gray-600 mb-2 line-clamp-2">
                              {event.description}
                            </p>
                            <div className="flex flex-wrap gap-4 text-sm text-gray-600">
                              <div>
                                <span className="text-gray-500">Date: </span>
                                {new Date(event.eventDate).toLocaleDateString()}
                              </div>
                              <div>
                                <span className="text-gray-500">Location: </span>
                                {event.location}
                              </div>
                              <div>
                                <span className="text-gray-500">Tickets: </span>
                                {event.ticketsSold} / {event.totalTickets}
                              </div>
                              <div>
                                <span className="text-gray-500">Price: </span>
                                ${event.ticketPrice.toFixed(2)}
                              </div>
                            </div>
                          </div>
                          <Button
                            size="sm"
                            variant="destructive"
                            className="gap-2"
                            onClick={() => setEventToDelete(event.eventId)}
                          >
                            <Trash2 className="h-4 w-4" />
                            Delete
                          </Button>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </TabsContent>
            </CardContent>
          </Tabs>
        </Card>
      </div>

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={!!eventToDelete} onOpenChange={() => setEventToDelete(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Event?</AlertDialogTitle>
            <AlertDialogDescription>
              This action cannot be undone. This will permanently delete the event and may affect
              existing bookings.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction onClick={handleDeleteEvent} className="bg-red-600 hover:bg-red-700">
              Delete Event
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
