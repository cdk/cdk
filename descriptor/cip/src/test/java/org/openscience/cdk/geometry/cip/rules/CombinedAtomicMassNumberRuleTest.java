/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.geometry.cip.rules;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.geometry.cip.ILigand;
import org.openscience.cdk.geometry.cip.Ligand;
import org.openscience.cdk.geometry.cip.VisitedAtoms;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @cdk.module test-cip
 */
class CombinedAtomicMassNumberRuleTest extends CDKTestCase {

    private static final SmilesParser   smiles = new SmilesParser(SilentChemObjectBuilder.getInstance());
    private static IAtomContainer molecule;

    @BeforeAll
    static void setup() throws Exception {
        molecule = smiles.parseSmiles("CC(Br)([13C])[H]");
    }

    @Test
    void testCompare_Identity() {
        ILigand ligand = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(0));
        ISequenceSubRule<ILigand> rule = new CombinedAtomicMassNumberRule();
        Assertions.assertEquals(0, rule.compare(ligand, ligand));
    }

    @Test
    void testCompare() {
        ILigand ligand1 = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(0));
        ILigand ligand2 = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(2));
        ISequenceSubRule<ILigand> rule = new CombinedAtomicMassNumberRule();
        Assertions.assertEquals(-1, rule.compare(ligand1, ligand2));
        Assertions.assertEquals(1, rule.compare(ligand2, ligand1));
    }

    @Test
    void testOrder() {
        ILigand ligand1 = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(4));
        ILigand ligand2 = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(3));
        ILigand ligand3 = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(2));
        ILigand ligand4 = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(0));
        List<ILigand> ligands = new ArrayList<>();
        ligands.add(ligand1);
        ligands.add(ligand2);
        ligands.add(ligand3);
        ligands.add(ligand4);

        ligands.sort(new CombinedAtomicMassNumberRule());
        Assertions.assertEquals("H", ligands.get(0).getLigandAtom().getSymbol());
        Assertions.assertEquals("C", ligands.get(1).getLigandAtom().getSymbol());
        Assertions.assertEquals("C", ligands.get(2).getLigandAtom().getSymbol());
        Assertions.assertEquals(13, ligands.get(2).getLigandAtom().getMassNumber().intValue());
        Assertions.assertEquals("Br", ligands.get(3).getLigandAtom().getSymbol());
    }

}
