# Savasana

A simple app with a Java backend and Angular frontend.
This is the fifth project of my course as a full-stack Java / Angular developer with OpenClassrooms.
The goal here is to write unit, integration and e2e tests for the app provided by Openclassrooms, with a code coverage requirement of 80%.  

# Installation
## Requirements
- Java 8
- Access to a MySQL server and a database
- Maven
- Git
- Postman for testing purposes

## Installation
1. Clone this repository on your local machine or server and go to directory

``` bash
git clone https://github.com/wilzwert/savasana.git
cd savasana
```

2. Configuration

MySQL configuration by default :

> database name = 'test'

> user = 'user'

> password = '123456'

You can change this configuration by editing the application.properties files located in : 

> back/src/main/resources


3. Import the schema provided in ressources/sql/script.sql into your database

You can either use a GUI like PhpMyAdmin or load the file from command line :

``` bash
 mysql -u user -p database_name < resources/sql/script.sql
 ```

4. Install backend dependencies

Windows :
``` bash
cd back
mvnw clean install
```

Linux / unix :
``` bash
cd back
mvn clean install
```

Or directly in your favorite IDE

5. Install frontend dependencies

``` bash
cd front
npm install
```

# Start the backend API

Windows :
``` bash 
cd back
mvnw spring-boot:run
```

Linux / unix :
``` bash
cd back 
mvn spring-boot:run
```

This will make the API available on localhost:8080.

# Start the frontend application

- run frontend
``` bash 
cd front
npm run start
```
or
``` bash 
cd front
ng serve
```

# Test the API
1. With provided Postman collection : see ressources/postman/yoga.postman_collection.json

Import the collection in Postman : https://learning.postman.com/docs/getting-started/importing-and-exporting-data/#importing-data-into-postman

Then run the collection.

2. Use the "real" frontend provided by OpenClassrooms, available by default on http://localhost:4200 once frontend application is started.


# Testing and code coverage
## Backend

Unitary and integration tests are located in back/src/test/java/com.openclassrooms.starterjwt.
Unitary tests classes names are suffixed by 'Test', integration tests classes names are suffixed by 'IT'.

> cd back

### Unit tests and jacoco report

Unitary tests are run with Maven Surefire Plugin. 

> mvn clean test

Jacoco report with code coverage is located in target/site/jacoco-ut/index.html

### Unit and integration tests with jacoco report

Integration tests are run with Maven Failsafe Plugin. 

> mvn clean verify

Jacoco report with code coverage is located in target/site/jacoco-merged/index.html

## Frontend

### Unitary and integration tests

Unitary tests filenames are suffixed with .spec.ts.

Integration tests filenames are suffixed with .integration.spec.ts.

Launching tests:

> npm run test

for following change:

> npm run test:watch

Launch unitary and integration tests with coverage report generation

> npm run test:coverage

Report is available here:

> front/coverage/jest/lcov-report/index.html

### E2E tests with Cypress

End-to-end tests files are located in front/cypress/e2e.

Launching e2e tests manually with Cypress UI :

> npm run e2e

Launching all e2e tests automatically in CLI (headless) with report generation :

> "npm run e2e:ci"

Report is available here:

> front/coverage/e2e/lcov-report/index.html