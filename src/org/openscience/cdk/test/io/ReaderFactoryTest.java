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
package org.openscience.cdk.test.io;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.geometry.*;
import java.io.*;
import junit.framework.*;
import com.baysmith.io.FileUtilities;
import java.util.Iterator;

/**
 * TestCase for the reading CML files using a few test files
 * in data/cmltest as found in the Jmol distribution
 * (http://jmol.sf.org/).
 */
public class ReaderFactoryTest extends TestCase {

    public ReaderFactoryTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(ReaderFactoryTest.class);
    }

    public void testCML() {
        String filename = "data/cmltest/estron.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(ins));
            ChemObjectReader reader = ReaderFactory.createReader(br);
            if (reader instanceof CMLReader) {
                // ok
            } else {
                fail("Wrong file format detected for " + filename + 
                     ". Expected CMLReader, but found: " +
                     reader.getClass().getName());
            }
            // reader should have been reset, so check number of lines
            int linecount = 0;
            while (br.ready()) {
                String line = br.readLine();
                linecount++;
            }
            assertEquals(850, linecount);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testXYZ() {
        String filename = "data/bf3.xyz";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(ins));
            ChemObjectReader reader = ReaderFactory.createReader(br);
            if (reader instanceof XYZReader) {
                // ok
            } else {
                fail("Wrong file format detected for " + filename + 
                     ". Expected XYZReader, but found: " +
                     reader.getClass().getName());
            }
            // reader should have been reset, so check number of lines
            int linecount = 0;
            while (br.ready()) {
                String line = br.readLine();
                linecount++;
            }
            assertEquals(7, linecount);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testShelX() {
        String filename = "data/frame_1.res";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(ins));
            ChemObjectReader reader = ReaderFactory.createReader(br);
            if (reader instanceof ShelXReader) {
                // ok
            } else {
                fail("Wrong file format detected for " + filename + 
                     ". Expected ShelXReader, but found: " +
                     reader.getClass().getName());
            }
            // reader should have been reset, so check number of lines
            int linecount = 0;
            while (br.ready()) {
                String line = br.readLine();
                linecount++;
            }
            assertEquals(51, linecount);
        } catch (Exception e) {
            fail(e.toString());
        }
    }
    
    public void testMDLMol() {
        String filename = "data/methylbenzol.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(ins));
            ChemObjectReader reader = ReaderFactory.createReader(br);
            if (reader instanceof MDLReader) {
                // ok
            } else {
                fail("Wrong file format detected for " + filename + 
                     ". Expected MDLReader, but found: " +
                     reader.getClass().getName());
            }
            // reader should have been reset, so check number of lines
            int linecount = 0;
            while (br.ready()) {
                String line = br.readLine();
                linecount++;
            }
            assertEquals(36, linecount);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testPDB() {
        String filename = "data/coffeine.pdb";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(ins));
            ChemObjectReader reader = ReaderFactory.createReader(br);
            if (reader instanceof PDBReader) {
                // ok
            } else {
                fail("Wrong file format detected for " + filename + 
                     ". Expected PDBReader, but found: " +
                     reader.getClass().getName());
            }
            // reader should have been reset, so check number of lines
            int linecount = 0;
            while (br.ready()) {
                String line = br.readLine();
                linecount++;
            }
            assertEquals(16, linecount);
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testSMILES() {
        String filename = "data/smiles.txt";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(ins));
            ChemObjectReader reader = ReaderFactory.createReader(br);
            if (reader instanceof SMILESReader) {
                // ok
            } else {
                fail("Wrong file format detected for " + filename + 
                     ". Expected SMILESReader, but found: " +
                     reader.getClass().getName());
            }
            // reader should have been reset, so check number of lines
            int linecount = 0;
            while (br.ready()) {
                String line = br.readLine();
                linecount++;
            }
            assertEquals(8, linecount);
        } catch (Exception e) {
            fail(e.toString());
        }
    }
}
