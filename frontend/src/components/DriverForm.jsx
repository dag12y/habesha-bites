import { useState, useEffect } from 'react';
import './DriverForm.css';

const DriverForm = ({ driver, onSave, onCancel }) => {
  const [formData, setFormData] = useState({ fullName: '', email: '', phone: '' });
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (driver) {
      setFormData({
        fullName: driver.fullName || '',
        email: driver.email || '',
        phone: driver.phone || '',
      });
    } else {
      setFormData({ fullName: '', email: '', phone: '' });
    }
  }, [driver]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors((prev) => ({ ...prev, [name]: '' }));
  };

  const validate = () => {
    const newErrors = {};
    if (!formData.fullName?.trim()) newErrors.fullName = 'Name is required';
    if (!formData.email?.trim()) newErrors.email = 'Email is required';
    if (!formData.phone?.trim()) newErrors.phone = 'Phone is required';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validate()) onSave(formData);
  };

  return (
    <div className="driver-form-overlay" onClick={onCancel}>
      <div className="driver-form-container" onClick={(e) => e.stopPropagation()}>
        <h2>{driver ? 'Edit Driver' : 'Add Driver'}</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Full Name *</label>
            <input
              name="fullName"
              value={formData.fullName}
              onChange={handleChange}
              placeholder="Driver full name"
              className={errors.fullName ? 'error' : ''}
            />
            {errors.fullName && <span className="error-message">{errors.fullName}</span>}
          </div>
          <div className="form-group">
            <label>Email *</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="driver@example.com"
              className={errors.email ? 'error' : ''}
            />
            {errors.email && <span className="error-message">{errors.email}</span>}
          </div>
          <div className="form-group">
            <label>Phone *</label>
            <input
              type="tel"
              name="phone"
              value={formData.phone}
              onChange={handleChange}
              placeholder="+251..."
              className={errors.phone ? 'error' : ''}
            />
            {errors.phone && <span className="error-message">{errors.phone}</span>}
          </div>
          <div className="form-actions">
            <button type="button" onClick={onCancel} className="cancel-btn">Cancel</button>
            <button type="submit" className="save-btn">{driver ? 'Update' : 'Add'} Driver</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default DriverForm;
