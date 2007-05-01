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
package org.openscience.cdk.test.io;

import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestCase for the writer MDL mol files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLWriter
 */
public class MDLWriterTest extends CDKTestCase {

    public MDLWriterTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(MDLWriterTest.class);
    }

    public void testAccepts() throws Exception {
    	MDLWriter reader = new MDLWriter();
    	assertTrue(reader.accepts(ChemFile.class));
    	assertTrue(reader.accepts(ChemModel.class));
    	assertTrue(reader.accepts(Molecule.class));
    	assertTrue(reader.accepts(MoleculeSet.class));
    }

    /**
     * @cdk.bug 890456
     */
    public void testBug890456() throws Exception {
        StringWriter writer = new StringWriter();
        Molecule molecule = new Molecule();
        molecule.addAtom(new PseudoAtom("*"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        
        MDLWriter mdlWriter = new MDLWriter(writer);
        mdlWriter.write(molecule);
        assertTrue(writer.toString().indexOf("M  END") != -1);
    }

    /**
     * @cdk.bug 1212219
     */
    public void testBug1212219() throws Exception {
        StringWriter writer = new StringWriter();
        Molecule molecule = new Molecule();
        Atom atom = new Atom("C");
        atom.setMassNumber(14);
        molecule.addAtom(atom);
        
        MDLWriter mdlWriter = new MDLWriter(writer);
        mdlWriter.write(molecule);
        String output = writer.toString();
        //logger.debug("MDL output for testBug1212219: " + output);
        assertTrue(output.indexOf("M  ISO  1   1  14") != -1);
    }
}
