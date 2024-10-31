import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule, } from '@angular/router/testing';
import { expect } from '@jest/globals'; 
import { SessionService } from '../../../../services/session.service';

import { DetailComponent } from './detail.component';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { Session } from '../../interfaces/session.interface';
import { Teacher } from 'src/app/interfaces/teacher.interface';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { ListComponent } from '../list/list.component';
import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';


describe('DetailComponent integration tests', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>; 
  let sessionService: SessionService;
  let sessionApiService: SessionApiService;
  let teacherService: TeacherService;
  let router: Router;
  let matSnackBar:MatSnackBar;
  let mockHttpController: HttpTestingController;

  const mockSession:Session = { id: 1, name: 'Test Session', description: 'Test description', date: new Date(), users: [66], teacher_id: 456 };
  const mockSessionWithoutParticipation:Session = { id: mockSession.id, name: mockSession.name, description: mockSession.description, date: mockSession.date, users: [], teacher_id: mockSession.teacher_id };;
  const mockTeacher:Teacher = { id: 456, firstName: 'Test', lastName: 'Teacher', createdAt: new Date(), updatedAt: new Date() };
  const mockSessionInformation = { id: 66, admin: true };

  beforeEach(async () => {
    const mockMatSnackBar = { open: jest.fn() };
    
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          { path: "", component: DetailComponent },
          { path: 'sessions', component: ListComponent }
        ]),
        HttpClientTestingModule,
        MatSnackBarModule,
        ReactiveFormsModule,
        MatCardModule,
        MatIconModule
      ],
      declarations: [DetailComponent],  
      providers: [
        SessionService,
        SessionApiService,
        TeacherService,
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } },
        { provide: MatSnackBar, useValue: mockMatSnackBar}
      ],
    })
      .compileComponents();
    
    sessionService = TestBed.inject(SessionService);
    sessionService.logIn(mockSessionInformation as SessionInformation);
    sessionApiService = TestBed.inject(SessionApiService);
    teacherService =TestBed.inject(TeacherService);
    matSnackBar = TestBed.inject(MatSnackBar);
    router = TestBed.inject(Router);
    mockHttpController = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();   
  });

  afterEach(() => {
    jest.restoreAllMocks();
  })

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  
  it('should display session detail by default', () => {    
    // mock http response for Session
    const testRequestSession: TestRequest = mockHttpController.expectOne("api/session/"+mockSession.id);
    expect(testRequestSession.request.method).toEqual("GET");
    testRequestSession.flush(mockSession);
    
    // mock http response for Teacher
    const testRequestTeacher: TestRequest = mockHttpController.expectOne("api/teacher/"+mockSession.teacher_id);
    expect(testRequestTeacher.request.method).toEqual("GET");
    testRequestTeacher.flush(mockTeacher);
    
    fixture.detectChanges();
    expect(component.userId).toBe(""+mockSessionInformation.id);
    expect(component.isAdmin).toBe(mockSessionInformation.admin);
    expect(component.sessionId).toBe(""+mockSession.id);
    expect(component.isParticipate).toBe(true);
    expect(component.session).toEqual(mockSession);
    expect(component.teacher).toEqual(mockTeacher);
    expect(fixture.nativeElement.querySelector('mat-card')).toBeTruthy();
  })

  it('should delete participation', () => {
    const matSnackBarSpy = jest.spyOn(matSnackBar, 'open');
    const matNavigateSpy = jest.spyOn(router, 'navigate');

    // mock http response for GET Session
    const testRequestGetSession: TestRequest = mockHttpController.expectOne("api/session/"+mockSession.id);
    expect(testRequestGetSession.request.method).toEqual("GET");
    testRequestGetSession.flush(mockSession);

    component.delete();

    // mock http response for DELETE Session
    const testRequestSession: TestRequest = mockHttpController.expectOne("api/session/"+mockSession.id);
    expect(testRequestSession.request.method).toEqual("DELETE");
    testRequestSession.flush(null);

    expect(matSnackBarSpy).toHaveBeenCalledTimes(1);
    expect(matSnackBarSpy).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(matNavigateSpy).toHaveBeenCalledTimes(1);
    expect(matNavigateSpy).toHaveBeenCalledWith(['sessions']);
  })
  
  it('should add participation', () => {
    // mock http response for first GET Session
    const testRequestGetSession: TestRequest = mockHttpController.expectOne("api/session/"+mockSessionWithoutParticipation.id);
    expect(testRequestGetSession.request.method).toEqual("GET");
    testRequestGetSession.flush(mockSessionWithoutParticipation);

    component.participate();

    // mock http response for participation
    const testRequestParticipate: TestRequest = mockHttpController.expectOne("api/session/"+mockSession.id+"/participate/"+mockSessionInformation.id);
    expect(testRequestParticipate.request.method).toEqual("POST");
    testRequestParticipate.flush(null);


    // mock http response for second GET Session to reload data
    const testRequestGetSessionAfterParticipate: TestRequest = mockHttpController.expectOne("api/session/"+mockSession.id);
    expect(testRequestGetSessionAfterParticipate.request.method).toEqual("GET");
    testRequestGetSessionAfterParticipate.flush(mockSession);

    expect(component.isParticipate).toBe(true);
  })
  
  it('should remove participation', () => {
  // mock http response for first GET Session
  const testRequestGetSession: TestRequest = mockHttpController.expectOne("api/session/"+mockSession.id);
  expect(testRequestGetSession.request.method).toEqual("GET");
  testRequestGetSession.flush(mockSession);

    component.unParticipate();
    
    // mock http response for participation deletion
    const testRequestParticipate: TestRequest = mockHttpController.expectOne("api/session/"+mockSession.id+"/participate/"+mockSessionInformation.id);
    expect(testRequestParticipate.request.method).toEqual("DELETE");
    testRequestParticipate.flush(null);

    // mock http response for second GET Session to reload data
    const testRequestGetSessionAfterParticipate: TestRequest = mockHttpController.expectOne("api/session/"+mockSession.id);
    expect(testRequestGetSessionAfterParticipate.request.method).toEqual("GET");
    testRequestGetSessionAfterParticipate.flush(mockSessionWithoutParticipation);

    expect(component.isParticipate).toBe(false);
  })

});

