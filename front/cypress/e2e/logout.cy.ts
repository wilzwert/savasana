describe("Logout", () => {
        
    it('should logout user ', () => {
        cy.login();

        cy.contains('span.link', 'Logout').click();

        // we use a regexp because in some circumstances it seems a trailing slash may be added
        cy.url().should('to.match', new RegExp('^'+Cypress.config().baseUrl +'[/]*$'));
        // cy.url().should('eq', Cypress.config().baseUrl +'/');
        cy.contains('span.link', 'Account').should('not.exist');
        cy.contains('span.link', 'Sessions').should('not.exist');
        cy.contains('span.link', 'Login');
        cy.contains('span.link', 'Register');
    })
})