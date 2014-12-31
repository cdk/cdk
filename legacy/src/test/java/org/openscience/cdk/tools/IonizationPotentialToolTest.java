/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.tools;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TestSuite that runs all tests.
 *
 * @cdk.module test-ionpot
 */
public class IonizationPotentialToolTest extends CDKTestCase {

    LonePairElectronChecker lpcheck = new LonePairElectronChecker();

    /**
     * Constructor of the IonizationPotentialToolTest.
     */
    public IonizationPotentialToolTest() {
        super();
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return The test suite
     */
    @Test
    public void testIonizationPotentialTool() {

        Assert.assertNotNull(new IonizationPotentialTool());
    }

    @Test
    public void testBenzene() throws Exception {
        String smiles = "c1ccccc1";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer molecule = sp.parseSmiles(smiles);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        addExplicitHydrogens(molecule);
        Aromaticity.cdkLegacy().apply(molecule);
        lpcheck.saturate(molecule);

        List<Double> carbonIPs = new ArrayList<Double>();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            if (atom.getSymbol().equals("H")) continue;
            carbonIPs.add(IonizationPotentialTool.predictIP(molecule, atom));
        }

        double firstIP = carbonIPs.get(0);
        for (double ip : carbonIPs) {
            Assert.assertEquals(firstIP, ip, 0.0001);
        }
    }

}
