/* Copyright (C) 2006-2007  Egon Willighagen <ewilligh@uni-koeln.de>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.graph.invariant;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.smiles.InvPair;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Checks the functionality of the CanonicalLabeler.
 *
 * @cdk.module test-standard
 */
@Category(SlowTest.class)
// CanonicalLabeler is deprecated (slow)
public class CanonicalLabelerTest extends CDKTestCase {

    private SmilesParser     parser;
    private CanonicalLabeler labeler;

    public CanonicalLabelerTest() {
        super();
    }

    @Before
    public void setUp() {
        parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        labeler = new CanonicalLabeler();
    }

    @Test
    public void testCanonicalLabeler() {
        // assume setup worked
        Assert.assertNotNull(labeler);
    }

    @Test
    public void testCanonLabel_IAtomContainer() throws Exception {
        IAtomContainer molecule = parser.parseSmiles("CC(=O)CBr");

        labeler.canonLabel(molecule);
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            Assert.assertNotNull(atom.getProperty(InvPair.CANONICAL_LABEL));
        }

        Assert.assertEquals(3, ((Long) molecule.getAtom(0).getProperty(InvPair.CANONICAL_LABEL)).intValue());
        Assert.assertEquals(2, ((Long) molecule.getAtom(1).getProperty(InvPair.CANONICAL_LABEL)).intValue());
        Assert.assertEquals(1, ((Long) molecule.getAtom(2).getProperty(InvPair.CANONICAL_LABEL)).intValue());
        Assert.assertEquals(4, ((Long) molecule.getAtom(3).getProperty(InvPair.CANONICAL_LABEL)).intValue());
        Assert.assertEquals(5, ((Long) molecule.getAtom(4).getProperty(InvPair.CANONICAL_LABEL)).intValue());
    }

    /**
     * Ordering of original should not matter, so the same SMILES
     * with a different atom order as the test above.
     *
     * @throws Exception
     * @see testSomeMolecule()
     */
    @Test
    public void testSomeMoleculeWithDifferentStartingOrder() throws Exception {
        IAtomContainer molecule = parser.parseSmiles("O=C(C)CBr");
        labeler.canonLabel(molecule);
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            Assert.assertNotNull(atom.getProperty(InvPair.CANONICAL_LABEL));
        }
        Assert.assertEquals(1, ((Long) molecule.getAtom(0).getProperty(InvPair.CANONICAL_LABEL)).intValue());
        Assert.assertEquals(2, ((Long) molecule.getAtom(1).getProperty(InvPair.CANONICAL_LABEL)).intValue());
        Assert.assertEquals(3, ((Long) molecule.getAtom(2).getProperty(InvPair.CANONICAL_LABEL)).intValue());
        Assert.assertEquals(4, ((Long) molecule.getAtom(3).getProperty(InvPair.CANONICAL_LABEL)).intValue());
        Assert.assertEquals(5, ((Long) molecule.getAtom(4).getProperty(InvPair.CANONICAL_LABEL)).intValue());
    }

    /**
     * @cdk.bug 1014344
     */
    @Test
    public void testStabilityAfterRoundtrip() throws Exception {
        String filename = "data/mdl/bug1014344-1.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins, Mode.STRICT);
        IAtomContainer mol1 = reader.read(new AtomContainer());
        addImplicitHydrogens(mol1);
        StringWriter output = new StringWriter();
        CMLWriter cmlWriter = new CMLWriter(output);
        cmlWriter.write(mol1);
        CMLReader cmlreader = new CMLReader(new ByteArrayInputStream(output.toString().getBytes()));
        IAtomContainer mol2 = ((IChemFile) cmlreader.read(new ChemFile())).getChemSequence(0).getChemModel(0)
                .getMoleculeSet().getAtomContainer(0);
        addImplicitHydrogens(mol2);

        labeler.canonLabel(mol1);
        labeler.canonLabel(mol2);
        Iterator<IAtom> atoms1 = mol1.atoms().iterator();
        Iterator<IAtom> atoms2 = mol2.atoms().iterator();
        while (atoms1.hasNext()) {
            IAtom atom1 = atoms1.next();
            IAtom atom2 = atoms2.next();
            Assert.assertEquals(atom1.getProperty(InvPair.CANONICAL_LABEL), atom2.getProperty(InvPair.CANONICAL_LABEL));
        }
    }

    /**
     * Convenience method that perceives atom types (CDK scheme) and
     * adds implicit hydrogens accordingly. It does not create 2D or 3D
     * coordinates for the new hydrogens.
     *
     * @param container to which implicit hydrogens are added.
     */
    @Override
    protected void addImplicitHydrogens(IAtomContainer container) throws Exception {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(container.getBuilder());
        Iterator<IAtom> atoms = container.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            IAtomType type = matcher.findMatchingAtomType(container, atom);
            AtomTypeManipulator.configure(atom, type);
        }
        CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(container.getBuilder());
        hAdder.addImplicitHydrogens(container);
    }

    /**
     * @cdk.bug 2944519
     */
    @Test
    public void testBug2944519() {
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        ac.addAtom(ac.getBuilder().newInstance(IAtom.class, "C"));
        ac.addAtom(ac.getBuilder().newInstance(IAtom.class, "O"));
        ac.addBond(0, 1, IBond.Order.SINGLE);
        CanonicalLabeler canLabler = new CanonicalLabeler();
        canLabler.canonLabel(ac);
        IAtomContainer ac2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        ac2.addAtom(ac2.getBuilder().newInstance(IAtom.class, "O"));
        ac2.addAtom(ac2.getBuilder().newInstance(IAtom.class, "C"));
        ac2.addBond(0, 1, IBond.Order.SINGLE);
        canLabler.canonLabel(ac2);
        Assert.assertSame(ac.getAtom(0).getProperty(InvPair.CANONICAL_LABEL),
                ac2.getAtom(1).getProperty(InvPair.CANONICAL_LABEL));
        Assert.assertSame(ac.getAtom(1).getProperty(InvPair.CANONICAL_LABEL),
                ac2.getAtom(0).getProperty(InvPair.CANONICAL_LABEL));
    }
}
