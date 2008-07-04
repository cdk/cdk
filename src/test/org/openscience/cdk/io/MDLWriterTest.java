/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@slists.sourceforge.net
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
package org.openscience.cdk.io;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;

/**
 * TestCase for the writer MDL mol files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLWriter
 */
public class MDLWriterTest extends ChemObjectIOTest {

    private static IChemObjectBuilder builder;

    @BeforeClass public static void setup() {
        builder = DefaultChemObjectBuilder.getInstance();
        setChemObjectIO(new MDLRXNWriter());
    }

    @Test public void testAccepts() throws Exception {
    	MDLWriter reader = new MDLWriter();
    	Assert.assertTrue(reader.accepts(ChemFile.class));
    	Assert.assertTrue(reader.accepts(ChemModel.class));
    	Assert.assertTrue(reader.accepts(Molecule.class));
    	Assert.assertTrue(reader.accepts(MoleculeSet.class));
    }

    /**
     * @cdk.bug 890456
     */
    @Test public void testBug890456() throws Exception {
        StringWriter writer = new StringWriter();
        Molecule molecule = new Molecule();
        molecule.addAtom(new PseudoAtom("*"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        
        MDLWriter mdlWriter = new MDLWriter(writer);
        mdlWriter.write(molecule);
        Assert.assertTrue(writer.toString().indexOf("M  END") != -1);
    }

    /**
     * @cdk.bug 1212219
     */
    @Test public void testBug1212219() throws Exception {
        StringWriter writer = new StringWriter();
        Molecule molecule = new Molecule();
        Atom atom = new Atom("C");
        atom.setMassNumber(14);
        molecule.addAtom(atom);
        
        MDLWriter mdlWriter = new MDLWriter(writer);
        mdlWriter.write(molecule);
        String output = writer.toString();
        //logger.debug("MDL output for testBug1212219: " + output);
        Assert.assertTrue(output.indexOf("M  ISO  1   1  14") != -1);
    }
    
    /**
     * Test for bug #1778479 "MDLWriter writes empty PseudoAtom label string".
     * When a molecule contains an IPseudoAtom without specifying the atom label
     * the MDLWriter generates invalid output as it prints the zero-length atom
     * label.
     * This was fixed with letting PseudoAtom have a default label of '*'.
     *
     * Author: Andreas Schueller <a.schueller@chemie.uni-frankfurt.de>
     * 
     * @cdk.bug 1778479
     */
    @Test public void testBug1778479() throws Exception {
        StringWriter writer = new StringWriter();
        IMolecule molecule = builder.newMolecule();
        IAtom atom1 = builder.newPseudoAtom();
        IAtom atom2 = builder.newAtom("C");
        IBond bond = builder.newBond(atom1, atom2);
        molecule.addAtom(atom1);
        molecule.addAtom(atom2);
        molecule.addBond(bond);
            
        MDLWriter mdlWriter = new MDLWriter(writer);
        mdlWriter.write(molecule);
        String output = writer.toString();
        Assert.assertEquals("Test for zero length pseudo atom label in MDL file", -1, output.indexOf("0.0000    0.0000    0.0000     0  0  0  0  0  0  0  0  0  0  0  0"));
    }
}
