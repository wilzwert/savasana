package com.openclassrooms.starterjwt.controllers;


import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:14:09
 */

@Tag("User")
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Nested
    class UserControllerFindTest {
        @Test
        public void shouldFindUserById() {
            User user = new User();
            user.setId(1L);
            user.setFirstName("Test");
            user.setLastName("User");

            UserDto userDto = new UserDto();
            userDto.setId(1L);
            userDto.setFirstName("Test");
            userDto.setLastName("User");

            when(userService.findById(1L)).thenReturn(user);
            when(userMapper.toDto(user)).thenReturn(userDto);

            ResponseEntity<?> responseEntity = userController.findById("1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody()).isSameAs(userDto);
        }

        @Test
        public void shouldReturnBadRequestWhenBadIdFormat() {
            ResponseEntity<?> responseEntity = userController.findById("badId1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldReturnNotFoundWhenNotFound() {
            when(userService.findById(1L)).thenReturn(null);

            ResponseEntity<?> responseEntity = userController.findById("1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    class UserControllerDeleteTest {

        @Test
        public void shouldDeleteUser() {
            User user = new User();
            user.setId(1L);
            user.setFirstName("Test");
            user.setLastName("User");
            user.setEmail("test@example.com");

            UserDetails userDetails = UserDetailsImpl.builder().username("test@example.com").build();

            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            SecurityContextHolder.setContext(securityContext);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userService.findById(1L)).thenReturn(user);
            doNothing().when(userService).delete(anyLong());

            ResponseEntity<?> responseEntity = userController.save("1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        public void shouldReturnUnauthorizedWhenNoAuthentication() {
            User user = new User();
            user.setId(1L);
            user.setFirstName("Test");
            user.setLastName("User");
            user.setEmail("test@example.com");

            UserDetails userDetails = UserDetailsImpl.builder().username("test-user@example.com").build();

            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            SecurityContextHolder.setContext(securityContext);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userService.findById(1L)).thenReturn(user);

            ResponseEntity<?> responseEntity = userController.save("1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        @Test
        public void shouldReturnBadRequestWhenBadIdFormat() {
            ResponseEntity<?> responseEntity = userController.save("badId1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldReturnNotFoundWhenNotFound() {
            when(userService.findById(1L)).thenReturn(null);

            ResponseEntity<?> responseEntity = userController.save("1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}
