/* $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */

package org.openscience.cdk.test;

import junit.framework.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.test.io.*;
import org.openscience.cdk.test.io.cml.*;
import org.openscience.cdk.test.smiles.*;


/**
 * TestSuite that runs all the sample tests
 *
 */
public class CDKTests {

	static MoleculeListViewer moleculeListViewer = null;

	public static void main (String[] args) 
	{
		junit.textui.TestRunner.run(suite());
	}
	public static Test suite ( ) 
	{
		TestSuite suite= new TestSuite("All CDK Tests");
		suite.addTest(CoreClassesTests.suite());
		suite.addTest(CloneAtomContainerTest.suite());
		suite.addTest(RingSearchTest.suite());
		suite.addTest(ConnectivityCheckerTest.suite());
		suite.addTest(MorganNumberToolsTest.suite());
		suite.addTest(MFAnalyserTest.suite());
		suite.addTest(PathLengthTest.suite());
		suite.addTest(IsomorphismTesterTest.suite());
		suite.addTest(IsotopeFactoryTest.suite());
		suite.addTest(AllRingsFinderTest.suite());
		suite.addTest(FingerprinterTest.suite());
		suite.addTest(AtomTypeFactoryTest.suite());
        // from cdk.test.io.*
		suite.addTest(CMLIOTests.suite());
		suite.addTest(PDBReaderTest.suite());
        // from cdk.test.smiles
		suite.addTest(SmilesGeneratorTest.suite());
		suite.addTest(SmilesParserTest.suite());

	    return suite;
	}
	public static MoleculeListViewer getMoleculeListViewer()
	{
		if (moleculeListViewer == null)
		{
			moleculeListViewer = new MoleculeListViewer();
		}
		return moleculeListViewer;
	}
	
}
