import { useEffect } from "react";
import { FaExclamationTriangle } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import { fetchCategories } from "../../store/actions";
import Loader from "../shared/Loader";
import Paginations from "../shared/Paginations";
import ProductCard from "../shared/ProductCard";
import useProductFilter from "../shared/useProductFilter";
import Filter from "./Filter";

const Products = () => {
  const { isLoading, errorMessage } = useSelector((state) => state.errors);
  const { products, categories, pagination } = useSelector(
    (state) => state.products,
  );

  const dispatch = useDispatch();
  useEffect(() => {
    dispatch(fetchCategories());
  }, [dispatch]);

  useProductFilter();

  return (
    <div className="lg:px-14 sm:px-8 px-4 py-14 2xl:2-[90%] 2xl:mx-auto">
      <Filter categories={categories ? categories : []} />
      {isLoading ? (
        <Loader text={"Products is loading"} />
      ) : errorMessage ? (
        <div className="flex justify-center items-center h-50">
          <FaExclamationTriangle className="text-slate-800 text-3xl mr-2s" />
          <span className="text-slate-800 text-lg font-medium">
            {errorMessage}
          </span>
        </div>
      ) : (
        <div className="min-h-175">
          <div className="pb-6 pt-14 grid 2xl:grid-cols-4 lg:grid-cols-3 sm:grid-cols-2 gap-x-6">
            {products &&
              products.map((item, i) => <ProductCard key={i} {...item} />)}
          </div>
          <div className="flex justify-center pt-10">
            <Paginations
              numberOfPage={pagination?.totalPages}
              totalProducts={pagination?.totalElements}
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default Products;
