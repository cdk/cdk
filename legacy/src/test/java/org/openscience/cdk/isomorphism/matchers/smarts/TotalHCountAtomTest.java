package org.openscience.cdk.isomorphism.matchers.smarts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author John May
 * @cdk.module test-smarts
 */
class TotalHCountAtomTest {

    @Test
    void matches() throws Exception {
        TotalHCountAtom matcher = new TotalHCountAtom(4, mock(IChemObjectBuilder.class));
        IAtom atom = mock(IAtom.class);
        when(atom.getProperty(SMARTSAtomInvariants.KEY))
                .thenReturn(
                        new SMARTSAtomInvariants(mock(IAtomContainer.class), 0, 0, Collections.emptySet(), 0,
                                0, 0, 4));
        Assertions.assertTrue(matcher.matches(atom));
    }

    @Test
    void testToString() throws Exception {
        TotalHCountAtom total = new TotalHCountAtom(4, mock(IChemObjectBuilder.class));
        assertThat(total.toString(), is("H4"));
    }
}
