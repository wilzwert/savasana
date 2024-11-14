import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { firstValueFrom, of} from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { LoginComponent } from './login.component';
import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';
import { ListComponent } from 'src/app/features/sessions/components/list/list.component';
import { SessionService } from 'src/app/services/session.service';
import { LoginRequest } from '../../interfaces/loginRequest.interface';

describe('RegisterComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let sessionService: SessionService;
  let router: Router;
  let mockHttpController: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        AuthService,
        SessionService
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
          { path: "", component: LoginComponent },
          { path: 'sessions', component: ListComponent }
        ])
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    mockHttpController = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should handle successful login and navigate to sessions', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation(() => firstValueFrom(of(true)));

    component.form.setValue({
      email: "test@example.com",
      password: "abcd1234"
    });
    component.submit();

    const testRequest: TestRequest = mockHttpController.expectOne("api/auth/login");
    expect(testRequest.request.method).toEqual("POST");
    expect(testRequest.request.body).toEqual(component.form.value as LoginRequest);
    testRequest.flush({token: 'access_token'});

    expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
  })

  it('should handle login error', () => {
    component.form.setValue({
      email: "test@example.com",
      password: "abcd1234"
    });
    component.submit();

    const testRequest: TestRequest = mockHttpController.expectOne("api/auth/login");
    expect(testRequest.request.method).toEqual("POST");
    expect(testRequest.request.body).toEqual(component.form.value as LoginRequest);
    testRequest.flush(null, {status: 401, statusText: 'Unauthorized'});
    
    fixture.detectChanges();
    const htmlElement: HTMLElement = fixture.nativeElement;
    expect(fixture.nativeElement.querySelector('p.error').textContent).toBe("An error occurred");
  })
});
