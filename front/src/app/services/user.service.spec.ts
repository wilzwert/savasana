import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { UserService } from './user.service';
import { User } from '../interfaces/user.interface';
import { Observable } from 'rxjs';

describe('UserService', () => {
  let service: UserService;
  let httpController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(UserService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpController.verify()
  })

  it('should be created', () => {
    expect(service).toBeTruthy();
  })

  it('should get a user by its id', () => {
    // mock user
    const expectedUser: User = {id: 1, admin: false, email: "test@example.com", firstName: "User", lastName: "Test", createdAt: new Date(), password: "testpassword"};
    service.getById("1").subscribe(user => expect(user).toBe(expectedUser))
    
    const req = httpController.expectOne("api/user/1");
    expect(req.request.method).toBe('GET');
    // mock request response
    req.flush(expectedUser);
  })

  it('should delete user by its id', () => {
    const expectedResponse = {success: true};
    service.delete("1").subscribe(response => expect(response).toBe(expectedResponse))
    
    const req = httpController.expectOne("api/user/1");
    expect(req.request.method).toBe('DELETE');
    // mock request response
    req.flush(expectedResponse);
  })

});
