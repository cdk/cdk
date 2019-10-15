/*  Copyright (C) 2001-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *                     2013  Egon Willighagen <egonw@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
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
package org.openscience.cdk.config;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to store and return data of a particular isotope. The classes
 * {@link Isotopes} extends this class and is to be used to get isotope
 * information.
 *
 * @cdk.module core
 * @cdk.githash
 *
 * @author         steinbeck
 * @cdk.created    2001-08-29
 */
public abstract class IsotopeFactory {

    public static final IIsotope[] EMPTY_ISOTOPE_ARRAY = new IIsotope[0];
    @SuppressWarnings("unchecked")
    private List<IIsotope> isotopes[]      = new List[256];
    @SuppressWarnings("unchecked")
    private IIsotope        majorIsotope[] = new IIsotope[256];
    protected static ILoggingTool  logger        = LoggingToolFactory.createLoggingTool(IsotopeFactory.class);

    /**
     *  Returns the number of isotopes defined by this class.
     *
     *@return    The size value
     */
    public int getSize() {
        int count = 0;
        for (List<IIsotope> isotope : isotopes)
            if (isotope != null)
                count += isotope.size();
        return count;
    }

    /**
     * Protected methods only to be used by classes extending this class to add
     * an IIsotope.
     */
    protected void add(IIsotope isotope) {
        Integer atomicNum = isotope.getAtomicNumber();
        assert atomicNum != null;
        List<IIsotope> isotopesForElement = isotopes[atomicNum];
        if (isotopesForElement == null) {
            isotopesForElement = new ArrayList<>();
            isotopes[atomicNum] = isotopesForElement;
        }
        isotopesForElement.add(isotope);
    }

    /**
     * Gets an array of all isotopes known to the IsotopeFactory for the given
     * element symbol.
     *
     * @param elem atomic number
     * @return An array of isotopes that matches the given element symbol
     */
    public IIsotope[] getIsotopes(int elem) {
        if (isotopes[elem] == null)
            return EMPTY_ISOTOPE_ARRAY;
        List<IIsotope> list = new ArrayList<>();
        for (IIsotope isotope : isotopes[elem]) {
            list.add(clone(isotope));
        }
        return list.toArray(new IIsotope[0]);
    }

    /**
     * Gets an array of all isotopes known to the IsotopeFactory for the given
     * element symbol.
     *
     * @param symbol An element symbol to search for
     * @return An array of isotopes that matches the given element symbol
     */
    public IIsotope[] getIsotopes(String symbol) {
        return getIsotopes(Elements.ofString(symbol).number());
    }

    /**
     * Gets a array of all isotopes known to the IsotopeFactory.
     *
     * @return         An array of all isotopes
     */
    public IIsotope[] getIsotopes() {
        List<IIsotope> list = new ArrayList<IIsotope>();
        for (List<IIsotope> isotopes : this.isotopes) {
            if (isotopes == null) continue;
            for (IIsotope isotope : isotopes) {
                list.add(clone(isotope));
            }
        }
        return list.toArray(new IIsotope[list.size()]);
    }

    /**
     * Gets an array of all isotopes matching the searched exact mass within
     * a certain difference.
     *
     * @param  exactMass  search mass
     * @param  difference mass the isotope is allowed to differ from the search mass
     * @return            An array of all isotopes
     */
    public IIsotope[] getIsotopes(double exactMass, double difference) {
        List<IIsotope> list = new ArrayList<>();
        for (List<IIsotope> isotopes : this.isotopes) {
            if (isotopes == null) continue;
            for (IIsotope isotope : isotopes) {
                if (Math.abs(isotope.getExactMass() - exactMass) <= difference) {
                    list.add(clone(isotope));
                }
            }
        }
        return list.toArray(new IIsotope[list.size()]);
    }

    /**
     * Get isotope based on element symbol and mass number.
     *
     * @param symbol the element symbol
     * @param massNumber the mass number
     * @return the corresponding isotope
     */
    public IIsotope getIsotope(String symbol, int massNumber) {
        int elem = Elements.ofString(symbol).number();
        List<IIsotope> isotopes = this.isotopes[elem];
        if (isotopes == null)
            return null;
        for (IIsotope isotope : isotopes) {
            if (isotope.getSymbol().equals(symbol) && isotope.getMassNumber() == massNumber) {
                return clone(isotope);
            }
        }
        return null;
    }

    /**
     * Get an isotope based on the element symbol and exact mass.
     *
     * @param symbol    the element symbol
     * @param exactMass the mass number
     * @param tolerance allowed difference from provided exact mass
     * @return the corresponding isotope
     */
    public IIsotope getIsotope(String symbol, double exactMass, double tolerance) {
        IIsotope ret = null;
        double minDiff = Double.MAX_VALUE;
        int elem = Elements.ofString(symbol).number();
        List<IIsotope> isotopes = this.isotopes[elem];
        if (isotopes == null)
            return null;
        for (IIsotope isotope : isotopes) {
            double diff = Math.abs(isotope.getExactMass() - exactMass);
            if (isotope.getSymbol().equals(symbol) && diff <= tolerance && diff < minDiff) {
                ret = clone(isotope);
                minDiff = diff;
            }
        }
        return ret;
    }

    /**
     * Returns the most abundant (major) isotope with a given atomic number.
     * Note that some high mass elements do not have a major isotopes
     * (0% abundance) and this method will return null for those.
     *
     * @param  elem  The atomicNumber for which an isotope is to be returned
     * @return       The isotope corresponding to the given atomic number
     *
     * @see #getMajorIsotope(String symbol)
     */
    public IIsotope getMajorIsotope(int elem) {
        IIsotope major = null;
        if (this.majorIsotope[elem] != null) {
            return clone(this.majorIsotope[elem]);
        }
        List<IIsotope> isotopes = this.isotopes[elem];
        if (isotopes != null) {
            for (IIsotope isotope : isotopes) {
                if (isotope.getNaturalAbundance() <= 0)
                    continue;
                if (major == null ||
                    isotope.getNaturalAbundance() > major.getNaturalAbundance()) {
                    major = isotope;
                }
            }
            if (major != null)
                this.majorIsotope[elem] = major;
            else
                logger.error("Could not find major isotope for: ", elem);
        }
        return clone(major);
    }

    /**
     * Get the mass of the most abundant (major) isotope, if there is no
     * major isotopes 0 is returned.
     *
     * @param elem the atomic number
     * @return the major isotope mass
     */
    public double getMajorIsotopeMass(int elem) {
        if (this.majorIsotope[elem] != null)
            return this.majorIsotope[elem].getExactMass();
        IIsotope major = getMajorIsotope(elem);
        return major != null ? major.getExactMass() : 0;
    }

    /**
     * Get the exact mass of a specified isotope for an atom.
     * @param atomicNumber atomic number
     * @param massNumber the mass number
     * @return the exact mass
     */
    public double getExactMass(Integer atomicNumber, Integer massNumber) {
        if (atomicNumber == null || massNumber == null)
            return 0;
        for (IIsotope isotope : this.isotopes[atomicNumber]) {
            if (isotope.getMassNumber().equals(massNumber))
                return isotope.getExactMass();
        }
        return 0;
    }

    private IIsotope clone(IIsotope isotope) {
        if (isotope == null)
            return null;
        try {
            return (IIsotope) isotope.clone();
        } catch (CloneNotSupportedException ex) {
            throw new UnsupportedOperationException("Clone not supported");
        }
    }

    /**
     * Checks whether the given element exists.
     *
     * @param  elementName   The element name to test
     * @return               True is the element exists, false otherwise
     */
    public boolean isElement(String elementName) {
        return (getElement(elementName) != null);
    }

    /**
     *  Returns the most abundant (major) isotope whose symbol equals element.
     *
     *@param  symbol  the symbol of the element in question
     *@return         The Major Isotope value
     */
    public IIsotope getMajorIsotope(String symbol) {
        return getMajorIsotope(Elements.ofString(symbol).number());
    }

    /**
     *  Returns an Element with a given element symbol.
     *
     *@param  symbol  The element symbol for the requested element
     *@return         The configured element
     */
    public IElement getElement(String symbol) {
        return getMajorIsotope(symbol);
    }

    /**
     *  Returns an element according to a given atomic number.
     *
     *@param  atomicNumber  The elements atomic number
     *@return               The Element
     */
    public IElement getElement(int atomicNumber) {
        return getMajorIsotope(atomicNumber);
    }

    /**
     * Returns the symbol matching the element with the given atomic number.
     *
     * @param  atomicNumber  The elements atomic number
     * @return               The symbol of the Element
     */
    public String getElementSymbol(int atomicNumber) {
        IIsotope isotope = getMajorIsotope(atomicNumber);
        return isotope.getSymbol();
    }

    /**
     * Configures an atom. Finds the correct element type
     * by looking at the atoms element symbol. If the element symbol is not recognised, it will
     * throw an {@link IllegalArgumentException}.
     *
     * @param  atom  The atom to be configured
     * @return       The configured atom
     */
    public IAtom configure(IAtom atom) {
        IIsotope isotope;
        if (atom.getMassNumber() == null)
            return atom;
        else
            isotope = getIsotope(atom.getSymbol(), atom.getMassNumber());
        if (isotope == null)
            throw new IllegalArgumentException("Cannot configure an unrecognized element/mass: " + atom.getMassNumber() + " " + atom);
        return configure(atom, isotope);
    }

    /**
     *  Configures an atom to have all the data of the
     *  given isotope.
     *
     *@param  atom     The atom to be configure
     *@param  isotope  The isotope to read the data from
     *@return          The configured atom
     */
    public IAtom configure(IAtom atom, IIsotope isotope) {
        atom.setMassNumber(isotope.getMassNumber());
        atom.setSymbol(isotope.getSymbol());
        atom.setExactMass(isotope.getExactMass());
        atom.setAtomicNumber(isotope.getAtomicNumber());
        atom.setNaturalAbundance(isotope.getNaturalAbundance());
        return atom;
    }

    /**
     *  Configures atoms in an AtomContainer to
     *  carry all the correct data according to their element type.
     *
     *@param  container  The AtomContainer to be configured
     */
    public void configureAtoms(IAtomContainer container) {
        for (int f = 0; f < container.getAtomCount(); f++) {
            configure(container.getAtom(f));
        }
    }

    /**
     * Gets the natural mass of this element, defined as average of masses of
     * isotopes, weighted by abundance.
     *
     * @param atomicNum the element in question
     * @return The natural mass value
     */
    public double getNaturalMass(int atomicNum) {
        List<IIsotope> isotopes = this.isotopes[atomicNum];
        if (isotopes == null)
            return 0;
        double summedAbundances = 0;
        double summedWeightedAbundances = 0;
        double getNaturalMass = 0;
        for (IIsotope isotope : isotopes) {
            summedAbundances += isotope.getNaturalAbundance();
            summedWeightedAbundances += isotope.getNaturalAbundance() * isotope.getExactMass();
            getNaturalMass = summedWeightedAbundances / summedAbundances;
        }
        return getNaturalMass;
    }

    /**
     * Gets the natural mass of this element, defined as average of masses of
     * isotopes, weighted by abundance.
     *
     * @param element the element in question
     * @return The natural mass value
     */
    public double getNaturalMass(IElement element) {
        return getNaturalMass(element.getAtomicNumber());
    }
}
