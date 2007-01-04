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
import org.openscience.cdk.tools.ValencyChecker;

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
public class DeduceBondOrderTestFromExplicitHydrogensAndCharges extends CDKTestCase {
	
	private IDeduceBondOrderTool dboTool;

	public DeduceBondOrderTestFromExplicitHydrogensAndCharges(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		dboTool = new ValencyChecker();
	}

	public static Test suite() {
		return new TestSuite(DeduceBondOrderTestFromExplicitHydrogensAndCharges.class);
	}

	/**
	 * Test <div class="inchi">InChI=1/C2H4O/c1-2-3/h2H,1H3/p+1</div>. 
	 */
	public void testKeto() throws Exception {
		IMolecule keto = new NNMolecule();
		
		// atom block
		IAtom atom1 = new NNAtom(Elements.CARBON);
		addHydrogens(keto, atom1, 3);
		IAtom atom2 = new NNAtom(Elements.CARBON);
		addHydrogens(keto, atom2, 1);
		IAtom atom3 = new NNAtom(Elements.OXYGEN);
		addHydrogens(keto, atom3, 1);
		atom3.setFormalCharge(+1);
		
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
	
	private void addHydrogens(IAtomContainer container, IAtom atom, int numberOfHydrogens) {
		for (int i=0; i<numberOfHydrogens; i++) 
			container.addBond(atom.getBuilder().newBond(atom, atom.getBuilder().newAtom("H")));
	}
	
}

