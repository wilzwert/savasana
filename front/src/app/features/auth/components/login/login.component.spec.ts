import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../interfaces/loginRequest.interface';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let sessionService: SessionService;
  let router: Router;

  const mockRouter = {
    navigate: jest.fn()
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        LoginComponent, 
        AuthService,
        SessionService
      ],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule]
    })
      .compileComponents();
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.restoreAllMocks();
  })

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  
  it('should redirect to sessions on sucessful login', () => {
    const response = {token: 'abcd1234'};
    const spyAuth = jest.spyOn(authService, 'login').mockReturnValue(of(response as SessionInformation));
    const spyRouter = jest.spyOn(router, 'navigate').mockImplementation();
    const spySession = jest.spyOn(sessionService, 'logIn');

    component.submit();

    expect(spyAuth).toHaveBeenCalledTimes(1);
    expect(spyAuth).toHaveBeenCalledWith(component.form.value as LoginRequest);
    expect(spySession).toHaveBeenCalledTimes(1);
    expect(spySession).toHaveBeenCalledWith(response);
    expect(component.onError).toBe(false);
    expect(spyRouter).toHaveBeenCalledTimes(1);
    expect(spyRouter).toHaveBeenCalledWith(['/sessions']);
  })

  it('should throw login error and display message', () => {
    const spy = jest.spyOn(authService, 'login').mockReturnValue(throwError(() => new Error('login failed')));

    component.submit();

    expect(spy).toHaveBeenCalledTimes(1);
    expect(spy).toHaveBeenCalledWith(component.form.value as LoginRequest);
    expect(component.onError).toBe(true);
    fixture.detectChanges();
    const htmlElement: HTMLElement = fixture.nativeElement;
    expect(fixture.nativeElement.querySelector('p').textContent).toBe("An error occurred");
  })
});
