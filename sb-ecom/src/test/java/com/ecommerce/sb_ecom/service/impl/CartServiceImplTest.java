package com.ecommerce.sb_ecom.service.impl;

import com.ecommerce.sb_ecom.AuthUtil;
import com.ecommerce.sb_ecom.model.*;
import com.ecommerce.sb_ecom.payload.CartDTO;
import com.ecommerce.sb_ecom.payload.ProductDTO;
import com.ecommerce.sb_ecom.repositories.CartItemRepository;
import com.ecommerce.sb_ecom.repositories.CartRepository;
import com.ecommerce.sb_ecom.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    CartRepository cartRepository;

    @Mock
    CartItemRepository cartItemRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    AuthUtil authUtil;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    CartServiceImpl cartService;

    Category category;
    Product product;
    User user;
    CartItem cartItem;
    List<Product> productList = new ArrayList<>();
    List<CartItem> cartItemList = new ArrayList<>();
    ProductDTO productDTO;
    Cart cart;

    @BeforeEach
    void init() {

        user = new User("user", "user@email.com", "123");

        productList = new ArrayList<>();
        cartItemList = new ArrayList<>();

        category = new Category(
                1L,
                "Books",
                productList
        );

        product = new Product(
                1L,
                "Ball",
                "A ball for kids play",
                "default.png",
                10,
                10.0,
                10.0,
                9.0,
                category,
                user,
                cartItemList
        );

        productList.add(product);

        cart = new Cart();
        cart.setCartId(1L);
        cart.setTotalPrice(0.0);
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>());

        productDTO = new ProductDTO();
        productDTO.setProductId(1L);
        productDTO.setProductName("Ball");
        productDTO.setDescription("A ball for kids play");
        productDTO.setImage("default.png");
        productDTO.setPrice(10.0);
        productDTO.setSpecialPrice(9.0);
        productDTO.setDiscount(10.0);
    }

    @Test
    void shouldAddAProductToCartAndReturnCartDTO() {
        // Arrange
        Long productId = 1L;
        Integer quantity = 2;

        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(1L);
        cartDTO.setTotalPrice(0.0);

        when(authUtil.loggedInEmail()).thenReturn("user@email.com");

        when(cartRepository.findCartByEmail("user@email.com"))
                .thenReturn(cart);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId))
                .thenReturn(null);

        when(cartItemRepository.save(any(CartItem.class)))
                .thenAnswer(invocation -> {
                    CartItem savedCartItem = invocation.getArgument(0);
                    savedCartItem.setCartItemId(1L);

                    cart.getCartItems().add(savedCartItem);

                    return savedCartItem;
                });

        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(modelMapper.map(any(Cart.class), eq(CartDTO.class)))
                .thenReturn(cartDTO);

        when(modelMapper.map(any(Product.class), eq(ProductDTO.class)))
                .thenReturn(productDTO);

        // Act
        CartDTO result = cartService.addProductToCart(productId, quantity);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getCartId());
        assertEquals(18.0, cart.getTotalPrice());
        assertEquals(1, result.getProducts().size());
        assertEquals("Ball", result.getProducts().get(0).getProductName());
        assertEquals(2, result.getProducts().get(0).getQuantity());

        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository).save(cart);
    }


}