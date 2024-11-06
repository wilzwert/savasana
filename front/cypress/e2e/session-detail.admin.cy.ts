describe("Sessions detail as an admin user", () => {
    let mockSessions: Array<any>;
    let mockSession: any;
    let mockTeacher: any;
    
    before(() => {
        cy.fixture('sessions.json').as('sessionsJson').then(json => {mockSessions = json; mockSession = json[0];});
        cy.fixture('teachers.json').as('teachersJson').then(json => {mockTeacher = json[0];});
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

        // login as an admin user
        cy.loginAdmin();
    })
    
    
    it('should display session detail with delete button when admin', () => {
        cy.contains("Detail").click();

        cy.url().should('to.match', /\/sessions\/detail\/1$/)

        cy.contains('h1', 'Beginner Yoga Session');
        cy.contains('span', 'Test TEACHER')
        cy.contains('span', '1 attendees');
        cy.contains('span', 'December 1, 2024');
        cy.contains('div', 'This session allows beginners to discover yoga.');
        cy.contains('div', 'November 6, 2024');
        cy.contains('div', 'November 7, 2024');
        cy.contains('delete');
    })

    it('should display session detail without participation button when admin user', () => {        
        cy.contains("Detail").click();

        cy.url().should('to.match', /\/sessions\/detail\/1$/)

        cy.contains('span', 'Participate').should('not.exist')
        cy.contains('span', 'Do not participate').should('not.exist')
    })

    it('should delete session and redirect to sessions list', () => {
        cy.contains("Detail").click();

        cy.url().should('to.match', /\/sessions\/detail\/1$/);

        cy.intercept(
            {method: 'DELETE', 'url': '/api/session/1'},
            req => {
                req.reply({})
            }
        ).as('deleteSession');
        
        cy.intercept(
            {method: 'GET', url: '/api/session', times: 1, middleware: true},
            req => {
                req.reply([])
            }
        );
        
        cy.contains('Delete').click();
        cy.wait('@deleteSession');
        cy.contains('Session deleted !')
        cy.url().should('to.match', /\/sessions$/);
        // sessions list should now be empty
        cy.get('.mat-card.item').should('have.lengthOf', 0);
    })
})