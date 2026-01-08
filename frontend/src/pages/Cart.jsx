import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
import { orderAPI } from "../services/api";
import "./Cart.css";

const Cart = () => {
    const {
        cartItems,
        updateQuantity,
        removeFromCart,
        getTotalPrice,
        clearCart,
    } = useCart();
    const { isAuthenticated } = useAuth();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [deliveryAddress, setDeliveryAddress] = useState("");
    const [phoneNumber, setPhoneNumber] = useState("");
    const [locationLoading, setLocationLoading] = useState(false);

    const handleCheckout = async () => {
        if (!isAuthenticated) {
            navigate("/login");
            return;
        }

        if (cartItems.length === 0) {
            alert("Your cart is empty!");
            return;
        }

        if (!deliveryAddress.trim()) {
            alert("Please enter a delivery address");
            return;
        }

        if (!phoneNumber.trim()) {
            alert("Please enter a phone number");
            return;
        }

        try {
            setLoading(true);
            const orderData = {
                items: cartItems.map((item) => ({
                    foodId: item.id,
                    quantity: item.quantity,
                })),
                deliveryAddress: deliveryAddress.trim(),
                phoneNumber: phoneNumber.trim(),
            };

            await orderAPI.create(orderData);
            clearCart();
            alert("Order placed successfully!");
            navigate("/orders");
        } catch (error) {
            console.error("Error placing order:", error);
            alert(
                error.response?.data?.error ||
                    "Failed to place order. Please try again."
            );
        } finally {
            setLoading(false);
        }
    };

    const handleUseLocation = () => {
        if (!navigator.geolocation) {
            alert("Geolocation is not supported by your browser");
            return;
        }

        setLocationLoading(true);
        navigator.geolocation.getCurrentPosition(
            (position) => {
                const { latitude, longitude } = position.coords;
                const formatted = `Lat: ${latitude.toFixed(
                    5
                )}, Lng: ${longitude.toFixed(5)}`;
                setDeliveryAddress(formatted);
                setLocationLoading(false);
            },
            (error) => {
                console.error("Geolocation error:", error);
                alert(
                    "Could not fetch your location. Please enter the address manually."
                );
                setLocationLoading(false);
            },
            { enableHighAccuracy: true, timeout: 10000 }
        );
    };

    if (cartItems.length === 0) {
        return (
            <div className="cart-page">
                <div className="empty-cart">
                    <h2>Your cart is empty</h2>
                    <p>Add some delicious items from our menu!</p>
                    <button
                        onClick={() => navigate("/menu")}
                        className="btn btn-primary"
                    >
                        Browse Menu
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="cart-page">
            <h1>Shopping Cart</h1>

            <div className="cart-content">
                <div className="cart-items">
                    {cartItems.map((item) => (
                        <div key={item.id} className="cart-item">
                            <div className="cart-item-info">
                                <h3>{item.name}</h3>
                                <p className="cart-item-price">
                                    {item.price.toFixed(2)} Birr each
                                </p>
                            </div>
                            <div className="cart-item-controls">
                                <div className="quantity-controls">
                                    <button
                                        onClick={() =>
                                            updateQuantity(
                                                item.id,
                                                item.quantity - 1
                                            )
                                        }
                                        className="quantity-btn"
                                    >
                                        -
                                    </button>
                                    <span className="quantity">
                                        {item.quantity}
                                    </span>
                                    <button
                                        onClick={() =>
                                            updateQuantity(
                                                item.id,
                                                item.quantity + 1
                                            )
                                        }
                                        className="quantity-btn"
                                    >
                                        +
                                    </button>
                                </div>
                                <div className="cart-item-total">
                                    {(item.price * item.quantity).toFixed(2)}{" "}
                                    Birr
                                </div>
                                <button
                                    onClick={() => removeFromCart(item.id)}
                                    className="remove-btn"
                                >
                                    Remove
                                </button>
                            </div>
                        </div>
                    ))}
                </div>

                <div className="cart-summary">
                    <h2>Order Summary</h2>

                    <div className="summary-row">
                        <span>Subtotal:</span>
                        <span>{getTotalPrice().toFixed(2)} Birr</span>
                    </div>
                    <div className="summary-row">
                        <span>Delivery:</span>
                        <span>Free</span>
                    </div>
                    <div className="summary-row total">
                        <span>Total:</span>
                        <span>{getTotalPrice().toFixed(2)} Birr</span>
                    </div>

                    <div className="checkout-form">
                        <input
                            type="text"
                            placeholder="Delivery Address *"
                            value={deliveryAddress}
                            onChange={(e) => setDeliveryAddress(e.target.value)}
                            className="form-input"
                            required
                        />
                        <button
                            type="button"
                            onClick={handleUseLocation}
                            className="location-btn"
                            disabled={locationLoading}
                        >
                            {locationLoading
                                ? "Getting location..."
                                : "Use my current location"}
                        </button>
                        <input
                            type="tel"
                            placeholder="Phone Number *"
                            value={phoneNumber}
                            onChange={(e) => setPhoneNumber(e.target.value)}
                            className="form-input"
                            required
                        />
                        <button
                            onClick={handleCheckout}
                            disabled={
                                loading ||
                                !deliveryAddress.trim() ||
                                !phoneNumber.trim()
                            }
                            className="checkout-btn"
                        >
                            {loading ? "Placing Order..." : "Place Order"}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Cart;
