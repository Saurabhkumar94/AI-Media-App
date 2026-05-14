import React, { useState } from 'react';
import axios from 'axios';
import { Upload, MessageSquare, PlayCircle, FileText, Send } from 'lucide-react';

function App() {
  const [file, setFile] = useState(null);
  const [response, setResponse] = useState(null);
  const [loading, setLoading] = useState(false);
  const [chatInput, setChatInput] = useState("");
  const [chatHistory, setChatHistory] = useState([]);

  // File Upload Handler 
  const handleUpload = async () => {
    if (!file) return alert("Pehle file select karein!");
    const formData = new FormData();
    formData.append('file', file);
    setLoading(true);

    try {
      // Backend Connection (Spring Boot) [cite: 12]
      const res = await axios.post("http://localhost:8081/api/files/upload", formData);
      setResponse(res.data);
      // Summary set karna jab upload ho jaye 
    } catch (err) {
      alert("Error: Backend (8081) check karein ya CORS allow karein.");
    }
    setLoading(false);
  };

  // Chatbot Question Handler 
  const handleAskQuestion = async () => {
    if (!chatInput) return;
    const newChat = { role: 'user', text: chatInput };
    setChatHistory([...chatHistory, newChat]);
    
    // Yahan hum chatbot API call karenge (Future Step)
    setChatInput("");
  };

  return (
    <div style={{ padding: '30px', fontFamily: '"Segoe UI", Tahoma, Geneva, Verdana, sans-serif', maxWidth: '900px', margin: 'auto', backgroundColor: '#f0f2f5', minHeight: '100vh' }}>
      <h1 style={{ textAlign: 'center', color: '#1a73e8', marginBottom: '30px' }}>AI Multimedia Assistant</h1>
      
      {/* 1. Upload Interface [cite: 22] */}
      <div style={{ border: '2px dashed #1a73e8', padding: '30px', textAlign: 'center', borderRadius: '15px', backgroundColor: '#fff', marginBottom: '25px', boxShadow: '0 4px 6px rgba(0,0,0,0.05)' }}>
        <FileText size={40} color="#1a73e8" style={{ marginBottom: '10px' }} />
        <p style={{ color: '#5f6368' }}>PDF, Audio, ya Video file upload karein [cite: 5]</p>
        <input 
          type="file" 
          onChange={(e) => setFile(e.target.files[0])} 
          style={{ display: 'block', margin: '15px auto', fontSize: '14px' }} 
        />
        <button 
          onClick={handleUpload} 
          disabled={loading} 
          style={{ display: 'inline-flex', alignItems: 'center', gap: '10px', padding: '12px 30px', backgroundColor: '#1a73e8', color: 'white', border: 'none', borderRadius: '25px', cursor: 'pointer', fontWeight: 'bold' }}
        >
          <Upload size={18} /> {loading ? "AI is Analyzing..." : "Upload & Analyze"}
        </button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: response ? '1fr 1fr' : '1fr', gap: '20px' }}>
        
        {/* 2. Chatbot UI  */}
        <div style={{ background: '#fff', padding: '20px', borderRadius: '15px', boxShadow: '0 4px 6px rgba(0,0,0,0.05)', height: '400px', display: 'flex', flexDirection: 'column' }}>
          <h3 style={{ borderBottom: '1px solid #eee', paddingBottom: '10px', marginTop: 0 }}><MessageSquare size={20} /> AI Chatbot</h3>
          <div style={{ flex: 1, overflowY: 'auto', padding: '10px', fontSize: '14px' }}>
            {chatHistory.length === 0 && <p style={{ color: '#999' }}>File upload karne ke baad sawal puchein...</p>}
            {chatHistory.map((chat, i) => (
              <div key={i} style={{ marginBottom: '10px', textAlign: chat.role === 'user' ? 'right' : 'left' }}>
                <span style={{ padding: '8px 12px', borderRadius: '15px', background: chat.role === 'user' ? '#e3f2fd' : '#f1f3f4', display: 'inline-block' }}>{chat.text}</span>
              </div>
            ))}
          </div>
          <div style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
            <input 
              value={chatInput} 
              onChange={(e) => setChatInput(e.target.value)}
              placeholder="Ask about the file..." 
              style={{ flex: 1, padding: '10px', borderRadius: '20px', border: '1px solid #ddd' }} 
            />
            <button onClick={handleAskQuestion} style={{ background: '#1a73e8', color: '#fff', border: 'none', borderRadius: '50%', width: '40px', height: '400px', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <Send size={18} />
            </button>
          </div>
        </div>

        {/* 3. Summary & Play Section  */}
        {response && (
          <div style={{ background: '#fff', padding: '20px', borderRadius: '15px', boxShadow: '0 4px 6px rgba(0,0,0,0.05)' }}>
            <h3 style={{ marginTop: 0 }}>AI Summary</h3>
            <p style={{ color: '#3c4043', lineHeight: '1.5', fontSize: '15px' }}>{response.summary}</p>
            
            {/* Timestamp & Play Button [cite: 8, 25, 26] */}
            <div style={{ marginTop: '20px', padding: '15px', background: '#fff9e6', borderRadius: '10px', border: '1px solid #ffeeba' }}>
              <h4 style={{ margin: '0 0 10px 0', fontSize: '14px' }}>Relevant Timestamps found:</h4>
              <button style={{ display: 'flex', alignItems: 'center', gap: '10px', padding: '8px 15px', background: '#d93025', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer', fontSize: '13px' }}>
                <PlayCircle size={16} /> Play from 00:45 [cite: 9]
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;