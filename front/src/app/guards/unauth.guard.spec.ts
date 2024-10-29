import { TestBed } from "@angular/core/testing";
import { AuthGuard } from "./auth.guard"
import { provideRouter, Router } from "@angular/router";
import { SessionService } from "../services/session.service";
import { UnauthGuard } from "./unauth.guard";


describe('AuthGuard', () => {
    let guard: UnauthGuard;
    let mockSessionService: any;
    let mockRouter: any;

  beforeEach(() => {
    mockSessionService = { isLogged: false };
    mockRouter ={navigate: jest.fn()};
    TestBed.configureTestingModule({
        providers: [
            { provide: Router, useValue: mockRouter},
            { provide: SessionService, useValue: mockSessionService}
        ]
    });
    TestBed.inject(SessionService);
    TestBed.inject(Router);
    guard = TestBed.inject(UnauthGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return true if not logged in', () => {    
    expect(guard.canActivate()).toBe(true);
  })

  it('should return false and redirect to rentals if logged in', () => {
    mockSessionService.isLogged = true;
    expect(guard.canActivate()).toBe(false);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['rentals']);
  })
})