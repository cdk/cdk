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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.test.CDKTestCase;
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
class MDMoleculeTest extends CDKTestCase {

    private final ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDMoleculeTest.class);

    /**
     * @cdk.bug 1748257
     */
    @Test
    void testBug1748257() {

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
        Assertions.assertEquals(cmlatom.getHydrogenCount(), 0);
    }

    /**
     * Test an MDMolecule with residues and charge groups
     *
     */
    @Test
    void testMDMolecule() {

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
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac.addAtom(mol.getAtom(0));
        ac.addAtom(mol.getAtom(1));
        ac.addAtom(mol.getAtom(2));
        Residue res1 = new Residue(ac, 0, mol);
        res1.setName("myResidue1");
        mol.addResidue(res1);

        IAtomContainer ac2 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac2.addAtom(mol.getAtom(3));
        ac2.addAtom(mol.getAtom(4));
        ac2.addAtom(mol.getAtom(5));
        Residue res2 = new Residue(ac2, 1, mol);
        res2.setName("myResidue2");
        mol.addResidue(res2);

        //Test residue creation
        Assertions.assertEquals(res1.getParentMolecule(), mol);
        Assertions.assertEquals(res2.getParentMolecule(), mol);
        Assertions.assertEquals(res1.getAtomCount(), 3);
        Assertions.assertEquals(res2.getAtomCount(), 3);
        Assertions.assertEquals(res1.getName(), "myResidue1");
        Assertions.assertEquals(res2.getName(), "myResidue2");
        Assertions.assertNotNull(mol.getResidues());
        Assertions.assertEquals(mol.getResidues().size(), 2);
        Assertions.assertEquals(mol.getResidues().get(0), res1);
        Assertions.assertEquals(mol.getResidues().get(1), res2);

        //Create 2 chargegroups
        IAtomContainer ac3 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac3.addAtom(mol.getAtom(0));
        ac3.addAtom(mol.getAtom(1));
        ChargeGroup chg1 = new ChargeGroup(ac3, 0, mol);
        mol.addChargeGroup(chg1);

        IAtomContainer ac4 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac4.addAtom(mol.getAtom(2));
        ac4.addAtom(mol.getAtom(3));
        ac4.addAtom(mol.getAtom(4));
        ac4.addAtom(mol.getAtom(5));
        ChargeGroup chg2 = new ChargeGroup(ac4, 1, mol);
        mol.addChargeGroup(chg2);

        //Test chargegroup creation
        Assertions.assertEquals(chg1.getParentMolecule(), mol);
        Assertions.assertEquals(chg2.getParentMolecule(), mol);
        Assertions.assertEquals(chg1.getAtomCount(), 2);
        Assertions.assertEquals(chg2.getAtomCount(), 4);

        Assertions.assertNotNull(mol.getChargeGroups());
        Assertions.assertEquals(mol.getChargeGroups().size(), 2);
        Assertions.assertEquals(mol.getChargeGroups().get(0), chg1);
        Assertions.assertEquals(mol.getChargeGroups().get(1), chg2);

    }

    @Test
    void testMDMoleculeCustomizationRoundtripping() throws Exception {
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
        IChemFile file = reader.read(new ChemFile());
        reader.close();
        List containers = ChemFileManipulator.getAllAtomContainers(file);
        Assertions.assertEquals(1, containers.size());

        Object molecule2 = containers.get(0);
        Assertions.assertTrue(molecule2 instanceof MDMolecule);
        MDMolecule mdMol = (MDMolecule) molecule2;

        Assertions.assertEquals(6, mdMol.getAtomCount());
        Assertions.assertEquals(6, mdMol.getBondCount());

        List residues = mdMol.getResidues();
        Assertions.assertEquals(2, residues.size());
        Assertions.assertEquals(3, ((Residue) residues.get(0)).getAtomCount());
        Assertions.assertEquals(3, ((Residue) residues.get(1)).getAtomCount());
        Assertions.assertEquals("myResidue1", ((Residue) residues.get(0)).getName());
        Assertions.assertEquals("myResidue2", ((Residue) residues.get(1)).getName());
        Assertions.assertEquals(0, ((Residue) residues.get(0)).getNumber());
        Assertions.assertEquals(1, ((Residue) residues.get(1)).getNumber());

        List chargeGroup = mdMol.getChargeGroups();
        Assertions.assertEquals(2, chargeGroup.size());
        Assertions.assertEquals(2, ((ChargeGroup) chargeGroup.get(0)).getAtomCount());
        Assertions.assertEquals(4, ((ChargeGroup) chargeGroup.get(1)).getAtomCount());
        Assertions.assertNotNull(((ChargeGroup) chargeGroup.get(0)).getSwitchingAtom());
        Assertions.assertEquals("a2", ((ChargeGroup) chargeGroup.get(0)).getSwitchingAtom().getID());
        Assertions.assertNotNull(((ChargeGroup) chargeGroup.get(1)).getSwitchingAtom());
        Assertions.assertEquals("a5", ((ChargeGroup) chargeGroup.get(1)).getSwitchingAtom().getID());

        Assertions.assertEquals(2, ((ChargeGroup) chargeGroup.get(0)).getNumber());
        Assertions.assertEquals(3, ((ChargeGroup) chargeGroup.get(1)).getNumber());

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

        Assertions.assertEquals(serializedMol, serializedMDMol);

    }

    @Test
    void testMDMoleculeCustomization() {
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
            Assertions.fail(exception.getMessage());
        }
        String cmlContent = writer.toString();
        logger.debug("****************************** testMDMoleculeCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        //        System.out.println("****************************** testMDMoleculeCustomization()");
        //        System.out.println(cmlContent);
        //        System.out.println("******************************");
        Assertions.assertTrue(cmlContent.contains("xmlns:md"));
        Assertions.assertTrue(cmlContent.contains("md:residue\""));
        Assertions.assertTrue(cmlContent.contains("md:resNumber\""));
        Assertions.assertTrue(cmlContent.contains("md:chargeGroup\""));
        Assertions.assertTrue(cmlContent.contains("md:cgNumber\""));
        Assertions.assertTrue(cmlContent.contains("md:switchingAtom\""));
    }

    /**
     * Create a benzene molecule with 2 residues and 2 charge groups
     * @return
     */
    MDMolecule makeMDBenzene() {

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
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac.addAtom(mol.getAtom(0));
        ac.addAtom(mol.getAtom(1));
        ac.addAtom(mol.getAtom(2));
        Residue res1 = new Residue(ac, 0, mol);
        res1.setName("myResidue1");
        mol.addResidue(res1);

        IAtomContainer ac2 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac2.addAtom(mol.getAtom(3));
        ac2.addAtom(mol.getAtom(4));
        ac2.addAtom(mol.getAtom(5));
        Residue res2 = new Residue(ac2, 1, mol);
        res2.setName("myResidue2");
        mol.addResidue(res2);

        //Create 2 chargegroups
        IAtomContainer ac3 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        ac3.addAtom(mol.getAtom(0));
        ac3.addAtom(mol.getAtom(1));
        ChargeGroup chg1 = new ChargeGroup(ac3, 2, mol);
        chg1.setSwitchingAtom(mol.getAtom(1));
        mol.addChargeGroup(chg1);

        IAtomContainer ac4 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
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
