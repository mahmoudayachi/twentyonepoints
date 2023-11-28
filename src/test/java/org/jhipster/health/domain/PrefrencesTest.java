package org.jhipster.health.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.jhipster.health.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PrefrencesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Prefrences.class);
        Prefrences prefrences1 = new Prefrences();
        prefrences1.setId(1L);
        Prefrences prefrences2 = new Prefrences();
        prefrences2.setId(prefrences1.getId());
        assertThat(prefrences1).isEqualTo(prefrences2);
        prefrences2.setId(2L);
        assertThat(prefrences1).isNotEqualTo(prefrences2);
        prefrences1.setId(null);
        assertThat(prefrences1).isNotEqualTo(prefrences2);
    }
}
