import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { foodAPI, orderAPI } from "../services/api";
import FoodForm from "../components/FoodForm";
import "./Admin.css";

const Admin = () => {
    const { isAdmin } = useAuth();
    const [activeTab, setActiveTab] = useState("orders");
    const [orders, setOrders] = useState([]);
    const [foods, setFoods] = useState([]);
    const [loading, setLoading] = useState(false);
    const [showFoodForm, setShowFoodForm] = useState(false);
    const [editingFood, setEditingFood] = useState(null);
    const [updatingStatus, setUpdatingStatus] = useState({});

    useEffect(() => {
        if (isAdmin()) {
            if (activeTab === "orders") {
                fetchOrders();
            } else {
                fetchFoods();
            }
        }
    }, [activeTab, isAdmin]);

    const fetchOrders = async () => {
        try {
            setLoading(true);
            const response = await orderAPI.getAll();
            setOrders(response.data);
        } catch (error) {
            console.error("Error fetching orders:", error);
        } finally {
            setLoading(false);
        }
    };

    const fetchFoods = async () => {
        try {
            setLoading(true);
            const response = await foodAPI.getAll();
            setFoods(response.data);
        } catch (error) {
            console.error("Error fetching foods:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleStatusUpdate = async (orderId, newStatus) => {
        // Prevent multiple updates
        if (updatingStatus[orderId]) {
            return;
        }

        try {
            setUpdatingStatus((prev) => ({ ...prev, [orderId]: true }));
            // Ensure status is uppercase to match enum
            const statusValue = newStatus.toUpperCase();
            console.log("Updating order", orderId, "to status:", statusValue);
            await orderAPI.updateStatus(orderId, statusValue);
            // Refresh orders to get updated data
            await fetchOrders();
        } catch (error) {
            console.error("Error updating status:", error);
            console.error("Error response:", error.response);

            // Extract error message from response
            let errorMessage =
                "Failed to update order status. Please try again.";

            if (error.response?.data) {
                // Handle string error message
                if (typeof error.response.data === "string") {
                    errorMessage = error.response.data;
                }
                // Handle ErrorResponse object
                else if (
                    error.response.data.error ||
                    error.response.data.message
                ) {
                    errorMessage =
                        error.response.data.message ||
                        error.response.data.error;
                }
            }

            alert(errorMessage);
            // Refresh to revert the UI state
            fetchOrders();
        } finally {
            setUpdatingStatus((prev) => ({ ...prev, [orderId]: false }));
        }
    };

    const handleDeleteFood = async (foodId) => {
        if (
            !window.confirm("Are you sure you want to delete this food item?")
        ) {
            return;
        }

        try {
            await foodAPI.delete(foodId);
            fetchFoods();
        } catch (error) {
            console.error("Error deleting food:", error);
            alert(
                error.response?.data?.error ||
                    "Failed to delete food item. Please try again."
            );
        }
    };

    const handleAddFood = () => {
        setEditingFood(null);
        setShowFoodForm(true);
    };

    const handleEditFood = (food) => {
        setEditingFood(food);
        setShowFoodForm(true);
    };

    const handleSaveFood = async (foodData) => {
        try {
            if (editingFood) {
                // Update existing food
                await foodAPI.update(editingFood.id, foodData);
            } else {
                // Create new food
                await foodAPI.create(foodData);
            }
            setShowFoodForm(false);
            setEditingFood(null);
            fetchFoods();
        } catch (error) {
            console.error("Error saving food:", error);
            alert(
                error.response?.data?.error ||
                    "Failed to save food item. Please try again."
            );
        }
    };

    const handleCancelFoodForm = () => {
        setShowFoodForm(false);
        setEditingFood(null);
    };

    if (!isAdmin()) {
        return (
            <div className="admin-page">
                <div className="error">
                    Access denied. Admin privileges required.
                </div>
            </div>
        );
    }

    return (
        <div className="admin-page">
            <h1>Admin Dashboard</h1>

            <div className="admin-tabs">
                <button
                    className={activeTab === "orders" ? "active" : ""}
                    onClick={() => setActiveTab("orders")}
                >
                    Orders
                </button>
                <button
                    className={activeTab === "foods" ? "active" : ""}
                    onClick={() => setActiveTab("foods")}
                >
                    Food Management
                </button>
            </div>

            {activeTab === "orders" && (
                <div className="admin-orders">
                    {loading ? (
                        <div className="loading">Loading orders...</div>
                    ) : orders.length === 0 ? (
                        <div className="no-data">No orders found</div>
                    ) : (
                        <div className="orders-table">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Order ID</th>
                                        <th>Customer</th>
                                        <th>Items</th>
                                        <th>Total</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {orders.map((order) => (
                                        <tr key={order.id}>
                                            <td>#{order.id}</td>
                                            <td>{order.userEmail}</td>
                                            <td className="items-cell">
                                                {order.items
                                                    .map(
                                                        (item) =>
                                                            `${item.foodName} x${item.quantity}`
                                                    )
                                                    .join(", ")}
                                            </td>
                                            <td>
                                                ${order.totalAmount.toFixed(2)}
                                            </td>
                                            <td>
                                                <select
                                                    value={order.status}
                                                    onChange={(e) =>
                                                        handleStatusUpdate(
                                                            order.id,
                                                            e.target.value
                                                        )
                                                    }
                                                    className="status-select"
                                                    disabled={
                                                        updatingStatus[order.id]
                                                    }
                                                >
                                                    <option value="PENDING">
                                                        PENDING
                                                    </option>
                                                    <option value="CONFIRMED">
                                                        CONFIRMED
                                                    </option>
                                                    <option value="PREPARING">
                                                        PREPARING
                                                    </option>
                                                    <option value="READY">
                                                        READY
                                                    </option>
                                                    <option value="OUT_FOR_DELIVERY">
                                                        OUT_FOR_DELIVERY
                                                    </option>
                                                    <option value="DELIVERED">
                                                        DELIVERED
                                                    </option>
                                                    <option value="CANCELLED">
                                                        CANCELLED
                                                    </option>
                                                </select>
                                                {updatingStatus[order.id] && (
                                                    <span className="updating-indicator">
                                                        Updating...
                                                    </span>
                                                )}
                                            </td>
                                            <td>
                                                <button
                                                    onClick={() =>
                                                        handleStatusUpdate(
                                                            order.id,
                                                            "CANCELLED"
                                                        )
                                                    }
                                                    className="cancel-btn"
                                                >
                                                    Cancel
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            )}

            {activeTab === "foods" && (
                <div className="admin-foods">
                    <button onClick={handleAddFood} className="add-food-btn">
                        + Add New Food
                    </button>

                    {loading ? (
                        <div className="loading">Loading foods...</div>
                    ) : foods.length === 0 ? (
                        <div className="no-data">No foods found</div>
                    ) : (
                        <div className="foods-list">
                            {foods.map((food) => (
                                <div key={food.id} className="food-item">
                                    <div className="food-info">
                                        <h3>{food.name}</h3>
                                        <p>{food.description}</p>
                                        <div className="food-meta">
                                            <span>
                                                ${food.price.toFixed(2)}
                                            </span>
                                            <span>{food.category}</span>
                                            <span
                                                className={
                                                    food.isAvailable
                                                        ? "available"
                                                        : "unavailable"
                                                }
                                            >
                                                {food.isAvailable
                                                    ? "Available"
                                                    : "Unavailable"}
                                            </span>
                                        </div>
                                    </div>
                                    <div className="food-actions">
                                        <button
                                            onClick={() => handleEditFood(food)}
                                            className="edit-btn"
                                        >
                                            Edit
                                        </button>
                                        <button
                                            onClick={() =>
                                                handleDeleteFood(food.id)
                                            }
                                            className="delete-btn"
                                        >
                                            Delete
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            )}

            {showFoodForm && (
                <FoodForm
                    food={editingFood}
                    onSave={handleSaveFood}
                    onCancel={handleCancelFoodForm}
                />
            )}
        </div>
    );
};

export default Admin;
