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

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  let mockSessions: Session[] = [
    {id: 1, name: "Session 1", description: "Description session 1", date: new Date(), teacher_id: 2, users: [1]},
    {id: 2, name: "Session 2", description: "Description session 2", date: new Date(), teacher_id: 2, users: [1]}
  ];

  const mockSessionService = {
    sessionInformation: {
      admin: true
    }
  }

  const mockSessionApiService: any = {
    all: jest.fn().mockReturnValue(of<Session[]>(mockSessions))
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [HttpClientModule, MatCardModule, MatIconModule, RouterTestingModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },        
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load all sessions', (done) => {
    component.sessions$.subscribe((sessions: Session[]) => {
      expect(sessions.length).toBe(mockSessions.length);
      expect(sessions[0].name).toBe(mockSessions[0].name)
      done()
    })

    fixture.detectChanges();
    expect(fixture.debugElement.nativeElement.querySelectorAll('.item').length).toBe(mockSessions.length);
  })
});
