describe('Page not found', () => {
  
  it('Should redirect to /404 and display 404 message', () => {
    cy.visit('/unknown-url')

    cy.url().should('to.match', /\/404$/)
    cy.contains('h1', 'Page not found !');
  })
});