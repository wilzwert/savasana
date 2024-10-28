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

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  const mockSessionService = {
    logIn: jest.fn()
  };

  const mockAuthService = {
    login: jest.fn()
  }

  const mockRouter = {
    navigate: jest.fn()
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        LoginComponent, 
        {provide: SessionService, useValue: mockSessionService},
        {provide: AuthService, useValue: mockAuthService},
        {provide: Router, useValue: mockRouter}
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
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  
  it('should redirect to sessions on sucessful login', () => {
    const response = {token: 'abcd1234'};
    mockAuthService.login.mockReturnValue(of(response));

    component.submit();
    expect(mockAuthService.login).toHaveBeenCalledWith(component.form.value as LoginRequest);
    expect(mockSessionService.logIn).toHaveBeenCalledWith(response);
    expect(component.onError).toBe(false);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  })

  it('should handle login error', () => {
    mockAuthService.login.mockReturnValue(throwError(() => new Error('login failed')));
    component.submit();

    expect(component.onError).toBe(true);
    fixture.detectChanges();
    const htmlElement: HTMLElement = fixture.nativeElement;
    expect(fixture.nativeElement.querySelector('p').textContent).toBe("An error occurred");
  })
  

});
