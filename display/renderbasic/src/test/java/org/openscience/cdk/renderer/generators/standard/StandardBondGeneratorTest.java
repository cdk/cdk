/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.renderer.generators.standard;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;

import javax.vecmath.Point2d;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openscience.cdk.renderer.generators.standard.StandardBondGenerator.RingBondOffsetComparator;

public class StandardBondGeneratorTest {

    @Test
    public void adenineRingPreference() throws Exception {

        IAtomContainer adenine = TestMoleculeFactory.makeAdenine();
        Map<IBond, IAtomContainer> ringMap = StandardBondGenerator.ringPreferenceMap(adenine);

        int nSize5 = 0, nSize6 = 0;
        for (IBond bond : adenine.bonds()) {
            IAtomContainer ring = ringMap.get(bond);
            // exocyclic bond
            if (ring == null) continue;
            int size = ring.getAtomCount();
            if (size == 5) nSize5++;
            if (size == 6) nSize6++;
        }

        // 6 bonds should point to the six member ring
        // 4 bonds should point to the five member ring
        assertThat(nSize5, is(4));
        assertThat(nSize6, is(6));
    }

    @Test
    public void metalRingPreference() throws Exception {

        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("C1[Fe]C=CC2=C1C=CN2");
        for (IAtom atom : mol.atoms())
            atom.setPoint2d(new Point2d(0,0));
        Map<IBond, IAtomContainer> ringMap = StandardBondGenerator.ringPreferenceMap(mol);

        int nSize5 = 0, nSize6 = 0;
        for (IBond bond : mol.bonds()) {
            IAtomContainer ring = ringMap.get(bond);
            // exocyclic bond
            if (ring == null) continue;
            int size = ring.getAtomCount();
            if (size == 5) nSize5++;
            if (size == 6) nSize6++;
        }

        // 5 bonds should point to the six member ring
        // 5 bonds should point to the five member ring
        assertThat(nSize5, is(5));
        assertThat(nSize6, is(5));
    }

    @Test
    public void ringSizePriority() {
        assertThat(RingBondOffsetComparator.sizePreference(6), is(0));
        assertThat(RingBondOffsetComparator.sizePreference(5), is(1));
        assertThat(RingBondOffsetComparator.sizePreference(7), is(2));
        assertThat(RingBondOffsetComparator.sizePreference(4), is(3));
        assertThat(RingBondOffsetComparator.sizePreference(3), is(4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidRingSize() {
        RingBondOffsetComparator.sizePreference(2);
    }

    @Test
    public void macroCycle() {
        assertThat(RingBondOffsetComparator.sizePreference(8), is(8));
        assertThat(RingBondOffsetComparator.sizePreference(10), is(10));
        assertThat(RingBondOffsetComparator.sizePreference(20), is(20));
    }

    @Test
    public void benzeneDoubleBondCount() {
        assertThat(RingBondOffsetComparator.nDoubleBonds(TestMoleculeFactory.makeBenzene()), is(3));
    }

    @Test
    public void benzeneElementCount() {
        int[] freq = RingBondOffsetComparator.countLightElements(TestMoleculeFactory.makeBenzene());
        assertThat(freq[6], is(6));
    }


    @Test
    public void highAtomicNoElementCount() {
        IAtomContainer container = TestMoleculeFactory.makeBenzene();
        container.getAtom(0).setAtomicNumber(34);
        container.getAtom(0).setSymbol("Se");
        int[] freq = RingBondOffsetComparator.countLightElements(container);
        assertThat(freq[6], is(5));
    }

    @Test
    public void adenineElementCount() {
        int[] freq = RingBondOffsetComparator.countLightElements(TestMoleculeFactory.makeAdenine());
        assertThat(freq[6], is(5));
        assertThat(freq[7], is(5));
    }

    @Test
    public void benzeneComparedToPyrrole() {
        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        IAtomContainer pyrrole = TestMoleculeFactory.makePyrrole();

        assertThat(new RingBondOffsetComparator().compare(benzene, pyrrole), is(-1));
        assertThat(new RingBondOffsetComparator().compare(pyrrole, benzene), is(+1));
    }

    @Test
    public void benzeneComparedToCycloHexane() {
        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        IAtomContainer cyclohexane = TestMoleculeFactory.makeCyclohexane();

        assertThat(new RingBondOffsetComparator().compare(benzene, cyclohexane), is(-1));
        assertThat(new RingBondOffsetComparator().compare(cyclohexane, benzene), is(+1));
    }

    @Test
    public void benzeneComparedToCycloHexene() {
        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        IAtomContainer cyclohexene = TestMoleculeFactory.makeCyclohexene();

        assertThat(new RingBondOffsetComparator().compare(benzene, cyclohexene), is(-1));
        assertThat(new RingBondOffsetComparator().compare(cyclohexene, benzene), is(+1));
    }

    @Test
    public void benzeneComparedToBenzene() {
        IAtomContainer benzene1 = TestMoleculeFactory.makeBenzene();
        IAtomContainer benzene2 = TestMoleculeFactory.makeBenzene();

        assertThat(new RingBondOffsetComparator().compare(benzene1, benzene2), is(0));
        assertThat(new RingBondOffsetComparator().compare(benzene2, benzene1), is(0));
    }

    @Test
    public void benzeneComparedToPyridine() {
        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        IAtomContainer pyridine = TestMoleculeFactory.makePyridine();

        assertThat(new RingBondOffsetComparator().compare(benzene, pyridine), is(-1));
        assertThat(new RingBondOffsetComparator().compare(pyridine, benzene), is(+1));
    }

    @Test
    public void furaneComparedToPyrrole() {
        IAtomContainer furane = TestMoleculeFactory.makePyrrole();
        IAtomContainer pyrrole = TestMoleculeFactory.makePyrrole();

        assert furane.getAtom(1).getAtomicNumber() == 7;
        furane.getAtom(1).setAtomicNumber(8);
        furane.getAtom(1).setSymbol("O");

        assertThat(new RingBondOffsetComparator().compare(pyrrole, furane), is(-1));
        assertThat(new RingBondOffsetComparator().compare(furane, pyrrole), is(+1));
    }

    @Test
    public void furaneComparedToThiophene() {
        IAtomContainer furane = TestMoleculeFactory.makePyrrole();
        IAtomContainer thiophene = TestMoleculeFactory.makePyrrole();

        assert furane.getAtom(1).getAtomicNumber() == 7;
        assert thiophene.getAtom(1).getAtomicNumber() == 7;
        furane.getAtom(1).setAtomicNumber(8);
        furane.getAtom(1).setSymbol("O");
        thiophene.getAtom(1).setAtomicNumber(16);
        thiophene.getAtom(1).setSymbol("S");

        assertThat(new RingBondOffsetComparator().compare(furane, thiophene), is(-1));
        assertThat(new RingBondOffsetComparator().compare(thiophene, furane), is(+1));
    }

}
