import { useState, useEffect } from 'react';
import { foodAPI } from '../services/api';
import FoodCard from '../components/FoodCard';
import './Menu.css';

const Menu = () => {
  const [foods, setFoods] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    fetchFoods();
  }, []);

  useEffect(() => {
    if (selectedCategory === 'all') {
      fetchFoods();
    } else {
      fetchFoodsByCategory(selectedCategory);
    }
  }, [selectedCategory]);

  const fetchFoods = async () => {
    try {
      setLoading(true);
      const response = await foodAPI.getAvailable();
      setFoods(response.data);
      
      // Extract unique categories
      const uniqueCategories = [...new Set(response.data.map(food => food.category))];
      setCategories(uniqueCategories);
    } catch (err) {
      setError('Failed to load menu. Please try again later.');
      console.error('Error fetching foods:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchFoodsByCategory = async (category) => {
    try {
      setLoading(true);
      const response = await foodAPI.getByCategory(category);
      setFoods(response.data);
    } catch (err) {
      setError('Failed to load menu. Please try again later.');
      console.error('Error fetching foods by category:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="menu-page">
        <div className="loading">Loading menu...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="menu-page">
        <div className="error">{error}</div>
      </div>
    );
  }

  return (
    <div className="menu-page">
      <div className="menu-header">
        <h1>Our Menu</h1>
        <p>Discover our delicious Ethiopian dishes</p>
      </div>

      <div className="category-filter">
        <button
          className={`category-btn ${selectedCategory === 'all' ? 'active' : ''}`}
          onClick={() => setSelectedCategory('all')}
        >
          All
        </button>
        {categories.map((category) => (
          <button
            key={category}
            className={`category-btn ${selectedCategory === category ? 'active' : ''}`}
            onClick={() => setSelectedCategory(category)}
          >
            {category}
          </button>
        ))}
      </div>

      {foods.length === 0 ? (
        <div className="no-foods">No items available in this category.</div>
      ) : (
        <div className="foods-grid">
          {foods.map((food) => (
            <FoodCard key={food.id} food={food} />
          ))}
        </div>
      )}
    </div>
  );
};

export default Menu;

