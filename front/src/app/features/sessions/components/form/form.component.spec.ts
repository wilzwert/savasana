import { HttpClientModule } from '@angular/common/http';
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
import { of } from 'rxjs';
import { Teacher } from 'src/app/interfaces/teacher.interface';
import { Session } from '../../interfaces/session.interface';

describe('FormComponent', () => {
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
  
  const imports = [
    RouterTestingModule,
    HttpClientModule,
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
  let mockRouter: any;
  let mockActivatedRoute: any;
  let mockTeacherService: any;
  let mockSessionApiService: any;  
  let mockSessionService: any;  
  let mockMatSnackBar: any; 
  
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
    mockTeacherService = { all: jest.fn().mockReturnValue(of<Teacher[]>(mockTeachers)) };
    mockSessionApiService = { 
      detail: jest.fn().mockReturnValue(of(mockSession)),
      create: jest.fn().mockReturnValue(of({})),
      update: jest.fn().mockReturnValue(of({}))
    };
    mockSessionService = {sessionInformation: { admin: true  }};
    mockMatSnackBar = {open: jest.fn()};
    mockRouter = {navigate: jest.fn(), url: '/sessions/create'};
    mockActivatedRoute = { snapshot: { paramMap: { get: () => '' } } } ;
    
    await TestBed.configureTestingModule({
      imports: imports,
      providers: [
        { provide: ActivatedRoute, useValue: mockActivatedRoute},
        { provide: Router, useValue: mockRouter},        
        { provide: SessionService, useValue: mockSessionService},
        { provide: TeacherService, useValue: mockTeacherService},
        { provide: SessionApiService, useValue:  mockSessionApiService},
        { provide: MatSnackBar, useValue: mockMatSnackBar}
      ],
      declarations: [FormComponent]
    })
      .compileComponents();
    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.restoreAllMocks()
  })
  
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to sessions if current user is not admin', () => {
    mockSessionService.sessionInformation.admin = false;
    fixture.detectChanges();
    component.ngOnInit();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions'])
  })
  
  it('should load teachers on init', (done) => {
    expect(mockTeacherService.all).toHaveBeenCalledTimes(1);
    component.teachers$.subscribe((teachers: Teacher[]) => {
        expect(teachers).toEqual(mockTeachers)
        done()
      })
  })

  it('should build empty form for session creation', () => {
    expect(mockRouter.url).toBe('/sessions/create');
    // check that onUpdate is false
    expect(component.onUpdate).toBe(false);    
    // check form inputs values 
    checkFormGroup();
  })

  it('should handle session creation', () => {
    component.sessionForm = new FormBuilder().group({
      name: ['Test Session', Validators.required],
      date: ['2025-01-01', Validators.required],
      teacher_id: ['66', Validators.required],
      description: ['Session description', [Validators.required, Validators.max(2000)]]
    });

    component.submit();

    expect(mockSessionApiService.create).toHaveBeenCalledTimes(1);
    expect(mockSessionApiService.create).toHaveBeenCalledWith(component.sessionForm.value as Session);
    expect(mockMatSnackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  })

  it('should build pre-filled form for session update', () => {
    mockRouter.url = '/sessions/update/1';
    mockActivatedRoute.snapshot.paramMap.get = () => '1';

    fixture.detectChanges();
    component.ngOnInit();

    expect(mockSessionApiService.detail).toHaveBeenCalledTimes(1);
    expect(mockSessionApiService.detail).toHaveBeenCalledWith("1");
    expect(component.onUpdate).toBe(true);

    // check form inputs default values
    checkFormGroup({name: mockSession.name, date: mockSession.date.toISOString().split('T')[0], teacher_id: mockSession.teacher_id, description: mockSession.description});
  })
  
  it('should handle session update', () => {
    mockRouter.url = '/sessions/update/1';
    mockActivatedRoute.snapshot.paramMap.get = () => '1';

    fixture.detectChanges();
    component.ngOnInit();

    component.submit();

    const expectedFormValues = {
      name: mockSession.name,
      teacher_id: mockSession.teacher_id,
      description: mockSession.description,
      date: mockSession.date.toISOString().split('T')[0]
    }

    expect(mockSessionApiService.update).toHaveBeenCalledTimes(1);
    expect(mockSessionApiService.update).toHaveBeenCalledWith("1", expectedFormValues);
    expect(mockMatSnackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  })
});