/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
package org.openscience.cdk.test.io.cml;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import java.io.*;
import junit.framework.*;
import javax.vecmath.*;

/**
 * TestCase for the reading CML 2 files using a few test files
 * in data/cmltest.
 */
public class CMLRoundTripTest extends TestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public CMLRoundTripTest(String name) {
        super(name);
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
    }

    public static Test suite() {
        return new TestSuite(CMLRoundTripTest.class);
    }

    public void testAtom() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        
        Molecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        Atom roundTrippedAtom = roundTrippedMol.getAtomAt(0);
        assertEquals(atom.getSymbol(), roundTrippedAtom.getSymbol());
    }
    
    public void testAtom2D() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        Point2d p2d = new Point2d(1.3, 1.4);
        atom.setPoint2D(p2d);
        
        Molecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        Atom roundTrippedAtom = roundTrippedMol.getAtomAt(0);
        assertEquals(atom.getX2D(), roundTrippedAtom.getX2D(), 0.00001);
        assertEquals(atom.getY2D(), roundTrippedAtom.getY2D(), 0.00001);
    }
    
    public void testAtom3D() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        Point3d p3d = new Point3d(1.3, 1.4, 0.9);
        atom.setPoint3D(p3d);
        
        Molecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        Atom roundTrippedAtom = roundTrippedMol.getAtomAt(0);
        assertEquals(atom.getX3D(), roundTrippedAtom.getX3D(), 0.00001);
        assertEquals(atom.getY3D(), roundTrippedAtom.getY3D(), 0.00001);
        assertEquals(atom.getZ3D(), roundTrippedAtom.getZ3D(), 0.00001);
    }
    
    public void testAtomFormalCharge() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        int formalCharge = +1;
        atom.setFormalCharge(formalCharge);
        
        Molecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        Atom roundTrippedAtom = roundTrippedMol.getAtomAt(0);
        assertEquals(atom.getFormalCharge(), roundTrippedAtom.getFormalCharge());
    }
    
    /**
     * Convert a Molecule to CML and back to a Molecule again.
     * Given that CML reading is working, the problem is with the
     * CMLWriter.
     *
     * @see org.openscience.cdk.CMLFragmentsTest
     */
    private Molecule roundTripMolecule(Molecule mol) {
        StringWriter stringWriter = new StringWriter();
        try {
            CMLWriter writer = new CMLWriter(stringWriter);
            writer.write(mol);
        } catch (Exception exception) {
            String message = "Failed when writing CML";
            logger.error(message);
            logger.debug(exception);
            fail(message);
        }

        Molecule roundTrippedMol = null;
        try {
            String cmlString = stringWriter.toString();
            CMLReader reader = new CMLReader(new StringReader(cmlString));
            
            roundTrippedMol = (Molecule)reader.read(new Molecule());
            
            assertNotNull(roundTrippedMol);
        } catch (Exception exception) {
            String message = "Failed when reading CML";
            logger.error(message);
            logger.debug(exception);
            fail(message);
        }
        
        return roundTrippedMol;
    }
}
