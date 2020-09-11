/*
 * Copyright (C) 2010 Rajarshi Guha <rajarshi.guha@gmail.com>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.fragment;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test fragment utils
 *
 * @cdk.module test-fragment
 */
public class FragmentUtilsTest extends CDKTestCase {

    static SmilesParser smilesParser;

    @BeforeClass
    public static void setup() {
        smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    }

    @Test
    public void testSplit() throws CDKException {
        IAtomContainer mol = smilesParser.parseSmiles("C1CC1C2CCC2");
        SpanningTree st = new SpanningTree(mol);
        IRingSet rings = st.getAllRings();
        IBond splitBond = null;
        for (int i = 0; i < mol.getBondCount(); i++) {
            if (rings.getRings(mol.getBond(i)).getAtomContainerCount() == 0) {
                splitBond = mol.getBond(i);
                break;
            }
        }
        List<IAtomContainer> frags = FragmentUtils.splitMolecule(mol, splitBond);
        SmilesGenerator sg = new SmilesGenerator();
        Set<String> uniqueFrags = new HashSet<String>();
        for (IAtomContainer frag : frags) {
            uniqueFrags.add(sg.create(frag));
        }
        Assert.assertEquals(2, uniqueFrags.size());
        // You can put the fragments back together with a ring closure and dot
        // [CH]12CC1.[CH]12CCC1
        org.hamcrest.MatcherAssert.assertThat(uniqueFrags, hasItems("[CH]1CC1", "[CH]1CCC1"));
    }

    @Test
    public void testMakeAtomContainer() {

        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

        IAtom atom = builder.newInstance(IAtom.class, "C");
        IAtom exclude = builder.newInstance(IAtom.class, "C");

        IAtom a1 = builder.newInstance(IAtom.class, "C");
        IAtom a2 = builder.newInstance(IAtom.class, "C");

        IBond[] bonds = new IBond[]{builder.newInstance(IBond.class, atom, exclude),
                builder.newInstance(IBond.class, a1, a2), builder.newInstance(IBond.class, a1, atom),
                builder.newInstance(IBond.class, a2, exclude)};

        IAtomContainer part = FragmentUtils.makeAtomContainer(atom, Arrays.asList(bonds), exclude);

        assertThat(part.getAtomCount(), is(3));
        assertThat(part.getBondCount(), is(2));

        Assert.assertTrue(part.contains(atom));
        Assert.assertTrue(part.contains(a1));
        Assert.assertTrue(part.contains(a2));
        Assert.assertFalse(part.contains(exclude));

        Assert.assertTrue(part.contains(bonds[1]));
        Assert.assertTrue(part.contains(bonds[2]));
    }

    @Test
    public void testTraversal_Chain() {

        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

        IAtom[] atoms = new IAtom[]{builder.newInstance(IAtom.class, "C"), builder.newInstance(IAtom.class, "C"),
                builder.newInstance(IAtom.class, "C"), builder.newInstance(IAtom.class, "C"),
                builder.newInstance(IAtom.class, "C"), builder.newInstance(IAtom.class, "C")};
        IBond[] bonds = new IBond[]{builder.newInstance(IBond.class, atoms[0], atoms[1]),
                builder.newInstance(IBond.class, atoms[1], atoms[2]),
                builder.newInstance(IBond.class, atoms[2], atoms[3]),
                builder.newInstance(IBond.class, atoms[3], atoms[4]),
                builder.newInstance(IBond.class, atoms[4], atoms[5])};

        IAtomContainer m = builder.newInstance(IAtomContainer.class, 0, 0, 0, 0);
        m.setAtoms(atoms);
        m.setBonds(bonds);

        List<IBond> accumulator = new ArrayList<IBond>();

        // traverse from one end
        FragmentUtils.traverse(m, atoms[0], accumulator);

        assertThat(accumulator.size(), is(5));
        assertThat(accumulator.get(0), is(bonds[0]));
        assertThat(accumulator.get(1), is(bonds[1]));
        assertThat(accumulator.get(2), is(bonds[2]));
        assertThat(accumulator.get(3), is(bonds[3]));
        assertThat(accumulator.get(4), is(bonds[4]));

        // traverse from the middle
        accumulator.clear();
        FragmentUtils.traverse(m, atoms[3], accumulator);

        assertThat(accumulator.size(), is(5));

        assertThat(accumulator.get(0), is(bonds[2]));
        assertThat(accumulator.get(1), is(bonds[1]));
        assertThat(accumulator.get(2), is(bonds[0]));
        assertThat(accumulator.get(3), is(bonds[3]));
        assertThat(accumulator.get(4), is(bonds[4]));
    }
}
