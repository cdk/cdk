/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2006-2007  Rajarshi Guha <rajarshi@users.sf.net>
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
 */
package org.openscience.cdk.smiles;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.nonotify.NNAtom;
import org.openscience.cdk.nonotify.NNBond;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.smiles.DeduceBondSystemTool;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *
 * @author         Rajarshi Guha
 * @cdk.created    2006-09-18
 * @cdk.module     test-smiles
 */
public class DeduceBondSystemToolTest extends NewCDKTestCase {

	private static DeduceBondSystemTool dbst;
	
	@BeforeClass public static void setup() {
		dbst = new DeduceBondSystemTool();
	}
	
	@Test(timeout=1000) 
	public void testPyrrole() throws Exception {
        String smiles = "c2ccc3n([H])c1ccccc1c3(c2)";
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule molecule = smilesParser.parseSmiles(smiles);
        
        molecule = dbst.fixAromaticBondOrders(molecule);
        Assert.assertNotNull(molecule);

        molecule = (IMolecule) AtomContainerManipulator.removeHydrogens(molecule);
        for (int i = 0; i < molecule.getBondCount(); i++) {
        	IBond bond = molecule.getBond(i);
        	Assert.assertTrue(bond.getFlag(CDKConstants.ISAROMATIC));
        }
    }

	/**
	 * @cdk.inchi InChI=1/C6H4O2/c7-5-1-2-6(8)4-3-5/h1-4H 
	 */
	@Test public void xtestQuinone() throws Exception {
		IMolecule enol = new NNMolecule();
		
		// atom block
		IAtom atom1 = new NNAtom(Elements.CARBON);
		atom1.setHybridization(Hybridization.SP2);
		IAtom atom2 = new NNAtom(Elements.CARBON);
		atom2.setHybridization(Hybridization.SP2);
		IAtom atom3 = new NNAtom(Elements.CARBON);
		atom3.setHybridization(Hybridization.SP2);
		IAtom atom4 = new NNAtom(Elements.CARBON);
		atom3.setHybridization(Hybridization.SP2);
		IAtom atom5 = new NNAtom(Elements.CARBON);
		atom3.setHybridization(Hybridization.SP2);
		IAtom atom6 = new NNAtom(Elements.CARBON);
		atom3.setHybridization(Hybridization.SP2);
		IAtom atom7 = new NNAtom(Elements.OXYGEN);
		atom3.setHybridization(Hybridization.SP2);
		IAtom atom8 = new NNAtom(Elements.OXYGEN);
		atom3.setHybridization(Hybridization.SP2);
		
		// bond block
		IBond bond1 = new NNBond(atom1, atom2);
		IBond bond2 = new NNBond(atom2, atom3);
		IBond bond3 = new NNBond(atom3, atom4);
		IBond bond4 = new NNBond(atom4, atom5);
		IBond bond5 = new NNBond(atom5, atom6);
		IBond bond6 = new NNBond(atom6, atom1);
		IBond bond7 = new NNBond(atom7, atom1);
		IBond bond8 = new NNBond(atom8, atom4);
		
		enol.addAtom(atom1);
		enol.addAtom(atom2);
		enol.addAtom(atom3);
		enol.addAtom(atom4);
		enol.addAtom(atom5);
		enol.addAtom(atom6);
		enol.addAtom(atom7);
		enol.addAtom(atom8);
		enol.addBond(bond1);
		enol.addBond(bond2);
		enol.addBond(bond3);
		enol.addBond(bond4);
		enol.addBond(bond5);
		enol.addBond(bond6);
		enol.addBond(bond7);
		enol.addBond(bond8);
		
		// now have the algorithm have a go at it
		enol = dbst.fixAromaticBondOrders(enol);
        Assert.assertNotNull(enol);
        
		// now check whether it did the right thing
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE, bond1.getOrder());
		Assert.assertEquals(CDKConstants.BONDORDER_DOUBLE, bond2.getOrder());
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE, bond3.getOrder());
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE, bond4.getOrder());
		Assert.assertEquals(CDKConstants.BONDORDER_DOUBLE, bond5.getOrder());
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE, bond6.getOrder());
		Assert.assertEquals(CDKConstants.BONDORDER_DOUBLE, bond7.getOrder());
		Assert.assertEquals(CDKConstants.BONDORDER_DOUBLE, bond8.getOrder());
	}

	/**
	 * @cdk.inchi InChI=1/C4H5N/c1-2-4-5-3-1/h1-5H 
	 */
	@Test public void xtestPyrrole() throws Exception {
		IMolecule enol = new NNMolecule();
		
		// atom block
		IAtom atom1 = new NNAtom(Elements.CARBON);
		atom1.setHybridization(Hybridization.SP2);
		IAtom atom2 = new NNAtom(Elements.CARBON);
		atom2.setHybridization(Hybridization.SP2);
		IAtom atom3 = new NNAtom(Elements.CARBON);
		atom3.setHybridization(Hybridization.SP2);
		IAtom atom4 = new NNAtom(Elements.CARBON);
		atom3.setHybridization(Hybridization.SP2);
		IAtom atom5 = new NNAtom(Elements.NITROGEN);
		atom3.setHybridization(Hybridization.SP2);
		
		// bond block
		IBond bond1 = new NNBond(atom1, atom2);
		IBond bond2 = new NNBond(atom2, atom3);
		IBond bond3 = new NNBond(atom3, atom4);
		IBond bond4 = new NNBond(atom4, atom5);
		IBond bond5 = new NNBond(atom5, atom1);
		
		enol.addAtom(atom1);
		enol.addAtom(atom2);
		enol.addAtom(atom3);
		enol.addAtom(atom4);
		enol.addAtom(atom5);
		enol.addBond(bond1);
		enol.addBond(bond2);
		enol.addBond(bond3);
		enol.addBond(bond4);
		enol.addBond(bond5);
		
		// now have the algorithm have a go at it
		enol = dbst.fixAromaticBondOrders(enol);
        Assert.assertNotNull(enol);
		
		// now check whether it did the right thing
		Assert.assertEquals(CDKConstants.BONDORDER_DOUBLE, bond1.getOrder());;
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE, bond2.getOrder());;
		Assert.assertEquals(CDKConstants.BONDORDER_DOUBLE, bond3.getOrder());;
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE, bond4.getOrder());;
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE, bond5.getOrder());;
	}

	@Test public void xtestPyridine() throws Exception {
		IMolecule enol = new NNMolecule();
		
		// atom block
		IAtom atom1 = new NNAtom(Elements.CARBON);
		atom1.setHybridization(Hybridization.SP2);
		IAtom atom2 = new NNAtom(Elements.CARBON);
		atom2.setHybridization(Hybridization.SP2);
		IAtom atom3 = new NNAtom(Elements.CARBON);
		atom3.setHybridization(Hybridization.SP2);
		IAtom atom4 = new NNAtom(Elements.CARBON);
		atom3.setHybridization(Hybridization.SP2);
		IAtom atom5 = new NNAtom(Elements.CARBON);
		atom3.setHybridization(Hybridization.SP2);
		IAtom atom6 = new NNAtom(Elements.NITROGEN);
		atom3.setHybridization(Hybridization.SP2);
		
		// bond block
		IBond bond1 = new NNBond(atom1, atom2);
		IBond bond2 = new NNBond(atom2, atom3);
		IBond bond3 = new NNBond(atom3, atom4);
		IBond bond4 = new NNBond(atom4, atom5);
		IBond bond5 = new NNBond(atom5, atom6);
		IBond bond6 = new NNBond(atom6, atom1);
		
		enol.addAtom(atom1);
		enol.addAtom(atom2);
		enol.addAtom(atom3);
		enol.addAtom(atom4);
		enol.addAtom(atom5);
		enol.addAtom(atom6);
		enol.addBond(bond1);
		enol.addBond(bond2);
		enol.addBond(bond3);
		enol.addBond(bond4);
		enol.addBond(bond5);
		enol.addBond(bond6);
		
		// now have the algorithm have a go at it
		enol = dbst.fixAromaticBondOrders(enol);
        Assert.assertNotNull(enol);
		
		// now check whether it did the right thing
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE.ordinal() + CDKConstants.BONDORDER_DOUBLE.ordinal(), 
				bond1.getOrder().ordinal() + bond6.getOrder().ordinal()); // around atom1
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE.ordinal() + CDKConstants.BONDORDER_DOUBLE.ordinal(), 
				bond1.getOrder().ordinal() + bond2.getOrder().ordinal()); // around atom2
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE.ordinal() + CDKConstants.BONDORDER_DOUBLE.ordinal(), 
				bond2.getOrder().ordinal() + bond3.getOrder().ordinal()); // around atom3
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE.ordinal() + CDKConstants.BONDORDER_DOUBLE.ordinal(), 
				bond3.getOrder().ordinal() + bond4.getOrder().ordinal()); // around atom4
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE.ordinal() + CDKConstants.BONDORDER_DOUBLE.ordinal(), 
				bond4.getOrder().ordinal() + bond5.getOrder().ordinal()); // around atom5
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE.ordinal() + CDKConstants.BONDORDER_DOUBLE.ordinal(), 
				bond5.getOrder().ordinal() + bond6.getOrder().ordinal()); // around atom6
	}

	/**
	 * @cdk.inchi InChI=1/C6H6/c1-2-4-6-5-3-1/h1-6H 
	 */
	@Test public void xtestBenzene() throws Exception {
		IMolecule enol = new NNMolecule();
		
		// atom block
		IAtom atom1 = new NNAtom(Elements.CARBON);
		atom1.setHybridization(Hybridization.SP2);
		IAtom atom2 = new NNAtom(Elements.CARBON);
		atom2.setHybridization(Hybridization.SP2);
		IAtom atom3 = new NNAtom(Elements.CARBON);
		atom3.setHybridization(Hybridization.SP2);
		IAtom atom4 = new NNAtom(Elements.CARBON);
		atom3.setHybridization(Hybridization.SP2);
		IAtom atom5 = new NNAtom(Elements.CARBON);
		atom3.setHybridization(Hybridization.SP2);
		IAtom atom6 = new NNAtom(Elements.CARBON);
		atom3.setHybridization(Hybridization.SP2);
		
		// bond block
		IBond bond1 = new NNBond(atom1, atom2);
		IBond bond2 = new NNBond(atom2, atom3);
		IBond bond3 = new NNBond(atom3, atom4);
		IBond bond4 = new NNBond(atom4, atom5);
		IBond bond5 = new NNBond(atom5, atom6);
		IBond bond6 = new NNBond(atom6, atom1);
		
		enol.addAtom(atom1);
		enol.addAtom(atom2);
		enol.addAtom(atom3);
		enol.addAtom(atom4);
		enol.addAtom(atom5);
		enol.addAtom(atom6);
		enol.addBond(bond1);
		enol.addBond(bond2);
		enol.addBond(bond3);
		enol.addBond(bond4);
		enol.addBond(bond5);
		enol.addBond(bond6);
		
		// now have the algorithm have a go at it
		enol = dbst.fixAromaticBondOrders(enol);
        Assert.assertNotNull(enol);
		
		// now check whether it did the right thing
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE.ordinal() + CDKConstants.BONDORDER_DOUBLE.ordinal(), 
				bond1.getOrder().ordinal() + bond6.getOrder().ordinal()); // around atom1
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE.ordinal() + CDKConstants.BONDORDER_DOUBLE.ordinal(), 
				bond1.getOrder().ordinal() + bond2.getOrder().ordinal()); // around atom2
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE.ordinal() + CDKConstants.BONDORDER_DOUBLE.ordinal(), 
				bond2.getOrder().ordinal() + bond3.getOrder().ordinal()); // around atom3
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE.ordinal() + CDKConstants.BONDORDER_DOUBLE.ordinal(), 
				bond3.getOrder().ordinal() + bond4.getOrder().ordinal()); // around atom4
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE.ordinal() + CDKConstants.BONDORDER_DOUBLE.ordinal(), 
				bond4.getOrder().ordinal() + bond5.getOrder().ordinal()); // around atom5
		Assert.assertEquals(CDKConstants.BONDORDER_SINGLE.ordinal() + CDKConstants.BONDORDER_DOUBLE.ordinal(), 
				bond5.getOrder().ordinal() + bond6.getOrder().ordinal()); // around atom6
	}
}
