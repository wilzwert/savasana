import { ComponentFixture, TestBed } from '@angular/core/testing';
import {  FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
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
  let sessionApiService: SessionApiService;
  let teacherService: TeacherService;
  let router: Router;
  let matSnackBar:MatSnackBar;
  let mockHttpController: HttpTestingController;
  let mockActivatedRoute: any;
  
  const checkFormGroup = (values: any = {name: '', date: '', teacher_id: '', description: ''}) => {
    expect(component.sessionForm).toBeTruthy();
    if(component.sessionForm) {
      expect(Object.keys(component.sessionForm.controls).length).toBe(4);
  
      const name:FormControl = component.sessionForm.get('name') as FormControl;
      const date:FormControl = component.sessionForm.get('date') as FormControl;
      const teacher_id:FormControl = component.sessionForm.get('teacher_id') as FormControl;
      const description:FormControl = component.sessionForm.get('description') as FormControl;
  
      expect(name).toBeTruthy();
      expect(date).toBeTruthy();
      expect(teacher_id).toBeTruthy();
      expect(description).toBeTruthy();
  
      expect(name.hasValidator(Validators.required)).toBe(true);
      expect(date.hasValidator(Validators.required)).toBe(true);
      expect(teacher_id.hasValidator(Validators.required)).toBe(true);
      expect(description.hasValidator(Validators.required)).toBe(true);
      // FIXME does not work, probably because Validators.max(xx) creates a lambda and we can't get a reference to it from the component
      // expect(description.hasValidator(Validators.max(2000))).toBe(true);
      
      expect(name.value).toBe(values.name);
      expect(date.value).toBe(values.date);
      expect(teacher_id.value).toBe(values.teacher_id);
      expect(description.value).toBe(values.description);
    }
  }

  beforeEach(async () => {
    const mockMatSnackBar = { open: jest.fn() };
    // const mockRouter = { navigate: jest.fn(), url: '/sessions', changeUrl: (url: String) => url = url };

    mockActivatedRoute = { snapshot: { paramMap: { get: () => '' } } } ;
    
    await TestBed.configureTestingModule({
      imports: imports,
      providers: [
        SessionService,
        TeacherService,
        SessionApiService,
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: MatSnackBar, useValue: mockMatSnackBar},
        // { provide: Router/*, useValue: mockRouter*/},
      ],
      declarations: [FormComponent]
    })
      .compileComponents();

    sessionService = TestBed.inject(SessionService);
    sessionService.logIn(mockSessionInformation as SessionInformation);
    sessionApiService = TestBed.inject(SessionApiService);
    teacherService =TestBed.inject(TeacherService);
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
  
  
  it('should handle session creation', () => {
    const routerSpy = jest.spyOn(router, 'navigate');

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

    expect(matSnackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
    expect(routerSpy).toHaveBeenCalledWith(['sessions']);
  })
  

  it('should handle session update', async () => {
    mockActivatedRoute.snapshot.paramMap.get = () => '1';
    const routerSpy = jest.spyOn(router, 'navigate');
    await router.navigate(['/sessions/update/1']).then(() => {
      expect(router.url).toEqual('/sessions/update/1');
    });
    // router = TestBed.inject(Router);
    console.log(router.url);
    // router.url = '/sessions/update/1';
    
    fixture.detectChanges();
    component.ngOnInit();

     // mock http response for successful session creation
     const testRequestGetSession: TestRequest = mockHttpController.expectOne("api/session/1");
     expect(testRequestGetSession.request.method).toEqual("GET");
     testRequestGetSession.flush(mockSession);

    component.submit();
    /*
    const expectedFormValues = {
      name: mockSession.name,
      teacher_id: mockSession.teacher_id,
      description: mockSession.description,
      date: mockSession.date.toISOString().split('T')[0]
    }*/
    
    // mock http response for successful session creation
    const testRequestSession: TestRequest = mockHttpController.expectOne("api/session/1");
    expect(testRequestSession.request.method).toEqual("PUT");
    expect(testRequestSession.request.body).toEqual(component.sessionForm?.value as Session);
    testRequestSession.flush(null);
    expect(matSnackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
    expect(routerSpy).toHaveBeenCalledWith(['sessions']);
  })
});