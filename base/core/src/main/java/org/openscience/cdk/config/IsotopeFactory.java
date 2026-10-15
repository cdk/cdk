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
 * An interface for working with an isotope catalog. The classes
 * {@link Isotopes} extends this class and is to be used to get isotope
 * information.
 * <br/>
 * At a minimum the {@code #isotopes()} method should be implemented, and ideally
 * provide either immutable or a copy of the internal data. For performance
 * reasons you should also implement the {@code #isotopes(int)},
 * {@code #getMajorIsotope} and {@code #getNaturalMass} - note the defaults
 * are provided but perform a linear scan the entire catalog.
 *
 * @author Christoph steinbeck
 * @author John Mayfield
 */
public interface IsotopeFactory {

    ILoggingTool logger = LoggingToolFactory.createLoggingTool(IsotopeFactory.class);

    /**
     * Access all isotopes available in this factory/catalog.
     *
     * @return all available isotopes
     */
    List<IIsotope> isotopes();

    default List<IIsotope> isotopes(int atomicNumber) {
        List<IIsotope> isotopes = new ArrayList<>();
        for (IIsotope isotope : getIsotopes()) {
            if (isotope.getAtomicNumber() == atomicNumber)
                isotopes.add(isotope);
        }
        return isotopes;
    }

    /**
     * Returns the most abundant (major) isotope with a given atomic number.
     * Note that some high mass elements do not have a major isotopes
     * (0% abundance) and this method will return null for those.
     *
     * @param elem The atomicNumber for which an isotope is to be returned
     * @return The isotope corresponding to the given atomic number
     * @see #getMajorIsotope(String symbol)
     */
    default IIsotope getMajorIsotope(int elem) {
        IIsotope major = null;
        for (IIsotope isotope : isotopes(elem)) {
            if (isotope.getNaturalAbundance() == null ||
                isotope.getNaturalAbundance() <= 0)
                continue;
            if (major == null ||
                isotope.getNaturalAbundance() > major.getNaturalAbundance()) {
                major = isotope;
            }
        }
        if (major != null) {
            logger.warn("Could not find major isotope for: ", elem);
        }
        return major;
    }

    /**
     * Gets the natural mass of this element, defined as average of masses of
     * isotopes, weighted by abundance.
     *
     * @param atomicNum the element in question
     * @return The natural mass value
     */
    default double getNaturalMass(int atomicNum) {
        double summedAbundances = 0;
        double summedWeightedAbundances = 0;
        double getNaturalMass = 0;
        for (IIsotope isotope : getIsotopes(atomicNum)) {
            summedAbundances += isotope.getNaturalAbundance();
            summedWeightedAbundances += isotope.getNaturalAbundance() * isotope.getExactMass();
            getNaturalMass = summedWeightedAbundances / summedAbundances;
        }
        return getNaturalMass;
    }


    /**
     * Gets an array of all isotopes known to the IsotopeFactory.
     *
     * @return An array of all isotopes
     */
    default IIsotope[] getIsotopes() {
        return isotopes().toArray(new IIsotope[0]);
    }

    /**
     * Gets an array of all isotopes known to the IsotopeFactory for the given
     * element symbol.
     *
     * @param atomicNumber atomic number
     * @return An array of isotopes that matches the given element symbol
     */
    default IIsotope[] getIsotopes(int atomicNumber) {
        return isotopes(atomicNumber).toArray(new IIsotope[0]);
    }

    /**
     * Gets an array of all isotopes known to the IsotopeFactory for the given
     * element symbol.
     *
     * @param symbol An element symbol to search for
     * @return An array of isotopes that matches the given element symbol
     */
    default IIsotope[] getIsotopes(String symbol) {
        return getIsotopes(Elements.ofString(symbol).number());
    }

    /**
     * Gets an array of all isotopes matching the searched exact mass within
     * a certain difference.
     *
     * @param exactMass  search mass
     * @param difference mass the isotope is allowed to differ from the search mass
     * @return An array of all isotopes
     */
    default IIsotope[] getIsotopes(double exactMass, double difference) {
        List<IIsotope> list = new ArrayList<>();
        for (IIsotope isotope : getIsotopes()) {
            if (Math.abs(isotope.getExactMass() - exactMass) <= difference)
                list.add(isotope);
        }
        return list.toArray(new IIsotope[0]);
    }

    /**
     * Get isotope based on element symbol and mass number.
     *
     * @param symbol     the element symbol
     * @param massNumber the mass number
     * @return the corresponding isotope
     */
    default IIsotope getIsotope(String symbol, int massNumber) {
        for (IIsotope isotope : getIsotopes(symbol)) {
            if (isotope.getSymbol().equals(symbol) && isotope.getMassNumber() == massNumber) {
                return isotope;
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
    default IIsotope getIsotope(String symbol, double exactMass, double tolerance) {
        IIsotope ret = null;
        double minDiff = Double.MAX_VALUE;
        for (IIsotope isotope : getIsotopes(symbol)) {
            double diff = Math.abs(isotope.getExactMass() - exactMass);
            if (isotope.getSymbol().equals(symbol) && diff <= tolerance && diff < minDiff) {
                ret = isotope;
                minDiff = diff;
            }
        }
        return ret;
    }


    /**
     * Get the mass of the most abundant (major) isotope, if there is no
     * major isotopes 2*elem is returned.
     *
     * @param elem the atomic number
     * @return the major isotope mass
     */
    default double getMajorIsotopeMass(int elem) {
        IIsotope major = getMajorIsotope(elem);
        if (major == null) {
            logger.warn("No major isotope for elem" + elem);
            return 2.0 * elem;
        }
        return major.getExactMass();
    }

    /**
     * Get the exact mass of a specified isotope for an atom. If there is no isotope with that
     * mass number then it is returned.
     *
     * @param atomicNumber atomic number
     * @param massNumber   the mass number
     * @return the exact mass
     */
    default double getExactMass(Integer atomicNumber, Integer massNumber) {
        if (atomicNumber == null || massNumber == null)
            return 0;
        for (IIsotope isotope : getIsotopes(atomicNumber)) {
            if (isotope.getMassNumber().equals(massNumber))
                return isotope.getExactMass();
        }
        return massNumber;
    }

    /**
     * Returns the most abundant (major) isotope whose symbol equals element.
     *
     * @param symbol the symbol of the element in question
     * @return The Major Isotope value
     */
    default IIsotope getMajorIsotope(String symbol) {
        return getMajorIsotope(Elements.ofString(symbol).number());
    }

    /**
     * Configures an atom. Finds the correct element type
     * by looking at the atoms element symbol. If the element symbol is not recognised, it will
     * throw an {@link IllegalArgumentException}.
     *
     * @param atom The atom to be configured
     * @return The configured atom
     */
    default IAtom configure(IAtom atom) {
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
     * Configures an atom to have all the data of the
     * given isotope.
     *
     * @param atom    The atom to be configure
     * @param isotope The isotope to read the data from
     * @return The configured atom
     */
    default IAtom configure(IAtom atom, IIsotope isotope) {
        atom.setMassNumber(isotope.getMassNumber());
        atom.setSymbol(isotope.getSymbol());
        atom.setExactMass(isotope.getExactMass());
        atom.setAtomicNumber(isotope.getAtomicNumber());
        atom.setNaturalAbundance(isotope.getNaturalAbundance());
        return atom;
    }

    /**
     * Configures atoms in an AtomContainer to
     * carry all the correct data according to their element type.
     *
     * @param container The AtomContainer to be configured
     */
    default void configureAtoms(IAtomContainer container) {
        for (int f = 0; f < container.getAtomCount(); f++) {
            configure(container.getAtom(f));
        }
    }

    /**
     * Gets the natural mass of this element, defined as average of masses of
     * isotopes, weighted by abundance.
     *
     * @param element the element in question
     * @return The natural mass value
     */
    default double getNaturalMass(IElement element) {
        return getNaturalMass(element.getAtomicNumber());
    }

    /**
     * Checks whether the given element exists.
     *
     * @param elementName The element name to test
     * @return True is the element exists, false otherwise
     * @deprecated use {@link Elements}
     */
    @Deprecated
    default boolean isElement(String elementName) {
        return (getElement(elementName) != null);
    }

    /**
     * Returns an Element with a given element symbol.
     *
     * @param symbol The element symbol for the requested element
     * @return The configured element
     * @deprecated use {@link Elements}
     */
    @Deprecated
    default IElement getElement(String symbol) {
        return getMajorIsotope(symbol);
    }

    /**
     * Returns an element according to a given atomic number.
     *
     * @param atomicNumber The elements atomic number
     * @return The Element
     * @deprecated use {@link Elements}
     */
    @Deprecated
    default IElement getElement(int atomicNumber) {
        return getMajorIsotope(atomicNumber);
    }

    /**
     * Returns the symbol matching the isotope with the given atomic number.
     *
     * @param atomicNumber The elements atomic number
     * @return The symbol of the Element
     * @deprecated use {@link Elements}
     */
    @Deprecated
    default String getElementSymbol(int atomicNumber) {
        IIsotope majorIsotope = getMajorIsotope(atomicNumber);
        if (majorIsotope != null)
            return majorIsotope.getSymbol();
        return null;
    }

    /**
     * Returns the number of isotopes defined by this class.
     *
     * @return The size value
     * @deprecated a list or array is returned and so this function is not useful
     */
    @Deprecated
    default int getSize() {
        return getIsotopes().length;
    }
}
