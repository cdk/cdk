/* $Revision: 7636 $ $Author: ospjuth $ $Date: 2007-01-04 17:46:10 +0000 (Thu, 04 Jan 2007) $
 *
 * Copyright (C) 2007  Ola Spjuth <ospjuth@users.sf.net>
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
package org.openscience.cdk.test.libio.md;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.libio.md.ChargeGroup;
import org.openscience.cdk.libio.md.MDMolecule;
import org.openscience.cdk.libio.md.Residue;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * @cdk.module test-libiomd
 */
public class MDMoleculeTest extends CDKTestCase {

    private LoggingTool logger;

    public MDMoleculeTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(MDMoleculeTest.class);
    }

    /**
     * Test an MDMolecule with residues and chargegroups
     *
     */
    public void testMDMolecule() {
    	
    	MDMolecule mol=new MDMolecule();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5

        mol.addBond(0, 1, 1); // 1
        mol.addBond(1, 2, 2); // 2
        mol.addBond(2, 3, 1); // 3
        mol.addBond(3, 4, 2); // 4
        mol.addBond(4, 5, 1); // 5
        mol.addBond(5, 0, 2); // 6

        //Create 2 residues
        AtomContainer ac= new AtomContainer();
        ac.addAtom(mol.getAtom(0));
        ac.addAtom(mol.getAtom(1));
        ac.addAtom(mol.getAtom(2));
        Residue res1=new Residue(ac,0,mol);
        res1.setName("myResidue1");
        mol.addResidue(res1);

        AtomContainer ac2= new AtomContainer();
        ac2.addAtom(mol.getAtom(3));
        ac2.addAtom(mol.getAtom(4));
        ac2.addAtom(mol.getAtom(5));
        Residue res2=new Residue(ac2,1,mol);
        res2.setName("myResidue2");
        mol.addResidue(res2);
                 
        //Test residue creation
        assertEquals(res1.getParentMolecule(),mol);
        assertEquals(res2.getParentMolecule(),mol);
        assertEquals(res1.getAtomCount(), 3);
        assertEquals(res2.getAtomCount(), 3);
        assertEquals(res1.getName(), "myResidue1");
        assertEquals(res2.getName(), "myResidue2");
        assertNotNull(mol.getResidues());
        assertEquals(mol.getResidues().size(),2);
        assertEquals(mol.getResidues().get(0), res1);
        assertEquals(mol.getResidues().get(1), res2);

        //Create 2 chargegroups
        AtomContainer ac3= new AtomContainer();
        ac3.addAtom(mol.getAtom(0));
        ac3.addAtom(mol.getAtom(1));
        ChargeGroup chg1=new ChargeGroup(ac3,0,mol);
        mol.addChargeGroup(chg1);

        AtomContainer ac4= new AtomContainer();
        ac4.addAtom(mol.getAtom(2));
        ac4.addAtom(mol.getAtom(3));
        ac4.addAtom(mol.getAtom(4));
        ac4.addAtom(mol.getAtom(5));
        ChargeGroup chg2=new ChargeGroup(ac4,1,mol);
        mol.addChargeGroup(chg2);

        //Test chargegroup creation
        assertEquals(chg1.getParentMolecule(),mol);
        assertEquals(chg2.getParentMolecule(),mol);
        assertEquals(chg1.getAtomCount(), 2);
        assertEquals(chg2.getAtomCount(), 4);

        assertNotNull(mol.getChargeGroups());
        assertEquals(mol.getChargeGroups().size(),2);
        assertEquals(mol.getChargeGroups().get(0), chg1);
        assertEquals(mol.getChargeGroups().get(1), chg2);

    }

    public void testMDMoleculeCustomization() {
        StringWriter writer = new StringWriter();

        CMLWriter cmlWriter = new CMLWriter(writer);
        try {
            IMolecule molecule=makeMDBenzene();
            cmlWriter.write(molecule);

        } catch (Exception exception) {
            logger.error("Error while creating an CML2 file: ", exception.getMessage());
            logger.debug(exception);
            fail(exception.getMessage());
        }
        String cmlContent = writer.toString();
        logger.debug("****************************** testMDMoleculeCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        System.out.println("****************************** testMDMoleculeCustomization()");
        System.out.println(cmlContent);
        System.out.println("******************************");
        assertTrue(cmlContent.indexOf("xmlns:md") != -1);
        assertTrue(cmlContent.indexOf("md:residue\"") != -1);
        assertTrue(cmlContent.indexOf("md:resNo\"") != -1);
        assertTrue(cmlContent.indexOf("md:chargeGroup\"") != -1);
        assertTrue(cmlContent.indexOf("md:cgNo\"") != -1);
        assertTrue(cmlContent.indexOf("md:switchingAtom\"") != -1);
    }

    public void testMDMoleculeCustomizationRoundtripping() {
        StringWriter writer = new StringWriter();

        CMLWriter cmlWriter = new CMLWriter(writer);
        try {
            MDMolecule molecule=makeMDBenzene();
            cmlWriter.write(molecule);

            String serializedMol=writer.toString();
            
            CMLReader reader = new CMLReader(new ByteArrayInputStream(serializedMol.getBytes()));
            IChemFile file = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());

            if (!(file instanceof MDMolecule)) {
            	fail();
			}
        	MDMolecule molecule2 = (MDMolecule) file;

        	assertEquals(molecule.getChargeGroups().size(), molecule2.getChargeGroups().size());
        	assertEquals(molecule.getResidues().size(), molecule2.getResidues().size());
            
        } catch (Exception exception) {
            logger.error("Error while creating an CML2 file: ", exception.getMessage());
            logger.debug(exception);
            fail(exception.getMessage());
        }
        String cmlContent = writer.toString();
        logger.debug("****************************** testMDMoleculeCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        System.out.println("****************************** testMDMoleculeCustomization()");
        System.out.println(cmlContent);
        System.out.println("******************************");
        assertTrue(cmlContent.indexOf("xmlns:md") != -1);
        assertTrue(cmlContent.indexOf("md:residue\"") != -1);
        assertTrue(cmlContent.indexOf("md:chargeGroup\"") != -1);
        assertTrue(cmlContent.indexOf("md:cgNumber\"") != -1);
        assertTrue(cmlContent.indexOf("md:switchingAtom\"") != -1);
    }

    
    /**
     * Create a benzene molecule with 2 residues and 2 chargegroups
     * @return
     */
    public MDMolecule makeMDBenzene(){
    	
    	MDMolecule mol=new MDMolecule();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5

        mol.addBond(0, 1, 1); // 1
        mol.addBond(1, 2, 2); // 2
        mol.addBond(2, 3, 1); // 3
        mol.addBond(3, 4, 2); // 4
        mol.addBond(4, 5, 1); // 5
        mol.addBond(5, 0, 2); // 6

        //Create 2 residues
        AtomContainer ac= new AtomContainer();
        ac.addAtom(mol.getAtom(0));
        ac.addAtom(mol.getAtom(1));
        ac.addAtom(mol.getAtom(2));
        Residue res1=new Residue(ac,0,mol);
        res1.setName("myResidue1");
        mol.addResidue(res1);

        AtomContainer ac2= new AtomContainer();
        ac2.addAtom(mol.getAtom(3));
        ac2.addAtom(mol.getAtom(4));
        ac2.addAtom(mol.getAtom(5));
        Residue res2=new Residue(ac2,1,mol);
        res2.setName("myResidue2");
        mol.addResidue(res2);
         
        //Create 2 chargegroups
        AtomContainer ac3= new AtomContainer();
        ac3.addAtom(mol.getAtom(0));
        ac3.addAtom(mol.getAtom(1));
        ChargeGroup chg1=new ChargeGroup(ac3,0,mol);
        chg1.setSwitchingAtom(mol.getAtom(1));
        mol.addChargeGroup(chg1);

        AtomContainer ac4= new AtomContainer();
        ac4.addAtom(mol.getAtom(2));
        ac4.addAtom(mol.getAtom(3));
        ac4.addAtom(mol.getAtom(4));
        ac4.addAtom(mol.getAtom(5));
        ChargeGroup chg2=new ChargeGroup(ac4,1,mol);
        chg2.setSwitchingAtom(mol.getAtom(4));
        mol.addChargeGroup(chg2);

        return mol;
        
    }
    
}
