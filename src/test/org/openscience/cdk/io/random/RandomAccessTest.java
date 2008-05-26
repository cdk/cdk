/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova <nina@acad.bg>
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
 */
package org.openscience.cdk.io.random;

import java.io.File;

import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Test for {@link RandomAccessSDFReader}.
 *
 * @author     Nina Jeliazkova <nina@acad.bg>
 * @cdk.module test-extra
 */
public class RandomAccessTest extends CDKTestCase {

    private LoggingTool logger;
    @Override
    protected void setUp() throws Exception {
    	super.setUp();
        logger = new LoggingTool(this);
    }
    @Override
    protected void tearDown() throws Exception {
    	super.tearDown();
    }
    public void test() throws Exception {
        String filename = "cdk/src/data/mdl/test2.sdf";
        logger.info("Testing: " + filename);
        File f = new File(filename);
        //System.out.println(System.getProperty("user.dir"));
        RandomAccessReader rf = new RandomAccessSDFReader(f,DefaultChemObjectBuilder.getInstance());
        assertEquals(6,rf.size());
        String[] mdlnumbers = {
        		"MFCD00000387",
        		"MFCD00000661",
        		"MFCD00000662",
        		"MFCD00000663",
        		"MFCD00000664",
        		"MFCD03453215"
        };
        //reading backwards - just for the test
        for (int i=rf.size()-1; i >=0;i--) {
            IChemObject m = rf.readRecord(i);
            assertEquals(m.getProperty("MDLNUMBER"),mdlnumbers[i]);
            assertTrue(m instanceof IMolecule);
            assertTrue(((IMolecule)m).getAtomCount()>0);
        }
        rf.close();
    }
}
