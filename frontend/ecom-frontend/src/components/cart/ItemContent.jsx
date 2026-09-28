import { useState } from "react";

const ItemContent = ({
    productId,
    productName,
    image,
    description,
    quantity,
    price,
    discount,
    specialPrice,
    cartId
}) => {

    const [currentQuantity, setCurrentQuantity] = useState(quantity);

    return (
        <div className="grid md:grid-cols-5 grid-cols-4 md:text--md text-sm gap-4 items-center border-[1.5px] border-slate-200 rounded-md lg:px-4 py-4 p-2">
            <div className="md:col-span-2 justify-self-start flex flex-col gap-2">
                <div className="flex md:flex-row flex-col lg:gap-4 sm:gap-3 gap-0 items-start">

                </div>
            </div>
        </div>
    )
}

export default ItemContent;