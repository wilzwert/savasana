package com.openclassrooms.starterjwt.controllers;


import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.aspectj.bridge.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/11/2024
 * Time:11:59
 */
@Tag("Teacher")
@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    @InjectMocks
    private TeacherController teacherController;

    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherMapper teacherMapper;

    @Test
    public void testFindTeacherById() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setFirstName("Test");
        teacherDto.setLastName("Teacher");

        when(teacherService.findById(1L)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        ResponseEntity<?> responseEntity = teacherController.findById("1");

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isSameAs(teacherDto);
    }

    @Test
    public void testBadIdFormat() {
        ResponseEntity<?> responseEntity = teacherController.findById("badId1");

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testTeacherNotFound() {
        when(teacherService.findById(1L)).thenReturn(null);

        ResponseEntity<?> responseEntity = teacherController.findById("1");

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testFindAll() {
        Teacher teacher1 = new Teacher().setId(1L).setFirstName("Test").setLastName("Teacher");
        Teacher teacher2 = new Teacher().setId(2L).setFirstName("Other").setLastName("Teacher");
        List<Teacher> teachers = Arrays.asList(teacher1, teacher2);

        TeacherDto teacherDto1 = new TeacherDto();
        teacherDto1.setId(teacher1.getId());
        teacherDto1.setFirstName(teacher1.getFirstName());
        teacherDto1.setLastName(teacher1.getLastName());

        TeacherDto teacherDto2 = new TeacherDto();
        teacherDto2.setId(teacher2.getId());
        teacherDto2.setFirstName(teacher2.getFirstName());
        teacherDto2.setLastName(teacher2.getLastName());

        List<TeacherDto> teacherDtos = Arrays.asList(teacherDto1, teacherDto2);

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

        ResponseEntity<?> responseEntity = teacherController.findAll();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isSameAs(teacherDtos);
    }
}
