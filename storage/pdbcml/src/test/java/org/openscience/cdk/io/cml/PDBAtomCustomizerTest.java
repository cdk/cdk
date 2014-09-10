/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *                    2013  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.io.cml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.libio.cml.PDBAtomCustomizer;
import org.openscience.cdk.protein.data.PDBPolymer;

/**
 * TestCase for the {@link PDBAtomCustomizer} class.
 *
 * @cdk.module test-pdbcml
 */
public class PDBAtomCustomizerTest extends CDKTestCase {

    /**
     * A roundtripping test to see of PDB atom customization works.
     *
     * @cdk.bug 1085912
     */
    @Test
    public void testSFBug1085912_1() throws Exception {
        String filename_pdb = "data/pdb/1CKV.pdb";
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename_pdb);

        ISimpleChemObjectReader reader = new PDBReader(ins1);
        IChemFile chemFile1 = (IChemFile) reader.read(new ChemFile());
        reader.close();
        IChemSequence seq1 = chemFile1.getChemSequence(0);
        IChemModel model1 = seq1.getChemModel(0);
        IAtomContainer container = model1.getMoleculeSet().getAtomContainer(0);
        IBioPolymer polymer1 = (IBioPolymer) container;
        int countchemFile1 = chemFile1.getChemSequenceCount();
        int countmodel1 = model1.getMoleculeSet().getAtomContainerCount();
        int countpolymer1 = polymer1.getAtomCount();

        StringWriter writer = new StringWriter();
        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.registerCustomizer(new PDBAtomCustomizer());
        cmlWriter.write(polymer1);
        cmlWriter.close();
        String cmlContent1 = writer.toString();
        System.out.println(cmlContent1.substring(0, 500));

        CMLReader reader2 = new CMLReader(new ByteArrayInputStream(cmlContent1.getBytes()));
        IChemFile chemFil2 = (IChemFile) reader2.read(new ChemFile());
        reader2.close();
        IChemSequence seq2 = chemFil2.getChemSequence(0);
        IChemModel model2 = seq2.getChemModel(0);
        PDBPolymer polymer2 = (PDBPolymer) model2.getMoleculeSet().getAtomContainer(0);

        int countchemFile2 = chemFil2.getChemSequenceCount();
        int countmodel2 = model2.getMoleculeSet().getAtomContainerCount();
        int countpolymer2 = polymer2.getAtomCount();

        Assert.assertEquals(countchemFile1, countchemFile2);
        Assert.assertEquals(countmodel1, countmodel2);
        Assert.assertEquals(countpolymer1, countpolymer2);

        writer = new StringWriter();
        cmlWriter = new CMLWriter(writer);
        cmlWriter.registerCustomizer(new PDBAtomCustomizer());
        cmlWriter.write(polymer2);
        cmlWriter.close();
        String cmlContent2 = writer.toString();
        System.out.println(cmlContent2.substring(0, 500));

        String conte1 = cmlContent1.substring(0, 1000);
        String conte2 = cmlContent2.substring(0, 1000);
        Assert.assertEquals(conte1, conte2);
    }

}
