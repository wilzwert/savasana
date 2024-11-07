describe("Sessions detail", () => {
    let mockSessions: Array<any>;
    let mockSession: any;
    let mockTeacher: any;
    
    before(() => {
        cy.fixture('sessions.json').as('sessionsJson').then(json => { mockSessions = json; mockSession = json[0]; });
        cy.fixture('teachers.json').as('teachersJson').then(json => { mockTeacher = json[0];});
    })

    beforeEach(() => {
        cy.intercept(
            {
              method: 'GET',
              url: '/api/session',
              times: 1,
              middleware: true
            },
            (request => {request.reply(mockSessions);})
        );

        cy.intercept(
            {
              method: 'GET',
              url: `/api/session/${mockSession.id}`
            },
            (request => {request.reply(mockSession);})
        );

        cy.intercept(
            {
              method: 'GET',
              url: `/api/teacher/${mockTeacher.id}`
            },
            (request => {request.reply(mockTeacher);})
        );
    })
    
    it('should display session detail without delete button when not admin', () => {
        // login as a non-admin user
        cy.login();

        cy.contains("Detail").click();

        cy.url().should('to.match', /\/sessions\/detail\/1$/)

        cy.contains('h1', 'Beginner Yoga Session');
        cy.contains('span', 'Test TEACHER')
        cy.contains('span', '1 attendees');
        cy.contains('span', 'December 1, 2024');
        cy.contains('div', 'This session allows beginners to discover yoga.');
        cy.contains('div', 'November 6, 2024');
        cy.contains('div', 'November 7, 2024');
        cy.contains('delete').should('not.exist');

    })
    
    it('should display session detail with participate button when user has no participation', () => {
        // by default, we know the interception in cy.login command returns a non-admin user with id 1
        // this user already participates to the mockSession (see sessions.json)
        // thus for this test, we have to login as a non-admin user with an id of 2
        cy.intercept(
            {method: 'POST', url: '/api/auth/login', middleware: true},
            req => {
                req.reply({
                    type: 'Bearer',
                    token: 'access_token',
                    id: 2,
                    username: 'userName',
                    firstName: 'firstName',
                    lastName: 'lastName',
                    admin: false
                });
            }
        );
        // login as a non-admin user
        cy.login();

        cy.contains("Detail").click();

        cy.url().should('to.match', /\/sessions\/detail\/1$/)

        cy.contains('span', 'Participate')
    })
    
    it('should display session detail with do not participate button when user already participates', () => {
        // login as a non-admin user
        cy.login();

        cy.contains("Detail").click();

        cy.url().should('to.match', /\/sessions\/detail\/1$/)

        cy.contains('span', 'Do not participate')
    })

    it('should save participation and update detail page', () => {
        // by default, we know the interception in cy.login command returns a non-admin user with id 1
        // this user already participates to the mockSession (see sessions.json)
        // thus for this test, we have to login as a non-admin user with an id of 2
        cy.intercept(
            {method: 'POST', url: '/api/auth/login', middleware: true},
            req => {
                req.reply({
                    type: 'Bearer',
                    token: 'access_token',
                    id: 2,
                    username: 'userName',
                    firstName: 'firstName',
                    lastName: 'lastName',
                    admin: false
                })
            }
        );
        cy.login();

        cy.contains("Detail").click();

        cy.url().should('to.match', /\/sessions\/detail\/1$/)
        
        cy.intercept(
            {method: 'POST', url: '/api/session/1/participate/2'},
            {body: {}}
        ).as('participate')
        
        //simulate the new participation by adding the user id to the users field of a new mocked session
        let mockUpdatedSession = structuredClone(mockSession);
        mockUpdatedSession.users = [1, 2];
        cy.intercept(
            {method: 'GET', url: '/api/session/1'},
            {body: mockUpdatedSession}
        ).as('updatedSession')

        cy.contains('Participate').click();
        cy.wait('@updatedSession');

        // page should be updated
        cy.contains('span', 'Do not participate');
        cy.contains('span', '2 attendees');
    })

    it('should delete participation and update detail page', () => {
        // login as a non-admin user
        cy.login();

        cy.contains("Detail").click();

        cy.url().should('to.match', /\/sessions\/detail\/1$/)
        
        cy.intercept(
            {method: 'DELETE', url: '/api/session/1/participate/1'},
            {body: {}}
        ).as('participate')
        
        //simulate the new participation by removing the user id to the users field of a new mocked session
        let mockUpdatedSession = structuredClone(mockSession);
        mockUpdatedSession.users = [];
        cy.intercept(
            {method: 'GET', url: '/api/session/1'},
            {body: mockUpdatedSession}
        ).as('updatedSession')

        cy.contains('Do not participate').click();
        cy.wait('@updatedSession');

        // page should be updated
        cy.contains('span', 'Participate');
        cy.contains('span', '0 attendees');
    })
})