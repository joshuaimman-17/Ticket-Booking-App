import { Link, useNavigate } from "react-router-dom";
import { Button } from "./ui/button";
import { AuthService } from "../lib/auth";
import { Ticket, User, LogOut, LayoutDashboard } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "./ui/dropdown-menu";

export function Navbar() {
  const navigate = useNavigate();

  const user = AuthService.getUser();
  const admin = AuthService.getAdmin();
  const isAuthenticated = AuthService.isAuthenticated();
  const role = AuthService.getCurrentRole();

  const handleLogout = () => {
    AuthService.logout();
    navigate("/"); // using react-router navigate
  };

  return (
    <nav className="border-b bg-white">
      <div className="container mx-auto px-4">
        <div className="flex h-16 items-center justify-between">
          <Link to="/" className="flex items-center gap-2">
            <Ticket className="h-6 w-6 text-blue-600" />
            <span className="font-semibold">TicketBooking</span>
          </Link>

          <div className="flex items-center gap-4">
            <Link to="/events">
              <Button variant="ghost">Events</Button>
            </Link>

            {isAuthenticated ? (
              <>
                {role !== "ADMIN" && (
                  <Link to="/bookings">
                    <Button variant="ghost">My Bookings</Button>
                  </Link>
                )}

                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button variant="outline" className="gap-2">
                      <User className="h-4 w-4" />
                      {admin ? admin.name : user?.name}
                    </Button>
                  </DropdownMenuTrigger>

                  <DropdownMenuContent align="end">
                    {role === "ADMIN" && (
                      <DropdownMenuItem asChild>
                        <Link
                          to="/admin"
                          className="flex items-center gap-2"
                        >
                          <LayoutDashboard className="h-4 w-4" />
                          Admin Dashboard
                        </Link>
                      </DropdownMenuItem>
                    )}
                    {role === "HOST" && (
                      <DropdownMenuItem asChild>
                        <Link
                          to="/host"
                          className="flex items-center gap-2"
                        >
                          <LayoutDashboard className="h-4 w-4" />
                          Host Dashboard
                        </Link>
                      </DropdownMenuItem>
                    )}
                    {role === "USER" && (
                      <DropdownMenuItem asChild>
                        <Link
                          to="/dashboard"
                          className="flex items-center gap-2"
                        >
                          <LayoutDashboard className="h-4 w-4" />
                          Dashboard
                        </Link>
                      </DropdownMenuItem>
                    )}

                    <DropdownMenuItem
                      onClick={handleLogout}
                      className="gap-2 cursor-pointer"
                    >
                      <LogOut className="h-4 w-4" />
                      Logout
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </>
            ) : (
              <div className="flex gap-2">
                <Link to="/login">
                  <Button variant="outline">Login</Button>
                </Link>
                <Link to="/signup">
                  <Button>Sign Up</Button>
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
