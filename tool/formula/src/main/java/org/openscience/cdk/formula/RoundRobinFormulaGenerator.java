package org.openscience.cdk.formula;

import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    protected final RangeMassDecomposer.DecompIterator decomposer;
    /**
     * defines the alphabet as well as the lower- and upperbounds of the chemical alphabet
     */
    protected final MolecularFormulaRange mfRange;
    /**
     * is used to estimate which part of the search space is already traversed
     */
    protected volatile int[] lastDecomposition;
    /**
     * a flag indicating if the algorithm is done or should be canceled.
     * This flag have to be volatile to allow other threads to cancel the enumeration procedure.
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
    RoundRobinFormulaGenerator(final IChemObjectBuilder builder,
                               final double minMass, final double maxMass,
                               final MolecularFormulaRange mfRange) {
        this.builder = builder;
        final List<IIsotope> isotopes = new ArrayList<>(mfRange.getIsotopeCount());
        for (IIsotope iso : mfRange.isotopes()) {
            if (mfRange.getIsotopeCountMin(iso) >= 0 && mfRange.getIsotopeCountMax(iso) > 0) isotopes.add(iso);
        }
        this.decomposer = DecomposerFactory.getInstance().getDecomposerFor(isotopes.toArray(new IIsotope[isotopes.size()])).decomposeIterator(minMass, maxMass, mfRange);
        this.done = false;
        this.mfRange = mfRange;
    }

    /**
     * @see MolecularFormulaGenerator#getNextFormula()
     */
    @Override
    public synchronized IMolecularFormula getNextFormula() {
        if (!done && decomposer.next()) {
            this.lastDecomposition = decomposer.getCurrentCompomere();
            return decomposer.generateCurrentMolecularFormula(builder);
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
        if (done) return 1d;
        final int[] lastDecomposition = this.lastDecomposition;
        if (lastDecomposition == null) return 0;
        double result = 0.0;
        double remainingPerc = 1.0;

        for (int i = lastDecomposition.length - 1; i >= 0; i--) {
            double max = mfRange.getIsotopeCountMax(decomposer.weights.get(i).getOwner());
            if (i > 0)
                max += 1.0;
            result += remainingPerc * ((double) lastDecomposition[i] / max);
            remainingPerc /= max;
        }
        return result;
    }

    /**
     * Cancel the computation
     */
    @Override
    public void cancel() {
        done=true;
    }
}

/**
 * As every decomposer has to be initialized (i.e. an extended residue table has to be computed) it is important
 * to cache decomposer instances when decomposing a large set of numbers (initialization time is only dependent on
 * alphabet size. For decomposing large masses the decomposition time might exceed the initialization time. For very
 * small masses it is the other way around). This simple cache stores the last 10 used decomposers. It is very likely
 * that for a given mass spectrum only one alphabet is chosen to decompose all peaks. In this case this cache should
 * be sufficient.
 */
final class DecomposerFactory {

    private static final int maximalNumberOfCachedDecomposers = 10;
    private final static DecomposerFactory instance = new DecomposerFactory();
    private final List<RangeMassDecomposer> decomposerCache;

    private DecomposerFactory() {
        this.decomposerCache = new ArrayList<>(maximalNumberOfCachedDecomposers);
    }

    public static DecomposerFactory getInstance() {
        return instance;
    }

    public RangeMassDecomposer getDecomposerFor(IIsotope[] alphabet) {
        for (RangeMassDecomposer decomposer : decomposerCache) {
            if (decomposer.isCompatible(alphabet)) {
                return decomposer;
            }
        }
        if (decomposerCache.size() >= maximalNumberOfCachedDecomposers) decomposerCache.remove(0);
        final RangeMassDecomposer decomposer = new RangeMassDecomposer(alphabet);
        decomposerCache.add(decomposer);
        return decomposer;
    }

}

/**
 * Decomposes a given mass over an alphabet, returning all decompositions which masses equals the given mass
 * considering a given deviation.
 * MassDecomposerFast calculates the decompositions with the help of an ERT containing deviation information, not requiring to iterate over all different integer mass values {@cdk.cite Duehrkop2013}.
 *
 * @author Marcus Ludwig, Kai Dührkop
 */
class RangeMassDecomposer {

    private final List<ChemicalElement> weights;
    private final IIsotope[] elements;
    private double precision;
    private double minError;
    private double maxError;

    /**
     * Avoid locks by making ERTs volatile. This leads to the situation that several threads might accidentally compute
     * the same ERT tables. However, as soon as an ERT table is written it is synchronized around all threads. After
     * writing an ERT table it is never changed, so additional locking is not necessary.
     */
    private volatile int[][][] ERTs;

    /**
     * @param allowedIsotopes array of the elements of the alphabet
     */
    RangeMassDecomposer(IIsotope[] allowedIsotopes) {
        this.ERTs = null;
        this.precision = findOptimalPrecision();
        final int n = allowedIsotopes.length;
        this.weights = new ArrayList<>(n);
        this.elements = new IIsotope[allowedIsotopes.length];
        for (IIsotope allowedIsotope : allowedIsotopes) {
            weights.add(new ChemicalElement(allowedIsotope, allowedIsotope.getExactMass()));
        }
        Collections.sort(weights);
        for (int i = 0; i < n; ++i) {
            elements[i] = weights.get(i).getOwner();
        }
    }

    private static int gcd(int u, int v) {
        int r;

        while (v != 0) {
            r = u % v;
            u = v;
            v = r;
        }
        return u;
    }

    /**
     * checks if this decomposer can be used for the given alphabet. This is the case when the decomposer
     * contains the same elements as the given alphabet.
     * <p>
     * It would be also the case when the given alphabet is a subset of this decomposers alphabet. However,
     * if the alphabet size of the decomposer is much larger, the decomposer might be slower anyways due to
     * larger memory footprint. As we expect that the alphabet does not change that often, it might be
     * sufficient to just compare the arrays.
     */
    boolean isCompatible(IIsotope[] elements) {
        return Arrays.equals(elements, this.elements);
    }

    /*

     */
    private double findOptimalPrecision() {
        return 1d / 5963.337687d; // This blowup is optimized for organic compounds based on the CHNOPS alphabet
    }

    /**
     * Initializes the decomposer. Computes the extended residue table. This have to be done only one time for
     * a given alphabet, independently from the masses you want to decompose. This method is called automatically
     * if you compute the decompositions, so call it only if you want to control the time of the initialisation.
     */
    private void init() {
        if (ERTs != null) return;
        synchronized (this) {
            if (ERTs != null) return;
            discretizeMasses();
            divideByGCD();
            computeLCMs();
            calcERT();
            computeErrors();
        }
    }

    /**
     * Check if a mass is decomposable. This is done in constant time (especially: it is very very very fast!).
     * But it doesn't check if there is a valid decomposition. Therefore, even if the method returns true,
     * all decompositions may be invalid for the given validator or given bounds.
     * #decompose(mass) uses this function before starting the decomposition, therefore this method should only
     * be used if you don't want to start the decomposition algorithm.
     *
     * @return true if the mass is decomposable, ignoring bounds or any additional filtering rule
     */
    boolean maybeDecomposable(double from, double to) {
        init();
        final int[][][] ERTs = this.ERTs;
        final int[] minmax = new int[2];
        //normal version seems to be faster, because it returns after first hit
        integerBound(from, to, minmax);
        final int a = weights.get(0).getIntegerMass();
        for (int i = minmax[0]; i <= minmax[1]; ++i) {
            final int r = i % a;
            if (i >= ERTs[0][r][weights.size() - 1]) return true;
        }
        return false;
    }

    /**
     * Returns an iterator over all decompositons of this mass range
     *
     * @param from       lowest mass to decompose
     * @param to         (inclusive) largest mass to decompose
     * @param boundaries defines lowerbounds and upperbounds for the number of elements
     */
    DecompIterator decomposeIterator(double from, double to, MolecularFormulaRange boundaries) {
        init();
        if (to < 0d || from < 0d)
            throw new IllegalArgumentException("Expect positive mass for decomposition: [" + from + ", " + to + "]");
        if (to < from) throw new IllegalArgumentException("Negative range given: [" + from + ", " + to + "]");
        final int[] minValues = new int[weights.size()];
        final int[] boundsarray = new int[weights.size()];
        double cfrom = from, cto = to;
        Arrays.fill(boundsarray, Integer.MAX_VALUE);
        if (boundaries != null) {
            for (int i = 0; i < boundsarray.length; i++) {
                IIsotope el = weights.get(i).getOwner();
                int max = boundaries.getIsotopeCountMax(el);
                int min = boundaries.getIsotopeCountMin(el);
                if (min >= 0 || max >= 0) {
                    boundsarray[i] = max - min;
                    minValues[i] = min;
                    if (minValues[i] > 0) {
                        final double reduceWeightBy = weights.get(i).getMass() * min;
                        cfrom -= reduceWeightBy;
                        cto -= reduceWeightBy;
                    }
                }
            }
        }
        final int[] minmax = new int[2];
        integerBound(cfrom, cto, minmax);
        final int deviation = minmax[1] - minmax[0];
        //calculate the required ERTs
        if ((1 << (ERTs.length - 1)) <= deviation) {
            calcERT(deviation);
        }
        final int[][][] ERTs = this.ERTs;

        //take ERT with required deviation
        int[][] currentERT;
        if (deviation == 0) currentERT = ERTs[0];
        else currentERT = ERTs[32 - Integer.numberOfLeadingZeros(deviation)];

        return new DecompIterator(currentERT, minmax[0], minmax[1], from, to, minValues, boundsarray, weights);
    }

    /**
     * calculates ERTs to look up whether a mass or lower masses within a certain deviation are decomposable.
     * only ERTs for deviation 2^x are calculated
     */
    private void calcERT(int deviation) {
        final int[][][] ERTs = this.ERTs;
        final int currentLength = ERTs.length;

        // we have to extend the ERT table

        int[][] lastERT = ERTs[ERTs.length - 1];
        int[][] nextERT = new int[lastERT.length][weights.size()];
        if (currentLength == 1) {
            //first line compares biggest residue and 0
            for (int j = 0; j < weights.size(); j++) {
                nextERT[0][j] = Math.min(lastERT[nextERT.length - 1][j], lastERT[0][j]);
            }
            for (int i = 1; i < nextERT.length; i++) {
                for (int j = 0; j < weights.size(); j++) {
                    nextERT[i][j] = Math.min(lastERT[i][j], lastERT[i - 1][j]);
                }
            }
        } else {
            int step = (1 << (currentLength - 2));
            for (int i = step; i < nextERT.length; i++) {
                for (int j = 0; j < weights.size(); j++) {
                    nextERT[i][j] = Math.min(lastERT[i][j], lastERT[i - step][j]);
                }
            }
            //first lines compared with last lines (greatest residues) because of modulo's cyclic characteristic
            for (int i = 0; i < step; i++) {
                for (int j = 0; j < weights.size(); j++) {
                    nextERT[i][j] = Math.min(lastERT[i][j], lastERT[i + nextERT.length - step][j]);
                }
            }
        }

        // now store newly calculated ERT
        synchronized (this) {
            final int[][][] tables = this.ERTs;
            if (tables.length == currentLength) {
                this.ERTs = Arrays.copyOf(this.ERTs, this.ERTs.length + 1);
                this.ERTs[this.ERTs.length - 1] = nextERT;
            } else {
                // another background thread did already compute the ERT. So we don't have to do this again
            }
        }
        // recursively calculate ERTs for higher deviations
        // current ERT is already sufficient
        if ((1 << (currentLength - 1)) <= deviation) calcERT(deviation);
    }

    private void calcERT() {
        int firstLongVal = weights.get(0).getIntegerMass();
        int[][] ERT = new int[firstLongVal][weights.size()];
        int d, r, n, argmin;

        //Init
        ERT[0][0] = 0;
        for (int i = 1; i < ERT.length; ++i) {
            ERT[i][0] = Integer.MAX_VALUE; // should be infinity
        }

        //Filling the Table, j loops over columns
        for (int j = 1; j < ERT[0].length; ++j) {
            ERT[0][j] = 0; // Init again
            d = gcd(firstLongVal, weights.get(j).getIntegerMass());
            for (int p = 0; p < d; p++) { // Need to start d Round Robin loops
                if (p == 0) {
                    n = 0; // 0 is the min in the complete RT or the first p-loop
                } else {
                    n = Integer.MAX_VALUE; // should be infinity
                    argmin = p;
                    for (int i = p; i < ERT.length; i += d) { // Find Minimum in specific part of ERT
                        if (ERT[i][j - 1] < n) {
                            n = ERT[i][j - 1];
                            argmin = i;
                        }
                    }
                    ERT[argmin][j] = n;
                }
                if (n == Integer.MAX_VALUE) { // Minimum of the specific part of ERT was infinity
                    for (int i = p; i < ERT.length; i += d) { // Fill specific part of ERT with infinity
                        ERT[i][j] = Integer.MAX_VALUE;
                    }
                } else { // Do normal loop
                    for (long i = 1; i < ERT.length / d; ++i) { // i is just a counter
                        n += weights.get(j).getIntegerMass();
                        if (n < 0) {
                            throw new ArithmeticException("Integer overflow occurs. DECOMP cannot calculate decompositions for the given alphabet as it exceeds the 32 bit integer space. Please use a smaller precision value.");
                        }
                        r = n % firstLongVal;
                        if (ERT[r][j - 1] < n) n = ERT[r][j - 1]; // get the min
                        ERT[r][j] = n;
                    }
                }
            } // end for p
        } // end for j
        synchronized (this) {
            if (this.ERTs == null) {
                this.ERTs = new int[][][]{ERT};
            }
        }
    }


    private void discretizeMasses() {
        // compute integer masses
        for (final ChemicalElement weight : weights) {
            weight.setIntegerMass((int) (weight.getMass() / precision));
        }
    }

    private void divideByGCD() {
        if (weights.size() > 0) {
            int d = gcd(weights.get(0).getIntegerMass(), weights.get(1).getIntegerMass());
            for (int i = 2; i < weights.size(); ++i) {
                d = gcd(d, weights.get(i).getIntegerMass());
                if (d == 1) return;
            }
            precision *= d;
            for (ChemicalElement weight : weights) {
                weight.setIntegerMass(weight.getIntegerMass() / d);
            }
        }
    }

    private void computeLCMs() {
        final ChemicalElement first = weights.get(0);
        first.setL(1);
        first.setLcm(first.getIntegerMass());

        for (int i = 1; i < weights.size(); i++) {
            final ChemicalElement weight = weights.get(i);
            int temp = first.getIntegerMass() / gcd(first.getIntegerMass(), weight.getIntegerMass());
            weight.setL(temp);
            weight.setLcm(temp * weight.getIntegerMass());
        }
    }

    private void computeErrors() {
        this.minError = 0d;
        this.maxError = 0d;
        for (ChemicalElement weight : weights) {
            final double error = (precision * weight.getIntegerMass() - weight.getMass()) / weight.getMass();
            minError = Math.min(minError, error);
            maxError = Math.max(maxError, error);
        }
    }

    private void integerBound(double from, double to, int[] bounds) {
        final double fromD = Math.ceil((1 + minError) * from / precision);
        final double toD = Math.floor((1 + maxError) * to / precision);
        if (fromD > Integer.MAX_VALUE || toD > Integer.MAX_VALUE) {
            throw new ArithmeticException("Given mass is too large to decompose. Please use a smaller precision value, i.e. mass/precision have to be within 32 bit integer space");
        }

        bounds[0] = Math.max(0, (int) fromD);
        bounds[1] = Math.max(0, (int) toD);
    }

    /**
     * Iterator implementation of the loop
     * We do not use static classes. This gives us the possibility to make some of the variables behave thread safe
     * and resistant against changes from the user.
     */
    static class DecompIterator {
        // final initialization values
        final int[][] ERT;
        final double minDoubleMass;
        final double maxDoubleMass;
        final int[] buffer;
        final int[] minValues;
        final int[] maxValues;
        final List<ChemicalElement> weights;

        // loop variables

        final int[] j;
        final int[] m;
        final int[] lbound;
        final int[] r;
        final int k;
        final int a;
        final int deviation;
        final int ERTdev;
        boolean flagWhile;
        boolean rewind;
        int i;


        DecompIterator(int[][] ERT, int minIntegerMass, int maxIntegerMass, double minDoubleMass, double maxDoubleMass, int[] minValues, int[] maxValues, List<ChemicalElement> weights) {
            this.ERT = ERT;
            this.minDoubleMass = minDoubleMass;
            this.maxDoubleMass = maxDoubleMass;
            this.buffer = new int[weights.size()];
            if (minValues != null) {
                boolean allZero = true;
                for (int k : minValues) if (k > 0) allZero = false;
                if (!allZero) this.minValues = minValues;
                else this.minValues = null;
            } else this.minValues = null;
            this.maxValues = maxValues;
            this.weights = weights;

            k = weights.size();
            j = new int[k];
            m = new int[k];
            lbound = new int[k];
            r = new int[k];
            flagWhile = false; // flag whether we are in the while-loop or not
            a = weights.get(0).getIntegerMass();
            // Init
            for (int i = 1; i < k; ++i) {
                lbound[i] = Integer.MAX_VALUE; // this is just to ensure, that lbound < m in the first iteration
            }

            i = k - 1;
            m[i] = maxIntegerMass; // m[i] corresponds to M, m[i-1] ^= m
            this.rewind = false;
            this.deviation = maxIntegerMass - minIntegerMass;
            this.ERTdev = Integer.highestOneBit(deviation);

        }

        boolean next() {
            while (decomposeRangeIntegerMass()) {
                if (checkCompomere()) return true;
            }
            return false;
        }

        private boolean decomposeRangeIntegerMass() {
            if (rewind) {
                afterFindingADecomposition();
                rewind = false;
            }
            while (i != k) {
                if (i == 0) {
                    final int v = (m[i] / a);
                    if (v <= maxValues[0]) {
                        buffer[0] = v;
                        rewind = true;
                        return true;
                    }
                    ++i; // "return" from recursion
                    flagWhile = true; // in this recursion-depth we are in the while-loop, cause the next recursion (the one we just exited) was called
                    m[i - 1] -= weights.get(i).getLcm(); // execute the rest of the while
                    buffer[i] += weights.get(i).getL();
                } else {
                    if (flagWhile) {
                        if (m[i - 1] >= lbound[i] && buffer[i] <= maxValues[i]) { //currently in while loop
                            --i; // "do" recursive call
                        } else {
                            flagWhile = false; //
                        }
                    } else { //we are in the for-loop
                        if (j[i] < weights.get(i).getL() && m[i] - j[i] * weights.get(i).getIntegerMass() >= 0) {
                            buffer[i] = j[i];
                            m[i - 1] = m[i] - j[i] * weights.get(i).getIntegerMass();
                            r[i] = m[i - 1] % a;
                            //changed from normal algorithm: you have to look up the minimum at 2 position
                            int pos = r[i] - deviation + ERTdev;
                            if (pos < 0) pos += ERT.length;
                            lbound[i] = Math.min(ERT[r[i]][i - 1], ERT[pos][i - 1]);
                            flagWhile = true; // call the while loop
                            ++j[i];
                        } else { //exit for loop
                            // reset "function variables"
                            lbound[i] = Integer.MAX_VALUE;
                            j[i] = 0;
                            buffer[i] = 0;
                            ++i; // "return" from recursion
                            if (i != k) { // only if we are not done
                                flagWhile = true; // in this recursion-depth we are in the while-loop, cause the next recursion was called
                                m[i - 1] -= weights.get(i).getLcm(); // execute the rest of the while
                                buffer[i] += weights.get(i).getL();
                            }
                        }
                    }
                } // end if i == 0
            } // end while

            return false;
        }

        private boolean checkCompomere() {
            if (minValues != null) {
                for (int j = 0; j < minValues.length; ++j) {
                    buffer[j] += minValues[j];
                }
            }
            // calculate mass of decomposition
            double exactMass = 0;
            for (int j = 0; j < buffer.length; ++j) {
                exactMass += buffer[j] * weights.get(j).getMass();
            }
            return exactMass >= minDoubleMass && exactMass <= maxDoubleMass;
        }

        private void afterFindingADecomposition() {

            if (minValues != null) {
                for (int j = 0; j < minValues.length; ++j) {
                    buffer[j] -= minValues[j];
                }
            }

            ++i; // "return" from recursion
            flagWhile = true; // in this recursion-depth we are in the while-loop, cause the next recursion (the one we just exited) was called
            m[i - 1] -= weights.get(i).getLcm(); // execute the rest of the while
            buffer[i] += weights.get(i).getL();
        }

        IMolecularFormula generateCurrentMolecularFormula(IChemObjectBuilder builder) {
            IMolecularFormula formula = builder.newInstance(IMolecularFormula.class);
            for (int k = 0; k < buffer.length; ++k) {
                if (buffer[k] > 0) formula.addIsotope(getCharacterAt(k), buffer[k]);
            }
            return formula;
        }

        int[] getCurrentCompomere() {
            return buffer;
        }

        IIsotope getCharacterAt(int index) {
            return weights.get(index).getOwner();
        }
    }

}


/**
 * A POJO storing the weight information about a character in the alphabet
 */
class ChemicalElement implements Comparable<ChemicalElement> {

    /**
     * corresponding character in the alphabet
     */
    private final IIsotope owner;

    /**
     * the exact mass of the character
     */
    private final double mass;

    /**
     * the transformation of the mass in the integer space
     */
    private int integerMass;

    private int l;
    private int lcm;

    ChemicalElement(IIsotope owner, double mass) {
        this.owner = owner;
        this.mass = mass;
    }

    IIsotope getOwner() {
        return owner;
    }

    double getMass() {
        return mass;
    }

    int getIntegerMass() {
        return integerMass;
    }

    void setIntegerMass(int integerMass) {
        this.integerMass = integerMass;
    }

    int getL() {
        return l;
    }

    void setL(int l) {
        this.l = l;
    }

    int getLcm() {
        return lcm;
    }

    void setLcm(int lcm) {
        this.lcm = lcm;
    }

    @Override
    public int compareTo(ChemicalElement tWeight) {
        return (int) Math.signum(mass - tWeight.mass);
    }
}
