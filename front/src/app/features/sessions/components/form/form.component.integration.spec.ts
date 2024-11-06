import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { FormComponent } from './form.component';
import { ActivatedRoute, Router } from '@angular/router';
import { TeacherService } from 'src/app/services/teacher.service';
import { Teacher } from 'src/app/interfaces/teacher.interface';
import { Session } from '../../interfaces/session.interface';
import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { ListComponent } from '../list/list.component';

describe('FormComponent integration tests', () => {
  const mockTeachers: Teacher[] = [
    { id: 456, firstName: 'Test', lastName: 'Teacher', createdAt: new Date(), updatedAt: new Date() },
    { id: 66, firstName: 'Second', lastName: 'Test-Teacher', createdAt: new Date(), updatedAt: new Date() }
  ];
  
  const mockSession: Session = {
    id: 1,
    name: "Test session",
    date: new Date(),
    description: "Test description",
    teacher_id: 66,
    users: []
  }

  const mockSessionInformation: SessionInformation = { id: 66, admin: true } as SessionInformation;
  
  const imports = [
    RouterTestingModule.withRoutes([
      { path: "sessions/update/:id", component: FormComponent },
      { path: 'sessions', component: ListComponent }
    ]),
    HttpClientTestingModule,
    MatCardModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule, 
    MatSnackBarModule,
    MatSelectModule,
    NoopAnimationsModule
  ];
  
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  let sessionService: SessionService;
  let router: Router;
  let matSnackBar:MatSnackBar;
  let mockHttpController: HttpTestingController;
  let mockActivatedRoute: any;
  
  beforeEach(async () => {
    mockActivatedRoute = { snapshot: { paramMap: { get: () => '' } } } ;
    
    await TestBed.configureTestingModule({
      imports: imports,
      providers: [
        SessionService,
        TeacherService,
        SessionApiService,
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ],
      declarations: [FormComponent]
    })
      .compileComponents();

    sessionService = TestBed.inject(SessionService);
    sessionService.logIn(mockSessionInformation as SessionInformation);
    matSnackBar = TestBed.inject(MatSnackBar);
    router = TestBed.inject(Router);
    mockHttpController = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    // mock http response for Teacher
    const testRequestTeachers: TestRequest = mockHttpController.expectOne("api/teacher");
    expect(testRequestTeachers.request.method).toEqual("GET");
    testRequestTeachers.flush(mockTeachers);
  });

  afterEach(() => {
    jest.restoreAllMocks()
  })
  
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to sessions if current user is not admin', () => {
    const routerSpy = jest.spyOn(router, 'navigate');
    const mockSessionInformationNonAdmin: SessionInformation = {id: 123, admin: false} as SessionInformation;
    sessionService.logIn(mockSessionInformationNonAdmin);
    fixture.detectChanges();
    component.ngOnInit();
    expect(routerSpy).toHaveBeenCalledWith(['/sessions'])
  })
  
  
  it('should create session and then navigate to sessions list', () => {
    const routerSpy = jest.spyOn(router, 'navigate');
    const matSnackBarSpy = jest.spyOn(matSnackBar, 'open');

    component.sessionForm = new FormBuilder().group({
      name: ['Test Session', Validators.required],
      date: ['2025-01-01', Validators.required],
      teacher_id: ['66', Validators.required],
      description: ['Session description', [Validators.required, Validators.max(2000)]]
    });

    component.submit();

    // mock http response for successful session creation
    const testRequestSession: TestRequest = mockHttpController.expectOne("api/session");
    expect(testRequestSession.request.method).toEqual("POST");
    expect(testRequestSession.request.body).toEqual(component.sessionForm.value as Session);
    testRequestSession.flush(null);

    expect(matSnackBarSpy).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
    expect(routerSpy).toHaveBeenCalledWith(['sessions']);
  })
  

  it('should update session and then navigate to sessions list', async () => {
    mockActivatedRoute.snapshot.paramMap.get = () => '1';
    const routerSpy = jest.spyOn(router, 'navigate');
    const matSnackBarSpy = jest.spyOn(matSnackBar, 'open');

    await router.navigate(['/sessions/update/1']).then(() => {
      expect(router.url).toEqual('/sessions/update/1');
    });
    
    fixture.detectChanges();
    component.ngOnInit();

     // mock http response for successful session creation
     const testRequestGetSession: TestRequest = mockHttpController.expectOne("api/session/1");
     expect(testRequestGetSession.request.method).toEqual("GET");
     testRequestGetSession.flush(mockSession);

    component.submit();
    
    // mock http response for successful session creation
    const testRequestSession: TestRequest = mockHttpController.expectOne("api/session/1");
    expect(testRequestSession.request.method).toEqual("PUT");
    expect(testRequestSession.request.body).toEqual(component.sessionForm?.value as Session);
    testRequestSession.flush(null);
    expect(matSnackBarSpy).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
    expect(routerSpy).toHaveBeenCalledWith(['sessions']);
  })
});