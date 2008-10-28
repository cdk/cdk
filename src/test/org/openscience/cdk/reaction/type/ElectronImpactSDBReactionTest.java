/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
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
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;

/**
 * TestSuite that runs a test for the ElectronImpactSDBReactionTest.
 *
 * @cdk.module test-reaction
 */
 
public class ElectronImpactSDBReactionTest extends ReactionProcessTest {
	
	private IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	/**
	 *  The JUnit setup method
	 */
	public  ElectronImpactSDBReactionTest()  throws Exception {
			setReaction(ElectronImpactSDBReaction.class);
	 }
	 
	 /**
	  *  The JUnit setup method
	  */
	 @Test public void testElectronImpactSDBReaction() throws Exception {
			IReactionProcess type = new ElectronImpactSDBReaction();
			Assert.assertNotNull(type);
	 }
	/**
	 *  A unit test for JUnit.
	 *  
	 *  FIXME REAC: not recognized IAtomType =C*
	 *
	 * @return    Description of the Return Value
	 */
	@Test public void testInitiate_IMoleculeSet_IMoleculeSet() throws Exception {
		/* ionize(>C-C<): C=CCC -> C=C* + C+ , set the reactive center*/
		
		IMolecule reactant = builder.newMolecule();//Smiles("C=CC")
		reactant.addAtom(builder.newAtom("C"));
		reactant.addAtom(builder.newAtom("C"));
		reactant.addAtom(builder.newAtom("C"));
		reactant.addBond(0, 1, IBond.Order.DOUBLE);
		reactant.addBond(1, 2, IBond.Order.SINGLE);
		addExplicitHydrogens(reactant);
        		
		Iterator<IBond> bonds = reactant.bonds().iterator();
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
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
	    IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        
        Assert.assertEquals(2, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());

        
        IMolecule molecule1 = setOfReactions.getReaction(0).getProducts().getMolecule(0);//[H][C+]=C([H])[H]

        Assert.assertEquals(1, molecule1.getAtom(1).getFormalCharge().intValue());
        Assert.assertEquals(0, molecule1.getSingleElectronCount());
        
        IMolecule molecule2 = setOfReactions.getReaction(0).getProducts().getMolecule(1);//[H][C*]([H])[H]

        Assert.assertEquals(1, molecule2.getSingleElectronCount());
        Assert.assertEquals(1, molecule2.getConnectedSingleElectronsCount(molecule2.getAtom(0)));
        
        Assert.assertTrue(setOfReactions.getReaction(0).mappings().iterator().hasNext());

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

		Iterator<IAtom> atoms = molecule.atoms().iterator();
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
