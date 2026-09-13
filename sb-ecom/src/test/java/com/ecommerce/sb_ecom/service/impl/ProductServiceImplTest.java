package com.ecommerce.sb_ecom.service.impl;

import com.ecommerce.sb_ecom.exception.APIException;
import com.ecommerce.sb_ecom.exception.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.CartItem;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.model.Product;
import com.ecommerce.sb_ecom.model.User;
import com.ecommerce.sb_ecom.payload.ProductDTO;
import com.ecommerce.sb_ecom.repositories.CartRepository;
import com.ecommerce.sb_ecom.repositories.CategoryRepository;
import com.ecommerce.sb_ecom.repositories.ProductRepository;
import com.ecommerce.sb_ecom.service.CartService;
import com.ecommerce.sb_ecom.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    Category category;
    Product product;
    User user;
    CartItem cartItem;
    List<Product> productList = new ArrayList<>();
    List<CartItem> cartItemList = new ArrayList<>();
    ProductDTO productDTO;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private FileService fileService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    ProductServiceImpl productService;

    @BeforeEach
    void init() {

        product = new Product(
                1L,
                "Ball",
                "A ball for kids play",
                "default.png",
                1,
                new Double(10),
                new Double(10),
                new Double(9),
                category,
                user,
                cartItemList
        );

        productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setProductName(product.getProductName());
        productDTO.setImage(product.getImage());
        productDTO.setDescription(product.getDescription());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setPrice(product.getPrice());
        productDTO.setDiscount(product.getDiscount());
        productDTO.setSpecialPrice(product.getSpecialPrice());


        category = new Category(
                1L,
                "Books",
                productList
        );

        productList.add(product);
        cartItemList.add(cartItem);

    }


    @Test
    void shouldAddAProductWithSuccess() {

        Product newProduct = new Product(
                3L,
                "Cup of coffee",
                "Cup of coffee to maintain learning tests",
                "default.png",
                1,
                new Double(10),
                new Double(10),
                new Double(9),
                category,
                user,
                cartItemList
        );

        ProductDTO newProductDto = new ProductDTO();
        newProductDto.setProductId(newProduct.getProductId());
        newProductDto.setProductName(newProduct.getProductName());
        newProductDto.setImage(newProduct.getImage());
        newProductDto.setDescription(newProduct.getDescription());
        newProductDto.setQuantity(newProduct.getQuantity());
        newProductDto.setPrice(newProduct.getPrice());
        newProductDto.setDiscount(newProduct.getDiscount());
        newProductDto.setSpecialPrice(newProduct.getSpecialPrice());



        Long categoryId = 3L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        //mapeia para setar propriedades porém aqui elas ja existem
        when(modelMapper.map(newProductDto, Product.class)).thenReturn(newProduct);

        //faz alguns ajustes de propriedade e salva o product
        when(productRepository.save(newProduct)).thenReturn(newProduct);

        //mapeia novamente para objeto de tráfego
        when(modelMapper.map(newProduct, ProductDTO.class)).thenReturn(newProductDto);

        ProductDTO productDTO1 = productService.addProduct(categoryId, newProductDto);

        assertNotNull(productDTO1);
        assertEquals(3L, productDTO1.getProductId());
        assertEquals("Cup of coffee", productDTO1.getProductName());
        assertEquals(9.0, productDTO1.getSpecialPrice()); // Garante que o cálculo de desconto retornou no DTO

        verify(productRepository).save(any(Product.class));
        verify(modelMapper).map(newProduct, ProductDTO.class);

    }

    @Test
    void shouldFailBecauseCategoryDoesNotExist() {

        Long categoryId = 3L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> productService.addProduct(categoryId, productDTO));

        assertNotNull(ex);

    }

    @Test
    void shoudFailBecauseProductcHaveThesameName() {

        Long categoryId = 1L;
        // prepara a categoria
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));


        APIException ex = assertThrows(APIException.class, () -> productService.addProduct(categoryId, productDTO));
        assertNotNull(ex);
        assertEquals("Product already exist!", ex.getMessage());
    }


}