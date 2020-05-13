package org.openscience.cdk.isomorphism.matchers.smarts;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author John May
 * @cdk.module test-smarts
 */
public class TotalHCountAtomTest {

    @Test
    public void matches() throws Exception {
        TotalHCountAtom matcher = new TotalHCountAtom(4, mock(IChemObjectBuilder.class));
        IAtom atom = mock(IAtom.class);
        when(atom.getProperty(SMARTSAtomInvariants.KEY))
                .thenReturn(
                        new SMARTSAtomInvariants(mock(IAtomContainer.class), 0, 0, Collections.<Integer> emptySet(), 0,
                                0, 0, 4));
        assertTrue(matcher.matches(atom));
    }

    @Test
    public void testToString() throws Exception {
        TotalHCountAtom total = new TotalHCountAtom(4, mock(IChemObjectBuilder.class));
        assertThat(total.toString(), is("H4"));
    }
}
