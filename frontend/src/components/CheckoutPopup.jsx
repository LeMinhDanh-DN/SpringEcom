import axios from '../axios';
import React, { useState, useEffect, useContext } from 'react';
import { Modal, Button, Form, Toast, ToastContainer } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import unplugged from "../assets/unplugged.png";
import AuthContext from '../Context/AuthContext';
import AppContext from '../Context/Context';

const CheckoutPopup = ({ show, handleClose, cartItems, totalPrice }) => {
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);
  const { clearCart } = useContext(AppContext);

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [number, setNumber] = useState('');
  const [shippingAddress, setShippingAddress] = useState('');
  const [payMethod, setPayMethod] = useState('COD');
  const [voucherCode, setVoucherCode] = useState('');
  const [note, setNote] = useState('');

  const [validated, setValidated] = useState(false);
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState('');
  const [toastVariant, setToastVariant] = useState('success');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (user) {
      if (user.name) setName(user.name);
      else if (user.username) setName(user.username);
      if (user.email) setEmail(user.email);
    }
  }, [user]);

  const handleConfirm = async (event) => {
    event.preventDefault();
    const form = event.currentTarget;

    if (form.checkValidity() === false) {
      event.stopPropagation();
      setValidated(true);
      return;
    }

    setValidated(true);
    setIsSubmitting(true);

    const orderData = {
      customerName: name,
      email: email,
      number: number,
      shippingAddress: shippingAddress,
      payMethod: payMethod,
      voucherCode: voucherCode || null,
      note: note || null
    };

    try {
      const response = await axios.post('/orders/place', orderData);
      console.log('Order placed successfully:', response.data);

      setToastVariant('success');
      setToastMessage('Order placed successfully!');
      setShowToast(true);

      if (clearCart) {
        clearCart();
      }
      localStorage.removeItem('cart');

      setTimeout(() => {
        handleClose();
        navigate('/orders');
      }, 1500);
    } catch (error) {
      console.error('Error placing order:', error);
      setToastVariant('danger');
      const msg = error.response?.data?.message || error.response?.data || 'Failed to place order. Please try again.';
      setToastMessage(typeof msg === 'string' ? msg : 'Failed to place order. Please try again.');
      setShowToast(true);
    } finally {
      setIsSubmitting(false);
    }
  };

  const convertBase64ToDataURL = (base64String, mimeType = 'image/jpeg') => {
    if (!base64String) return unplugged;
    if (base64String.startsWith('data:')) return base64String;
    if (base64String.startsWith('http')) return base64String;
    return `data:${mimeType};base64,${base64String}`;
  };

  return (
    <>
      <Modal show={show} onHide={handleClose} centered size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Checkout</Modal.Title>
        </Modal.Header>
        <Form noValidate validated={validated} onSubmit={handleConfirm}>
          <Modal.Body>
            <div className="checkout-items mb-4">
              <h6 className="fw-bold mb-3">Order Items</h6>
              {cartItems.map((item) => (
                <div key={item.id} className="d-flex mb-3 border-bottom pb-3">
                  <img
                    src={item.imageUrl || convertBase64ToDataURL(item.imageData)}
                    alt={item.productName || item.name}
                    className="me-3 rounded"
                    style={{ width: '60px', height: '60px', objectFit: 'cover' }}
                    onError={(e) => {
                      e.target.src = unplugged;
                    }}
                  />
                  <div className="flex-grow-1">
                    <h6 className="mb-1">{item.productName || item.name}</h6>
                    <p className="mb-1 small">Quantity: {item.quantity}</p>
                    <p className="mb-0 small">Price: {(item.price * item.quantity).toLocaleString('vi-VN')} VNĐ</p>
                  </div>
                </div>
              ))}

              <div className="text-end my-3">
                <h5 className="fw-bold">Total: {totalPrice.toLocaleString('vi-VN')} VNĐ</h5>
              </div>

              <hr />

              <h6 className="fw-bold mb-3">Customer Information</h6>

              <div className="row">
                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Customer Name</Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="Enter your name"
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      required
                    />
                    <Form.Control.Feedback type="invalid">
                      Please provide your name.
                    </Form.Control.Feedback>
                  </Form.Group>
                </div>

                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Email Address</Form.Label>
                    <Form.Control
                      type="email"
                      placeholder="Enter your email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      required
                    />
                    <Form.Control.Feedback type="invalid">
                      Please provide a valid email address.
                    </Form.Control.Feedback>
                  </Form.Group>
                </div>
              </div>

              <div className="row">
                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Phone Number</Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="Enter phone number"
                      value={number}
                      onChange={(e) => setNumber(e.target.value)}
                      required
                    />
                    <Form.Control.Feedback type="invalid">
                      Please provide a phone number.
                    </Form.Control.Feedback>
                  </Form.Group>
                </div>

                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Payment Method</Form.Label>
                    <Form.Select
                      value={payMethod}
                      onChange={(e) => setPayMethod(e.target.value)}
                      required
                    >
                      <option value="COD">Cash on Delivery (COD)</option>
                      <option value="CREDIT_CARD">Credit / Debit Card</option>
                      <option value="BANK_TRANSFER">Bank Transfer</option>
                    </Form.Select>
                  </Form.Group>
                </div>
              </div>

              <Form.Group className="mb-3">
                <Form.Label>Shipping Address</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={2}
                  placeholder="Enter detailed shipping address"
                  value={shippingAddress}
                  onChange={(e) => setShippingAddress(e.target.value)}
                  required
                />
                <Form.Control.Feedback type="invalid">
                  Please provide a shipping address.
                </Form.Control.Feedback>
              </Form.Group>

              <div className="row">
                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Voucher Code (Optional)</Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="Enter voucher code if any"
                      value={voucherCode}
                      onChange={(e) => setVoucherCode(e.target.value)}
                    />
                  </Form.Group>
                </div>

                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Order Note (Optional)</Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="Special instructions for delivery"
                      value={note}
                      onChange={(e) => setNote(e.target.value)}
                    />
                  </Form.Group>
                </div>
              </div>
            </div>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleClose} disabled={isSubmitting}>
              Close
            </Button>
            <Button variant="primary" type="submit" disabled={isSubmitting}>
              {isSubmitting ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                  Processing...
                </>
              ) : 'Confirm Purchase'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      <ToastContainer position="top-end" className="p-3" style={{ zIndex: 1070 }}>
        <Toast
          show={showToast}
          onClose={() => setShowToast(false)}
          delay={4000}
          autohide
          bg={toastVariant}
        >
          <Toast.Header closeButton>
            <strong className="me-auto">Order Status</strong>
          </Toast.Header>
          <Toast.Body className={toastVariant === 'success' ? 'text-white' : ''}>
            {toastMessage}
          </Toast.Body>
        </Toast>
      </ToastContainer>
    </>
  );
};

export default CheckoutPopup;