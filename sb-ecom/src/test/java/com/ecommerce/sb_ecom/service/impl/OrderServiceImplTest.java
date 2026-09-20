package com.ecommerce.sb_ecom.service.impl;

import com.ecommerce.sb_ecom.exception.APIException;
import com.ecommerce.sb_ecom.exception.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.*;
import com.ecommerce.sb_ecom.payload.*;
import com.ecommerce.sb_ecom.repositories.*;
import com.ecommerce.sb_ecom.service.CartService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    String userEmail = "user@email.com";
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
    Address address;
    AddressDTO addressDTO;

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

        address = new Address();
        address.setAddressId(1L);
        address.setStreet("Avenida brasil");
        address.setBuildingName("Edificio do centro");
        address.setCity("Sao paulo");
        address.setState("Sao paulo");
        address.setCountry("Brazil");
        address.setPincode("12345");
        address.setUser(user);

        addressDTO = new AddressDTO();
        addressDTO.setAddressId(1L);
        addressDTO.setStreet("Avenida brasil");
        addressDTO.setBuildingName("Edificio do centro");
        addressDTO.setCity("Sao paulo");
        addressDTO.setState("Sao paulo");
        addressDTO.setCountry("Brazil");
        addressDTO.setPincode("12345");

        user.setAddresses(new ArrayList<>(List.of(address)));

    }

    @Test
    void shouldThrowExceptionBecauseCartIsNull() {
        when(cartRepository.findCartByEmail(userEmail)).thenReturn(null);

        ResourceNotFoundException response =
                assertThrows(ResourceNotFoundException.class,
                        () -> orderService.placeOrder(
                                userEmail,
                                address.getAddressId(),
                                "Credit Card",        // paymentMethod
                                "Stripe",
                                "pay_123456",
                                "SUCCESS",
                                "Payment Approved"
                        ));

        assertEquals("Cart not found with email: " + userEmail, response.getMessage());
    }

    @Test
    void shouldThrowExceptionBecauseAddresssNull() {
        when(cartRepository.findCartByEmail(userEmail)).thenReturn(cart);
        when(addressRepository.findById(address.getAddressId())).thenReturn(Optional.empty());

        ResourceNotFoundException response =
                assertThrows(ResourceNotFoundException.class,
                        () -> orderService.placeOrder(
                                userEmail,
                                address.getAddressId(),
                                "Credit Card",        // paymentMethod
                                "Stripe",
                                "pay_123456",
                                "SUCCESS",
                                "Payment Approved"
                        ));

        assertEquals("Address not found with addressId: " + address.getAddressId(), response.getMessage());
    }

    @Test
    void shouldThrowApiExceptionBecauseListIsEmpty() {

        Cart newCart = new Cart(2L, user, new ArrayList<>(), 0.0);

        when(cartRepository.findCartByEmail(userEmail)).thenReturn(newCart);
        when(addressRepository.findById(address.getAddressId())).thenReturn(Optional.of(address));

        APIException response =
                assertThrows(APIException.class,
                        () -> orderService.placeOrder(
                                userEmail,
                                address.getAddressId(),
                                "Credit Card",        // paymentMethod
                                "Stripe",
                                "pay_123456",
                                "SUCCESS",
                                "Payment Approved"
                        ));

        assertEquals("Cart is empty", response.getMessage());
    }

    @Test
    void shouldPlaceOrderSuccessfully() {
        // 1. Arrange (Preparação)
        String email = userEmail;
        Long addressId = address.getAddressId();
        String paymentMethod = "Credit Card";
        String pgName = "Stripe";
        String pgPaymentId = "pay_123456";
        String pgStatus = "SUCCESS";
        String pgResponseMessage = "Payment Approved";

        // Mockando o retorno do carrinho e do endereço
        when(cartRepository.findCartByEmail(email)).thenReturn(cart);
        when(addressRepository.findById(addressId)).thenReturn(java.util.Optional.of(address));

        // Mockando o save do Payment (retorna o próprio objeto passado como argumento)
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mockando o save do Order
        Order savedOrder = new Order();
        savedOrder.setOrderId(100L); // Simulando ID gerado pelo banco
        savedOrder.setEmail(email);
        savedOrder.setTotalAmount(cart.getTotalPrice());
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Mockando o saveAll dos OrderItems (retorna a própria lista passada)
        when(orderItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // Mockando a atualização do estoque do produto
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mockando a chamada para deletar o produto do carrinho
        when(cartService.deleteProductFromCart(eq(cart.getCartId()), eq(product.getProductId())))
                .thenReturn("Product removed");

        // Mockando o ModelMapper para o OrderDTO
        OrderDTO expectedOrderDTO = new OrderDTO();
        expectedOrderDTO.setOrderId(100L);
        expectedOrderDTO.setOrderItems(new ArrayList<>());
        when(modelMapper.map(savedOrder, OrderDTO.class)).thenReturn(expectedOrderDTO);

        // Mockando o ModelMapper para o OrderItemDTO
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemDTO.class))).thenReturn(orderItemDTO);

        // Armazenando a quantidade inicial do produto para validar se diminuiu
        int initialQuantity = product.getQuantity();
        int cartItemQuantity = cartItem.getQuantity();

        // 2. Act (Execução)
        OrderDTO result = orderService.placeOrder(
                email,
                addressId,
                paymentMethod,
                pgName,
                pgPaymentId,
                pgStatus,
                pgResponseMessage
        );

        // 3. Assert (Validações)
        assertNotNull(result);
        assertEquals(100L, result.getOrderId());
        assertEquals(addressId, result.getAddressId());

        // Verifica se a quantidade do produto foi subtraída corretamente no estoque
        assertEquals(initialQuantity - cartItemQuantity, product.getQuantity());

        // Verifica se os métodos corretos foram chamados a quantidade certa de vezes
        verify(cartRepository, times(1)).findCartByEmail(email);
        verify(addressRepository, times(1)).findById(addressId);
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).saveAll(anyList());
        verify(productRepository, times(1)).save(product);
        verify(cartService, times(1)).deleteProductFromCart(cart.getCartId(), product.getProductId());
    }
}