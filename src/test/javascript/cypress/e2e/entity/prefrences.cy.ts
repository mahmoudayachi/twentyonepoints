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

describe('Prefrences e2e test', () => {
  const prefrencesPageUrl = '/prefrences';
  const prefrencesPageUrlPattern = new RegExp('/prefrences(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const prefrencesSample = { weeklygoal: 17, weightunits: 'KG' };

  let prefrences;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/prefrences+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/prefrences').as('postEntityRequest');
    cy.intercept('DELETE', '/api/prefrences/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (prefrences) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/prefrences/${prefrences.id}`,
      }).then(() => {
        prefrences = undefined;
      });
    }
  });

  it('Prefrences menu should load Prefrences page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('prefrences');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Prefrences').should('exist');
    cy.url().should('match', prefrencesPageUrlPattern);
  });

  describe('Prefrences page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(prefrencesPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Prefrences page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/prefrences/new$'));
        cy.getEntityCreateUpdateHeading('Prefrences');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', prefrencesPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/prefrences',
          body: prefrencesSample,
        }).then(({ body }) => {
          prefrences = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/prefrences+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/prefrences?page=0&size=20>; rel="last",<http://localhost/api/prefrences?page=0&size=20>; rel="first"',
              },
              body: [prefrences],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(prefrencesPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Prefrences page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('prefrences');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', prefrencesPageUrlPattern);
      });

      it('edit button click should load edit Prefrences page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Prefrences');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', prefrencesPageUrlPattern);
      });

      it('edit button click should load edit Prefrences page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Prefrences');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', prefrencesPageUrlPattern);
      });

      it('last delete button click should delete instance of Prefrences', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('prefrences').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', prefrencesPageUrlPattern);

        prefrences = undefined;
      });
    });
  });

  describe('new Prefrences page', () => {
    beforeEach(() => {
      cy.visit(`${prefrencesPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Prefrences');
    });

    it('should create an instance of Prefrences', () => {
      cy.get(`[data-cy="weeklygoal"]`).type('19').should('have.value', '19');

      cy.get(`[data-cy="weightunits"]`).select('KG');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        prefrences = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', prefrencesPageUrlPattern);
    });
  });
});
