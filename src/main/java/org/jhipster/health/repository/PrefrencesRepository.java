package org.jhipster.health.repository;

import java.util.List;
import java.util.Optional;
import org.jhipster.health.domain.Prefrences;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Prefrences entity.
 */
@Repository
public interface PrefrencesRepository extends JpaRepository<Prefrences, Long> {
    default Optional<Prefrences> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Prefrences> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Prefrences> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct prefrences from Prefrences prefrences left join fetch prefrences.userprefrences",
        countQuery = "select count(distinct prefrences) from Prefrences prefrences"
    )
    Page<Prefrences> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct prefrences from Prefrences prefrences left join fetch prefrences.userprefrences")
    List<Prefrences> findAllWithToOneRelationships();

    @Query("select prefrences from Prefrences prefrences left join fetch prefrences.userprefrences where prefrences.id =:id")
    Optional<Prefrences> findOneWithToOneRelationships(@Param("id") Long id);
}
