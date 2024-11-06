describe("User info", () => {
        
    it('should redirect to login if not logged in', () => {
        cy.visit('/me');
        cy.url().should('to.match', /\/login$/)
    })

    it('should display admin user info', () => {
        cy.loginAdmin();

        cy.intercept(
            {
                method: 'GET',
                url: '/api/user/1',
                times: 1
            },
            req => {
                req.reply({
                    id: 1,
                    email: 'yoga@studio.com',
                    lastName: 'Admin',
                    firtsName: 'Admin',
                    admin: true,
                    createdAt: "2024-11-05T09:45:12",
                    updatedAt: "2024-11-06T08:23:54" 
                })
            }
        );
        
        cy.contains('span.link', 'Account').click();

        cy.url().should('to.match', /\/me$/);
        cy.contains('h1', 'User information')
        cy.contains('p', 'ADMIN')
        cy.contains('p', 'Email: yoga@studio.com')
        cy.contains('p', 'You are admin')
        cy.contains('p', 'November 5, 2024')
        cy.contains('p', 'November 6, 2024')

    })

    it('should display user info', () => {
        cy.login();

        cy.intercept(
            {
                method: 'GET',
                url: '/api/user/1',
                times: 1
            },
            req => {
                req.reply({
                    id: 1,
                    email: 'test@example.com',
                    lastName: 'User',
                    firtsName: 'Test',
                    admin: false,
                    createdAt: "2024-11-05T09:45:12",
                    updatedAt: "2024-11-06T08:23:54" 
                })
            }
        );
        
        cy.contains('span.link', 'Account').click();

        cy.url().should('to.match', /\/me$/);
        cy.contains('h1', 'User information')
        cy.contains('p', 'USER')
        cy.contains('p', 'Email: test@example.com')
        cy.contains('p', 'You are admin').should('not.exist')
        cy.contains('p', 'November 5, 2024')
        cy.contains('p', 'November 6, 2024')
        cy.contains('span', 'Detail')

    })

    it('should delete user account and redirect to root', () => {
        cy.login();

        cy.intercept(
            {
                method: 'GET',
                url: '/api/user/1',
                times: 1
            },
            req => {
                req.reply({
                    id: 1,
                    email: 'test@example.com',
                    lastName: 'User',
                    firtsName: 'Test',
                    admin: false,
                    createdAt: "2024-11-05T09:45:12",
                    updatedAt: "2024-11-06T08:23:54" 
                })
            }
        );
        
        cy.contains('span.link', 'Account').click();
        cy.url().should('to.match', /\/me$/);

        cy.intercept(
            {
                method: 'DELETE',
                url: '/api/user/1',
                times: 1
            },
            req => {
                req.reply({})
            }
        ).as('deleteUser');

        cy.contains('button', 'Detail').click();
        cy.wait('@deleteUser');

        // we use a regexp because in some circumstances it seems a trailing slash may be added
        cy.url().should('to.match', new RegExp('^'+Cypress.config().baseUrl +'[/]*$'));
        // cy.url().should('eq', Cypress.config().baseUrl +'/');
        cy.contains('.mat-snack-bar-container', 'Your account has been deleted !');
        cy.contains('span.link', 'Account').should('not.exist');
        cy.contains('Login');
        cy.contains('Register');
    })
})