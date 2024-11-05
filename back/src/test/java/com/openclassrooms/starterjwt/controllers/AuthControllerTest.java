package com.openclassrooms.starterjwt.controllers;


import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Max;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:14:29
 */

@ExtendWith(MockitoExtension.class)
@Tag("Auth")
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void shouldAuthenticateAdminUser() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");
        User user = new User().setId(1L).setAdmin(true).setEmail("test@example.com").setFirstName("Test").setLastName("User");
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .username("test@example.com")
                .id(1L)
                .admin(true)
                .firstName("Test")
                .lastName("User")
                .build();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("access_token");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> responseEntity = authController.authenticateUser(loginRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        JwtResponse expectedResponse = new JwtResponse(
                "access_token",
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                userDetails.getAdmin()
        );

        JwtResponse actualResponse = (JwtResponse) responseEntity.getBody();
        if(actualResponse == null) {
            fail("Expected JWTResponse");
        }
        else {
            assertThat(actualResponse.getId()).isEqualTo(expectedResponse.getId());
            assertThat(actualResponse.getUsername()).isEqualTo(expectedResponse.getUsername());
            assertThat(actualResponse.getFirstName()).isEqualTo(expectedResponse.getFirstName());
            assertThat(actualResponse.getLastName()).isEqualTo(expectedResponse.getLastName());
            assertThat(actualResponse.getAdmin()).isEqualTo(expectedResponse.getAdmin());
        }
    }

    @Test
    public void shouldAuthenticateNonAdminUser() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");
        User user = new User().setId(1L).setAdmin(false).setEmail("test@example.com").setFirstName("Test").setLastName("User");
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .username("test@example.com")
                .id(1L)
                .admin(false)
                .firstName("Test")
                .lastName("User")
                .build();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("access_token");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> responseEntity = authController.authenticateUser(loginRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        JwtResponse expectedResponse = new JwtResponse(
                "access_token",
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                userDetails.getAdmin()
        );

        JwtResponse actualResponse = (JwtResponse) responseEntity.getBody();
        if(actualResponse == null) {
            fail("Expected JWTResponse");
        }
        else {
            assertThat(actualResponse.getId()).isEqualTo(expectedResponse.getId());
            assertThat(actualResponse.getUsername()).isEqualTo(expectedResponse.getUsername());
            assertThat(actualResponse.getFirstName()).isEqualTo(expectedResponse.getFirstName());
            assertThat(actualResponse.getLastName()).isEqualTo(expectedResponse.getLastName());
            assertThat(actualResponse.getAdmin()).isEqualTo(expectedResponse.getAdmin());
        }
    }

    @Test
    public void shouldAuthenticateNonAdminUserWhenUserIsNull() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .username("test@example.com")
                .id(1L)
                .admin(false)
                .firstName("Test")
                .lastName("User")
                .build();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("access_token");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = authController.authenticateUser(loginRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        JwtResponse expectedResponse = new JwtResponse(
                "access_token",
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                userDetails.getAdmin()
        );

        JwtResponse actualResponse = (JwtResponse) responseEntity.getBody();
        if(actualResponse == null) {
            fail("Expected JWTResponse");
        }
        else {
            assertThat(actualResponse.getId()).isEqualTo(expectedResponse.getId());
            assertThat(actualResponse.getUsername()).isEqualTo(expectedResponse.getUsername());
            assertThat(actualResponse.getFirstName()).isEqualTo(expectedResponse.getFirstName());
            assertThat(actualResponse.getLastName()).isEqualTo(expectedResponse.getLastName());
            assertThat(actualResponse.getAdmin()).isEqualTo(expectedResponse.getAdmin());
        }
    }

    @Test
    public void shouldRegisterUser() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setFirstName("Test");
        signupRequest.setLastName("User");
        signupRequest.setPassword("password");

        when(passwordEncoder.encode("password")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        ResponseEntity<?> responseEntity = authController.registerUser(signupRequest);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assertThat(messageResponse.getMessage()).isEqualTo("User registered successfully!");
    }

    @Test
    public void shouldDenyRegistrationWhenUserAlreadyExists() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        ResponseEntity<?> responseEntity = authController.registerUser(signupRequest);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assertThat(messageResponse.getMessage()).isEqualTo("Error: Email is already taken!");
    }
}
