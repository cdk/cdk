/*
 * Copyright (c) 2026 John Mayfield
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */
package org.openscience.cdk.renderer.generators.standard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class StandardSgroupGeneratorTest {

    static void assertHiddenAtoms(int expected, IAtomContainer container) {
        int actual = 0;
        for (IAtom atom : container.atoms()) {
            if (atom.is(IChemObject.HIDDEN))
                actual++;
        }
        Assertions.assertEquals(expected, actual,
                                "Unexpected number of hidden atoms");
    }

    static void assertHiddenBonds(int expected, IAtomContainer container) {
        int actual = 0;
        for (IBond bond : container.bonds()) {
            if (bond.is(IChemObject.HIDDEN))
                actual++;
        }
        Assertions.assertEquals(expected, actual,
                                "Unexpected number of hidden bonds");
    }

    @Test
    public void testBasicAbbreviation() throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer container = smipar.parseSmiles("c1ccccc1OP(=O)(Oc1ccccc1)Oc1ccccc1");

        Map<IAtom,String> remap = new HashMap<>();
        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(0, remap.size());

        // Ph contraction the first 6 atoms and the 6th bond between C and O
        Sgroup sgroup = new Sgroup();
        for (int i=0; i<6; i++)
            sgroup.addAtom(container.getAtom(i));
        sgroup.setType(SgroupType.CtabAbbreviation);
        sgroup.addBond(container.getBond(6)); // C-O
        sgroup.setSubscript("Ph");
        container.setProperty(CDKConstants.CTAB_SGROUPS,
                              Collections.singletonList(sgroup));

        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(1, remap.size());
        Assertions.assertEquals("Ph", remap.get(container.getAtom(5)));
        assertHiddenAtoms(5, container);
        assertHiddenBonds(6, container);
    }

    @Test
    public void testBasicAbbreviationPartialHighlight() throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer container = smipar.parseSmiles("c1ccccc1OP(=O)(Oc1ccccc1)Oc1ccccc1");

        Map<IAtom,String> remap = new HashMap<>();
        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(0, remap.size());

        // Ph contraction the first 6 atoms and the 6th bond between C and O
        Sgroup sgroup = new Sgroup();
        for (int i=0; i<6; i++)
            sgroup.addAtom(container.getAtom(i));
        sgroup.setType(SgroupType.CtabAbbreviation);
        sgroup.addBond(container.getBond(6)); // C-O
        sgroup.setSubscript("Ph");
        container.setProperty(CDKConstants.CTAB_SGROUPS,
                              Collections.singletonList(sgroup));

        container.getAtom(0).setProperty(StandardGenerator.HIGHLIGHT_COLOR,
                                         Color.RED);

        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(0, remap.size());
        assertHiddenAtoms(0, container);
        assertHiddenBonds(0, container);
    }

    @Test
    public void testBasicAbbreviationFullHighlight() throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer container = smipar.parseSmiles("c1ccccc1OP(=O)(Oc1ccccc1)Oc1ccccc1");

        Map<IAtom,String> remap = new HashMap<>();
        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(0, remap.size());

        // Ph contraction the first 6 atoms and the 6th bond between C and O
        Sgroup sgroup = new Sgroup();
        for (int i=0; i<6; i++)
            sgroup.addAtom(container.getAtom(i));
        sgroup.setType(SgroupType.CtabAbbreviation);
        sgroup.addBond(container.getBond(6)); // C-O
        sgroup.setSubscript("Ph");
        container.setProperty(CDKConstants.CTAB_SGROUPS,
                              Collections.singletonList(sgroup));

        for (int i=0; i<6; i++) {
            container.getAtom(i).setProperty(StandardGenerator.HIGHLIGHT_COLOR,
                                             Color.RED);
            container.getBond(i).setProperty(StandardGenerator.HIGHLIGHT_COLOR,
                                             Color.RED);
        }

        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(1, remap.size());
        assertHiddenAtoms(5, container);
        assertHiddenBonds(6, container);
    }

    @Test
    public void testNestedAbbreviation() throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer container = smipar.parseSmiles("c1ccccc1OP(=O)(Oc1ccccc1)Oc1ccccc1");

        Map<IAtom,String> remap = new HashMap<>();
        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(0, remap.size());

        // Ph contraction the first 6 atoms and the 6th bond between C and O
        Sgroup sgroup1 = new Sgroup();
        for (int i=0; i<6; i++)
            sgroup1.addAtom(container.getAtom(i));
        sgroup1.setType(SgroupType.CtabAbbreviation);
        sgroup1.addBond(container.getBond(6)); // C-O
        sgroup1.setSubscript("Ph");

        Sgroup sgroup2 = new Sgroup();
        for (int i=0; i<7; i++)
            sgroup2.addAtom(container.getAtom(i));
        sgroup2.setType(SgroupType.CtabAbbreviation);
        sgroup2.addBond(container.getBond(7)); // O-P
        sgroup2.setSubscript("OPh");
        container.setProperty(CDKConstants.CTAB_SGROUPS,
                              Arrays.asList(sgroup1, sgroup2));

        remap = new HashMap<>();
        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(1, remap.size());
        Assertions.assertEquals("OPh", remap.get(container.getAtom(6)));
        assertHiddenAtoms(6, container);
        assertHiddenBonds(7, container);

        // order does not matter
        container.setProperty(CDKConstants.CTAB_SGROUPS,
                              Arrays.asList(sgroup2, sgroup1));
        remap = new HashMap<>();
        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(1, remap.size());
        Assertions.assertEquals("OPh", remap.get(container.getAtom(6)));
        assertHiddenAtoms(6, container);
        assertHiddenBonds(7, container);
    }


    @Test
    public void testNestedAbbreviationExpanded() throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer container = smipar.parseSmiles("c1ccccc1OP(=O)(Oc1ccccc1)Oc1ccccc1");

        Map<IAtom,String> remap = new HashMap<>();
        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(0, remap.size());

        // Ph contraction the first 6 atoms and the 6th bond between C and O
        Sgroup sgroup1 = new Sgroup();
        for (int i=0; i<6; i++)
            sgroup1.addAtom(container.getAtom(i));
        sgroup1.setType(SgroupType.CtabAbbreviation);
        sgroup1.addBond(container.getBond(6)); // C-O
        sgroup1.setSubscript("Ph");

        Sgroup sgroup2 = new Sgroup();
        for (int i=0; i<7; i++)
            sgroup2.addAtom(container.getAtom(i));
        sgroup2.setType(SgroupType.CtabAbbreviation);
        sgroup2.addBond(container.getBond(7)); // O-P
        sgroup2.setSubscript("OPh");
        container.setProperty(CDKConstants.CTAB_SGROUPS,
                              Arrays.asList(sgroup1, sgroup2));
        // expand!
        sgroup2.putValue(SgroupKey.CtabExpansion, true);

        remap = new HashMap<>();
        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(1, remap.size());
        Assertions.assertEquals("Ph", remap.get(container.getAtom(5)));
        assertHiddenAtoms(5, container);
        assertHiddenBonds(6, container);

        // order does not matter
        container.setProperty(CDKConstants.CTAB_SGROUPS,
                              Arrays.asList(sgroup2, sgroup1));
        remap = new HashMap<>();
        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(1, remap.size());
        Assertions.assertEquals("Ph", remap.get(container.getAtom(5)));
        assertHiddenAtoms(5, container);
        assertHiddenBonds(6, container);

        Assertions.assertFalse(StandardSgroupGenerator.needsAbbreviationLabel(container, sgroup2));
    }

    @Test
    public void testNestedAbbreviationExpandedReset() throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer container = smipar.parseSmiles("c1ccccc1OP(=O)(Oc1ccccc1)Oc1ccccc1");

        Map<IAtom,String> remap = new HashMap<>();
        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(0, remap.size());

        // Ph contraction the first 6 atoms and the 6th bond between C and O
        Sgroup sgroup1 = new Sgroup();
        for (int i=0; i<6; i++)
            sgroup1.addAtom(container.getAtom(i));
        sgroup1.setType(SgroupType.CtabAbbreviation);
        sgroup1.addBond(container.getBond(6)); // C-O
        sgroup1.setSubscript("Ph");

        Sgroup sgroup2 = new Sgroup();
        for (int i=0; i<7; i++)
            sgroup2.addAtom(container.getAtom(i));
        sgroup2.setType(SgroupType.CtabAbbreviation);
        sgroup2.addBond(container.getBond(7)); // O-P
        sgroup2.setSubscript("OPh");
        container.setProperty(CDKConstants.CTAB_SGROUPS,
                              Arrays.asList(sgroup1, sgroup2));
        // not expand!
        sgroup2.putValue(SgroupKey.CtabExpansion, false);
        remap = new HashMap<>();
        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(1, remap.size());
        Assertions.assertEquals("OPh", remap.get(container.getAtom(6)));
        assertHiddenAtoms(6, container);
        assertHiddenBonds(7, container);

        // expand 2!
        sgroup2.putValue(SgroupKey.CtabExpansion, true);
        remap = new HashMap<>();
        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(1, remap.size());
        Assertions.assertEquals("Ph", remap.get(container.getAtom(5)));
        assertHiddenAtoms(5, container);
        assertHiddenBonds(6, container);

        // expand 1!
        sgroup1.putValue(SgroupKey.CtabExpansion, true);
        remap = new HashMap<>();
        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(0, remap.size());
        assertHiddenAtoms(0, container);
        assertHiddenBonds(0, container);

        Assertions.assertFalse(StandardSgroupGenerator.needsAbbreviationLabel(container, sgroup1));
        Assertions.assertFalse(StandardSgroupGenerator.needsAbbreviationLabel(container, sgroup2));
    }

    /**
     * When fully abbreviated the generator returns a new element centered on
     * the atoms.
     */
    @Test
    public void testFullyAbbreviated() throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer container = smipar.parseSmiles("NN");

        Map<IAtom,String> remap = new HashMap<>();
        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(0, remap.size());

        Sgroup sgroup = new Sgroup();
        for (int i=0; i<2; i++)
            sgroup.addAtom(container.getAtom(i));
        sgroup.setType(SgroupType.CtabAbbreviation);
        sgroup.setSubscript("N2H2");
        container.setProperty(CDKConstants.CTAB_SGROUPS,
                              Collections.singletonList(sgroup));

        remap = new HashMap<>();
        StandardSgroupGenerator.prepareDisplayShortcuts(container, remap);
        Assertions.assertEquals(0, remap.size());
        assertHiddenAtoms(2, container);
        assertHiddenBonds(1, container);

        Assertions.assertTrue(StandardSgroupGenerator.needsAbbreviationLabel(container, sgroup));
    }
}