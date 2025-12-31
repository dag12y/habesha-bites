import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { userAPI } from '../services/api';
import './Profile.css';

const Profile = () => {
  const { user: authUser, logout } = useAuth();
  const [user, setUser] = useState(authUser);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      setLoading(true);
      const response = await userAPI.getProfile();
      setUser(response.data);
    } catch (error) {
      console.error('Error fetching profile:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="profile-page">
        <div className="loading">Loading profile...</div>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="profile-page">
        <div className="error">Failed to load profile</div>
      </div>
    );
  }

  return (
    <div className="profile-page">
      <h1>My Profile</h1>
      
      <div className="profile-card">
        <div className="profile-header">
          <div className="profile-avatar">
            {user.fullName.charAt(0).toUpperCase()}
          </div>
          <h2>{user.fullName}</h2>
          <span className={`role-badge ${user.role.toLowerCase()}`}>
            {user.role}
          </span>
        </div>

        <div className="profile-info">
          <div className="info-item">
            <label>Email</label>
            <p>{user.email}</p>
          </div>
          
          <div className="info-item">
            <label>Phone Number</label>
            <p>{user.phone}</p>
          </div>
          
          <div className="info-item">
            <label>Member Since</label>
            <p>{new Date(user.createdAt).toLocaleDateString()}</p>
          </div>
        </div>

        <button onClick={logout} className="logout-btn">
          Logout
        </button>
      </div>
    </div>
  );
};

export default Profile;

