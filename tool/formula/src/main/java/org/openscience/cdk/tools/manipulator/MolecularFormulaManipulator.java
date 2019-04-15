/* Copyright (C) 2007  Miguel Rojasch <miguelrojasch@users.sf.net>
 *               2014  Mark B Vine (orcid:0000-0002-7794-0426)
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
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
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
public class MolecularFormulaManipulator {

    /**
     * For use with {@link #getMass(IMolecularFormula)}. This option uses the mass
     * stored on atoms ({@link IAtom#getExactMass()}) or the average mass of the
     * element when unspecified.
     */
    public static final int MolWeight                = AtomContainerManipulator.MolWeight;

    /**
     * For use with {@link #getMass(IMolecularFormula)}. This option ignores the
     * mass stored on atoms ({@link IAtom#getExactMass()}) and uses the average
     * mass of each element. This option is primarily provided for backwards
     * compatibility.
     */
    public static final int MolWeightIgnoreSpecified = AtomContainerManipulator.MolWeightIgnoreSpecified;

    /**
     * For use with {@link #getMass(IMolecularFormula)}. This option uses the mass
     * stored on atoms {@link IAtom#getExactMass()} or the mass of the major
     * isotope when this is not specified.
     */
    public static final int MonoIsotopic             = AtomContainerManipulator.MonoIsotopic;

    /**
     * For use with {@link #getMass(IMolecularFormula)}. This option uses the mass
     * stored on atoms {@link IAtom#getExactMass()} and then calculates a
     * distribution for any unspecified atoms and uses the most abundant
     * distribution. For example C<sub>6</sub>Br<sub>6</sub> would have three
     * <sup>79</sup>Br and <sup>81</sup>Br because their abundance is 51 and
     * 49%.
     */
    public static final int MostAbundant             = AtomContainerManipulator.MostAbundant;

    public static final Comparator<IIsotope> NAT_ABUN_COMP = new Comparator<IIsotope>() {
        @Override
        public int compare(IIsotope o1, IIsotope o2) {
            return -Double.compare(o1.getNaturalAbundance(),
                                   o2.getNaturalAbundance());
        }
    };

    /**
     *  Checks a set of Nodes for the occurrence of each isotopes
     *  instance in the molecular formula. In short number of atoms.
     *
     * @param   formula  The MolecularFormula to check
     * @return           The occurrence total
     */
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
    public static int getElementCount(IMolecularFormula formula, IElement element) {

        int count = 0;
        for (IIsotope isotope : formula.isotopes()) {
            if (isotope.getSymbol().equals(element.getSymbol())) count += formula.getIsotopeCount(isotope);
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
    public static List<IIsotope> getIsotopes(IMolecularFormula formula, IElement element) {

        List<IIsotope> isotopeList = new ArrayList<IIsotope>();
        for (IIsotope isotope : formula.isotopes()) {
            if (isotope.getSymbol().equals(element.getSymbol())) isotopeList.add(isotope);

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
    public static IMolecularFormula removeElement(IMolecularFormula formula, IElement element) {
        for (IIsotope isotope : getIsotopes(formula, element)) {
            formula.removeIsotope(isotope);
        }
        return formula;
    }

    /**
     * Returns the string representation of the molecular formula.
     *
     * @param formula       The IMolecularFormula Object
     * @param orderElements The order of Elements
     * @param setOne        True, when must be set the value 1 for elements with
     *                      one atom
     * @return A String containing the molecular formula
     * @see #getHTML(IMolecularFormula)
     * @see #generateOrderEle()
     * @see #generateOrderEle_Hill_NoCarbons()
     * @see #generateOrderEle_Hill_WithCarbons()
     */
    public static String getString(IMolecularFormula formula, String[] orderElements,
                                   boolean setOne) {
        return getString(formula, orderElements, setOne, true);
    }

    private static void appendElement(StringBuilder sb, Integer mass, int elem, int count) {
        if (mass != null)
            sb.append('[')
              .append(mass)
              .append(']')
              .append(Elements.ofNumber(elem).symbol());
        else
            sb.append(Elements.ofNumber(elem).symbol());
        if (count != 0)
            sb.append(count);
    }

    /**
     * Returns the string representation of the molecular formula.
     *
     * @param formula       The IMolecularFormula Object
     * @param orderElements The order of Elements
     * @param setOne        True, when must be set the value 1 for elements with
     *                      one atom
     * @param setMassNumber If the formula contains an isotope of an element that is the
     *                      non-major isotope, the element is represented as <code>[XE]</code> where
     *                      <code>X</code> is the mass number and <code>E</code> is the element symbol
     * @return A String containing the molecular formula
     * @see #getHTML(IMolecularFormula)
     * @see #generateOrderEle()
     * @see #generateOrderEle_Hill_NoCarbons()
     * @see #generateOrderEle_Hill_WithCarbons()
     */
    public static String getString(IMolecularFormula formula, String[] orderElements,
                                   boolean setOne, boolean setMassNumber) {
        StringBuilder  stringMF     = new StringBuilder();
        List<IIsotope> isotopesList = putInOrder(orderElements, formula);
        Integer q = formula.getCharge();

        if (q != null && q != 0)
            stringMF.append('[');

        if (!setMassNumber) {
            int count = 0;
            int prev  = -1;
            for (IIsotope isotope : isotopesList) {
                if (!Objects.equals(isotope.getAtomicNumber(), prev)) {
                    if (count != 0)
                        appendElement(stringMF,
                                      null, prev,
                                      setOne || count != 1 ? count : 0);
                    prev   = isotope.getAtomicNumber();
                    count  = formula.getIsotopeCount(isotope);
                } else
                    count += formula.getIsotopeCount(isotope);
            }
            if (count != 0)
                appendElement(stringMF,
                              null, prev,
                              setOne || count != 1 ? count : 0);
        } else {
            for (IIsotope isotope : isotopesList) {
                int count = formula.getIsotopeCount(isotope);
                appendElement(stringMF,
                              isotope.getMassNumber(), isotope.getAtomicNumber(),
                              setOne || count != 1 ? count : 0);
            }
        }


        if (q != null && q != 0) {
            stringMF.append(']');
            if (q > 0) {
                if (q > 1)
                    stringMF.append(q);
                stringMF.append('+');
            } else {
                if (q < -1)
                    stringMF.append(-q);
                stringMF.append('-');
            }
        }

        return stringMF.toString();
    }

    /**
     * Returns the string representation of the molecular formula.
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
    public static String getString(IMolecularFormula formula) {

        return getString(formula, false);
    }

    /**
     * Returns the string representation of the molecular formula.
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
    public static String getString(IMolecularFormula formula, boolean setOne) {

        if (containsElement(formula, formula.getBuilder().newInstance(IElement.class, "C")))
            return getString(formula, generateOrderEle_Hill_WithCarbons(), setOne, false);
        else
            return getString(formula, generateOrderEle_Hill_NoCarbons(), setOne, false);
    }


    /**
     * Returns the string representation of the molecular formula.
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
     * @param setMassNumber If the formula contains an isotope of an element that is the
     *                      non-major isotope, the element is represented as <code>[XE]</code> where
     *                      <code>X</code> is the mass number and <code>E</code> is the element symbol
     * @return          A String containing the molecular formula
     *
     * @see #getHTML(IMolecularFormula)
     */
    public static String getString(IMolecularFormula formula, boolean setOne, boolean setMassNumber) {

        if (containsElement(formula, formula.getBuilder().newInstance(IElement.class, "C")))
            return getString(formula, generateOrderEle_Hill_WithCarbons(), setOne, setMassNumber);
        else
            return getString(formula, generateOrderEle_Hill_NoCarbons(), setOne, setMassNumber);
    }

    public static List<IIsotope> putInOrder(String[] orderElements, IMolecularFormula formula) {
        List<IIsotope> isotopesList = new ArrayList<IIsotope>();
        for (String orderElement : orderElements) {
            IElement element = formula.getBuilder().newInstance(IElement.class, orderElement);
            if (containsElement(formula, element)) {
                List<IIsotope> isotopes = getIsotopes(formula, element);
                Collections.sort(isotopes,
                                 new Comparator<IIsotope>() {
                                     @Override
                                     public int compare(IIsotope a,
                                                        IIsotope b) {
                                         Integer aMass = a.getMassNumber();
                                         Integer bMass = b.getMassNumber();
                                         if (aMass == null)
                                             return -1;
                                         if (bMass == null)
                                             return +1;
                                         return aMass.compareTo(bMass);
                                     }
                                 });
                isotopesList.addAll(isotopes);
            }
        }
        return isotopesList;
    }

    /**
     * @deprecated  Use {@link #getString(org.openscience.cdk.interfaces.IMolecularFormula)}
     */
    @Deprecated
    public static String getHillString(IMolecularFormula formula) {
        StringBuffer hillString = new StringBuffer();

        Map<String, Integer> hillMap = new TreeMap<String, Integer>();
        for (IIsotope isotope : formula.isotopes()) {
            String symbol = isotope.getSymbol();
            if (hillMap.containsKey(symbol))
                hillMap.put(symbol, hillMap.get(symbol) + formula.getIsotopeCount(isotope));
            else
                hillMap.put(symbol, formula.getIsotopeCount(isotope));
        }

        // if we have a C append it and also add in the H
        // and then remove these elements
        int count;
        if (hillMap.containsKey("C")) {
            hillString.append('C');
            count = hillMap.get("C");
            if (count > 1) hillString.append(count);
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
     * Returns the string representation of the molecular formula based on Hill
     * System with numbers wrapped in &lt;sub&gt;&lt;/sub&gt; tags. Useful for
     * displaying formulae in Swing components or on the web.
     *
     *
     * @param   formula  The IMolecularFormula object
     * @return           A HTML representation of the molecular formula
     * @see              #getHTML(IMolecularFormula, boolean, boolean)
     *
     */
    public static String getHTML(IMolecularFormula formula) {
        return getHTML(formula, true, true);
    }

    /**
     * Returns the string representation of the molecular formula based on Hill
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
    public static String getHTML(IMolecularFormula formula, boolean chargeB, boolean isotopeB) {
        String[] orderElements;
        if (containsElement(formula, formula.getBuilder().newInstance(IElement.class, "C")))
            orderElements = generateOrderEle_Hill_WithCarbons();
        else
            orderElements = generateOrderEle_Hill_NoCarbons();
        return getHTML(formula, orderElements, chargeB, isotopeB);
    }

    /**
     * Returns the string representation of the molecular formula with numbers
     * wrapped in &lt;sub&gt;&lt;/sub&gt; tags and the isotope of each Element
     * in &lt;sup&gt;&lt;/sup&gt; tags and the total showCharge of IMolecularFormula
     * in &lt;sup&gt;&lt;/sup&gt; tags. Useful for displaying formulae in Swing
     * components or on the web.
     *
     *
     * @param   formula  The IMolecularFormula object
     * @param   orderElements The order of Elements
     * @param   showCharge  True, If it has to show the showCharge
     * @param   showIsotopes True, If it has to show the Isotope mass
     * @return           A HTML representation of the molecular formula
     * @see              #getHTML(IMolecularFormula)
     *
     */
    public static String getHTML(IMolecularFormula formula, String[] orderElements, boolean showCharge, boolean showIsotopes) {
        StringBuilder sb = new StringBuilder();
        for (String orderElement : orderElements) {
            IElement element = formula.getBuilder().newInstance(IElement.class, orderElement);
            if (containsElement(formula, element)) {
                if (!showIsotopes) {
                    sb.append(element.getSymbol());
                    int n = getElementCount(formula, element);
                    if (n > 1) {
                        sb.append("<sub>").append(n).append("</sub>");
                    }
                } else {
                    for (IIsotope isotope : getIsotopes(formula, element)) {
                        Integer massNumber = isotope.getMassNumber();
                        if (massNumber != null)
                            sb.append("<sup>").append(massNumber).append("</sup>");
                        sb.append(isotope.getSymbol());
                        int n = formula.getIsotopeCount(isotope);
                        if (n > 1) {
                            sb.append("<sub>").append(n).append("</sub>");
                        }
                    }
                }
            }
        }

        if (showCharge) {
            Integer charge = formula.getCharge();
            if (charge == CDKConstants.UNSET || charge == 0) {
                return sb.toString();
            } else {
                sb.append("<sup>");
                if (charge > 1 || charge < -1)
                    sb.append(Math.abs(charge));
                if (charge > 0)
                    sb.append('+');
                else
                    sb.append(MINUS); // note, not a hyphen!
                sb.append("</sup>");
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
    public static IMolecularFormula getMajorIsotopeMolecularFormula(String stringMF, IChemObjectBuilder builder) {
        return getMolecularFormula(stringMF, true, builder);
    }

    private static IMolecularFormula getMolecularFormula(String stringMF, boolean assumeMajorIsotope,
            IChemObjectBuilder builder) {
        IMolecularFormula formula = builder.newInstance(IMolecularFormula.class);

        return getMolecularFormula(stringMF, formula, assumeMajorIsotope);
    }

    private static final char HYPHEN = '-';
    private static final char MINUS  = '–';
    private static final String HYPHEN_STR = "-";
    private static final String MINUS_STR  = "–";

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
    public static IMolecularFormula getMolecularFormula(String stringMF, IMolecularFormula formula) {
        return getMolecularFormula(stringMF, formula, false);
    }

    private static boolean isUpper(char c) {
        return c >= 'A' && c <= 'Z';
    }

    private static boolean isLower(char c) {
        return c >= 'a' && c <= 'z';
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    // helper class for parsing MFs
    private static final class CharIter {
        int pos;
        String str;

        char next() {
            return pos == str.length() ? '\0' : str.charAt(pos++);
        }

        int nextUInt() {
            char c = next();
            if (!isDigit(c)) {
                if (c != '\0')
                    pos--;
                return -1;
            }
            int res = c - '0';
            while (isDigit(c = next()))
                res = (10 * res) + (c - '0');
            if (c != '\0')
                pos--;
            return res;
        }

        boolean nextIf(char c) {
            if (str.charAt(pos) == c) {
                pos++;
                return true;
            }
            return false;
        }
    }

    // parses an isotope from a symbol in the form:
     // ('[' <DIGIT> ']')? <UPPER> <LOWER>? <DIGIT>+?
    private static boolean parseIsotope(CharIter iter,
                                        IMolecularFormula mf,
                                        boolean setMajor) {
        Elements elem = null;
        int mass = 0;
        int count = 0;
        if (iter.nextIf('[')) {
            mass = iter.nextUInt();
            if (mass < 0)
                return false;
            if (!iter.nextIf(']'))
                return false;
        }
        char c1 = iter.next();
        char c2 = iter.next();
        if (!isLower(c2)) {
            // could use a switch, see SMARTS parser
            elem = Elements.ofString("" + c1);
            if (c2 != '\0')
                iter.pos--;
        } else {
            elem = Elements.ofString("" + c1 + c2);
        }
        count = iter.nextUInt();
        if (count < 0)
            count = 1;
        IIsotope isotope = mf.getBuilder().newInstance(IIsotope.class, elem.symbol());
        isotope.setAtomicNumber(elem.number());
        if (mass != 0)
            isotope.setMassNumber(mass);
        else if (setMajor) {
            try {
                IIsotope major = Isotopes.getInstance().getMajorIsotope(elem.number());
                if (major != null)
                    isotope.setMassNumber(major.getMassNumber());
            } catch (IOException ex) {
                // ignored
            }
        }
        mf.addIsotope(isotope, count);
        return true;
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
    private static IMolecularFormula getMolecularFormula(String stringMF, IMolecularFormula formula,
            boolean assumeMajorIsotope) {

        if (stringMF.contains(".") || stringMF.contains("(") || stringMF.length() > 0 && stringMF.charAt(0) >= '0' && stringMF.charAt(0) <= '9')
            stringMF = simplifyMolecularFormula(stringMF);

        // Extract charge from String when contains []X- format
        Integer charge = null;
        if ((stringMF.contains("[") && stringMF.contains("]")) && (stringMF.contains("+") || stringMF.contains(HYPHEN_STR) || stringMF.contains(MINUS_STR))) {
            charge = extractCharge(stringMF);
            stringMF = cleanMFfromCharge(stringMF);
        }
        if (stringMF.isEmpty())
            return null;
        int len = stringMF.length();
        CharIter iter = new CharIter();
        iter.str = stringMF;
        while (iter.pos < len) {
            if (!parseIsotope(iter, formula, assumeMajorIsotope)) {
                LoggingToolFactory.createLoggingTool(MolecularFormulaManipulator.class)
                    .error("Could not parse MF: " + iter.str);
                return null;
            }
        }

        if (charge != null) formula.setCharge(charge);
        return formula;
    }

    /**
     * Extract the molecular formula when it is defined with charge. e.g. [O3S]2-.
     *
     * @param formula  The formula to inspect
     * @return         The corrected formula
     */
    private static String cleanMFfromCharge(String formula) {
        if (!(formula.contains("[") && formula.contains("]"))) return formula;
        boolean startBreak = false;
        String finalFormula = "";
        for (int f = 0; f < formula.length(); f++) {
            char thisChar = formula.charAt(f);
            if (thisChar == '[') {
                // start
                startBreak = true;
            } else if (thisChar == ']') {
                break;
            } else if (startBreak) finalFormula += thisChar;
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

        if (!(formula.contains("[") && formula.contains("]") && (formula.contains("+") || formula.contains(HYPHEN_STR) || formula.contains(MINUS_STR))))
            return 0;

        boolean finishBreak = false;
        String multiple = "";
        for (int f = 0; f < formula.length(); f++) {
            char thisChar = formula.charAt(f);
            if (thisChar == ']') {
                // finish
                finishBreak = true;
            } else if (thisChar == HYPHEN || thisChar == MINUS) {
                multiple = HYPHEN + multiple;
                break;
            } else if (thisChar == '+') {
                break;
            } else if (finishBreak) {
                multiple += thisChar;
            }
        }
        if (multiple.isEmpty() || multiple.equals(HYPHEN_STR) || multiple.equals(MINUS_STR)) multiple += 1;
        return Integer.valueOf(multiple);
    }

    /**
     * @deprecated calls {@link #getMass(IMolecularFormula, int)} with option
     * {@link #MonoIsotopic} and adjusts for charge with
     * {@link #correctMass(double, Integer)}. These functions should be used
     * directly.
     */
    @Deprecated
    public static double getTotalExactMass(IMolecularFormula formula) {
        return correctMass(getMass(formula, MonoIsotopic), formula.getCharge());
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
        if (charge == null)
            return mass;
        double massE = 0.00054857990927;
        if (charge > 0)
            mass -= massE * charge;
        else if (charge < 0) mass += massE * Math.abs(charge);
        return mass;
    }

    /**
     * Get the summed mass number of all isotopes from an MolecularFormula. It
     * assumes isotope masses to be preset, and returns 0.0 if not.
     *
     * @param  formula The IMolecularFormula to calculate
     * @return         The summed nominal mass of all atoms in this MolecularFormula
     */
    public static double getTotalMassNumber(IMolecularFormula formula) {
        double mass = 0.0;
        for (IIsotope isotope : formula.isotopes()) {
            try {
                IIsotope isotope2 = Isotopes.getInstance().getMajorIsotope(isotope.getSymbol());
                if (isotope2 != null) {
                    mass += isotope2.getMassNumber() * formula.getIsotopeCount(isotope);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mass;
    }

    private static double getExactMass(IsotopeFactory isofact, IIsotope atom) {
        if (atom.getExactMass() != null)
            return atom.getExactMass();
        else if (atom.getMassNumber() != null)
            return isofact.getExactMass(atom.getAtomicNumber(),
                                        atom.getMassNumber());
        else
            return isofact.getMajorIsotopeMass(atom.getAtomicNumber());
    }

    private static double getMassOrAvg(IsotopeFactory isofact, IIsotope atom) {
        if (atom.getMassNumber() == null ||
            atom.getMassNumber() == 0)
            return isofact.getNaturalMass(atom);
        else
            return getExactMass(isofact, atom);
    }

    /**
     * Calculate the mass of a formula, this function takes an optional
     * 'mass flavour' that switches the computation type. The key distinction
     * is how specified/unspecified isotopes are handled. A specified isotope
     * is an atom that has either {@link IAtom#setMassNumber(Integer)}
     * or {@link IAtom#setExactMass(Double)} set to non-null and non-zero.
     * <br>
     * The flavours are:
     * <br>
     * <ul>
     *     <li>{@link #MolWeight} (default) - uses the exact mass of each
     *     atom when an isotope is specified, if not specified the average mass
     *     of the element is used.</li>
     *     <li>{@link #MolWeightIgnoreSpecified} - uses the average mass of each
     *     element, ignoring any isotopic/exact mass specification</li>
     *     <li>{@link #MonoIsotopic} - uses the exact mass of each
     *     atom when an isotope is specified, if not specified the major isotope
     *     mass for that element is used.</li>
     *     <li>{@link #MostAbundant} - uses the exact mass of each atom when
     *     specified, if not specified a distribution is calculated and the
     *     most abundant isotope pattern is used.</li>
     * </ul>
     *
     * @param mf molecular formula
     * @param flav flavor
     * @return the mass of the molecule
     * @see #getMass(IMolecularFormula, int)
     * @see #MolWeight
     * @see #MolWeightIgnoreSpecified
     * @see #MonoIsotopic
     * @see #MostAbundant
     */
    public static double getMass(IMolecularFormula mf, int flav) {
        final Isotopes isofact;
        try {
            isofact = Isotopes.getInstance();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load Isotopes!");
        }

        double mass = 0;
        switch (flav & 0xf) {
            case MolWeight:
                for (IIsotope iso : mf.isotopes()) {
                    mass += mf.getIsotopeCount(iso) *
                            getMassOrAvg(isofact, iso);
                }
                break;
            case MolWeightIgnoreSpecified:
                for (IIsotope iso : mf.isotopes()) {
                    mass += mf.getIsotopeCount(iso) *
                            isofact.getNaturalMass(iso.getAtomicNumber());
                }
                break;
            case MonoIsotopic:
                for (IIsotope iso : mf.isotopes()) {
                    mass += mf.getIsotopeCount(iso) *
                            getExactMass(isofact, iso);
                }
                break;
            case MostAbundant:
                IMolecularFormula mamf = getMostAbundant(mf);
                if (mamf != null)
                    mass = getMass(mamf, MonoIsotopic);
                break;
        }
        return mass;
    }

    /**
     * Calculate the mass of a formula, this function takes an optional
     * 'mass flavour' that switches the computation type. The key distinction
     * is how specified/unspecified isotopes are handled. A specified isotope
     * is an atom that has either {@link IAtom#setMassNumber(Integer)}
     * or {@link IAtom#setExactMass(Double)} set to non-null and non-zero.
     * <br>
     * The flavours are:
     * <br>
     * <ul>
     *     <li>{@link #MolWeight} (default) - uses the exact mass of each
     *     atom when an isotope is specified, if not specified the average mass
     *     of the element is used.</li>
     *     <li>{@link #MolWeightIgnoreSpecified} - uses the average mass of each
     *     element, ignoring any isotopic/exact mass specification</li>
     *     <li>{@link #MonoIsotopic} - uses the exact mass of each
     *     atom when an isotope is specified, if not specified the major isotope
     *     mass for that element is used.</li>
     *     <li>{@link #MostAbundant} - uses the exact mass of each atom when
     *     specified, if not specified a distribution is calculated and the
     *     most abundant isotope pattern is used.</li>
     * </ul>
     *
     * @param mf molecular formula
     * @return the mass of the molecule
     * @see #getMass(IMolecularFormula, int)
     * @see #MolWeight
     * @see #MolWeightIgnoreSpecified
     * @see #MonoIsotopic
     * @see #MostAbundant
     */
    public static double getMass(IMolecularFormula mf) {
        return getMass(mf, MolWeight);
    }

    /**
     * @deprecated use {@link #getMass(IMolecularFormula, int)} with option
     * {@link #MolWeightIgnoreSpecified}.
     */
    @Deprecated
    public static double getNaturalExactMass(IMolecularFormula formula) {
        return getMass(formula, MolWeightIgnoreSpecified);
    }

    /**
     * @deprecated use {@link #getMass(IMolecularFormula, int)} with option
     * {@link #MonoIsotopic}.
     */
    @Deprecated
    public static double getMajorIsotopeMass(IMolecularFormula formula) {
        return getMass(formula, MonoIsotopic);
    }

    /**
     * Get the summed natural abundance of all isotopes from an MolecularFormula. Assumes
     * abundances to be preset, and will return 0.0 if not.
     *
     * @param  formula The IMolecularFormula to calculate
     * @return         The summed natural abundance of all isotopes in this MolecularFormula
     */
    public static double getTotalNaturalAbundance(IMolecularFormula formula) {
        double abundance = 1.0;
        for (IIsotope isotope : formula.isotopes()) {
            if (isotope.getNaturalAbundance() == null) return 0.0;
            abundance = abundance * Math.pow(isotope.getNaturalAbundance(), formula.getIsotopeCount(isotope));
        }
        return abundance / Math.pow(100, getAtomCount(formula));
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
    public static double getDBE(IMolecularFormula formula) throws CDKException {
        int valencies[] = new int[5];
        IAtomContainer ac = getAtomContainer(formula);
        AtomTypeFactory factory = AtomTypeFactory.getInstance(
                "org/openscience/cdk/config/data/structgen_atomtypes.xml", ac.getBuilder());

        for (int f = 0; f < ac.getAtomCount(); f++) {
            IAtomType[] types = factory.getAtomTypes(ac.getAtom(f).getSymbol());
            if (types.length == 0)
                throw new CDKException(
                        "Calculation of double bond equivalents not possible due to problems with element "
                                + ac.getAtom(f).getSymbol());
            //			valencies[(int) (types[0].getBondOrderSum() + ac.getAtom(f).getFormalCharge())]++;
            valencies[types[0].getBondOrderSum().intValue()]++;
        }
        return 1 + (valencies[4]) + (valencies[3] / 2) - (valencies[1] / 2);
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
    public static IMolecularFormula getMolecularFormula(IAtomContainer atomContainer, IMolecularFormula formula) {
        int charge = 0;
        int hcnt   = 0;
        for (IAtom iAtom : atomContainer.atoms()) {
            formula.addIsotope(iAtom);
            if (iAtom.getFormalCharge() != null)
                charge += iAtom.getFormalCharge();
            if (iAtom.getImplicitHydrogenCount() != null)
                hcnt += iAtom.getImplicitHydrogenCount();
            }
        if (hcnt != 0) {
            IAtom hAtom = atomContainer.getBuilder().newInstance(IAtom.class, "H");
            formula.addIsotope(hAtom, hcnt);
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
    public static IAtomContainer getAtomContainer(IMolecularFormula formula, IAtomContainer atomContainer) {

        for (IIsotope isotope : formula.isotopes()) {
            int occur = formula.getIsotopeCount(isotope);
            for (int i = 0; i < occur; i++) {
                IAtom atom = formula.getBuilder().newInstance(IAtom.class, isotope);
                atom.setImplicitHydrogenCount(0);
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
    public static IAtomContainer getAtomContainer(String formulaString, IChemObjectBuilder builder) {
        return MolecularFormulaManipulator.getAtomContainer(MolecularFormulaManipulator.getMolecularFormula(
                formulaString, builder));
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
    public static String[] generateOrderEle() {
        return new String[]{
                // Elements of life
                "C", "H", "O", "N", "Si", "P", "S", "F", "Cl",

                "Br", "I", "Sn", "B", "Pb", "Tl", "Ba", "In", "Pd", "Pt", "Os", "Ag", "Zr", "Se", "Zn", "Cu", "Ni",
                "Co", "Fe", "Cr", "Ti", "Ca", "K", "Al", "Mg", "Na", "Ce", "Hg", "Au", "Ir", "Re", "W", "Ta", "Hf",
                "Lu", "Yb", "Tm", "Er", "Ho", "Dy", "Tb", "Gd", "Eu", "Sm", "Pm", "Nd", "Pr", "La", "Cs", "Xe", "Te",
                "Sb", "Cd", "Rh", "Ru", "Tc", "Mo", "Nb", "Y", "Sr", "Rb", "Kr", "As", "Ge", "Ga", "Mn", "V", "Sc",
                "Ar", "Ne", "He", "Be", "Li",

                // rest of periodic table, in atom-number order.
                "Bi", "Po", "At", "Rn",
                // row-7 elements (including f-block)
                "Fr", "Ra", "Ac", "Th", "Pa", "U", "Np", "Pu", "Am", "Cm", "Bk", "Cf", "Es", "Fm", "Md", "No", "Lr",
                "Rf", "Db", "Sg", "Bh", "Hs", "Mt", "Ds", "Rg", "Cn",

                // The "odd one out": an unspecified R-group
                "R"};

    }

    /**
     * Returns the Elements in Hill system order for non-carbon-containing formulas
     * (i.e. strict alphabetical order, with one-letter elements preceding two-letter elements.)
     * The generic R-group is treated specially and comes last.
     *
     * @return  Elements in Hill system order (strictly alphabetical), with generic R-groups last.
     */
    private static String[] generateOrderEle_Hill_NoCarbons() {
        return new String[]{"Ac", "Ag", "Al", "Am", "Ar", "As", "At", "Au", "B", "Ba", "Be", "Bh", "Bi", "Bk", "Br",
                "C", "Ca", "Cd", "Ce", "Cf", "Cl", "Cm", "Cn", "Co", "Cr", "Cs", "Cu", "Db", "Ds", "Dy", "Er", "Es",
                "Eu", "F", "Fe", "Fm", "Fr", "Ga", "Gd", "Ge", "H", "He", "Hf", "Hg", "Ho", "Hs", "I", "In", "Ir", "K",
                "Kr", "La", "Li", "Lr", "Lu", "Md", "Mg", "Mn", "Mo", "Mt", "N", "Na", "Nb", "Nd", "Ne", "Ni", "No",
                "Np", "O", "Os", "P", "Pa", "Pb", "Pd", "Pm", "Po", "Pr", "Pt", "Pu", "Ra", "Rb", "Re", "Rf", "Rg",
                "Rh", "Rn", "Ru", "S", "Sb", "Sc", "Se", "Sg", "Si", "Sm", "Sn", "Sr", "Ta", "Tb", "Tc", "Te", "Th",
                "Ti", "Tl", "Tm", "U", "V", "W", "Xe", "Y", "Yb", "Zn", "Zr",
                // The "odd one out": an unspecified R-group
                "R"};
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
    private static String[] generateOrderEle_Hill_WithCarbons() {
        return new String[]{"C", "H", "Ac", "Ag", "Al", "Am", "Ar", "As", "At", "Au", "B", "Ba", "Be", "Bh", "Bi",
                "Bk", "Br", "Ca", "Cd", "Ce", "Cf", "Cl", "Cm", "Cn", "Co", "Cr", "Cs", "Cu", "Db", "Ds", "Dy", "Er",
                "Es", "Eu", "F", "Fe", "Fm", "Fr", "Ga", "Gd", "Ge", "He", "Hf", "Hg", "Ho", "Hs", "I", "In", "Ir",
                "K", "Kr", "La", "Li", "Lr", "Lu", "Md", "Mg", "Mn", "Mo", "Mt", "N", "Na", "Nb", "Nd", "Ne", "Ni",
                "No", "Np", "O", "Os", "P", "Pa", "Pb", "Pd", "Pm", "Po", "Pr", "Pt", "Pu", "Ra", "Rb", "Re", "Rf",
                "Rg", "Rh", "Rn", "Ru", "S", "Sb", "Sc", "Se", "Sg", "Si", "Sm", "Sn", "Sr", "Ta", "Tb", "Tc", "Te",
                "Th", "Ti", "Tl", "Tm", "U", "V", "W", "Xe", "Y", "Yb", "Zn", "Zr",
                // The "odd one out": an unspecified R-group
                "R"};
    }

    /**
     * Compare two IMolecularFormula looking at type and number of IIsotope and
     * charge of the formula.
     *
     * @param formula1   The first IMolecularFormula
     * @param formula2   The second IMolecularFormula
     * @return           True, if the both IMolecularFormula are the same
     */
    public static boolean compare(IMolecularFormula formula1, IMolecularFormula formula2) {

        if (!Objects.equals(formula1.getCharge(), formula2.getCharge())) return false;

        if (formula1.getIsotopeCount() != formula2.getIsotopeCount()) return false;

        for (IIsotope isotope : formula1.isotopes()) {
            if (!formula2.contains(isotope)) return false;

            if (formula1.getIsotopeCount(isotope) != formula2.getIsotopeCount(isotope)) return false;

        }

        for (IIsotope isotope : formula2.isotopes()) {
            if (!formula1.contains(isotope)) return false;
            if (formula2.getIsotopeCount(isotope) != formula1.getIsotopeCount(isotope)) return false;
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
    public static String simplifyMolecularFormula(String formula) {
        String newFormula = formula;
        char thisChar;

        if (formula.contains(" ")) {
            newFormula = newFormula.replace(" ", "");
        }
        if (!formula.contains(".")) return breakExtractor(formula);

        List<String> listMF = new ArrayList<String>();
        while (newFormula.contains(".")) {
            int pos = newFormula.indexOf('.');
            String thisFormula = newFormula.substring(0, pos);
            if (thisFormula.charAt(0) >= '0' && thisFormula.charAt(0) <= '9')
                thisFormula = multipleExtractor(thisFormula);

            if (thisFormula.contains("(")) thisFormula = breakExtractor(thisFormula);

            listMF.add(thisFormula);
            thisFormula = newFormula.substring(pos + 1, newFormula.length());
            if (!thisFormula.contains(".")) {

                if (thisFormula.charAt(0) >= '0' && thisFormula.charAt(0) <= '9')
                    thisFormula = multipleExtractor(thisFormula);

                if (thisFormula.contains("(")) thisFormula = breakExtractor(thisFormula);

                listMF.add(thisFormula);
            }
            newFormula = thisFormula;
        }
        if (newFormula.contains("(")) newFormula = breakExtractor(newFormula);

        String recentElementSymbol = "";
        String recentElementCountString = "0";

        List<String> eleSymb = new ArrayList<String>();
        List<Integer> eleCount = new ArrayList<Integer>();
        for (int i = 0; i < listMF.size(); i++) {
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
                if (f == thisFormula.length() - 1
                        || (thisFormula.charAt(f + 1) >= 'A' && thisFormula.charAt(f + 1) <= 'Z')) {
                    int posit = eleSymb.indexOf(recentElementSymbol);
                    int count = Integer.valueOf(recentElementCountString);
                    if (posit == -1) {
                        eleSymb.add(recentElementSymbol);
                        eleCount.add(count);
                    } else {
                        int countP = Integer.valueOf(recentElementCountString);
                        if (countP == 0) countP = 1;
                        int countA = eleCount.get(posit);
                        if (countA == 0) countA = 1;
                        int value = countP + countA;
                        eleCount.remove(posit);
                        eleCount.add(posit, value);
                    }

                }
            }
        }
        String newF = "";
        for (int i = 0; i < eleCount.size(); i++) {
            String element = eleSymb.get(i);
            int num = eleCount.get(i);
            if (num == 0)
                newF += element;
            else
                newF += element + num;

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

        int innerMostBracket = formula.lastIndexOf("(");
        
        if (innerMostBracket<0)
        	return formula;
        
        String finalformula = formula.substring(0, innerMostBracket);
        String multipliedformula = "";
        String formulaEnd = "";
        String multiple = "";
        
        for (int f = innerMostBracket + 1; f < formula.length(); f++) {
            char thisChar = formula.charAt(f);
            
        	if ( finalBreak ) {
        		if ( isDigit(thisChar) ){
                    multiple += thisChar;
                } else {
                	formulaEnd = formula.substring(f, formula.length());
                	break;
                }
        	}else {
        		if ( thisChar == ')' ) {
                    finalBreak = true;
                }else
                    multipliedformula += thisChar;
        	}
        }
        finalformula += muliplier(multipliedformula, multiple.isEmpty() ? 1:Integer.valueOf(multiple)) + formulaEnd;
        
        if (finalformula.contains("("))
        	return breakExtractor(finalformula);
        else
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
                    finalformula += recentElementSymbol + factor;
                else
                    finalformula += recentElementSymbol + recentElementCount * factor;
            }
        }
        return finalformula;
    }

    /**
     * Adjust the protonation of a molecular formula. This utility method adjusts the hydrogen isotope count
     * and charge at the same time.
     *
     * <pre>
     * IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula("[C6H5O]-", bldr);
     * MolecularFormulaManipulator.adjustProtonation(mf, +1); // now "C6H6O"
     * MolecularFormulaManipulator.adjustProtonation(mf, -1); // now "C6H5O-"
     * </pre>
     *
     * The return value indicates whether the protonation could be adjusted:
     *
     * <pre>
     * IMolecularFormula mf = MolecularFormulaManipulator.getMolecularFormula("[Cl]-", bldr);
     * MolecularFormulaManipulator.adjustProtonation(mf, +0); // false still "[Cl]-"
     * MolecularFormulaManipulator.adjustProtonation(mf, +1); // true now "HCl"
     * MolecularFormulaManipulator.adjustProtonation(mf, -1); // true now "[Cl]-" (again)
     * MolecularFormulaManipulator.adjustProtonation(mf, -1); // false still "[Cl]-" (no H to remove!)
     * </pre>
     *
     * The method tries to select an existing hydrogen isotope to augment. If no hydrogen isotopes are found
     * a new major isotope (<sup>1</sup>H) is created.
     *
     * @param mf molecular formula
     * @param hcnt the number of hydrogens to add/remove, (&gt;0 protonate:, &lt;0: deprotonate)
     * @return the protonation was be adjusted
     */
    public static boolean adjustProtonation(IMolecularFormula mf, int hcnt) {
        if (mf == null) throw new NullPointerException("No formula provided");
        if (hcnt == 0) return false; // no protons to add

        final IChemObjectBuilder bldr = mf.getBuilder();
        final int                chg  = mf.getCharge() != null ? mf.getCharge() : 0;

        IIsotope proton = null;
        int pcount = 0;

        for (IIsotope iso : mf.isotopes()) {
            if ("H".equals(iso.getSymbol())) {
                final int count = mf.getIsotopeCount(iso);
                if (count < hcnt)
                    continue;
                // acceptable
                if (proton == null &&
                    (iso.getMassNumber() == null || iso.getMassNumber() == 1)) {
                    proton = iso;
                    pcount = count;
                }
                // better
                else if (proton != null &&
                           iso.getMassNumber() != null && iso.getMassNumber() == 1 &&
                           proton.getMassNumber() == null) {
                    proton = iso;
                    pcount = count;
                }
            }
        }


        if (proton == null && hcnt < 0) {
            return false;
        } else if (proton == null && hcnt > 0) {
            proton = bldr.newInstance(IIsotope.class, "H");
            proton.setMassNumber(1);
        }

        mf.removeIsotope(proton);
        if (pcount + hcnt > 0)
            mf.addIsotope(proton, pcount + hcnt);
        mf.setCharge(chg + hcnt);

        return true;
    }

    /**
     * Helper method for adding isotope distributions to a MF. The method adds
     * a distribution of isotopes by splitting the set of isotopes in two,
     * the one under consideration (specified by 'idx') and the remaining to be
     * considered ('&gt;idx'). The inflection point is calculate as 'k'
     * &le 'count' isotopes added. If there are remaining isotopes the method
     * calls it's self with 'idx+1' and 'count := k'.
     *
     * @param mf       the molecular formula to update
     * @param isotopes the isotopes, sorted most abundance to least
     * @param idx      which isotope we're currently considering
     * @param count    the number of isotopes remaining to select from
     * @return the distribution is unique (or not)
     */
    private static boolean addIsotopeDist(IMolecularFormula mf,
                                          IIsotope[] isotopes,
                                          int idx, int count) {
        if (count == 0)
            return true;
        double frac = 100d;
        for (int i = 0; i < idx; i++)
            frac -= isotopes[i].getNaturalAbundance();
        double p = isotopes[idx].getNaturalAbundance() / frac;

        if (p >= 1.0) {
            mf.addIsotope(isotopes[idx], count);
            return true;
        }

        double kMin = (count + 1) * (1 - p) - 1;
        double kMax = (count + 1) * (1 - p);
        if ((int) Math.ceil(kMin) == (int) Math.floor(kMax)) {
            int k = (int) kMax;
            mf.addIsotope(isotopes[idx], count - k);
            // recurse with remaining
            return addIsotopeDist(mf, isotopes, idx + 1, k);
        }
        return false; // multiple are most abundant
    }

    /**
     * Compute the most abundant MF. Given the MF C<sub>6</sub>Br<sub>6</sub>
     * this function rapidly computes the most abundant MF as
     * <sup>12</sup>C<sub>6</sub><sup>79</sup>Br<sub>3</sub><sup>81
     * </sup>Br<sub>3</sub>.
     *
     * @param mf a molecular formula with unspecified isotopes
     * @return the most abundant MF, or null if it could not be computed
     */
    public static IMolecularFormula getMostAbundant(IMolecularFormula mf) {
        final Isotopes isofact;
        try {
            isofact = Isotopes.getInstance();
        } catch (IOException e) {
            return null;
        }
        IMolecularFormula res = mf.getBuilder()
                                  .newInstance(IMolecularFormula.class);
        for (IIsotope iso : mf.isotopes()) {
            int count = mf.getIsotopeCount(iso);
            if (iso.getMassNumber() == null || iso.getMassNumber() == 0) {
                IIsotope[] isotopes = isofact.getIsotopes(iso.getSymbol());
                Arrays.sort(isotopes, NAT_ABUN_COMP);
                if (!addIsotopeDist(res, isotopes, 0, count))
                    return null;
            } else
                res.addIsotope(iso, count);
        }
        return res;
    }

    /**
     * Compute the most abundant MF. Given the a molecule
     * C<sub>6</sub>Br<sub>6</sub> this function rapidly computes the most
     * abundant MF as
     * <sup>12</sup>C<sub>6</sub><sup>79</sup>Br<sub>3</sub><sup>81
     * </sup>Br<sub>3</sub>.
     *
     * @param mol a molecule with unspecified isotopes
     * @return the most abundant MF, or null if it could not be computed
     */
    public static IMolecularFormula getMostAbundant(IAtomContainer mol) {
        return getMostAbundant(getMolecularFormula(mol));
    }
}
