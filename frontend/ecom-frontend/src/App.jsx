import { useState } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Home from "../src/components/home/Home";
import Products from "../src/components/products/Products";
import "./App.css";
function App() {
  const [count, setCount] = useState(0);

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/products" element={<Products />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
