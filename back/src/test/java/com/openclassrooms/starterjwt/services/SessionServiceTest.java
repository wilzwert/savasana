package com.openclassrooms.starterjwt.services;


import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 * Date:31/10/2024
 * Time:14:23
 */

@ExtendWith(MockitoExtension.class)
@Tag("SessionService")
@DisplayName("Testing session business service")
public class SessionServiceTest {

    // under test
    @InjectMocks
    private SessionService sessionService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Nested
    class CreateSession {
        @Test
        public void shouldCreateSession() {
            Session session = new Session();
            when(sessionRepository.save(any(Session.class))).thenReturn(session);

            Session createdSession = sessionService.create(session);

            verify(sessionRepository).save(session);
            assertThat(createdSession).isNotNull().isEqualTo(session);
        }
    }

    @Nested
    class GetSession {
        @Test
        public void shouldFindAllSessions() {
            List<Session> sessions = Arrays.asList(new Session(), new Session());
            when(sessionRepository.findAll()).thenReturn(sessions);

            List<Session> allSessions = sessionService.findAll();

            verify(sessionRepository).findAll();
            assertThat(allSessions).isNotNull().isEqualTo(sessions);
        }

        @Test
        public void shouldFindAnExistingSessionByItsId() {
            Session session = new Session();
            when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));

            Session foundSession = sessionService.getById(1L);

            verify(sessionRepository).findById(1L);
            assertThat(foundSession).isNotNull().isEqualTo(session);
        }

        @Test
        public void shouldReturnNullIfSessionDoesNotExist() {
            when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());

            Session foundSession = sessionService.getById(1L);

            verify(sessionRepository).findById(1L);
            assertThat(foundSession).isNull();
        }
    }

    @Nested
    class UpdateSession {
        @Test
        public void shouldUpdateSession() {
            Session session = new Session();
            session.setId(4L);
            when(sessionRepository.save(any(Session.class))).thenReturn(session);

            Session updatedSession = sessionService.update(1L, session);

            verify(sessionRepository).save(session);
            assertThat(updatedSession).isNotNull().isEqualTo(session);
        }
    }

    @Nested
    class DeleteSession {
        @Test
        public void shouldDeleteSession() {
            sessionService.delete(1L);
            verify(sessionRepository).deleteById(1L);
        }
    }

    @Nested
    class ParticipationSession {
        @Test
        public void shouldCreateParticipationSession() {
            Session session = new Session();
            session.setId(1L);
            session.setUsers(new ArrayList<>());
            User user = new User();
            user.setId(1L);

            when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(sessionRepository.save(any(Session.class))).thenReturn(session);

            sessionService.participate(1L, 1L);

            verify(sessionRepository).save(session);
            assertThat(session.getUsers()).isEqualTo(Collections.singletonList(user));
        }

        @Test
        public void shouldThrowNotFoundExceptionIfSessionNotFound() {
            when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

            assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 1L));
        }

        @Test
        public void shouldThrowNotFoundExceptionIfUserNotFound() {
            when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(new Session()));
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 1L));
        }

        @Test
        public void shouldThrowBadRequestExceptionIfUserAlreadyParticipating() {
            User user = new User();
            user.setId(1L);
            Session session = new Session();
            session.setId(1L);
            session.setUsers(Collections.singletonList(user));

            when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

            assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 1L));
        }
    }

    @Nested
    class DeleteParticipationSession {
        @Test
        public void shouldDeleteParticipationSession() {
            User user = new User();
            user.setId(1L);
            Session session = new Session();
            session.setId(1L);
            session.setUsers(Collections.singletonList(user));

            when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));
            when(sessionRepository.save(any(Session.class))).thenReturn(session);

            sessionService.noLongerParticipate(1L, 1L);

            verify(sessionRepository).save(session);
            assertThat(session.getUsers()).isEqualTo(new ArrayList<>());
        }

        @Test
        public void shouldThrowNotFoundExceptionIfSessionNotFound() {
            when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(1L, 1L));
        }

        @Test
        public void shouldBadRequestExceptionIfUserNotParticipating() {
            Session session = new Session();
            session.setId(1L);
            session.setUsers(new ArrayList<>());

            when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));

            assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 1L));
        }
    }
}