import { createContext, useContext, useState, useEffect } from "react";
import { authAPI, userAPI } from "../services/api";

const AuthContext = createContext(null);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth must be used within AuthProvider");
    }
    return context;
};

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(localStorage.getItem("token"));
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Check if user is logged in on mount
        const storedToken = localStorage.getItem("token");
        const storedUser = localStorage.getItem("user");

        if (storedToken && storedUser) {
            setToken(storedToken);
            setUser(JSON.parse(storedUser));
            // Verify token by fetching user profile
            fetchUserProfile();
        } else {
            setLoading(false);
        }
    }, []);

    const fetchUserProfile = async () => {
        try {
            const response = await userAPI.getProfile();
            setUser(response.data);
            localStorage.setItem("user", JSON.stringify(response.data));
        } catch (error) {
            console.error("Failed to fetch user profile:", error);
            logout();
        } finally {
            setLoading(false);
        }
    };

    const login = async (email, password) => {
        try {
            const response = await authAPI.login({ email, password });
            if (response.data.success && response.data.token) {
                const newToken = response.data.token;
                setToken(newToken);
                localStorage.setItem("token", newToken);

                // Fetch user profile
                const userResponse = await userAPI.getProfile();
                setUser(userResponse.data);
                localStorage.setItem("user", JSON.stringify(userResponse.data));

                return { success: true };
            }
            return {
                success: false,
                message: response.data.message || "Login failed",
            };
        } catch (error) {
            return {
                success: false,
                message:
                    error.response?.data?.message ||
                    error.message ||
                    "Login failed",
            };
        }
    };

    const register = async (userData) => {
        try {
            const response = await authAPI.register(userData);
            if (response.data.success && response.data.token) {
                const newToken = response.data.token;
                setToken(newToken);
                localStorage.setItem("token", newToken);

                // Fetch user profile
                const userResponse = await userAPI.getProfile();
                setUser(userResponse.data);
                localStorage.setItem("user", JSON.stringify(userResponse.data));

                return { success: true };
            }
            return {
                success: false,
                message: response.data.message || "Registration failed",
            };
        } catch (error) {
            return {
                success: false,
                message:
                    error.response?.data?.message ||
                    error.message ||
                    "Registration failed",
            };
        }
    };

    const logout = () => {
        setToken(null);
        setUser(null);
        localStorage.removeItem("token");
        localStorage.removeItem("user");
    };

    const bootstrapWithToken = async (nextToken) => {
        try {
            setToken(nextToken);
            localStorage.setItem("token", nextToken);
            const userResponse = await userAPI.getProfile();
            setUser(userResponse.data);
            localStorage.setItem("user", JSON.stringify(userResponse.data));
            return { success: true };
        } catch (error) {
            logout();
            return {
                success: false,
                message:
                    error.response?.data?.message ||
                    error.message ||
                    "Unable to complete login",
            };
        } finally {
            setLoading(false);
        }
    };

    const isAdmin = () => {
        return user?.role === "ADMIN";
    };

    const value = {
        user,
        token,
        loading,
        login,
        register,
        logout,
        bootstrapWithToken,
        isAdmin,
        isAuthenticated: !!token,
    };

    return (
        <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
    );
};
