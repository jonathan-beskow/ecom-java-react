package com.ecommerce.sb_ecom.service.impl;

import com.ecommerce.sb_ecom.model.Address;
import com.ecommerce.sb_ecom.model.Product;
import com.ecommerce.sb_ecom.model.User;
import com.ecommerce.sb_ecom.payload.AddressDTO;
import com.ecommerce.sb_ecom.payload.ProductDTO;
import com.ecommerce.sb_ecom.repositories.AddressRepository;
import com.ecommerce.sb_ecom.repositories.UserRepository;
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
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    Address address;
    AddressDTO addressDTO;
    User user;

    @BeforeEach
    void init() {
        user = new User("user", "user@email.com", "123");
        user.setUserId(1L);


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

        user.setAddresses(List.of(address));
    }



    @Test
    void shouldCreateAnAddressWithSuccess() {

        when(modelMapper.map(any(AddressDTO.class), eq(Address.class))).thenReturn(address);

        when(modelMapper.map(any(Address.class), eq(AddressDTO.class))).thenReturn(addressDTO);

        when(addressRepository.save(address)).thenReturn(address);

        AddressDTO response = addressService.createAddress(addressDTO, user);
        assertNotNull(response);

        assertEquals(1l, response.getAddressId());

        verify(modelMapper, times(1)).map(addressDTO, Address.class);
        verify(addressRepository, times(1)).save(address);
        verify(modelMapper, times(1)).map(address, AddressDTO.class);
    }

    @Test
    void shouldGetAllAddresses() {
        List<Address> addressList = List.of(address);

        when(addressRepository.findAll()).thenReturn(addressList);
        when(modelMapper.map(any(Address.class), eq(AddressDTO.class))).thenReturn(addressDTO);

        List<AddressDTO> response = addressService.getAddressess();

        assertNotNull(response);
        assertEquals(1, response.size());

        AddressDTO result = response.get(0);

        assertEquals(1L, result.getAddressId());
        assertEquals("Avenida brasil", result.getStreet());
        assertEquals("Edificio do centro", result.getBuildingName());
        assertEquals("Sao paulo", result.getCity());
        assertEquals("Sao paulo", result.getState());
        assertEquals("Brazil", result.getCountry());
        assertEquals("12345", result.getPincode());

        verify(addressRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(address, AddressDTO.class);
    }

    @Test
    void shouldReturnAListOfAddressessWhenProvindingAUser() {
        List<Address> addresses = user.getAddresses();
        when(modelMapper.map(any(Address.class), eq(AddressDTO.class))).thenReturn(addressDTO);

        List<AddressDTO> response = addressService.getUserAddressess(user);

        AddressDTO result = response.get(0);

        assertEquals(1L, result.getAddressId());
        assertEquals("Avenida brasil", result.getStreet());
        assertEquals("Edificio do centro", result.getBuildingName());
        assertEquals("Sao paulo", result.getCity());
        assertEquals("Sao paulo", result.getState());
        assertEquals("Brazil", result.getCountry());
        assertEquals("12345", result.getPincode());

        verify(modelMapper, times(1)).map(address, AddressDTO.class);

    }

    @Test
    void shouldUpdateAnAddressWithSuccess() {
        Long addressId = 2L;

        Address addressUpdated = new Address();
        addressUpdated.setAddressId(addressId);
        addressUpdated.setStreet("Rua antiga");
        addressUpdated.setBuildingName("Edificio antigo");
        addressUpdated.setUser(user);

        user.setAddresses(new ArrayList<>(List.of(addressUpdated)));

        AddressDTO addressUpdatedDTO = new AddressDTO();
        addressUpdatedDTO.setAddressId(addressId);
        addressUpdatedDTO.setStreet("Rua nova");
        addressUpdatedDTO.setBuildingName("Edificio novo");

        AddressDTO responseDTO = new AddressDTO();
        responseDTO.setAddressId(addressId);
        responseDTO.setStreet("Rua nova");
        responseDTO.setBuildingName("Edificio novo");

        when(addressRepository.findById(addressId)).thenReturn(Optional.of(addressUpdated));
        when(addressRepository.save(addressUpdated)).thenReturn(addressUpdated);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(any(Address.class), eq(AddressDTO.class))).thenReturn(responseDTO);

        AddressDTO addressDTOUpdated = addressService.updateAddress(addressId, addressUpdatedDTO);

        assertEquals("Rua nova", addressDTOUpdated.getStreet());
        assertEquals("Edificio novo", addressDTOUpdated.getBuildingName());

        verify(addressRepository, times(1)).findById(addressId);
        verify(addressRepository, times(1)).save(addressUpdated);
        verify(modelMapper, times(1)).map(addressUpdated, AddressDTO.class);
    }





}