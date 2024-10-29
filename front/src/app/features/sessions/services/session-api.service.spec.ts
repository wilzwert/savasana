import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';


describe('SessionsService', () => {
  let service: SessionApiService;
  let mockHttpController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(SessionApiService);
    mockHttpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() =>  {
    mockHttpController.verify()
  })

  it('should be created', () => {
    expect(service).toBeTruthy();
  });


  it('should retreive all sessions', (done) => {
    const mockSessions:Session[] = [
      {id: 1, name: "Session débutant", date: new Date(), description: "Session de yoga pour débutant", teacher_id: 1, users: [1,2]},
      {id: 1, name: "Session expériment&", date: new Date(), description: "Session de yoga pour expérimentés", teacher_id: 2, users: [3,4]},
    ];

    service.all().subscribe(
      (sessions: Session[]) => {
        expect(sessions).toEqual(mockSessions);
        done();
      }
    );

    const testRequest: TestRequest = mockHttpController.expectOne("api/session");
    expect(testRequest.request.method).toEqual("GET");
    testRequest.flush(mockSessions);
  })

  it('should retreive a session by its id', (done) => {
    const mockSession:Session = {id: 1, name: "Session débutant", date: new Date(), description: "Session de yoga pour débutant", teacher_id: 1, users: [1,2]};

    service.detail("1").subscribe(
      (session: Session) => {
        expect(session).toEqual(mockSession);
        done();
      }
    );

    const testRequest: TestRequest = mockHttpController.expectOne("api/session/1");
    expect(testRequest.request.method).toEqual("GET");
    testRequest.flush(mockSession);
  })

  it('should delete a session by its id', (done) => {
    const mockResponse = {}
    service.delete("1").subscribe(
      response => {
        expect(response).toEqual(mockResponse);
        done();
      }
    );

    const testRequest: TestRequest = mockHttpController.expectOne("api/session/1");
    expect(testRequest.request.method).toEqual("DELETE");
    testRequest.flush(mockResponse);
  })

  it('should create a session', (done) => {
    const mockSession:Session = {name: "Session débutant", date: new Date(), description: "Session de yoga pour débutant", teacher_id: 1, users: [2,3]};
    const mockSessionCreated:Session = {id: 3, ...mockSession};
    service.create(mockSession).subscribe(
      (session: Session)  => {
        expect(session).toEqual(mockSessionCreated);
        done();
      }
    );

    const testRequest: TestRequest = mockHttpController.expectOne("api/session");
    expect(testRequest.request.method).toEqual("POST");
    testRequest.flush(mockSessionCreated);
  })

  it('should update a session', (done) => {
    const mockSession:Session = {id: 4, name: "Session débutant", date: new Date(), description: "Session de yoga pour débutant", teacher_id: 1, users: [2,3]};
    service.update("4", mockSession).subscribe(
      (session: Session)  => {
        expect(session).toEqual(mockSession);
        done();
      }
    );

    const testRequest: TestRequest = mockHttpController.expectOne("api/session/4");
    expect(testRequest.request.method).toEqual("PUT");
    testRequest.flush(mockSession);
  })

  it('should add session participation for a user', (done) => {
    service.participate("4", "2").subscribe(
      response  => {
        expect(response).toBeNull()
        done();
      }
    );

    const testRequest: TestRequest = mockHttpController.expectOne("api/session/4/participate/2");
    expect(testRequest.request.method).toEqual("POST");
    testRequest.flush(null);
  })

  it('should delete session participation for a user', (done) => {
    service.unParticipate("4", "2").subscribe(
      response  => {
        expect(response).toBeNull()
        done();
      }
    );

    const testRequest: TestRequest = mockHttpController.expectOne("api/session/4/participate/2");
    expect(testRequest.request.method).toEqual("DELETE");
    testRequest.flush(null);
  })
});
