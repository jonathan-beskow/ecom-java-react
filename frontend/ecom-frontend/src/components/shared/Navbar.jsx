import { Badge } from "@mui/material";
import { FaShoppingCart, FaStore } from "react-icons/fa";
import { Link, useLocation } from "react-router-dom";

const NavBar = () => {
    const path = useLocation().pathname;
    return (
        <div className="h-17.5 bg-custom-gradient text-white z-50 flex items-center sticky top-0">
            <div className="lg:px-14 sm:px:8 px-4 w-full flex justify-between">
                <Link to="/" className="flex items-center text-2xl font-bold">
                    <FaStore className="mr-2 text-3xl" />
                    <span className="font-[Poppins] text-white">E-Shop</span>
                </Link>
                <ul className="flex text-slate-800 gap-4">
                    <li className="font-medium transition-all duration-150">
                        <Link className={`${path === "/" ? "text-white font-semibold" : "text-gray-200"
                            }`} to="/">
                            Home
                        </Link>
                    </li>

                    <li className="font-medium transition-all duration-150">
                        <Link className={`${path === "/products" ? "text-white font-semibold" : "text-gray-200"
                            }`} to="/products">
                            Products
                        </Link>
                    </li>
                    <li className="font-medium transition-all duration-150">
                        <Link className={`${path === "/about" ? "text-white font-semibold" : "text-gray-200"
                            }`} to="/about">
                            About
                        </Link>
                    </li>
                    <li className="font-medium transition-all duration-150">
                        <Link className={`${path === "/contact" ? "text-white font-semibold" : "text-gray-200"
                            }`} to="/contact">
                            Contact
                        </Link>
                    </li>
                    <li className="font-medium transition-all duration-150">
                        <Link className={`${path === "/cart" ? "text-white font-semibold" : "text-gray-200"
                            }`} to="/contact">
                            <Badge
                                showZero
                                badgeContent={0}
                                color="primary"
                                overlap="circular"
                                anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
                            >
                                <FaShoppingCart size={25}/>
                            </Badge>
                        </Link>
                    </li>
                </ul>
            </div>
        </div>
    )
}

export default NavBar;