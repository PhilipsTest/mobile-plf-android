package com.philips.pins.shinelib;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SHNUserConfigurationCalculationsTest {

    private SHNUserConfigurationCalculations calculations;

    @Before
    public void setUp() {
        calculations = new SHNUserConfigurationCalculations();
    }

    @Test
    public void calculationForYoungMen() {
        Integer base = calculations.getBaseMetabolicRate(0d, 0, 20, SHNUserConfiguration.Sex.Male);
        assertThat(base).isEqualTo(SHNUserConfigurationCalculations.YOUNG_MEN_BASE_KILO_CALORIES);

        Integer baseWithWeight1 = calculations.getBaseMetabolicRate(1d, 0, 20, SHNUserConfiguration.Sex.Male);
        assertThat(baseWithWeight1).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.YOUNG_MEN_BASE_KILO_CALORIES + SHNUserConfigurationCalculations.YOUNG_MEN_WEIGHT_KG_KILO_CALORIES));

        Integer baseWithHeight1 = calculations.getBaseMetabolicRate(0d, 1, 20, SHNUserConfiguration.Sex.Male);
        assertThat(baseWithHeight1).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.YOUNG_MEN_BASE_KILO_CALORIES + SHNUserConfigurationCalculations.YOUNG_MEN_HEIGHT_M_KILO_CALORIES / 100d));

        Integer complex = calculations.getBaseMetabolicRate(2d, 2, 20, SHNUserConfiguration.Sex.Male);
        assertThat(complex).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.YOUNG_MEN_BASE_KILO_CALORIES + 2 * SHNUserConfigurationCalculations.YOUNG_MEN_HEIGHT_M_KILO_CALORIES / 100d + 2 * SHNUserConfigurationCalculations.YOUNG_MEN_WEIGHT_KG_KILO_CALORIES));
    }

    @Test
    public void calculationForYoungWoman() {
        Integer base = calculations.getBaseMetabolicRate(0d, 0, 20, SHNUserConfiguration.Sex.Female);
        assertThat(base).isEqualTo(SHNUserConfigurationCalculations.YOUNG_WOMEN_BASE_KILO_CALORIES);

        Integer baseWithWeight1 = calculations.getBaseMetabolicRate(1d, 0, 20, SHNUserConfiguration.Sex.Female);
        assertThat(baseWithWeight1).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.YOUNG_WOMEN_BASE_KILO_CALORIES + SHNUserConfigurationCalculations.YOUNG_WOMEN_WEIGHT_KG_KILO_CALORIES));

        Integer baseWithHeight1 = calculations.getBaseMetabolicRate(0d, 1, 20, SHNUserConfiguration.Sex.Female);
        assertThat(baseWithHeight1).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.YOUNG_WOMEN_BASE_KILO_CALORIES + SHNUserConfigurationCalculations.YOUNG_WOMEN_HEIGHT_M_KILO_CALORIES / 100d));

        Integer complex = calculations.getBaseMetabolicRate(2d, 2, 20, SHNUserConfiguration.Sex.Female);
        assertThat(complex).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.YOUNG_WOMEN_BASE_KILO_CALORIES + 2 * SHNUserConfigurationCalculations.YOUNG_WOMEN_HEIGHT_M_KILO_CALORIES / 100d + 2 * SHNUserConfigurationCalculations.YOUNG_WOMEN_WEIGHT_KG_KILO_CALORIES));
    }

    @Test
    public void calculationForAdultMen() {
        Integer base = calculations.getBaseMetabolicRate(0d, 0, 40, SHNUserConfiguration.Sex.Male);
        assertThat(base).isEqualTo(SHNUserConfigurationCalculations.ADULT_MEN_BASE_KILO_CALORIES);

        Integer baseWithWeight1 = calculations.getBaseMetabolicRate(1d, 0, 40, SHNUserConfiguration.Sex.Male);
        assertThat(baseWithWeight1).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.ADULT_MEN_BASE_KILO_CALORIES + SHNUserConfigurationCalculations.ADULT_MEN_WEIGHT_KG_KILO_CALORIES));

        Integer baseWithHeight1 = calculations.getBaseMetabolicRate(0d, 1, 40, SHNUserConfiguration.Sex.Male);
        assertThat(baseWithHeight1).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.ADULT_MEN_BASE_KILO_CALORIES + SHNUserConfigurationCalculations.ADULT_MEN_HEIGHT_M_KILO_CALORIES / 100d));

        Integer complex = calculations.getBaseMetabolicRate(2d, 2, 40, SHNUserConfiguration.Sex.Male);
        assertThat(complex).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.ADULT_MEN_BASE_KILO_CALORIES + 2 * SHNUserConfigurationCalculations.ADULT_MEN_HEIGHT_M_KILO_CALORIES / 100d + 2 * SHNUserConfigurationCalculations.ADULT_MEN_WEIGHT_KG_KILO_CALORIES));
    }

    @Test
    public void calculationForAdultWoman() {
        Integer base = calculations.getBaseMetabolicRate(0d, 0, 40, SHNUserConfiguration.Sex.Female);
        assertThat(base).isEqualTo(SHNUserConfigurationCalculations.ADULT_WOMEN_BASE_KILO_CALORIES);

        Integer baseWithWeight1 = calculations.getBaseMetabolicRate(1d, 0, 40, SHNUserConfiguration.Sex.Female);
        assertThat(baseWithWeight1).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.ADULT_WOMEN_BASE_KILO_CALORIES + SHNUserConfigurationCalculations.ADULT_WOMEN_WEIGHT_KG_KILO_CALORIES));

        Integer baseWithHeight1 = calculations.getBaseMetabolicRate(0d, 1, 40, SHNUserConfiguration.Sex.Female);
        assertThat(baseWithHeight1).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.ADULT_WOMEN_BASE_KILO_CALORIES + SHNUserConfigurationCalculations.ADULT_WOMEN_HEIGHT_M_KILO_CALORIES / 100d));

        Integer complex = calculations.getBaseMetabolicRate(2d, 2, 40, SHNUserConfiguration.Sex.Female);
        assertThat(complex).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.ADULT_WOMEN_BASE_KILO_CALORIES + 2 * SHNUserConfigurationCalculations.ADULT_WOMEN_HEIGHT_M_KILO_CALORIES / 100d + 2 * SHNUserConfigurationCalculations.ADULT_WOMEN_WEIGHT_KG_KILO_CALORIES));
    }

    @Test
    public void calculationForElderlyMen() {
        Integer base = calculations.getBaseMetabolicRate(0d, 0, 70, SHNUserConfiguration.Sex.Male);
        assertThat(base).isEqualTo(SHNUserConfigurationCalculations.ELDERLY_MEN_BASE_KILO_CALORIES);

        Integer baseWithWeight1 = calculations.getBaseMetabolicRate(1d, 0, 70, SHNUserConfiguration.Sex.Male);
        assertThat(baseWithWeight1).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.ELDERLY_MEN_BASE_KILO_CALORIES + SHNUserConfigurationCalculations.ELDERLY_MEN_WEIGHT_KG_KILO_CALORIES));

        Integer baseWithHeight1 = calculations.getBaseMetabolicRate(0d, 1, 70, SHNUserConfiguration.Sex.Male);
        assertThat(baseWithHeight1).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.ELDERLY_MEN_BASE_KILO_CALORIES + SHNUserConfigurationCalculations.ELDERLY_MEN_HEIGHT_M_KILO_CALORIES / 100d));

        Integer complex = calculations.getBaseMetabolicRate(2d, 2, 70, SHNUserConfiguration.Sex.Male);
        assertThat(complex).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.ELDERLY_MEN_BASE_KILO_CALORIES + 2 * SHNUserConfigurationCalculations.ELDERLY_MEN_HEIGHT_M_KILO_CALORIES / 100d + 2 * SHNUserConfigurationCalculations.ELDERLY_MEN_WEIGHT_KG_KILO_CALORIES));
    }

    @Test
    public void calculationForElderlyWoman() {
        Integer base = calculations.getBaseMetabolicRate(0d, 0, 70, SHNUserConfiguration.Sex.Female);
        assertThat(base).isEqualTo(SHNUserConfigurationCalculations.ELDERLY_WOMEN_BASE_KILO_CALORIES);

        Integer baseWithWeight1 = calculations.getBaseMetabolicRate(1d, 0, 70, SHNUserConfiguration.Sex.Female);
        assertThat(baseWithWeight1).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.ELDERLY_WOMEN_BASE_KILO_CALORIES + SHNUserConfigurationCalculations.ELDERLY_WOMEN_WEIGHT_KG_KILO_CALORIES));

        Integer baseWithHeight1 = calculations.getBaseMetabolicRate(0d, 1, 70, SHNUserConfiguration.Sex.Female);
        assertThat(baseWithHeight1).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.ELDERLY_WOMEN_BASE_KILO_CALORIES + SHNUserConfigurationCalculations.ELDERLY_WOMEN_HEIGHT_M_KILO_CALORIES / 100d));

        Integer complex = calculations.getBaseMetabolicRate(2d, 2, 70, SHNUserConfiguration.Sex.Female);
        assertThat(complex).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.ELDERLY_WOMEN_BASE_KILO_CALORIES + 2 * SHNUserConfigurationCalculations.ELDERLY_WOMEN_HEIGHT_M_KILO_CALORIES / 100d + 2 * SHNUserConfigurationCalculations.ELDERLY_WOMEN_WEIGHT_KG_KILO_CALORIES));
    }

    @Test
    public void calculationForChildren() {
        Integer base = calculations.getBaseMetabolicRate(0d, 0, 10, SHNUserConfiguration.Sex.Male);
        assertThat(base).isEqualTo(SHNUserConfigurationCalculations.CHILD_BASE_KILO_CALORIES);

        Integer baseWithWeight1 = calculations.getBaseMetabolicRate(1d, 0, 10, SHNUserConfiguration.Sex.Male);
        assertThat(baseWithWeight1).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.CHILD_BASE_KILO_CALORIES + SHNUserConfigurationCalculations.CHILD_WEIGHT_KG_KILO_CALORIES));

        Integer baseWithHeight1 = calculations.getBaseMetabolicRate(0d, 1, 10, SHNUserConfiguration.Sex.Male);
        assertThat(baseWithHeight1).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.CHILD_BASE_KILO_CALORIES + SHNUserConfigurationCalculations.CHILD_HEIGHT_M_KILO_CALORIES / 100d));

        Integer complex = calculations.getBaseMetabolicRate(2d, 2, 10, SHNUserConfiguration.Sex.Male);
        assertThat(complex).isEqualTo((int) Math.round(SHNUserConfigurationCalculations.CHILD_BASE_KILO_CALORIES + 2 * SHNUserConfigurationCalculations.CHILD_HEIGHT_M_KILO_CALORIES / 100d + 2 * SHNUserConfigurationCalculations.CHILD_WEIGHT_KG_KILO_CALORIES));
    }

    @Test
    public void calculationForMidWomanWithNullAge() {
        Integer base = calculations.getBaseMetabolicRate(0d, 0, null, SHNUserConfiguration.Sex.Female);
        assertThat(base).isEqualTo(SHNUserConfigurationCalculations.ADULT_WOMEN_BASE_KILO_CALORIES);
    }
}
