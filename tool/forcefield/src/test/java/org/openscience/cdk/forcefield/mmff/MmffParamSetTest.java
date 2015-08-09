package org.openscience.cdk.forcefield.mmff;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author John May
 */
public class MmffParamSetTest {

    public static final MmffParamSet mmffParams = MmffParamSet.INSTANCE;

    @Test public void formalCharge() {
        assertThat(mmffParams.getFormalCharge("NCN+"),
                   is(new BigDecimal("0.500")));
    }

    @Test public void formalChargeAdjustment() {
        assertThat(mmffParams.getFormalChargeAdjustment(32),
                   is(new BigDecimal("0.500")));
    }

    @Test public void crd() {
        assertThat(mmffParams.getCrd(32), is(1));
    }
    
    @Test public void bciBetween1And18() {
        assertThat(mmffParams.getBondChargeIncrement(0, 1, 18),
                   is(new BigDecimal("-0.1052")));
    }

    @Test public void bciBetween18And1() {
        assertThat(mmffParams.getBondChargeIncrement(0, 18, 1),
                   is(new BigDecimal("0.1052")));
    }
    
    @Test public void bciBetween37And63WithBondClass() {
        assertThat(mmffParams.getBondChargeIncrement(0, 37, 63),
                   is(new BigDecimal("0.0000")));
        assertThat(mmffParams.getBondChargeIncrement(1, 37, 63),
                   is(new BigDecimal("-0.0530")));
    }         
}