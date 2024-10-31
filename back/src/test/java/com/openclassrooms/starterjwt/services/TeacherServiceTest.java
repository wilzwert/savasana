package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 * Date:31/10/2024
 * Time:14:23
 */

@ExtendWith(MockitoExtension.class)
@Tag("TeacherService")
@DisplayName("Testing teacher business service")
public class TeacherServiceTest {

    // under test
    @InjectMocks
    private TeacherService teacherService;

    @Mock
    private TeacherRepository teacherRepository;

    @Test
    public void shouldFindAllTeachers() {
        List<Teacher> teachers = Arrays.asList(new Teacher(), new Teacher());
        when(teacherRepository.findAll()).thenReturn(teachers);

        List<Teacher> allTeachers = teacherService.findAll();

        verify(teacherRepository).findAll();
        assertNotNull(allTeachers);
        assertThat(allTeachers).isEqualTo(teachers);
    }

    @Test
    public void shouldFindAnExistingSessionByItsId() {
        Teacher teacher = new Teacher();
        when(teacherRepository.findById(anyLong())).thenReturn(Optional.of(teacher));

        Teacher foundTeacher = teacherService.findById(1L);

        verify(teacherRepository).findById(1L);
        assertThat(foundTeacher).isNotNull().isEqualTo(teacher);
    }

    @Test
    public void shouldReturnNullIfTeacherDoesNotExist() {
        when(teacherRepository.findById(anyLong())).thenReturn(Optional.empty());

        Teacher teacher = teacherService.findById(1L);

        verify(teacherRepository).findById(1L);
        assertThat(teacher).isNull();
    }
}