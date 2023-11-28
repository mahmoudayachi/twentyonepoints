package org.jhipster.health.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.jhipster.health.IntegrationTest;
import org.jhipster.health.domain.Prefrences;
import org.jhipster.health.domain.enumeration.units;
import org.jhipster.health.repository.PrefrencesRepository;
import org.jhipster.health.repository.search.PrefrencesSearchRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PrefrencesResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PrefrencesResourceIT {

    private static final Integer DEFAULT_WEEKLYGOAL = 10;
    private static final Integer UPDATED_WEEKLYGOAL = 11;

    private static final units DEFAULT_WEIGHTUNITS = units.KG;
    private static final units UPDATED_WEIGHTUNITS = units.LB;

    private static final String ENTITY_API_URL = "/api/prefrences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/prefrences";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PrefrencesRepository prefrencesRepository;

    @Mock
    private PrefrencesRepository prefrencesRepositoryMock;

    @Autowired
    private PrefrencesSearchRepository prefrencesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPrefrencesMockMvc;

    private Prefrences prefrences;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Prefrences createEntity(EntityManager em) {
        Prefrences prefrences = new Prefrences().weeklygoal(DEFAULT_WEEKLYGOAL).weightunits(DEFAULT_WEIGHTUNITS);
        return prefrences;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Prefrences createUpdatedEntity(EntityManager em) {
        Prefrences prefrences = new Prefrences().weeklygoal(UPDATED_WEEKLYGOAL).weightunits(UPDATED_WEIGHTUNITS);
        return prefrences;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        prefrencesSearchRepository.deleteAll();
        assertThat(prefrencesSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        prefrences = createEntity(em);
    }

    @Test
    @Transactional
    void createPrefrences() throws Exception {
        int databaseSizeBeforeCreate = prefrencesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        // Create the Prefrences
        restPrefrencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(prefrences)))
            .andExpect(status().isCreated());

        // Validate the Prefrences in the database
        List<Prefrences> prefrencesList = prefrencesRepository.findAll();
        assertThat(prefrencesList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Prefrences testPrefrences = prefrencesList.get(prefrencesList.size() - 1);
        assertThat(testPrefrences.getWeeklygoal()).isEqualTo(DEFAULT_WEEKLYGOAL);
        assertThat(testPrefrences.getWeightunits()).isEqualTo(DEFAULT_WEIGHTUNITS);
    }

    @Test
    @Transactional
    void createPrefrencesWithExistingId() throws Exception {
        // Create the Prefrences with an existing ID
        prefrences.setId(1L);

        int databaseSizeBeforeCreate = prefrencesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPrefrencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(prefrences)))
            .andExpect(status().isBadRequest());

        // Validate the Prefrences in the database
        List<Prefrences> prefrencesList = prefrencesRepository.findAll();
        assertThat(prefrencesList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkWeeklygoalIsRequired() throws Exception {
        int databaseSizeBeforeTest = prefrencesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        // set the field null
        prefrences.setWeeklygoal(null);

        // Create the Prefrences, which fails.

        restPrefrencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(prefrences)))
            .andExpect(status().isBadRequest());

        List<Prefrences> prefrencesList = prefrencesRepository.findAll();
        assertThat(prefrencesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkWeightunitsIsRequired() throws Exception {
        int databaseSizeBeforeTest = prefrencesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        // set the field null
        prefrences.setWeightunits(null);

        // Create the Prefrences, which fails.

        restPrefrencesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(prefrences)))
            .andExpect(status().isBadRequest());

        List<Prefrences> prefrencesList = prefrencesRepository.findAll();
        assertThat(prefrencesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllPrefrences() throws Exception {
        // Initialize the database
        prefrencesRepository.saveAndFlush(prefrences);

        // Get all the prefrencesList
        restPrefrencesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prefrences.getId().intValue())))
            .andExpect(jsonPath("$.[*].weeklygoal").value(hasItem(DEFAULT_WEEKLYGOAL)))
            .andExpect(jsonPath("$.[*].weightunits").value(hasItem(DEFAULT_WEIGHTUNITS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPrefrencesWithEagerRelationshipsIsEnabled() throws Exception {
        when(prefrencesRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPrefrencesMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(prefrencesRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPrefrencesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(prefrencesRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPrefrencesMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(prefrencesRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPrefrences() throws Exception {
        // Initialize the database
        prefrencesRepository.saveAndFlush(prefrences);

        // Get the prefrences
        restPrefrencesMockMvc
            .perform(get(ENTITY_API_URL_ID, prefrences.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(prefrences.getId().intValue()))
            .andExpect(jsonPath("$.weeklygoal").value(DEFAULT_WEEKLYGOAL))
            .andExpect(jsonPath("$.weightunits").value(DEFAULT_WEIGHTUNITS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPrefrences() throws Exception {
        // Get the prefrences
        restPrefrencesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPrefrences() throws Exception {
        // Initialize the database
        prefrencesRepository.saveAndFlush(prefrences);

        int databaseSizeBeforeUpdate = prefrencesRepository.findAll().size();
        prefrencesSearchRepository.save(prefrences);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());

        // Update the prefrences
        Prefrences updatedPrefrences = prefrencesRepository.findById(prefrences.getId()).get();
        // Disconnect from session so that the updates on updatedPrefrences are not directly saved in db
        em.detach(updatedPrefrences);
        updatedPrefrences.weeklygoal(UPDATED_WEEKLYGOAL).weightunits(UPDATED_WEIGHTUNITS);

        restPrefrencesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPrefrences.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPrefrences))
            )
            .andExpect(status().isOk());

        // Validate the Prefrences in the database
        List<Prefrences> prefrencesList = prefrencesRepository.findAll();
        assertThat(prefrencesList).hasSize(databaseSizeBeforeUpdate);
        Prefrences testPrefrences = prefrencesList.get(prefrencesList.size() - 1);
        assertThat(testPrefrences.getWeeklygoal()).isEqualTo(UPDATED_WEEKLYGOAL);
        assertThat(testPrefrences.getWeightunits()).isEqualTo(UPDATED_WEIGHTUNITS);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Prefrences> prefrencesSearchList = IterableUtils.toList(prefrencesSearchRepository.findAll());
                Prefrences testPrefrencesSearch = prefrencesSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testPrefrencesSearch.getWeeklygoal()).isEqualTo(UPDATED_WEEKLYGOAL);
                assertThat(testPrefrencesSearch.getWeightunits()).isEqualTo(UPDATED_WEIGHTUNITS);
            });
    }

    @Test
    @Transactional
    void putNonExistingPrefrences() throws Exception {
        int databaseSizeBeforeUpdate = prefrencesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        prefrences.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrefrencesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, prefrences.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(prefrences))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prefrences in the database
        List<Prefrences> prefrencesList = prefrencesRepository.findAll();
        assertThat(prefrencesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchPrefrences() throws Exception {
        int databaseSizeBeforeUpdate = prefrencesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        prefrences.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrefrencesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(prefrences))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prefrences in the database
        List<Prefrences> prefrencesList = prefrencesRepository.findAll();
        assertThat(prefrencesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPrefrences() throws Exception {
        int databaseSizeBeforeUpdate = prefrencesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        prefrences.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrefrencesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(prefrences)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Prefrences in the database
        List<Prefrences> prefrencesList = prefrencesRepository.findAll();
        assertThat(prefrencesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdatePrefrencesWithPatch() throws Exception {
        // Initialize the database
        prefrencesRepository.saveAndFlush(prefrences);

        int databaseSizeBeforeUpdate = prefrencesRepository.findAll().size();

        // Update the prefrences using partial update
        Prefrences partialUpdatedPrefrences = new Prefrences();
        partialUpdatedPrefrences.setId(prefrences.getId());

        partialUpdatedPrefrences.weeklygoal(UPDATED_WEEKLYGOAL).weightunits(UPDATED_WEIGHTUNITS);

        restPrefrencesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPrefrences.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPrefrences))
            )
            .andExpect(status().isOk());

        // Validate the Prefrences in the database
        List<Prefrences> prefrencesList = prefrencesRepository.findAll();
        assertThat(prefrencesList).hasSize(databaseSizeBeforeUpdate);
        Prefrences testPrefrences = prefrencesList.get(prefrencesList.size() - 1);
        assertThat(testPrefrences.getWeeklygoal()).isEqualTo(UPDATED_WEEKLYGOAL);
        assertThat(testPrefrences.getWeightunits()).isEqualTo(UPDATED_WEIGHTUNITS);
    }

    @Test
    @Transactional
    void fullUpdatePrefrencesWithPatch() throws Exception {
        // Initialize the database
        prefrencesRepository.saveAndFlush(prefrences);

        int databaseSizeBeforeUpdate = prefrencesRepository.findAll().size();

        // Update the prefrences using partial update
        Prefrences partialUpdatedPrefrences = new Prefrences();
        partialUpdatedPrefrences.setId(prefrences.getId());

        partialUpdatedPrefrences.weeklygoal(UPDATED_WEEKLYGOAL).weightunits(UPDATED_WEIGHTUNITS);

        restPrefrencesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPrefrences.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPrefrences))
            )
            .andExpect(status().isOk());

        // Validate the Prefrences in the database
        List<Prefrences> prefrencesList = prefrencesRepository.findAll();
        assertThat(prefrencesList).hasSize(databaseSizeBeforeUpdate);
        Prefrences testPrefrences = prefrencesList.get(prefrencesList.size() - 1);
        assertThat(testPrefrences.getWeeklygoal()).isEqualTo(UPDATED_WEEKLYGOAL);
        assertThat(testPrefrences.getWeightunits()).isEqualTo(UPDATED_WEIGHTUNITS);
    }

    @Test
    @Transactional
    void patchNonExistingPrefrences() throws Exception {
        int databaseSizeBeforeUpdate = prefrencesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        prefrences.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrefrencesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, prefrences.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(prefrences))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prefrences in the database
        List<Prefrences> prefrencesList = prefrencesRepository.findAll();
        assertThat(prefrencesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPrefrences() throws Exception {
        int databaseSizeBeforeUpdate = prefrencesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        prefrences.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrefrencesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(prefrences))
            )
            .andExpect(status().isBadRequest());

        // Validate the Prefrences in the database
        List<Prefrences> prefrencesList = prefrencesRepository.findAll();
        assertThat(prefrencesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPrefrences() throws Exception {
        int databaseSizeBeforeUpdate = prefrencesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        prefrences.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrefrencesMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(prefrences))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Prefrences in the database
        List<Prefrences> prefrencesList = prefrencesRepository.findAll();
        assertThat(prefrencesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deletePrefrences() throws Exception {
        // Initialize the database
        prefrencesRepository.saveAndFlush(prefrences);
        prefrencesRepository.save(prefrences);
        prefrencesSearchRepository.save(prefrences);

        int databaseSizeBeforeDelete = prefrencesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the prefrences
        restPrefrencesMockMvc
            .perform(delete(ENTITY_API_URL_ID, prefrences.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Prefrences> prefrencesList = prefrencesRepository.findAll();
        assertThat(prefrencesList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(prefrencesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchPrefrences() throws Exception {
        // Initialize the database
        prefrences = prefrencesRepository.saveAndFlush(prefrences);
        prefrencesSearchRepository.save(prefrences);

        // Search the prefrences
        restPrefrencesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + prefrences.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(prefrences.getId().intValue())))
            .andExpect(jsonPath("$.[*].weeklygoal").value(hasItem(DEFAULT_WEEKLYGOAL)))
            .andExpect(jsonPath("$.[*].weightunits").value(hasItem(DEFAULT_WEIGHTUNITS.toString())));
    }
}
