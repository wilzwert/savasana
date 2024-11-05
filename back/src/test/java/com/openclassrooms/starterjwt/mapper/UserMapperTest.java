package com.openclassrooms.starterjwt.mapper;


import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:14:39
 */

@SpringBootTest
@Tag("Mapper")
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testEntityToDto() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setAdmin(true);
        user.setPassword("password");
        user.setId(1L);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        UserDto userDto = userMapper.toDto(user);

        assertThat(userDto).isNotNull();
        assertThat(userDto.getId()).isEqualTo(user.getId());
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(userDto.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(userDto.getLastName()).isEqualTo(user.getLastName());
        assertThat(userDto.getPassword()).isEqualTo(user.getPassword());
        assertThat(userDto.isAdmin()).isEqualTo(user.isAdmin());
        assertThat(userDto.getCreatedAt()).isEqualTo(user.getCreatedAt());
        assertThat(userDto.getUpdatedAt()).isEqualTo(user.getUpdatedAt());
    }

    @Test
    public void testEntityListToDtoList() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setEmail("test@example.com"+i);
            user.setFirstName("Test "+i);
            user.setLastName("User "+i);
            user.setAdmin(i%2==0);
            user.setPassword("password"+i);
            user.setId((long) i);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            users.add(user);
        }

        System.out.println(users);

        List<UserDto> userDtos = userMapper.toDto(users);

        assertThat(userDtos).isNotNull();

        assertThat(userDtos).extracting(UserDto::getId).containsExactlyElementsOf(users.stream().map(User::getId).collect(Collectors.toList()));
        assertThat(userDtos).extracting(UserDto::getEmail).containsExactlyElementsOf(users.stream().map(User::getEmail).collect(Collectors.toList()));
        assertThat(userDtos).extracting(UserDto::getFirstName).containsExactlyElementsOf(users.stream().map(User::getFirstName).collect(Collectors.toList()));
        assertThat(userDtos).extracting(UserDto::getLastName).containsExactlyElementsOf(users.stream().map(User::getLastName).collect(Collectors.toList()));
        assertThat(userDtos).extracting(UserDto::getPassword).containsExactlyElementsOf(users.stream().map(User::getPassword).collect(Collectors.toList()));
        assertThat(userDtos).extracting(UserDto::isAdmin).containsExactlyElementsOf(users.stream().map(User::isAdmin).collect(Collectors.toList()));
        assertThat(userDtos).extracting(UserDto::getCreatedAt).containsExactlyElementsOf(users.stream().map(User::getCreatedAt).collect(Collectors.toList()));
        assertThat(userDtos).extracting(UserDto::getUpdatedAt).containsExactlyElementsOf(users.stream().map(User::getUpdatedAt).collect(Collectors.toList()));
    }

    @Test
    public void testDtoToEntity() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setFirstName("Test");
        userDto.setLastName("User");
        userDto.setAdmin(true);
        userDto.setPassword("password");
        userDto.setId(1L);
        userDto.setCreatedAt(LocalDateTime.now());
        userDto.setUpdatedAt(LocalDateTime.now());

        User user = userMapper.toEntity(userDto);

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(userDto.getId());
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(user.getFirstName()).isEqualTo(userDto.getFirstName());
        assertThat(user.getLastName()).isEqualTo(userDto.getLastName());
        assertThat(user.isAdmin()).isEqualTo(userDto.isAdmin());
        assertThat(user.getPassword()).isEqualTo(userDto.getPassword());
        assertThat(user.getCreatedAt()).isEqualTo(userDto.getCreatedAt());
        assertThat(user.getUpdatedAt()).isEqualTo(userDto.getUpdatedAt());
    }

    @Test
    public void testDtoListToEntityList() {
        List<UserDto> userDtos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserDto userDto = new UserDto();
            userDto.setId((long) i);
            userDto.setEmail("test@example.com"+i);
            userDto.setFirstName("Test "+i);
            userDto.setLastName("User "+i);
            userDto.setAdmin(i%2==0);
            userDto.setPassword("password"+i);
            userDto.setCreatedAt(LocalDateTime.now());
            userDto.setUpdatedAt(LocalDateTime.now());
            userDtos.add(userDto);
        }

        List<User> users = userMapper.toEntity(userDtos);

        assertThat(users).isNotNull();

        assertThat(users).extracting(User::getId).containsExactlyElementsOf(userDtos.stream().map(UserDto::getId).collect(Collectors.toList()));
        assertThat(users).extracting(User::getEmail).containsExactlyElementsOf(userDtos.stream().map(UserDto::getEmail).collect(Collectors.toList()));
        assertThat(users).extracting(User::getFirstName).containsExactlyElementsOf(userDtos.stream().map(UserDto::getFirstName).collect(Collectors.toList()));
        assertThat(users).extracting(User::getLastName).containsExactlyElementsOf(userDtos.stream().map(UserDto::getLastName).collect(Collectors.toList()));
        assertThat(users).extracting(User::isAdmin).containsExactlyElementsOf(userDtos.stream().map(UserDto::isAdmin).collect(Collectors.toList()));
        assertThat(users).extracting(User::getPassword).containsExactlyElementsOf(userDtos.stream().map(UserDto::getPassword).collect(Collectors.toList()));
        assertThat(users).extracting(User::getCreatedAt).containsExactlyElementsOf(userDtos.stream().map(UserDto::getCreatedAt).collect(Collectors.toList()));
        assertThat(users).extracting(User::getUpdatedAt).containsExactlyElementsOf(userDtos.stream().map(UserDto::getUpdatedAt).collect(Collectors.toList()));
    }
}
