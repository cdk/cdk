/* Copyright (C) 2009  Stefan Kuhn <shk3@users.sf.net>
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
package org.openscience.cdk.structgen.stochastic.operator;

import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.io.SMILESReader;
import org.openscience.cdk.silent.AtomContainerSet;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * @cdk.module test-structgen
 */
@Category(SlowTest.class)
// structgen is slow... a single method here currently takes ~6 seconds
public class CrossoverMachineTest extends CDKTestCase {

    @Test
    public void testdoCrossover_IAtomContainer() throws Exception {
        String filename = "data/smiles/c10h16isomers.smi";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        SMILESReader reader = new SMILESReader(ins);
        IAtomContainerSet som = reader.read(new AtomContainerSet());
        Assert.assertEquals("We must have read 99 structures", 99, som.getAtomContainerCount());
        CrossoverMachine cm = new CrossoverMachine();
        String correctFormula = "C10H16";
        int errorcount = 0;
        for (int i = 0; i < som.getAtomContainerCount(); i++) {
            int[] hydrogencount1 = new int[4];
            for (IAtom atom : som.getAtomContainer(i).atoms()) {
                hydrogencount1[atom.getImplicitHydrogenCount()]++;
            }
            for (int k = i + 1; k < som.getAtomContainerCount(); k++) {
                try {
                    List<IAtomContainer> result = cm.doCrossover(som.getAtomContainer(i), som.getAtomContainer(k));
                    int[] hydrogencount2 = new int[4];
                    for (IAtom atom : som.getAtomContainer(k).atoms()) {
                        hydrogencount2[atom.getImplicitHydrogenCount()]++;
                    }
                    Assert.assertEquals("Result size must be 2", 2, result.size());
                    for (int l = 0; l < 2; l++) {
                        IAtomContainer ac = result.get(l);
                        Assert.assertTrue("Result must be connected", ConnectivityChecker.isConnected(ac));
                        Assert.assertEquals("Molecular formula must be the same as" + "of the input",
                                MolecularFormulaManipulator.getString(MolecularFormulaManipulator
                                        .getMolecularFormula(ac)), correctFormula);
                        int[] hydrogencountresult = new int[4];
                        int hcounttotal = 0;
                        for (IAtom atom : result.get(l).atoms()) {
                            hydrogencountresult[atom.getImplicitHydrogenCount()]++;
                            hcounttotal += atom.getImplicitHydrogenCount();
                        }
                        if (hydrogencount1[0] == hydrogencount2[0])
                            Assert.assertEquals("Hydrogen count of the result must" + " be same as of input",
                                    hydrogencount1[0], hydrogencountresult[0]);
                        if (hydrogencount1[1] == hydrogencount2[1])
                            Assert.assertEquals("Hydrogen count of the result must" + " be same as of input",
                                    hydrogencount1[1], hydrogencountresult[1]);
                        if (hydrogencount1[2] == hydrogencount2[2])
                            Assert.assertEquals("Hydrogen count of the result must" + " be same as of input",
                                    hydrogencount1[2], hydrogencountresult[2]);
                        if (hydrogencount1[3] == hydrogencount2[3])
                            Assert.assertEquals("Hydrogen count of the result must" + " be same as of input",
                                    hydrogencount1[3], hydrogencountresult[3]);
                        Assert.assertEquals(16, hcounttotal);
                    }
                } catch (CDKException ex) {
                    errorcount++;
                }
            }
        }
        Assert.assertTrue("We tolerate up to 300 errors", errorcount < 300);
    }
}
