/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.test.io;

import java.io.StringWriter;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


import org.openscience.cdk.Reaction;
import org.openscience.cdk.io.MDLRXNWriter;
import org.openscience.cdk.io.MDLRXNReader;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestCase for the writer MDL rxn files using one test file.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.io.MDLRXNWriter
 */
public class MDLRXNWriterTest extends CDKTestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public MDLRXNWriterTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(MDLRXNWriterTest.class);
    }

    public void testRXNWriter() {
        
        String filename = "data/mdl/reaction-1.rxn";
        
        String originalFile = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            while (br.ready()) {
                originalFile += br.readLine() + "\n";
            }
            br.close();
        } catch (Exception ex) {
            logger.error("Error while testing MDLRXNWriter.");
            logger.debug(ex);
            fail(ex.getMessage());
        }
        
        Reaction reaction = new Reaction();
        try {
            MDLRXNReader reader = new MDLRXNReader(new FileReader(filename));
            reaction = (Reaction)reader.read(reaction);
            reader.close();
        } catch (Exception ex) {
            logger.error("Error while reading an MDL file");
            logger.debug(ex);
            fail(ex.getMessage());
        }
        
        assertEquals(2, reaction.getReactantCount());
        assertEquals(1, reaction.getProductCount());
        
        StringWriter writer = new StringWriter(10000);
        String file = "";
        try {
            MDLRXNWriter mdlWriter = new MDLRXNWriter(writer);
            mdlWriter.write(reaction);
            mdlWriter.close();
            file = writer.toString();
        } catch (Exception exception) {
            logger.error("Error while creating an MDL rxn file");
            logger.debug(exception);
            fail("Error in MDLRXNWriterTest: " + exception.getMessage());
        }
        
        assertTrue(file.length() > 0);
        assertTrue(originalFile.length() > 0);
        
        BufferedReader br1 = new BufferedReader(new StringReader(file));
        BufferedReader br2 = new BufferedReader(new StringReader(originalFile));
        
        try {
            int lineCounter = 0;
            while (br1.ready()) {
                String line = br1.readLine();
                String line2 = br2.readLine();
                if (line == null || line2 == null) break;
                assertTrue(line.trim().equals(line2.trim()));
                lineCounter++;
            }
            assertEquals(60, lineCounter);
        } catch (IOException ex) {
            logger.error("Error while reading an MDL file");
            logger.debug(ex);
            fail(ex.getMessage());
        }
        
        try {
            writer.close();
        } catch (Exception ex) {}
        
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }
    
}
