package com.openclassrooms.starterjwt.controllers;


import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;
import static org.hamcrest.Matchers.hasSize;
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
public class TeacherControllerIT {

    @MockBean
    private TeacherRepository teacherRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldDenyTeacherReadWhenUserNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/teacher").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldReturnBadRequestWhenBadId() throws Exception {
        mockMvc.perform(get("/api/teacher/badId").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldGetAllSessions() throws Exception {
        Teacher teacher1 = new Teacher();
        teacher1.setId(1L);
        teacher1.setFirstName("Test");
        teacher1.setLastName("Teacher");

        Teacher teacher2 = new Teacher();
        teacher2.setId(2L);
        teacher2.setFirstName("Second");
        teacher2.setLastName("Teacher");

        List<Teacher> teachers = Arrays.asList(teacher1, teacher2);
        when(teacherRepository.findAll()).thenReturn(teachers);

        mockMvc.perform(get("/api/teacher").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(teacher1.getId()))
                .andExpect(jsonPath("$[0].firstName").value(teacher1.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(teacher1.getLastName()))
                .andExpect(jsonPath("$[1].id").value(teacher2.getId()))
                .andExpect(jsonPath("$[1].firstName").value(teacher2.getFirstName()))
                .andExpect(jsonPath("$[1].lastName").value(teacher2.getLastName()))
        ;
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldGetATeacherByItsId() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        mockMvc.perform(get("/api/teacher/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(teacher.getId()))
                .andExpect(jsonPath("firstName").value(teacher.getFirstName()))
                .andExpect(jsonPath("lastName").value(teacher.getLastName()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void shouldReturnNotFountWhenTeacherNotFound() throws Exception {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/teacher/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
