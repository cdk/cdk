/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IChemObject;

import java.io.InputStream;

/**
 * TestCase for CDK IO classes.
 *
 * @cdk.module test-io
 */
public abstract class SimpleChemObjectReaderTest extends ChemObjectReaderTest {

    protected static ISimpleChemObjectReader chemObjectIO;

    public static void setSimpleChemObjectReader(ISimpleChemObjectReader aSimpelChemObjectReader, String testFile) {
        ChemObjectReaderTest.setChemObjectReader(aSimpelChemObjectReader, testFile);
        SimpleChemObjectReaderTest.chemObjectIO = aSimpelChemObjectReader;
    }

    @Test
    public void testRead_IChemObject() throws Exception {
        Assert.assertNotNull("No test file has been set!", testFile);

        boolean read = false;
        for (IChemObject object : acceptableChemObjects()) {
            if (chemObjectIO.accepts(object.getClass())) {
                InputStream ins = SimpleChemObjectReaderTest.class.getClassLoader().getResourceAsStream(testFile);
                chemObjectIO.setReader(ins);
                IChemObject readObject = chemObjectIO.read(object);
                chemObjectIO.close();
                Assert.assertNotNull("Failed attempt to read the file as " + object.getClass().getName(), readObject);
                read = true;
            }
        }
        if (!read) {
            Assert.fail("Reading an IChemObject from the Reader did not work properly.");
        }
    }

}
