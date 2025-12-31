# Habesha Bites - Frontend

React frontend application for the Habesha Bites restaurant web application.

## Features

- 🍽️ **Menu Browsing** - View all available food items with categories
- 🛒 **Shopping Cart** - Add items to cart and manage quantities
- 📦 **Order Management** - Place orders and track order status
- 👤 **User Authentication** - Register, login, and manage profile
- 🔐 **Admin Dashboard** - Manage orders and food items (admin only)
- 📱 **Responsive Design** - Works on desktop, tablet, and mobile

## Tech Stack

- **React 19** - UI library
- **React Router** - Routing
- **Axios** - HTTP client for API calls
- **Vite** - Build tool and dev server
- **CSS3** - Styling

## Getting Started

### Prerequisites

- Node.js (v16 or higher)
- npm or yarn
- Backend API running on `http://localhost:8080`

### Installation

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm run dev
```

The application will be available at `http://localhost:3000`

### Build for Production

```bash
npm run build
```

The built files will be in the `dist` directory.

## Project Structure

```
src/
├── components/       # Reusable UI components
│   ├── Navbar.jsx
│   └── FoodCard.jsx
├── pages/           # Page components
│   ├── Home.jsx
│   ├── Menu.jsx
│   ├── Cart.jsx
│   ├── Orders.jsx
│   ├── Profile.jsx
│   ├── Login.jsx
│   ├── Register.jsx
│   └── Admin.jsx
├── context/         # React Context providers
│   ├── AuthContext.jsx
│   └── CartContext.jsx
├── services/        # API service layer
│   └── api.js
├── App.jsx          # Main app component
└── main.jsx         # Entry point
```

## API Integration

The frontend communicates with the backend API at `http://localhost:8080/api`. All API calls are handled through the `services/api.js` file.

### Authentication

- JWT tokens are stored in `localStorage`
- Tokens are automatically included in API requests via axios interceptors
- On 401 errors, users are automatically logged out

### Available Endpoints

- **Auth**: `/api/auth/register`, `/api/auth/login`
- **Foods**: `/api/foods/*`
- **Orders**: `/api/orders/*`
- **Users**: `/api/users/*`

## Features Overview

### Public Pages
- **Home** (`/`) - Landing page with hero section
- **Menu** (`/menu`) - Browse all available food items

### Authenticated Pages
- **Cart** (`/cart`) - Shopping cart with checkout
- **Orders** (`/orders`) - View order history
- **Profile** (`/profile`) - User profile information

### Admin Pages
- **Admin Dashboard** (`/admin`) - Manage orders and food items

## Environment Variables

You can configure the API base URL by modifying `vite.config.js` or creating a `.env` file:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## Development

### Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## Notes

- Make sure the backend API is running before starting the frontend
- The app uses localStorage to persist cart items and authentication tokens
- Admin access requires a user with `ADMIN` role in the database
