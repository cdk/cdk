/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.reaction.type;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.math.PermutationGenerator;
import org.openscience.cdk.tools.MFAnalyser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.SetOfAtomContainersManipulator;

/**
 * The ReactionBalancer tries to stoichiometrically balance a reaction. It currently uses
 * water for balancing oxygen, protons for balancing charges and H2 for balancing
 * remaining hydrogens. If any other elements have to be balanced, permutations of stoichiometric
 * coefficients up to five are tested.
 * 
 * <p><b>Warning</b>: If the reaction cannot be balanced, the method balance returns false. 
 * Nevertheless the original reaction is modified. Use a copy of the reaction to
 * avoid this behaviour.
 * 
 * <code><pre>
 * Reaction reaction;
 * ReactionBalancer rb = new ReactionBalancer();
 * boolean balanced;
 * if(!rb.isBalanced(reaction)) balanced = rb.balance(reaction);
 * </pre></code>
 *
 * @author        Kai Hartmann
 * @author        Bart Geurten
 * @cdk.created   2004-02-20
 * @cdk.module    reaction
 */
public class ReactionBalancer {

	private static Molecule water = new Molecule();
	private static Molecule hydrogen = new Molecule();
	private static Molecule proton = new Molecule();
	private Reaction reaction = null;
	private Hashtable diff = new Hashtable();


	/**
	 *  Constructor for the ReactionBalancer object
	 */
	public ReactionBalancer() {
        if (water.getAtomCount() == 0) {
            water.addAtom(new Atom("H"));
            water.addAtom(new Atom("H"));
            water.addAtom(new Atom("O"));
            water.addBond(0, 2, 1.0);
            water.addBond(1, 2, 1.0);
            water.setProperty("name", "H2O");
            hydrogen.addAtom(new Atom("H"));
            hydrogen.addAtom(new Atom("H"));
            hydrogen.addBond(0, 1, 1.0);
            hydrogen.setProperty("name", "H2");
            proton.addAtom(new Atom("H"));
            proton.getAtomAt(0).setFormalCharge(1);
            proton.setProperty("name", "H+");
        }
	}


	/**
	 *  Gets the cloned Reaction of the ReactionBalancer object
	 *
	 *@return    The Reaction
	 */
	public Reaction getReaction() {
		return reaction;
	}


	/**
	 *  Gets the Hashtable diff of the ReactionBalancer object. It contains the
	 *  Atom symbols of the Atoms that are not balanced. Values greater zero mean
	 *  there is an excess of this Element on the product side, values lower zero
	 *  mean the opposite.
	 *
	 *@return    The Hashtable diff
	 */
	public Hashtable getDiffHashtable() {
		return diff;
	}


	/**
	 *  Tests if the reaction of this object is balanced.
	 *
	 * @return    true if reaction is balanced.
	 */
	public boolean isBalanced(Reaction reaction) {
        if (reaction != this.reaction) {
            this.reaction = reaction;
            makeDiffHashtable();
        }
		if (SetOfAtomContainersManipulator.getTotalFormalCharge(reaction.getProducts())
				 - SetOfAtomContainersManipulator.getTotalFormalCharge(reaction.getReactants()) == 0
				 && diff.isEmpty()) {
			return true;
		}
		return false;
	}


	/**
	 *  Try to balance this Reaction.
	 *
	 *@return    true if Reaction could be balanced.
	 */
	public boolean balance(Reaction reaction) throws CDKException {

        if (reaction != this.reaction) {
            this.reaction = reaction;
            makeDiffHashtable();
        }
        
		double chargeDifference =
				SetOfAtomContainersManipulator.getTotalFormalCharge(reaction.getProducts())
				 - SetOfAtomContainersManipulator.getTotalFormalCharge(reaction.getReactants());

		// If reaction is already balanced, return true.
		if (diff.isEmpty() && chargeDifference == 0) {
			return true;
		}

		if (!containsOnlyOH(diff)) {
			if (!permutateStoichiometries(5)) {
				return false;
			}
		}

		addMoleculeToBalanceElement(water, "O");
		if (diff.isEmpty() && chargeDifference == 0) {
			return true;
		}
		
		balanceCharge(proton);

		if (diff.isEmpty()) {
			return true;
		}

		
		addMoleculeToBalanceElement(hydrogen, "H");
		if (diff.isEmpty()) {
			return true;
		}

		return false;
	}


	/**
	 *  Construct the Hashtable diff for this Reaction
	 *
	 */
	protected void makeDiffHashtable() {

		addMoleculeHashs(reaction.getReactants(), -1, diff);
		addMoleculeHashs(reaction.getProducts(), 1, diff);
		removeZeroEntries(diff);

		return;
	}


	/**
	 *  Adds the FormulaHashtables of a SetOfMolecules to a Hashtable
	 *
	 *@param  som   The SetOfMolecules to be added
	 *@param  side  equals 1 for products, -1 for reactants
	 *@param  hash  The feature to be added to the MoleculeHashs attribute
	 */
	protected void addMoleculeHashs(org.openscience.cdk.interfaces.IMoleculeSet som, int side, Hashtable hash) {
		for (int i = 0; i < som.getAtomContainerCount(); i++) {
			Hashtable molHash = new MFAnalyser(som.getMolecule(i)).getFormulaHashtable();
			for (Enumeration e = molHash.keys(); e.hasMoreElements(); ) {
				String symbol = (String) e.nextElement();
				double elementCount = ((Integer) molHash.get(symbol)).doubleValue();
				if (hash.containsKey(symbol)) {
					double count = ((Double) hash.get(symbol)).doubleValue();
					count += som.getMultiplier(i) * elementCount * side;
					hash.put(symbol, new Double(count));
				} else {
					hash.put(symbol, new Double(som.getMultiplier(i) * elementCount * side));
				}
			}
		}
		return;
	}


	/**
	 *  Tries to balance the formal charge using a charged Molecule.
	 *
	 *@param  mol  The Molecule that should be used.
	 */
	public void balanceCharge(Molecule mol)  throws CDKException{
		int molCharge = AtomContainerManipulator.getTotalFormalCharge(mol);
		if (molCharge == 0) {
			return;
		}
		
		double chargeDifference =
				SetOfAtomContainersManipulator.getTotalFormalCharge(reaction.getProducts())
				 - SetOfAtomContainersManipulator.getTotalFormalCharge(reaction.getReactants());
		
		double molsToAdd = (chargeDifference) / ((double) molCharge);
		
		Hashtable molHash = new MFAnalyser(mol).getFormulaHashtable();

		if (chargeDifference == 0) {
			return;
		} else if (molsToAdd < 0) {
			balanceSetOfMolecules(reaction.getProducts(), -molsToAdd, mol);
			updateDiffHashtable(molsToAdd, molHash);
			return;
		} else if (molsToAdd > 0) {
			balanceSetOfMolecules(reaction.getReactants(), molsToAdd, mol);
			updateDiffHashtable(molsToAdd, molHash);
			return;
		}
		return;
	}


	/**
	 *  Checks whether the Hashtable hash contains other Atoms than oxygen and
	 *  hydrogen.
	 *
	 *@param  hash  The hashtable to be tested
	 *@return       true if the diff Hashtable contains the elements oxygen and
	 *      hydrogen only.
	 */
	public boolean containsOnlyOH(Hashtable hash) {
		for (Enumeration e = hash.keys(); e.hasMoreElements(); ) {
			String s = (String) e.nextElement();
			if (!s.equals("H") && !s.equals("O")) {
				return false;
			}
		}
		return true;
	}


	/**
	 *  Add a Molecule to the Reaction to balance a certain element.
	 *
	 *@param  mol      The molecule to add
	 *@param  element  The element that is to be balanced
	 */
	protected void addMoleculeToBalanceElement(Molecule mol, String element) throws CDKException{

		if (!diff.containsKey(element)) {
			return;
		}

		// The Hashtable of the molecule used for balancing
		Hashtable molHash = new MFAnalyser(mol).getFormulaHashtable();

		// How many elements do we need (negative, if more educts than products)
		double elementCount = ((Double) diff.get(element)).doubleValue() / ((Integer) molHash.get(element)).doubleValue();
		
		// get position of molecule in educts or products
		int edPos = getMoleculePosition(reaction.getReactants(), mol);
		int prodPos = getMoleculePosition(reaction.getProducts(), mol);
		
		// Add molecule to the correct side
		if (elementCount < 0) {
			// is true if more elements on educt side
			
			if (edPos != -1) {
				// is true if molecule is present on educt side
				
				double stoich = reaction.getReactants().getMultiplier(edPos);
				if (stoich < -elementCount) {
					reaction.getReactants().setMultiplier(edPos, 0);
					elementCount += stoich;
				} else {
					reaction.getReactants().setMultiplier(edPos, stoich + elementCount);
					elementCount = 0;
				}
				updateDiffHashtable(stoich, molHash);
			}
			if (elementCount < 0) {
				balanceSetOfMolecules(reaction.getProducts(), -elementCount, mol, prodPos);
				updateDiffHashtable(elementCount, molHash);
			}

		} else if (elementCount > 0) {
			// is true if more elements on product side
			
			if (prodPos != -1) {
				// is true if molecule is present on product side
				
				double stoich = reaction.getProducts().getMultiplier(prodPos);
				if (stoich < elementCount) {
					reaction.getProducts().setMultiplier(prodPos, 0);
					elementCount -= stoich;
				} else {
					reaction.getProducts().setMultiplier(prodPos, stoich - elementCount);
					elementCount = 0;
				}
				updateDiffHashtable(stoich, molHash);
			}
			if (elementCount > 0) {
				balanceSetOfMolecules(reaction.getReactants(), elementCount, mol, edPos);
				updateDiffHashtable(elementCount, molHash);
			}
		}
		
		return;
	}


	/**
	 *  Add a number of Molecules to a SetOfMolecules
	 *
	 *@param  som           The SetOfMolecules that the Molecules are to be added
	 *@param  elementCount  The number of Molecules to be added
	 *@param  mol           The Molecule to be added
	 */
	public void balanceSetOfMolecules(org.openscience.cdk.interfaces.IMoleculeSet som, double elementCount, Molecule mol) throws CDKException{
		int molPosition = getMoleculePosition(som, mol);
		balanceSetOfMolecules(som, elementCount, mol, molPosition);
	}

	/**
	 *  Add a number of Molecules to a SetOfMolecules
	 *
	 *@param  som           The SetOfMolecules that the Molecules are to be added
	 *@param  elementCount  The number of Molecules to be added
	 *@param  mol           The Molecule to be added
	 *@param  molPosition   The position of Molecule mol in SetOfMolecules som
	 */
	public void balanceSetOfMolecules(org.openscience.cdk.interfaces.IMoleculeSet som, double elementCount, Molecule mol, int molPosition) {
		
		if (molPosition == -1) {
			som.addAtomContainer(mol, elementCount);
			return;
		} else {
			som.setMultiplier(molPosition, elementCount + som.getMultiplier(molPosition));
		}
	}

	
	
	/**
	 *  Test whether a Molecule is already in this SetOfMolecules
	 *
	 *@param  som  The SetOfMolecules to be tested in
	 *@param  mol  The Molecule to be checked
	 *@return      The position in SetOfMolecules if found, -1 otherwise
	 */
	public int getMoleculePosition(org.openscience.cdk.interfaces.IMoleculeSet som, Molecule mol) throws CDKException {
		for (int i = 0; i < som.getAtomContainerCount(); i++) {
			if (som.getAtomContainer(i).getAtomCount() == mol.getAtomCount()) {
				if (mol.getBondCount() == 0 || som.getAtomContainer(i).getBondCount() == 0) {
					if (mol.getAtomAt(0).getSymbol().equals(som.getAtomContainer(i).getAtomAt(0).getSymbol())) {
						return i;
					} else {
						continue;
					}
				}
				
				if (UniversalIsomorphismTester.isIsomorph(som.getAtomContainer(i), mol)) {
					return i;
				}
			}
		}
		return -1;
	}


	/**
	 *  Updates the diff Hashtable by another Hashtable
	 *
	 *@param  elementCount  The number of occurences of the hash
	 *@param  hash          The Hashtable to use for update
	 */
	protected void updateDiffHashtable(double elementCount, Hashtable hash) {
		for (Enumeration e = hash.keys(); e.hasMoreElements(); ) {
			String element = (String) e.nextElement();
			double tempStoich = -elementCount * ((Integer) hash.get(element)).doubleValue();
			if (diff.containsKey(element)) {
				tempStoich += ((Double) diff.get(element)).doubleValue();
			}
			diff.put(element, new Double(tempStoich));
		}
		removeZeroEntries(diff);
		return;
	}


	/**
	 *  Remove entries from diff Hashtable whose values equal zero
	 *
	 * @param  hash  The Hashtable to be cleaned.
	 */
	protected void removeZeroEntries(Hashtable hash) {
		HashSet set = new HashSet();
		for (Enumeration e = hash.keys(); e.hasMoreElements(); ) {
			String element = (String) e.nextElement();
			if (((Double) hash.get(element)).doubleValue() == 0.0) {
				set.add(element);
			}
		}
		for (Iterator iter = set.iterator(); iter.hasNext(); ) {
			String element = (String) iter.next();
			hash.remove(element);
		}
		return;
	}


	/**
	 *  Permutate the stoichiometry
	 *
	 *@param  max  The maximal value of stoichiometric coefficients
	 *@return      true if reaction could be balanced with respect to non-O and non-H Atoms
	 */
	public boolean permutateStoichiometries(double max) {

		int noOfEducts = reaction.getReactantCount();
		int noOfProducts = reaction.getProductCount();
		int noOfMolecules = noOfEducts + noOfProducts;
		double[] coefficients = new double[noOfMolecules];

		for (int p = 0; p < noOfMolecules; ++p) {
			coefficients[p] = 1.0;
		}

		int[] permutation = new int[noOfMolecules];
		while (coefficients[0] <= max) {
			PermutationGenerator permutationGenerator = new PermutationGenerator(noOfMolecules);

			while (permutationGenerator.hasMore()) {
				permutation = permutationGenerator.getNext();
				double[] newCoefficients = new double[noOfMolecules];

				for (int i = 0; i < permutation.length; ++i) {
					newCoefficients[i] = coefficients[permutation[i]];
				}
				double[] eductCoefficients = new double[noOfEducts];
				double[] productCoefficients = new double[noOfProducts];
				for (int e = 0; e < noOfEducts; ++e) {
					eductCoefficients[e] = newCoefficients[e];
				}
				for (int p = noOfEducts; p < noOfMolecules; ++p) {
					productCoefficients[p - noOfEducts] = newCoefficients[p];
				}
				reaction.setProductCoefficients(productCoefficients);
				reaction.setReactantCoefficients(eductCoefficients);
				diff = new Hashtable();
				makeDiffHashtable();
				if (containsOnlyOH(diff)) {
					return true;
				}
			}
			Arrays.sort(coefficients);
			coefficients[0] += 1.0;
		}
		return false;
	}

}

