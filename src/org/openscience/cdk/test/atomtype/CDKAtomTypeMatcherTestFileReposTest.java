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
    		"2,5-dimethyl-furan.mol"
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
