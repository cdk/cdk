/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.test.io;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.io.GhemicalMMReader;

/**
 * TestCase for the reading Ghemical molecular dynamics files using one test file.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.io.GhemicalReader
 */
public class GhemicalReaderTest extends TestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public GhemicalReaderTest(String name) {
        super(name);
        logger = new org.openscience.cdk.tools.LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(GhemicalReaderTest.class);
    }

    public void testExample() {
        String testfile =
"!Header mm1gp 100\n" +
"!Info 1\n" +
"!Atoms 6\n" +
"0 6 \n" +
"1 6 \n" +
"2 1 \n" +
"3 1 \n" +
"4 1 \n" +
"5 1 \n" +
"!Bonds 5\n" +
"1 0 D \n" +
"2 0 S \n" +
"3 0 S \n" +
"4 1 S \n" +
"5 1 S \n" +
"!Coord\n" +
"0 0.06677 -0.00197151 4.968e-07 \n" +
"1 -0.0667699 0.00197154 -5.19252e-07 \n" +
"2 0.118917 -0.097636 2.03406e-06 \n" +
"3 0.124471 0.0904495 -4.84021e-07 \n" +
"4 -0.118917 0.0976359 -2.04017e-06 \n" +
"5 -0.124471 -0.0904493 5.12591e-07 \n" +
"!Charges\n" +
"0 -0.2\n" +
"1 -0.2\n" +
"2 0.1\n" +
"3 0.1\n" +
"4 0.1\n" +
"5 0.1\n" +
"!End";
        StringReader stringReader = new StringReader(testfile);
        try {
            GhemicalMMReader reader = new GhemicalMMReader(stringReader);
            ChemModel model = (ChemModel)reader.read((ChemObject)new ChemModel());
            
            assertNotNull(model);
            assertNotNull(model.getSetOfMolecules());
            SetOfMolecules som = model.getSetOfMolecules();
            assertNotNull(som);
            assertEquals(1, som.getMoleculeCount());
            Molecule m = som.getMolecule(0);
            assertNotNull(m);
            assertEquals(6, m.getAtomCount());
            assertEquals(5, m.getBondCount());
            
            // test reading of formal charges
            Atom a = m.getAtomAt(0);
            assertNotNull(a);
            assertEquals(6, a.getAtomicNumber());
            assertEquals(-0.2, a.getCharge(), 0.01);
            assertEquals(0.06677, a.getX3d(), 0.01);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

}
