package org.jhipster.health.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.jhipster.health.domain.Bloodpressure;
import org.jhipster.health.repository.BloodpressureRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link Bloodpressure} entity.
 */
public interface BloodpressureSearchRepository
    extends ElasticsearchRepository<Bloodpressure, Long>, BloodpressureSearchRepositoryInternal {}

interface BloodpressureSearchRepositoryInternal {
    Page<Bloodpressure> search(String query, Pageable pageable);

    Page<Bloodpressure> search(Query query);

    void index(Bloodpressure entity);
}

class BloodpressureSearchRepositoryInternalImpl implements BloodpressureSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final BloodpressureRepository repository;

    BloodpressureSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, BloodpressureRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Bloodpressure> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Bloodpressure> search(Query query) {
        SearchHits<Bloodpressure> searchHits = elasticsearchTemplate.search(query, Bloodpressure.class);
        List<Bloodpressure> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Bloodpressure entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
