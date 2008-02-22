/* $Revision: 8288 $ $Author: egonw $ $Date: 2007-05-01 17:58:55 +0200 (Tue, 01 May 2007) $
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
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.CDKSourceCodeWriter;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestCase for the writer CDK source code files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.CDKSourceCodeWriterTest
 */
public class CDKSourceCodeWriterTest extends CDKTestCase {

    public CDKSourceCodeWriterTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(CDKSourceCodeWriterTest.class);
    }

    public void testAccepts() throws Exception {
    	CDKSourceCodeWriter reader = new CDKSourceCodeWriter();
    	assertTrue(reader.accepts(Molecule.class));
    	assertTrue(reader.accepts(AtomContainer.class));
    }

    public void testOutput() throws Exception {
        StringWriter writer = new StringWriter();
        Molecule molecule = new Molecule();
        Atom atom = new Atom("C");
        atom.setMassNumber(14);
        molecule.addAtom(atom);
        
        CDKSourceCodeWriter sourceWriter = new CDKSourceCodeWriter(writer);
        sourceWriter.write(molecule);
        String output = writer.toString();
        System.out.println(output);
        assertTrue(output.indexOf("IAtom a1 = mol.getBuilder().newAtom(\"C\")") != -1);
    }
}
