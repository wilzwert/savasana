describe("Sessions list actions as an admin user", () => {
    let mockSessions: Array<Object>;
    let mockSession: any;
    let mockTeachers: Array<Object>;
    before(() => {
        cy.fixture('sessions.json').as('sessionsJson').then(json => {mockSessions = json; mockSession = json[0];});
        cy.fixture('teachers.json').as('teachersJson').then(json => {mockTeachers = json;});
    })

    beforeEach(() => {
        cy.intercept(
            {
              method: 'GET',
              url: '/api/session',
              // force this interception to be called first to "override" interception made in cy.login()
              middleware: true,
              times: 1
            },
            (request => {request.reply(mockSessions);})
        );

        cy.intercept(
            {
              method: 'GET',
              url: '/api/teacher',
              // force this interception to be called first to "override" interception made in cy.login()
              middleware: true
            },
            (request => {request.reply(mockTeachers);})
        );

        cy.loginAdmin();
    })
    
    it('should display creation form and handle invalid fields or creation error', () => {
        cy.intercept(
            {method: 'POST', url: '/api/session'},
            {
                statusCode: 400,
                times: 1
            }
        ).as('createSessionError');

        cy.contains('Create').click();
        cy.contains('h1', 'Create session');
        // check form initial state : pre filled with mockSession data, submit button enabled
        cy.get('form input[formControlName=name]').should('have.value', '');
        cy.get('form input[formControlName=date]').should('have.value', '');
        // we click the already selected item to close the choices list 
        // this is mandatory because opening the options list creates an overlay that prevents further actions on other elements
        cy.get('form [formControlName=description]').should('have.value', '');
        cy.get('button[type=submit]').should('be.disabled');

        // set valid values and submit
        cy.get('form input[formControlName=name]').type('Test session');
        cy.get('button[type=submit]').should('be.disabled');
        cy.get('form input[formControlName=name]').clear();
        cy.get('form input[formControlName=name]').should('have.class', 'ng-invalid');
        cy.get('form input[formControlName=name]').type('Test session');
        cy.get('form input[formControlName=date]').type('2024-12-01');
        cy.get('button[type=submit]').should('be.disabled');
        cy.get('form [formControlName=teacher_id]').click().get('mat-option').contains('Test Teacher').click();
        cy.get('button[type=submit]').should('be.disabled');
        cy.get('form [formControlName=description]').type('Test session description');
        cy.get('button[type=submit]').should('not.be.disabled');

        cy.get('button[type=submit]').click();
        cy.wait('@createSessionError');

        cy.contains('Session created !').should('not.exist');
        // current url should remain unchanged
        cy.url().should('to.match', /\/sessions\/create$/);
    })
    
    it('should create session when creation form valid', () => {
        const mockCreatedSession = {
            "id": 2,
            "name": "Test yoga session",
            "date": "2024-11-21T00:00:00.000+00:00",
            "teacher_id": 1,
            "description": "This is a test yoga session.",
            "users": [1],
            "createdAt": "2024-11-07T11:45:12",
            "updatedAt": "2024-11-07T11:45:12" 
        };

        cy.intercept(
            {method: 'POST', url: '/api/session'},
            {
                body: mockCreatedSession
            }
        ).as('createSession');
        
        let mockUpdatedSessions = structuredClone(mockSessions);
        mockUpdatedSessions.push(mockCreatedSession);
        cy.intercept(
            {
              method: 'GET',
              url: '/api/session',
              // force this interception to be called first to "override" interception made in cy.login()
              middleware: true,
              times: 1
            },
            (request => {request.reply(mockUpdatedSessions);})
        );

        cy.contains('Create').click();
        cy.contains('h1', 'Create session');
        cy.get('button[type=submit]').should('be.disabled');
        cy.get('form input[formControlName=name]').type("Test yoga session");
        cy.get('form input[formControlName=date]').type("2024-11-21");
        cy.get('form [formControlName=teacher_id]').click().get('mat-option').contains('Test Teacher').click();
        cy.get('form [formControlName=description]').type('This is a test yoga session');

        cy.get('button[type=submit]').click();

        cy.wait('@createSession');

        cy.contains('.mat-snack-bar-container', 'Session created !').should('be.visible');
        cy.url().should('to.match', /\/sessions$/);
        cy.get('.mat-card.item').should('have.lengthOf', 2);
    })

    it('should display update form and handle invalid fields or update error', () => {
        cy.intercept(
            {method: 'GET', url: '/api/session/1'},
            {
                body: mockSession
            }
        );

        cy.intercept(
            {method: 'PUT', url: '/api/session/1'},
            {
                statusCode: 400,
                times: 1
            }
        ).as('updateSessionError');

        cy.contains('Edit').click();
        cy.contains('h1', 'Update session');
        // check form initial state : pre filled with mockSession data, submit button enabled
        cy.get('form input[formControlName=name]').should('have.value', 'Beginner yoga session');
        cy.get('form input[formControlName=date]').should('have.value', '2024-12-01');
        // we click the already selected item to close the choices list 
        // this is mandatory because opening the options list creates an overlay that prevents further actions on other elements
        cy.get('form [formControlName=teacher_id]').click().get('mat-option.mat-selected').contains('Test Teacher').click();
        cy.get('form [formControlName=description]').should('have.value', 'This session allows beginners to discover yoga.');
        cy.get('button[type=submit]').should('not.be.disabled');

        // clear name and check error
        cy.get('form input[formControlName=name]').clear()
        cy.get('button[type=submit]').should('be.disabled');
        // clicks somewhere else in the page to remove focus from the name input
        cy.get('mat-toolbar').click();
        cy.get('form input[formControlName=name]').should('have.class', 'ng-invalid')

        cy.get('form input[formControlName=date]').clear()
        // clicks somewhere else in the page to remove focus from the date input
        cy.get('mat-toolbar').click();
        cy.get('form input[formControlName=date]').should('have.class', 'ng-invalid')  

        cy.get('form [formControlName=description]').clear()
        // clicks somewhere else in the page to remove focus from the description textarea
        cy.get('mat-toolbar').click();

        // set valid values and submit
        cy.get('form input[formControlName=name]').type('Test session');
        cy.get('form input[formControlName=date]').type('2024-12-01');
        cy.get('form [formControlName=teacher_id]').click().get('mat-option').contains('Test Teacher').click();
        cy.get('form [formControlName=description]').type('Test session description');
        cy.get('button[type=submit]').should('not.be.disabled');

        cy.get('button[type=submit]').click();
        cy.wait('@updateSessionError');

        cy.contains('Session updated !').should('not.exist');
        // current url should remain unchanged
        cy.url().should('to.match', /\/sessions\/update\/1$/);
    })
    
    it('should update session when form valid', () => {
        cy.intercept(
            {method: 'GET', url: '/api/session/1'},
            {
                body: mockSession
            }
        );

        const mockUpdatedSession = {
            "id": 1,
            "name": "Beginner yoga session updated ",
            "date": "2024-12-10T00:00:00.000+00:00",
            "teacher_id": 2,
            "description": "This session allows beginners to discover yoga. updated description",
            "users": [1],
            "createdAt": "2024-11-06T08:45:12",
            "updatedAt": "2024-11-07T11:30:54"  
        };

        cy.contains('Edit').click();
        cy.contains('h1', 'Update session');
        cy.get('form input[formControlName=name]').type(" updated");
        cy.get('form input[formControlName=date]').type("2024-12-10");
        cy.get('form [formControlName=teacher_id]').click().get('mat-option').contains('Other Teacher').click();
        cy.get('form [formControlName=description]').type(' updated description');

        // mock API responses
        cy.intercept(
            {method: 'PUT', url: '/api/session/1'},
            {
                body: mockUpdatedSession
            }
        ).as('updateSession');
        let mockUpdatedSessions = structuredClone(mockSessions);
        mockUpdatedSessions[0] = mockUpdatedSession;
        cy.intercept(
            {
              method: 'GET',
              url: '/api/session',
              // force this interception to be called first and one time only to "override" interception made before
              middleware: true,
              times: 1
            },
            (request => {request.reply(mockUpdatedSessions);})
        );

        // submit form and wait for update request response
        cy.get('button[type=submit]').click();
        cy.wait('@updateSession');

        // user should be redired to sessions list containing only the updated session
        cy.url().should('to.match', /\/sessions$/);
        cy.contains('.mat-snack-bar-container', 'Session updated !').should('be.visible');
        cy.get('.mat-card.item').should('have.lengthOf', 1);
        cy.contains('.mat-card-title', 'Beginner yoga session updated');
        cy.contains('Session on December 10, 2024')
        cy.contains('div', 'This session allows beginners to discover yoga. updated description');
    })
})