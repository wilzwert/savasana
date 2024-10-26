import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthService } from './auth.service';
import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';
import { UserService } from 'src/app/services/user.service';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';

describe("AuthService unit tests", () =>  {
    let service: AuthService;
    let mockHttpController: HttpTestingController;

    beforeEach(async () => {
        TestBed.configureTestingModule({
          imports: [
            HttpClientTestingModule            
          ]
        });
    
        service = TestBed.inject(AuthService);
        mockHttpController = TestBed.inject(HttpTestingController);
      });

      afterEach(() =>  {
        mockHttpController.verify()
      })

      it('should post login request and return session information as an observable', (done) => {
        const mockSessionInfo: SessionInformation = {id: 1, username: "johndoe", admin: false, firstName: "John", lastName: "Doe", token: "token123", type: "Bearer"};
        const loginRequest: LoginRequest = {email: "john.doe@example.com", password: "testpassword"};

        service.login(loginRequest).subscribe(response => {
            expect(response).toEqual(mockSessionInfo);
            done();
        });

        const testRequest: TestRequest = mockHttpController.expectOne("api/auth/login");
        expect(testRequest.request.method).toEqual("POST");
        expect(testRequest.request.body).toEqual(loginRequest);
        testRequest.flush(mockSessionInfo);
      })

      
      it('should post register request and return void as an observable', (done) => {
        const registerRequest: RegisterRequest = {email: "john.doe@example.com", password: "testpassword", firstName: "John", "lastName": "Doe"};
        service.register(registerRequest).subscribe(response => {
            expect(response).toBeNull();
            done()
        });

        const testRequest: TestRequest = mockHttpController.expectOne("api/auth/register");
        expect(testRequest.request.method).toEqual("POST");
        expect(testRequest.request.body).toEqual(registerRequest);
        testRequest.flush(null);
      })

      
})