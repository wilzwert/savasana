package com.openclassrooms.starterjwt.mapper;


import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
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
public class TeacherMapperTest {
    @Autowired
    private TeacherMapper teacherMapper;


    @Test
    public void testNullEntityToDto() {
        Teacher teacher = null;

        TeacherDto teacherDto = teacherMapper.toDto(teacher);

        assertThat(teacherDto).isNull();
    }

    @Test
    public void testEntityToDto() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Test");
        teacher.setLastName("Teacher");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());

        TeacherDto teacherDto = teacherMapper.toDto(teacher);

        assertThat(teacherDto).isNotNull();
        assertThat(teacherDto.getId()).isEqualTo(teacher.getId());
        assertThat(teacherDto.getFirstName()).isEqualTo(teacher.getFirstName());
        assertThat(teacherDto.getLastName()).isEqualTo(teacher.getLastName());
        assertThat(teacherDto.getCreatedAt()).isEqualTo(teacher.getCreatedAt());
        assertThat(teacherDto.getUpdatedAt()).isEqualTo(teacher.getUpdatedAt());
    }

    @Test
    public void testNullEntityListToDto() {
        List<Teacher> teachers = null;

        List<TeacherDto> teacherDtos = teacherMapper.toDto(teachers);

        assertThat(teacherDtos).isNull();
    }

    @Test
    public void testEntityListToDtoList() {
        List<Teacher> teachers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Teacher teacher = new Teacher();
            teacher.setId((long) i);
            teacher.setFirstName("Test "+i);
            teacher.setLastName("Teacher "+i);
            teacher.setCreatedAt(LocalDateTime.now());
            teacher.setUpdatedAt(LocalDateTime.now());
            teachers.add(teacher);
        }

        System.out.println(teachers);

        List<TeacherDto> teacherDtos = teacherMapper.toDto(teachers);

        assertThat(teacherDtos).isNotNull();

        assertThat(teacherDtos).extracting(TeacherDto::getId).containsExactlyElementsOf(teachers.stream().map(Teacher::getId).collect(Collectors.toList()));
        assertThat(teacherDtos).extracting(TeacherDto::getFirstName).containsExactlyElementsOf(teachers.stream().map(Teacher::getFirstName).collect(Collectors.toList()));
        assertThat(teacherDtos).extracting(TeacherDto::getLastName).containsExactlyElementsOf(teachers.stream().map(Teacher::getLastName).collect(Collectors.toList()));
        assertThat(teacherDtos).extracting(TeacherDto::getCreatedAt).containsExactlyElementsOf(teachers.stream().map(Teacher::getCreatedAt).collect(Collectors.toList()));
        assertThat(teacherDtos).extracting(TeacherDto::getUpdatedAt).containsExactlyElementsOf(teachers.stream().map(Teacher::getUpdatedAt).collect(Collectors.toList()));
    }

    @Test
    public void testNullDtoToEntity() {
        TeacherDto teacherDto = null;

        Teacher teacher = teacherMapper.toEntity(teacherDto);

        assertThat(teacher).isNull();
    }

    @Test
    public void testDtoToEntity() {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setFirstName("Test");
        teacherDto.setLastName("Teacher");
        teacherDto.setCreatedAt(LocalDateTime.now());
        teacherDto.setUpdatedAt(LocalDateTime.now());

        Teacher teacher = teacherMapper.toEntity(teacherDto);

        assertThat(teacher).isNotNull();
        assertThat(teacher.getId()).isEqualTo(teacherDto.getId());
        assertThat(teacher.getFirstName()).isEqualTo(teacherDto.getFirstName());
        assertThat(teacher.getLastName()).isEqualTo(teacherDto.getLastName());
        assertThat(teacher.getCreatedAt()).isEqualTo(teacherDto.getCreatedAt());
        assertThat(teacher.getUpdatedAt()).isEqualTo(teacherDto.getUpdatedAt());
    }

    @Test
    public void testNullDtoListToEntity() {
        List<TeacherDto> teacherDtos = null;

        List<Teacher> teachers = teacherMapper.toEntity(teacherDtos);

        assertThat(teachers).isNull();
    }

    @Test
    public void testDtoListToEntityList() {
        List<TeacherDto> teacherDtos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TeacherDto teacherDto = new TeacherDto();
            teacherDto.setId((long) i);
            teacherDto.setFirstName("Test "+i);
            teacherDto.setLastName("Teacher "+i);
            teacherDto.setCreatedAt(LocalDateTime.now());
            teacherDto.setUpdatedAt(LocalDateTime.now());
            teacherDtos.add(teacherDto);
        }

        List<Teacher> teachers = teacherMapper.toEntity(teacherDtos);

        assertThat(teachers).isNotNull();

        assertThat(teachers).extracting(Teacher::getId).containsExactlyElementsOf(teacherDtos.stream().map(TeacherDto::getId).collect(Collectors.toList()));
        assertThat(teachers).extracting(Teacher::getFirstName).containsExactlyElementsOf(teacherDtos.stream().map(TeacherDto::getFirstName).collect(Collectors.toList()));
        assertThat(teachers).extracting(Teacher::getLastName).containsExactlyElementsOf(teacherDtos.stream().map(TeacherDto::getLastName).collect(Collectors.toList()));
        assertThat(teachers).extracting(Teacher::getCreatedAt).containsExactlyElementsOf(teacherDtos.stream().map(TeacherDto::getCreatedAt).collect(Collectors.toList()));
        assertThat(teachers).extracting(Teacher::getUpdatedAt).containsExactlyElementsOf(teacherDtos.stream().map(TeacherDto::getUpdatedAt).collect(Collectors.toList()));
    }


}
