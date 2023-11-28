package org.jhipster.health.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.jhipster.health.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BloodpressureTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bloodpressure.class);
        Bloodpressure bloodpressure1 = new Bloodpressure();
        bloodpressure1.setId(1L);
        Bloodpressure bloodpressure2 = new Bloodpressure();
        bloodpressure2.setId(bloodpressure1.getId());
        assertThat(bloodpressure1).isEqualTo(bloodpressure2);
        bloodpressure2.setId(2L);
        assertThat(bloodpressure1).isNotEqualTo(bloodpressure2);
        bloodpressure1.setId(null);
        assertThat(bloodpressure1).isNotEqualTo(bloodpressure2);
    }
}
