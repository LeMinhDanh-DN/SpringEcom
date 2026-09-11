import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import AuthContext from "../Context/AuthContext";
import { toast } from "react-toastify";
import "./Login.css";

const Login = () => {
  const navigate = useNavigate();
  const { login, register, isLoading } = useContext(AuthContext); // Thêm register vào đây
  const [isLoginMode, setIsLoginMode] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");
  const [formData, setFormData] = useState({
    email: "",
    password: "",
    confirmPassword: "",
    name: "",
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setErrorMessage("");
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage("");

    if (!formData.email || !formData.password) {
      const msg = "Vui lòng nhập đầy đủ Email và Mật khẩu!";
      setErrorMessage(msg);
      toast.error(msg);
      return;
    }

    const result = await login(formData.email, formData.password);

    if (result.success) {
      toast.success("Đăng nhập thành công!");
      setTimeout(() => {
        navigate("/");
      }, 800);
    } else {
      setErrorMessage(result.message);
      toast.error(result.message);
    }
  };

  const handleRegisterSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage("");

    if (!formData.email || !formData.password || !formData.name) {
      const msg = "Vui lòng điền đầy đủ các thông tin!";
      setErrorMessage(msg);
      toast.error(msg);
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      const msg = "Mật khẩu xác nhận không khớp!";
      setErrorMessage(msg);
      toast.error(msg);
      return;
    }

    const result = await register({
      email: formData.email,
      password: formData.password,
      name: formData.name,
    });

    if (result.success) {
      toast.success("Đăng ký tài khoản thành công!");
      setTimeout(() => {
        navigate("/");
      }, 800);
    } else {
      setErrorMessage(result.message);
      toast.error(result.message);
    }
  };

  const handleToggleMode = () => {
    setIsLoginMode(!isLoginMode);
    setErrorMessage("");
    setFormData({
      email: "",
      password: "",
      confirmPassword: "",
      name: "",
    });
  };

  return (
    <div className="login-container mt-5 pt-5">
      <div className="login-box">
        <h1 className="text-center mb-4">
          {isLoginMode ? "Login" : "Register"}
        </h1>

        {errorMessage && (
          <div className="alert alert-danger py-2 px-3 mb-3 small" role="alert">
            <i className="bi bi-exclamation-triangle-fill me-2"></i>
            {errorMessage}
          </div>
        )}

        <form onSubmit={isLoginMode ? handleLoginSubmit : handleRegisterSubmit}>
          {!isLoginMode && (
            <div className="mb-3">
              <label htmlFor="name" className="form-label">
                Full Name
              </label>
              <input
                type="text"
                className="form-control"
                id="name"
                name="name"
                placeholder="Enter your full name"
                value={formData.name}
                onChange={handleInputChange}
              />
            </div>
          )}

          <div className="mb-3">
            <label htmlFor="email" className="form-label">
              Email Address
            </label>
            <input
              type="email"
              className="form-control"
              id="email"
              name="email"
              placeholder="Enter your email"
              value={formData.email}
              onChange={handleInputChange}
              required
            />
          </div>

          <div className="mb-3">
            <label htmlFor="password" className="form-label">
              Password
            </label>
            <input
              type="password"
              className="form-control"
              id="password"
              name="password"
              placeholder="Enter your password"
              value={formData.password}
              onChange={handleInputChange}
              required
            />
          </div>

          {!isLoginMode && (
            <div className="mb-3">
              <label htmlFor="confirmPassword" className="form-label">
                Confirm Password
              </label>
              <input
                type="password"
                className="form-control"
                id="confirmPassword"
                name="confirmPassword"
                placeholder="Confirm your password"
                value={formData.confirmPassword}
                onChange={handleInputChange}
              />
            </div>
          )}

          <button
            type="submit"
            className="btn btn-primary w-100 mb-3"
            disabled={isLoading}
          >
            {isLoading
              ? "Loading..."
              : isLoginMode
              ? "Login"
              : "Register"}
          </button>
        </form>

        <div className="text-center">
          <p>
            {isLoginMode
              ? "Don't have an account? "
              : "Already have an account? "}
            <button
              type="button"
              className="btn-link"
              onClick={handleToggleMode}
            >
              {isLoginMode ? "Register here" : "Login here"}
            </button>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;