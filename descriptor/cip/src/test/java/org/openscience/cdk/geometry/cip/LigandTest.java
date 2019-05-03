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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @cdk.module test-cip
 */
public class LigandTest extends CDKTestCase {

    @Test
    public void testConstructorAndGetMethods() throws Exception {
        SmilesParser smiles = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer molecule = smiles.parseSmiles("ClC(Br)(I)[H]");

        ILigand ligand = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(0));
        Assert.assertNotNull(ligand);
        Assert.assertEquals(molecule, ligand.getAtomContainer());
        Assert.assertEquals(molecule.getAtom(1), ligand.getCentralAtom());
        Assert.assertEquals(molecule.getAtom(0), ligand.getLigandAtom());
    }

    @Test
    public void testVisitedTracking() throws Exception {
        SmilesParser smiles = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer molecule = smiles.parseSmiles("ClC(Br)(I)[H]");

        ILigand ligand = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(0));
        Assert.assertTrue(ligand.getVisitedAtoms().isVisited(molecule.getAtom(1)));
        Assert.assertTrue(ligand.isVisited(molecule.getAtom(1)));
        Assert.assertFalse(ligand.getVisitedAtoms().isVisited(molecule.getAtom(0)));
        Assert.assertFalse(ligand.isVisited(molecule.getAtom(0)));
    }
}
