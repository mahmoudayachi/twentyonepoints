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
import org.jhipster.health.domain.Prefrences;
import org.jhipster.health.repository.PrefrencesRepository;
import org.jhipster.health.repository.search.PrefrencesSearchRepository;
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
 * REST controller for managing {@link org.jhipster.health.domain.Prefrences}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PrefrencesResource {

    private final Logger log = LoggerFactory.getLogger(PrefrencesResource.class);

    private static final String ENTITY_NAME = "prefrences";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PrefrencesRepository prefrencesRepository;

    private final PrefrencesSearchRepository prefrencesSearchRepository;

    public PrefrencesResource(PrefrencesRepository prefrencesRepository, PrefrencesSearchRepository prefrencesSearchRepository) {
        this.prefrencesRepository = prefrencesRepository;
        this.prefrencesSearchRepository = prefrencesSearchRepository;
    }

    /**
     * {@code POST  /prefrences} : Create a new prefrences.
     *
     * @param prefrences the prefrences to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new prefrences, or with status {@code 400 (Bad Request)} if the prefrences has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/prefrences")
    public ResponseEntity<Prefrences> createPrefrences(@Valid @RequestBody Prefrences prefrences) throws URISyntaxException {
        log.debug("REST request to save Prefrences : {}", prefrences);
        if (prefrences.getId() != null) {
            throw new BadRequestAlertException("A new prefrences cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Prefrences result = prefrencesRepository.save(prefrences);
        prefrencesSearchRepository.index(result);
        return ResponseEntity
            .created(new URI("/api/prefrences/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /prefrences/:id} : Updates an existing prefrences.
     *
     * @param id the id of the prefrences to save.
     * @param prefrences the prefrences to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated prefrences,
     * or with status {@code 400 (Bad Request)} if the prefrences is not valid,
     * or with status {@code 500 (Internal Server Error)} if the prefrences couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/prefrences/{id}")
    public ResponseEntity<Prefrences> updatePrefrences(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Prefrences prefrences
    ) throws URISyntaxException {
        log.debug("REST request to update Prefrences : {}, {}", id, prefrences);
        if (prefrences.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, prefrences.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!prefrencesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Prefrences result = prefrencesRepository.save(prefrences);
        prefrencesSearchRepository.index(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, prefrences.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /prefrences/:id} : Partial updates given fields of an existing prefrences, field will ignore if it is null
     *
     * @param id the id of the prefrences to save.
     * @param prefrences the prefrences to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated prefrences,
     * or with status {@code 400 (Bad Request)} if the prefrences is not valid,
     * or with status {@code 404 (Not Found)} if the prefrences is not found,
     * or with status {@code 500 (Internal Server Error)} if the prefrences couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/prefrences/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Prefrences> partialUpdatePrefrences(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Prefrences prefrences
    ) throws URISyntaxException {
        log.debug("REST request to partial update Prefrences partially : {}, {}", id, prefrences);
        if (prefrences.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, prefrences.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!prefrencesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Prefrences> result = prefrencesRepository
            .findById(prefrences.getId())
            .map(existingPrefrences -> {
                if (prefrences.getWeeklygoal() != null) {
                    existingPrefrences.setWeeklygoal(prefrences.getWeeklygoal());
                }
                if (prefrences.getWeightunits() != null) {
                    existingPrefrences.setWeightunits(prefrences.getWeightunits());
                }

                return existingPrefrences;
            })
            .map(prefrencesRepository::save)
            .map(savedPrefrences -> {
                prefrencesSearchRepository.save(savedPrefrences);

                return savedPrefrences;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, prefrences.getId().toString())
        );
    }

    /**
     * {@code GET  /prefrences} : get all the prefrences.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of prefrences in body.
     */
    @GetMapping("/prefrences")
    public ResponseEntity<List<Prefrences>> getAllPrefrences(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Prefrences");
        Page<Prefrences> page;
        if (eagerload) {
            page = prefrencesRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = prefrencesRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /prefrences/:id} : get the "id" prefrences.
     *
     * @param id the id of the prefrences to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the prefrences, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/prefrences/{id}")
    public ResponseEntity<Prefrences> getPrefrences(@PathVariable Long id) {
        log.debug("REST request to get Prefrences : {}", id);
        Optional<Prefrences> prefrences = prefrencesRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(prefrences);
    }

    /**
     * {@code DELETE  /prefrences/:id} : delete the "id" prefrences.
     *
     * @param id the id of the prefrences to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/prefrences/{id}")
    public ResponseEntity<Void> deletePrefrences(@PathVariable Long id) {
        log.debug("REST request to delete Prefrences : {}", id);
        prefrencesRepository.deleteById(id);
        prefrencesSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/prefrences?query=:query} : search for the prefrences corresponding
     * to the query.
     *
     * @param query the query of the prefrences search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/prefrences")
    public ResponseEntity<List<Prefrences>> searchPrefrences(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Prefrences for query {}", query);
        Page<Prefrences> page = prefrencesSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
