package org.jhipster.health.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Bloodpressure.
 */
@Entity
@Table(name = "bloodpressure")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "bloodpressure")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Bloodpressure implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "datetime", nullable = false)
    private ZonedDateTime datetime;

    @NotNull
    @Column(name = "systolic", nullable = false)
    private Float systolic;

    @NotNull
    @Column(name = "diastolic", nullable = false)
    private Float diastolic;

    @ManyToOne
    private User userbloodpressure;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Bloodpressure id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDatetime() {
        return this.datetime;
    }

    public Bloodpressure datetime(ZonedDateTime datetime) {
        this.setDatetime(datetime);
        return this;
    }

    public void setDatetime(ZonedDateTime datetime) {
        this.datetime = datetime;
    }

    public Float getSystolic() {
        return this.systolic;
    }

    public Bloodpressure systolic(Float systolic) {
        this.setSystolic(systolic);
        return this;
    }

    public void setSystolic(Float systolic) {
        this.systolic = systolic;
    }

    public Float getDiastolic() {
        return this.diastolic;
    }

    public Bloodpressure diastolic(Float diastolic) {
        this.setDiastolic(diastolic);
        return this;
    }

    public void setDiastolic(Float diastolic) {
        this.diastolic = diastolic;
    }

    public User getUserbloodpressure() {
        return this.userbloodpressure;
    }

    public void setUserbloodpressure(User user) {
        this.userbloodpressure = user;
    }

    public Bloodpressure userbloodpressure(User user) {
        this.setUserbloodpressure(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bloodpressure)) {
            return false;
        }
        return id != null && id.equals(((Bloodpressure) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Bloodpressure{" +
            "id=" + getId() +
            ", datetime='" + getDatetime() + "'" +
            ", systolic=" + getSystolic() +
            ", diastolic=" + getDiastolic() +
            "}";
    }

    public void setUser(User orElse) {}
}
