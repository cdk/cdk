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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

/**
 * Used to store and return data of a particular isotope.
 *
 * @cdk.module core
 * @cdk.githash
 *
 * @author         steinbeck
 * @cdk.created    2001-08-29
 */
public abstract class IsotopeFactory {

    protected Map<String, List<IIsotope>> isotopes      = null;
    protected Map<String, IIsotope>       majorIsotopes = null;
    protected static ILoggingTool         logger        = LoggingToolFactory.createLoggingTool(IsotopeFactory.class);

    /**
     *  Returns the number of isotopes defined by this class.
     *
     *@return    The size value
     */
    public int getSize() {
        return isotopes.size();
    }

    /**
     * Protected methods only to be used by classes extending this class to add
     * an IIsotope.
     */
    protected void add(IIsotope isotope) {
        List<IIsotope> isotopesForSymbol = isotopes.get(isotope.getSymbol());
        if (isotopesForSymbol == null) {
            isotopesForSymbol = new ArrayList<IIsotope>();
            isotopes.put(isotope.getSymbol(), isotopesForSymbol);
        }
        isotopesForSymbol.add(isotope);
    }

    /**
     * Gets an array of all isotopes known to the IsotopeFactory for the given
     * element symbol.
     *
     *@param  symbol  An element symbol to search for
     *@return         An array of isotopes that matches the given element symbol
     */
    public IIsotope[] getIsotopes(String symbol) {
        if (isotopes.get(symbol) == null) return new IIsotope[0];
        List<IIsotope> list = new ArrayList<IIsotope>();
        for (IIsotope isotope : isotopes.get(symbol)) {
            try {
                IIsotope clone = (IIsotope) isotope.clone();
                list.add(clone);
            } catch (CloneNotSupportedException e) {
                logger.error("Could not clone IIsotope: ", e.getMessage());
                logger.debug(e);
            }
        }
        return list.toArray(new IIsotope[list.size()]);
    }

    /**
     * Gets a array of all isotopes known to the IsotopeFactory.
     *
     * @return         An array of all isotopes
     */
    public IIsotope[] getIsotopes() {
        ArrayList<IIsotope> list = new ArrayList<IIsotope>();
        for (String element : isotopes.keySet()) {
            for (IIsotope isotope : isotopes.get(element)) {
                try {
                    IIsotope clone = (IIsotope) isotope.clone();
                    list.add(clone);
                } catch (CloneNotSupportedException e) {
                    logger.error("Could not clone IIsotope: ", e.getMessage());
                    logger.debug(e);
                }
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
        ArrayList<IIsotope> list = new ArrayList<IIsotope>();
        for (String element : isotopes.keySet()) {
            for (IIsotope isotope : isotopes.get(element)) {
                if (Math.abs(isotope.getExactMass() - exactMass) <= difference) {
                    try {
                        IIsotope clone = (IIsotope) isotope.clone();
                        list.add(clone);
                    } catch (CloneNotSupportedException e) {
                        logger.error("Could not clone IIsotope: ", e.getMessage());
                        logger.debug(e);
                    }
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
        IIsotope ret = null;
        for (IIsotope isotope : isotopes.get(symbol)) {
            if (isotope.getSymbol().equals(symbol) && isotope.getMassNumber() == massNumber) {
                try {
                    ret = (IIsotope) isotope.clone();
                } catch (CloneNotSupportedException e) {
                    logger.error("Could not clone IIsotope: ", e.getMessage());
                    logger.debug(e);
                }
                return ret;
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
        for (IIsotope isotope : isotopes.get(symbol)) {
            double diff = Math.abs(isotope.getExactMass() - exactMass);
            if (isotope.getSymbol().equals(symbol) && diff <= tolerance && diff < minDiff) {
                try {
                    ret = (IIsotope) isotope.clone();
                    minDiff = diff;
                } catch (CloneNotSupportedException e) {
                    logger.error("Could not clone IIsotope: ", e.getMessage());
                    logger.debug(e);
                }
            }
        }
        return ret;
    }

    /**
     * Returns the most abundant (major) isotope with a given atomic number.
     *
     * <p>The isotope's abundance is for atoms with atomic number 60 and smaller
     * defined as a number that is proportional to the 100 of the most abundant
     * isotope. For atoms with higher atomic numbers, the abundance is defined
     * as a percentage.
     *
     * @param  atomicNumber  The atomicNumber for which an isotope is to be returned
     * @return               The isotope corresponding to the given atomic number
     *
     * @see #getMajorIsotope(String symbol)
     */
    public IIsotope getMajorIsotope(int atomicNumber) {
        IIsotope major = null;
        for (IIsotope isotope : isotopes.get(PeriodicTable.getSymbol(atomicNumber))) {
            try {
                if (major == null) {
                    major = (IIsotope) isotope.clone();
                } else if (isotope.getNaturalAbundance() > major.getNaturalAbundance()) {
                    major = (IIsotope) isotope.clone();
                }
            } catch (CloneNotSupportedException e) {
                logger.error("Could not clone IIsotope: ", e.getMessage());
                logger.debug(e);
            }
        }
        if (major == null) logger.error("Could not find major isotope for: ", atomicNumber);
        return major;
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
        IIsotope major = null;
        if (majorIsotopes.containsKey(symbol)) {
            major = majorIsotopes.get(symbol);
        } else {
            if (isotopes.get(symbol) == null) {
                logger.error("Could not find major isotope for: ", symbol);
                return null;
            }
            for (IIsotope isotope : isotopes.get(symbol)) {
                if (isotope.getSymbol().equals(symbol)) {
                    try {
                        if (major == null) {
                            major = (IIsotope) isotope.clone();
                        } else {
                            if (isotope.getNaturalAbundance() > major.getNaturalAbundance()) {
                                major = (IIsotope) isotope.clone();
                            }
                        }
                    } catch (CloneNotSupportedException e) {
                        logger.error("Could not clone IIsotope: ", e.getMessage());
                        logger.debug(e);
                    }
                }
            }
            if (major == null) {
                logger.error("Could not find major isotope for: ", symbol);
            } else {
                majorIsotopes.put(symbol, major);
            }
        }
        return major;
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
            isotope = getMajorIsotope(atom.getSymbol());
        else
            isotope = getIsotope(atom.getSymbol(), atom.getMassNumber());

        if (isotope == null) throw new IllegalArgumentException("Cannot configure an unrecognized element: " + atom);
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
     *  Gets the natural mass of this element, defined as average of masses of isotopes,
     *  weighted by abundance.
     *
     * @param  element                     the element in question
     * @return                             The natural mass value
     */
    public double getNaturalMass(IElement element) {
        IIsotope[] isotopes = getIsotopes(element.getSymbol());
        double summedAbundances = 0;
        double summedWeightedAbundances = 0;
        double getNaturalMass = 0;
        for (int i = 0; i < isotopes.length; i++) {
            summedAbundances += isotopes[i].getNaturalAbundance();
            summedWeightedAbundances += isotopes[i].getNaturalAbundance() * isotopes[i].getExactMass();
            getNaturalMass = summedWeightedAbundances / summedAbundances;
        }
        return getNaturalMass;
    }

}
