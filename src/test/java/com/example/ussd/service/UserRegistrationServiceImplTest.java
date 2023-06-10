package com.example.ussd.service;

import com.example.ussd.domain.UserDetails;
import com.example.ussd.domain.dto.UssdRequest;
import com.example.ussd.exception.CustomException;
import com.example.ussd.integrations.MessageService;
import com.example.ussd.repository.UserDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static com.example.ussd.Constants.REGISTER_USER_MENU;
import static com.example.ussd.Constants.RETRIEVE_USER_DETAIL_MENU;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class UserRegistrationServiceImplTest {
    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private MessageService messageService;

    private UserRegistrationServiceImpl userRegistrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userRegistrationService = new UserRegistrationServiceImpl(userDetailsRepository, messageService);
    }

    @Test
    void testPerformUssdRequest_WhenTextIsEmpty() throws Exception {
        UssdRequest request = new UssdRequest();
        request.setText("");
        String expectedResponse = "CON What would you like to do?\n1. Register User\n2. Check Account\n";

        String response;
        response = userRegistrationService.performUssdRequest(request);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void testRegisterUser() throws Exception {
        // given
        String phoneNumber = "1234567890";
        String name = "John Doe";
        String age = "30";
        UserDetails savedUser = UserDetails.builder().name(name).phoneNumber(phoneNumber).age(Integer.parseInt(age)).build();
        //when
        Mockito.when(userDetailsRepository.save(any(UserDetails.class)))
                .thenReturn(savedUser);
        String result = userRegistrationService.registerUser(phoneNumber, name, age);

        // then
        assertEquals(savedUser.toString(), result);
        Mockito.verify(userDetailsRepository, Mockito.times(1)).save(any(UserDetails.class));
        Mockito.verify(messageService, Mockito.times(1)).send(phoneNumber);
    }


    @Test
    public void testFetchUser_UserExists_ReturnsUserDetails() {
        // Given
        String phoneNumber = "1234567890";
        String name = "John Doe";
        String age = "30";
        UserDetails expectedUser = UserDetails.builder().name(name).phoneNumber(phoneNumber).age(Integer.parseInt(age)).build();

        // When
        Mockito.when(userDetailsRepository.findByPhoneNumber(anyString()))
                .thenReturn(expectedUser);
        UserDetails actualUser = userRegistrationService.fetchUser(phoneNumber);

        // Then
        assertEquals(expectedUser, actualUser);
        Mockito.verify(userDetailsRepository, Mockito.times(1))
                .findByPhoneNumber(phoneNumber);
    }

    @Test
    public void testFetchUser_UserDoesNotExist_ThrowsCustomException() {
        //given
        String phoneNumber = "1234567890";
        //when
        Mockito.when(userDetailsRepository.findByPhoneNumber(anyString()))
                .thenReturn(null);
        //then
        assertThrows(CustomException.class,
                () -> userRegistrationService.fetchUser(phoneNumber));
        Mockito.verify(userDetailsRepository, Mockito.times(1))
                .findByPhoneNumber(phoneNumber);
    }
    @Test
    void performUssdRequest_WhenTextIsTwo_ReturnsRetrieveUserDetailMenu() throws Exception {
        // given
        UssdRequest request = UssdRequest.builder().text("2").build();
        String expectedResponse = RETRIEVE_USER_DETAIL_MENU;

        // when
        String response = userRegistrationService.performUssdRequest(request);

        // then
        assertEquals(expectedResponse, response);
    }

    @Test
    void performUssdRequest_WhenTextIsOne_ReturnsRegisterUserMenu() throws Exception {
        // given
        UssdRequest request = UssdRequest.builder().text("1").build();
        String expectedResponse = REGISTER_USER_MENU;

        // when
        String response = userRegistrationService.performUssdRequest(request);

        // then
        assertEquals(expectedResponse, response);
    }

}