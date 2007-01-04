/* $Revision: 6707 $ $Author: egonw $ $Date: 2006-07-30 22:38:18 +0200 (Sun, 30 Jul 2006) $
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.tools;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NNAtom;
import org.openscience.cdk.nonotify.NNBond;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.IDeduceBondOrderTool;
import org.openscience.cdk.tools.SaturationChecker;

/**
 * Test suite for testing deduce-bond-order implementations.
 * This suite tests deduction from hybridization rich starting
 * points, excluding, but optional, implicit or explicit
 * hydrogen counts.
 * 
 * @author      egonw
 * @cdk.module  test-valencycheck
 * @cdk.created 2006-08-16
 */
public class DeduceBondOrderTestFromExplicitHydrogens extends CDKTestCase {
	
	private IDeduceBondOrderTool dboTool;

	public DeduceBondOrderTestFromExplicitHydrogens(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		dboTool = new SaturationChecker();
	}

	public static Test suite() {
		return new TestSuite(DeduceBondOrderTestFromExplicitHydrogens.class);
	}

	/**
	 * Test <div class="inchi">InChI=1/C2H2/c1-2/h1-2H</div>. 
	 */
	public void testAcetylene() throws Exception {
		IMolecule keto = new NNMolecule();
		
		// atom block
		IAtom atom1 = new NNAtom(Elements.CARBON);
		addHydrogens(keto, atom1, 1);
		IAtom atom2 = new NNAtom(Elements.CARBON);
		addHydrogens(keto, atom2, 1);
		
		// bond block
		IBond bond1 = new NNBond(atom1, atom2);
		
		keto.addAtom(atom1);
		keto.addAtom(atom2);
		keto.addBond(bond1);
		
		// now have the algorithm have a go at it
		dboTool.saturate(keto);
		
		// now check wether it did the right thing
		assertEquals(CDKConstants.BONDORDER_TRIPLE, bond1.getOrder(), 0.00001);
	}

	/**
	 * Test <div class="inchi">InChI=1/C2H4O/c1-2-3/h2H,1H3</div>. 
	 */
	public void testKeto() throws Exception {
		IMolecule keto = new NNMolecule();
		
		// atom block
		IAtom atom1 = new NNAtom(Elements.CARBON);
		addHydrogens(keto, atom1, 3);
		IAtom atom2 = new NNAtom(Elements.CARBON);
		addHydrogens(keto, atom2, 1);
		IAtom atom3 = new NNAtom(Elements.OXYGEN);
		
		// bond block
		IBond bond1 = new NNBond(atom1, atom2);
		IBond bond2 = new NNBond(atom2, atom3);
		
		keto.addAtom(atom1);
		keto.addAtom(atom2);
		keto.addAtom(atom3);
		keto.addBond(bond1);
		keto.addBond(bond2);
		
		// now have the algorithm have a go at it
		dboTool.saturate(keto);
		
		// now check wether it did the right thing
		assertEquals(CDKConstants.BONDORDER_SINGLE, bond1.getOrder(), 0.00001);
		assertEquals(CDKConstants.BONDORDER_DOUBLE, bond2.getOrder(), 0.00001);
	}
	
	/**
	 * Test <div class="inchi">InChI=1/C2H6O/c1-2-3/h3H,2H2,1H3</div>. 
	 */
	public void testEnol() throws Exception {
		IMolecule enol = new NNMolecule();
		
		// atom block
		IAtom atom1 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom1, 2);
		IAtom atom2 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom2, 1);
		IAtom atom3 = new NNAtom(Elements.OXYGEN);
		addHydrogens(enol, atom3, 1);
		
		// bond block
		IBond bond1 = new NNBond(atom1, atom2);
		IBond bond2 = new NNBond(atom2, atom3);
		
		enol.addAtom(atom1);
		enol.addAtom(atom2);
		enol.addAtom(atom3);
		enol.addBond(bond1);
		enol.addBond(bond2);
		
		// now have the algorithm have a go at it
		dboTool.saturate(enol);
		
		// now check wether it did the right thing
		assertEquals(CDKConstants.BONDORDER_DOUBLE, bond1.getOrder(), 0.00001);
		assertEquals(CDKConstants.BONDORDER_SINGLE, bond2.getOrder(), 0.00001);
	}
	
	/**
	 * Test <div class="inchi">InChI=1/C4H6/c1-3-4-2/h3-4H,1-2H2</div>. 
	 */
	public void xtestButadiene() throws Exception {
		IMolecule enol = new NNMolecule();
		
		// atom block
		IAtom atom1 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom1, 2);
		IAtom atom2 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom2, 1);
		IAtom atom3 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom3, 1);
		IAtom atom4 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom4, 2);
		
		// bond block
		IBond bond1 = new NNBond(atom1, atom2);
		IBond bond2 = new NNBond(atom2, atom3);
		IBond bond3 = new NNBond(atom3, atom4);
		
		enol.addAtom(atom1);
		enol.addAtom(atom2);
		enol.addAtom(atom3);
		enol.addAtom(atom4);
		enol.addBond(bond1);
		enol.addBond(bond2);
		enol.addBond(bond3);
		
		// now have the algorithm have a go at it
		dboTool.saturate(enol);
		
		// now check wether it did the right thing
		assertEquals(CDKConstants.BONDORDER_DOUBLE, bond1.getOrder(), 0.00001);
		assertEquals(CDKConstants.BONDORDER_SINGLE, bond2.getOrder(), 0.00001);
		assertEquals(CDKConstants.BONDORDER_SINGLE, bond3.getOrder(), 0.00001);
	}

	/**
	 * Test <div class="inchi">InChI=1/C6H4O2/c7-5-1-2-6(8)4-3-5/h1-4H</div>. 
	 */
	public void testQuinone() throws Exception {
		IMolecule enol = new NNMolecule();
		
		// atom block
		IAtom atom1 = new NNAtom(Elements.CARBON);
		IAtom atom2 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom2, 1);
		IAtom atom3 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom3, 1);
		IAtom atom4 = new NNAtom(Elements.CARBON);
		IAtom atom5 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom5, 1);
		IAtom atom6 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom6, 1);
		IAtom atom7 = new NNAtom(Elements.OXYGEN);
		IAtom atom8 = new NNAtom(Elements.OXYGEN);
		
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
		dboTool.saturate(enol);
		
		// now check wether it did the right thing
		assertEquals(CDKConstants.BONDORDER_SINGLE, bond1.getOrder(), 0.00001);
		assertEquals(CDKConstants.BONDORDER_DOUBLE, bond2.getOrder(), 0.00001);
		assertEquals(CDKConstants.BONDORDER_SINGLE, bond3.getOrder(), 0.00001);
		assertEquals(CDKConstants.BONDORDER_SINGLE, bond4.getOrder(), 0.00001);
		assertEquals(CDKConstants.BONDORDER_DOUBLE, bond5.getOrder(), 0.00001);
		assertEquals(CDKConstants.BONDORDER_SINGLE, bond6.getOrder(), 0.00001);
		assertEquals(CDKConstants.BONDORDER_DOUBLE, bond7.getOrder(), 0.00001);
		assertEquals(CDKConstants.BONDORDER_DOUBLE, bond8.getOrder(), 0.00001);
	}
	
	/**
	 * Test <div class="inchi">InChI=1/C6H6/c1-2-4-6-5-3-1/h1-6H</div>. 
	 */
	public void testBenzene() throws Exception {
		IMolecule enol = new NNMolecule();
		
		// atom block
		IAtom atom1 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom1, 1);
		IAtom atom2 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom2, 1);
		IAtom atom3 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom3, 1);
		IAtom atom4 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom4, 1);
		IAtom atom5 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom5, 1);
		IAtom atom6 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom6, 1);
		
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
		dboTool.saturate(enol);
		
		// now check wether it did the right thing
		assertEquals(CDKConstants.BONDORDER_SINGLE + CDKConstants.BONDORDER_DOUBLE, 
				bond1.getOrder() + bond6.getOrder(), 0.00001); // around atom1
		assertEquals(CDKConstants.BONDORDER_SINGLE + CDKConstants.BONDORDER_DOUBLE, 
				bond1.getOrder() + bond2.getOrder(), 0.00001); // around atom2
		assertEquals(CDKConstants.BONDORDER_SINGLE + CDKConstants.BONDORDER_DOUBLE, 
				bond2.getOrder() + bond3.getOrder(), 0.00001); // around atom3
		assertEquals(CDKConstants.BONDORDER_SINGLE + CDKConstants.BONDORDER_DOUBLE, 
				bond3.getOrder() + bond4.getOrder(), 0.00001); // around atom4
		assertEquals(CDKConstants.BONDORDER_SINGLE + CDKConstants.BONDORDER_DOUBLE, 
				bond4.getOrder() + bond5.getOrder(), 0.00001); // around atom5
		assertEquals(CDKConstants.BONDORDER_SINGLE + CDKConstants.BONDORDER_DOUBLE, 
				bond5.getOrder() + bond6.getOrder(), 0.00001); // around atom6
	}
	
	/**
	 * Test <div class="inchi">InChI=1/C4H5N/c1-2-4-5-3-1/h1-5H</div>. 
	 */
	public void testPyrrole() throws Exception {
		IMolecule enol = new NNMolecule();
		
		// atom block
		IAtom atom1 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom1, 1);
		IAtom atom2 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom2, 1);
		IAtom atom3 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom3, 1);
		IAtom atom4 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom4, 1);
		IAtom atom5 = new NNAtom(Elements.NITROGEN);
		addHydrogens(enol, atom5, 1);
		
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
		dboTool.saturate(enol);
		
		// now check wether it did the right thing
		assertEquals(CDKConstants.BONDORDER_DOUBLE, bond1.getOrder(), 0.00001);
		assertEquals(CDKConstants.BONDORDER_SINGLE, bond2.getOrder(), 0.00001);
		assertEquals(CDKConstants.BONDORDER_DOUBLE, bond3.getOrder(), 0.00001);
		assertEquals(CDKConstants.BONDORDER_SINGLE, bond4.getOrder(), 0.00001);
		assertEquals(CDKConstants.BONDORDER_SINGLE, bond5.getOrder(), 0.00001);
	}
	
	/**
	 * Test <div class="inchi">InChI=1/C5H5N/c1-2-4-6-5-3-1/h1-5H</div>. 
	 */
	public void xtestPyridine() throws Exception {
		IMolecule enol = new NNMolecule();
		
		// atom block
		IAtom atom1 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom1, 1);
		IAtom atom2 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom2, 1);
		IAtom atom3 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom3, 1);
		IAtom atom4 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom4, 1);
		IAtom atom5 = new NNAtom(Elements.NITROGEN);
		IAtom atom6 = new NNAtom(Elements.CARBON);
		addHydrogens(enol, atom6, 1);
		
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
		dboTool.saturate(enol);
		
		// now check wether it did the right thing
		assertEquals(CDKConstants.BONDORDER_SINGLE + CDKConstants.BONDORDER_DOUBLE, 
				bond1.getOrder() + bond6.getOrder(), 0.00001); // around atom1
		assertEquals(CDKConstants.BONDORDER_SINGLE + CDKConstants.BONDORDER_DOUBLE, 
				bond1.getOrder() + bond2.getOrder(), 0.00001); // around atom2
		assertEquals(CDKConstants.BONDORDER_SINGLE + CDKConstants.BONDORDER_DOUBLE, 
				bond2.getOrder() + bond3.getOrder(), 0.00001); // around atom3
		assertEquals(CDKConstants.BONDORDER_SINGLE + CDKConstants.BONDORDER_DOUBLE, 
				bond3.getOrder() + bond4.getOrder(), 0.00001); // around atom4
		assertEquals(CDKConstants.BONDORDER_SINGLE + CDKConstants.BONDORDER_DOUBLE, 
				bond4.getOrder() + bond5.getOrder(), 0.00001); // around atom5
		assertEquals(CDKConstants.BONDORDER_SINGLE + CDKConstants.BONDORDER_DOUBLE, 
				bond5.getOrder() + bond6.getOrder(), 0.00001); // around atom6
	}
	
	private void addHydrogens(IAtomContainer container, IAtom atom, int numberOfHydrogens) {
		for (int i=0; i<numberOfHydrogens; i++) 
			container.addBond(atom.getBuilder().newBond(atom, atom.getBuilder().newAtom("H")));
	}
	
}

