describe("Sessions list", () => {
    let mockSessions: Array<Object>;
    
    before(() => {
        cy.fixture('sessions.json').as('sessionsJson').then(json => {mockSessions = json;});
    })

    beforeEach(() => {
        cy.intercept(
            {
              method: 'GET',
              url: '/api/session',
              // force this interception to be called first to "override" interception made in cy.login()
              middleware: true
            },
            (request => {request.reply(mockSessions);})
        );
    })
    
    it('should redirect to login if not logged in', () => {
        cy.visit('/sessions');
        cy.url().should('to.match', /\/login$/)
    })

    it('should display sessions list without create and detail buttons when not admin', () => {
        cy.login();
        
        cy.contains('Create').should('not.exist');
        cy.get('.mat-card.item').should('have.lengthOf', 1);
        cy.get(':contains("Edit")').should('not.exist');
    })
    
    it('should display sessions list with create and detail buttons when admin', () => {
        cy.loginAdmin();
        
        cy.contains('Create').should('exist');
        cy.get('.mat-card.item').should('have.lengthOf', 1);
        cy.get('.mat-button-wrapper :contains("Edit")').should('have.lengthOf', 1);
    })
})