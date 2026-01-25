import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Home.css';

const Home = () => {
  const { isAuthenticated } = useAuth();
console.log('home page called!@!!!!!!!!!');
  return (
    <div className="home">
      
      <section className="hero">
        <div className="hero-content">
          <h1 className="hero-title">Welcome to Habesha Bites</h1>
          <p className="hero-subtitle">
            Experience the authentic flavors of Ethiopian cuisine
          </p>
          <div className="hero-buttons">
            <Link to="/menu" className="btn btn-primary">
              View Menu
            </Link>
            {isAuthenticated && (
              <Link to="/book-table" className="btn btn-secondary">
                Book a Table
              </Link>
            )}
            {!isAuthenticated && (
              <Link to="/register" className="btn btn-secondary">
                Get Started
              </Link>
            )}
          </div>
        </div>
      </section>

      <section className="features">
        <div className="container">
          <h2 className="section-title">Why Choose Habesha Bites?</h2>
          <div className="features-grid">
            <div className="feature-card">
              <div className="feature-icon">🍽️</div>
              <h3>Authentic Cuisine</h3>
              <p>Traditional Ethiopian recipes passed down through generations</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">🚚</div>
              <h3>Fast Delivery</h3>
              <p>Quick and reliable delivery to your doorstep</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">⭐</div>
              <h3>Quality Ingredients</h3>
              <p>Fresh, high-quality ingredients in every dish</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Home;

