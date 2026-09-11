import { createContext, useState, useEffect } from "react";
import API from "../axios";

const AuthContext = createContext({
  user: null,
  token: null,
  isAuthenticated: false,
  isLoading: true,
  login: async (email, password) => {},
  logout: () => {},
  register: async (userData) => {},
});

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Check if token exists in localStorage on mount
  useEffect(() => {
    const storedToken = localStorage.getItem("authToken");
    if (storedToken) {
      setToken(storedToken);
      setIsAuthenticated(true);
      // Optionally fetch user details
      fetchUserDetails(storedToken);
    } else {
      setIsLoading(false);
    }
  }, []);

  // Fetch user details from backend
  const fetchUserDetails = async (token) => {
    try {
      const response = await API.get("/auth/me", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setUser(response.data);
      setIsLoading(false);
    } catch (error) {
      console.error("Error fetching user details:", error);
      localStorage.removeItem("authToken");
      setToken(null);
      setIsAuthenticated(false);
      setIsLoading(false);
    }
  };

  // Login function
  const login = async (email, password) => {
    try {
      setIsLoading(true);
      const response = await API.post("/auth/login", {
        email,
        password,
      });

      const { token, user } = response.data;

      // Store token in localStorage
      localStorage.setItem("authToken", token);

      // Update state
      setToken(token);
      setUser(user);
      setIsAuthenticated(true);

      return { success: true, data: response.data };
    } catch (error) {
      console.error("Login error:", error);
      const serverMsg = error.response?.data;
      const errMsg = typeof serverMsg === 'string' ? serverMsg : serverMsg?.message || "Email hoặc mật khẩu không chính xác!";
      return {
        success: false,
        message: errMsg,
      };
    } finally {
      setIsLoading(false);
    }
  };

  // Register function
  const register = async (userData) => {
    try {
      setIsLoading(true);
      const response = await API.post("/auth/register", userData);

      const { token, user } = response.data;

      // Store token in localStorage
      localStorage.setItem("authToken", token);

      // Update state
      setToken(token);
      setUser(user);
      setIsAuthenticated(true);

      return { success: true, data: response.data };
    } catch (error) {
      console.error("Register error:", error);
      const serverMsg = error.response?.data;
      const errMsg = typeof serverMsg === 'string' ? serverMsg : serverMsg?.message || "Đăng ký thất bại. Email có thể đã được sử dụng!";
      return {
        success: false,
        message: errMsg,
      };
    } finally {
      setIsLoading(false);
    }
  };

  // Logout function
  const logout = () => {
    localStorage.removeItem("authToken");
    setToken(null);
    setUser(null);
    setIsAuthenticated(false);
  };

  const value = {
    user,
    token,
    isAuthenticated,
    isLoading,
    login,
    logout,
    register,
  };

  return (
    <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
  );
};

export default AuthContext;

