package org.openscience.cdk.formula;

import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;

/**
 * This class generates molecular formulas within given mass range and elemental
 * composition.
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
 * This class offers two implementations: The Round Robin algorithm {@cdk.cite Boecker2008} on mass ranges
 * {@cdk.cite Duehrkop2013} is used on most inputs. For special cases (e.g. single elements, extremely large mass ranges)
 * a full enumeration algorithm {@cdk.cite Pluskal2012} is used.
 *
 * The Round Robin algorithm was originally developed for the SIRIUS 3 software. The full enumeration algorithm was
 * originally developed for a MZmine 2 framework module, published in Pluskal et al. {@cdk.cite Pluskal2012}.
 *
 * @cdk.module formula
 * @author Tomas Pluskal, Kai Dührkop, Marcus Ludwig
 * @cdk.created 2014-12-28
 * @cdk.githash
 */
public class MolecularFormulaGenerator implements IFormulaGenerator {

    /**
     * The chosen implementation
     */
    protected final IFormulaGenerator formulaGenerator;

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
        checkInputParameters(builder,minMass,maxMass,mfRange);
        this.formulaGenerator = isIllPosed(minMass, maxMass, mfRange) ? new FullEnumerationFormulaGenerator(builder, minMass, maxMass, mfRange) : new RoundRobinFormulaGenerator(builder, minMass, maxMass, mfRange);
    }

    /**
     * Decides wheter to use the round robin algorithm or full enumeration algorithm.
     * The round robin implementation here is optimized for chemical elements in organic compounds. It gets slow
     * if
     * - the mass of the smallest element is very large (i.e. hydrogen is not allowed)
     * - the maximal mass to decompose is too large (round robin always decomposes integers. Therefore, the mass have
     *   to be small enough to be represented as 32 bit integer)
     * - the number of elements in the set is extremely small (in this case, however, the problem becomes trivial anyways)
     *
     * In theory we could handle these problems by optimizing the way DECOMP discretizes the masses. However, it's easier
     * to just fall back to the full enumeration method if a problem occurs (especially, because most of the problems
     * lead to trivial cases that are fast to compute).
     *
     * @return true if the problem is ill-posed (i.e. should be calculated by full enumeration method)
     */
    private static boolean isIllPosed(double minMass, double maxMass, MolecularFormulaRange mfRange) {
        // when the number of integers to decompose is incredible large
        // we have to adjust the internal settings (e.g. precision!)
        // instead we simply fallback to the full enumeration method
        if (maxMass-minMass > 1) return true;
        if (maxMass > 400000) return true;
        // if the number of elements to decompose is very small
        // we fall back to the full enumeration methods as the
        // minimal decomposable mass of a certain residue class might
        // exceed the 32 bit integer space
        if (mfRange.getIsotopeCount() <= 2) return true;

        return false;
    }

    /**
     * Returns next generated formula or null in case no new formula was found
     * (search is finished). There is no guaranteed order in which the formulas
     * are generated.
     */
    @Override
    public IMolecularFormula getNextFormula() {
        return formulaGenerator.getNextFormula();
    }

    /**
     * Generates a {@link IMolecularFormulaSet} by repeatedly calling {@link
     * MolecularFormulaGenerator#getNextFormula()} until all possible formulas are generated. There is no
     * guaranteed order to the formulas in the resulting
     * {@link IMolecularFormulaSet}.
     *
     * Note: if some formulas were already generated by calling {@link
     * MolecularFormulaGenerator#getNextFormula()} on this MolecularFormulaGenerator instance, those
     * formulas will not be included in the returned
     * {@link IMolecularFormulaSet}.
     *
     * @see #getNextFormula()
     */
    @Override
    public IMolecularFormulaSet getAllFormulas() {
        return formulaGenerator.getAllFormulas();
    }

    /**
     * Returns a value between 0.0 and 1.0 indicating what portion of the search
     * space has been examined so far by this MolecularFormulaGenerator. Before
     * the first call to {@link MolecularFormulaGenerator#getNextFormula()}, this method returns 0. After
     * all possible formulas are generated, this method returns 1.0 (the exact
     * returned value might be slightly off due to rounding errors). This method
     * can be called from any thread.
     */
    @Override
    public double getFinishedPercentage() {
        return formulaGenerator.getFinishedPercentage();
    }

    /**
     * Cancel the current search. This method can be called from any thread. If
     * another thread is executing the {@link MolecularFormulaGenerator#getNextFormula()} method, that
     * method call will return immediately with null return value. If another
     * thread is executing the {@link MolecularFormulaGenerator#getAllFormulas()} method, that method call
     * will return immediately, returning all formulas generated until this
     * moment. The search cannot be restarted once canceled - any subsequent
     * calls to {@link MolecularFormulaGenerator#getNextFormula()} will return null.
     */
    @Override
    public void cancel() {
        formulaGenerator.cancel();
    }

    /**
     * Checks if input parameters are valid and throws an IllegalArgumentException otherwise.
     */
    protected void checkInputParameters(final IChemObjectBuilder builder,
                                        final double minMass, final double maxMass,
                                        final MolecularFormulaRange mfRange) {
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

        // Sort the elements by mass in ascending order. That speeds up
        // the search.
        for (IIsotope isotope : mfRange.isotopes()) {
            // Check if exact mass of each isotope is set
            if (isotope.getExactMass() == null)
                throw new IllegalArgumentException(
                        "The exact mass value of isotope " + isotope
                                + " is not set");
        }
    }
}
