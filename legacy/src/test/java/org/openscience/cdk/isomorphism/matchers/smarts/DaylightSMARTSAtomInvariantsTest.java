package org.openscience.cdk.isomorphism.matchers.smarts;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Sets the computation of SMARTSAtomInvariants using the Daylight ring
 * values.
 *
 * @author John May
 * @cdk.module test-smarts
 */
public class DaylightSMARTSAtomInvariantsTest {

    @Test
    public void target() throws Exception {
        IAtomContainer container = sp.parseSmiles("CCC");
        SMARTSAtomInvariants.configureDaylightWithRingInfo(container);
        for (IAtom atom : container.atoms()) {
            assertThat(((SMARTSAtomInvariants) atom.getProperty(SMARTSAtomInvariants.KEY)).target(), is(container));
        }
    }

    @Test
    public void valence() throws Exception {
        assertThat(invariantOfFirstAtom("C").valence(), is(4));
        assertThat(invariantOfFirstAtom("N").valence(), is(3));
        assertThat(invariantOfFirstAtom("O").valence(), is(2));
        assertThat(invariantOfFirstAtom("P").valence(), is(3));
        assertThat(invariantOfFirstAtom("S").valence(), is(2));
        assertThat(invariantOfFirstAtom("[H]").valence(), is(0));
        assertThat(invariantOfFirstAtom("[NH5]").valence(), is(5));
        assertThat(invariantOfFirstAtom("N(=O)=C").valence(), is(5));
    }

    @Test
    public void connectivity() throws Exception {
        assertThat(invariantOfFirstAtom("C").connectivity(), is(4));
        assertThat(invariantOfFirstAtom("N").connectivity(), is(3));
        assertThat(invariantOfFirstAtom("O").connectivity(), is(2));
        assertThat(invariantOfFirstAtom("P").connectivity(), is(3));
        assertThat(invariantOfFirstAtom("S").connectivity(), is(2));
        assertThat(invariantOfFirstAtom("[H]").connectivity(), is(0));
        assertThat(invariantOfFirstAtom("[H][H]").connectivity(), is(1));
        assertThat(invariantOfFirstAtom("[NH5]").connectivity(), is(5));
        assertThat(invariantOfFirstAtom("N(=O)=C").connectivity(), is(3));
    }

    @Test
    public void degree() throws Exception {
        assertThat(invariantOfFirstAtom("C").degree(), is(0));
        assertThat(invariantOfFirstAtom("N").degree(), is(0));
        assertThat(invariantOfFirstAtom("O").degree(), is(0));
        assertThat(invariantOfFirstAtom("P").degree(), is(0));
        assertThat(invariantOfFirstAtom("S").degree(), is(0));
        assertThat(invariantOfFirstAtom("[H]").degree(), is(0));
        assertThat(invariantOfFirstAtom("[H][H]").degree(), is(1));
        assertThat(invariantOfFirstAtom("[NH5]").degree(), is(0));
        assertThat(invariantOfFirstAtom("N(=O)=C").degree(), is(2));
    }

    @Test
    public void totalHydrogenCount() throws Exception {
        assertThat(invariantOfFirstAtom("C").totalHydrogenCount(), is(4));
        assertThat(invariantOfFirstAtom("[CH4]").totalHydrogenCount(), is(4));
        assertThat(invariantOfFirstAtom("C[H]").totalHydrogenCount(), is(4));
        assertThat(invariantOfFirstAtom("[CH2][H]").totalHydrogenCount(), is(3));
        assertThat(invariantOfFirstAtom("[CH2]([H])[H]").totalHydrogenCount(), is(4));
    }

    @Test
    public void ringConnectivity() throws Exception {
        assertThat(invariantOfFirstAtom("C").ringConnectivity(), is(0));

        // 2,3,4 ring bonds
        assertThat(invariantOfFirstAtom("C1CCC1").ringConnectivity(), is(2));
        assertThat(invariantOfFirstAtom("C12CCC1CC2").ringConnectivity(), is(3));
        assertThat(invariantOfFirstAtom("C12(CCC2)CCC1").ringConnectivity(), is(4));

        // note 2 ring bonds but 3 ring atoms
        assertThat(invariantOfFirstAtom("C1(CCC1)C1CCC1").ringConnectivity(), is(2));
    }

    @Test
    public void ringNumber() throws Exception {
        assertThat(invariantOfFirstAtom("C").ringNumber(), is(0));

        assertThat(invariantOfFirstAtom("C1CCC1").ringNumber(), is(1));
        assertThat(invariantOfFirstAtom("C12CCC1CC2").ringNumber(), is(2));
        assertThat(invariantOfFirstAtom("C12(CCC2)CCC1").ringNumber(), is(2));
    }

    /**
     * Demonstates a problems with the SSSR but we match what Daylight depict
     * match says. We always have 4 atoms atoms in 3 rings but the other atoms
     * are either in 1 ring or 2 rings. Which atoms depends on the order of
     * atoms in the input.
     */
    @Test
    public void ringNumber_cyclophane() throws Exception {
        IAtomContainer container = sp.parseSmiles("C1CC23CCC11CCC4(CC1)CCC(CC2)(CC3)CC4");
        SMARTSAtomInvariants.configureDaylightWithRingInfo(container);
        int R1 = 0, R2 = 0, R3 = 0;
        for (IAtom atom : container.atoms()) {
            SMARTSAtomInvariants inv = atom.getProperty(SMARTSAtomInvariants.KEY);
            switch (inv.ringNumber()) {
                case 1:
                    R1++;
                    break;
                case 2:
                    R2++;
                    break;
                case 3:
                    R3++;
                    break;
            }
        }
        assertThat(R1, is(8));
        assertThat(R2, is(8));
        assertThat(R3, is(4));
    }

    @Test
    public void ringSize() throws Exception {
        assertTrue(invariantOfFirstAtom("C").ringSize().isEmpty());
        assertThat(invariantOfFirstAtom("C1CC1").ringSize(), hasItem(3));
        assertThat(invariantOfFirstAtom("C1CCC1").ringSize(), hasItem(4));
        assertThat(invariantOfFirstAtom("C1CCCC1").ringSize(), hasItem(5));
    }

    /**
     * Shows that the store ring sizes are only the smallest. There is one ring
     * of size six and one ring of size 5. When we count the ring sizes (can be
     * verities on depict match) there are only 4 atoms in a 6 member ring. This
     * is because 2 atoms are shared with the smalled 5 member ring.
     *
     * @throws Exception
     */
    @Test
    public void ringSize_imidazole() throws Exception {

        IAtomContainer container = sp.parseSmiles("N1C=NC2=CC=CC=C12");
        SMARTSAtomInvariants.configureDaylightWithRingInfo(container);
        int ringSize5 = 0, ringSize6 = 0;
        for (IAtom atom : container.atoms()) {
            SMARTSAtomInvariants inv = atom.getProperty(SMARTSAtomInvariants.KEY);
            if (inv.ringSize().contains(5)) ringSize5++;
            if (inv.ringSize().contains(6)) ringSize6++;
        }

        assertThat(ringSize5, is(5));
        assertThat(ringSize6, is(4));
    }

    /**
     * Shows that the exterior ring of the SSSR (size 12) is not
     * @throws Exception
     */
    @Test
    public void ringSize_cyclophane() throws Exception {

        IAtomContainer container = sp.parseSmiles("C1CC23CCC11CCC4(CC1)CCC(CC2)(CC3)CC4");
        SMARTSAtomInvariants.configureDaylightWithRingInfo(container);
        int ringSize5 = 0, ringSize6 = 0;
        for (IAtom atom : container.atoms()) {
            SMARTSAtomInvariants inv = atom.getProperty(SMARTSAtomInvariants.KEY);
            assertThat(inv.ringSize(), hasItem(6));
            assertThat(inv.ringSize(), not(hasItem(12)));
        }
    }

    @Test
    public void noRingInfo() throws Exception {
        IAtomContainer container = sp.parseSmiles("C1CC23CCC11CCC4(CC1)CCC(CC2)(CC3)CC4");
        SMARTSAtomInvariants.configureDaylightWithoutRingInfo(container);
        for (IAtom atom : container.atoms()) {
            SMARTSAtomInvariants inv = atom.getProperty(SMARTSAtomInvariants.KEY);
            assertTrue(inv.ringSize().isEmpty());
            assertThat(inv.ringNumber(), is(0));
        }
    }

    static final SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());

    // compute the invariants for the first atom in a SMILES string
    static SMARTSAtomInvariants invariantOfFirstAtom(String smiles) throws Exception {
        IAtomContainer container = sp.parseSmiles(smiles);
        SMARTSAtomInvariants.configureDaylightWithRingInfo(container);
        return container.getAtom(0).getProperty(SMARTSAtomInvariants.KEY);
    }

}
