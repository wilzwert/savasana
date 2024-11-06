package com.openclassrooms.starterjwt.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Wilhelm Zwertvaegher
 * Date:04/11/2024
 * Time:13:44
 */

@SpringBootTest
@AutoConfigureMockMvc
@Tag("Integration")
public class SessionControllerIT {

    @MockBean
    private SessionRepository sessionRepository;

    @MockBean
    private TeacherRepository teacherRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionMapper sessionMapper;

    @Nested
    class SessionControllerGetIT {

        @Test
        public void shouldDenySessionReadWhenUserNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/session").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldReturnBadRequestWhenBadId() throws Exception {
            mockMvc.perform(get("/api/session/badId").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldGetAllSessions() throws Exception {
            Session session1 = new Session();
            session1.setId(1L);
            session1.setName("Session 1");

            Session session2 = new Session();
            session2.setId(2L);
            session2.setName("Session 2");

            List<Session> sessions = Arrays.asList(session1, session2);
            when(sessionRepository.findAll()).thenReturn(sessions);

            mockMvc.perform(get("/api/session").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(session1.getId()))
                    .andExpect(jsonPath("$[0].name").value(session1.getName()))
                    .andExpect(jsonPath("$[1].id").value(session2.getId()))
                    .andExpect(jsonPath("$[1].name").value(session2.getName()))
            ;
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldGetASessionByItsId() throws Exception {
            Session session = new Session();
            session.setId(1L);
            session.setName("Session 1");

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

            mockMvc.perform(get("/api/session/1").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("id").value(session.getId()))
                    .andExpect(jsonPath("name").value(session.getName()));
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldReturnNotFountWhenSessionNotFound() throws Exception {
            when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/session/1").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class SessionControllerCreationIT {

        @Test
        public void shouldDenySessionCreationWhenUserNotAuthenticated() throws Exception {
            mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldCreateSessionWhenBodyIsValid() throws Exception {
            SessionDto sessionDto = new SessionDto();
            sessionDto.setName("New Session");
            sessionDto.setDate(new Date());
            sessionDto.setTeacher_id(1L);
            sessionDto.setDescription("Session Description");

            Session session = sessionMapper.toEntity(sessionDto);
            session.setId(1L);

            when(teacherRepository.findById(anyLong())).thenReturn(Optional.of(new Teacher().setId(1L)));
            when(sessionRepository.save(any(Session.class))).thenReturn(session);

            MvcResult result = mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(sessionDto)))
                    .andExpect(status().isOk())
                    .andReturn();
            String json = result.getResponse().getContentAsString();
            SessionDto createdSession = objectMapper.readValue(json, SessionDto.class);
            assertThat(createdSession).isNotNull();
            assertThat(createdSession.getId()).isEqualTo(session.getId());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldNotCreateSessionWhenBodyNotValid() throws Exception {
            // no name and description in dto but @NotBlank
            SessionDto sessionDto = new SessionDto();
            sessionDto.setDate(new Date());
            sessionDto.setTeacher_id(1L);

            mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(sessionDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class SessionControllerUpdateIT {
        @Test
        public void shouldDenySessionUpdateWhenUserNotAuthenticated() throws Exception {
            mockMvc.perform(put("/api/session/1").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldReturnBadRequestOnUpdateWhenBadId() throws Exception {
            mockMvc.perform(put("/api/session/badId").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldReturnNotFoundOnUpdateWhenSessionNotFound() throws Exception {
            when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/session/1").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldUpdateSessionWhenBodyIsValid() throws Exception {
            // valid body
            SessionDto sessionDto = new SessionDto();
            sessionDto.setName("Updated Session");
            sessionDto.setDate(new Date());
            sessionDto.setTeacher_id(2L);
            sessionDto.setDescription("Updated Session Description");

            // Session to update
            Session session = new Session();
            session.setId(1L);
            session.setTeacher(new Teacher().setId(1L));
            session.setName("Session");
            session.setDate(new Date());
            session.setDescription("Session Description");

            // Saved Session resulting form the update
            Session savedSession = sessionMapper.toEntity(sessionDto);
            savedSession.setId(1L);
            savedSession.setTeacher(new Teacher().setId(2L));

            when(teacherRepository.findById(1L)).thenAnswer(args -> Optional.of(new Teacher().setId(args.getArgument(0))));
            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(sessionRepository.save(any(Session.class))).thenReturn(savedSession);

            MvcResult result = mockMvc.perform(put("/api/session/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(sessionDto)))
                    .andExpect(status().isOk())
                    .andReturn();
            String json = result.getResponse().getContentAsString();

            // data returned in the json response should match data sent in the request
            SessionDto updatedSessionDto = objectMapper.readValue(json, SessionDto.class);
            assertThat(updatedSessionDto).isNotNull();
            assertThat(updatedSessionDto.getId()).isEqualTo(session.getId());
            assertThat(updatedSessionDto.getName()).isEqualTo(sessionDto.getName());
            assertThat(updatedSessionDto.getDescription()).isEqualTo(sessionDto.getDescription());
            assertThat(updatedSessionDto.getTeacher_id()).isEqualTo(sessionDto.getTeacher_id());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldNotUpdateSessionWhenBodyNotValid() throws Exception {
            // no name and description in dto but @NotBlank
            SessionDto sessionDto = new SessionDto();
            sessionDto.setDate(new Date());
            sessionDto.setTeacher_id(1L);

            mockMvc.perform(put("/api/session/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(sessionDto)))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Nested
    class SessionControllerDeleteIT {
        @Test
        public void shouldDenySessionDeleteWhenUserNotAuthenticated() throws Exception {
            mockMvc.perform(delete("/api/session/1").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldReturnBadRequestOnDeleteWhenBadId() throws Exception {
            mockMvc.perform(delete("/api/session/badId").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldReturnNotFoundOnDeleteWhenSessionNotFound() throws Exception {
            when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());

            mockMvc.perform(delete("/api/session/1").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }


        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldDeleteSession() throws Exception {
            Session session = new Session();
            session.setId(1L);
            session.setName("Session 1");

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

            mockMvc.perform(delete("/api/session/1"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class SessionControllerParticipateIT {

        @Test
        public void shouldDenyParticipateWhenUserNotAuthenticated() throws Exception {
            mockMvc.perform(post("/api/session/1/participate/2").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldReturnBadRequestOnParticipateWhenBadId() throws Exception {
            mockMvc.perform(post("/api/session/badId/participate/any").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldReturnNotFoundOnParticipateWhenUserNotFound() throws Exception {
            Session session = new Session();
            session.setId(1L);
            session.setName("Session 1");

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/session/1/participate/2"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldReturnNotFoundOnParticipateWhenSessionNotFound() throws Exception {
            User user = new User();
            user.setId(1L);

            when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            mockMvc.perform(post("/api/session/1/participate/2"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldParticipate() throws Exception {
            Session session = new Session();
            session.setId(1L);
            session.setName("Session 1");
            session.setUsers(new ArrayList<>());

            User user = new User();
            user.setId(1L);

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(userRepository.findById(2L)).thenReturn(Optional.of(user));

            mockMvc.perform(post("/api/session/1/participate/2"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldReturnBadRequestOnParticipateIfAlreadyParticipates() throws Exception {
            User user = new User();
            user.setId(2L);

            Session session = new Session();
            session.setId(1L);
            session.setName("Session 1");
            session.setUsers(Collections.singletonList(user));

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(userRepository.findById(2L)).thenReturn(Optional.of(user));

            mockMvc.perform(post("/api/session/1/participate/2"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class SessionControllerUnparticipateIT {

        @Test
        public void shouldDenyUnparticipateWhenUserNotAuthenticated() throws Exception {
            mockMvc.perform(delete("/api/session/1/participate/2").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldReturnBadRequestOnUnparticipateWhenBadId() throws Exception {
            mockMvc.perform(delete("/api/session/badId/participate/any").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldReturnNotFoundOnUnparticipateWhenSessionNotFound() throws Exception {
            User user = new User();
            user.setId(1L);

            when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            mockMvc.perform(delete("/api/session/1/participate/2"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldUnparticipate() throws Exception {
            User user = new User();
            user.setId(2L);

            Session session = new Session();
            session.setId(1L);
            session.setName("Session 1");
            session.setUsers(Collections.singletonList(user));

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(userRepository.findById(2L)).thenReturn(Optional.of(user));


            mockMvc.perform(delete("/api/session/1/participate/2"))
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        public void shouldReturnBadRequestOnUnparticipateIfNoParticipationFound() throws Exception {
            Session session = new Session();
            session.setId(1L);
            session.setName("Session 1");
            session.setUsers(new ArrayList<>());

            User user = new User();
            user.setId(1L);

            when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(userRepository.findById(2L)).thenReturn(Optional.of(user));

            mockMvc.perform(delete("/api/session/1/participate/2"))
                    .andExpect(status().isBadRequest());
        }
    }


}
