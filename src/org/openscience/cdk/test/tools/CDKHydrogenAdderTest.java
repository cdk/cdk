/* $Revision: 8537 $ $Author: egonw $ $Date: 2007-07-14 15:46:21 +0200 (Sat, 14 Jul 2007) $ 
 *
 * Copyright (C) 2007  Egon Willighagen <egonw@sci.kun.nl>
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
package org.openscience.cdk.test.tools;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NNAtom;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Tests CDK's hydrogen adding capabilities in terms of
 * example molecules.
 *
 * @cdk.module  test-valencycheck
 *
 * @author      Egon Willighagen <egonw@users.sf.net>
 * @cdk.created 2007-07-28
 */
public class CDKHydrogenAdderTest extends CDKTestCase {

//	private final static LoggingTool logger = new LoggingTool(CDKHydrogenAdderTest.class);
	private final static CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(NoNotificationChemObjectBuilder.getInstance());
	private final static CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(NoNotificationChemObjectBuilder.getInstance());
	
    public CDKHydrogenAdderTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {}

    public static Test suite() {
        return new TestSuite(CDKHydrogenAdderTest.class);
    }
    
    public void testMethane() throws CDKException {
    	IMolecule molecule = new NNMolecule();
    	IAtom newAtom = new NNAtom(Elements.CARBON);
    	molecule.addAtom(newAtom);
    	IAtomType type = matcher.findMatchingAtomType(molecule, newAtom);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(newAtom, type);
    	
    	assertNull(newAtom.getHydrogenCount());
    	adder.addImplicitHydrogens(molecule);
    	assertNotNull(newAtom.getHydrogenCount());
    	assertEquals(4, newAtom.getHydrogenCount().intValue());	
    }

}

