import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Bloodpressure e2e test', () => {
  const bloodpressurePageUrl = '/bloodpressure';
  const bloodpressurePageUrlPattern = new RegExp('/bloodpressure(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const bloodpressureSample = { datetime: '2023-11-28T12:37:03.299Z', systolic: 57421, diastolic: 8696 };

  let bloodpressure;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/bloodpressures+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/bloodpressures').as('postEntityRequest');
    cy.intercept('DELETE', '/api/bloodpressures/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (bloodpressure) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/bloodpressures/${bloodpressure.id}`,
      }).then(() => {
        bloodpressure = undefined;
      });
    }
  });

  it('Bloodpressures menu should load Bloodpressures page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('bloodpressure');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Bloodpressure').should('exist');
    cy.url().should('match', bloodpressurePageUrlPattern);
  });

  describe('Bloodpressure page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(bloodpressurePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Bloodpressure page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/bloodpressure/new$'));
        cy.getEntityCreateUpdateHeading('Bloodpressure');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', bloodpressurePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/bloodpressures',
          body: bloodpressureSample,
        }).then(({ body }) => {
          bloodpressure = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/bloodpressures+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/bloodpressures?page=0&size=20>; rel="last",<http://localhost/api/bloodpressures?page=0&size=20>; rel="first"',
              },
              body: [bloodpressure],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(bloodpressurePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Bloodpressure page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('bloodpressure');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', bloodpressurePageUrlPattern);
      });

      it('edit button click should load edit Bloodpressure page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Bloodpressure');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', bloodpressurePageUrlPattern);
      });

      it('edit button click should load edit Bloodpressure page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Bloodpressure');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', bloodpressurePageUrlPattern);
      });

      it('last delete button click should delete instance of Bloodpressure', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('bloodpressure').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', bloodpressurePageUrlPattern);

        bloodpressure = undefined;
      });
    });
  });

  describe('new Bloodpressure page', () => {
    beforeEach(() => {
      cy.visit(`${bloodpressurePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Bloodpressure');
    });

    it('should create an instance of Bloodpressure', () => {
      cy.get(`[data-cy="datetime"]`).type('2023-11-28T06:04').blur().should('have.value', '2023-11-28T06:04');

      cy.get(`[data-cy="systolic"]`).type('89247').should('have.value', '89247');

      cy.get(`[data-cy="diastolic"]`).type('43942').should('have.value', '43942');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        bloodpressure = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', bloodpressurePageUrlPattern);
    });
  });
});
