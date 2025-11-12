import { User, Admin } from "../types";

export type Role = "USER" | "HOST" | "ADMIN";

export const AuthService = {
  setUser: (user: User) => localStorage.setItem("user", JSON.stringify(user)),

  getUser: (): User | null => {
    const data = localStorage.getItem("user");
    return data ? JSON.parse(data) : null;
  },

  setAdmin: (admin: Admin) => localStorage.setItem("admin", JSON.stringify(admin)),

  getAdmin: (): Admin | null => {
    const data = localStorage.getItem("admin");
    return data ? JSON.parse(data) : null;
  },

  getCurrentRole: (): Role | null => {
    const admin = AuthService.getAdmin();
    if (admin) return "ADMIN";

    const user = AuthService.getUser();
    return user?.role || null;
  },

  logout: () => {
    localStorage.removeItem("user");
    localStorage.removeItem("admin");
  },

  isAuthenticated: (): boolean => !!AuthService.getUser() || !!AuthService.getAdmin(),
};
