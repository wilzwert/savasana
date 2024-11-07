describe('Register', () => {  
  it('should fail', () => {
    cy.visit('/register')

    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400
    })
   
    
    cy.get('input[formControlName=firstName]').type("User")
    cy.get('input[formControlName=lastName]').type("Test")
    cy.get('input[formControlName=email]').type("test")
    cy.get('input[formControlName=password]').type("password{enter}{enter}")
    cy.get('input[formControlName=email]').should('have.class', 'ng-invalid');
    cy.get('button[type=submit]').should('be.disabled');
    cy.get('input[formControlName=email]').type("test@example.com{enter}{enter}")

    cy.get('span.error').should('be.visible').and('contain', 'An error occurred');
  })
  
  it('should redirect to login after successful registration', () => {
    cy.visit('/register')

    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200
    }).as('successfullRegistration');

     cy.get('input[formControlName=firstName]').type("User")
    cy.get('input[formControlName=lastName]').type("Test")
    cy.get('input[formControlName=email]').type("test@example.com")
    cy.get('input[formControlName=password]').type("password{enter}{enter}")
    cy.get('span.error').should('not.exist');

    cy.wait('@successfullRegistration');
    
    cy.url().should('to.match', /\/login$/)
  })
});