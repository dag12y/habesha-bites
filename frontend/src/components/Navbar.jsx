import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import './Navbar.css';

const Navbar = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const { getTotalItems } = useCart();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-logo">
          <span className="logo-text">🍽️ Habesha Bites</span>
        </Link>

        <ul className="navbar-menu">
          <li>
            <Link to="/" className="navbar-link">
              Home
            </Link>
          </li>
          <li>
            <Link to="/menu" className="navbar-link">
              Menu
            </Link>
          </li>
          
          {isAuthenticated ? (
            <>
              <li>
                <Link to="/cart" className="navbar-link cart-link">
                  Cart
                  {getTotalItems() > 0 && (
                    <span className="cart-badge">{getTotalItems()}</span>
                  )}
                </Link>
              </li>
              <li>
                <Link to="/orders" className="navbar-link">
                  My Orders
                </Link>
              </li>
              {user?.role === 'ADMIN' && (
                <li>
                  <Link to="/admin" className="navbar-link">
                    Admin
                  </Link>
                </li>
              )}
              <li className="navbar-user">
                <Link to="/profile" className="navbar-link">
                  {user?.fullName || 'Profile'}
                </Link>
              </li>
              <li>
                <button onClick={handleLogout} className="navbar-button">
                  Logout
                </button>
              </li>
            </>
          ) : (
            <>
              <li>
                <Link to="/login" className="navbar-link">
                  Login
                </Link>
              </li>
              <li>
                <Link to="/register" className="navbar-button">
                  Sign Up
                </Link>
              </li>
            </>
          )}
        </ul>
      </div>
    </nav>
  );
};

export default Navbar;

