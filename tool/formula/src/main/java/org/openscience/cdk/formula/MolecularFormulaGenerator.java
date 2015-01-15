/* Copyright (C) 2014  Tomas Pluskal <plusik@gmail.com>
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
 */
package org.openscience.cdk.formula;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * This class generates molecular formulas within given mass range and elemental
 * composition. There is no guaranteed order in which the formulas are
 * generated.
 * 
 * Usage:
 * 
 * <pre>
 * IsotopeFactory ifac = Isotopes.getInstance();
 * IIsotope c = ifac.getMajorIsotope(&quot;C&quot;);
 * IIsotope h = ifac.getMajorIsotope(&quot;H&quot;);
 * IIsotope n = ifac.getMajorIsotope(&quot;N&quot;);
 * IIsotope o = ifac.getMajorIsotope(&quot;O&quot;);
 * IIsotope p = ifac.getMajorIsotope(&quot;P&quot;);
 * IIsotope s = ifac.getMajorIsotope(&quot;S&quot;);
 * 
 * MolecularFormulaRange mfRange = new MolecularFormulaRange();
 * mfRange.addIsotope(c, 0, 50);
 * mfRange.addIsotope(h, 0, 100);
 * mfRange.addIsotope(o, 0, 50);
 * mfRange.addIsotope(n, 0, 50);
 * mfRange.addIsotope(p, 0, 10);
 * mfRange.addIsotope(s, 0, 10);
 * 
 * MolecularFormulaGenerator mfg = new MolecularFormulaGenerator(builder, minMass,
 *         maxMass, mfRange);
 * double minMass = 133.003;
 * double maxMass = 133.005;
 * IMolecularFormulaSet mfSet = mfg.getAllFormulas();
 * </pre>
 * 
 * The code was originally developed for a MZmine 2 framework module, published
 * in Pluskal et al. {@cdk.cite Pluskal2012}.
 * 
 * @cdk.module formula
 * @author Tomas Pluskal
 * @cdk.created 2014-12-28
 * @cdk.githash
 */
public class MolecularFormulaGenerator {

    private static final ILoggingTool logger = LoggingToolFactory
            .createLoggingTool(MolecularFormulaGenerator.class);

    private final IChemObjectBuilder builder;

    /**
     * Mass range to search by this instance of MolecularFormulaGenerator
     */
    private final double minMass, maxMass;

    /**
     * Internal arrays of isotopes (elements) used for the formula generation,
     * their minimal and maximal counts, and the current counts, which
     * correspond to the latest formula candidate.
     * 
     * For example, let's assume we set isotopes=[C,H,N], minCounts=[0,0,0], and
     * maxCounts=[3,3,3]. Then, currentCounts will be iterated in the following
     * order: [0,0,0], [1,0,0], [2,0,0], [3,0,0], [0,1,0], [1,1,0], [2,1,0],
     * [3,1,0], [0,2,0], [1,2,0], [2,2,0], etc.
     * 
     * The lastIncreasedPosition index indicates the last position in
     * currentCounts that was increased by calling increaseCounter(position)
     */
    private final IIsotope isotopes[];
    private final int minCounts[], maxCounts[], currentCounts[];
    private int lastIncreasedPosition = 0;

    /**
     * A flag indicating that the formula generation is running. If the search
     * is finished (the whole search space has been examined) or the search is
     * canceled by calling the cancel() method, this flag is set to false.
     * 
     * @see #cancel()
     */
    private boolean searchRunning = true;

    /**
     * Initiate the MolecularFormulaGenerator.
     * 
     * @param minMass
     *            Lower boundary of the target mass range
     * @param maxMass
     *            Upper boundary of the target mass range
     * @param mfRange
     *            A range of elemental compositions defining the search space
     * @throws IllegalArgumentException
     *             In case some of the isotopes in mfRange has undefined exact
     *             mass or in case illegal parameters are provided (e.g.,
     *             negative mass values or empty MolecularFormulaRange)
     * @see MolecularFormulaRange
     */
    public MolecularFormulaGenerator(final IChemObjectBuilder builder,
            final double minMass, final double maxMass,
            final MolecularFormulaRange mfRange) {

        logger.info("Initiate MolecularFormulaGenerator, mass range ", minMass,
                "-", maxMass);

        // Check parameter values
        if ((minMass < 0.0) || (maxMass < 0.0)) {
            throw (new IllegalArgumentException(
                    "The minimum and maximum mass values must be >=0"));
        }

        if ((minMass > maxMass)) {
            throw (new IllegalArgumentException(
                    "Minimum mass must be <= maximum mass"));
        }

        if ((mfRange == null) || (mfRange.getIsotopeCount() == 0)) {
            throw (new IllegalArgumentException(
                    "The MolecularFormulaRange parameter must be non-null and must contain at least one isotope"));
        }

        // Save the parameters
        this.builder = builder;
        this.minMass = minMass;
        this.maxMass = maxMass;

        // Sort the elements by mass in ascending order. That speeds up
        // the search.
        final TreeSet<IIsotope> isotopesSet = new TreeSet<IIsotope>(
                new IIsotopeSorterByMass());
        for (IIsotope isotope : mfRange.isotopes()) {
            // Check if exact mass of each isotope is set
            if (isotope.getExactMass() == null)
                throw new IllegalArgumentException(
                        "The exact mass value of isotope " + isotope
                                + " is not set");
            isotopesSet.add(isotope);
        }
        isotopes = isotopesSet.toArray(new IIsotope[isotopesSet.size()]);

        // Save the element counts from the provided MolecularFormulaRange
        minCounts = new int[isotopes.length];
        maxCounts = new int[isotopes.length];
        for (int i = 0; i < isotopes.length; i++) {
            minCounts[i] = mfRange.getIsotopeCountMin(isotopes[i]);
            maxCounts[i] = mfRange.getIsotopeCountMax(isotopes[i]);

            // Update the maximum count according to the mass limit
            int maxCountAccordingToMass = (int) Math.floor(maxMass
                    / isotopes[i].getExactMass());
            if (maxCounts[i] > maxCountAccordingToMass)
                maxCounts[i] = maxCountAccordingToMass;

        }

        // Set the current counters to minimal values, initially
        currentCounts = Arrays.copyOf(minCounts, minCounts.length);

    }

    /**
     * Returns next generated formula or null in case no new formula was found
     * (search is finished). There is no guaranteed order in which the formulas
     * are generated.
     */
    public synchronized IMolecularFormula getNextFormula() {

        // Main cycle iterating through element counters
        mainCycle: while (searchRunning) {

            double currentMass = calculateCurrentMass();

            // Heuristics: if we are over the mass, it is meaningless to add
            // more atoms, so let's jump directly to the maximum count at the
            // position we increased last time.
            if (currentMass > maxMass) {
                // Keep a lock on the currentCounts, because
                // getFinishedPercentage() might be called from another
                // thread
                synchronized (currentCounts) {
                    currentCounts[lastIncreasedPosition] = maxCounts[lastIncreasedPosition];
                    increaseCounter(lastIncreasedPosition);
                }
                continue mainCycle;
            }

            // If the current formula mass fits in the target mass range, let's
            // return it
            if ((currentMass >= minMass) && (currentMass <= maxMass)) {
                IMolecularFormula cdkFormula = generateFormulaObject();
                increaseCounter(0);
                return cdkFormula;
            }

            // Increase the counter
            increaseCounter(0);
        }

        // All combinations tested, return null
        return null;

    }

    /**
     * Generates a {@link IMolecularFormulaSet} by repeatedly calling {@link
     * getNextFormula()} until all possible formulas are generated. There is no
     * guaranteed order to the formulas in the resulting
     * {@link IMolecularFormulaSet}.
     * 
     * Note: if some formulas were already generated by calling {@link
     * getNextFormula()} on this MolecularFormulaGenerator instance, those
     * formulas will not be included in the returned
     * {@link IMolecularFormulaSet}.
     * 
     * @see #getNextFormula()
     */
    public synchronized IMolecularFormulaSet getAllFormulas() {

        final IMolecularFormulaSet result = builder
                .newInstance(IMolecularFormulaSet.class);
        IMolecularFormula nextFormula;
        while ((nextFormula = getNextFormula()) != null) {
            result.addMolecularFormula(nextFormula);
        }
        return result;
    }

    /**
     * Increases the internal counter array currentCounts[] at given position.
     * If the position has reached its maximum value, its value is reset to the
     * minimum value and the next position is increased.
     * 
     * @param position
     *            Index to the currentCounts[] array that should be increased
     */
    private void increaseCounter(int position) {

        // This should never happen, but let's check, just in case
        if (position >= currentCounts.length) {
            throw new IllegalArgumentException(
                    "Cannot increase the currentCounts counter at position "
                            + position);
        }

        lastIncreasedPosition = position;

        // Keep a lock on the currentCounts, because
        // getFinishedPercentage() might be called from another thread
        synchronized (currentCounts) {

            if (currentCounts[position] < maxCounts[position]) {
                currentCounts[position]++;
            } else {
                // Reset the value at given position, and increase the next one.
                if (position < isotopes.length - 1) {
                    currentCounts[position] = minCounts[position];
                    increaseCounter(position + 1);
                } else {
                    // If we are already at the last position, that means we
                    // have covered the whole search space and we can stop the
                    // search.
                    searchRunning = false;

                    // Copy the maxCounts[] array to currentCounts[]. This
                    // ensures that getFinishedPercentage() will return 1.0
                    System.arraycopy(maxCounts, 0, currentCounts, 0,
                            maxCounts.length);
                }
            }
        }

    }

    /**
     * Calculates the exact mass of the currently evaluated formula. Basically,
     * it multiplies the currentCounts[] array by the masses of the isotopes in
     * the isotopes[] array.
     */
    private double calculateCurrentMass() {
        double mass = 0;
        for (int i = 0; i < isotopes.length; i++) {
            mass += currentCounts[i] * isotopes[i].getExactMass();
        }

        return mass;
    }

    /**
     * Generates a MolecularFormula object that contains the isotopes in the
     * isotopes[] array with counts in the currentCounts[] array. In other
     * words, generates a proper CDK representation of the currently examined
     * formula.
     */
    private IMolecularFormula generateFormulaObject() {
        IMolecularFormula formulaObject = builder
                .newInstance(IMolecularFormula.class);
        for (int i = 0; i < isotopes.length; i++) {
            if (currentCounts[i] == 0)
                continue;
            formulaObject.addIsotope(isotopes[i], currentCounts[i]);
        }
        return formulaObject;

    }

    /**
     * Returns a value between 0.0 and 1.0 indicating what portion of the search
     * space has been examined so far by this MolecularFormulaGenerator. Before
     * the first call to {@link getNextFormula()}, this method returns 0. After
     * all possible formulas are generated, this method returns 1.0 (the exact
     * returned value might be slightly off due to rounding errors). This method
     * can be called from any thread.
     */
    public double getFinishedPercentage() {
        double result = 0.0;
        double remainingPerc = 1.0;

        // Keep a lock on currentCounts, otherwise it might change during the
        // calculation
        synchronized (currentCounts) {
            for (int i = currentCounts.length - 1; i >= 0; i--) {
                double max = maxCounts[i];
                if (i > 0)
                    max += 1.0;
                result += remainingPerc * ((double) currentCounts[i] / max);
                remainingPerc /= max;
            }
        }
        return result;
    }

    /**
     * Cancel the current search. This method can be called from any thread. If
     * another thread is executing the {@link getNextFormula()} method, that
     * method call will return immediately with null return value. If another
     * thread is executing the {@link getAllFormulas()} method, that method call
     * will return immediately, returning all formulas generated until this
     * moment. The search cannot be restarted once canceled - any subsequent
     * calls to {@link getNextFormula()} will return null.
     */
    public void cancel() {
        logger.info("Canceling MolecularFormulaGenerator");
        this.searchRunning = false;
    }

    /**
     * A simple {@link Comparator} implementation for sorting IIsotopes by their
     * mass
     */
    private class IIsotopeSorterByMass implements Comparator<IIsotope> {
        @Override
        public int compare(IIsotope i1, IIsotope i2) {
            return Double.compare(i1.getExactMass(), i2.getExactMass());
        }
    }
}
