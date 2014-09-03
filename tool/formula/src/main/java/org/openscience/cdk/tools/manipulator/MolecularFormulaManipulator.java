/* Copyright (C) 2007  Miguel Rojasch <miguelrojasch@users.sf.net>
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
 *  */
package org.openscience.cdk.tools.manipulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;

/**
 * Class with convenience methods that provide methods to manipulate
 * {@link IMolecularFormula}'s. For example:
 * 
 *
 * @cdk.module  formula
 * @author      miguelrojasch
 * @cdk.created 2007-11-20
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.formula.MolecularFormulaManipulatorTest")
public class MolecularFormulaManipulator {

	/**
	 *  Checks a set of Nodes for the occurrence of each isotopes
	 *  instance in the molecular formula. In short number of atoms.
	 *
	 * @param   formula  The MolecularFormula to check
	 * @return           The occurrence total
	 */
	@TestMethod("testGetAtomCount_IMolecularFormula")
	public static int getAtomCount(IMolecularFormula formula) {

		int count = 0;
		for (IIsotope isotope : formula.isotopes()) {
			count += formula.getIsotopeCount(isotope);
		}
		return count;
	}

	/**
	 * Checks a set of Nodes for the occurrence of the isotopes in the
	 * molecular formula from a particular IElement. It returns 0 if the
	 * element does not exist. The search is based only on the IElement.
	 *
	 * @param   formula The MolecularFormula to check
	 * @param   element The IElement object
	 * @return          The occurrence of this element in this molecular formula
	 */
	@TestMethod("testGetElementCount_IMolecularFormula_IElement")
	public static int getElementCount(IMolecularFormula formula, IElement element) {

		int count = 0;
		for (IIsotope isotope : formula.isotopes()) {
			if (isotope.getSymbol().equals(element.getSymbol()))
				count += formula.getIsotopeCount(isotope);
		}
		return count;
	}

	/**
	 * Occurrences of a given element from an isotope in a molecular formula.
	 * 
	 * @param  formula the formula
	 * @param  isotope isotope of an element
	 * @return         number of the times the element occurs
	 * @see #getElementCount(IMolecularFormula, IElement)  
	 */
    public static int getElementCount(IMolecularFormula formula, IIsotope isotope) {
        return getElementCount(formula, formula.getBuilder().newInstance(IElement.class, isotope));
	}
    
    /**
	 * Occurrences of a given element in a molecular formula.
	 * 
	 * @param  formula the formula
	 * @param  symbol  element symbol (e.g. C for carbon)
	 * @return         number of the times the element occurs
	 * @see #getElementCount(IMolecularFormula, IElement)  
	 */
    public static int getElementCount(IMolecularFormula formula, String symbol) {
        return getElementCount(formula, formula.getBuilder().newInstance(IElement.class, symbol));
	}

	/**
	 * Get a list of IIsotope from a given IElement which is contained
	 * molecular. The search is based only on the IElement.
	 *
	 * @param   formula The MolecularFormula to check
	 * @param   element The IElement object
	 * @return          The list with the IIsotopes in this molecular formula
	 */
	@TestMethod("testGetIsotopes_IMolecularFormula_IElement")
	public static List<IIsotope> getIsotopes(IMolecularFormula formula, IElement element) {

		List<IIsotope> isotopeList = new ArrayList<IIsotope>();
		for (IIsotope isotope : formula.isotopes()) {
			if (isotope.getSymbol().equals(element.getSymbol()))
				isotopeList.add(isotope);

		}
		return isotopeList;
	}

	/**
	 *  Get a list of all Elements which are contained
	 *  molecular.
	 *
	 *@param   formula The MolecularFormula to check
	 *@return          The list with the IElements in this molecular formula
	 */
	@TestMethod("testElements_IMolecularFormula")
	public static List<IElement> elements(IMolecularFormula formula) {

		List<IElement> elementList = new ArrayList<IElement>();
		List<String> stringList = new ArrayList<String>();
		for (IIsotope isotope : formula.isotopes()) {
			if (!stringList.contains(isotope.getSymbol())) {
				elementList.add(isotope);
				stringList.add(isotope.getSymbol());
			}

		}
		return elementList;
	}

	/**
	 * True, if the MolecularFormula contains the given element as IIsotope object.
	 *
	 * @param  formula   IMolecularFormula molecularFormula
	 * @param  element   The element this MolecularFormula is searched for
	 * @return           True, if the MolecularFormula contains the given element object
	 */
	@TestMethod("testContainsElement_IMolecularFormula_IElement")
	public static boolean containsElement(IMolecularFormula formula, IElement element) {

		for (IIsotope isotope : formula.isotopes()) {
			if (element.getSymbol().equals(isotope.getSymbol())) return true;
		}

		return false;
	}

	/**
	 * Removes all isotopes from a given element in the MolecularFormula.
	 * 
	 * @param  formula   IMolecularFormula molecularFormula
	 * @param  element   The IElement of the IIsotopes to be removed
	 * @return           The molecularFormula with the isotopes removed
	 */
	@TestMethod("testRemoveElement_IMolecularFormula_IElement")
	public static IMolecularFormula removeElement(IMolecularFormula formula, IElement element) {
		for (IIsotope isotope : getIsotopes(formula, element)) {
			formula.removeIsotope(isotope);
		}
		return formula;
	}

	/**
	 * Returns the string representation of the molecule formula.
	 * 
	 * @param formula       The IMolecularFormula Object
	 * @param orderElements The order of Elements
	 * @param setOne        True, when must be set the value 1 for elements with
	 * 					    one atom
	 * @return              A String containing the molecular formula
	 * 
	 * @see #getHTML(IMolecularFormula)
	 * @see #generateOrderEle()
	 * @see #generateOrderEle_Hill_NoCarbons()
	 * @see #generateOrderEle_Hill_WithCarbons()
	 * 
	 */
	@TestMethod("testGetString_IMolecularFormula_arrayString_boolean")
	public static String getString(IMolecularFormula formula, String[] orderElements, boolean setOne) {
		StringBuffer stringMF = new StringBuffer();
		List<IIsotope> isotopesList = putInOrder(orderElements, formula);

		// collect elements in a map - since different isotopes of the
		// same element will get repeated in the formula
		List<String> elemSet = new ArrayList<String>();
		for (IIsotope isotope : isotopesList) {
			String symbol = isotope.getSymbol();
			if (!elemSet.contains(symbol)) elemSet.add(symbol);
		}

		for (String elem : elemSet) {
			int count = 0;
			for (IIsotope isotope : formula.isotopes()) {
				if (isotope.getSymbol().equals(elem)) count += formula.getIsotopeCount(isotope);
			}
			stringMF.append(elem);
			if (!(count == 1 && !setOne))
				stringMF.append(count);
		}
		return stringMF.toString();
	}

	/**
	 * Returns the string representation of the molecule formula.
	 * Based on Hill System. The Hill system is a system of writing
	 * chemical formulas such that the number of carbon atoms in a
	 * molecule is indicated first, the number of hydrogen atoms next,
	 * and then the number of all other chemical elements subsequently,
	 * in alphabetical order. When the formula contains no carbon, all
	 * the elements, including hydrogen, are listed alphabetically.
	 *
	 * @param  formula  The IMolecularFormula Object
	 * @return          A String containing the molecular formula
	 * 
	 * @see #getHTML(IMolecularFormula)
	 */
	@TestMethod("testGetString_IMolecularFormula")
	public static String getString(IMolecularFormula formula) {

		return getString(formula, false);
	}

	/**
	 * Returns the string representation of the molecule formula.
	 * Based on Hill System. The Hill system is a system of writing
	 * chemical formulas such that the number of carbon atoms in a
	 * molecule is indicated first, the number of hydrogen atoms next,
	 * and then the number of all other chemical elements subsequently,
	 * in alphabetical order. When the formula contains no carbon, all
	 * the elements, including hydrogen, are listed alphabetically.
	 *
	 * @param  formula  The IMolecularFormula Object
	 * @param  setOne   True, when must be set the value 1 for elements with
	 * 					one atom
	 * @return          A String containing the molecular formula
	 * 
	 * @see #getHTML(IMolecularFormula)
	 */
	@TestMethod("testGetString_IMolecularFormula_boolean")
	public static String getString(IMolecularFormula formula, boolean setOne) {

		if (containsElement(formula, formula.getBuilder().newInstance(IElement.class,"C")))
			return getString(formula, generateOrderEle_Hill_WithCarbons(), setOne);
		else
			return getString(formula, generateOrderEle_Hill_NoCarbons(), setOne);
	}

	public static List<IIsotope> putInOrder(String[] orderElements, IMolecularFormula formula) {
		List<IIsotope> isotopesList = new ArrayList<IIsotope>();
		for (String orderElement : orderElements) {
			IElement element = formula.getBuilder().newInstance(IElement.class,orderElement);
			if (containsElement(formula, element)) {
				List<IIsotope> isotopes = getIsotopes(formula, element);
				for (IIsotope isotope : isotopes) {
					isotopesList.add(isotope);
				}
			}
		}
		return isotopesList;
	}

	/**
	 * @deprecated  Use {@link #getString(org.openscience.cdk.interfaces.IMolecularFormula)}
	 */
	@TestMethod("testGetHillString_IMolecularFormula")
	@Deprecated 
	public static String getHillString(IMolecularFormula formula) {
		StringBuffer hillString = new StringBuffer();

		Map<String, Integer> hillMap = new TreeMap<String, Integer>();
		for (IIsotope isotope : formula.isotopes()) {
			String symbol = isotope.getSymbol();
			if (hillMap.containsKey(symbol))
				hillMap.put(symbol, hillMap.get(symbol) + formula.getIsotopeCount(isotope));
			else hillMap.put(symbol, formula.getIsotopeCount(isotope));
		}

		// if we have a C append it and also add in the H
		// and then remove these elements
		int count;
		if (hillMap.containsKey("C")) {
			hillString.append('C');
			count = hillMap.get("C");
			if (count> 1) hillString.append(count);
			hillMap.remove("C");
			if (hillMap.containsKey("H")) {
				hillString.append('H');
				count = hillMap.get("H");
				if (count > 1) hillString.append(count);
				hillMap.remove("H");
			}
		}

		// now take all the rest in alphabetical order
		for (String key : hillMap.keySet()) {
			hillString.append(key);
			count = hillMap.get(key);
			if (count > 1) hillString.append(count);
		}
		return hillString.toString();
	}

	/**
	 * Returns the string representation of the molecule formula based on Hill
	 * System with numbers wrapped in &lt;sub&gt;&lt;/sub&gt; tags. Useful for
	 * displaying formulae in Swing components or on the web.
	 * 
	 *
	 * @param   formula  The IMolecularFormula object
	 * @return           A HTML representation of the molecular formula
	 * @see              #getHTML(IMolecularFormula, boolean, boolean)
	 * 
	 */
	@TestMethod("testGetHTML_IMolecularFormula")
	public static String getHTML(IMolecularFormula formula) {
		return getHTML(formula, false, false);
	}

	/**
	 * Returns the string representation of the molecule formula based on Hill
	 * System with numbers wrapped in &lt;sub&gt;&lt;/sub&gt; tags and the
	 * isotope of each Element in &lt;sup&gt;&lt;/sup&gt; tags and the total
	 * charge of IMolecularFormula in &lt;sup&gt;&lt;/sup&gt; tags. Useful for
	 * displaying formulae in Swing components or on the web.
	 * 
	 *
	 * @param   formula  The IMolecularFormula object
	 * @param   chargeB  True, If it has to show the charge
	 * @param   isotopeB True, If it has to show the Isotope mass
	 * @return           A HTML representation of the molecular formula
	 * @see              #getHTML(IMolecularFormula)
	 * 
	 */
	@TestMethod("testGetHTML_IMolecularFormula_boolean_boolean")
	public static String getHTML(IMolecularFormula formula, boolean chargeB, boolean isotopeB) {
		String[] orderElements;
		if (containsElement(formula, formula.getBuilder().newInstance(IElement.class,"C")))
			orderElements = generateOrderEle_Hill_WithCarbons();
		else
			orderElements = generateOrderEle_Hill_NoCarbons();
		return getHTML(formula, orderElements, chargeB, isotopeB);
	}
	
	/**
	 * Returns the string representation of the molecule formula with numbers 
	 * wrapped in &lt;sub&gt;&lt;/sub&gt; tags and the isotope of each Element
	 * in &lt;sup&gt;&lt;/sup&gt; tags and the total charge of IMolecularFormula
	 * in &lt;sup&gt;&lt;/sup&gt; tags. Useful for displaying formulae in Swing
	 * components or on the web.
	 * 
	 *
	 * @param   formula  The IMolecularFormula object
	 * @param   orderElements The order of Elements
	 * @param   chargeB  True, If it has to show the charge
	 * @param   isotopeB True, If it has to show the Isotope mass
	 * @return           A HTML representation of the molecular formula
	 * @see              #getHTML(IMolecularFormula)
	 * 
	 */
	@TestMethod("testGetHTML_IMolecularFormula_arrayString_boolean_boolean")
	public static String getHTML(IMolecularFormula formula, String[] orderElements, boolean chargeB, boolean isotopeB) {
		StringBuilder sb = new StringBuilder();
		for (String orderElement : orderElements) {
			IElement element = formula.getBuilder().newInstance(IElement.class,orderElement);
			if (containsElement(formula, element)) {
				if (!isotopeB) {
					sb.append(element.getSymbol());
                    int n = getElementCount(formula, element);
                    if (n > 1) {
                        sb.append("<sub>").append(n).append("</sub>");
                    }
				} else {
					for (IIsotope isotope : getIsotopes(formula, element)) {
                        sb.append("<sup>").append(isotope.getMassNumber()).append("</sup>");
                        sb.append(isotope.getSymbol());
                        int n = formula.getIsotopeCount(isotope);
                        if (n > 1) {
                            sb.append("<sub>").append(n).append("</sub>");
                        }
					}
				}
			}
		}
        
		if (chargeB) {
			Integer charge = formula.getCharge();
			if (charge == CDKConstants.UNSET || charge == 0) {
				return sb.toString();
			} else if (charge < 0) {
				sb.append("<sup>").append(charge * -1).append('-').append("</sup>");
			} else {
                sb.append("<sup>").append(charge).append('+').append("</sup>");
			}
		}
		return sb.toString();
	}

	/**
	 * Construct an instance of IMolecularFormula, initialized with a molecular
	 * formula string. The string is immediately analyzed and a set of Nodes
	 * is built based on this analysis
	 * <p> The hydrogens must be implicit.
	 *
	 * @param  stringMF   The molecularFormula string
	 * @param builder a IChemObjectBuilder which is used to construct atoms
	 * @return            The filled IMolecularFormula
	 * @see               #getMolecularFormula(String,IMolecularFormula)
	 */
	@TestMethod("testGetMolecularFormula_String_IChemObjectBuilder")
	public static IMolecularFormula getMolecularFormula(String stringMF,
			IChemObjectBuilder builder) {
		return getMolecularFormula(stringMF, false, builder);
	}

	/**
	 * Construct an instance of IMolecularFormula, initialized with a molecular
	 * formula string. The string is immediately analyzed and a set of Nodes
	 * is built based on this analysis. The hydrogens must be implicit. Major
	 * isotopes are being used.
	 *
	 * @param  stringMF   The molecularFormula string
	 * @param builder a IChemObjectBuilder which is used to construct atoms
	 * @return The filled IMolecularFormula
	 * @see               #getMolecularFormula(String,IMolecularFormula)
	 */
	@TestMethod("testGetMajorIsotopeMolecularFormula_String_IChemObjectBuilder")
	public static IMolecularFormula getMajorIsotopeMolecularFormula(String stringMF,
			IChemObjectBuilder builder) {
		return getMolecularFormula(stringMF, true, builder);
	}

	private static IMolecularFormula getMolecularFormula(String stringMF,
			boolean assumeMajorIsotope, IChemObjectBuilder builder) {
		IMolecularFormula formula = builder.newInstance(IMolecularFormula.class);

		return getMolecularFormula(stringMF, formula, assumeMajorIsotope);
	}

	/**
	 * add in a instance of IMolecularFormula the elements extracts form
	 * molecular formula string. The string is immediately analyzed and a set of Nodes
	 * is built based on this analysis
	 * <p> The hydrogens must be implicit.
	 *
	 * @param  stringMF   The molecularFormula string
	 * @return            The filled IMolecularFormula
	 * @see               #getMolecularFormula(String, IChemObjectBuilder)
	 */
	@TestMethod("testGetMolecularFormula_String_IMolecularFormula")
	public static IMolecularFormula getMolecularFormula(String stringMF, IMolecularFormula formula) {
		return getMolecularFormula(stringMF, formula, false);
	}

	/**
	 * Add to an instance of IMolecularFormula the elements extracts form
	 * molecular formula string. The string is immediately analyzed and a set of Nodes
	 * is built based on this analysis. The hydrogens are assumed to be implicit.
	 * The boolean indicates if the major isotope is to be assumed, or if no
	 * assumption is to be made.
	 *
	 * @param  stringMF           The molecularFormula string
	 * @param  assumeMajorIsotope If true, it will take the major isotope for each element
	 * @return                    The filled IMolecularFormula
	 * @see                       #getMolecularFormula(String, org.openscience.cdk.interfaces.IChemObjectBuilder)
	 * @see #getMolecularFormula(String, boolean, org.openscience.cdk.interfaces.IChemObjectBuilder)
	 */
	private static IMolecularFormula getMolecularFormula(String stringMF, IMolecularFormula formula, boolean assumeMajorIsotope) {

		if (stringMF.contains(".") || stringMF.contains("(") || stringMF.charAt(0) >= '0' && stringMF.charAt(0) <= '9')
			stringMF = simplifyMolecularFormula(stringMF);


		// Extract charge from String when contains []X- format
		Integer charge = null;
		if ((stringMF.contains("[") && stringMF.contains("]") )
				&& (stringMF.contains("+") || stringMF.contains("-"))) {
			charge = extractCharge(stringMF);
			stringMF = cleanMFfromCharge(stringMF);
		}

		// FIXME: MF: variables with lower case first char
		char ThisChar;
		/*
		 *  Buffer for
		 */
		String RecentElementSymbol = "";
		String RecentElementCountString = "0";
		/*
		 *  String to be converted to an integer
		 */
		int RecentElementCount;

		if (stringMF.length() == 0) {
			return null;
		}

		for (int f = 0; f < stringMF.length(); f++) {
			ThisChar = stringMF.charAt(f);
			if (f < stringMF.length()) {
				if (ThisChar >= 'A' && ThisChar <= 'Z') {
					/*
					 *  New Element begins
					 */
					RecentElementSymbol = java.lang.String.valueOf(ThisChar);
					RecentElementCountString = "0";
				}
				if (ThisChar >= 'a' && ThisChar <= 'z') {
					/*
					 *  Two-letter Element continued
					 */
					RecentElementSymbol += ThisChar;
				}
				if (ThisChar >= '0' && ThisChar <= '9') {
					/*
					 *  Two-letter Element continued
					 */
					RecentElementCountString += ThisChar;
				}
			}
			if (f == stringMF.length() - 1 || (stringMF.charAt(f + 1) >= 'A' && stringMF.charAt(f + 1) <= 'Z')) {
				/*
				 *  Here an element symbol as well as its number should have been read completely
				 */
				RecentElementCount = Integer.valueOf(RecentElementCountString);
				if (RecentElementCount == 0) {
					RecentElementCount = 1;
				}

				IIsotope isotope = formula.getBuilder().newInstance(IIsotope.class, RecentElementSymbol);
				if (assumeMajorIsotope) {
					try {
						isotope = Isotopes.getInstance().getMajorIsotope(RecentElementSymbol);
					} catch (IOException e) {
						throw new RuntimeException("Cannot load the IsotopeFactory");
					}
				}
				formula.addIsotope(isotope, RecentElementCount);

			}
		}
		if (charge != null)
			formula.setCharge(charge);
		return formula;
	}

	/**
	 * Extract the molecular formula when it is defined with charge. e.g. [O3S]2-.
	 * 
	 * @param formula  The formula to inspect
	 * @return         The corrected formula
	 */
	private static String cleanMFfromCharge(String formula) {
		if (!(formula.contains("[") && formula.contains("]") ))
			return formula;
		boolean startBreak = false;
		String finalFormula = "";
		for (int f = 0; f < formula.length(); f++) {
			char thisChar = formula.charAt(f);
			if (thisChar == '[') {
				// start
				startBreak = true;
			} else if (thisChar == ']') {
				break;
			} else if (startBreak)
				finalFormula += thisChar;
		}
		return finalFormula;
	}

	/**
	 * Extract the charge given a molecular formula format [O3S]2-.
	 * 
	 * @param formula The formula to inspect
	 * @return        The charge
	 */
	private static int extractCharge(String formula) {

		if (!(formula.contains("[") && formula.contains("]")
				&& (formula.contains("+") || formula.contains("-"))))
			return 0;

		boolean finishBreak = false;
		String multiple = "";
		for (int f = 0; f < formula.length(); f++) {
			char thisChar = formula.charAt(f);
			if (thisChar == ']') {
				// finish
				finishBreak = true;
			} else if (thisChar == '-') {
				multiple = thisChar + multiple;
				break;
			} else if (thisChar == '+' ) {
				break;
			} else if (finishBreak) {
				multiple += thisChar;
			}
		}
		if (multiple.isEmpty() || multiple.equals("-"))
			multiple += 1;
		return Integer.valueOf(multiple);
	}

	/**
	 * Get the summed exact mass of all isotopes from an MolecularFormula. It
	 * assumes isotope masses to be preset, and returns 0.0 if not.
	 * 
	 * @param  formula The IMolecularFormula to calculate
	 * @return         The summed exact mass of all atoms in this MolecularFormula
	 */
	@TestMethod("testGetTotalExactMass_IMolecularFormula")
	public static double getTotalExactMass(IMolecularFormula formula) {
		Double mass = 0.0;

		for (IIsotope isotope : formula.isotopes()) {
			if (isotope.getExactMass() == CDKConstants.UNSET) {
				try {
					mass += Isotopes.getInstance().getMajorIsotope(isotope.getSymbol()).getExactMass() *
					formula.getIsotopeCount(isotope);
				} catch (IOException e) {
					throw new RuntimeException("Could not instantiate the IsotopeFactory.");
				}
			} else
				mass += isotope.getExactMass() * formula.getIsotopeCount(isotope);
		}
		if (formula.getCharge() != null)
			mass = correctMass(mass,formula.getCharge());
		return mass;
	}

	/**
	 * Correct the mass according the charge of the IMmoleculeFormula.
	 * Negative charge will add the mass of one electron to the mass.
	 * 
	 * @param mass      The mass to correct
	 * @param charge    The charge
	 * @return          The mass with the correction
	 */
	private static double correctMass(double mass, Integer charge) {
		double massE = 0.00054857990927;
		if (charge > 0)
			mass -= massE*charge;
		else if (charge < 0)
			mass += massE*Math.abs(charge);
		return mass;
	}

	/**
	 * Get the summed mass number of all isotopes from an MolecularFormula. It
	 * assumes isotope masses to be preset, and returns 0.0 if not.
	 * 
	 * @param  formula The IMolecularFormula to calculate
	 * @return         The summed nominal mass of all atoms in this MolecularFormula
	 */
	@TestMethod("testGetTotalMassNumber_IMolecularFormula")
	public static double getTotalMassNumber(IMolecularFormula formula) {
		double mass = 0.0;
		for (IIsotope isotope : formula.isotopes()) {
			try {
				IIsotope isotope2 = Isotopes.getInstance().getMajorIsotope(isotope.getSymbol());
				mass += isotope2.getMassNumber() * formula.getIsotopeCount(isotope);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mass;
	}

	/**
	 * Get the summed natural mass of all elements from an MolecularFormula.
	 * 
	 * @param  formula The IMolecularFormula to calculate
	 * @return         The summed exact mass of all atoms in this MolecularFormula
	 */
	@TestMethod("testGetNaturalExactMass_IMolecularFormula")
	public static double getNaturalExactMass(IMolecularFormula formula) {
		double mass = 0.0;
		IsotopeFactory factory;
		try {
			factory = Isotopes.getInstance();
		} catch (IOException e) {
			throw new RuntimeException("Could not instantiate the IsotopeFactory.");
		}
		for (IIsotope isotope : formula.isotopes()) {
			IElement isotopesElement = isotope.getBuilder().newInstance(IElement.class,isotope);
			mass += factory.getNaturalMass(isotopesElement) * formula.getIsotopeCount(isotope);
		}
		return mass;
	}

	/**
	 * Get the summed major isotopic mass of all elements from an MolecularFormula.
	 *
	 * @param  formula The IMolecularFormula to calculate
	 * @return         The summed exact major isotope masses of all atoms in this MolecularFormula
	 */
	@TestMethod("testGetMajorIsotopeMass_IMolecularFormula")
	public static double getMajorIsotopeMass(IMolecularFormula formula) {
		double mass = 0.0;
		IsotopeFactory factory;
		try {
			factory = Isotopes.getInstance();
		} catch (IOException e) {
			throw new RuntimeException("Could not instantiate the IsotopeFactory.");
		}
		for (IIsotope isotope : formula.isotopes()) {
			IIsotope major = factory.getMajorIsotope(isotope.getSymbol());
			mass += major.getExactMass() * formula.getIsotopeCount(isotope);
		}
		return mass;
	}

	/**
	 * Get the summed natural abundance of all isotopes from an MolecularFormula. Assumes
	 * abundances to be preset, and will return 0.0 if not.
	 * 
	 * @param  formula The IMolecularFormula to calculate
	 * @return         The summed natural abundance of all isotopes in this MolecularFormula
	 */
	@TestMethod("testGetTotalNaturalAbundance_IMolecularFormula")
	public static double getTotalNaturalAbundance(IMolecularFormula formula) {
		double abundance =  1.0;
		for (IIsotope isotope : formula.isotopes()) {
			if (isotope.getNaturalAbundance() == null) return 0.0;
			abundance = abundance * Math.pow(isotope.getNaturalAbundance(), formula.getIsotopeCount(isotope));
		}
		return abundance/Math.pow(100,getAtomCount(formula));
	}

	/**
	 * Returns the number of double bond equivalents in this molecule.
	 *
	 * @param  formula  The IMolecularFormula to calculate
	 * @return          The number of DBEs
	 * @throws          CDKException if DBE cannot be be evaluated
	 * 
	 * @cdk.keyword DBE
	 * @cdk.keyword double bond equivalent
	 */
	@TestMethod("testGetDBE_IMolecularFormula")
	public static double getDBE(IMolecularFormula formula) throws CDKException{
		int valencies[] = new int[5];
		IAtomContainer ac = getAtomContainer(formula);
		AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/structgen_atomtypes.xml", ac.getBuilder());

		for (int f = 0; f < ac.getAtomCount(); f++) {
			IAtomType[] types = factory.getAtomTypes(ac.getAtom(f).getSymbol());
			if (types.length==0)
				throw new CDKException("Calculation of double bond equivalents not possible due to problems with element "+ac.getAtom(f).getSymbol());
//			valencies[(int) (types[0].getBondOrderSum() + ac.getAtom(f).getFormalCharge())]++;
			valencies[types[0].getBondOrderSum().intValue()]++;
		}
		return  1 + (valencies[4]) + (valencies[3] /2) - (valencies[1] /2);
	}

	/**
	 * Method that actually does the work of convert the atomContainer
	 * to IMolecularFormula.
	 * <p> The hydrogens must be implicit.
	 *
	 * @param 	atomContainer     IAtomContainer object
	 * @return	a molecular formula object
	 * @see		#getMolecularFormula(IAtomContainer,IMolecularFormula)
	 */
	@TestMethod("testGetMolecularFormula_IAtomContainer")
	public static IMolecularFormula getMolecularFormula(IAtomContainer atomContainer) {

		IMolecularFormula formula = atomContainer.getBuilder().newInstance(IMolecularFormula.class);

		return getMolecularFormula(atomContainer, formula);
	}

	/**
	 * Method that actually does the work of convert the atomContainer
	 * to IMolecularFormula given a IMolecularFormula.
	 * <p> The hydrogens must be implicit.
	 *
	 * @param  atomContainer     IAtomContainer object
	 * @param  formula           IMolecularFormula molecularFormula to put the new Isotopes
	 * @return                   the filled AtomContainer
	 * @see                      #getMolecularFormula(IAtomContainer)
	 */
	@TestMethod("testGetMolecularFormula_IAtomContainer_IMolecularFormula")
    public static IMolecularFormula getMolecularFormula(IAtomContainer atomContainer, IMolecularFormula formula) {
        int charge = 0;
        IAtom hAtom = null;
        for (IAtom iAtom : atomContainer.atoms()) {
            formula.addIsotope(iAtom);
            charge += iAtom.getFormalCharge();

            if (iAtom.getImplicitHydrogenCount() != null &&
                    (iAtom.getImplicitHydrogenCount() > 0)) {
                if (hAtom == null) hAtom =
                        atomContainer.getBuilder().newInstance(IAtom.class, "H");
                formula.addIsotope(hAtom, iAtom.getImplicitHydrogenCount());
            }
        }
        formula.setCharge(charge);
        return formula;
    }

    /**
	 * Method that actually does the work of convert the IMolecularFormula
	 * to IAtomContainer.
	 * <p> The hydrogens must be implicit.
	 *
	 * @param  formula  IMolecularFormula object
	 * @return          the filled AtomContainer
	 * @see             #getAtomContainer(IMolecularFormula, IAtomContainer)
	 */
	@TestMethod("testGetAtomContainer_IMolecularFormula")
	public static IAtomContainer getAtomContainer(IMolecularFormula formula) {

		IAtomContainer atomContainer = formula.getBuilder().newInstance(IAtomContainer.class);
		return getAtomContainer(formula, atomContainer);
	}

	/**
	 * Method that actually does the work of convert the IMolecularFormula
	 * to IAtomContainer given a IAtomContainer.
	 * <p> The hydrogens must be implicit.
	 *
	 * @param  formula           IMolecularFormula object
	 * @param  atomContainer     IAtomContainer to put the new Elements
	 * @return                   the filled AtomContainer
	 * @see                      #getAtomContainer(IMolecularFormula)
	 */
	@TestMethod("testGetAtomContainer_IMolecularFormula_IAtomContainer")
	public static IAtomContainer getAtomContainer(IMolecularFormula formula, IAtomContainer atomContainer) {

		for (IIsotope isotope : formula.isotopes()) {
			int occur = formula.getIsotopeCount(isotope);
			for (int i = 0; i < occur; i++) {
				IAtom atom = formula.getBuilder().newInstance(IAtom.class,isotope);
				atomContainer.addAtom(atom);
			}
		}
		return atomContainer;
	}

	/**
	 * Converts a formula string (like "C2H4") into an atom container with atoms
	 * but no bonds.
	 * 
	 * @param formulaString the formula to convert
	 * @param builder a chem object builder
	 * @return atoms wrapped in an atom container
	 */
	@TestMethod("testGetAtomContainer_String_IChemObjectBuilder")
	public static IAtomContainer getAtomContainer(String formulaString, IChemObjectBuilder builder) {
	    return MolecularFormulaManipulator.getAtomContainer(
	            MolecularFormulaManipulator.getMolecularFormula(formulaString, builder));
	}
	/**
	 * Returns the Elements ordered according to (approximate) probability of occurrence.
	 * 
	 * <p>This begins with the "elements of life" C, H, O, N, (Si, P, S, F, Cl),
	 * then continues with the "common" chemical synthesis ingredients, closing off
	 * with the tail-end of the periodic table in atom-number order and finally
	 * the generic R-group.
	 * 
	 * @return  fixed-order array
	 * 
	 */
	public static String[] generateOrderEle(){
		return new String[]{
				// Elements of life
				"C", "H", "O", "N", "Si", "P", "S", "F", "Cl",

				"Br", "I", "Sn", "B", "Pb", "Tl", "Ba", "In", "Pd",
				"Pt", "Os", "Ag", "Zr", "Se", "Zn", "Cu", "Ni", "Co",
				"Fe", "Cr", "Ti", "Ca", "K", "Al", "Mg", "Na", "Ce",
				"Hg", "Au", "Ir", "Re", "W", "Ta", "Hf", "Lu", "Yb",
				"Tm", "Er", "Ho", "Dy", "Tb", "Gd", "Eu", "Sm", "Pm",
				"Nd", "Pr", "La", "Cs", "Xe", "Te", "Sb", "Cd", "Rh",
				"Ru", "Tc", "Mo", "Nb", "Y", "Sr", "Rb", "Kr", "As",
				"Ge", "Ga", "Mn", "V", "Sc", "Ar", "Ne", "He", "Be", "Li",

				// rest of periodic table, in atom-number order.
				"Bi", "Po", "At", "Rn",
				// row-7 elements (including f-block)
				"Fr", "Ra", "Ac", "Th", "Pa", "U", "Np", "Pu", "Am", "Cm", "Bk", "Cf", "Es", "Fm", "Md", "No", "Lr",
				"Rf", "Db", "Sg", "Bh", "Hs", "Mt", "Ds", "Rg", "Cn",

				// The "odd one out": an unspecified R-group
				"R"
		};

	}
	/**
	 * Returns the Elements in Hill system order for non-carbon-containing formulas
	 * (i.e. strict alphabetical order, with one-letter elements preceding two-letter elements.)
	 * The generic R-group is treated specially and comes last.
	 *
	 * @return  Elements in Hill system order (strictly alphabetical), with generic R-groups last.
	 */
	private static String[] generateOrderEle_Hill_NoCarbons(){
		return new String[]{
				"Ac", "Ag", "Al", "Am", "Ar", "As", "At", "Au",
				"B", "Ba", "Be", "Bh", "Bi", "Bk", "Br",
				"C", "Ca", "Cd", "Ce", "Cf", "Cl", "Cm", "Cn", "Co", "Cr", "Cs", "Cu",
				"Db", "Ds", "Dy",
				"Er", "Es", "Eu",
				"F", "Fe", "Fm", "Fr",
				"Ga", "Gd", "Ge",
				"H", "He", "Hf", "Hg", "Ho", "Hs",
				"I", "In", "Ir",
				"K", "Kr",
				"La", "Li", "Lr", "Lu",
				"Md", "Mg", "Mn", "Mo", "Mt",
				"N", "Na", "Nb", "Nd", "Ne", "Ni", "No", "Np",
				"O", "Os",
				"P", "Pa", "Pb", "Pd", "Pm", "Po", "Pr", "Pt", "Pu",
				"Ra", "Rb", "Re", "Rf", "Rg", "Rh", "Rn", "Ru",
				"S", "Sb", "Sc", "Se", "Sg", "Si", "Sm", "Sn", "Sr",
				"Ta", "Tb", "Tc", "Te", "Th", "Ti", "Tl", "Tm",
				"U", "V", "W", "Xe", "Y", "Yb", "Zn", "Zr",
				// The "odd one out": an unspecified R-group
				"R"
		};
	}

	/**
	 * Returns the Elements in Hill system order for carbon-containing formulas
	 * (i.e. first carbon and hydrogen, and then the rest of the elements in strict
	 * alphabetical order, with one-letter elements preceding two-letter elements.)
	 * The generic R-group is treated specially and comes last.
	 * 
	 * @return  Elements in Hill system order with carbons and hydrogens
	 * 		first (and generic R-groups last).
	 */
	private static String[] generateOrderEle_Hill_WithCarbons(){
		return new String[]{
				"C", "H",
				"Ac", "Ag", "Al", "Am", "Ar", "As", "At", "Au",
				"B", "Ba", "Be", "Bh", "Bi", "Bk", "Br",
				"Ca", "Cd", "Ce", "Cf", "Cl", "Cm", "Cn", "Co", "Cr", "Cs", "Cu",
				"Db", "Ds", "Dy",
				"Er", "Es", "Eu",
				"F", "Fe", "Fm", "Fr",
				"Ga", "Gd", "Ge",
				"He", "Hf", "Hg", "Ho", "Hs",
				"I", "In", "Ir",
				"K", "Kr",
				"La", "Li", "Lr", "Lu",
				"Md", "Mg", "Mn", "Mo", "Mt",
				"N", "Na", "Nb", "Nd", "Ne", "Ni", "No", "Np",
				"O", "Os",
				"P", "Pa", "Pb", "Pd", "Pm", "Po", "Pr", "Pt", "Pu",
				"Ra", "Rb", "Re", "Rf", "Rg", "Rh", "Rn", "Ru",
				"S", "Sb", "Sc", "Se", "Sg", "Si", "Sm", "Sn", "Sr",
				"Ta", "Tb", "Tc", "Te", "Th", "Ti", "Tl", "Tm",
				"U", "V", "W", "Xe", "Y", "Yb", "Zn", "Zr",
				// The "odd one out": an unspecified R-group
				"R"
		};
	}

	/**
	 * Compare two IMolecularFormula looking at type and number of IIsotope and
	 * charge of the formula.
	 * 
	 * @param formula1   The first IMolecularFormula
	 * @param formula2   The second IMolecularFormula
	 * @return           True, if the both IMolecularFormula are the same
	 */
	@TestMethod("testCompare_IMolecularFormula_IMolecularFormula")
	public static boolean compare(IMolecularFormula formula1, IMolecularFormula formula2) {

		if (formula1.getCharge() != formula2.getCharge())
			return false;

		if (formula1.getIsotopeCount() != formula2.getIsotopeCount())
			return false;

		for (IIsotope isotope : formula1.isotopes()) {
			if (!formula2.contains(isotope))
				return false;

			if (formula1.getIsotopeCount(isotope) != formula2.getIsotopeCount(isotope))
				return false;

		}

		for (IIsotope isotope : formula2.isotopes()) {
			if (!formula1.contains(isotope))
				return false;
			if (formula2.getIsotopeCount(isotope) != formula1.getIsotopeCount(isotope))
				return false;
		}

		return true;
	}

	/**
	 * Returns a set of nodes excluding all the hydrogens.
	 * 
	 * @param   formula The IMolecularFormula
	 * @return          The heavyElements value into a List
	 * 
	 * @cdk.keyword    hydrogen, removal
	 */
	@TestMethod("testGetHeavyElements_IMolecularFormula")
	public static List<IElement> getHeavyElements(IMolecularFormula formula) {
		List<IElement> newEle = new ArrayList<IElement>();
		for (IElement element : elements(formula)) {
			if (!element.getSymbol().equals("H")) {
				newEle.add(element);
			}
		}
		return newEle;
	}

	/**
	 * Simplify the molecular formula. E.g the dot '.' character convention is
	 * used when dividing a formula into parts. In this case any numeral following a dot refers
	 * to all the elements within that part of the formula that follow it.
	 * 
	 * @param formula  The molecular formula
	 * @return         The simplified molecular formula
	 */
	@TestMethod("testSimplifyMolecularFormula_String")
	public static String simplifyMolecularFormula(String formula) {
		String newFormula = formula;
		char thisChar;

		if (formula.contains(" ")) {
			newFormula = newFormula.replace(" ", "");
		}
		if (!formula.contains("."))
			return breakExtractor(formula);

		List<String> listMF = new ArrayList<String>();
		while(newFormula.contains(".")) {
			int pos = newFormula.indexOf(".");
			String thisFormula = newFormula.substring(0, pos);
			if (thisFormula.charAt(0) >= '0' && thisFormula.charAt(0) <= '9')
				thisFormula = multipleExtractor(thisFormula);

			if (thisFormula.contains("("))
				thisFormula = breakExtractor(thisFormula);

			listMF.add(thisFormula);
			thisFormula = newFormula.substring(pos+1,newFormula.length());
			if (!thisFormula.contains(".")) {

				if (thisFormula.charAt(0) >= '0' && thisFormula.charAt(0) <= '9')
					thisFormula = multipleExtractor(thisFormula);

				if (thisFormula.contains("("))
					thisFormula = breakExtractor(thisFormula);

				listMF.add(thisFormula);
			}
			newFormula = thisFormula;
		}
		if (newFormula.contains("("))
			newFormula = breakExtractor(newFormula);

		String recentElementSymbol = "";
		String recentElementCountString = "0";

		List<String> eleSymb = new ArrayList<String>();
		List<Integer> eleCount = new ArrayList<Integer>();
		for (int i = 0 ; i < listMF.size(); i++) {
			String thisFormula = listMF.get(i);
			for (int f = 0; f < thisFormula.length(); f++) {
				thisChar = thisFormula.charAt(f);
				if (f < thisFormula.length()) {
					if (thisChar >= 'A' && thisChar <= 'Z') {
						recentElementSymbol = String.valueOf(thisChar);
						recentElementCountString = "0";
					}
					if (thisChar >= 'a' && thisChar <= 'z') {
						recentElementSymbol += thisChar;
					}
					if (thisChar >= '0' && thisChar <= '9') {
						recentElementCountString += thisChar;
					}
				}
				if (f == thisFormula.length() - 1 || (thisFormula.charAt(f + 1) >= 'A' && thisFormula.charAt(f + 1) <= 'Z')) {
					int posit = eleSymb.indexOf(recentElementSymbol);
					int count = Integer.valueOf(recentElementCountString);
					if (posit == -1) {
						eleSymb.add(recentElementSymbol);
						eleCount.add(count);
					} else {
						int countP = Integer.valueOf(recentElementCountString);
						if (countP == 0)
							countP = 1;
						int countA = eleCount.get(posit);
						if (countA == 0)
							countA = 1;
						int value = countP+countA;
						eleCount.remove(posit);
						eleCount.add(posit,value);
					}

				}
			}
		}
		String newF = "";
		for (int i = 0 ; i < eleCount.size(); i++) {
			String element = eleSymb.get(i);
			int num = eleCount.get(i);
			if (num == 0)
				newF += element;
			else
				newF += element+num;

		}
		return newF;
	}

	/**
	 * The parenthesis convention is used to show a quantity by which a formula is multiplied.
	 * For example: (C12H20O11)2 really means that a C24H40O22 unit.
	 * 
	 * @param formula Formula to correct
	 * @return        Formula with the correction
	 */
	private static String breakExtractor(String formula) {
		boolean finalBreak = false;
		String recentformula = "";
		String multiple = "0";
		for (int f = 0; f < formula.length(); f++) {
			char thisChar = formula.charAt(f);
			if (thisChar == '(') {
				// start
			} else if (thisChar == ')') {
				// final
				finalBreak = true;
			} else if (!finalBreak) {
				recentformula += thisChar;
			} else {
				multiple += thisChar;
			}
		}

		String finalformula = muliplier(recentformula, Integer.valueOf(multiple));
		return finalformula;
	}

	/**
	 * The starting with numeric value is used to show a quantity by which a formula is multiplied.
	 * For example: 2H2O really means that a H4O2 unit.
	 * 
	 * @param formula Formula to correct
	 * @return        Formula with the correction
	 */
	private static String multipleExtractor(String formula) {
		String recentCompoundCount = "0";
		String recentCompound = "";

		boolean found = false;
		for (int f = 0; f < formula.length(); f++) {
			char thisChar = formula.charAt(f);
			if (thisChar >= '0' && thisChar <= '9') {
				if (!found)
					recentCompoundCount += thisChar;
				else
					recentCompound += thisChar;

			} else {
				found = true;
				recentCompound += thisChar;
			}
		}
		return muliplier(recentCompound, Integer.valueOf(recentCompoundCount));
	}

	/**
	 * This method multiply all the element over a value.
	 * 
	 * @param formula Formula to correct
	 * @param factor  Factor to multiply
	 * @return        Formula with the correction
	 */
	private static String muliplier(String formula, int factor) {
		String finalformula = "";
		String recentElementSymbol = "";
		String recentElementCountString = "0";
		for (int f = 0; f < formula.length(); f++) {
			char thisChar = formula.charAt(f);
			if (f < formula.length()) {
				if (thisChar >= 'A' && thisChar <= 'Z') {
					recentElementSymbol = String.valueOf(thisChar);
					recentElementCountString = "0";
				}
				if (thisChar >= 'a' && thisChar <= 'z') {
					recentElementSymbol += thisChar;
				}
				if (thisChar >= '0' && thisChar <= '9') {
					recentElementCountString += thisChar;
				}
			}
			if (f == formula.length() - 1 || (formula.charAt(f + 1) >= 'A' && formula.charAt(f + 1) <= 'Z')) {
				Integer recentElementCount = Integer.valueOf(recentElementCountString);
				if (recentElementCount == 0)
					finalformula += recentElementSymbol+factor;
				else
					finalformula += recentElementSymbol+recentElementCount*factor;
			}
		}
		return finalformula;
	}
}

