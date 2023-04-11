package com.petroandrushchak.unit.tests;

import com.petroandrushchak.steps.FutBinMappingSteps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class FutBinParsingTest {

    @Autowired
    FutBinMappingSteps futBinMappingSteps;

    @Test
    void testConvertStringToLong() {
        assertThat(futBinMappingSteps.parsePrice("")).isZero();
        assertThat(futBinMappingSteps.parsePrice(null)).isZero();
        assertThat(futBinMappingSteps.parsePrice("invalid")).isZero();
        assertThat(futBinMappingSteps.parsePrice("900")).isEqualTo(900L);
        assertThat(futBinMappingSteps.parsePrice("1.5K")).isEqualTo(1500L);
        assertThat(futBinMappingSteps.parsePrice("684.45K")).isEqualTo(684450L);
        assertThat(futBinMappingSteps.parsePrice("420K")).isEqualTo(420000L);
        assertThat(futBinMappingSteps.parsePrice("4.94M")).isEqualTo(4940000L);
        assertThat(futBinMappingSteps.parsePrice("4.94m")).isEqualTo(4940000L);
    }
}
