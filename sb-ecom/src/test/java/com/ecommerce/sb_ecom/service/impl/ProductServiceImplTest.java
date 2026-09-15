package com.ecommerce.sb_ecom.service.impl;

import com.ecommerce.sb_ecom.exception.APIException;
import com.ecommerce.sb_ecom.exception.ResourceNotFoundException;
import com.ecommerce.sb_ecom.model.*;
import com.ecommerce.sb_ecom.payload.CartDTO;
import com.ecommerce.sb_ecom.payload.ProductDTO;
import com.ecommerce.sb_ecom.payload.ProductResponse;
import com.ecommerce.sb_ecom.repositories.CartRepository;
import com.ecommerce.sb_ecom.repositories.CategoryRepository;
import com.ecommerce.sb_ecom.repositories.ProductRepository;
import com.ecommerce.sb_ecom.service.CartService;
import com.ecommerce.sb_ecom.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    void shouldGetAllProductsPaginated() {
        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "productId";
        Sort sortByASC = Sort.by(sortBy).ascending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByASC);

        Page<Product> productsPage = new PageImpl<>(
                productList,
                pageDetails,
                productList.size()
        );

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productsPage);
        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(productDTO);

        ProductResponse response =
                productService.getAllProducts(pageNumber, pageSize, sortBy, "asc");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(0, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertTrue(response.isLastPage());
        verify(productRepository).findAll(any(Pageable.class));
    }

    @Test
    void shouldThrowExceptionBecauseListIsEmpty() {

        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "productId";
        Sort sortByASC = Sort.by(sortBy).ascending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByASC);

        List<Product> emptyList = new ArrayList<>();

        Page<Product> productsPage = new PageImpl<>(
                emptyList,
                pageDetails,
                emptyList.size()
        );

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productsPage);

        APIException ex = assertThrows(APIException.class, () -> productService.getAllProducts(pageNumber, pageSize, sortBy, "asc"));

        assertNotNull(ex);
        assertEquals("No products found!", ex.getMessage());

        verify(productRepository).findAll(any(Pageable.class));
        verifyNoInteractions(modelMapper);

    }

    @Test
    void shouldReturnAResponseBecauseACorrectCategoryWasInformed() {

        Long categoryId = 1L;

        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "productId";
        Sort sortByASC = Sort.by(sortBy).ascending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByASC);

        Page<Product> productsPage = new PageImpl<>(
                productList,
                pageDetails,
                productList.size()
        );

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        when(productRepository.findByCategoryOrderByPriceAsc(eq(category), any(Pageable.class)))
                .thenReturn(productsPage);

        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(productDTO);

        ProductResponse response = productService.searchByCategory(categoryId, pageNumber, pageSize, sortBy, "asc");
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(0, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertTrue(response.isLastPage());
    }

    @Test
    void shouldThrowExceptionBecauseCategoryWasNotFound() {
        Long invalidId = 10L;
        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "productId";
        Sort sortByASC = Sort.by(sortBy).ascending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByASC);

        when(categoryRepository.findById(invalidId)).thenReturn(Optional.empty());


        ResourceNotFoundException ex =
                assertThrows(ResourceNotFoundException.class,
                        () -> productService.searchByCategory(invalidId, pageNumber, pageSize, sortBy, "asc"));

        assertNotNull(ex);

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(productRepository);
        verify(categoryRepository).findById(invalidId);

    }

    @Test
    void shouldThrowExceptionBecauseCategoryHasNoProducts() {
        Long categoryId = 1L;
        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "productId";
        Sort sortByASC = Sort.by(sortBy).ascending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByASC);

        List<Product> emptyProductList = new ArrayList<>();

        Page<Product> productsPage = new PageImpl<>(
                emptyProductList,
                pageDetails,
                emptyProductList.size()
        );

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        when(productRepository.findByCategoryOrderByPriceAsc(eq(category), any(Pageable.class)))
                .thenReturn(productsPage);


        APIException ex = assertThrows(APIException.class,
                () -> productService.searchByCategory(categoryId, pageNumber, pageSize, sortBy, "asc"));

        assertNotNull(ex);
        assertEquals("Products not found with categoryId: " + categoryId, ex.getMessage());
        // Verificações de fluxo
        verify(categoryRepository).findById(categoryId);
        verify(productRepository).findByCategoryOrderByPriceAsc(eq(category), any(Pageable.class));
        verifyNoInteractions(modelMapper); // G
    }

    @Test
    void shouldReturnAResponseBecauseFoundAProductWithKeyWord() {

        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "productId";
        Sort sortByASC = Sort.by(sortBy).ascending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByASC);

        String keyWord = "Ball";

        Page<Product> productsPage = new PageImpl<>(
                productList,
                pageDetails,
                productList.size()
        );

        when(productRepository.findByProductNameLikeIgnoreCase("%" + keyWord + "%", pageDetails))
                .thenReturn(productsPage);

        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(productDTO);

        ProductResponse response = productService.searchProductByKeyword(keyWord, pageNumber, pageSize, sortBy, "asc");
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(0, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertTrue(response.isLastPage());

    }

    @Test
    void shouldReturnExceptionBecauseKeywordIsNotFound() {
        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "productId";
        Sort sortByASC = Sort.by(sortBy).ascending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByASC);

        List<Product> emptyProductList = new ArrayList<>();

        String keyword = "Table";

        Page<Product> productsPage = new PageImpl<>(
                emptyProductList,
                pageDetails,
                emptyProductList.size()
        );

        when(productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%", pageDetails))
                .thenReturn(productsPage);

        APIException ex = assertThrows(APIException.class,
                () -> productService.searchProductByKeyword(keyword, pageNumber, pageSize, sortBy, "asc"));

        assertEquals("Products not found with keyword: " + keyword, ex.getMessage());
        verify(productRepository).findByProductNameLikeIgnoreCase("%" + keyword + "%", pageDetails);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void shouldUpdateAProductWithSuccess() {

        Long productId = 1L;

        ProductDTO updatedProductDTO = new ProductDTO();
        updatedProductDTO.setProductId(productId);
        updatedProductDTO.setProductName("Table");
        updatedProductDTO.setImage(product.getImage());
        updatedProductDTO.setDescription("A table to call your friends");
        updatedProductDTO.setQuantity(product.getQuantity());
        updatedProductDTO.setPrice(100.0);
        updatedProductDTO.setDiscount(product.getDiscount());
        updatedProductDTO.setSpecialPrice(product.getSpecialPrice());

        Product mappedProduct = new Product();
        mappedProduct.setProductId(productId);
        mappedProduct.setProductName("Table");
        mappedProduct.setImage(product.getImage());
        mappedProduct.setDescription("A table to call your friends");
        mappedProduct.setQuantity(product.getQuantity());
        mappedProduct.setPrice(100.0);
        mappedProduct.setDiscount(product.getDiscount());
        mappedProduct.setSpecialPrice(product.getSpecialPrice());

        ProductDTO responseDTO = new ProductDTO();
        responseDTO.setProductId(productId);
        responseDTO.setProductName("Table");
        responseDTO.setImage(product.getImage());
        responseDTO.setDescription("A table to call your friends");
        responseDTO.setQuantity(product.getQuantity());
        responseDTO.setPrice(100.0);
        responseDTO.setDiscount(product.getDiscount());
        responseDTO.setSpecialPrice(product.getSpecialPrice());

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setProductPrice(100.0);
        cartItem.setQuantity(1);
        cartItem.setDiscount(product.getDiscount());

        List<CartItem> cartItems = List.of(cartItem);

        Cart cart = new Cart();
        cart.setCartId(1L);
        cart.setUser(user);
        cart.setCartItems(cartItems);
        cart.setTotalPrice(cartItem.getProductPrice());

        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(1L);
        cartDTO.setProducts(List.of(productDTO));
        cartDTO.setTotalPrice(cartItem.getProductPrice());

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(modelMapper.map(updatedProductDTO, Product.class)).thenReturn(mappedProduct);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(cartRepository.findCartsByProductId(productId)).thenReturn(List.of(cart));
        when(modelMapper.map(any(Cart.class), eq(CartDTO.class))).thenReturn(cartDTO);
        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(responseDTO);

        ProductDTO result = productService.updateProduct(productId, updatedProductDTO);

        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals("Table", result.getProductName());
        assertEquals("A table to call your friends", result.getDescription());
        assertEquals(100.0, result.getPrice());

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        verify(productRepository).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();

        assertEquals("Table", savedProduct.getProductName());
        assertEquals("A table to call your friends", savedProduct.getDescription());
        assertEquals(product.getQuantity(), savedProduct.getQuantity());
        assertEquals(product.getDiscount(), savedProduct.getDiscount());
        assertEquals(100.0, savedProduct.getPrice());
        assertEquals(product.getSpecialPrice(), savedProduct.getSpecialPrice());

        verify(productRepository).findById(productId);
        verify(modelMapper).map(updatedProductDTO, Product.class);
        verify(cartRepository).findCartsByProductId(productId);
        verify(cartService).updateProductInCarts(1L, productId);
    }

    @Test
    void shouldFailBecauseProductIsNotFound() {

        Long invalidId = 10L;

        ProductDTO updatedProductDTO = new ProductDTO();
        updatedProductDTO.setProductId(invalidId);
        updatedProductDTO.setProductName("Table");
        updatedProductDTO.setImage(product.getImage());
        updatedProductDTO.setDescription("A table to call your friends");
        updatedProductDTO.setQuantity(product.getQuantity());
        updatedProductDTO.setPrice(100.0);
        updatedProductDTO.setDiscount(product.getDiscount());
        updatedProductDTO.setSpecialPrice(product.getSpecialPrice());

        when(productRepository.findById(invalidId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(invalidId, updatedProductDTO));


        // Verificações de fluxo
        verify(productRepository).findById(invalidId);
        verifyNoInteractions(modelMapper); // G
    }

    @Test
    void shouldDeleteAProductById() {
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setProductPrice(100.0);
        cartItem.setQuantity(1);
        cartItem.setDiscount(product.getDiscount());

        List<CartItem> cartItems = List.of(cartItem);

        Cart cart = new Cart();
        cart.setCartId(1L);
        cart.setUser(user);
        cart.setCartItems(cartItems);
        cart.setTotalPrice(cartItem.getProductPrice());

        List<Cart> cartList = List.of(cart);

        when(cartRepository.findCartsByProductId(productId))
                .thenReturn(cartList);

        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(productDTO);


        ProductDTO response = productService.deleteProductById(productId);
        assertNotNull(response);

        assertNotNull(response);
        assertEquals(productDTO, response);

        verify(productRepository, times(1)).findById(productId);
        verify(cartRepository, times(1)).findCartsByProductId(productId);
        verify(cartService, times(1)).deleteProductFromCart(cart.getCartId(), productId);
        verify(productRepository, times(1)).delete(product);
        verify(modelMapper, times(1)).map(product, ProductDTO.class);
    }

    @Test
    void shouldThrowExceptionBecauseProductIdIsNotFound() {
        Long productId = 10L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productService.deleteProductById(productId));

        assertNotNull(ex);
        verify(productRepository).findById(productId);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void shouldUpdateProductImage() throws IOException {
        Long productId = 1L;
        String filename = "produto.png";
        String path = "images/";

        ReflectionTestUtils.setField(productService, "path", path);

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "produto.png",
                "image/png",
                "conteudo-da-imagem".getBytes()
        );

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(fileService.uploadImage(eq(path), eq(image)))
                .thenReturn(filename);

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(modelMapper.map(any(Product.class), eq(ProductDTO.class)))
                .thenReturn(productDTO);

        ProductDTO response = productService.updateProductImage(productId, image);

        assertNotNull(response);
        assertEquals(productDTO, response);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        verify(productRepository, times(1)).findById(productId);
        verify(fileService, times(1)).uploadImage(eq(path), eq(image));
        verify(productRepository, times(1)).save(productCaptor.capture());
        verify(modelMapper, times(1)).map(any(Product.class), eq(ProductDTO.class));

        Product savedProduct = productCaptor.getValue();

        assertEquals(filename, savedProduct.getImage());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenProductDoesNotExist() throws IOException {
        Long productId = 1L;

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "produto.png",
                "image/png",
                "conteudo-da-imagem".getBytes()
        );

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProductImage(productId, image);
        });

        verify(productRepository, times(1)).findById(productId);
        verify(fileService, never()).uploadImage(anyString(), any(MultipartFile.class));
        verify(productRepository, never()).save(any(Product.class));
        verify(modelMapper, never()).map(any(Product.class), eq(ProductDTO.class));
    }
    
    @Test
    void shouldGetAllProductsPaginatedDesc() {
        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "productId";

        Sort sortByDESC = Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByDESC);

        Page<Product> productsPage = new PageImpl<>(
                productList,
                pageDetails,
                productList.size()
        );

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productsPage);
        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(productDTO);

        ProductResponse response =
                productService.getAllProducts(pageNumber, pageSize, sortBy, "desc");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(0, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertTrue(response.isLastPage());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(productRepository).findAll(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(pageNumber, capturedPageable.getPageNumber());
        assertEquals(pageSize, capturedPageable.getPageSize());
        assertNotNull(capturedPageable.getSort().getOrderFor(sortBy));
        assertTrue(capturedPageable.getSort().getOrderFor(sortBy).isDescending());

        verify(modelMapper).map(any(Product.class), eq(ProductDTO.class));
    }

    @Test
    void shouldReturnAResponseBecauseACorrectCategoryWasInformedWithDescPagination() {
        Long categoryId = 1L;

        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "productId";

        Sort sortByDESC = Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByDESC);

        Page<Product> productsPage = new PageImpl<>(
                productList,
                pageDetails,
                productList.size()
        );

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        when(productRepository.findByCategoryOrderByPriceAsc(eq(category), any(Pageable.class)))
                .thenReturn(productsPage);

        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(productDTO);

        ProductResponse response =
                productService.searchByCategory(categoryId, pageNumber, pageSize, sortBy, "desc");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(0, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertTrue(response.isLastPage());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(categoryRepository).findById(categoryId);
        verify(productRepository).findByCategoryOrderByPriceAsc(eq(category), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(pageNumber, capturedPageable.getPageNumber());
        assertEquals(pageSize, capturedPageable.getPageSize());
        assertNotNull(capturedPageable.getSort().getOrderFor(sortBy));
        assertTrue(capturedPageable.getSort().getOrderFor(sortBy).isDescending());

        verify(modelMapper).map(any(Product.class), eq(ProductDTO.class));
    }

    @Test
    void shouldReturnAResponseBecauseFoundAProductWithKeyWordWithDescPagination() {
        Integer pageNumber = 0;
        Integer pageSize = 10;
        String sortBy = "productId";

        String keyWord = "Ball";

        Sort sortByDESC = Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByDESC);

        Page<Product> productsPage = new PageImpl<>(
                productList,
                pageDetails,
                productList.size()
        );

        when(productRepository.findByProductNameLikeIgnoreCase(eq("%" + keyWord + "%"), any(Pageable.class)))
                .thenReturn(productsPage);

        when(modelMapper.map(any(Product.class), eq(ProductDTO.class))).thenReturn(productDTO);

        ProductResponse response =
                productService.searchProductByKeyword(keyWord, pageNumber, pageSize, sortBy, "desc");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals(0, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertTrue(response.isLastPage());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(productRepository)
                .findByProductNameLikeIgnoreCase(eq("%" + keyWord + "%"), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();

        assertEquals(pageNumber, capturedPageable.getPageNumber());
        assertEquals(pageSize, capturedPageable.getPageSize());
        assertNotNull(capturedPageable.getSort().getOrderFor(sortBy));
        assertTrue(capturedPageable.getSort().getOrderFor(sortBy).isDescending());

        verify(modelMapper).map(any(Product.class), eq(ProductDTO.class));
    }


}