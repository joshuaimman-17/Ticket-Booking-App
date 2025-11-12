import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthService } from '../lib/auth';
import { userApi } from '../lib/api';
import { Card, CardContent, CardHeader } from '../components/ui/card';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '../components/ui/dialog';
import { Badge } from '../components/ui/badge';
import { User, Ticket, Calendar, UserPlus } from 'lucide-react';
import { toast } from 'sonner';

export function UserDashboard() {
  const navigate = useNavigate();
  const [user, setUser] = useState(AuthService.getUser());
  const [showHostApplication, setShowHostApplication] = useState(false);
  const [idProofUrl, setIdProofUrl] = useState('');
  const [paypalEmail, setPaypalEmail] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!user) {
      navigate('/login');
    }
  }, [user, navigate]);

  const handleApplyHost = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return;

    setLoading(true);
    try {
      await userApi.applyHost(user.id, { idProofUrl, paypalEmail });
      toast.success('Host application submitted! Wait for admin approval.');
      setShowHostApplication(false);
      setIdProofUrl('');
      setPaypalEmail('');
    } catch (error) {
      toast.error('Failed to submit host application');
    } finally {
      setLoading(false);
    }
  };

  if (!user) return null;

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        <div className="mb-8">
          <h1 className="text-3xl mb-2">Dashboard</h1>
          <p className="text-gray-600">Welcome back, {user.name}!</p>
        </div>

        <div className="grid md:grid-cols-3 gap-6 mb-8">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <h3>Account Status</h3>
                <User className="h-5 w-5 text-gray-400" />
              </div>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                <div className="flex justify-between">
                  <span className="text-gray-600">Role</span>
                  <Badge>{user.role}</Badge>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Email</span>
                  <span className="text-sm">{user.email}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Verified</span>
                  <Badge variant={user.isVerified ? 'default' : 'secondary'}>
                    {user.isVerified ? 'Yes' : 'No'}
                  </Badge>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="cursor-pointer hover:shadow-lg transition-shadow" onClick={() => navigate('/bookings')}>
            <CardHeader>
              <div className="flex items-center justify-between">
                <h3>My Bookings</h3>
                <Ticket className="h-5 w-5 text-blue-600" />
              </div>
            </CardHeader>
            <CardContent>
              <p className="text-gray-600">View and manage your event bookings</p>
            </CardContent>
          </Card>

          <Card className="cursor-pointer hover:shadow-lg transition-shadow" onClick={() => navigate('/events')}>
            <CardHeader>
              <div className="flex items-center justify-between">
                <h3>Browse Events</h3>
                <Calendar className="h-5 w-5 text-green-600" />
              </div>
            </CardHeader>
            <CardContent>
              <p className="text-gray-600">Discover exciting events near you</p>
            </CardContent>
          </Card>
        </div>

        {user.role === 'USER' && (
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-xl mb-1">Become a Host</h3>
                  <p className="text-sm text-gray-600">
                    Create and manage your own events. Reach thousands of potential attendees.
                  </p>
                </div>
                <UserPlus className="h-8 w-8 text-purple-600" />
              </div>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div className="grid md:grid-cols-3 gap-4">
                  <div className="p-4 bg-purple-50 rounded-lg">
                    <h4 className="font-medium mb-1">Create Events</h4>
                    <p className="text-sm text-gray-600">
                      List your events and sell tickets
                    </p>
                  </div>
                  <div className="p-4 bg-purple-50 rounded-lg">
                    <h4 className="font-medium mb-1">Manage Bookings</h4>
                    <p className="text-sm text-gray-600">
                      Track ticket sales and attendees
                    </p>
                  </div>
                  <div className="p-4 bg-purple-50 rounded-lg">
                    <h4 className="font-medium mb-1">Earn Revenue</h4>
                    <p className="text-sm text-gray-600">
                      Get paid directly to your account
                    </p>
                  </div>
                </div>
                <Button onClick={() => setShowHostApplication(true)} className="gap-2">
                  <UserPlus className="h-4 w-4" />
                  Apply to Become a Host
                </Button>
              </div>
            </CardContent>
          </Card>
        )}
      </div>

      <Dialog open={showHostApplication} onOpenChange={setShowHostApplication}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Apply to Become a Host</DialogTitle>
          </DialogHeader>

          <form onSubmit={handleApplyHost} className="space-y-4">
            <div>
              <Label htmlFor="idProof">ID Proof URL</Label>
              <Input
                id="idProof"
                placeholder="https://example.com/id-proof.pdf"
                value={idProofUrl}
                onChange={(e) => setIdProofUrl(e.target.value)}
                required
              />
              <p className="text-xs text-gray-500 mt-1">
                Upload your ID to a cloud storage and paste the link here
              </p>
            </div>

            <div>
              <Label htmlFor="paypal">PayPal Email</Label>
              <Input
                id="paypal"
                type="email"
                placeholder="your-paypal@example.com"
                value={paypalEmail}
                onChange={(e) => setPaypalEmail(e.target.value)}
                required
              />
              <p className="text-xs text-gray-500 mt-1">
                This will be used to receive payments from ticket sales
              </p>
            </div>

            <div className="bg-blue-50 p-4 rounded-lg">
              <p className="text-sm text-blue-900">
                Your application will be reviewed by our admin team. You'll be notified once approved.
              </p>
            </div>

            <Button type="submit" className="w-full" disabled={loading}>
              {loading ? 'Submitting...' : 'Submit Application'}
            </Button>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
