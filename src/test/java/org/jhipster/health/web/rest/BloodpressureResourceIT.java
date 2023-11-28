package org.jhipster.health.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.jhipster.health.web.rest.TestUtil.sameInstant;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
import org.jhipster.health.domain.Bloodpressure;
import org.jhipster.health.repository.BloodpressureRepository;
import org.jhipster.health.repository.search.BloodpressureSearchRepository;
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
 * Integration tests for the {@link BloodpressureResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BloodpressureResourceIT {

    private static final ZonedDateTime DEFAULT_DATETIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATETIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Float DEFAULT_SYSTOLIC = 1F;
    private static final Float UPDATED_SYSTOLIC = 2F;

    private static final Float DEFAULT_DIASTOLIC = 1F;
    private static final Float UPDATED_DIASTOLIC = 2F;

    private static final String ENTITY_API_URL = "/api/bloodpressures";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/bloodpressures";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BloodpressureRepository bloodpressureRepository;

    @Mock
    private BloodpressureRepository bloodpressureRepositoryMock;

    @Autowired
    private BloodpressureSearchRepository bloodpressureSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBloodpressureMockMvc;

    private Bloodpressure bloodpressure;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bloodpressure createEntity(EntityManager em) {
        Bloodpressure bloodpressure = new Bloodpressure()
            .datetime(DEFAULT_DATETIME)
            .systolic(DEFAULT_SYSTOLIC)
            .diastolic(DEFAULT_DIASTOLIC);
        return bloodpressure;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bloodpressure createUpdatedEntity(EntityManager em) {
        Bloodpressure bloodpressure = new Bloodpressure()
            .datetime(UPDATED_DATETIME)
            .systolic(UPDATED_SYSTOLIC)
            .diastolic(UPDATED_DIASTOLIC);
        return bloodpressure;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        bloodpressureSearchRepository.deleteAll();
        assertThat(bloodpressureSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        bloodpressure = createEntity(em);
    }

    @Test
    @Transactional
    void createBloodpressure() throws Exception {
        int databaseSizeBeforeCreate = bloodpressureRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        // Create the Bloodpressure
        restBloodpressureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bloodpressure)))
            .andExpect(status().isCreated());

        // Validate the Bloodpressure in the database
        List<Bloodpressure> bloodpressureList = bloodpressureRepository.findAll();
        assertThat(bloodpressureList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Bloodpressure testBloodpressure = bloodpressureList.get(bloodpressureList.size() - 1);
        assertThat(testBloodpressure.getDatetime()).isEqualTo(DEFAULT_DATETIME);
        assertThat(testBloodpressure.getSystolic()).isEqualTo(DEFAULT_SYSTOLIC);
        assertThat(testBloodpressure.getDiastolic()).isEqualTo(DEFAULT_DIASTOLIC);
    }

    @Test
    @Transactional
    void createBloodpressureWithExistingId() throws Exception {
        // Create the Bloodpressure with an existing ID
        bloodpressure.setId(1L);

        int databaseSizeBeforeCreate = bloodpressureRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restBloodpressureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bloodpressure)))
            .andExpect(status().isBadRequest());

        // Validate the Bloodpressure in the database
        List<Bloodpressure> bloodpressureList = bloodpressureRepository.findAll();
        assertThat(bloodpressureList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDatetimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = bloodpressureRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        // set the field null
        bloodpressure.setDatetime(null);

        // Create the Bloodpressure, which fails.

        restBloodpressureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bloodpressure)))
            .andExpect(status().isBadRequest());

        List<Bloodpressure> bloodpressureList = bloodpressureRepository.findAll();
        assertThat(bloodpressureList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSystolicIsRequired() throws Exception {
        int databaseSizeBeforeTest = bloodpressureRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        // set the field null
        bloodpressure.setSystolic(null);

        // Create the Bloodpressure, which fails.

        restBloodpressureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bloodpressure)))
            .andExpect(status().isBadRequest());

        List<Bloodpressure> bloodpressureList = bloodpressureRepository.findAll();
        assertThat(bloodpressureList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDiastolicIsRequired() throws Exception {
        int databaseSizeBeforeTest = bloodpressureRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        // set the field null
        bloodpressure.setDiastolic(null);

        // Create the Bloodpressure, which fails.

        restBloodpressureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bloodpressure)))
            .andExpect(status().isBadRequest());

        List<Bloodpressure> bloodpressureList = bloodpressureRepository.findAll();
        assertThat(bloodpressureList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllBloodpressures() throws Exception {
        // Initialize the database
        bloodpressureRepository.saveAndFlush(bloodpressure);

        // Get all the bloodpressureList
        restBloodpressureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bloodpressure.getId().intValue())))
            .andExpect(jsonPath("$.[*].datetime").value(hasItem(sameInstant(DEFAULT_DATETIME))))
            .andExpect(jsonPath("$.[*].systolic").value(hasItem(DEFAULT_SYSTOLIC.doubleValue())))
            .andExpect(jsonPath("$.[*].diastolic").value(hasItem(DEFAULT_DIASTOLIC.doubleValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBloodpressuresWithEagerRelationshipsIsEnabled() throws Exception {
        when(bloodpressureRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBloodpressureMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bloodpressureRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBloodpressuresWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bloodpressureRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBloodpressureMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(bloodpressureRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBloodpressure() throws Exception {
        // Initialize the database
        bloodpressureRepository.saveAndFlush(bloodpressure);

        // Get the bloodpressure
        restBloodpressureMockMvc
            .perform(get(ENTITY_API_URL_ID, bloodpressure.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bloodpressure.getId().intValue()))
            .andExpect(jsonPath("$.datetime").value(sameInstant(DEFAULT_DATETIME)))
            .andExpect(jsonPath("$.systolic").value(DEFAULT_SYSTOLIC.doubleValue()))
            .andExpect(jsonPath("$.diastolic").value(DEFAULT_DIASTOLIC.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingBloodpressure() throws Exception {
        // Get the bloodpressure
        restBloodpressureMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBloodpressure() throws Exception {
        // Initialize the database
        bloodpressureRepository.saveAndFlush(bloodpressure);

        int databaseSizeBeforeUpdate = bloodpressureRepository.findAll().size();
        bloodpressureSearchRepository.save(bloodpressure);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());

        // Update the bloodpressure
        Bloodpressure updatedBloodpressure = bloodpressureRepository.findById(bloodpressure.getId()).get();
        // Disconnect from session so that the updates on updatedBloodpressure are not directly saved in db
        em.detach(updatedBloodpressure);
        updatedBloodpressure.datetime(UPDATED_DATETIME).systolic(UPDATED_SYSTOLIC).diastolic(UPDATED_DIASTOLIC);

        restBloodpressureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBloodpressure.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBloodpressure))
            )
            .andExpect(status().isOk());

        // Validate the Bloodpressure in the database
        List<Bloodpressure> bloodpressureList = bloodpressureRepository.findAll();
        assertThat(bloodpressureList).hasSize(databaseSizeBeforeUpdate);
        Bloodpressure testBloodpressure = bloodpressureList.get(bloodpressureList.size() - 1);
        assertThat(testBloodpressure.getDatetime()).isEqualTo(UPDATED_DATETIME);
        assertThat(testBloodpressure.getSystolic()).isEqualTo(UPDATED_SYSTOLIC);
        assertThat(testBloodpressure.getDiastolic()).isEqualTo(UPDATED_DIASTOLIC);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Bloodpressure> bloodpressureSearchList = IterableUtils.toList(bloodpressureSearchRepository.findAll());
                Bloodpressure testBloodpressureSearch = bloodpressureSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testBloodpressureSearch.getDatetime()).isEqualTo(UPDATED_DATETIME);
                assertThat(testBloodpressureSearch.getSystolic()).isEqualTo(UPDATED_SYSTOLIC);
                assertThat(testBloodpressureSearch.getDiastolic()).isEqualTo(UPDATED_DIASTOLIC);
            });
    }

    @Test
    @Transactional
    void putNonExistingBloodpressure() throws Exception {
        int databaseSizeBeforeUpdate = bloodpressureRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        bloodpressure.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBloodpressureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bloodpressure.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bloodpressure))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bloodpressure in the database
        List<Bloodpressure> bloodpressureList = bloodpressureRepository.findAll();
        assertThat(bloodpressureList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchBloodpressure() throws Exception {
        int databaseSizeBeforeUpdate = bloodpressureRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        bloodpressure.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBloodpressureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bloodpressure))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bloodpressure in the database
        List<Bloodpressure> bloodpressureList = bloodpressureRepository.findAll();
        assertThat(bloodpressureList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBloodpressure() throws Exception {
        int databaseSizeBeforeUpdate = bloodpressureRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        bloodpressure.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBloodpressureMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bloodpressure)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bloodpressure in the database
        List<Bloodpressure> bloodpressureList = bloodpressureRepository.findAll();
        assertThat(bloodpressureList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateBloodpressureWithPatch() throws Exception {
        // Initialize the database
        bloodpressureRepository.saveAndFlush(bloodpressure);

        int databaseSizeBeforeUpdate = bloodpressureRepository.findAll().size();

        // Update the bloodpressure using partial update
        Bloodpressure partialUpdatedBloodpressure = new Bloodpressure();
        partialUpdatedBloodpressure.setId(bloodpressure.getId());

        partialUpdatedBloodpressure.diastolic(UPDATED_DIASTOLIC);

        restBloodpressureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBloodpressure.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBloodpressure))
            )
            .andExpect(status().isOk());

        // Validate the Bloodpressure in the database
        List<Bloodpressure> bloodpressureList = bloodpressureRepository.findAll();
        assertThat(bloodpressureList).hasSize(databaseSizeBeforeUpdate);
        Bloodpressure testBloodpressure = bloodpressureList.get(bloodpressureList.size() - 1);
        assertThat(testBloodpressure.getDatetime()).isEqualTo(DEFAULT_DATETIME);
        assertThat(testBloodpressure.getSystolic()).isEqualTo(DEFAULT_SYSTOLIC);
        assertThat(testBloodpressure.getDiastolic()).isEqualTo(UPDATED_DIASTOLIC);
    }

    @Test
    @Transactional
    void fullUpdateBloodpressureWithPatch() throws Exception {
        // Initialize the database
        bloodpressureRepository.saveAndFlush(bloodpressure);

        int databaseSizeBeforeUpdate = bloodpressureRepository.findAll().size();

        // Update the bloodpressure using partial update
        Bloodpressure partialUpdatedBloodpressure = new Bloodpressure();
        partialUpdatedBloodpressure.setId(bloodpressure.getId());

        partialUpdatedBloodpressure.datetime(UPDATED_DATETIME).systolic(UPDATED_SYSTOLIC).diastolic(UPDATED_DIASTOLIC);

        restBloodpressureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBloodpressure.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBloodpressure))
            )
            .andExpect(status().isOk());

        // Validate the Bloodpressure in the database
        List<Bloodpressure> bloodpressureList = bloodpressureRepository.findAll();
        assertThat(bloodpressureList).hasSize(databaseSizeBeforeUpdate);
        Bloodpressure testBloodpressure = bloodpressureList.get(bloodpressureList.size() - 1);
        assertThat(testBloodpressure.getDatetime()).isEqualTo(UPDATED_DATETIME);
        assertThat(testBloodpressure.getSystolic()).isEqualTo(UPDATED_SYSTOLIC);
        assertThat(testBloodpressure.getDiastolic()).isEqualTo(UPDATED_DIASTOLIC);
    }

    @Test
    @Transactional
    void patchNonExistingBloodpressure() throws Exception {
        int databaseSizeBeforeUpdate = bloodpressureRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        bloodpressure.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBloodpressureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bloodpressure.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bloodpressure))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bloodpressure in the database
        List<Bloodpressure> bloodpressureList = bloodpressureRepository.findAll();
        assertThat(bloodpressureList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBloodpressure() throws Exception {
        int databaseSizeBeforeUpdate = bloodpressureRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        bloodpressure.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBloodpressureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bloodpressure))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bloodpressure in the database
        List<Bloodpressure> bloodpressureList = bloodpressureRepository.findAll();
        assertThat(bloodpressureList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBloodpressure() throws Exception {
        int databaseSizeBeforeUpdate = bloodpressureRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        bloodpressure.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBloodpressureMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(bloodpressure))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bloodpressure in the database
        List<Bloodpressure> bloodpressureList = bloodpressureRepository.findAll();
        assertThat(bloodpressureList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteBloodpressure() throws Exception {
        // Initialize the database
        bloodpressureRepository.saveAndFlush(bloodpressure);
        bloodpressureRepository.save(bloodpressure);
        bloodpressureSearchRepository.save(bloodpressure);

        int databaseSizeBeforeDelete = bloodpressureRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the bloodpressure
        restBloodpressureMockMvc
            .perform(delete(ENTITY_API_URL_ID, bloodpressure.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Bloodpressure> bloodpressureList = bloodpressureRepository.findAll();
        assertThat(bloodpressureList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bloodpressureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchBloodpressure() throws Exception {
        // Initialize the database
        bloodpressure = bloodpressureRepository.saveAndFlush(bloodpressure);
        bloodpressureSearchRepository.save(bloodpressure);

        // Search the bloodpressure
        restBloodpressureMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + bloodpressure.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bloodpressure.getId().intValue())))
            .andExpect(jsonPath("$.[*].datetime").value(hasItem(sameInstant(DEFAULT_DATETIME))))
            .andExpect(jsonPath("$.[*].systolic").value(hasItem(DEFAULT_SYSTOLIC.doubleValue())))
            .andExpect(jsonPath("$.[*].diastolic").value(hasItem(DEFAULT_DIASTOLIC.doubleValue())));
    }
}
