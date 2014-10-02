/* Copyright (C) 2007  Ola Spjuth <ospjuth@users.sf.net>
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
 *
 */
package org.openscience.cdk.libio.md;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.cml.MDMoleculeConvention;
import org.openscience.cdk.libio.cml.Convertor;
import org.openscience.cdk.libio.cml.MDMoleculeCustomizer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import org.xmlcml.cml.element.CMLAtom;

/**
 * @cdk.module test-libiomd
 */
public class MDMoleculeTest extends CDKTestCase {

    private ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDMoleculeTest.class);

    /**
     * @cdk.bug 1748257
     */
    @Test
    public void testBug1748257() {

        MDMolecule mol = new MDMolecule();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("H")); // 2
        mol.addAtom(new Atom("H")); // 3
        mol.addAtom(new Atom("H")); // 4
        mol.addAtom(new Atom("H")); // 5

        mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
        mol.addBond(2, 0, IBond.Order.SINGLE); // 3
        mol.addBond(3, 0, IBond.Order.SINGLE); // 4
        mol.addBond(4, 1, IBond.Order.SINGLE); // 5
        mol.addBond(5, 1, IBond.Order.SINGLE); // 6

        Convertor convertor = new Convertor(false, "");
        CMLAtom cmlatom = convertor.cdkAtomToCMLAtom(mol, mol.getAtom(2));
        Assert.assertEquals(cmlatom.getHydrogenCount(), 0);
    }

    /**
     * Test an MDMolecule with residues and charge groups
     *
     */
    @Test
    public void testMDMolecule() {

        MDMolecule mol = new MDMolecule();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.DOUBLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.DOUBLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.DOUBLE); // 6

        //Create 2 residues
        AtomContainer ac = new AtomContainer();
        ac.addAtom(mol.getAtom(0));
        ac.addAtom(mol.getAtom(1));
        ac.addAtom(mol.getAtom(2));
        Residue res1 = new Residue(ac, 0, mol);
        res1.setName("myResidue1");
        mol.addResidue(res1);

        AtomContainer ac2 = new AtomContainer();
        ac2.addAtom(mol.getAtom(3));
        ac2.addAtom(mol.getAtom(4));
        ac2.addAtom(mol.getAtom(5));
        Residue res2 = new Residue(ac2, 1, mol);
        res2.setName("myResidue2");
        mol.addResidue(res2);

        //Test residue creation
        Assert.assertEquals(res1.getParentMolecule(), mol);
        Assert.assertEquals(res2.getParentMolecule(), mol);
        Assert.assertEquals(res1.getAtomCount(), 3);
        Assert.assertEquals(res2.getAtomCount(), 3);
        Assert.assertEquals(res1.getName(), "myResidue1");
        Assert.assertEquals(res2.getName(), "myResidue2");
        Assert.assertNotNull(mol.getResidues());
        Assert.assertEquals(mol.getResidues().size(), 2);
        Assert.assertEquals(mol.getResidues().get(0), res1);
        Assert.assertEquals(mol.getResidues().get(1), res2);

        //Create 2 chargegroups
        AtomContainer ac3 = new AtomContainer();
        ac3.addAtom(mol.getAtom(0));
        ac3.addAtom(mol.getAtom(1));
        ChargeGroup chg1 = new ChargeGroup(ac3, 0, mol);
        mol.addChargeGroup(chg1);

        AtomContainer ac4 = new AtomContainer();
        ac4.addAtom(mol.getAtom(2));
        ac4.addAtom(mol.getAtom(3));
        ac4.addAtom(mol.getAtom(4));
        ac4.addAtom(mol.getAtom(5));
        ChargeGroup chg2 = new ChargeGroup(ac4, 1, mol);
        mol.addChargeGroup(chg2);

        //Test chargegroup creation
        Assert.assertEquals(chg1.getParentMolecule(), mol);
        Assert.assertEquals(chg2.getParentMolecule(), mol);
        Assert.assertEquals(chg1.getAtomCount(), 2);
        Assert.assertEquals(chg2.getAtomCount(), 4);

        Assert.assertNotNull(mol.getChargeGroups());
        Assert.assertEquals(mol.getChargeGroups().size(), 2);
        Assert.assertEquals(mol.getChargeGroups().get(0), chg1);
        Assert.assertEquals(mol.getChargeGroups().get(1), chg2);

    }

    @Test
    public void testMDMoleculeCustomizationRoundtripping() throws Exception {
        StringWriter writer = new StringWriter();

        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.registerCustomizer(new MDMoleculeCustomizer());
        MDMolecule molecule = makeMDBenzene();
        cmlWriter.write(molecule);
        cmlWriter.close();

        String serializedMol = writer.toString();
        logger.debug("****************************** testMDMoleculeCustomizationRoundtripping()");
        logger.debug(serializedMol);
        logger.debug("******************************");
        logger.debug("****************************** testMDMoleculeCustomization Write first");
        logger.debug(serializedMol);
        logger.debug("******************************");

        CMLReader reader = new CMLReader(new ByteArrayInputStream(serializedMol.getBytes()));
        reader.registerConvention("md:mdMolecule", new MDMoleculeConvention(new ChemFile()));
        IChemFile file = (IChemFile) reader.read(new ChemFile());
        reader.close();
        List containers = ChemFileManipulator.getAllAtomContainers(file);
        Assert.assertEquals(1, containers.size());

        Object molecule2 = containers.get(0);
        Assert.assertTrue(molecule2 instanceof MDMolecule);
        MDMolecule mdMol = (MDMolecule) molecule2;

        Assert.assertEquals(6, mdMol.getAtomCount());
        Assert.assertEquals(6, mdMol.getBondCount());

        List residues = mdMol.getResidues();
        Assert.assertEquals(2, residues.size());
        Assert.assertEquals(3, ((Residue) residues.get(0)).getAtomCount());
        Assert.assertEquals(3, ((Residue) residues.get(1)).getAtomCount());
        Assert.assertEquals("myResidue1", ((Residue) residues.get(0)).getName());
        Assert.assertEquals("myResidue2", ((Residue) residues.get(1)).getName());
        Assert.assertEquals(0, ((Residue) residues.get(0)).getNumber());
        Assert.assertEquals(1, ((Residue) residues.get(1)).getNumber());

        List chargeGroup = mdMol.getChargeGroups();
        Assert.assertEquals(2, chargeGroup.size());
        Assert.assertEquals(2, ((ChargeGroup) chargeGroup.get(0)).getAtomCount());
        Assert.assertEquals(4, ((ChargeGroup) chargeGroup.get(1)).getAtomCount());
        Assert.assertNotNull(((ChargeGroup) chargeGroup.get(0)).getSwitchingAtom());
        Assert.assertEquals("a2", ((ChargeGroup) chargeGroup.get(0)).getSwitchingAtom().getID());
        Assert.assertNotNull(((ChargeGroup) chargeGroup.get(1)).getSwitchingAtom());
        Assert.assertEquals("a5", ((ChargeGroup) chargeGroup.get(1)).getSwitchingAtom().getID());

        Assert.assertEquals(2, ((ChargeGroup) chargeGroup.get(0)).getNumber());
        Assert.assertEquals(3, ((ChargeGroup) chargeGroup.get(1)).getNumber());

        writer = new StringWriter();

        cmlWriter = new CMLWriter(writer);
        cmlWriter.registerCustomizer(new MDMoleculeCustomizer());
        cmlWriter.write(mdMol);
        cmlWriter.close();

        String serializedMDMol = writer.toString();
        logger.debug("****************************** testMDMoleculeCustomizationRoundtripping()");
        logger.debug(serializedMol);
        logger.debug("******************************");
        logger.debug("****************************** testMDMoleculeCustomization Write second");
        logger.debug(serializedMDMol);
        logger.debug("******************************");

        Assert.assertEquals(serializedMol, serializedMDMol);

    }

    @Test
    public void testMDMoleculeCustomization() {
        StringWriter writer = new StringWriter();

        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.registerCustomizer(new MDMoleculeCustomizer());
        try {
            IAtomContainer molecule = makeMDBenzene();
            cmlWriter.write(molecule);
            cmlWriter.close();

        } catch (CDKException | IOException exception) {
            logger.error("Error while creating an CML2 file: ", exception.getMessage());
            logger.debug(exception);
            Assert.fail(exception.getMessage());
        }
        String cmlContent = writer.toString();
        logger.debug("****************************** testMDMoleculeCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        //        System.out.println("****************************** testMDMoleculeCustomization()");
        //        System.out.println(cmlContent);
        //        System.out.println("******************************");
        Assert.assertTrue(cmlContent.indexOf("xmlns:md") != -1);
        Assert.assertTrue(cmlContent.indexOf("md:residue\"") != -1);
        Assert.assertTrue(cmlContent.indexOf("md:resNumber\"") != -1);
        Assert.assertTrue(cmlContent.indexOf("md:chargeGroup\"") != -1);
        Assert.assertTrue(cmlContent.indexOf("md:cgNumber\"") != -1);
        Assert.assertTrue(cmlContent.indexOf("md:switchingAtom\"") != -1);
    }

    /**
     * Create a benzene molecule with 2 residues and 2 charge groups
     * @return
     */
    public MDMolecule makeMDBenzene() {

        MDMolecule mol = new MDMolecule();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.DOUBLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.DOUBLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.DOUBLE); // 6

        //Create 2 residues
        AtomContainer ac = new AtomContainer();
        ac.addAtom(mol.getAtom(0));
        ac.addAtom(mol.getAtom(1));
        ac.addAtom(mol.getAtom(2));
        Residue res1 = new Residue(ac, 0, mol);
        res1.setName("myResidue1");
        mol.addResidue(res1);

        AtomContainer ac2 = new AtomContainer();
        ac2.addAtom(mol.getAtom(3));
        ac2.addAtom(mol.getAtom(4));
        ac2.addAtom(mol.getAtom(5));
        Residue res2 = new Residue(ac2, 1, mol);
        res2.setName("myResidue2");
        mol.addResidue(res2);

        //Create 2 chargegroups
        AtomContainer ac3 = new AtomContainer();
        ac3.addAtom(mol.getAtom(0));
        ac3.addAtom(mol.getAtom(1));
        ChargeGroup chg1 = new ChargeGroup(ac3, 2, mol);
        chg1.setSwitchingAtom(mol.getAtom(1));
        mol.addChargeGroup(chg1);

        AtomContainer ac4 = new AtomContainer();
        ac4.addAtom(mol.getAtom(2));
        ac4.addAtom(mol.getAtom(3));
        ac4.addAtom(mol.getAtom(4));
        ac4.addAtom(mol.getAtom(5));
        ChargeGroup chg2 = new ChargeGroup(ac4, 3, mol);
        chg2.setSwitchingAtom(mol.getAtom(4));
        mol.addChargeGroup(chg2);

        return mol;

    }

}
