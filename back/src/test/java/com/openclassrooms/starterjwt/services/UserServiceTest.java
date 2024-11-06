package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 * Date:31/10/2024
 * Time:14:23
 */

@ExtendWith(MockitoExtension.class)
@Tag("UserService")
@DisplayName("Testing user business service")
public class UserServiceTest {

    // under test
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    /*
    @Disabled
    @Test
    public void shouldCreateUser() {
        User user = new User();
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.create(user);

        verify(userRepository).save(user);
        assertThat(createdUser).isNotNull().isEqualTo(user);
    }*/


    @Test
    public void shouldFindAnExistingUserByItsId() {
        User user = new User();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User foundUser = userService.findById(1L);

        verify(userRepository).findById(1L);
        assertThat(foundUser).isNotNull().isEqualTo(user);
    }

    @Test
    public void shouldReturnNullIfUserDoesNotExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        User foundUser = userService.findById(1L);

        verify(userRepository).findById(1L);
        assertThat(foundUser).isNull();
    }

    @Test
    public void shouldDeleteUser() {
        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }
}