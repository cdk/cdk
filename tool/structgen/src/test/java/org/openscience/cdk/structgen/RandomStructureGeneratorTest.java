/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.structgen;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.templates.TestMoleculeFactory;

import javax.vecmath.Vector2d;
import java.util.Vector;

/**
 * @cdk.module test-structgen
 */
public class RandomStructureGeneratorTest extends CDKTestCase {

    public boolean debug      = false;
    boolean        standAlone = false;

    public void setStandAlone(boolean standAlone) {
        this.standAlone = standAlone;
    }

    @Test
    public void testTwentyRandomStructures() {
        IAtomContainer molecule = TestMoleculeFactory.makeAlphaPinene();
        RandomGenerator rg = new RandomGenerator(molecule);
        IAtomContainer result = null;
        for (int f = 0; f < 50; f++) {
            result = rg.proposeStructure();
            Assert.assertEquals(molecule.getAtomCount(), result.getAtomCount());
            Assert.assertEquals(1, ConnectivityChecker.partitionIntoMolecules(result).getAtomContainerCount());
        }
    }

    /**
     * @param structures
     * @return
     */
    private boolean everythingOk(Vector structures) throws Exception {
        StructureDiagramGenerator sdg;
        IAtomContainer mol;
        if (debug) System.out.println("number of structures in vector: " + structures.size());
        for (int f = 0; f < structures.size(); f++) {
            sdg = new StructureDiagramGenerator();

            mol = (IAtomContainer) structures.elementAt(f);
            sdg.setMolecule(mol);

            sdg.generateCoordinates(new Vector2d(0, 1));
        }
        return true;
    }

}
