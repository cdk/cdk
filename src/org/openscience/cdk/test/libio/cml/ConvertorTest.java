/* $Revision: 7636 $ $Author: ospjuth $ $Date: 2007-01-04 17:46:10 +0000 (Thu, 04 Jan 2007) $
 *
 * Copyright (C) 2007  Ola Spjuth <ospjuth@users.sf.net>
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
 * 
 */
package org.openscience.cdk.test.libio.cml;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.libio.cml.Convertor;
import org.openscience.cdk.libio.md.MDMolecule;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;
import org.xmlcml.cml.element.CMLAtom;

/**
 * @cdk.module test-libiocml
 */
public class ConvertorTest extends CDKTestCase {

    private LoggingTool logger;

    public ConvertorTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(ConvertorTest.class);
    }

    /**
     * @cdk.bug 1748257
     */
    public void testBug1748257 () {
    	
    	MDMolecule mol=new MDMolecule();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("H")); // 2
        mol.addAtom(new Atom("H")); // 3
        mol.addAtom(new Atom("H")); // 4
        mol.addAtom(new Atom("H")); // 5

        mol.addBond(0, 1, 2); // 1
        mol.addBond(1, 2, 2); // 2
        mol.addBond(2, 0, 1); // 3
        mol.addBond(3, 0, 1); // 4
        mol.addBond(4, 1, 1); // 5
        mol.addBond(5, 1, 1); // 6
        
        Convertor convertor=new Convertor(false,"");
        CMLAtom cmlatom=convertor.cdkAtomToCMLAtom(mol,mol.getAtom(2));
        assertEquals(cmlatom.getHydrogenCount(),0);
    }
    
}
