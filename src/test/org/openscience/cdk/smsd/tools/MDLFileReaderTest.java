
/* Copyright (C) 2009-2010 Syed Asad Rahman {asad@ebi.ac.uk}
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
 */
package org.openscience.cdk.smsd.tools;

import java.io.IOException;
import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.IChemObjectReader.Mode;

/**
 * @cdk.module test-smsd
 * @author Asad
 */
public class MDLFileReaderTest {

    public MDLFileReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getMolecule method, of class MDLFileReader.
     * @throws IOException
     */
    @Test
    public void testGetMolecule() throws IOException {
        System.out.println("getMolecule");
        String molfile = "data/mdl/5SD.mol";
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(molfile);
        MDLFileReader instance = new MDLFileReader(ins1, Mode.STRICT);
        IMolecule result = instance.getMolecule();
        assertNotNull(result);
    }

    /**
     * Test of getMoleculeWithLayoutCheck method, of class MDLFileReader.
     * @throws IOException 
     */
    @Test
    public void testGetMoleculeWithLayoutCheck() throws IOException {
        System.out.println("getMoleculeWithLayoutCheck");
        String molfile = "data/mdl/decalin.mol";
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(molfile);
        MDLFileReader instance = new MDLFileReader(ins1, Mode.STRICT);
        IMolecule expResult = null;
        IMolecule result = instance.getMoleculeWithLayoutCheck();
        assertNotNull(result);
    }

    /**
     * Test of getChemModelWithMoleculeWithLayoutCheck method, of class MDLFileReader.
     * @throws IOException 
     */
    @Test
    public void testGetChemModelWithMoleculeWithLayoutCheck() throws IOException {
        System.out.println("getChemModelWithMoleculeWithLayoutCheck");
        String molfile = "data/mdl/decalin.mol";
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(molfile);
        MDLFileReader instance = new MDLFileReader(ins1, Mode.STRICT);
        IMolecule expResult = null;
        IChemModel result = instance.getChemModelWithMoleculeWithLayoutCheck();
        assertNotNull(result);
    }
}
