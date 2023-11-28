package org.jhipster.health.repository;

import java.util.List;
import java.util.Optional;
import org.jhipster.health.domain.Bloodpressure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Bloodpressure entity.
 */
@Repository
public interface BloodpressureRepository extends JpaRepository<Bloodpressure, Long> {
    @Query("select bloodpressure from Bloodpressure bloodpressure where bloodpressure.userbloodpressure.login = ?#{principal.username}")
    List<Bloodpressure> findByUserbloodpressureIsCurrentUser();

    default Optional<Bloodpressure> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Bloodpressure> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Bloodpressure> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct bloodpressure from Bloodpressure bloodpressure left join fetch bloodpressure.userbloodpressure",
        countQuery = "select count(distinct bloodpressure) from Bloodpressure bloodpressure"
    )
    Page<Bloodpressure> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct bloodpressure from Bloodpressure bloodpressure left join fetch bloodpressure.userbloodpressure")
    List<Bloodpressure> findAllWithToOneRelationships();

    @Query(
        "select bloodpressure from Bloodpressure bloodpressure left join fetch bloodpressure.userbloodpressure where bloodpressure.id =:id"
    )
    Optional<Bloodpressure> findOneWithToOneRelationships(@Param("id") Long id);
}
