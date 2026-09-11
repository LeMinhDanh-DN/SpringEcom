import React, { useState, useEffect, useRef } from "react";
import API from "../axios";
import "./ChatAssistant.css";

const ChatAssistant = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [input, setInput] = useState("");
  const [messages, setMessages] = useState([
    {
      id: "welcome",
      sender: "ai",
      text: "Xin chào! Tôi là Trợ lý mua sắm AI. Tôi có thể giúp gì cho bạn hôm nay?",
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    }
  ]);
  const [isLoading, setIsLoading] = useState(false);

  // Generate or retrieve session ID for conversation memory
  const [sessionId] = useState(() => {
    let id = sessionStorage.getItem("chat_session_id");
    if (!id) {
      id = Math.random().toString(36).substring(2, 15) + "_" + Date.now();
      sessionStorage.setItem("chat_session_id", id);
    }
    return id;
  });

  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    if (isOpen) {
      scrollToBottom();
    }
  }, [messages, isOpen]);

  const handleSend = async (e) => {
    e.preventDefault();
    if (!input.trim() || isLoading) return;

    const userMessage = {
      id: Date.now().toString(),
      sender: "user",
      text: input,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };

    setMessages((prev) => [...prev, userMessage]);
    const currentInput = input;
    setInput("");
    setIsLoading(true);

    try {
      // Use /ai/ask GET endpoint with question and sessionId
      const response = await API.get("/ai/ask", {
        params: {
          question: currentInput,
          sessionId: sessionId
        }
      });

      const aiMessage = {
        id: (Date.now() + 1).toString(),
        sender: "ai",
        text: response.data || "Xin lỗi, tôi không thể xử lý yêu cầu lúc này.",
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      };

      setMessages((prev) => [...prev, aiMessage]);
    } catch (error) {
      console.error("AI Chat Error:", error);
      const errorMessage = {
        id: (Date.now() + 1).toString(),
        sender: "ai",
        text: "Hệ thống AI đang bận hoặc có lỗi kết nối. Vui lòng thử lại sau!",
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        isError: true
      };
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleRecommendation = async (brand, category, label) => {
    if (isLoading) return;

    const userMessage = {
      id: Date.now().toString(),
      sender: "user",
      text: `Gợi ý: ${label}`,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };

    setMessages((prev) => [...prev, userMessage]);
    setIsLoading(true);

    try {
      // Use /ai/recommend POST endpoint with brand, category, and sessionId as query params
      const response = await API.post("/ai/recommend", null, {
        params: {
          brand: brand,
          category: category,
          sessionId: sessionId
        }
      });

      const aiMessage = {
        id: (Date.now() + 1).toString(),
        sender: "ai",
        text: response.data || "Xin lỗi, tôi không thể tìm thấy đề xuất lúc này.",
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      };

      setMessages((prev) => [...prev, aiMessage]);
    } catch (error) {
      console.error("AI Recommend Error:", error);
      const errorMessage = {
        id: (Date.now() + 1).toString(),
        sender: "ai",
        text: "Hệ thống AI đang bận hoặc có lỗi kết nối. Vui lòng thử lại sau!",
        time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        isError: true
      };
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const formatBoldText = (text) => {
    return text.replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>");
  };

  const renderMessageText = (text) => {
    if (!text) return "";
    const lines = text.split("\n");
    let inList = false;
    const renderedElements = [];
    let listItems = [];

    lines.forEach((line, index) => {
      const trimmedLine = line.trim();
      
      if (trimmedLine.startsWith("- ") || trimmedLine.startsWith("* ")) {
        if (!inList) {
          inList = true;
        }
        listItems.push(trimmedLine.substring(2));
      } else {
        if (inList) {
          renderedElements.push(
            <ul key={`list-${index}`} className="chat-msg-list">
              {listItems.map((item, idx) => (
                <li key={idx} dangerouslySetInnerHTML={{ __html: formatBoldText(item) }} />
              ))}
            </ul>
          );
          inList = false;
          listItems = [];
        }
        
        if (trimmedLine) {
          renderedElements.push(
            <p key={index} className="chat-msg-paragraph" dangerouslySetInnerHTML={{ __html: formatBoldText(trimmedLine) }} />
          );
        }
      }
    });

    if (inList && listItems.length > 0) {
      renderedElements.push(
        <ul key="list-end" className="chat-msg-list">
          {listItems.map((item, idx) => (
            <li key={idx} dangerouslySetInnerHTML={{ __html: formatBoldText(item) }} />
          ))}
        </ul>
      );
    }

    return renderedElements;
  };

  const suggestions = [
    { label: "Apple", brand: "Apple", category: "", icon: "🍏" },
    { label: "Điện thoại", brand: "", category: "Mobile", icon: "📱" },
    { label: "Uniqlo", brand: "Uniqlo", category: "", icon: "👕" },
    { label: "Quần áo", brand: "", category: "Fashion", icon: "👗" }
  ];

  return (
    <div className="chat-assistant-container">
      {/* Floating Action Button */}
      <button
        className={`chat-fab ${isOpen ? 'active' : ''}`}
        onClick={() => setIsOpen(!isOpen)}
        aria-label="Chat with AI Assistant"
      >
        {isOpen ? (
          <i className="bi bi-x-lg"></i>
        ) : (
          <div className="fab-content">
            <i className="bi bi-chat-dots-fill"></i>
            <span className="fab-badge">AI</span>
          </div>
        )}
      </button>

      {/* Chat Window Panel */}
      <div className={`chat-window ${isOpen ? 'open' : ''}`}>
        {/* Chat Header */}
        <div className="chat-header">
          <div className="header-info">
            <div className="ai-avatar">
              <i className="bi bi-robot"></i>
              <span className="online-dot"></span>
            </div>
            <div>
              <h5 className="mb-0">Shopping Assistant</h5>
              <small className="text-muted-custom">Hỗ trợ trực tuyến bởi AI</small>
            </div>
          </div>
          <button className="btn-close-custom" onClick={() => setIsOpen(false)}>
            <i className="bi bi-dash-lg"></i>
          </button>
        </div>

        {/* Chat Messages Log */}
        <div className="chat-messages">
          {messages.map((msg) => (
            <div key={msg.id} className={`message-wrapper ${msg.sender}`}>
              <div className="message-bubble">
                <div className="message-text">
                  {msg.sender === "ai" ? renderMessageText(msg.text) : msg.text}
                </div>
                <div className="message-time">{msg.time}</div>
              </div>
            </div>
          ))}

          {/* Loading Animation */}
          {isLoading && (
            <div className="message-wrapper ai">
              <div className="message-bubble typing-bubble">
                <div className="typing-indicator">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>

        {/* Quick Suggestion Chips */}
        <div className="chat-suggestions">
          {suggestions.map((sug, idx) => (
            <button
              key={idx}
              type="button"
              className="suggestion-chip"
              onClick={() => handleRecommendation(sug.brand, sug.category, sug.label)}
              disabled={isLoading}
            >
              <span className="chip-icon">{sug.icon}</span>
              <span className="chip-label">{sug.label}</span>
            </button>
          ))}
        </div>

        {/* Input Bar Form */}
        <form className="chat-input-area" onSubmit={handleSend}>
          <input
            type="text"
            className="form-control chat-input"
            placeholder="Hỏi về sản phẩm, giá cả..."
            value={input}
            onChange={(e) => setInput(e.target.value)}
            disabled={isLoading}
          />
          <button
            type="submit"
            className="btn btn-send-custom"
            disabled={!input.trim() || isLoading}
          >
            <i className="bi bi-send-fill"></i>
          </button>
        </form>
      </div>
    </div>
  );
};

export default ChatAssistant;
