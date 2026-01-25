import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { bookingAPI } from '../services/api';
import { useNavigate } from 'react-router-dom';
import './TableBooking.css';

const TableBooking = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [tables, setTables] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showBookingForm, setShowBookingForm] = useState(false);
  const [selectedTable, setSelectedTable] = useState(null);
  const [bookingForm, setBookingForm] = useState({
    bookingDateTime: '',
    numberOfGuests: 1,
    specialRequests: '',
  });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }
    fetchTables();
  }, [user, navigate]);

  const fetchTables = async () => {
    try {
      setLoading(true);
      const response = await bookingAPI.getTables();
      setTables(response.data);
    } catch (err) {
      setError('Failed to load tables. Please try again later.');
      console.error('Error fetching tables:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleBookTable = (table) => {
    setSelectedTable(table);
    // Set default booking time to 2 hours from now
    const defaultTime = new Date();
    defaultTime.setHours(defaultTime.getHours() + 2);
    defaultTime.setMinutes(0);
    const isoString = defaultTime.toISOString().slice(0, 16);
    setBookingForm({
      bookingDateTime: isoString,
      numberOfGuests: 1,
      specialRequests: '',
    });
    setShowBookingForm(true);
  };

  const handleSubmitBooking = async (e) => {
    e.preventDefault();
    if (!selectedTable) return;

    if (bookingForm.numberOfGuests > selectedTable.capacity) {
      setError(`This table can only seat ${selectedTable.capacity} guests.`);
      return;
    }

    try {
      setSubmitting(true);
      setError(null);
      await bookingAPI.createBooking({
        tableId: selectedTable.id,
        bookingDateTime: bookingForm.bookingDateTime,
        numberOfGuests: bookingForm.numberOfGuests,
        specialRequests: bookingForm.specialRequests,
      });
      // Close form and refresh tables to show updated status
      setShowBookingForm(false);
      setSelectedTable(null);
      setError(null);
      // Refresh tables to update availability status
      await fetchTables();
      alert('Table booked successfully!');
    } catch (err) {
      const errorMessage = err.response?.data?.message || 
                          err.response?.data?.error || 
                          err.message || 
                          'Failed to book table. Please try again.';
      setError(errorMessage);
      console.error('Error booking table:', err);
    } finally {
      setSubmitting(false);
    }
  };

  const formatDateTime = (dateTimeString) => {
    const date = new Date(dateTimeString);
    return date.toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: 'numeric',
      minute: '2-digit',
      hour12: true,
    });
  };

  const getAvailableCount = () => {
    return tables.filter((table) => table.isAvailable).length;
  };

  if (loading) {
    return (
      <div className="table-booking-page">
        <div className="loading">Loading tables...</div>
      </div>
    );
  }

  return (
    <div className="table-booking-page">
      <div className="table-booking-header">
        <h1>Table Booking</h1>
        <p>Reserve a table for your dining experience</p>
        <div className="availability-summary">
          <span className="available-count">{getAvailableCount()}</span> tables available out of{' '}
          <span className="total-count">{tables.length}</span>
        </div>
      </div>

      {error && !showBookingForm && (
        <div className="error-message">{error}</div>
      )}

      {showBookingForm && selectedTable && (
        <div className="booking-form-modal">
          <div className="booking-form-container">
            <h2>Book Table {selectedTable.tableNumber}</h2>
            <p className="table-info">
              Capacity: {selectedTable.capacity} guests
            </p>
            {error && <div className="error-message">{error}</div>}
            <form onSubmit={handleSubmitBooking}>
              <div className="form-group">
                <label htmlFor="bookingDateTime">Date & Time</label>
                <input
                  type="datetime-local"
                  id="bookingDateTime"
                  value={bookingForm.bookingDateTime}
                  onChange={(e) =>
                    setBookingForm({ ...bookingForm, bookingDateTime: e.target.value })
                  }
                  required
                  min={new Date().toISOString().slice(0, 16)}
                />
              </div>
              <div className="form-group">
                <label htmlFor="numberOfGuests">Number of Guests</label>
                <input
                  type="number"
                  id="numberOfGuests"
                  min="1"
                  max={selectedTable.capacity}
                  value={bookingForm.numberOfGuests}
                  onChange={(e) =>
                    setBookingForm({ ...bookingForm, numberOfGuests: parseInt(e.target.value) })
                  }
                  required
                />
                <small>Maximum: {selectedTable.capacity} guests</small>
              </div>
              <div className="form-group">
                <label htmlFor="specialRequests">Special Requests (Optional)</label>
                <textarea
                  id="specialRequests"
                  rows="3"
                  value={bookingForm.specialRequests}
                  onChange={(e) =>
                    setBookingForm({ ...bookingForm, specialRequests: e.target.value })
                  }
                  placeholder="Any special requests or dietary requirements..."
                />
              </div>
              <div className="form-actions">
                <button
                  type="button"
                  onClick={() => {
                    setShowBookingForm(false);
                    setSelectedTable(null);
                    setError(null);
                  }}
                  className="btn-cancel"
                >
                  Cancel
                </button>
                <button type="submit" disabled={submitting} className="btn-submit">
                  {submitting ? 'Booking...' : 'Confirm Booking'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div className="tables-grid">
        {tables.map((table) => (
          <div
            key={table.id}
            className={`table-card ${table.isAvailable ? 'available' : 'booked'}`}
          >
            <div className="table-header">
              <h3>Table {table.tableNumber}</h3>
              <span className={`status-badge ${table.isAvailable ? 'available' : 'booked'}`}>
                {table.isAvailable ? 'Available' : 'Booked'}
              </span>
            </div>
            <div className="table-details">
              <p className="capacity">
                <strong>Capacity:</strong> {table.capacity} guests
              </p>
              {table.upcomingBookings && table.upcomingBookings.length > 0 && (
                <div className="upcoming-bookings">
                  <strong>Upcoming Bookings:</strong>
                  <ul>
                    {table.upcomingBookings.map((booking) => (
                      <li key={booking.bookingId}>
                        {formatDateTime(booking.bookingDateTime)} - {booking.numberOfGuests} guests
                        {booking.userName && ` (${booking.userName})`}
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
            <button
              onClick={() => handleBookTable(table)}
              disabled={!table.isAvailable}
              className={`book-btn ${table.isAvailable ? '' : 'disabled'}`}
            >
              {table.isAvailable ? 'Book This Table' : 'Not Available'}
            </button>
          </div>
        ))}
      </div>

      {tables.length === 0 && (
        <div className="no-tables">No tables available at the moment.</div>
      )}
    </div>
  );
};

export default TableBooking;
