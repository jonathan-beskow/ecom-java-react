import { useEffect } from "react";
import { FaExclamationTriangle } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import { fetchProducts } from "../../store/actions";
import Loader from "../shared/Loader";
import ProductCard from "../shared/ProductCard";
import HeroBanner from "./HeroBanner";

const Home = () => {

    const dispatch = useDispatch();
    const { products } = useSelector((state) => state.products);
    const { isLoading, errorMessage } = useSelector((state) => state.errors);

    useEffect(() => {
        dispatch(fetchProducts());
    }, [dispatch])

    return (
        <div className="lg:px-14 sm:px-8 px-4">
            <div className="py-6">
                <HeroBanner />
            </div>
            <div className="py-5">
                <div className="flex flex-col justify-center items-center space-y-2">
                    <h1 className="text-slate-800text-4xl font-bold">Products</h1>
                    <span>Discouver our handpicked selection of top-rated items just for you!</span>

                </div>

                {isLoading ? (
                    <Loader />
                ) : errorMessage ? (
                    <div>
                        <FaExclamationTriangle className="text-slate-800 text-3xl mr-2s" />
                        <span className="text-slate-800 text-lg font-medium">
                            {errorMessage}
                        </span>
                    </div>

                ) : (
                    <div className="pb-6 pt-14 grid 2xl:grid-cols-4 lg:grid-cols-3 sm:grid-cols-2 gap-x-6">
                        {products &&
                            products?.slice(0, 8).map((item, i) => <ProductCard key={i} {...item} />)}
                    </div>
                )}


            </div>

        </div >
    )
}
export default Home;