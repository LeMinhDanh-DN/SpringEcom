import axios from "../axios";
import { useState, useEffect, createContext } from "react";

const AppContext = createContext({
  data: [],
  isError: "",
  cart: [],
  addToCart: (product, quantity) => {},
  updateCartItemQuantity: (itemId, quantity) => {},
  removeFromCart: (itemId) => {},
  refreshData: () => {},
  clearCart: () => {},
  fetchCart: () => {}
});

export const AppProvider = ({ children }) => {
  const [data, setData] = useState([]);
  const [isError, setIsError] = useState("");
  const [cart, setCart] = useState(JSON.parse(localStorage.getItem('cart')) || []);

  const token = localStorage.getItem("authToken");

  const fetchCart = async () => {
    const currentToken = localStorage.getItem("authToken");
    if (!currentToken) return;

    try {
      const response = await axios.get('/cart');
      if (response.data && response.data.items) {
        setCart(response.data.items);
        localStorage.setItem('cart', JSON.stringify(response.data.items));
      }
    } catch (error) {
      console.error("Error fetching cart from backend:", error);
    }
  };

  const addToCart = async (product, quantityToAdd = 1) => {
    const currentToken = localStorage.getItem("authToken");
    if (currentToken) {
      try {
        const response = await axios.post('/cart/items', {
          productId: product.id,
          quantity: quantityToAdd
        });
        if (response.data && response.data.items) {
          setCart(response.data.items);
          localStorage.setItem('cart', JSON.stringify(response.data.items));
          return;
        }
      } catch (error) {
        console.error("Error adding item to cart via API:", error);
      }
    }

    // Local state fallback
    const existingProductIndex = cart.findIndex((item) => (item.productId || item.id) === product.id);
    let updatedCart;
    if (existingProductIndex !== -1) {
      updatedCart = cart.map((item, index) =>
        index === existingProductIndex
          ? { ...item, quantity: item.quantity + quantityToAdd }
          : item
      );
    } else {
      updatedCart = [...cart, { ...product, quantity: quantityToAdd }];
    }
    setCart(updatedCart);
    localStorage.setItem('cart', JSON.stringify(updatedCart));
  };

  const updateCartItemQuantity = async (itemId, newQuantity) => {
    const currentToken = localStorage.getItem("authToken");
    if (currentToken) {
      try {
        const response = await axios.put(`/cart/items/${itemId}`, {
          quantity: newQuantity
        });
        if (response.data && response.data.items) {
          setCart(response.data.items);
          localStorage.setItem('cart', JSON.stringify(response.data.items));
          return;
        }
      } catch (error) {
        console.error("Error updating cart quantity via API:", error);
      }
    }

    // Local state fallback
    const updatedCart = cart.map((item) =>
      item.id === itemId
        ? { ...item, quantity: newQuantity }
        : item
    );
    setCart(updatedCart);
    localStorage.setItem('cart', JSON.stringify(updatedCart));
  };

  const removeFromCart = async (itemId) => {
    const currentToken = localStorage.getItem("authToken");
    if (currentToken) {
      try {
        const response = await axios.delete(`/cart/items/${itemId}`);
        if (response.data && response.data.items) {
          setCart(response.data.items);
          localStorage.setItem('cart', JSON.stringify(response.data.items));
          return;
        }
      } catch (error) {
        console.error("Error removing cart item via API:", error);
      }
    }

    const updatedCart = cart.filter((item) => item.id !== itemId);
    setCart(updatedCart);
    localStorage.setItem('cart', JSON.stringify(updatedCart));
  };

  const clearCart = async () => {
    const currentToken = localStorage.getItem("authToken");
    if (currentToken) {
      try {
        await axios.delete('/cart');
      } catch (error) {
        console.error("Error clearing cart via API:", error);
      }
    }
    setCart([]);
    localStorage.removeItem('cart');
  };

  const refreshData = async () => {
    try {
      const response = await axios.get('/products');
      setData(response.data);
    } catch (error) {
      setIsError(error.message);
    }
  };

  useEffect(() => {
    refreshData();
    if (localStorage.getItem("authToken")) {
      fetchCart();
    }
  }, []);

  return (
    <AppContext.Provider value={{ 
      data, 
      isError, 
      cart, 
      addToCart, 
      updateCartItemQuantity,
      removeFromCart, 
      refreshData, 
      clearCart,
      fetchCart 
    }}>
      {children}
    </AppContext.Provider>
  );
};

export default AppContext;