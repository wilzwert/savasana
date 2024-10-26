import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';

import { AppComponent } from './app.component';
import { Observable, of } from 'rxjs';
import { Router } from '@angular/router';
import { SessionService } from './services/session.service';


describe('AppComponent', () => {

  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let mockRouter: Partial<Router>;
  let mockSessionService: Partial<SessionService>;

  beforeEach(async () => {
    // mock session service
    mockSessionService = {
      logOut: jest.fn(),
      $isLogged: jest.fn().mockReturnValue(of(true))

    }
    // mock router
    mockRouter = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatToolbarModule
      ],
      declarations: [
        AppComponent
      ],
      providers: [
        { provide: Router, useValue: mockRouter },
        { provide: SessionService, useValue: mockSessionService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should return logged status as an observable', () => {
    expect(component.$isLogged()).toBeTruthy();
    expect(component.$isLogged()).toBeInstanceOf(Observable);

    component.$isLogged().subscribe((value) => {
        expect(value).toBeTruthy();
    });
  });


  it('should log out', async () => {
    component.logout();
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith([""]);
  })
});
