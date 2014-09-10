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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.templates.TestMoleculeFactory;

/**
 * TestCase for the PDBWriter class.
 *
 * @cdk.module test-pdb
 *
 * @author      Egon Willighagen
 * @cdk.created 2001-08-09
 */
public class PDBWriterTest extends ChemObjectIOTest {

    private static IChemObjectBuilder builder;

    @BeforeClass
    public static void setup() {
        builder = DefaultChemObjectBuilder.getInstance();
        setChemObjectIO(new MDLRXNWriter());
    }

    @Test
    public void testRoundTrip() throws Exception {
        StringWriter sWriter = new StringWriter();
        PDBWriter writer = new PDBWriter(sWriter);

        ICrystal crystal = builder.newInstance(ICrystal.class);
        crystal.setA(new Vector3d(0, 1, 0));
        crystal.setB(new Vector3d(1, 0, 0));
        crystal.setC(new Vector3d(0, 0, 2));

        IAtom atom = builder.newInstance(IAtom.class, "C");
        atom.setPoint3d(new Point3d(0.1, 0.1, 0.3));
        crystal.addAtom(atom);

        writer.write(crystal);
        writer.close();

        String output = sWriter.toString();
        Assert.assertNotNull(output);
        Assert.assertTrue(output.length() > 0);

        PDBReader reader = new PDBReader();
        ChemFile chemFile = (ChemFile) reader.read(new ChemFile());
        reader.close();

        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        IChemSequence sequence = chemFile.getChemSequence(0);
        Assert.assertEquals(1, sequence.getChemModelCount());
        IChemModel chemModel = sequence.getChemModel(0);
        Assert.assertNotNull(chemModel);

        // can't do further testing as the PDBReader does not read
        // Crystal structures :(
    }

    @Test
    public void testRoundTrip_fractionalCoordinates() throws Exception {
        StringWriter sWriter = new StringWriter();
        PDBWriter writer = new PDBWriter(sWriter);

        Crystal crystal = new Crystal();
        crystal.setA(new Vector3d(0, 1, 0));
        crystal.setB(new Vector3d(1, 0, 0));
        crystal.setC(new Vector3d(0, 0, 2));

        IAtom atom = new Atom("C");
        atom.setFractionalPoint3d(new Point3d(0.1, 0.1, 0.3));
        crystal.addAtom(atom);

        writer.write(crystal);
        writer.close();

        String output = sWriter.toString();
        Assert.assertNotNull(output);
        Assert.assertTrue(output.length() > 0);

        PDBReader reader = new PDBReader();
        ChemFile chemFile = (ChemFile) reader.read(new ChemFile());
        reader.close();

        Assert.assertNotNull(chemFile);
        Assert.assertEquals(1, chemFile.getChemSequenceCount());
        IChemSequence sequence = chemFile.getChemSequence(0);
        Assert.assertEquals(1, sequence.getChemModelCount());
        IChemModel chemModel = sequence.getChemModel(0);
        Assert.assertNotNull(chemModel);

        // can't do further testing as the PDBReader does not read
        // Crystal structures :(
    }

    private IAtomContainer singleAtomMolecule() {
        return singleAtomMolecule("");
    }

    private IAtomContainer singleAtomMolecule(String id) {
        return singleAtomMolecule(id, null);
    }

    private IAtomContainer singleAtomMolecule(String id, Integer formalCharge) {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C", new Point3d(0.0, 0.0, 0.0));
        mol.addAtom(atom);
        mol.setID(id);
        if (formalCharge != null) {
            atom.setFormalCharge(formalCharge);
        }
        return mol;
    }

    private IAtomContainer singleBondMolecule() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C", new Point3d(0.0, 0.0, 0.0)));
        mol.addAtom(new Atom("O", new Point3d(1.0, 1.0, 1.0)));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        return mol;
    }

    private String getAsString(IAtomContainer mol) throws CDKException, IOException {
        StringWriter stringWriter = new StringWriter();
        PDBWriter writer = new PDBWriter(stringWriter);
        writer.writeMolecule(mol);
        writer.close();
        return stringWriter.toString();
    }

    private String[] getAsStringArray(IAtomContainer mol) throws CDKException, IOException {
        return getAsString(mol).split(System.getProperty("line.separator"));
    }

    @Test
    public void writeAsHET() throws CDKException, IOException {
        IAtomContainer mol = singleAtomMolecule();
        StringWriter stringWriter = new StringWriter();
        PDBWriter writer = new PDBWriter(stringWriter);
        writer.getSetting("WriteAsHET").setSetting("true");
        writer.writeMolecule(mol);
        writer.close();
        String asString = stringWriter.toString();
        Assert.assertTrue(asString.indexOf("HETATM") != -1);
    }

    @Test
    public void writeAsATOM() throws CDKException, IOException {
        IAtomContainer mol = singleAtomMolecule();
        StringWriter stringWriter = new StringWriter();
        PDBWriter writer = new PDBWriter(stringWriter);
        writer.getSetting("WriteAsHET").setSetting("false");
        writer.writeMolecule(mol);
        writer.close();
        String asString = stringWriter.toString();
        Assert.assertTrue(asString.indexOf("ATOM") != -1);
    }

    @Test
    public void writeMolID() throws CDKException, IOException {
        IAtomContainer mol = singleAtomMolecule("ZZZ");
        Assert.assertTrue(getAsString(mol).indexOf("ZZZ") != -1);
    }

    @Test
    public void writeNullMolID() throws CDKException, IOException {
        IAtomContainer mol = singleAtomMolecule(null);
        Assert.assertTrue(getAsString(mol).indexOf("MOL") != -1);
    }

    @Test
    public void writeEmptyStringMolID() throws CDKException, IOException {
        IAtomContainer mol = singleAtomMolecule("");
        Assert.assertTrue(getAsString(mol).indexOf("MOL") != -1);
    }

    @Test
    public void writeChargedAtom() throws CDKException, IOException {
        IAtomContainer mol = singleAtomMolecule("", 1);
        String[] lines = getAsStringArray(mol);
        Assert.assertTrue(lines[lines.length - 2].endsWith("+1"));
    }

    @Test
    public void writeMoleculeWithBond() throws CDKException, IOException {
        IAtomContainer mol = singleBondMolecule();
        String[] lines = getAsStringArray(mol);
        String lastLineButTwo = lines[lines.length - 3];
        String lastLineButOne = lines[lines.length - 2];
        Assert.assertEquals("CONECT    1    2", lastLineButTwo);
        Assert.assertEquals("CONECT    2    1", lastLineButOne);
    }

    private void setCoordinatesToZero(IAtomContainer mol) {
        for (IAtom atom : mol.atoms()) {
            atom.setPoint3d(new Point3d(0.0, 0.0, 0.0));
        }
    }

    @Test
    public void molfactoryRoundtripTest() throws Exception {
        IAtomContainer original = TestMoleculeFactory.makePyrrole();
        setCoordinatesToZero(original);
        StringWriter stringWriter = new StringWriter();
        PDBWriter writer = new PDBWriter(stringWriter);
        writer.writeMolecule(original);
        writer.close();
        String output = stringWriter.toString();
        PDBReader reader = new PDBReader(new StringReader(output));
        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
        reader.close();
        IAtomContainer reconstructed = chemFile.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
        Assert.assertEquals(original.getAtomCount(), reconstructed.getAtomCount());
        Assert.assertEquals(original.getBondCount(), reconstructed.getBondCount());
    }
}
