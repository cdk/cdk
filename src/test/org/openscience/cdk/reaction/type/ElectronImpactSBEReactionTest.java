/* $RCSfile$
 * $Author: miguelrojasch $
 * $Date: 2006-05-11 14:25:07 +0200 (Do, 11 Mai 2006) $
 * $Revision: 6221 $
 *
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.reaction.type;


import java.util.HashMap;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.ElectronImpactSDBReaction;
import org.openscience.cdk.reaction.ReactionProcessTest;

/**
 * TestSuite that runs a test for the ElectronImpactSBEReactionTest.
 *
 * @cdk.module test-reaction
 */
 
public class ElectronImpactSBEReactionTest extends ReactionProcessTest {

	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	/**
	 *  The JUnit setup method
	 */
	 @BeforeClass public static void setUp() throws Exception {
	 	setReaction(ElectronImpactSDBReaction.class);
	 }
	/**
	 *  A unit test for JUnit.
	 *  
	 *  FIXME REAC: not recognized IAtomType =C*
	 *
	 * @return    Description of the Return Value
	 */
	@Test public void testManual_Set_Active_Atom() throws Exception {
		/* ionize(>C-C<): C=CCC -> C=C* + C+ , set the reactive center*/
		
		IMolecule reactant = builder.newMolecule();//Smiles("C=CC")
		reactant.addAtom(builder.newAtom("C"));
		reactant.addAtom(builder.newAtom("C"));
		reactant.addAtom(builder.newAtom("C"));
		reactant.addBond(0, 1, IBond.Order.DOUBLE);
		reactant.addBond(1, 2, IBond.Order.SINGLE);
		addExplicitHydrogens(reactant);
        		
		Iterator<IBond> bonds = reactant.bonds();
		while (bonds.hasNext()){
			IBond bond = (IBond)bonds.next();
			IAtom atom1 = bond.getAtom(0);
			IAtom atom2 = bond.getAtom(1);
			if(bond.getOrder() == IBond.Order.SINGLE &&
					atom1.getSymbol().equals("C")&&
					atom2.getSymbol().equals("C")){
				bond.setFlag(CDKConstants.REACTIVE_CENTER,true);
				atom1.setFlag(CDKConstants.REACTIVE_CENTER,true);
				atom2.setFlag(CDKConstants.REACTIVE_CENTER,true);
			}
		}
		
		Assert.assertEquals(0, reactant.getSingleElectronCount());
		
		IMoleculeSet setOfReactants = DefaultChemObjectBuilder.getInstance().newMoleculeSet();
		setOfReactants.addMolecule(reactant);

		/* initiate */
		makeSureAtomTypesAreRecognized(reactant);
		
		IReactionProcess type  = new ElectronImpactSDBReaction();
        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("hasActiveCenter",Boolean.TRUE);;
        type.setParameters(params);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(2, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());

        
        IMolecule molecule1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);//[H][C+]=C([H])[H]

        Assert.assertEquals(1, molecule1.getAtom(1).getFormalCharge().intValue());
        Assert.assertEquals(0, molecule1.getSingleElectronCount());
        
        IMolecule molecule2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);//[H][C*]([H])[H]

        Assert.assertEquals(1, molecule2.getSingleElectronCount());
        Assert.assertEquals(1, molecule2.getConnectedSingleElectronsCount(molecule2.getAtom(0)));
        
        Assert.assertTrue(setOfReactions.getReaction(0).mappings().hasNext());

        Assert.assertEquals(2, setOfReactions.getReaction(1).getProductCount());

        molecule1 = setOfReactions.getReaction(1).getProducts().getMolecule(0);//[H]C=[C*]([H])[H]
        Assert.assertEquals(1, molecule1.getConnectedSingleElectronsCount(molecule1.getAtom(1)));

        molecule2 = setOfReactions.getReaction(1).getProducts().getMolecule(1);//[H][C+]([H])[H]

        Assert.assertEquals(0, molecule2.getSingleElectronCount());
        Assert.assertEquals(1, molecule2.getAtom(0).getFormalCharge().intValue());
        
        
	}
	/**
	 * Test to recognize if a IMolecule matcher correctly the CDKAtomTypes.
	 * 
	 * @param molecule          The IMolecule to analyze
	 * @throws CDKException
	 */
	private void makeSureAtomTypesAreRecognized(IMolecule molecule) throws CDKException {

		Iterator<IAtom> atoms = molecule.atoms();
		CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(molecule.getBuilder());
		while (atoms.hasNext()) {
				IAtom nextAtom = atoms.next();
				Assert.assertNotNull(
					"Missing atom type for: " + nextAtom, 
					matcher.findMatchingAtomType(molecule, nextAtom)
				);
		}
	}
}
