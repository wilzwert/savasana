package com.openclassrooms.starterjwt.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:10:53
 */

@SpringBootTest
@AutoConfigureMockMvc
@Tag("Integration")
public class AuthControllerIT {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final static String LOGIN_URL = "/api/auth/login";
    private final static String REGISTER_URL = "/api/auth/register";

    @Nested
    class AuthControllerAuthenticateIT {

        @Test
        public void shouldReturnBadRequestWhenRequestBodyEmpty() throws Exception {
            mockMvc.perform(post(LOGIN_URL))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenRequestBodyInvalid() throws Exception {
            // no password
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("test@example.com");
            mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnUnauthorizedWhenUserNotFound() throws Exception {
            // no password
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("test@example.com");
            loginRequest.setPassword("abcd1234");
            mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        public void shouldReturnJwtResponse() throws Exception {
            // no password
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail("test@example.com");
            loginRequest.setPassword("abcd1234");

            User user = new User()
                    .setId(1L)
                    .setEmail("test@example.com")
                    .setPassword(passwordEncoder.encode("abcd1234"))
                    .setFirstName("Test")
                    .setLastName("User")
                    .setAdmin(true);

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

            MvcResult result = mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andReturn();
            String json = result.getResponse().getContentAsString();
            JwtResponse response = objectMapper.readValue(json, JwtResponse.class);
            assertThat(response.getType()).isEqualTo("Bearer");
            assertThat(response.getToken()).isNotEmpty();
            assertThat(response.getId()).isEqualTo(user.getId());
            assertThat(response.getUsername()).isEqualTo("test@example.com");
            assertThat(response.getAdmin()).isTrue();
            assertThat(response.getFirstName()).isEqualTo("Test");
            assertThat(response.getLastName()).isEqualTo("User");
        }
    }

    @Nested
    class AuthControllerRegisterIT {

        private SignupRequest signupRequest;

        @BeforeEach
        public void setup() {
            // setup a default valid signup request
            signupRequest = new SignupRequest();
            signupRequest.setEmail("test@example.com");
            signupRequest.setFirstName("Test");
            signupRequest.setLastName("User");
            signupRequest.setPassword("abcd1234");
        }

        @Test
        public void shouldReturnBadRequestWhenRequestBodyEmpty() throws Exception {
            mockMvc.perform(post(REGISTER_URL))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenEmailEmpty() throws Exception {
            signupRequest.setEmail("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenEmailInvalid() throws Exception {
            signupRequest.setEmail("test");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenEmailTooLong() throws Exception {
            signupRequest.setEmail("testingaverylongemailaddress@averylongtestdomain.com");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenFirstNameEmpty() throws Exception {
            signupRequest.setFirstName("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenFirstNameTooShort() throws Exception {
            signupRequest.setFirstName("Te");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenFirstNameTooLong() throws Exception {
            signupRequest.setFirstName("Teststoolongfirstname");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenLastNameEmpty() throws Exception {
            signupRequest.setLastName("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenLastNameTooShort() throws Exception {
            signupRequest.setLastName("Us");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenLastNameTooLong() throws Exception {
            signupRequest.setLastName("Testingtoolonglastname");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenPasswordEmpty() throws Exception {
            signupRequest.setPassword("");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenPasswordTooShort() throws Exception {
            signupRequest.setPassword("pass");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenPasswordTooLong() throws Exception {
            signupRequest.setLastName("Testingtoolongpasswordinaresgisterrequest");
            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void shouldReturnBadRequestWhenUserAlreadyExists() throws Exception {
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("message").value("Error: Email is already taken!"));
        }

        @Test
        public void shouldRegisterUser() throws Exception {
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

            mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("message").value("User registered successfully!"));
        }
    }
}
