import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Auth.css";

const OAuthCallback = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { bootstrapWithToken } = useAuth();
    const [error, setError] = useState("");

    useEffect(() => {
        const token = searchParams.get("token");
        const err = searchParams.get("error");

        const finish = async () => {
            if (err) {
                setError(err);
                return;
            }
            if (!token) {
                setError("Missing token from OAuth provider");
                return;
            }
            const result = await bootstrapWithToken(token);
            if (result.success) {
                navigate("/", { replace: true });
            } else {
                setError(result.message || "Unable to sign you in");
            }
        };

        finish();
    }, [bootstrapWithToken, navigate, searchParams]);

    return (
        <div className="auth-page">
            <div className="auth-container">
                <h1>Signing you in…</h1>
                {error ? (
                    <div className="error-message">{error}</div>
                ) : (
                    <p className="auth-subtitle">Completing Google sign-in</p>
                )}
            </div>
        </div>
    );
};

export default OAuthCallback;
