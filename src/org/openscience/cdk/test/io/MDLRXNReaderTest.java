/* $RCSfile $
 * $Author $
 * $Date $
 * $Revision $
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.test.io;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.geometry.*;
import java.io.*;
import junit.framework.*;
import com.baysmith.io.FileUtilities;
import java.util.Iterator;

/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @cdkPackage test
 *
 * @see org.openscience.cdk.io.MDLReader
 */
public class MDLRXNReaderTest extends TestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public MDLRXNReaderTest(String name) {
        super(name);
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
    }

    public static Test suite() {
        return new TestSuite(MDLRXNReaderTest.class);
    }

    public void testReadReactions1And2() {
        String filename1 = "data/mdl/reaction-1.rxn";
	String filename2 = "data/mdl/reaction-2.rxn";
        logger.info("Testing: " + filename1);
	logger.info("Testing: " + filename2);
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename1);
	InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(filename2);
        try {
            MDLRXNReader reader1 = new MDLRXNReader(new InputStreamReader(ins1));
            Reaction reaction1 = new Reaction();
	    reaction1 = (Reaction)reader1.read(reaction1);
            
            assertNotNull(reaction1);
            assertEquals(2, reaction1.getReactantCount());
	    assertEquals(1, reaction1.getProductCount());
	    
	    reader1.close();
	    
	    MDLRXNReader reader2 = new MDLRXNReader(new InputStreamReader(ins2));
	    Reaction reaction2 = (Reaction)reader2.read(reaction1);
            
            assertNotNull(reaction2);
            assertEquals(2, reaction2.getReactantCount());
	    assertEquals(2, reaction2.getProductCount());
	    
	    reader2.close();
	    
        } catch (Exception e) {
            fail(e.toString());
        }
    }
}
