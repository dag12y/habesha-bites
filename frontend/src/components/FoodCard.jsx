import { useCart } from '../context/CartContext';
import './FoodCard.css';

const FoodCard = ({ food }) => {
  const { addToCart } = useCart();

  const handleAddToCart = () => {
    addToCart(food, 1);
  };

  return (
    <div className="food-card">
      <div className="food-image-container">
        {food.imageUrl ? (
          <img src={food.imageUrl} alt={food.name} className="food-image" />
        ) : (
          <div className="food-image-placeholder">
            <span>🍽️</span>
          </div>
        )}
        {!food.isAvailable && (
          <div className="food-unavailable">Unavailable</div>
        )}
      </div>
      <div className="food-info">
        <h3 className="food-name">{food.name}</h3>
        <p className="food-description">{food.description}</p>
        <div className="food-footer">
          <span className="food-price">${food.price.toFixed(2)}</span>
          <span className="food-category">{food.category}</span>
        </div>
        <button
          onClick={handleAddToCart}
          disabled={!food.isAvailable}
          className="add-to-cart-btn"
        >
          {food.isAvailable ? 'Add to Cart' : 'Unavailable'}
        </button>
      </div>
    </div>
  );
};

export default FoodCard;

