/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
 *
 */
package org.openscience.cdk.structgen;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;

/**
 * @cdk.module test-structgen
 */
public class VicinitySamplerTest extends CDKTestCase {

    private static SmilesParser parser;

    @BeforeClass
    public static void setUp() {
        parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
    }

    @Test
    public void testVicinitySampler_sample() throws Exception {
        IAtomContainer mol = TestMoleculeFactory.makeEthylPropylPhenantren();

        Isotopes.getInstance().configureAtoms(mol);
        addImplicitHydrogens(mol);

        IAtomContainer temp = null;
        List structures = VicinitySampler.sample(mol);
        Assert.assertEquals(37, structures.size());
        for (int f = 0; f < structures.size(); f++) {
            temp = (IAtomContainer) structures.get(f);
            Assert.assertNotNull(temp);
            Assert.assertTrue(ConnectivityChecker.isConnected(temp));
            Assert.assertEquals(mol.getAtomCount(), temp.getAtomCount());
        }

    }

    /**
     * @cdk.bug 1632610
     */
    public void testCycloButene() throws Exception {
        IAtomContainer mol = parser.parseSmiles("C=CC=C");

        Isotopes.getInstance().configureAtoms(mol);
        addImplicitHydrogens(mol);

        IAtomContainer temp = null;
        List structures = VicinitySampler.sample(mol);
        Assert.assertEquals(1, structures.size());
        for (int f = 0; f < structures.size(); f++) {
            temp = (IAtomContainer) structures.get(f);
            Assert.assertNotNull(temp);
            Assert.assertTrue(ConnectivityChecker.isConnected(temp));
            Assert.assertEquals(mol.getAtomCount(), temp.getAtomCount());
        }

    }
}
