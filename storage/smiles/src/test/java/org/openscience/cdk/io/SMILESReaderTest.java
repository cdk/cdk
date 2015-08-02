/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk.io;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.iterator.IteratingSMILESReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.InputStream;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @cdk.module test-smiles
 *
 * @see org.openscience.cdk.io.MDLReader
 */
public class SMILESReaderTest extends SimpleChemObjectReaderTest {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(SMILESReaderTest.class);

    @BeforeClass
    public static void setup() throws Exception {
        setSimpleChemObjectReader(new SMILESReader(), "data/smiles/smiles.smi");
    }

    @Test
    public void testAccepts() {
        SMILESReader reader = new SMILESReader();
        Assert.assertTrue(reader.accepts(ChemFile.class));
        Assert.assertTrue(reader.accepts(AtomContainerSet.class));
    }

    @Test
    public void testReading() throws Exception {
        String filename = "data/smiles/smiles.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        SMILESReader reader = new SMILESReader(ins);
        IAtomContainerSet som = reader.read(new AtomContainerSet());
        Assert.assertEquals(8, som.getAtomContainerCount());
    }

    @Test
    public void testReadingSmiFile_1() throws Exception {
        String filename = "data/smiles/smiles.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        SMILESReader reader = new SMILESReader(ins);
        IAtomContainerSet som = reader.read(new AtomContainerSet());
        String name = null;
        IAtomContainer thisMol = som.getAtomContainer(0);
        name = ((String) thisMol.getProperty("SMIdbNAME")).toString();
        Assert.assertEquals("benzene", name);
    }

    @Test
    public void testReadingSmiFile_2() throws Exception {
        String filename = "data/smiles/smiles.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        SMILESReader reader = new SMILESReader(ins);
        IAtomContainerSet som = reader.read(new AtomContainerSet());
        IAtomContainer thisMol = som.getAtomContainer(1);
        Assert.assertNull(thisMol.getProperty("SMIdbNAME"));
    }

    @Test
    public void testReadingSmiFile_3() throws Exception {
        String filename = "data/smiles/test3.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        SMILESReader reader = new SMILESReader(ins);
        IAtomContainerSet som = reader.read(new AtomContainerSet());
        Assert.assertEquals(5, som.getAtomContainerCount());
    }
    
    @Test 
    public void badSmilesLine() throws CDKException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        String input = "C\nn1cccc1\nc1ccccc1\n";
        DefaultChemObjectReader cor = new SMILESReader(new StringReader(input));
        IAtomContainerSet mols = cor.read(bldr.newInstance(IAtomContainerSet.class));
        assertThat(mols.getAtomContainerCount(), is(3));
        assertThat(mols.getAtomContainer(0).getAtomCount(), is(1));
        assertThat(mols.getAtomContainer(0).getProperty(IteratingSMILESReader.BAD_SMILES_INPUT),
                   nullValue());
        assertThat(mols.getAtomContainer(1).getAtomCount(), is(0));
        assertThat(mols.getAtomContainer(1).getProperty(IteratingSMILESReader.BAD_SMILES_INPUT),
                   notNullValue());
        assertThat(mols.getAtomContainer(2).getAtomCount(), is(6));
        assertThat(mols.getAtomContainer(2).getProperty(IteratingSMILESReader.BAD_SMILES_INPUT),
                   nullValue());
    }

}
