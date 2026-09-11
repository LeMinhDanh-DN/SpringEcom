import React, { useContext, useState, useEffect } from "react";
import AppContext from "../Context/Context";
import CheckoutPopup from "./CheckoutPopup";
import { Button } from 'react-bootstrap';

const Cart = () => {
  const { cart, removeFromCart, updateCartItemQuantity, fetchCart } = useContext(AppContext);
  const [totalPrice, setTotalPrice] = useState(0);
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    fetchCart();
  }, []);

  useEffect(() => {
    const total = cart.reduce(
      (acc, item) => acc + (item.totalPrice ? item.totalPrice : item.price * item.quantity),
      0
    );
    setTotalPrice(total);
  }, [cart]);

  const handleIncreaseQuantity = (item) => {
    const currentQty = item.quantity;
    updateCartItemQuantity(item.id, currentQty + 1);
  };

  const handleDecreaseQuantity = (item) => {
    const currentQty = item.quantity;
    if (currentQty > 1) {
      updateCartItemQuantity(item.id, currentQty - 1);
    } else {
      removeFromCart(item.id);
    }
  };

  const handleRemoveFromCart = (itemId) => {
    removeFromCart(itemId);
  };

  const convertBase64ToDataURL = (base64String, mimeType = 'image/jpeg') => {
    const fallbackImage = "/fallback-image.jpg";
    if (!base64String) return fallbackImage;
    if (base64String.startsWith("data:")) return base64String;
    if (base64String.startsWith("http")) return base64String;
    return `data:${mimeType};base64,${base64String}`;
  };

  return (
    <div className="container mt-5 pt-5">
      <div className="row justify-content-center">
        <div className="col-md-10">
          <div className="card shadow">
            <div className="card-header bg-white">
              <h4 className="mb-0">Shopping Cart</h4>
            </div>
            <div className="card-body">
              {cart.length === 0 ? (
                <div className="text-center py-5">
                  <i className="bi bi-cart-x fs-1 text-muted"></i>
                  <h5 className="mt-3">Your cart is empty</h5>
                  <a href="/" className="btn btn-primary mt-3">Continue Shopping</a>
                </div>
              ) : (
                <>
                  <div className="table-responsive">
                    <table className="table table-hover align-middle">
                      <thead>
                        <tr>
                          <th>Product</th>
                          <th>Price</th>
                          <th>Quantity</th>
                          <th>Total</th>
                          <th>Action</th>
                        </tr>
                      </thead>
                      <tbody>
                        {cart.map((item) => {
                          const name = item.productName || item.name;
                          const price = item.price || (item.totalPrice ? (item.totalPrice / item.quantity) : 0);
                          const itemTotal = item.totalPrice !== undefined ? item.totalPrice : price * item.quantity;

                          return (
                            <tr key={item.id}>
                              <td>
                                <div className="d-flex align-items-center">
                                  <img
                                    src={item.imageUrl || convertBase64ToDataURL(item.imageData)}
                                    alt={name}
                                    className="rounded me-3"
                                    width="70"
                                    height="70"
                                    style={{ objectFit: "cover" }}
                                  />
                                  <div>
                                    <h6 className="mb-0">{name}</h6>
                                    {item.brand && <small className="text-muted">{item.brand}</small>}
                                  </div>
                                </div>
                              </td>
                              <td>{Number(price).toLocaleString('vi-VN')} VNĐ</td>
                              <td>
                                <div className="input-group input-group-sm" style={{ width: "120px" }}>
                                  <button
                                    className="btn btn-outline-secondary"
                                    type="button"
                                    onClick={() => handleDecreaseQuantity(item)}
                                  >
                                    <i className="bi bi-dash"></i>
                                  </button>
                                  <input
                                    type="text"
                                    className="form-control text-center"
                                    value={item.quantity}
                                    readOnly
                                  />
                                  <button
                                    className="btn btn-outline-secondary"
                                    type="button"
                                    onClick={() => handleIncreaseQuantity(item)}
                                  >
                                    <i className="bi bi-plus"></i>
                                  </button>
                                </div>
                              </td>
                              <td className="fw-bold">{Number(itemTotal).toLocaleString('vi-VN')} VNĐ</td>
                              <td>
                                <button
                                  className="btn btn-sm btn-outline-danger"
                                  onClick={() => handleRemoveFromCart(item.id)}
                                >
                                  <i className="bi bi-trash"></i>
                                </button>
                              </td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </div>

                  <div className="card mt-3">
                    <div className="card-body">
                      <div className="d-flex justify-content-between align-items-center">
                        <h5 className="mb-0">Total:</h5>
                        <h5 className="mb-0">{Number(totalPrice).toLocaleString('vi-VN')} VNĐ</h5>
                      </div>
                    </div>
                  </div>

                  <div className="d-grid mt-4">
                    <Button
                      variant="primary"
                      size="lg"
                      onClick={() => setShowModal(true)}
                    >
                      Proceed to Checkout
                    </Button>
                  </div>
                </>
              )}
            </div>
          </div>
        </div>
      </div>

      <CheckoutPopup
        show={showModal}
        handleClose={() => setShowModal(false)}
        cartItems={cart}
        totalPrice={totalPrice}
      />
    </div>
  );
};

export default Cart;
