package org.openscience.cdk.formula;

import org.openscience.cdk.decomp.ChemicalAlphabet;
import org.openscience.cdk.decomp.DecompIterator;
import org.openscience.cdk.decomp.DecomposerFactory;
import org.openscience.cdk.decomp.Interval;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;

import java.util.HashMap;

/**
 * This class generates molecular formulas within given mass range and elemental
 * composition. It should not be used directly but via the {@link MolecularFormulaGenerator} as it cannot deal with
 * all kind of inputs.
 *
 * This class is using the Round Robin algorithm {@cdk.cite Boecker2008} on mass ranges
 * {@cdk.cite Duehrkop2013}. It uses dynamic programming to compute an extended residue table which allows a constant
 * time lookup if some integer mass is decomposable over a certain alphabet. For each alphabet this table has to be
 * computed only once and is then cached in memory. Using this table the algorithm can decide directly which masses
 * are decomposable and, therefore, is only enumerating formulas which masses are in the given integer mass range. The
 * mass range decomposer is using a logarithmic number of tables, one for each alphabet and an allowed mass deviation.
 * It, therefore, allows to decompose a whole range of integer numbers instead of a single one.
 *
 * As masses are real values, the decomposer has to translate values from real space to integer space and vice versa.
 * This translation is done via multiplying with a blow-up factor (which is by default 5963.337687) and rounding the
 * results. The blow-up factor is optimized for organic molecules. For other alphabets (e.g. amino acids or molecules
 * without hydrogens) another blow-up factor have to be chosen. Therefore, it is recommended to use this decomposer
 * only for organic molecular formulas.
 */
class RoundRobinFormulaGenerator implements IFormulaGenerator {

    /**
     * generates the IMolecularFormula and IMolecularFormulaSet instances
     */
    protected final IChemObjectBuilder builder;

    /**
     * the decomposer algorithm with the cached extended residue table
     */
    protected final DecompIterator<IIsotope> decomposer;

    /**
     * The used chemical alphabet (a mapping of IIsotopes to their masses)
     */
    protected final ChemicalAlphabet alphabet;

    /**
     * a flag indicating if the algorithm is done or should be canceled.
     * This flag have to be volatile to allow other threads to cancel the enumeration.
     */
    protected volatile boolean done;

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
    public RoundRobinFormulaGenerator(final IChemObjectBuilder builder,
                                      final double minMass, final double maxMass,
                                      final MolecularFormulaRange mfRange) {
        this.builder = builder;
        this.alphabet = new ChemicalAlphabet(builder, mfRange);
        final HashMap<IIsotope, Interval> boundaries = new HashMap<>(alphabet.size());
        for (IIsotope i : mfRange.isotopes()) {
            boundaries.put(i, new Interval(mfRange.getIsotopeCountMin(i), mfRange.getIsotopeCountMax(i)));
        }
        this.decomposer = DecomposerFactory.getInstance().getDecomposerFor(alphabet).decomposeIterator(minMass, maxMass,boundaries);
        this.done = false;
    }

    /**
     * @see MolecularFormulaGenerator#getNextFormula()
     */
    @Override
    public synchronized IMolecularFormula getNextFormula() {
        if (!done && decomposer.next()) {
            return alphabet.buildFormulaFromCompomere(decomposer.getCurrentCompomere(), decomposer.getAlphabetOrder());
        } else {
            done = true;
            return null;
        }
    }

    /**
     * @see MolecularFormulaGenerator#getAllFormulas()
     */
    @Override
    public synchronized IMolecularFormulaSet getAllFormulas() {
        final IMolecularFormulaSet set = builder.newInstance(IMolecularFormulaSet.class);
        if (done) return set;
        for (IMolecularFormula formula = getNextFormula(); formula != null; formula = getNextFormula()) {
            set.addMolecularFormula(formula);
            if (done) return set;
        }
        done = true;
        return set;
    }

    /**
     * This method does not work for Round Robin as the algorithm only enumerates formulas which really have the
     * searched mass range (except for false positives due to rounding errors). As the exact number of molecular formulas
     * with a given mass is unknown (calculating it is as expensive as enumerating them) there is no way to give a
     * progress number. Therefore, the method returns just 0 if it's enumerating and 1 if it's done.
     */
    @Override
    public double getFinishedPercentage() {
        return done ? 1 : 0;
    }

    /**
     * Cancel the computation
     */
    @Override
    public void cancel() {
        done=true;
    }
}
