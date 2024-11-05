package com.openclassrooms.starterjwt.mapper;


import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:14:39
 */

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Tag("Mapper")
public class SessionMapperTest {

    @MockBean
    private UserService userService;

    @MockBean
    private TeacherService teacherService;

    @Autowired
    private SessionMapper sessionMapper;

    @Test
    public void testNullEntityToDto() {
        Session session = null;

        SessionDto sessionDto = sessionMapper.toDto(session);

        assertThat(sessionDto).isNull();
    }

    @Test
    public void testEntityWithoutTeacherToDto() {
        Session session = new Session().setId(1L);

        SessionDto sessionDto = sessionMapper.toDto(session);

        assertThat(sessionDto).isNotNull();
        assertThat(sessionDto.getId()).isEqualTo(session.getId());
        assertThat(sessionDto.getTeacher_id()).isNull();
    }

    @Test
    public void testEntityWithoutUsersToDto() {
        Session session = new Session().setId(1L);

        SessionDto sessionDto = sessionMapper.toDto(session);

        assertThat(sessionDto).isNotNull();
        assertThat(sessionDto.getId()).isEqualTo(session.getId());
        assertThat(sessionDto.getUsers().size()).isEqualTo(0);
    }

    @Test
    public void testEntityToDto() {
        Session session = new Session()
                .setId(1L)
                .setName("test session")
                .setDescription("this is a test session")
                .setTeacher(new Teacher().setId(1L).setFirstName("Test").setLastName("Teacher"))
                .setUsers(Arrays.asList(
                    new User().setId(1L).setFirstName("Test").setLastName("Session"),
                    new User().setId(2L).setFirstName("Other").setLastName("Session")
                ))
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());
        SessionDto sessionDto = sessionMapper.toDto(session);

        assertThat(sessionDto).isNotNull();
        assertThat(sessionDto.getId()).isEqualTo(session.getId());
        assertThat(sessionDto.getName()).isEqualTo(session.getName());
        assertThat(sessionDto.getDescription()).isEqualTo(session.getDescription());
        assertThat(sessionDto.getTeacher_id()).isEqualTo(session.getTeacher().getId());
        assertThat(sessionDto.getUsers()).containsExactlyElementsOf(session.getUsers().stream().map(User::getId).collect(Collectors.toList()));
        assertThat(sessionDto.getCreatedAt()).isEqualTo(session.getCreatedAt());
        assertThat(sessionDto.getUpdatedAt()).isEqualTo(session.getUpdatedAt());
    }

    @Test
    public void testNullEntityListToDto() {
        List<Session> sessions = null;

        List<SessionDto> sessionDtos = sessionMapper.toDto(sessions);

        assertThat(sessionDtos).isNull();
    }

    @Test
    public void testEntityListToDtoList() {
        List<Session> sessions = new ArrayList<>();
        List<User> users = Collections.singletonList(new User().setId(1L).setFirstName("Test").setLastName("User"));
        List<List<Long>> expectedUsers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Session session = new Session();
            session.setId((long) i);
            session.setTeacher(new Teacher().setId((long)i).setFirstName("Test"+i).setLastName("Teacher"+i));
            session.setUsers(users);
            session.setName("Test session "+i);
            session.setCreatedAt(LocalDateTime.now());
            session.setUpdatedAt(LocalDateTime.now());
            sessions.add(session);
            expectedUsers.add(Collections.singletonList(1L));
        }

        List<SessionDto> sessionDtos = sessionMapper.toDto(sessions);

        assertThat(sessionDtos).isNotNull();

        assertThat(sessionDtos).extracting(SessionDto::getId).containsExactlyElementsOf(sessions.stream().map(Session::getId).collect(Collectors.toList()));
        assertThat(sessionDtos).extracting(SessionDto::getCreatedAt).containsExactlyElementsOf(sessions.stream().map(Session::getCreatedAt).collect(Collectors.toList()));
        assertThat(sessionDtos).extracting(SessionDto::getUpdatedAt).containsExactlyElementsOf(sessions.stream().map(Session::getUpdatedAt).collect(Collectors.toList()));
        assertThat(sessionDtos).extracting(SessionDto::getTeacher_id).containsExactlyElementsOf(sessions.stream().map(s -> s.getTeacher().getId()).collect(Collectors.toList()));
        assertThat(sessionDtos).extracting(SessionDto::getUsers).isEqualTo(expectedUsers);
    }

    @Test
    public void testNullDtoToEntity() {
        SessionDto sessionDto = null;

        Session session = sessionMapper.toEntity(sessionDto);

        assertThat(session).isNull();
    }

    @Test
    public void testDtoWithoutTeacherToEntity() {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Test session");
        sessionDto.setId(1L);
        Session session = sessionMapper.toEntity(sessionDto);

        assertThat(session).isNotNull();
        assertThat(session.getId()).isEqualTo(sessionDto.getId());
        assertThat(session.getTeacher()).isNull();
    }

    @Test
    public void testDtoWithoutUsersToEntity() {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Test session");
        sessionDto.setId(1L);

        Session session = sessionMapper.toEntity(sessionDto);

        assertThat(session).isNotNull();
        assertThat(session.getId()).isEqualTo(sessionDto.getId());
        assertThat(session.getUsers().size()).isEqualTo(0);
    }

    @Test
    public void testDtoToEntity() {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setName("Test session");
        sessionDto.setId(1L);
        sessionDto.setCreatedAt(LocalDateTime.now());
        sessionDto.setUpdatedAt(LocalDateTime.now());
        sessionDto.setTeacher_id(1L);
        sessionDto.setUsers(Arrays.asList(1L, 2L));

        when(userService.findById(1L)).thenReturn(new User().setId(1L));
        when(userService.findById(2L)).thenReturn(new User().setId(2L));
        when(teacherService.findById(1L)).thenReturn(new Teacher().setId(1L));

        Session session = sessionMapper.toEntity(sessionDto);

        assertThat(session).isNotNull();
        assertThat(session.getId()).isEqualTo(sessionDto.getId());
        assertThat(session.getCreatedAt()).isEqualTo(sessionDto.getCreatedAt());
        assertThat(session.getUpdatedAt()).isEqualTo(sessionDto.getUpdatedAt());
        assertThat(session.getTeacher().getId()).isEqualTo(sessionDto.getTeacher_id());
        assertThat(session.getUsers()).extracting(User::getId).containsExactlyElementsOf(sessionDto.getUsers());
    }

    @Test
    public void testNullDtoListToEntity() {
        List<SessionDto> sessionDtos = null;

        List<Session> sessions = sessionMapper.toEntity(sessionDtos);

        assertThat(sessions).isNull();
    }

    @Test
    public void testDtoListToEntityList() {
        LocalDateTime now = LocalDateTime.now();
        List<SessionDto> sessionDtos = new ArrayList<>();

        User user1 = new User().setId(1L).setFirstName("Test").setLastName("User");
        User user2 = new User().setId(2L).setFirstName("Other").setLastName("User");
        Teacher teacher1 = new Teacher().setId(1L).setFirstName("Test").setLastName("Teacher");
        Teacher teacher2 = new Teacher().setId(2L).setFirstName("Other").setLastName("Teacher");

        SessionDto sessionDto1 = new SessionDto();
        sessionDto1.setId(1L);
        sessionDto1.setTeacher_id(1L);
        sessionDto1.setUsers(Collections.singletonList(1L));
        sessionDto1.setName("Test session 1");
        sessionDto1.setCreatedAt(now);
        sessionDto1.setUpdatedAt(now);
        sessionDtos.add(sessionDto1);

        SessionDto sessionDto2 = new SessionDto();
        sessionDto2.setId(2L);
        sessionDto2.setTeacher_id(2L);
        sessionDto2.setUsers(Collections.singletonList(2L));
        sessionDto2.setName("Test session 2");
        sessionDto2.setCreatedAt(now);
        sessionDto2.setUpdatedAt(now);
        sessionDtos.add(sessionDto2);

        when(userService.findById(1L)).thenReturn(user1);
        when(userService.findById(2L)).thenReturn(user2);
        when(teacherService.findById(1L)).thenReturn(teacher1);
        when(teacherService.findById(2L)).thenReturn(teacher2);

        List<Session> sessions = sessionMapper.toEntity(sessionDtos);

        assertThat(sessions).isNotNull();
        assertThat(sessions).extracting(Session::getId).containsExactly(1L, 2L);
        assertThat(sessions).extracting(Session::getCreatedAt).containsExactly(now, now);
        assertThat(sessions).extracting(Session::getUpdatedAt).containsExactly(now, now);
        assertThat(sessions.get(0).getUsers()).extracting(User::getId).containsExactly(1L);
        assertThat(sessions.get(1).getUsers()).extracting(User::getId).containsExactly(2L);
        assertThat(sessions).extracting(Session::getTeacher).extracting(Teacher::getId).containsExactly(1L, 2L);
    }
}
