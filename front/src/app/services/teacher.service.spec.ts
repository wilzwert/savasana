import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Teacher } from '../interfaces/teacher.interface';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(TeacherService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpController.verify()
  })
 
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get a list of all teachers', () => {
    const date = new Date();
    const expectedTeachers: Array<Teacher> = [
      {id: 1,   lastName: "Doe", firstName: "Jane", createdAt: date, updatedAt: date},
      {id: 2,   lastName: "Doe", firstName: "John", createdAt: date, updatedAt: date}
    ];

    service.all().subscribe(teachers => expect(teachers).toBe(expectedTeachers))

    const request = httpController.expectOne("api/teacher");
    expect(request.request.method).toBe("GET");
    request.flush(expectedTeachers);
  })

  it('should get a teacher by its id', () => {
    const date = new Date();
    const expectedTeacher:Teacher = {id: 1,   lastName: "Doe", firstName: "Jane", createdAt: date, updatedAt: date};

    service.detail("1").subscribe(teacher => expect(teacher).toBe(expectedTeacher))

    const request = httpController.expectOne("api/teacher/1");
    expect(request.request.method).toBe("GET");
    request.flush(expectedTeacher);
  })
});
