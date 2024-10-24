import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import { Observable } from 'rxjs';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

describe('SessionService', () => {
  let service: SessionService;
  let mockUser:SessionInformation;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
    mockUser = {
        token: 'abcd1234',
        type: 'Bearer',
        id: 1,
        username: 'test',
        firstName: 'Test',
        lastName: 'User',
        admin: false
    };
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return logged status as an observable', () => {
    expect(service.$isLogged()).toBeTruthy();
    expect(service.$isLogged()).toBeInstanceOf(Observable);
  });

  it('initial logged status should be false', () => {
    expect(service.isLogged).toBe(false);
    service.$isLogged().subscribe((value) => {
      expect(value).toBe(false);
    });
  })

  it('should return logged status true after login', () => {
    service.logIn(mockUser);
    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toBe(mockUser);
    service.$isLogged().subscribe((value) => {
      expect(value).toBe(true);
    });
  })

  it('should return logged status false after logout', () => {
    service.logIn(mockUser);
    service.logOut();
    expect(service.sessionInformation).toBeUndefined();
    expect(service.isLogged).toBe(false);

    service.$isLogged().subscribe((value) => {
        expect(value).toBe(false);
    });
  });
});
