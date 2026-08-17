package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.payload.ProductDTO;
import com.ecommerce.sb_ecom.payload.ProductResponse;
import com.ecommerce.sb_ecom.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(
            @RequestBody ProductDTO productDTO,
            @PathVariable Long categoryId
    ) {
        ProductDTO savedproductDTO = productService.addProduct(categoryId, productDTO);
        return new ResponseEntity<>(savedproductDTO, CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts() {
        ProductResponse productResponse = productService.getAllProducts();
        return new ResponseEntity<>(productResponse, OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProdctsByCategory(@PathVariable Long categoryId) {
        ProductResponse productResponse = productService.searchByCategory(categoryId);
        return new ResponseEntity<>(productResponse, OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyWord(@PathVariable String keyword) {
        ProductResponse productResponse = productService.searchProductByKeyword(keyword);
        return new ResponseEntity<>(productResponse, FOUND);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO productDTO,
                                                    @PathVariable Long productId) {

        ProductDTO updatedProduct = productService.updateProduct(productId, productDTO);
        return new ResponseEntity<>(updatedProduct, OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        ProductDTO deletedProduct = productService.deleteProductById(productId);
        return new ResponseEntity<>(deletedProduct, OK);
    }

}
