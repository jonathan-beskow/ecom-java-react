import { useState } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Home from "../src/components/home/Home";
import Products from "../src/components/products/Products";
import "./App.css";
import About from "./components/shared/About";
import Contact from "./components/shared/Contact";
import NavBar from "./components/shared/Navbar";
function App() {
  const [count, setCount] = useState(0);

  return (
    <BrowserRouter>
      <NavBar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/products" element={<Products />} />
        <Route path="/about" element={<About />} />
        <Route path="/contact" element={<Contact />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
