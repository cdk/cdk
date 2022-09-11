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
package org.openscience.cdk.geometry.cip;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @cdk.module test-cip
 */
class ImplicitHydrogenLigandTest extends CDKTestCase {

    @Test
    void testConstructorAndGetMethods() throws Exception {
        SmilesParser smiles = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer molecule = smiles.parseSmiles("ClC(Br)(I)");

        ILigand ligand = new ImplicitHydrogenLigand(molecule, new VisitedAtoms(), molecule.getAtom(1));
        Assertions.assertNotNull(ligand);
        Assertions.assertEquals(molecule, ligand.getAtomContainer());
        Assertions.assertEquals(molecule.getAtom(1), ligand.getCentralAtom());
        Assertions.assertTrue(ligand.getLigandAtom() instanceof ImmutableHydrogen);
    }
}
