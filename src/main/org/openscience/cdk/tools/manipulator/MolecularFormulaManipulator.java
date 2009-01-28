/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2007  Miguel Rojasch <miguelrojasch@users.sf.net>
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class with convenience methods that provide methods to manipulate
 * {@link IMolecularFormula}'s. For example:
 * 
 *
 * @cdk.module  formula
 * @author      miguelrojasch
 * @cdk.created 2007-11-20
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
	public static int getAtomCount(IMolecularFormula formula){
		
		int count = 0;
        for (IIsotope isotope : formula.isotopes()) {
            count += formula.getIsotopeCount(isotope);
        }
		return count;
	}
	
	/**
	 *  Checks a set of Nodes for the occurrence of the isotopes in the 
	 *  molecular formula from a particular IElement. It returns 0 if the
	 *  element does not exist. The search is based only on the IElement.
	 *
	 *@param   formula The MolecularFormula to check
	 *@param   element The IElement object
	 *@return          The occurrence of this element in this molecular formula
	 */
	@TestMethod("testGetElementCount_IMolecularFormula_IElement")
	public static int getElementCount(IMolecularFormula formula, IElement element){
		
		int count = 0;
        for (IIsotope isotope : formula.isotopes()) {
            if (isotope.getSymbol().equals(element.getSymbol()))
                count += formula.getIsotopeCount(isotope);
        }
		return count;
	}
	
	/**
	 *  Get a list of IIsotope from a given IElement which is contained  
	 *  molecular. The search is based only on the IElement.
	 *
	 *@param   formula The MolecularFormula to check
	 *@param   element The IElement object
	 *@return          The list with the IIsotopes in this molecular formula
	 */
	@TestMethod("testGetIsotopes_IMolecularFormula_IElement")
	public static List<IIsotope> getIsotopes(IMolecularFormula formula, IElement element){
		
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
	public static List<IElement> elements(IMolecularFormula formula){
		
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
	 *  True, if the MolecularFormula contains the given element as IIsotope object.
	 *
	 * @param  formula   IMolecularFormula molecularFormula
	 * @param  element   The element this MolecularFormula is searched for
	 * @return           True, if the MolecularFormula contains the given element object
	 */
	@TestMethod("testContainsElement_IMolecularFormula_IElement")
	public static boolean containsElement(IMolecularFormula formula, IElement element){

        for (IIsotope isotope : formula.isotopes()) {
            if (element.getSymbol().equals(isotope.getSymbol())) return true;
        }
		
		return false;
	}
	
	/**
	 *  Removes all isotopes from a given element in the MolecularFormula.
	 *  
	 * @param  formula   IMolecularFormula molecularFormula
	 * @param  element   The IElement of the IIsotopes to be removed
	 * @return           The molecularFormula with the isotopes removed
	 */
	@TestMethod("testRemoveElement_IMolecularFormula_IElement")
	public static IMolecularFormula removeElement(IMolecularFormula formula, IElement element){
        for (IIsotope isotope : getIsotopes(formula, element)) {
            formula.removeIsotope(isotope);
        }
		return formula;
	}
	
	/**
	 * Returns the string representation of the molecule formula.
	 * 
	 * @param  formula      The IMolecularFormula Object 
	 * @param orderElements The order of Elements
	 * @return              A String containing the molecular formula
	 * 
	 * @see #getHTML(IMolecularFormula)
	 * @see #generateOrderEle()
	 * @see #generateOrderEle_Hill_NoCarbons()
	 * @see #generateOrderEle_Hill_WithCarbons()
	 * 
	 */
	@TestMethod("testGetString_IMolecularFormula_String")
	public static String getString(IMolecularFormula formula, String[] orderElements ){
		String stringMF = "";
		List<IIsotope> isotopesList = putInOrder(orderElements, formula);
        for (IIsotope isotope : isotopesList) {
            stringMF = stringMF + isotope.getSymbol() + getElementCount(formula, isotope);
        }
		return stringMF;
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
		
		if(containsElement(formula, formula.getBuilder().newElement("C")))
			return getString(formula, generateOrderEle_Hill_WithCarbons());
		else 
			return getString(formula, generateOrderEle_Hill_NoCarbons());
	}
	
	public static List<IIsotope> putInOrder(String[] orderElements, IMolecularFormula formula) {
		List<IIsotope> isotopesList = new ArrayList<IIsotope>();
        for (String orderElement : orderElements) {
            IElement element = formula.getBuilder().newElement(orderElement);
            if (containsElement(formula, element)) {
                List<IIsotope> isotopes = getIsotopes(formula, element);
                for (IIsotope isotope : isotopes) {
                    isotopesList.add(isotope);
                }
            }
        }
		return isotopesList;
	}
	
	@TestMethod("testGetHillString_IMolecularFormula")
	public static String getHillString(IMolecularFormula formula){
		return null;
	}

	/**
	 * Returns the string representation of the molecule formula with
	 * numbers wrapped in &lt;sub&gt;&lt;/sub&gt; tags. 
	 * Useful for displaying formulae in Swing components or on the web.
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
	 * Returns the string representation of the molecule formula with
	 * numbers wrapped in &lt;sub&gt;&lt;/sub&gt; tags and the isotope
	 * of each Element in &lt;sup&gt;&lt;/sup&gt; tags and the total
	 * charge of IMolecularFormula in &lt;sup&gt;&lt;/sup&gt; tags. 
	 * Useful for displaying formulae in Swing components or on the web.
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
		String htmlString = "";
		String[] orderElements = generateOrderEle();
        for (String orderElement : orderElements) {
            IElement element = formula.getBuilder().newElement(orderElement);
            if (containsElement(formula, element)) {
                if (!isotopeB) {
                    String eleToAdd = element.getSymbol() + "<sub>" + getElementCount(formula, element) + "</sub>";
                    htmlString += eleToAdd;
                } else {
                    for (IIsotope isotope : getIsotopes(formula, element)) {
                        String isoToAdd = "<sup>" + isotope.getMassNumber() + "</sup>"
                                + isotope.getSymbol() + "<sub>" + formula.getIsotopeCount(isotope) + "</sub>";
                        htmlString += isoToAdd;
                    }
                }
            }
        }
		if(chargeB){
			Double charge = formula.getCharge();
			if((charge == CDKConstants.UNSET) || (charge == 0)){
				return htmlString;
			} else if (charge < 0) {
				return htmlString + "<sup>" + charge * -1 + "-" + "</sup>";
			} else {
				return htmlString + "<sup>" + charge +"+" + "</sup>";
			}
		}
		return htmlString;
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
	public static IMolecularFormula getMolecularFormula(String stringMF, IChemObjectBuilder builder) {
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
	public static IMolecularFormula getMajorIsotopeMolecularFormula(String stringMF, IChemObjectBuilder builder) {
		return getMolecularFormula(stringMF, true, builder);
	}

	private static IMolecularFormula getMolecularFormula(String stringMF, boolean assumeMajorIsotope, IChemObjectBuilder builder) {
		IMolecularFormula formula = builder.newMolecularFormula();
		
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
		// FIXME: MF: variables with lower case first char
		char ThisChar;
		/*
		 *  Buffer for
		 */
		String RecentElementSymbol = new String();
		String RecentElementCountString = new String("0");
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
				
				IIsotope isotope = formula.getBuilder().newIsotope(RecentElementSymbol);
				if (assumeMajorIsotope) {
					try {
						isotope = IsotopeFactory.getInstance(isotope.getBuilder()).getMajorIsotope(RecentElementSymbol);
					} catch (IOException e) {
						throw new RuntimeException("Cannot load the IsotopeFactory");
					}
				}
				formula.addIsotope(isotope, RecentElementCount);
				
			}
		}
		return formula;
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
        double mass = 0.0;

        IChemObjectBuilder builder = formula.getBuilder();
        for (IIsotope isotope : formula.isotopes()) {
            if (isotope.getExactMass() == CDKConstants.UNSET) {
                try {
                    mass += IsotopeFactory.getInstance(builder).getMajorIsotope(isotope.getSymbol()).getExactMass() *
                            formula.getIsotopeCount(isotope);
                } catch (IOException e) {
                    throw new RuntimeException("Could not instantiate the IsotopeFactory.");
                }
            } else
                mass += isotope.getExactMass() * formula.getIsotopeCount(isotope);
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
			factory = IsotopeFactory.getInstance(formula.getBuilder());
		} catch (IOException e) {
			throw new RuntimeException("Could not instantiate the IsotopeFactory.");
		}
        for (IIsotope isotope : formula.isotopes()) {
            IElement isotopesElement = isotope.getBuilder().newElement(isotope);
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
	           factory = IsotopeFactory.getInstance(formula.getBuilder());
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
 		    if(types.length==0)
 		    	throw new CDKException("Calculation of double bond equivalents not possible due to problems with element "+ac.getAtom(f).getSymbol());
// 		   valencies[(int) (types[0].getBondOrderSum() + ac.getAtom(f).getFormalCharge())]++;
 		    valencies[types[0].getBondOrderSum().intValue()]++;
		}
		return  1 + (valencies[4]) + (valencies[3] /2) - (valencies[1] /2);
	}
	
	/**
	 * Method that actually does the work of convert the atomContainer
	 * to IMolecularFormula.
	 * <p> The hydrogens must be implicit.
	 *
	 * @param  atomContainer     IAtomContainer object
	 * @see                      #getMolecularFormula(IAtomContainer,IMolecularFormula)
     * @return a molecular formula object
	 */
	@TestMethod("testGetMolecularFormula_IAtomContainer")
	public static IMolecularFormula getMolecularFormula(IAtomContainer atomContainer) {
		
		IMolecularFormula formula = atomContainer.getBuilder().newMolecularFormula();
		
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

        for (IAtom iAtom : atomContainer.atoms()) {
            formula.addIsotope(iAtom);
        }
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
		
		IAtomContainer atomContainer = formula.getBuilder().newAtomContainer();
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
            for (int i = 0; i < occur; i++)
                atomContainer.addAtom(formula.getBuilder().newAtom(isotope));

        }
		return atomContainer;
	}
	
	
	
	
	/**
	 * Generate the order of the Elements according probability occurrence.,
	 * beginning the C, H, O, N, Si, P, S, F, Cl, Br, I, Sn, B, Pb, Tl, Ba, In, Pd,
	 * Pt, Os, Ag, Zr, Se, Zn, Cu, Ni, Co, Fe, Cr, Ti, Ca, K, Al, Mg, Na, Ce,
	 * Hg, Au, Ir, Re, W, Ta, Hf, Lu, Yb, Tm, Er, Ho, Dy, Tb, Gd, Eu, Sm, Pm,
	 * Nd, Pr, La, Cs, Xe, Te, Sb, Cd, Rh, Ru, Tc, Mo, Nb, Y, Sr, Rb, Kr, As, 
	 * Ge, Ga, Mn, V, Sc, Ar, Ne, Be, Li, Tl, Pb, Bi, Po, At, Rn, Fr, Ra, Ac, 
	 * Th, Pa, U, Np, Pu. 
	 * 
	 * @return  Array with the elements ordered
	 * 
	 */
	public static String[] generateOrderEle(){
        return new String[]{
                "C", "H", "O", "N", "Si", "P", "S", "F", "Cl",
                "Br", "I", "Sn", "B", "Pb", "Tl", "Ba", "In", "Pd",
                "Pt", "Os", "Ag", "Zr", "Se", "Zn", "Cu", "Ni", "Co",
                "Fe", "Cr", "Ti", "Ca", "K", "Al", "Mg", "Na", "Ce",
                "Hg", "Au", "Ir", "Re", "W", "Ta", "Hf", "Lu", "Yb",
                "Tm", "Er", "Ho", "Dy", "Tb", "Gd", "Eu", "Sm", "Pm",
                "Nd", "Pr", "La", "Cs", "Xe", "Te", "Sb", "Cd", "Rh",
                "Ru", "Tc", "Mo", "Nb", "Y", "Sr", "Rb", "Kr", "As",
                "Ge", "Ga", "Mn", "V", "Sc", "Ar", "Ne", "Be", "Li",
                "Tl", "Pb", "Bi", "Po", "At", "Rn", "Fr", "Ra", "Ac",
                "Th", "Pa", "U", "Np", "Pu"};
		
	}
	/**
	 * Generate the order of the Elements according Hill system 
	 * when contains carbons.
	 *
	 * @return  Array with the elements ordered
	 */
	private static String[] generateOrderEle_Hill_NoCarbons(){
        return new String[]{
                "Ac", "Ag", "Al", "Ar", "As", "At", "Au",
                "B", "Ba", "Be", "Bi", "Br", "Ca", "Cd", "Ce", "Cl", "Co", "Cr", "Cs", "Cu",
                "Dy", "Er", "Eu", "F", "Fe", "Fr",
                "Ga", "Gd", "Ge", "H", "Hf", "Hg", "Ho", "I", "In", "Ir",
                "K", "Kr", "La", "Li", "Lu", "Mg", "Mn", "Mo",
                "N", "Na", "Nb", "Nd", "Ne", "Ni", "Np", "O", "Os",
                "P", "Pa", "Pb", "Pd", "Pm", "Po", "Pr", "Pt", "Pu",
                "Ra", "Rb", "Re", "Rh", "Rn", "Ru",
                "S", "Sb", "Sc", "Se", "Si", "Sr", "Sm", "Sn",
                "Ta", "Tb", "Tc", "Te", "Th", "Ti", "Tl", "Tm",
                "U", "V", "W", "Xe", "Y", "Yb", "Zn", "Zr"};
	}
	/**
	 * Generate the order of the Elements according Hill system 
	 * when contains carbons.
	 *
	 * @return  Array with the elements ordered
	 */
	private static String[] generateOrderEle_Hill_WithCarbons(){
        return new String[]{
                "C", "H", "Ac", "Ag", "Al", "Ar", "As", "At", "Au",
                "B", "Ba", "Be", "Bi", "Br", "Ca", "Cd", "Ce", "Cl", "Co", "Cr", "Cs", "Cu",
                "Dy", "Er", "Eu", "F", "Fe", "Fr",
                "Ga", "Gd", "Ge", "Hf", "Hg", "Ho", "I", "In", "Ir",
                "K", "Kr", "La", "Li", "Lu", "Mg", "Mn", "Mo",
                "N", "Na", "Nb", "Nd", "Ne", "Ni", "Np", "O", "Os",
                "P", "Pa", "Pb", "Pd", "Pm", "Po", "Pr", "Pt", "Pu",
                "Ra", "Rb", "Re", "Rh", "Rn", "Ru",
                "S", "Sb", "Sc", "Se", "Si", "Sr", "Sm", "Sn",
                "Ta", "Tb", "Tc", "Te", "Th", "Ti", "Tl", "Tm",
                "U", "V", "W", "Xe", "Y", "Yb", "Zn", "Zr"};
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
	public static boolean compare(IMolecularFormula formula1, IMolecularFormula formula2){
		
		if(formula1.getCharge() != formula2.getCharge())
			return false;
		
		
		if(formula1.getIsotopeCount() != formula2.getIsotopeCount())
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
	 * Returns a set of nodes excluding all the hydrogens
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
	
}

