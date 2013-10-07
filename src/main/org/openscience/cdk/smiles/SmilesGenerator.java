/*  $Revision$ $Author$ $Date$
 *  
 *  Copyright (C) 2002-2007  Oliver Horlacher
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smiles;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.BondTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.invariant.CanonicalLabeler;
import org.openscience.cdk.graph.invariant.MorganNumbersTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

/**
 * Generates SMILES strings {@cdk.cite WEI88, WEI89}. It takes into account the
 * isotope and formal charge information of the atoms. In addition to this it
 * takes stereochemistry in account for both Bond's and Atom's. Via the flag 
 * useAromaticity it can be set if only SP2-hybridized atoms shall be set to 
 * lower case (default) or atoms, which are SP2 or aromatic.
 *
 * <p>Some example code:
 * <pre>
 * IMolecule benzene; // single/aromatic bonds between 6 carbons
 * SmilesGenerator sg = new SmilesGenerator();
 * String smiles = sg.createSMILES(benzene); // C1CCCCC1
 * sg.setUseAromaticityFlag(true);
 * smiles = sg.createSMILES(benzene); // c1ccccc1
 * IMolecule benzene2; // one of the two kekule structures with explicit double bond orders
 * String smiles2 = sg.createSMILES(benzene2); // C1=CC=CC=C1
 * </pre>
 * <b>Note</b>Due to the way the initial atom labeling is constructed, ensure
 * that the input molecule is appropriately configured.
 * In absence of such configuration it is possible that different forms
 * of the same molecule will not result in the same canonical SMILES.
 *
 * @author         Oliver Horlacher
 * @author         Stefan Kuhn (chiral smiles)
 * @cdk.created    2002-02-26
 * @cdk.keyword    SMILES, generator
 * @cdk.module     smiles
 * @cdk.githash
 * @cdk.bug        1793446
 */
@TestClass("org.openscience.cdk.smiles.SmilesGeneratorTest")
public class SmilesGenerator
{
	/**
	 *  Create the SMILES generator.
	 */
	public SmilesGenerator() {}

	/**
	 *  Create the SMILES generator.
	 *  @param useAromaticityFlag if false only SP2-hybridized atoms will be lower case (default), true=SP2 or aromaticity trigger lower case (same as using setUseAromaticityFlag later)
	 */
	public SmilesGenerator(boolean useAromaticityFlag) {
		// ignore for now
	}
	
    /**
     *  Generate canonical SMILES from the <code>molecule</code>. This method
     *  canonicaly lables the molecule but does not perform any checks on the
     *  chemical validity of the molecule.
     *  IMPORTANT: A precomputed Set of All Rings (SAR) can be passed to this
     *  SmilesGenerator in order to avoid recomputing it. Use setRings() to
     *  assign the SAR.
     *
     * @param  molecule  The molecule to evaluate
     * @see              org.openscience.cdk.graph.invariant.CanonicalLabeler#canonLabel(IAtomContainer)
     * @return the SMILES representation of the molecule
     */
    @TestMethod("testCisResorcinol,testEthylPropylPhenantren,testAlanin")
    public synchronized String createSMILES(IAtomContainer molecule)
	{
        return "";
	}

	/**
	 *  Generate a SMILES for the given <code>Reaction</code>.
     * @param reaction the reaction in question
     * @return the SMILES representation of the reaction
     * @throws org.openscience.cdk.exception.CDKException if there is an error during SMILES generation
     */
	public synchronized String createSMILES(IReaction reaction) throws CDKException
	{
		StringBuffer reactionSMILES = new StringBuffer();
		IAtomContainerSet reactants = reaction.getReactants();
		for (int i = 0; i < reactants.getAtomContainerCount(); i++)
		{
			reactionSMILES.append(createSMILES(reactants.getAtomContainer(i)));
			if (i + 1 < reactants.getAtomContainerCount())
			{
				reactionSMILES.append('.');
			}
		}
		reactionSMILES.append('>');
		IAtomContainerSet agents = reaction.getAgents();
		for (int i = 0; i < agents.getAtomContainerCount(); i++)
		{
			reactionSMILES.append(createSMILES(agents.getAtomContainer(i)));
			if (i + 1 < agents.getAtomContainerCount())
			{
				reactionSMILES.append('.');
			}
		}
		reactionSMILES.append('>');
		IAtomContainerSet products = reaction.getProducts();
		for (int i = 0; i < products.getAtomContainerCount(); i++)
		{
			reactionSMILES.append(createSMILES(products.getAtomContainer(i)));
			if (i + 1 < products.getAtomContainerCount())
			{
				reactionSMILES.append('.');
			}
		}
		return reactionSMILES.toString();
	}


	/**
	 *  Generate canonical and chiral SMILES from the <code>molecule</code>. This
	 *  method canonicaly lables the molecule but dose not perform any checks on
	 *  the chemical validity of the molecule. The chiral smiles is done like in
	 *  the <a href="http://www.daylight.com/dayhtml/doc/theory/theory.smiles.html">
	 *  daylight theory manual</a> . I did not find rules for canonical and chiral
	 *  smiles, therefore there is no guarantee that the smiles complies to any
	 *  externeal rules, but it is canonical compared to other smiles produced by
	 *  this method. The method checks if there are 2D coordinates but does not
	 *  check if coordinates make sense. Invalid stereo configurations are ignored;
	 *  if there are no valid stereo configuration the smiles will be the same as
	 *  the non-chiral one. Note that often stereo configurations are only complete
	 *  and can be converted to a smiles if explicit Hs are given.
	 *  IMPORTANT: A precomputed Set of All Rings (SAR) can be passed to this 
	 *  SmilesGenerator in order to avoid recomputing it. Use setRings() to 
	 *  assign the SAR.
	 *
	 * @param  molecule                 The molecule to evaluate.
     * @param  doubleBondConfiguration  Should E/Z configurations be read at these positions? If the flag at position X is set to true, 
     *                                  an E/Z configuration will be written from coordinates around bond X, if false, it will be ignored. 
     *                                  If flag is true for a bond which does not constitute a valid double bond configuration, it will be 
     *                                  ignored (meaning setting all to true will create E/Z indication will be pu in the smiles wherever 
     *                                  possible, but note the coordinates might be arbitrary).
	 * @exception  CDKException         At least one atom has no Point2D;
	 *      coordinates are needed for creating the chiral smiles.
	 * @see                             org.openscience.cdk.graph.invariant.CanonicalLabeler#canonLabel(IAtomContainer)
     * @return the SMILES representation of the molecule
	 */
    @TestMethod("testAlaSMILES,testSugarSMILES")
    public synchronized String createChiralSMILES(IAtomContainer molecule, boolean[] doubleBondConfiguration) throws CDKException
	{
        return "";
	}


    /**
     *  Generate canonical SMILES from the <code>molecule</code>. This method
     *  canonicaly lables the molecule but dose not perform any checks on the
     *  chemical validity of the molecule. This method also takes care of multiple
     *  molecules.
     *  IMPORTANT: A precomputed Set of All Rings (SAR) can be passed to this
     *  SmilesGenerator in order to avoid recomputing it. Use setRings() to
     *  assign the SAR.
     *
     * @param  molecule                 The molecule to evaluate.
     * @param  chiral                   true=SMILES will be chiral, false=SMILES.
     *      will not be chiral.
     * @param  doubleBondConfiguration  Should E/Z configurations be read at these positions? If the flag at position X is set to true, 
     *                                  an E/Z configuration will be written from coordinates around bond X, if false, it will be ignored. 
     *                                  If flag is true for a bond which does not constitute a valid double bond configuration, it will be 
     *                                  ignored (meaning setting all to true will create E/Z indication will be pu in the smiles wherever 
     *                                  possible, but note the coordinates might be arbitrary).
     * @exception CDKException          At least one atom has no Point2D;
     *      coordinates are needed for crating the chiral smiles. This excpetion
     *      can only be thrown if chiral smiles is created, ignore it if you want a
     *      non-chiral smiles (createSMILES(AtomContainer) does not throw an
     *      exception).
     * @see                             org.openscience.cdk.graph.invariant.CanonicalLabeler#canonLabel(IAtomContainer)
     * @return the SMILES representation of the molecule
     */
	public synchronized String createSMILES(IAtomContainer molecule, boolean chiral, boolean doubleBondConfiguration[]) throws CDKException
	{
	   return "";
	}


	/**
	 *  Generate canonical SMILES from the <code>molecule</code>. This method
	 *  canonicaly lables the molecule but dose not perform any checks on the
	 *  chemical validity of the molecule. Does not care about multiple molecules.
	 *  IMPORTANT: A precomputed Set of All Rings (SAR) can be passed to this 
	 *  SmilesGenerator in order to avoid recomputing it. Use setRings() to 
	 *  assign the SAR.
	 *
	 * @param  molecule                 The molecule to evaluate.
	 * @param  chiral                   true=SMILES will be chiral, false=SMILES
	 *      will not be chiral.
     * @param  doubleBondConfiguration  Should E/Z configurations be read at these positions? If the flag at position X is set to true, 
     *                                  an E/Z configuration will be written from coordinates around bond X, if false, it will be ignored. 
     *                                  If flag is true for a bond which does not constitute a valid double bond configuration, it will be 
     *                                  ignored (meaning setting all to true will create E/Z indication will be pu in the smiles wherever 
     *                                  possible, but note the coordinates might be arbitrary).
	 * @exception  CDKException         At least one atom has no Point2D;
	 *      coordinates are needed for creating the chiral smiles. This excpetion
	 *      can only be thrown if chiral smiles is created, ignore it if you want a
	 *      non-chiral smiles (createSMILES(AtomContainer) does not throw an
	 *      exception).
	 *@see                             org.openscience.cdk.graph.invariant.CanonicalLabeler#canonLabel(IAtomContainer)
     * @return the SMILES representation of the molecule
	 */
	@TestMethod("testCreateSMILESWithoutCheckForMultipleMolecules_withDetectAromaticity,testCreateSMILESWithoutCheckForMultipleMolecules_withoutDetectAromaticity")
	public synchronized String createSMILESWithoutCheckForMultipleMolecules(IAtomContainer molecule, boolean chiral, boolean doubleBondConfiguration[]) throws CDKException
	{
        return "";
	}

	/**
	 *  Returns the current AllRingsFinder instance
	 *
	 *@return   the current AllRingsFinder instance
	 */
	public AllRingsFinder getRingFinder()
	{
		return null;
	}


	/**
	 *  Sets the current AllRingsFinder instance
	 * Use this if you want to customize the timeout for 
	 * the AllRingsFinder. AllRingsFinder is stopping its 
	 * quest to find all rings after a default of 5 seconds.
	 *
	 * @see org.openscience.cdk.ringsearch.AllRingsFinder
	 * 
	 * @param  ringFinder  The value to assign ringFinder.
	 */
	public void setRingFinder(AllRingsFinder ringFinder)
	{
        // ignore for now
	}

	/**
     * Indicates whether output should be an aromatic SMILES.
     *
	 * @param useAromaticityFlag if false only SP2-hybridized atoms will be lower case (default),
     * true=SP2 or aromaticity trigger lower case
	 */
    @TestMethod("testSFBug956923")
    public void setUseAromaticityFlag(boolean useAromaticityFlag) {
		// ignore for now
	}

}
