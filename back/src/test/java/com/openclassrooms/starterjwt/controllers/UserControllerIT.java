package com.openclassrooms.starterjwt.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.time.LocalDateTime;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:04/11/2024
 * Time:13:44
 */

@SpringBootTest
@AutoConfigureMockMvc
@Tag("Integration")
public class UserControllerIT {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    JwtUtils jwtUtils;

    @Test
    public void shouldDenyUserReadWhenUserNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/user/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldDenyUserDeleteWhenUserNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/user/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldReturnBadRequestOnGetWhenBadId() throws Exception {
        mockMvc.perform(get("/api/user/badId").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldReturnBadRequestOnDeleteWhenBadId() throws Exception {
        mockMvc.perform(delete("/api/user/badId").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldGetAUserByItsId() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setAdmin(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPassword("abcd1234");
        // api endpoint is expected to return a UserDto with an empty password
        UserDto userDto = userMapper.toDto(user);
        userDto.setPassword(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        MvcResult result = mockMvc.perform(get("/api/user/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        UserDto foundUser = objectMapper.readValue(json, UserDto.class);
        assertThat(foundUser).isEqualTo(userDto);

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldReturnNotFoundWhenUserNotFound() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldReturnNotFoundOnDeleteWhenUserNotFound() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/user/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "yoga@studio.com", roles = {"ADMIN"})
    public void shouldDenyDeleteUserWhenNotEqualsAuthenticatedUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setEmail("test@example.com");
        user.setPassword("abcd1234");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldDeleteUserWhenBearerMatches() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setEmail("test@examplce.com");
        user.setPassword("abcd1234");

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = UserDetailsImpl.builder().username("test@example.com").build();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        String token = jwtUtils.generateJwtToken(authentication);

        mockMvc.perform(delete("/api/user/1") .header("authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com", password = "abcd1234", roles = {"ADMIN"})
    public void shouldDeleteUserWhenAuthenticationMatches() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setEmail("test@example.com");
        user.setPassword("abcd1234");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/user/1") )
                .andExpect(status().isOk());
    }
}
