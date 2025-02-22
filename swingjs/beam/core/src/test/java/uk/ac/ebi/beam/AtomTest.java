package uk.ac.ebi.beam;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/** @author John May */
public class AtomTest {

    @Test public void aliphaticSubsetFromElement() {
        for (Atom a : AtomImpl.AliphaticSubset.values()) {
            assertThat(AtomImpl.AliphaticSubset.ofElement(a.element()), is(a));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void aliphaticSubsetInvalidElement() {
        AtomImpl.AliphaticSubset.ofElement(Element.Californium);
    }

    @Test public void aromaticSubsetFromElement() {
        for (Atom a : AtomImpl.AromaticSubset.values()) {
            assertThat(AtomImpl.AromaticSubset.ofElement(a.element()), is(a));
        }
    }
}
