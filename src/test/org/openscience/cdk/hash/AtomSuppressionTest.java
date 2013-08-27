package org.openscience.cdk.hash;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class AtomSuppressionTest {

    @Test public void unsuppressed() throws Exception {
        AtomSuppression suppression = AtomSuppression.unsuppressed();
        IAtomContainer container = mock(IAtomContainer.class);
        Suppressed suppressed = suppression.suppress(container);
        assertFalse(suppressed.contains(0));
        assertFalse(suppressed.contains(1));
        assertFalse(suppressed.contains(2));
        assertFalse(suppressed.contains(3));
        assertFalse(suppressed.contains(4));
    }

    @Test public void anyHydrogens() throws Exception {
        AtomSuppression suppression = AtomSuppression.anyHydrogens();
        IAtomContainer container = mock(IAtomContainer.class);
        when(container.getAtomCount()).thenReturn(5);

        IAtom carbon = mock(IAtom.class);
        IAtom hydrogen = mock(IAtom.class);

        when(carbon.getSymbol()).thenReturn("C");
        when(hydrogen.getSymbol()).thenReturn("H");

        when(container.getAtom(0)).thenReturn(carbon);
        when(container.getAtom(1)).thenReturn(hydrogen);
        when(container.getAtom(2)).thenReturn(carbon);
        when(container.getAtom(3)).thenReturn(carbon);
        when(container.getAtom(4)).thenReturn(hydrogen);

        Suppressed suppressed = suppression.suppress(container);
        assertFalse(suppressed.contains(0));
        assertTrue(suppressed.contains(1));
        assertFalse(suppressed.contains(2));
        assertFalse(suppressed.contains(3));
        assertTrue(suppressed.contains(4));
    }

    @Test public void anyPseudos() throws Exception {
        AtomSuppression suppression = AtomSuppression.anyPseudos();
        IAtomContainer container = mock(IAtomContainer.class);
        when(container.getAtomCount()).thenReturn(5);

        IAtom carbon = mock(IAtom.class);
        IAtom pseudo = mock(IPseudoAtom.class);

        when(container.getAtom(0)).thenReturn(carbon);
        when(container.getAtom(1)).thenReturn(pseudo);
        when(container.getAtom(2)).thenReturn(carbon);
        when(container.getAtom(3)).thenReturn(carbon);
        when(container.getAtom(4)).thenReturn(pseudo);

        Suppressed suppressed = suppression.suppress(container);
        assertFalse(suppressed.contains(0));
        assertTrue(suppressed.contains(1));
        assertFalse(suppressed.contains(2));
        assertFalse(suppressed.contains(3));
        assertTrue(suppressed.contains(4));
    }
}
