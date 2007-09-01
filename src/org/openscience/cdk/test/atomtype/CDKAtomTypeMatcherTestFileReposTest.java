/* $Revision: 5889 $ $Author: egonw $ $Date: 2006-04-06 15:24:58 +0200 (Thu, 06 Apr 2006) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.atomtype;

import java.io.InputStream;
import java.util.Iterator;

import junit.framework.JUnit4TestAdapter;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.test.NewCDKTestCase;

/**
 * This class tests the matching of atom types defined in the
 * structgen atom type list, using the test files in src/data.
 *
 * @cdk.module test-extra
 * 
 * @see org.openscience.cdk.atomtype.CDKAtomTypeMatcher
 */
public class CDKAtomTypeMatcherTestFileReposTest extends NewCDKTestCase {

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CDKAtomTypeMatcherTestFileReposTest.class);
    }

    @Test public void testMDLMolfiles() throws Exception {
    	final String DIRNAME = "data/mdl/";
    	String[] testFiles = {
    		"2,5-dimethyl-furan.mol",
    		"5SD.mol",
    		"9553.mol",
    		"9554.mol",
    		"ADN.mol",
    		"allmol231.mol",
    		"allmol232.mol",
    		"a-pinene.mol",
    		"azulene.mol",
    		"big.mol",
    		"BremserPredictionTest.mol",
    		"bug1014344-1.mol",
    		"bug1089770-1.mol",
    		"bug1089770-2.mol",
    		"bug1208740_1.mol",
    		"bug1208740_2.mol",
    		"bug1328739.mol",
    		"bug1587283.mol",
    		"bug_1750968.mol",
    		"bug1772609.mol",
    		"bug599540.mol",
    		"bug682233.mol",
    		"bug698152.mol",
    		"bug706786-1.mol",
    		"bug706786-2.mol",
    		"bug716259.mol",
    		"bug736137.mol",
    		"bug771485-1.mol",
    		"bug771485-2.mol",
    		"bug853254-1.mol",
    		"bug853254-2.mol",
    		"bug931608-1.mol",
    		"bug931608-2.mol",
    		"bug934819-1.mol",
    		"bug934819-2.mol",
    		"Butane-TestFF.mol",
    		"Butane-TestFF-output.mol",
    		"butanoic_acid.mol",
    		"C12308.mol",
    		"carbocations.mol",
    		"choloylcoa.mol",
    		"clorobenzene.mol",
    		"cyclooctadien.mol",
    		"cyclooctan.mol",
    		"cycloocten.mol",
    		"cyclopropane.mol",
    		"d-ala.mol",
    		"decalin.mol",
    		"D+-glucose.mol",
    		"D-mannose.mol",
    		"Ethane-TestFF.mol",
    		"Ethane-TestFF-output.mol",
    		"figueras-test-buried.mol",
    		"figueras-test-inring.mol",
    		"figueras-test-sep3D.mol",
    		"four-ring-5x10.mol",
    		"heptane_almost_cyclic.mol",
    		"heptane_almost_cyclic-output.mol",
    		"heptane-modelbuilder.mol",
    		"heptane-modelbuilder-output.mol",
    		"heptane.mol",
    		"Heptane-TestFF.mol",
    		"Heptane-TestFF-output.mol",
    		"hydroxyamino.mol",
    		"isopropylacetate.mol",
    		"l-ala.mol",
    		"methylbenzol.mol",
    		"mmff94AtomTypeTest_molecule.mol",
    		"molV3000.mol",
    		"murckoTest10.mol",
    		"murckoTest11.mol",
    		"murckoTest1.mol",
    		"murckoTest2.mol",
    		"murckoTest3.mol",
    		"murckoTest4.mol",
    		"murckoTest5.mol",
    		"murckoTest6_3d_2.mol",
    		"murckoTest6_3d.mol",
    		"murckoTest6.mol",
    		"murckoTest7.mol",
    		"murckoTest8.mol",
    		"murckoTest9.mol",
    		"NN_dimethylaniline.mol",
    		"nonConnectedPiSystems.mol",
    		"Ooporphyrin.mol",
    		"piSystemCumulative.mol",
    		"piSystemWithCarbokation.mol",
    		"polycarpol.mol",
    		"porphyrin.mol",
    		"prediction-test.mol",
    		"reserpine.mol",
    		"ring_03419.mol",
    		"saturationcheckertest.mol",
    		"sdg_test.mol",
    		"shortest_path_test.mol",
    		"six-ring-4x4.mol",
    		"squareplanar.mol",
    		"sulfurCompound.mol",
    		"superspiro.mol",
    		"testdoublebondconfig.mol",
    		"testisopropylacetate.mol",
    		"tetrahedral_1_lazy.mol",
    		"tetrahedral_1.mol",
    		"tetrahedral_2_lazy.mol",
    		"tetrahedral_with_four_wedges.mol",
    		"thiamin.mol",
    		"too.many.rings.mol",
    		"triacylglycerols.mol",
    		"trigonal_bipyramidal.mol",
    		"withcharges.mol",
    		"zinctest.mol"
    	};
    	int tested = 0;
    	int failed = 0;
    	for (int i=0;i<testFiles.length; i++) {
    		TestResults results = testFile(DIRNAME, testFiles[i]);
    		tested += results.tested;
    		failed += results.failed;
    	}
    	Assert.assertEquals("Could not match all atom types!", tested, (tested - failed));
    }
    
    private TestResults testFile(String dir, String filename) throws Exception {    	
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(DefaultChemObjectBuilder.getInstance());
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(dir+filename);
        IChemObjectReader reader = new MDLV2000Reader(ins);
        IMolecule mol = (IMolecule)reader.read(new NNMolecule());
        
        TestResults results = new TestResults();
        Iterator<IAtom> atoms = mol.atoms();
        while (atoms.hasNext()) {
        	results.tested++;
        	IAtom atom = atoms.next();
        	IAtomType matched = matcher.findMatchingAtomType(mol, atom);
        	if (matched == null) {
        		results.failed++;
        		System.out.println("Could not match atom: " + results.tested + " in file " + filename);
        	}
        }
        return results;
    }
    
    class TestResults {
    	
    	int tested;
    	int failed;
    	
    	TestResults() {
    		tested = 0;
    		failed = 0;
    	}
    	
    }

}
