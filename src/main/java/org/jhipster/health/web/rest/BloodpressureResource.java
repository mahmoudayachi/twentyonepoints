package org.jhipster.health.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.jhipster.health.domain.Bloodpressure;
import org.jhipster.health.repository.BloodpressureRepository;
import org.jhipster.health.repository.UserRepository;
import org.jhipster.health.repository.search.BloodpressureSearchRepository;
import org.jhipster.health.security.AuthoritiesConstants;
import org.jhipster.health.security.SecurityUtils;
import org.jhipster.health.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.jhipster.health.domain.Bloodpressure}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BloodpressureResource {

    private final Logger log = LoggerFactory.getLogger(BloodpressureResource.class);

    private static final String ENTITY_NAME = "bloodpressure";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BloodpressureRepository bloodpressureRepository;

    private final BloodpressureSearchRepository bloodpressureSearchRepository;
    private final UserRepository userRepository;

    public BloodpressureResource(
        BloodpressureRepository bloodpressureRepository,
        BloodpressureSearchRepository bloodpressureSearchRepository,
        UserRepository userRepository
    ) {
        this.bloodpressureRepository = bloodpressureRepository;
        this.bloodpressureSearchRepository = bloodpressureSearchRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /bloodpressures} : Create a new bloodpressure.
     *
     * @param bloodpressure the bloodpressure to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bloodpressure, or with status {@code 400 (Bad Request)} if the bloodpressure has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/blood-pressures")
    public ResponseEntity<Bloodpressure> createBloodPressure(@Valid @RequestBody Bloodpressure bloodPressure) throws URISyntaxException {
        log.debug("REST request to save BloodPressure : {}", bloodPressure);
        if (bloodPressure.getId() != null) {
            throw new BadRequestAlertException("A new bloodPressure cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            log.debug("No user passed in, using current user: {}", SecurityUtils.getCurrentUserLogin().orElse(""));
            bloodPressure.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().orElse("")).orElse(null));
        }
        Bloodpressure result = bloodpressureRepository.save(bloodPressure);
        bloodpressureSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/blood-pressures/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /bloodpressures/:id} : Updates an existing bloodpressure.
     *
     * @param id the id of the bloodpressure to save.
     * @param bloodpressure the bloodpressure to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bloodpressure,
     * or with status {@code 400 (Bad Request)} if the bloodpressure is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bloodpressure couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/bloodpressures/{id}")
    public ResponseEntity<Bloodpressure> updateBloodpressure(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Bloodpressure bloodpressure
    ) throws URISyntaxException {
        log.debug("REST request to update Bloodpressure : {}, {}", id, bloodpressure);
        if (bloodpressure.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bloodpressure.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bloodpressureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Bloodpressure result = bloodpressureRepository.save(bloodpressure);
        bloodpressureSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bloodpressure.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /bloodpressures/:id} : Partial updates given fields of an existing bloodpressure, field will ignore if it is null
     *
     * @param id the id of the bloodpressure to save.
     * @param bloodpressure the bloodpressure to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bloodpressure,
     * or with status {@code 400 (Bad Request)} if the bloodpressure is not valid,
     * or with status {@code 404 (Not Found)} if the bloodpressure is not found,
     * or with status {@code 500 (Internal Server Error)} if the bloodpressure couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/bloodpressures/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Bloodpressure> partialUpdateBloodpressure(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Bloodpressure bloodpressure
    ) throws URISyntaxException {
        log.debug("REST request to partial update Bloodpressure partially : {}, {}", id, bloodpressure);
        if (bloodpressure.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bloodpressure.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bloodpressureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Bloodpressure> result = bloodpressureRepository
            .findById(bloodpressure.getId())
            .map(existingBloodpressure -> {
                if (bloodpressure.getDatetime() != null) {
                    existingBloodpressure.setDatetime(bloodpressure.getDatetime());
                }
                if (bloodpressure.getSystolic() != null) {
                    existingBloodpressure.setSystolic(bloodpressure.getSystolic());
                }
                if (bloodpressure.getDiastolic() != null) {
                    existingBloodpressure.setDiastolic(bloodpressure.getDiastolic());
                }

                return existingBloodpressure;
            })
            .map(bloodpressureRepository::save)
            .map(savedBloodpressure -> {
                bloodpressureSearchRepository.save(savedBloodpressure);

                return savedBloodpressure;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bloodpressure.getId().toString())
        );
    }

    /**
     * {@code GET  /bloodpressures} : get all the bloodpressures.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bloodpressures in body.
     */
    @GetMapping("/bloodpressures")
    public ResponseEntity<List<Bloodpressure>> getAllBloodpressures(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Bloodpressures");
        Page<Bloodpressure> page;
        if (eagerload) {
            page = bloodpressureRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = bloodpressureRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /bloodpressures/:id} : get the "id" bloodpressure.
     *
     * @param id the id of the bloodpressure to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bloodpressure, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/bloodpressures/{id}")
    public ResponseEntity<Bloodpressure> getBloodpressure(@PathVariable Long id) {
        log.debug("REST request to get Bloodpressure : {}", id);
        Optional<Bloodpressure> bloodpressure = bloodpressureRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(bloodpressure);
    }

    /**
     * {@code DELETE  /bloodpressures/:id} : delete the "id" bloodpressure.
     *
     * @param id the id of the bloodpressure to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/bloodpressures/{id}")
    public ResponseEntity<Void> deleteBloodpressure(@PathVariable Long id) {
        log.debug("REST request to delete Bloodpressure : {}", id);
        bloodpressureRepository.deleteById(id);
        bloodpressureSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/bloodpressures?query=:query} : search for the bloodpressure corresponding
     * to the query.
     *
     * @param query the query of the bloodpressure search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/bloodpressures")
    public ResponseEntity<List<Bloodpressure>> searchBloodpressures(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Bloodpressures for query {}", query);
        Page<Bloodpressure> page = bloodpressureSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
