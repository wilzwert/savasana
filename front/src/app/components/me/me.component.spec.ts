import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { SessionService } from 'src/app/services/session.service';

import { MeComponent } from './me.component';
import { User } from 'src/app/interfaces/user.interface';
import { of } from 'rxjs';
import { UserService } from 'src/app/services/user.service';
import { Session } from 'src/app/features/sessions/interfaces/session.interface';
import { Router } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let mockUser: User = {id: 1, firstName: "John", lastName: "Doe", email: "john.doe@example.com", admin: false, password: "abcd1234", createdAt: new Date() };

  const mockSessionService: Partial<SessionService> = {
    sessionInformation: {
      admin: true,
      id: 1,
      username: 'johndoe',
      token: 'token123',
      firstName: 'John',
      lastName: 'Doe',
      type: 'Bearer'
    },
    logOut: jest.fn()
  }

  const mockUserService: Partial<UserService> = {
    getById: jest.fn().mockReturnValue(of<User>(mockUser)),
    delete: jest.fn().mockReturnValue(of({sucess: true}))
  }

  const mockMatSnackBar: Partial<MatSnackBar> = {
    open: jest.fn()
  }

  const mockRouter: Partial<Router> = {
    navigate: jest.fn()
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        HttpClientTestingModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService},
        { provide: MatSnackBar, useValue: mockMatSnackBar},
        { provide: Router, useValue: mockRouter}
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.restoreAllMocks()
  })

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have current session information', () => {
    expect(mockUserService.getById).toHaveBeenCalledWith('1')
    expect(component.user).toEqual(mockUser)
  })

  it('should go back on back button press', () => {
    const spy = jest.spyOn(history, 'back');
    component.back();
    expect(spy).toHaveBeenCalled();
  });

  it('should delete user, display message and navigate to /', () =>  {
    component.delete();
    expect(mockUserService.delete).toHaveBeenCalled();
    expect(mockMatSnackBar.open).toHaveBeenCalledWith("Your account has been deleted !", 'Close', { duration: 3000 })
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(["/"]);
  })

});
