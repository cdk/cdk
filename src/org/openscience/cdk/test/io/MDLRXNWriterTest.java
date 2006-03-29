/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.test.io;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.MDLRXNReader;
import org.openscience.cdk.io.MDLRXNWriter;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the writer MDL rxn files using one test file.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.io.MDLRXNWriter
 */
public class MDLRXNWriterTest extends CDKTestCase {

    private org.openscience.cdk.tools.LoggingTool logger;
    private IChemObjectBuilder builder;

    public MDLRXNWriterTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }
    
    public static Test suite() {
        return new TestSuite(MDLRXNWriterTest.class);
    }

    public void testRoundtrip() {
        IReaction reaction = builder.newReaction();
        IMolecule hydroxide = builder.newMolecule();
        hydroxide.addAtom(builder.newAtom("O"));
        reaction.addReactant(hydroxide);
        IMolecule proton = builder.newMolecule();
        proton.addAtom(builder.newAtom("H"));
        reaction.addReactant(proton);
        IMolecule water = builder.newMolecule();
        water.addAtom(builder.newAtom("O"));
        reaction.addProduct(water);
        
        // now serialize to MDL RXN
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
        
        // now deserialize the MDL RXN output
        IReaction reaction2 = builder.newReaction();
        try {
        	MDLRXNReader reader = new MDLRXNReader(new StringReader(file));
        	reaction2 = (IReaction)reader.read(reaction2);
            reader.close();
        } catch (Exception ex) {
            logger.error("Error while reading an MDL file");
            logger.debug(ex);
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            fail(ex.getMessage());
        }
        
        assertEquals(2, reaction2.getReactantCount());
        assertEquals(1, reaction2.getProductCount());
    }
    

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
}
