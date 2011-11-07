/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.io;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;

/**
 * TestCase for reading CML files.
 *
 * @cdk.module test-io
 */
public class CMLReaderTest extends SimpleChemObjectReaderTest {

    @BeforeClass public static void setup() {
        setSimpleChemObjectReader(new CMLReader(), "data/cml/3.cml");
    }

    @Test public void testAccepts() {
    	Assert.assertTrue(chemObjectIO.accepts(ChemFile.class));
    }

    @Test(expected=CDKException.class)
    public void testSetReader_Reader() throws Exception {
        InputStream ins = ChemObjectReaderTest.class.getClassLoader().getResourceAsStream(testFile);
        chemObjectIO.setReader(new InputStreamReader(ins));
    }
}
