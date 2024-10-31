import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { firstValueFrom, of, throwError } from 'rxjs';
import { RegisterRequest } from '../../interfaces/registerRequest.interface';
import { RouterTestingModule } from '@angular/router/testing';
import { LoginComponent } from '../login/login.component';
import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: AuthService;
  let router: Router;
  let mockHttpController: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      providers: [
        AuthService
      ],
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        ReactiveFormsModule,  
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        RouterTestingModule.withRoutes([
          { path: "", component: RegisterComponent },
          { path: 'login', component: LoginComponent }
        ])
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    mockHttpController = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should handle successful registration and navigate to login', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation(() => firstValueFrom(of(true)));

    component.form.setValue({
      email: "test@example.com",
      firstName: "Test",
      lastName: "User",
      password: "abcd1234"
    });
    component.submit();

    const testRequest: TestRequest = mockHttpController.expectOne("api/auth/register");
    expect(testRequest.request.method).toEqual("POST");
    expect(testRequest.request.body).toEqual(component.form.value as RegisterRequest);
    testRequest.flush(null);

    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  })

  it('should handle registration error', () => {
    component.form.setValue({
      email: "test@example.com",
      firstName: "Test",
      lastName: "User",
      password: "abcd1234"
    });
    component.submit();

    const testRequest: TestRequest = mockHttpController.expectOne("api/auth/register");
    expect(testRequest.request.method).toEqual("POST");
    expect(testRequest.request.body).toEqual(component.form.value as RegisterRequest);
    testRequest.flush(null, {status: 500, statusText: 'Internal server error'});
    
    fixture.detectChanges();
    const htmlElement: HTMLElement = fixture.nativeElement;
    expect(fixture.nativeElement.querySelector('span.error').textContent).toBe("An error occurred");
  })
});
