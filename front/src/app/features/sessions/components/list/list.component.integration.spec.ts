import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { ListComponent } from './list.component';
import { SessionApiService } from '../../services/session-api.service';
import { of } from 'rxjs';
import { Session } from '../../interfaces/session.interface';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule, HttpTestingController, TestRequest } from '@angular/common/http/testing';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  let sessionService: SessionService;
  let sessionApiService: SessionApiService;

  let mockHttpController: HttpTestingController;

  const mockSessionInformation:SessionInformation = {
    id: 66, 
    admin: true
  } as SessionInformation;

  const mockSessions: Session[] = [
    {id: 1, name: "Session 1", description: "Description session 1", date: new Date(), teacher_id: 2, users: [1]},
    {id: 2, name: "Session 2", description: "Description session 2", date: new Date(), teacher_id: 2, users: [1]}
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [HttpClientTestingModule, MatCardModule, MatIconModule, RouterTestingModule],
      providers: [
        SessionService,
        SessionApiService
      ]
    })
      .compileComponents();

    sessionService = TestBed.inject(SessionService);
    sessionService.logIn(mockSessionInformation);
    sessionApiService = TestBed.inject(SessionApiService);
    mockHttpController = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(ListComponent);    
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.restoreAllMocks()
  })

  it('should list all sessions', () => {
    // mock http response for Sessions
    const testRequestSessions: TestRequest = mockHttpController.expectOne("api/session");
    expect(testRequestSessions.request.method).toEqual("GET");
    testRequestSessions.flush(mockSessions);

    fixture.detectChanges();
    expect(fixture.debugElement.nativeElement.querySelectorAll('.item').length).toBe(mockSessions.length);
  })
});
