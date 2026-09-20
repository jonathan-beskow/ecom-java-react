package com.ecommerce.sb_ecom.service.impl;

import com.ecommerce.sb_ecom.AuthUtil;
import com.ecommerce.sb_ecom.exception.APIException;
import com.ecommerce.sb_ecom.exception.ResourceNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    List<ProductDTO> productDTOList;
    Cart cart;
    CartDTO cartDTO;

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

        // 1. Cria o cart primeiro
        cart = new Cart();
        cart.setCartId(1L);
        cart.setTotalPrice(0.0);
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>());

        // 2. Cria o cartItem com o cart já existente
        cartItem = new CartItem(1L, cart, product, 2, 0.1, 10.00);
        //                                          ↑ quantidade relevante para os asserts

        // 3. Adiciona o cartItem ao cart
        cart.getCartItems().add(cartItem);

        cartDTO = new CartDTO();
        productDTOList = new ArrayList<>();

        productDTO = new ProductDTO();
        productDTO.setProductId(1L);
        productDTO.setProductName("Ball");
        productDTO.setDescription("A ball for kids play");
        productDTO.setImage("default.png");
        productDTO.setPrice(10.0);
        productDTO.setSpecialPrice(9.0);
        productDTO.setDiscount(10.0);
        productDTO.setQuantity(2); // ← adicione a quantidade também
        productDTOList.add(productDTO);
    }

    @Test
    void shouldAddAProductToCartAndReturnCartDTO() {
        // Arrange
        Long productId = 1L;
        Integer quantity = 2;

        cartDTO = new CartDTO();
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
        assertEquals(2, result.getProducts().size());
        assertEquals("Ball", result.getProducts().get(0).getProductName());
        assertEquals(2, result.getProducts().get(0).getQuantity());

        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository).save(cart);
    }

    @Test
    void shouldFailBecauseProductDoesNotExist() {
        Long invalidId = 10L;
        Integer quantity = 10;
        when(productRepository.findById(invalidId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> cartService.addProductToCart(invalidId, quantity));

        assertEquals("Product not found with productId: 10", ex.getMessage());

    }

    @Test
    void shouldThrowExceptionBecauseCarIsNotNull() {
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
                .thenReturn(cartItem);

        APIException ex = assertThrows(APIException.class,
                () -> cartService.addProductToCart(1L, 10));

        assertEquals("Product " + product.getProductName() + " already exists in the cart", ex.getMessage());

    }

    @Test
    void shouldThrowExceptionBecauseProductIsNotAvailable() {
        Long productId = 1L;

        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(1L);
        cartDTO.setTotalPrice(0.0);

        product.setQuantity(0);

        when(authUtil.loggedInEmail()).thenReturn("user@email.com");

        when(cartRepository.findCartByEmail("user@email.com"))
                .thenReturn(cart);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId))
                .thenReturn(null);

        APIException ex = assertThrows(APIException.class,
                () -> cartService.addProductToCart(1L, 10));

        assertEquals(product.getProductName() + " is not available", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionBecauseWantedQuantityIsBiggerThanQuantityAvailable() {
        Long productId = 1L;
        Integer quantity = 11;
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

        APIException ex = assertThrows(APIException.class,
                () -> cartService.addProductToCart(productId, quantity));
        assertEquals("Please, make an order of the " + product.getProductName()
                + " less than or equal to the quantity " + product.getQuantity() + ".", ex.getMessage());
    }

    @Test
    void shouldReturnAListWithAllCarts() {
        // Garante que cart tem itens antes de usar
        Product product = new Product();
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cart.setCartItems(List.of(cartItem));

        List<Cart> cartList = new ArrayList<>();
        cartList.add(cart);

        when(cartRepository.findAll()).thenReturn(cartList);

        when(modelMapper.map(any(Cart.class), eq(CartDTO.class)))
                .thenReturn(cartDTO);

        when(modelMapper.map(any(Product.class), eq(ProductDTO.class)))
                .thenReturn(productDTO);

        List<CartDTO> resposta = cartService.getAllCarts();

        assertEquals(1, resposta.size());
    }

    @Test
    void shouldThroExpetionBecauseListIsEmpty() {

        List<Cart> cartList = new ArrayList<>();

        List<CartDTO> cartDTOList = new ArrayList<>();
        cartDTOList.add(cartDTO);

        when(cartRepository.findAll())
                .thenReturn(cartList);

        APIException ex = assertThrows(APIException.class,
                () -> cartService.getAllCarts());
        assertEquals("No cart exists", ex.getMessage());
    }

    @Test
    void shouldGetACart() {
        String email = "user@email.com";
        Long cartId = 1L;
        cartDTO = new CartDTO();
        cartDTO.setCartId(1L);
        cartDTO.setTotalPrice(product.getPrice());
        cartDTO.setProducts(new ArrayList<>(productDTOList));
        when(cartRepository.findCartByEmailAndCartId(email, cartId)).thenReturn(cart);

        when(modelMapper.map(any(Cart.class), eq(CartDTO.class)))
                .thenReturn(cartDTO);

        when(modelMapper.map(any(Product.class), eq(ProductDTO.class)))
                .thenReturn(productDTO);

        CartDTO result = cartService.getCart(email, cartId);

        assertNotNull(result);
        assertEquals(1L, result.getCartId());
        assertEquals(10.0, result.getTotalPrice());
        assertEquals("Ball", result.getProducts().getFirst().getProductName());
        assertEquals(2, result.getProducts().get(0).getQuantity());

    }

    @Test
    void shouldThrowExcepitionBecauseCarIsNull() {
        String email = "user@email.com";
        Long cartId = 10L;
        cartDTO = new CartDTO();
        cartDTO.setCartId(1L);
        cartDTO.setTotalPrice(product.getPrice());
        cartDTO.setProducts(new ArrayList<>(productDTOList));
        when(cartRepository.findCartByEmailAndCartId(email, cartId)).thenReturn(null);

        ResourceNotFoundException result = assertThrows( ResourceNotFoundException.class, () -> cartService.getCart(email, cartId));
        assertEquals("Cart not found with cartId: 10", result.getMessage());
    }

    @Test
    void shouldUpdateQuantityInCart() {
        String email = "user@email.com";
        Long cartId = cart.getCartId();
        Long productId = product.getProductId();
        Integer quantity = 1;

        // Mock do usuário logado
        when(authUtil.loggedInEmail()).thenReturn(email);

        // Mocks do repositório
        when(cartRepository.findCartByEmail(email)).thenReturn(cart);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId))
                .thenReturn(cartItem);

        // Mock do save
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Mock do mapeamento
        when(modelMapper.map(any(Cart.class), eq(CartDTO.class))).thenReturn(cartDTO);
        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(productDTO);

        CartDTO result = cartService.updateProductQuantityInCart(productId, quantity);

        // Verificações
        assertNotNull(result);
        assertNotNull(result.getProducts());

        verify(cartRepository).findCartByEmail(email);
        verify(cartRepository).findById(cartId);
        verify(productRepository).findById(productId);
        verify(cartItemRepository).findCartItemByProductIdAndCartId(cartId, productId);
        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void shouldThrowExceptionWhenProductOutOfStock() {
        product.setQuantity(0);

        when(authUtil.loggedInEmail()).thenReturn("user@email.com");
        when(cartRepository.findCartByEmail("user@email.com")).thenReturn(cart);
        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

        assertThrows(APIException.class,
                () -> cartService.updateProductQuantityInCart(product.getProductId(), 1));
    }

    @Test
    void shouldThrowExceptionWhenQuantityExceedsStock() {
        when(authUtil.loggedInEmail()).thenReturn("user@email.com");
        when(cartRepository.findCartByEmail("user@email.com")).thenReturn(cart);
        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

        // Tenta adicionar mais do que o estoque disponível
        assertThrows(APIException.class,
                () -> cartService.updateProductQuantityInCart(product.getProductId(), 999));
    }

    @Test
    void shouldThrowExceptionWhenCartItemNotFound() {
        when(authUtil.loggedInEmail()).thenReturn("user@email.com");
        when(cartRepository.findCartByEmail("user@email.com")).thenReturn(cart);
        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(cartItemRepository.findCartItemByProductIdAndCartId(
                cart.getCartId(), product.getProductId())).thenReturn(null);

        assertThrows(APIException.class,
                () -> cartService.updateProductQuantityInCart(product.getProductId(), 1));
    }

    @Test
    void shouldThrowExceptionWhenResultingQuantityIsNegative() {
        // cartItem com quantidade 1, tentando remover 5 → newQuantity = -4
        cartItem.setQuantity(1);

        when(authUtil.loggedInEmail()).thenReturn("user@email.com");
        when(cartRepository.findCartByEmail("user@email.com")).thenReturn(cart);
        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
        when(cartItemRepository.findCartItemByProductIdAndCartId(
                cart.getCartId(), product.getProductId())).thenReturn(cartItem);

        assertThrows(APIException.class,
                () -> cartService.updateProductQuantityInCart(product.getProductId(), -5));
    }

    @Test
    void shouldDeleteAProductFromCart() {

        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), product.getProductId())).thenReturn(cartItem);
        String response = cartService.deleteProductFromCart(cart.getCartId(), product.getProductId());
        assertEquals("Product" + cartItem.getProduct().getProductName() + " removed from the cart", response);

    }

    @Test
    void shouldThrowResourceNotFoundWhenTryToDeleteAProductFromCart() {
        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.empty());
        ResourceNotFoundException response = assertThrows( ResourceNotFoundException.class, () -> cartService.deleteProductFromCart(cart.getCartId(), product.getProductId()));
        assertEquals("Cart not found with cartId: 1", response.getMessage());

    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhentryToDeleteAProductFromCartBecauseCartItemIsNull() {

        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));

        when(cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), product.getProductId())).thenReturn(null);

        ResourceNotFoundException response = assertThrows( ResourceNotFoundException.class, () -> cartService.deleteProductFromCart(cart.getCartId(), product.getProductId()));
        assertEquals("Product  not found with productId: 1", response.getMessage());

    }

    @Test
    void shouldThrowExceptionWhenTryToUpdateProductInCartBecauseCartIsNotFound() {
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException response = assertThrows( ResourceNotFoundException.class, () -> cartService.updateProductInCarts(cart.getCartId(), product.getProductId()));
        assertEquals("Cart not found with cartId: 1", response.getMessage());

    }

    @Test
    void shouldThrowExceptionWhenTryToUpdateProductInCartBecauseProductIsNotFound() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        ResourceNotFoundException response = assertThrows( ResourceNotFoundException.class, () -> cartService.updateProductInCarts(cart.getCartId(), product.getProductId()));
        assertEquals("Product not found with productId: 1", response.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTryToUpdateProductInCartBecauseCartItemIsNull() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), product.getProductId())).thenReturn(null);
        APIException response = assertThrows( APIException.class, () -> cartService.updateProductInCarts(cart.getCartId(), product.getProductId()));
        assertEquals("Product " + product.getProductName() + " not available", response.getMessage());
    }

    @Test
    void shouldUpdateAProduct() {
        // 1. Preparação (Arrange)
        Cart cart = new Cart();
        cart.setCartId(1L);
        cart.setTotalPrice(100.0); // Preço total antes da atualização

        Product product = new Product();
        product.setProductId(1L);
        product.setSpecialPrice(20.0); // Novo preço promocional do produto

        CartItem cartItem = new CartItem();
        cartItem.setProductPrice(30.0); // Preço antigo do item
        cartItem.setQuantity(2); // Quantidade
        cartItem.setCart(cart);
        cartItem.setProduct(product);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Mantendo exatamente a ordem dos parâmetros como está no seu serviço original
        when(cartItemRepository.findCartItemByProductIdAndCartId(1L, 1L)).thenReturn(cartItem);
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);

        // 2. Execução (Act)
        // Apenas chamamos o método, sem atribuir o retorno, pois ele é void
        cartService.updateProductInCarts(1L, 1L);

        // 3. Validação (Assert)
        // Verifica se o preço do item foi atualizado para o preço especial do produto
        assertEquals(20.0, cartItem.getProductPrice());

        // Verifica se o preço do carrinho foi recalculado corretamente
        // Calculo: Total Antigo (100) - Valor Antigo dos Itens (30 * 2) + Novo Valor dos Itens (20 * 2) = 80.0
        assertEquals(80.0, cart.getTotalPrice());

        // Verifica se o método save foi invocado exatamente 1 vez com o cartItem atualizado
        verify(cartItemRepository, times(1)).save(cartItem);
    }

    @Test
    void shouldRemoveProductFromCartWhenNewQuantityIsZero() {
        // Arrange
        String email = "user@email.com";
        Long cartId = cart.getCartId();
        Long productId = product.getProductId();

        // O cartItem já existe com quantidade 2. Vamos adicionar -2 para zerar.
        cartItem.setQuantity(2);
        cartItem.setCartItemId(99L); // ID para validar o deleteById
        Integer quantityToAdd = -2;

        when(authUtil.loggedInEmail()).thenReturn(email);
        when(cartRepository.findCartByEmail(email)).thenReturn(cart);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId))
                .thenReturn(cartItem);

        // Simulamos o retorno do save para ter quantidade 0,
        // ativando assim a condição if (updatedItem.getQuantity() == 0)
        CartItem zeroQuantityItem = new CartItem();
        zeroQuantityItem.setCartItemId(99L);
        zeroQuantityItem.setQuantity(0);
        zeroQuantityItem.setProduct(product);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(zeroQuantityItem);

        when(modelMapper.map(any(Cart.class), eq(CartDTO.class))).thenReturn(cartDTO);

        // Se a lista de produtos no carrinho mapeada tiver algum, mockamos também o mapper do produto
        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(productDTO);


        CartDTO result = cartService.updateProductQuantityInCart(productId, quantityToAdd);


        assertNotNull(result);

        verify(cartItemRepository, times(1)).save(cartItem);

        verify(cartItemRepository, times(1)).deleteById(99L);

        assertNotEquals(product.getSpecialPrice(), cartItem.getProductPrice());
    }

    @Test
    void shouldThrowResourceNotFoundBecauseCartIsNotFound() {
        String email = "user@email.com";

        when(authUtil.loggedInEmail()).thenReturn(email);
        when(cartRepository.findCartByEmail(email)).thenReturn(cart);

        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.empty());
        ResourceNotFoundException response =
                assertThrows( ResourceNotFoundException.class,
                        () -> cartService.updateProductQuantityInCart(cart.getCartId(), 10));
        assertEquals("Cart not found with cartId: 1", response.getMessage());
    }

    @Test
    void shouldThrowResourceNotFoundBecauseProductIsNotFound() {
        String email = "user@email.com";

        when(authUtil.loggedInEmail()).thenReturn(email);
        when(cartRepository.findCartByEmail(email)).thenReturn(cart);
        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getProductId())).thenReturn(Optional.empty());

        ResourceNotFoundException response =
                assertThrows( ResourceNotFoundException.class,
                        () -> cartService.updateProductQuantityInCart(cart.getCartId(), 10));
        assertEquals("Product not found with productId: 1", response.getMessage());

    }


}