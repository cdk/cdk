/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@slists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;

/**
 * @cdk.module test-smiles
 */
public class MoSSOutputReaderTest extends SimpleChemObjectReaderTest {

    @BeforeClass
    public static void setup() {
        setSimpleChemObjectReader(new MoSSOutputReader(), "data/moss/TKO.mossoutput");
    }

    @Test
    public void testAccepts() {
        MoSSOutputReader reader = new MoSSOutputReader();
        Assert.assertTrue(reader.accepts(AtomContainerSet.class));
    }

    @Test
    public void testExampleFile_MolReading() throws Exception {
        String filename = "data/moss/TKO.mossoutput";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MoSSOutputReader reader = new MoSSOutputReader(ins);
        IAtomContainerSet moleculeSet = new AtomContainerSet();
        moleculeSet = reader.read(moleculeSet);
        Assert.assertEquals(19, moleculeSet.getAtomContainerCount());
        for (IAtomContainer mol : moleculeSet.atomContainers()) {
            Assert.assertEquals(Integer.valueOf(mol.getProperty("atomCount").toString()).intValue(), mol.getAtomCount());
            Assert.assertEquals(Integer.valueOf(mol.getProperty("bondCount").toString()).intValue(), mol.getBondCount());
        }
    }

    @Test
    public void testExampleFile_SupportColumns() throws Exception {
        String filename = "data/moss/TKO.mossoutput";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MoSSOutputReader reader = new MoSSOutputReader(ins);
        IAtomContainerSet moleculeSet = new AtomContainerSet();
        moleculeSet = reader.read(moleculeSet);
        Assert.assertEquals(5.06, Double
                .valueOf(moleculeSet.getAtomContainer(0).getProperty("focusSupport").toString()).doubleValue(), 0.01);
        Assert.assertEquals(1.74,
                Double.valueOf(moleculeSet.getAtomContainer(0).getProperty("complementSupport").toString())
                        .doubleValue(), 0.01);
    }

}
