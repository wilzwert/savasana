package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:11:59
 */
@Tag("Session")
@ExtendWith(MockitoExtension.class)
public class SessionControllerTest {

    @InjectMocks
    private SessionController sessionController;

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    @Nested
    class SessionControllerFindTest {
        @Test
        public void shouldFindSessionById() {
            Session session = new Session();
            session.setId(1L);
            session.setName("Test Session");
            SessionDto sessionDto = new SessionDto();
            sessionDto.setId(1L);

            when(sessionService.getById(1L)).thenReturn(session);
            when(sessionMapper.toDto(session)).thenReturn(sessionDto);

            ResponseEntity<?> responseEntity = sessionController.findById("1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody()).isSameAs(sessionDto);
        }

        @Test
        public void shouldReturnBadRequestWhenBadIdFormat() {
            ResponseEntity<?> responseEntity = sessionController.findById("badId1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldReturnNotFoundWhenNotFound() {
            when(sessionService.getById(1L)).thenReturn(null);

            ResponseEntity<?> responseEntity = sessionController.findById("1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        public void shouldFindAll() {
            Session session1 = new Session().setId(1L).setName("Test session");
            Session session2 = new Session().setId(2L).setName("Other test session");
            List<Session> sessions = Arrays.asList(session1, session2);

            SessionDto sessionDto1 = new SessionDto();
            sessionDto1.setId(session1.getId());
            sessionDto1.setName("Test session");

            SessionDto sessionDto2 = new SessionDto();
            sessionDto2.setId(session2.getId());
            sessionDto2.setName("Other test session");

            List<SessionDto> sessionDtos = Arrays.asList(sessionDto1, sessionDto2);

            when(sessionService.findAll()).thenReturn(sessions);
            when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

            ResponseEntity<?> responseEntity = sessionController.findAll();

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody()).isSameAs(sessionDtos);
        }
    }

    @Nested
    class SessionControllerCreateTest {
        @Test
        public void shouldCreateSession() {
            SessionDto requestSessionDto = new SessionDto();
            requestSessionDto.setName("Test session");

            Session session = new Session();
            session.setId(1L).setName("Test session").setCreatedAt(LocalDateTime.now()).setTeacher(new Teacher().setId(1L));

            SessionDto responseSessionDto = new SessionDto();
            responseSessionDto.setId(session.getId());
            responseSessionDto.setName(session.getName());
            responseSessionDto.setTeacher_id(session.getTeacher().getId());
            responseSessionDto.setCreatedAt(session.getCreatedAt());

            when(sessionService.create(any(Session.class))).thenReturn(session);
            when(sessionMapper.toEntity(requestSessionDto)).thenReturn(session);
            when(sessionMapper.toDto(session)).thenReturn(responseSessionDto);

            ResponseEntity<?> responseEntity = sessionController.create(requestSessionDto);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody()).isSameAs(responseSessionDto);
        }
    }

    @Nested
    class SessionControllerUpdateTest {
        @Test
        public void shouldReturnBadRequestWhenBadIdFormat() {
            SessionDto requestSessionDto = new SessionDto();
            requestSessionDto.setName("Updated test session");

            ResponseEntity<?> responseEntity = sessionController.update("badId1", requestSessionDto);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldUpdateSession() {
            SessionDto requestSessionDto = new SessionDto();
            requestSessionDto.setName("Updated test session");

            Session session = new Session();
            session.setId(1L).setName("Test session").setCreatedAt(LocalDateTime.now()).setTeacher(new Teacher().setId(1L));

            SessionDto responseSessionDto = new SessionDto();
            responseSessionDto.setId(session.getId());
            responseSessionDto.setName(session.getName());
            responseSessionDto.setTeacher_id(session.getTeacher().getId());
            responseSessionDto.setCreatedAt(session.getCreatedAt());

            when(sessionService.update(1L, session)).thenReturn(session);
            when(sessionMapper.toEntity(requestSessionDto)).thenReturn(session);
            when(sessionMapper.toDto(session)).thenReturn(responseSessionDto);

            ResponseEntity<?> responseEntity = sessionController.update("1", requestSessionDto);

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseEntity.getBody()).isSameAs(responseSessionDto);
        }
    }

    @Nested
    class SessionControllerDeleteTest {
        @Test
        public void shouldReturnBadRequestWhenBadIdFormat() {
            ResponseEntity<?> responseEntity = sessionController.save("badId1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldReturnBadRequestWhenSessionNotFound() {
            when(sessionService.getById(1L)).thenReturn(null);

            ResponseEntity<?> responseEntity = sessionController.save("1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        public void shouldDeleteSession() {
            Session session = new Session();
            session.setId(1L).setName("Test session").setCreatedAt(LocalDateTime.now()).setTeacher(new Teacher().setId(1L));

            when(sessionService.getById(1L)).thenReturn(session);

            ResponseEntity<?> responseEntity = sessionController.save("1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    class SessionControllerParticipateTest {
        @Test
        public void shouldReturnBadRequestWhenBadSessionIdFormat() {
            ResponseEntity<?> responseEntity = sessionController.participate("badId1", "1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldReturnBadRequestWhenBadUserIdFormat() {
            ResponseEntity<?> responseEntity = sessionController.participate("1", "badUserId1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }


        @Test
        public void shouldParticipate() {
            ResponseEntity<?> responseEntity = sessionController.participate("1", "1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    class SessionControllerNoLongerParticipateTest {
        @Test
        public void shouldReturnBadRequestWhenBadSessionIdFormat() {
            ResponseEntity<?> responseEntity = sessionController.noLongerParticipate("badId1", "1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldReturnBadRequestWhenBadUserIdFormat() {
            ResponseEntity<?> responseEntity = sessionController.noLongerParticipate("1", "badUserId1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        public void shouldNoLongerParticipate() {
            ResponseEntity<?> responseEntity = sessionController.noLongerParticipate("1", "1");

            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

    }
}
