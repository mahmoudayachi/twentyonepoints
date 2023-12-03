package org.jhipster.health.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jhipster.health.domain.enumeration.units;

/**
 * A Prefrences.
 */
@Entity
@Table(name = "prefrences")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "prefrences")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Prefrences implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 10)
    @Max(value = 21)
    @Column(name = "weeklygoal", nullable = false)
    private Integer weeklygoal;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "weightunits", nullable = false)
    private units weightunits;

    @OneToOne
    @JoinColumn(unique = true)
    private User userprefrences;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Prefrences id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getWeeklygoal() {
        return this.weeklygoal;
    }

    public Prefrences weeklygoal(Integer weeklygoal) {
        this.setWeeklygoal(weeklygoal);
        return this;
    }

    public void setWeeklygoal(Integer weeklygoal) {
        this.weeklygoal = weeklygoal;
    }

    public units getWeightunits() {
        return this.weightunits;
    }

    public Prefrences weightunits(units weightunits) {
        this.setWeightunits(weightunits);
        return this;
    }

    public void setWeightunits(units weightunits) {
        this.weightunits = weightunits;
    }

    public User getUserprefrences() {
        return this.userprefrences;
    }

    public void setUserprefrences(User user) {
        this.userprefrences = user;
    }

    public Prefrences userprefrences(User user) {
        this.setUserprefrences(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Prefrences)) {
            return false;
        }
        return id != null && id.equals(((Prefrences) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Prefrences{" +
            "id=" + getId() +
            ", weeklygoal=" + getWeeklygoal() +
            ", weightunits='" + getWeightunits() + "'" +
            "}";
    }

    public void setUser(User orElse) {}
}
