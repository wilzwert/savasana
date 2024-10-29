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
import { UserService } from 'src/app/services/user.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { Session } from '../../interfaces/session.interface';
import { Teacher } from 'src/app/interfaces/teacher.interface';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';


describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>; 
  let service: SessionService;

  let mockSessionApiService: any;
  let mockTeacherService: any;
  let mockMatSnackBar: any;
  let mockRouter: any;
  let mockSessionService: any;

  const mockSession:Session = { id: 1, name: 'Test Session', description: 'Test description', date: new Date(), users: [66], teacher_id: 456 };
  const mockTeacher:Teacher = { id: 456, firstName: 'Test', lastName: 'Teacher', createdAt: new Date(), updatedAt: new Date() };
  const mockSessionInformation = { id: 66, admin: true };

  beforeEach(async () => {
    mockSessionApiService = {
      detail: jest.fn().mockReturnValue(of(mockSession)),
      delete: jest.fn().mockReturnValue(of({})),
      participate: jest.fn().mockReturnValue(of({})),
      unParticipate: jest.fn().mockReturnValue(of({}))
    };
    mockTeacherService = { detail: jest.fn().mockReturnValue(of(mockTeacher)) };
    mockMatSnackBar = { open: jest.fn() };
    mockRouter = { navigate: jest.fn() };
    mockSessionService = { sessionInformation: mockSessionInformation };

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule,
        MatCardModule,
        MatIconModule
      ],
      declarations: [DetailComponent],  
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } },
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService},
        { provide: TeacherService, useValue: mockTeacherService},
        { provide: MatSnackBar, useValue: mockMatSnackBar},
        { provide: Router, useValue: mockRouter},
      ],
    })
      .compileComponents();
    
    service = TestBed.inject(SessionService);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();   
  });

  afterEach(() => {
    jest.restoreAllMocks()
  })

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load session detail on init', () => {
    expect(component.sessionId).toBe("1");
    expect(component.userId).toBe(""+mockSessionInformation.id);
    expect(component.isAdmin).toBe(mockSessionInformation.admin);

    expect(mockSessionApiService.detail).toHaveBeenCalledTimes(1);
    expect(mockSessionApiService.detail).toHaveBeenCalledWith(""+mockSession.id);
    expect(component.session).toEqual(mockSession);

    expect(mockTeacherService.detail).toHaveBeenCalledTimes(1);
    expect(mockTeacherService.detail).toHaveBeenCalledWith(""+mockSession.teacher_id);
    expect(component.teacher).toEqual(mockTeacher);

    expect(component.isParticipate).toBe(true);
  })

  it('should delete participation', () => {
    component.delete();

    expect(mockSessionApiService.delete).toHaveBeenCalledTimes(1);
    expect(mockSessionApiService.delete).toHaveBeenCalledWith(""+mockSession.id);
    expect(mockMatSnackBar.open).toHaveBeenCalledTimes(1);
    expect(mockMatSnackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(mockRouter.navigate).toHaveBeenCalledTimes(1);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  })

  it('should add participation', () => {
    component.participate();

    expect(mockSessionApiService.participate).toHaveBeenCalledTimes(1);
    expect(mockSessionApiService.participate).toHaveBeenCalledWith(""+mockSession.id, ""+mockSessionInformation.id);
    
    // TODO :  fetchSession ?
  })
  
  it('should remove participation', () => {
    component.unParticipate();
    
    expect(mockSessionApiService.unParticipate).toHaveBeenCalledTimes(1);
    expect(mockSessionApiService.unParticipate).toHaveBeenCalledWith(""+mockSession.id, ""+mockSessionInformation.id);

  // TODO :  fetchSession ?

  })


  



  it('should call window.history.back on back', () => {
    const spy = jest.spyOn(window.history, 'back');
    component.back();
    expect(spy).toHaveBeenCalled();
  });


/*
public ngOnInit(): void {
    this.fetchSession();
  }

  public back() {
    window.history.back();
  }

  public delete(): void {
    this.sessionApiService
      .delete(this.sessionId)
      .subscribe((_: any) => {
          this.matSnackBar.open('Session deleted !', 'Close', { duration: 3000 });
          this.router.navigate(['sessions']);
        }
      );
  }

  public participate(): void {
    this.sessionApiService.participate(this.sessionId, this.userId).subscribe(_ => this.fetchSession());
  }

  public unParticipate(): void {
    this.sessionApiService.unParticipate(this.sessionId, this.userId).subscribe(_ => this.fetchSession());
  }

  private fetchSession(): void {
    this.sessionApiService
      .detail(this.sessionId)
      .subscribe((session: Session) => {
        this.session = session;
        this.isParticipate = session.users.some(u => u === this.sessionService.sessionInformation!.id);
        this.teacherService
          .detail(session.teacher_id.toString())
          .subscribe((teacher: Teacher) => this.teacher = teacher);
      });
  }
*/

});

