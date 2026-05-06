/*
 * Copyright (c) 2026 John Mayfield
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */
package org.openscience.cdk.isomorphism.matchers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RGroupQueryManipulatorTest {

    private static void assertEnumerated(IRGroupQuery rgroupQuery, String ... expected) throws Exception {
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Default + SmiFlavor.UseAromaticSymbols);
        Set<String> expectedSet = new HashSet<>();
        Collections.addAll(expectedSet, expected);
        Set<String> actual = new HashSet<>();
        for (IAtomContainer explicitMol : rgroupQuery.getAllConfigurations()) {
            actual.add(smigen.create(explicitMol));
        }

        Assertions.assertEquals(expectedSet, actual);
    }

    private static RGroup rGroupFromSmiles(String smi) throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        RGroup rgp = new RGroup();
        rgp.setGroup(smipar.parseSmiles(smi));
        return rgp;
    }

    @Test
    public void testToAtomContainer() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);

        RGroupQuery query = new RGroupQuery(bldr);
        IAtomContainer root = smipar.parseSmiles("*c1ncccc1 |$R1$|");
        Map<Integer,IRGroupList> defs = new HashMap<>();

        RGroupList rGroupList = new RGroupList(1);
        List<IRGroup> rgroups = new ArrayList<>();
        rgroups.add(rGroupFromSmiles("*Cl |$_AP1$|"));
        rgroups.add(rGroupFromSmiles("*Br |$_AP1$|"));
        rgroups.add(rGroupFromSmiles("*OC |$_AP1$|"));
        rgroups.add(rGroupFromSmiles("*C(=O)C |$_AP1$|"));
        rGroupList.setRGroups(rgroups);
        defs.put(1, rGroupList);

        query.setRootStructure(root);
        query.setRGroupDefinitions(defs);

        Map<IAtom,Map<Integer, IBond>> rootAttach = new HashMap<>();
        rootAttach.put(root.getAtom(0),
                       Collections.singletonMap(1, root.getAtom(0).bonds().iterator().next()));
        query.setRootAttachmentPoints(rootAttach);

        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Default + SmiFlavor.UseAromaticSymbols);
        Assertions.assertEquals("*c1ncccc1.*Cl.*Br.*OC.*C(=O)C |$R1;;;;;;;_AP1;;_AP1;;_AP1;;;_AP1$|",
                                smigen.create(RGroupQueryManipulator.toAtomContainer(query)));

        assertEnumerated(query, "c1(Br)ncccc1", "c1(Cl)ncccc1", "c1(OC)ncccc1", "c1(C(=O)C)ncccc1");
        // round tripped
        assertEnumerated(RGroupQueryManipulator.toRgroupQuery(RGroupQueryManipulator.toAtomContainer(query)),
                         "c1(Br)ncccc1", "c1(Cl)ncccc1", "c1(OC)ncccc1", "c1(C(=O)C)ncccc1");
    }

    @Test
    public void testToRgroupQuery() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);

        IAtomContainer mol = smipar.parseSmiles("*c1ncccc1 |$R1$|");

        IAtomContainer def;
        def = smipar.parseSmiles("*Cl |$_AP1$|");
        RGroupQueryManipulator.setRgroupLabel(def, "R1");
        mol.add(def);
        def = smipar.parseSmiles("*Br |$_AP1$|");
        RGroupQueryManipulator.setRgroupLabel(def, "R1");
        mol.add(def);
        def = smipar.parseSmiles("*OC |$_AP1$|");
        RGroupQueryManipulator.setRgroupLabel(def, "R1");
        mol.add(def);
        def = smipar.parseSmiles("*C(=O)C |$_AP1$|");
        RGroupQueryManipulator.setRgroupLabel(def, "R1");
        mol.add(def);

        IRGroupQuery rgroupQuery = RGroupQueryManipulator.toRgroupQuery(mol);

        assertEnumerated(rgroupQuery, "c1(Br)ncccc1", "c1(Cl)ncccc1", "c1(OC)ncccc1", "c1(C(=O)C)ncccc1");
        // round tripping
        assertEnumerated(RGroupQueryManipulator.toRgroupQuery(RGroupQueryManipulator.toAtomContainer(rgroupQuery)),
                         "c1(Br)ncccc1", "c1(Cl)ncccc1", "c1(OC)ncccc1", "c1(C(=O)C)ncccc1");

//        Assertions.assertEquals("*c1ncccc1.*Cl.*Br.*OC.*C(=O)C |$R1;;;;;;;_AP1;;_AP1;;_AP1;;;_AP1$|",
//                                smigen.create(rgroupQuery));
    }

    // when an Rgroup has multiple attachments the ordering of the bonds is
    // important, in CXSMILES this is specified by ligand order (LO)
    @Test
    public void testToRgroupQueryLigandOrdering() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);

        IAtomContainer mol = smipar.parseSmiles("N*C(=O)O |$;R1$,LO:1:0.2|");

        IAtomContainer def;
        def = smipar.parseSmiles("*CCO* |$_AP1;;;;_AP2$|");
        RGroupQueryManipulator.setRgroupLabel(def, "R1");
        mol.add(def);
        def = smipar.parseSmiles("*CCN* |$_AP1;;;;_AP2$|");
        RGroupQueryManipulator.setRgroupLabel(def, "R1");
        mol.add(def);

        IRGroupQuery rgroupQuery = RGroupQueryManipulator.toRgroupQuery(mol);

        assertEnumerated(rgroupQuery, "NCCOC(=O)O", "NCCNC(=O)O");
        // round tripping
        assertEnumerated(RGroupQueryManipulator.toRgroupQuery(RGroupQueryManipulator.toAtomContainer(rgroupQuery)),
                         "NCCOC(=O)O", "NCCNC(=O)O");

//        Assertions.assertEquals("*c1ncccc1.*Cl.*Br.*OC.*C(=O)C |$R1;;;;;;;_AP1;;_AP1;;_AP1;;;_AP1$|",
//                                smigen.create(rgroupQuery));
    }

    // when an Rgroup has multiple attachments the ordering of the bonds is
    // important, in CXSMILES this is specified by ligand order (LO)
    @Test
    public void testToRgroupQueryLigandOrdering2() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);

        IAtomContainer mol = smipar.parseSmiles("N*C(=O)O |$;R1$,LO:1:2.0|");

        IAtomContainer def;
        def = smipar.parseSmiles("*CCO* |$_AP1;;;;_AP2$|");
        RGroupQueryManipulator.setRgroupLabel(def, "R1");
        mol.add(def);
        def = smipar.parseSmiles("*CCN* |$_AP1;;;;_AP2$|");
        RGroupQueryManipulator.setRgroupLabel(def, "R1");
        mol.add(def);

        IRGroupQuery rgroupQuery = RGroupQueryManipulator.toRgroupQuery(mol);

        assertEnumerated(rgroupQuery, "NOCCC(=O)O", "NNCCC(=O)O");

        // round tripping
        assertEnumerated(roundTrip(rgroupQuery),
                         "NOCCC(=O)O", "NNCCC(=O)O");

//        Assertions.assertEquals("*c1ncccc1.*Cl.*Br.*OC.*C(=O)C |$R1;;;;;;;_AP1;;_AP1;;_AP1;;;_AP1$|",
//                                smigen.create(rgroupQuery));
    }

    private static IRGroupQuery roundTrip(IRGroupQuery rgroupQuery) throws CDKException {
        return RGroupQueryManipulator.toRgroupQuery(RGroupQueryManipulator.toAtomContainer(rgroupQuery));
    }
}
