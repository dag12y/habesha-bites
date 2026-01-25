import { useState, useEffect } from "react";
import { useAuth } from "../context/AuthContext";
import { foodAPI, orderAPI, bookingAPI, driverAPI } from "../services/api";
import FoodForm from "../components/FoodForm";
import DriverForm from "../components/DriverForm";
import "./Admin.css";

const Admin = () => {
    const { isAdmin } = useAuth();
    const [activeTab, setActiveTab] = useState("orders");
    const [orders, setOrders] = useState([]);
    const [foods, setFoods] = useState([]);
    const [bookings, setBookings] = useState([]);
    const [tables, setTables] = useState([]);
    const [loading, setLoading] = useState(false);
    const [showFoodForm, setShowFoodForm] = useState(false);
    const [editingFood, setEditingFood] = useState(null);
    const [updatingStatus, setUpdatingStatus] = useState({});
    const [cancellingBooking, setCancellingBooking] = useState({});
    const [drivers, setDrivers] = useState([]);
    const [showDriverForm, setShowDriverForm] = useState(false);
    const [editingDriver, setEditingDriver] = useState(null);
    const [assignModalOrder, setAssignModalOrder] = useState(null);
    const [availableDriversForAssign, setAvailableDriversForAssign] = useState([]);
    const [selectedDriverIdForAssign, setSelectedDriverIdForAssign] = useState("");
    const [assigningInProgress, setAssigningInProgress] = useState(false);

    useEffect(() => {
        if (isAdmin()) {
            if (activeTab === "orders") fetchOrders();
            else if (activeTab === "foods") fetchFoods();
            else if (activeTab === "bookings") fetchBookings();
            else if (activeTab === "drivers") fetchDrivers();
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

    const fetchBookings = async () => {
        try {
            setLoading(true);
            const [bookingsRes, tablesRes] = await Promise.all([
                bookingAPI.getAllBookings(),
                bookingAPI.getTables(),
            ]);
            setBookings(bookingsRes.data);
            setTables(tablesRes.data);
        } catch (error) {
            console.error("Error fetching bookings:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleCancelBooking = async (bookingId) => {
        if (!window.confirm("Cancel this table booking?")) return;
        try {
            setCancellingBooking((p) => ({ ...p, [bookingId]: true }));
            await bookingAPI.cancelBooking(bookingId);
            fetchBookings();
        } catch (error) {
            console.error("Error cancelling booking:", error);
            alert(error.response?.data?.message || error.response?.data?.error || "Failed to cancel booking.");
        } finally {
            setCancellingBooking((p) => ({ ...p, [bookingId]: false }));
        }
    };

    const formatDateTime = (dt) => {
        if (!dt) return "—";
        const d = new Date(dt);
        return d.toLocaleString("en-US", { dateStyle: "medium", timeStyle: "short" });
    };

    const fetchDrivers = async () => {
        try {
            setLoading(true);
            const res = await driverAPI.getAll();
            setDrivers(res.data);
        } catch (e) {
            console.error("Error fetching drivers:", e);
        } finally {
            setLoading(false);
        }
    };

    const handleAddDriver = () => { setEditingDriver(null); setShowDriverForm(true); };
    const handleEditDriver = (d) => { setEditingDriver(d); setShowDriverForm(true); };
    const handleCancelDriverForm = () => { setShowDriverForm(false); setEditingDriver(null); };
    const handleSaveDriver = async (data) => {
        try {
            if (editingDriver) await driverAPI.update(editingDriver.id, data);
            else await driverAPI.create(data);
            setShowDriverForm(false);
            setEditingDriver(null);
            fetchDrivers();
        } catch (e) {
            console.error("Error saving driver:", e);
            alert(e.response?.data?.message || e.response?.data?.error || "Failed to save driver.");
        }
    };
    const handleDeleteDriver = async (id) => {
        if (!window.confirm("Remove this driver? They will be deactivated.")) return;
        try {
            await driverAPI.delete(id);
            fetchDrivers();
        } catch (e) {
            alert(e.response?.data?.message || e.response?.data?.error || "Failed to delete driver.");
        }
    };

    const openAssignModal = async (order) => {
        setAssignModalOrder(order);
        setSelectedDriverIdForAssign("");
        try {
            const res = await driverAPI.getAvailable();
            setAvailableDriversForAssign(res.data);
        } catch (e) {
            setAvailableDriversForAssign([]);
        }
    };
    const handleAssignConfirm = async () => {
        if (!assignModalOrder || !selectedDriverIdForAssign) {
            alert("Please select a driver.");
            return;
        }
        try {
            setAssigningInProgress(true);
            await orderAPI.assignDriver(assignModalOrder.id, Number(selectedDriverIdForAssign));
            setAssignModalOrder(null);
            fetchOrders();
        } catch (e) {
            alert(e.response?.data?.message || e.response?.data?.error || "Failed to assign driver.");
        } finally {
            setAssigningInProgress(false);
        }
    };
    const handleUnassignDriver = async (orderId) => {
        if (!window.confirm("Unassign driver from this order?")) return;
        try {
            await orderAPI.unassignDriver(orderId);
            fetchOrders();
        } catch (e) {
            alert(e.response?.data?.message || e.response?.data?.error || "Failed to unassign.");
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
                    className={activeTab === "bookings" ? "active" : ""}
                    onClick={() => setActiveTab("bookings")}
                >
                    Bookings
                </button>
                <button
                    className={activeTab === "drivers" ? "active" : ""}
                    onClick={() => setActiveTab("drivers")}
                >
                    Drivers
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
                                        <th>Delivery</th>
                                        <th>Driver</th>
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
                                            <td className="delivery-cell">
                                                <div className="delivery-address">
                                                    {order.deliveryAddress ||
                                                        "No address provided"}
                                                </div>
                                                <div className="delivery-phone">
                                                    {order.phoneNumber ||
                                                        "No phone"}
                                                </div>
                                            </td>
                                            <td className="driver-cell">
                                                {order.driverId ? (
                                                    <>
                                                        <div>{order.driverName}</div>
                                                        <div className="driver-phone">{order.driverPhone}</div>
                                                        {order.status !== "DELIVERED" &&
                                                         order.status !== "CANCELLED" && (
                                                            <button
                                                                type="button"
                                                                onClick={() => handleUnassignDriver(order.id)}
                                                                className="unassign-driver-btn"
                                                            >
                                                                Unassign
                                                            </button>
                                                        )}
                                                    </>
                                                ) : order.status !== "DELIVERED" &&
                                                  order.status !== "CANCELLED" ? (
                                                    <button
                                                        type="button"
                                                        onClick={() => openAssignModal(order)}
                                                        className="assign-driver-btn"
                                                    >
                                                        Assign driver
                                                    </button>
                                                ) : (
                                                    "—"
                                                )}
                                            </td>
                                            <td>
                                                {order.totalAmount.toFixed(2)}{" "}
                                                Birr
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

            {activeTab === "bookings" && (
                <div className="admin-bookings">
                    {(() => {
                        const now = new Date();
                        const oneDayAgo = new Date(now.getTime() - 24 * 60 * 60 * 1000);
                        const activeBookings = bookings.filter((b) => b.status !== "CANCELLED");
                        const upcoming = activeBookings.filter(
                            (b) => new Date(b.bookingDateTime) >= now
                        );
                        const newBookings = bookings.filter(
                            (b) => new Date(b.createdAt) > oneDayAgo && b.status !== "CANCELLED"
                        );
                        return (
                            <>
                                <div className="bookings-summary">
                                    <div className="summary-card">
                                        <span className="summary-value">{tables.length}</span>
                                        <span className="summary-label">Total Tables</span>
                                    </div>
                                    <div className="summary-card">
                                        <span className="summary-value">{activeBookings.length}</span>
                                        <span className="summary-label">Active Bookings</span>
                                    </div>
                                    <div className="summary-card">
                                        <span className="summary-value">{upcoming.length}</span>
                                        <span className="summary-label">Upcoming</span>
                                    </div>
                                </div>
                                {newBookings.length > 0 && (
                                    <div className="new-bookings-alert">
                                        You have <strong>{newBookings.length}</strong> new table
                                        booking(s) in the last 24 hours.
                                    </div>
                                )}
                                {loading ? (
                                    <div className="loading">Loading bookings...</div>
                                ) : bookings.length === 0 ? (
                                    <div className="no-data">No bookings yet</div>
                                ) : (
                                    <div className="bookings-table orders-table">
                                        <table>
                                            <thead>
                                                <tr>
                                                    <th>ID</th>
                                                    <th>Customer</th>
                                                    <th>Table</th>
                                                    <th>Date & Time</th>
                                                    <th>Guests</th>
                                                    <th>Status</th>
                                                    <th>Created</th>
                                                    <th>Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {bookings.map((b) => {
                                                    const isNew =
                                                        new Date(b.createdAt) > oneDayAgo &&
                                                        b.status !== "CANCELLED";
                                                    return (
                                                        <tr
                                                            key={b.id}
                                                            className={isNew ? "booking-row-new" : ""}
                                                        >
                                                            <td>#{b.id}</td>
                                                            <td>
                                                                <div>{b.userName}</div>
                                                                <div className="booking-email">
                                                                    {b.userEmail}
                                                                </div>
                                                            </td>
                                                            <td>Table {b.tableNumber}</td>
                                                            <td>{formatDateTime(b.bookingDateTime)}</td>
                                                            <td>{b.numberOfGuests}</td>
                                                            <td>
                                                                <span
                                                                    className={`booking-status status-${b.status.toLowerCase()}`}
                                                                >
                                                                    {b.status}
                                                                </span>
                                                                {isNew && (
                                                                    <span className="new-badge">
                                                                        New
                                                                    </span>
                                                                )}
                                                            </td>
                                                            <td>{formatDateTime(b.createdAt)}</td>
                                                            <td>
                                                                {b.status !== "CANCELLED" && (
                                                                    <button
                                                                        onClick={() =>
                                                                            handleCancelBooking(b.id)
                                                                        }
                                                                        disabled={cancellingBooking[b.id]}
                                                                        className="cancel-btn"
                                                                    >
                                                                        {cancellingBooking[b.id]
                                                                            ? "…"
                                                                            : "Cancel"}
                                                                    </button>
                                                                )}
                                                            </td>
                                                        </tr>
                                                    );
                                                })}
                                            </tbody>
                                        </table>
                                    </div>
                                )}
                            </>
                        );
                    })()}
                </div>
            )}

            {activeTab === "drivers" && (
                <div className="admin-drivers">
                    <button onClick={handleAddDriver} className="add-food-btn">+ Add Driver</button>
                    {loading ? (
                        <div className="loading">Loading drivers...</div>
                    ) : drivers.length === 0 ? (
                        <div className="no-data">No drivers. Add one to assign to orders.</div>
                    ) : (
                        <div className="drivers-table orders-table">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Name</th>
                                        <th>Email</th>
                                        <th>Phone</th>
                                        <th>Status</th>
                                        <th>Current delivery (if occupied)</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {drivers.map((d) => (
                                        <tr key={d.id}>
                                            <td>{d.fullName}</td>
                                            <td>{d.email}</td>
                                            <td>{d.phone}</td>
                                            <td>
                                                <span className={`driver-status status-${d.status?.toLowerCase()}`}>
                                                    {d.status || "—"}
                                                </span>
                                            </td>
                                            <td>
                                                {d.status === "OCCUPIED" && d.currentOrderId ? (
                                                    <>
                                                        <div><strong>Order #{d.currentOrderId}</strong></div>
                                                        <div>Location: {d.deliveryAddress || "—"}</div>
                                                        <div>Customer: {d.customerPhone || "—"}</div>
                                                    </>
                                                ) : "—"}
                                            </td>
                                            <td>
                                                <button onClick={() => handleEditDriver(d)} className="edit-btn">Edit</button>
                                                <button onClick={() => handleDeleteDriver(d.id)} className="delete-btn">Remove</button>
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
                                                {food.price.toFixed(2)} Birr
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

            {showDriverForm && (
                <DriverForm
                    driver={editingDriver}
                    onSave={handleSaveDriver}
                    onCancel={handleCancelDriverForm}
                />
            )}

            {assignModalOrder && (
                <div className="assign-driver-overlay" onClick={() => setAssignModalOrder(null)}>
                    <div className="assign-driver-modal" onClick={(e) => e.stopPropagation()}>
                        <h3>Assign driver to Order #{assignModalOrder.id}</h3>
                        <p className="assign-info">
                            The driver will receive: <strong>Location</strong> {assignModalOrder.deliveryAddress || "—"},
                            <strong> Customer phone</strong> {assignModalOrder.phoneNumber || "—"}
                        </p>
                        <div className="form-group">
                            <label>Choose driver</label>
                            <select
                                value={selectedDriverIdForAssign}
                                onChange={(e) => setSelectedDriverIdForAssign(e.target.value)}
                            >
                                <option value="">— Select —</option>
                                {availableDriversForAssign.map((d) => (
                                    <option key={d.id} value={d.id}>{d.fullName} ({d.phone})</option>
                                ))}
                            </select>
                        </div>
                        {availableDriversForAssign.length === 0 && (
                            <p className="no-available">No available drivers.</p>
                        )}
                        <div className="modal-actions">
                            <button type="button" onClick={() => setAssignModalOrder(null)} className="cancel-btn">Cancel</button>
                            <button
                                type="button"
                                onClick={handleAssignConfirm}
                                disabled={!selectedDriverIdForAssign || assigningInProgress}
                                className="save-btn"
                            >
                                {assigningInProgress ? "Assigning…" : "Assign & send info"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Admin;
