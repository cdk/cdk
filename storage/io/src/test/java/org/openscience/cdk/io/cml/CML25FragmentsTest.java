/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io.cml;

import java.io.ByteArrayInputStream;

import org.junit.Assert;
import org.junit.Ignore;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.silent.ChemFile;

/**
 * Atomic tests for reading CML documents. All tested CML strings are valid CML 2.5,
 * as can be determined in cdk/src/org.openscience.cdk/io/cml/cml25TestFramework.xml</code>.
 *
 * @cdk.module test-io
 *
 * @author Egon Willighagen &lt;egonw@sci.kun.nl&gt;
 */
public class CML25FragmentsTest extends CDKTestCase {

    @Ignore("Functionality not yet implemented")
    public void testIsotopeRef() throws Exception {
        String cmlString = "<cml>" + "  <isotopeList>" + "    <isotope id='H1' number='1' elementType='H'>"
                + "      <scalar dictRef='bo:relativeAbundance'>99.9885</scalar>"
                + "      <scalar dictRef='bo:exactMass' errorValue='0.0001E-6'>1.007825032</scalar>" + "    </isotope>"
                + "  </isotopeList>" + "  <molecule>" + "    <atomArray>"
                + "      <atom id='a1' elementType='H' isotopeRef='H1'/>" + "    </atomArray>" + "  </molecule>"
                + "</cml>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assert.assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assert.assertEquals("a1", atom.getID());
        Assert.assertNotNull(atom.getNaturalAbundance());
        Assert.assertEquals(99.9885, atom.getNaturalAbundance(), 0.0001);
        Assert.assertNotNull(atom.getExactMass());
        Assert.assertEquals(1.007825032, atom.getExactMass(), 0.0000001);
    }

    private IChemFile parseCMLString(String cmlString) throws Exception {
        IChemFile chemFile = null;
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));
        chemFile = (IChemFile) reader.read(new ChemFile());
        reader.close();
        return chemFile;
    }

    /**
     * Tests whether the file is indeed a single molecule file
     */
    private IAtomContainer checkForSingleMoleculeFile(IChemFile chemFile) {
        return checkForXMoleculeFile(chemFile, 1);
    }

    private IAtomContainer checkForXMoleculeFile(IChemFile chemFile, int numberOfMolecules) {
        Assert.assertNotNull(chemFile);

        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
        IChemSequence seq = chemFile.getChemSequence(0);
        Assert.assertNotNull(seq);

        Assert.assertEquals(seq.getChemModelCount(), 1);
        IChemModel model = seq.getChemModel(0);
        Assert.assertNotNull(model);

        IAtomContainerSet moleculeSet = model.getMoleculeSet();
        Assert.assertNotNull(moleculeSet);

        Assert.assertEquals(moleculeSet.getAtomContainerCount(), numberOfMolecules);
        IAtomContainer mol = null;
        for (int i = 0; i < numberOfMolecules; i++) {
            mol = moleculeSet.getAtomContainer(i);
            Assert.assertNotNull(mol);
        }
        return mol;
    }
}
