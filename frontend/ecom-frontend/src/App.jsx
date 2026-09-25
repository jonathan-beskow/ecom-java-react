import { useState } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Home from "../src/components/home/Home";
import Products from "../src/components/products/Products";
import "./App.css";
import NavBar from "./components/shared/Navbar";
import About from "./components/About";
function App() {
  const [count, setCount] = useState(0);

  return (
    <BrowserRouter>
      <NavBar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/products" element={<Products />} />
        <Route path="/about" element={<About />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
