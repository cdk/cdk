/*
 *  This file is part of the SIRIUS library for analyzing MS and MS/MS data
 *
 *  Copyright (C) 2013-2015 Kai Dührkop
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with SIRIUS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openscience.cdk.decomp;

import java.util.*;

/**
 * Decomposes a given mass over an alphabet, returning all decompositions which mass equals the given mass
 * considering a given deviation.
 * {@cdk.cite Boecker2008}
 * @param <T>
 *           type of the alphabet's characters
 * @author Anton Pervukhin, Kai Dührkop, Marcus Ludwig
 */
class MassDecomposer<T> {

    protected int[][] ERT;
    protected double precision;
    protected final List<Weight<T>> weights;
    protected double minError, maxError;
    protected final Alphabet<T> alphabet;
    protected final int[] orderedCharacterIds;

    /**
     * @param alphabet the alphabet the mass is decomposed over
     */
    public MassDecomposer(Alphabet<T> alphabet) {
        this.precision = findOptimalPrecision();
        final int n = alphabet.size();
        this.weights = new ArrayList<Weight<T>>(n);
        for (int i=0; i < n; ++i) {
            weights.add(new Weight<T>(alphabet.get(i), alphabet.weightOf(i)));
        }
        Collections.sort(weights);
        this.alphabet = alphabet;
        this.orderedCharacterIds = new int[alphabet.size()];
        for (int i=0; i < alphabet.size(); ++i) {
            orderedCharacterIds[i] = alphabet.indexOf(weights.get(i).getOwner());
        }
    }

    public DecompIterator<T> decomposeIterator(double from, double to) {
        return decomposeIterator(from, to, null);
    }

    public DecompIterator<T> decomposeIterator(double from, double to, Map<T, Interval> boundaries) {
        init();
        if (to < 0d || from < 0d) throw new IllegalArgumentException("Expect positive mass for decomposition: [" + from + ", " + to + "]");
        if (to < from) throw new IllegalArgumentException("Negative range given: [" + from + ", " + to + "]");
        final int[] minValues = new int[weights.size()];
        final int[] boundsarray = new int[weights.size()];
        boolean minAllZero = true;
        double cfrom = from, cto = to;
        Arrays.fill(boundsarray, Integer.MAX_VALUE);
        if (boundaries!=null && !boundaries.isEmpty()) {
            for (int i = 0; i < boundsarray.length; i++) {
                T el = weights.get(i).getOwner();
                Interval range = boundaries.get(el);
                if (range != null) {
                    boundsarray[i] = range.getMax()-range.getMin();
                    minValues[i] = range.getMin();
                    if (minValues[i] > 0) {
                        minAllZero = false;
                        final double reduceWeightBy = weights.get(i).getMass() * range.getMin();
                        cfrom -= reduceWeightBy;
                        cto -= reduceWeightBy;
                    }
                }
            }
        }

        final Interval interval = integerBound(cfrom, cto);

        return new DecompIteratorImpl<T>(ERT, interval.getMin(), interval.getMax(), from, to, minValues,  boundsarray, alphabet, weights, orderedCharacterIds.clone());
    }

    protected double findOptimalPrecision() {
        return 1d/5963.337687d; // TODO: check alphabet and mass deviation, define optimal blowup for given alphabet
    }

    public Alphabet<T> getAlphabet() {
        return alphabet;
    }

    public boolean maybeDecomposable(double startMass, double endMass) {
        init();
        final Interval range = integerBound(startMass, endMass);
        final int a = weights.get(0).getIntegerMass();
        for (int i = range.getMin(); i <= range.getMax(); ++i) {
            final int r = i % a;
            if (i >= ERT[r][weights.size()-1]) return true;
        }
        return false;
    }

    /**
     */
    public List<int[]> decompose(double from, double to) {
        return decompose(from, to, null);
    }

    /**
     * computes all decompositions for the given mass. The runtime depends only on the number of characters and the
     * number of decompositions. Therefore this method is very fast as int as the number of decompositions is low.
     * Unfortunately, the number of decompositions increases nearly exponential in the number of characters and in the
     * input mass.
     *
     * This function can be called in multiple threads in parallel, because it does not modify the decomposer

     */
    public List<int[]> decompose(final double from, final double to, Map<T, Interval> boundaries){
        init();
        if (to < 0d || from < 0d) throw new IllegalArgumentException("Expect positive mass for decomposition: [" + from + ", " + to + "]");
        if (to < from) throw new IllegalArgumentException("Negative range given: [" + from + ", " + to + "]");
        if (to == 0d) return Collections.emptyList();
        final int[] minValues = new int[weights.size()];
        final int[] boundsarray = new int[weights.size()];
        boolean minAllZero = true;
        double cfrom = from, cto = to;
        Arrays.fill(boundsarray, Integer.MAX_VALUE);
        if (boundaries!=null && !boundaries.isEmpty()) {
            for (int i = 0; i < boundsarray.length; i++) {
                T el = weights.get(i).getOwner();
                Interval range = boundaries.get(el);
                if (range != null) {
                    boundsarray[i] = range.getMax()-range.getMin();
                    minValues[i] = range.getMin();
                    if (minValues[i] > 0) {
                        minAllZero = false;
                        final double reduceWeightBy = weights.get(i).getMass() * range.getMin();
                        cfrom -= reduceWeightBy;
                        cto -= reduceWeightBy;
                    }
                }
            }
        }
        final ArrayList<int[]> results = new ArrayList<int[]>();
        ArrayList<int[]> rawDecompositions = null;
        final Interval interval = integerBound(cfrom, cto);
        if (!minAllZero && interval.getMax() == 0) results.add(minValues);
        for (int m = interval.getMin(); m <= interval.getMax(); ++m) {
            rawDecompositions = integerDecompose(m, boundsarray);
            for (int i=0; i < rawDecompositions.size(); ++i) {
                final int[] decomp = rawDecompositions.get(i);
                if (!minAllZero) {
                    for (int j=0; j < minValues.length; ++j) {
                        decomp[j] += minValues[j];
                    }
                }
                final double realMass = calcMass(decomp);
                if (realMass < from || realMass > to) continue;
                else results.add(decomp);
            }
        }
        return results;
    }


    /**
     * Iterator implementation of the loop
     * We do not use static classes. This gives us the possibility to make some of the variables behave thread safe
     * and resistant against changes from the user.
     * @param <T>
     */
    protected static class DecompIteratorImpl<T> implements DecompIterator<T> {
        // final initialization values
        protected final int[][] ERT;
        protected final int minIntegerMass, maxIntegerMass;
        protected final double minDoubleMass, maxDoubleMass;
        protected final int[] buffer;
        protected final int[] minValues;
        protected final int[] maxValues;
        protected final Alphabet<T> alphabet;
        protected final List<Weight<T>> weights;
        protected final int[] orderedCharacterIds;

        // loop variables
        protected final int k; // current character
        protected final int a; // mass of first character of the alphabet
        protected int i; // current column in the ERT
        protected int m; // current mass
        protected int currentIntegerMass; // current mass to decompose
        protected boolean rewind;



        protected DecompIteratorImpl(int[][] ERT, int minIntegerMass, int maxIntegerMass, double minDoubleMass, double maxDoubleMass, int[] minValues, int[] maxValues, Alphabet<T> alphabet, List<Weight<T>> weights, int[] orderedCharacterIds) {
            this.ERT = ERT;
            this.minIntegerMass = minIntegerMass;
            this.maxIntegerMass = maxIntegerMass;
            this.minDoubleMass = minDoubleMass;
            this.maxDoubleMass = maxDoubleMass;
            this.buffer = new int[weights.size()];
            if (minValues!=null) {
                boolean allZero = true;
                for (int k : minValues) if (k > 0) allZero=false;
                if (!allZero) this.minValues = minValues;
                else this.minValues = null;
            } else this.minValues = null;
            this.maxValues = maxValues;
            this.alphabet = alphabet;
            this.weights = weights;
            this.orderedCharacterIds = orderedCharacterIds;

            this.a = weights.get(0).getIntegerMass();
            this.k = weights.size()-1;
            this.i = k;
            this.currentIntegerMass = this.minIntegerMass;
            this.m = this.minIntegerMass;
            this.rewind = false;
        }

        @Override
        public boolean next() {
            while (currentIntegerMass <= maxIntegerMass) {
                if (decomposeSingleIntegerMass()) {
                    if (checkCompomere()) return true;
                }
                nextIntegerInRange();
            }
            return false;
        }

        private void nextIntegerInRange() {
            ++currentIntegerMass;
            this.i = k;
            this.m = this.currentIntegerMass;
            this.rewind = false;
        }

        private boolean decomposable(int i, int m, int a) {
            return (m>=0) && ERT[(m % a)][i] <= m;
        }

        private boolean decomposeSingleIntegerMass() {
            if (rewind) {
                afterFindingADecomposition();
                rewind = false;
            }
            while (i <= k) {
                if (!decomposable(i,m,a)) { // jump back the search tree as int as there are no branches you can jump into
                    while (i <= k && !decomposable(i, m, a)) {
                        m = m+buffer[i]*weights.get(i).getIntegerMass();
                        buffer[i] = 0;
                        ++i;
                    }
                    // now decomposable(i,m,a) = true
                    while (i<=k && buffer[i]>=maxValues[i]) {  // Jump a step back if you reached the boundary
                        m += buffer[i]*weights.get(i).getIntegerMass();
                        buffer[i] = 0;
                        ++i;
                    }
                    if (i <= k) {  // insert a character
                        m -= weights.get(i).getIntegerMass();
                        ++buffer[i];
                    }
                } else {
                    while (i > 0 && decomposable(i-1, m, a)) { // go as deep as possible into the "search tree"
                        --i; // initially we do not add any elements
                    }
                    // now decomposable[i,m,a]=true
                    if (i==0) { // you are finished: Add the decomposition
                        buffer[0] = (m/a);
                        rewind = true;
                        return true;
                    }
                    while (i<=k && buffer[i]>=maxValues[i]) { // Jump a step back if you reached the boundary
                        m += buffer[i]*weights.get(i).getIntegerMass();
                        buffer[i] = 0;
                        ++i;
                    }
                    if (i <= k) {  // insert a character
                        m -= weights.get(i).getIntegerMass();
                        ++buffer[i];
                    }
                }
            }
            return false;
        }

        private boolean checkCompomere() {
            if (minValues!=null) {
                for (int j=0; j < minValues.length; ++j) {
                    buffer[j] += minValues[j];
                }
            }
            // calculate mass of decomposition
            double exactMass = 0;
            for (int j=0; j < buffer.length; ++j) {
                exactMass += buffer[j]*weights.get(j).getMass();
            }
            if (exactMass >= minDoubleMass && exactMass <= maxDoubleMass) return true;
            else return false;
        }

        private void afterFindingADecomposition() {
            if (minValues!=null) {
                for (int j=0; j < minValues.length; ++j) {
                    buffer[j] -= minValues[j];
                }
            }
            ++i; // and go one step back in the search tree
            while (i<=k && buffer[i]>=maxValues[i]) { // Jump a step back if you reached the boundary
                m += buffer[i]*weights.get(i).getIntegerMass();
                buffer[i] = 0;
                ++i;
            }
            if (i <= k) {  // insert a character
                m -= weights.get(i).getIntegerMass();
                ++buffer[i];
            }
        }

        @Override
        public int[] getCurrentCompomere() {
            return buffer;
        }

        @Override
        public Alphabet<T> getAlphabet() {
            return alphabet;
        }

        @Override
        public int[] getAlphabetOrder() {
            return orderedCharacterIds;
        }

        @Override
        public T getCharacterAt(int index) {
            return alphabet.get(orderedCharacterIds[index]);
        }
    }

    protected ArrayList<int[]> integerDecompose(int mass, int[] bounds){
        // Find compomers
        ArrayList<int[]> result = new ArrayList<int[]>();
        int k = weights.size()-1; // index of last character
        int a = weights.get(0).getIntegerMass(); // mass of first character
        int[] c = new int[k+1]; // compomere
        int i=k; // current column in ERT
        int m = mass; // current mass
        while (i <= k) {
            if (!decomposable(i, m, a)) { // jump back the search tree as int as there are no branches you can jump into
                while (i <= k && !decomposable(i, m, a)) {
                    m = m+c[i]*weights.get(i).getIntegerMass();
                    c[i] = 0;
                    ++i;
                }
                // now decomposable(i,m,a) = true
                while (i<=k && c[i]>=bounds[i]) {  // Jump a step back if you reached the boundary
                    m += c[i]*weights.get(i).getIntegerMass();
                    c[i] = 0;
                    ++i;
                }
                if (i <= k) {  // insert a character
                    m -= weights.get(i).getIntegerMass();
                    ++c[i];
                }
            } else {
                while (i > 0 && decomposable(i-1, m, a)) { // go as deep as possible into the "search tree"
                    --i; // initially we do not add any elements
                }
                // now decomposable[i,m,a]=true
                if (i==0) { // you are finished: Add the decomposition
                    c[0] = (int)(m/a);
                    result.add(c.clone());
                    ++i; // and go one step back in the search tree
                }
                while (i<=k && c[i]>=bounds[i]) { // Jump a step back if you reached the boundary
                    m += c[i]*weights.get(i).getIntegerMass();
                    c[i] = 0;
                    ++i;
                }
                if (i <= k) {  // insert a character
                    m -= weights.get(i).getIntegerMass();
                    ++c[i];
                }
            }
        }
        return result;
    }

    private boolean decomposable(int i, int m, int a1) {
        if (m<0)return false;
        return ERT[(m % a1)][i] <= m;
    }

    /**
     * Initializes the decomposer. Computes the extended residue table. This have to be done only one time for
     * a given alphabet, independently from the masses you want to decompose. This method is called automatically
     * if you compute the decompositions, so call it only if you want to control the time of the initialisation.
     */
    public void init() {
        if (ERT != null) return;
        synchronized (this) {
            if (ERT != null) return;
            discretizeMasses();
            divideByGCD();
            computeLCMs();
            calcERT();
            computeErrors();
        }
    }

    protected double calcMass(int[] input){
        double result = 0d;
        for (int i = 0; i < input.length; ++i){
            result += input[i]*weights.get(i).getMass();
        }
        return result;
    }

    protected void calcERT(){
        int firstLongVal = weights.get(0).getIntegerMass();
        ERT = new int[firstLongVal][weights.size()];
        int d, r, n, argmin;

        //Init
        ERT[0][0] = 0;
        for (int i = 1; i < ERT.length; ++i){
            ERT[i][0] = Integer.MAX_VALUE; // should be infinity
        }

        //Filling the Table, j loops over columns
        for (int j = 1; j < ERT[0].length; ++j){
            ERT[0][j] = 0; // Init again
            d = gcd(firstLongVal, weights.get(j).getIntegerMass());
            for (int p = 0; p < d; p++){ // Need to start d Round Robin loops
                if (p == 0) {
                    n = 0; // 0 is the min in the complete RT or the first p-loop
                } else {
                    n = Integer.MAX_VALUE; // should be infinity
                    argmin = p;
                    for (int i = p; i<ERT.length; i += d){ // Find Minimum in specific part of ERT
                        if (ERT[i][j-1] < n){
                            n = ERT[i][j-1];
                            argmin = i;
                        }
                    }
                    ERT[argmin][j]= n;
                }
                if (n == Integer.MAX_VALUE){ // Minimum of the specific part of ERT was infinity
                    for (int i = p; i<ERT.length; i += d){ // Fill specific part of ERT with infinity
                        ERT[i][j] = Integer.MAX_VALUE;
                    }
                } else { // Do normal loop
                    for (int i = 1; i < ERT.length/d; ++i){ // i is just a counter
                        n += weights.get(j).getIntegerMass();
                        r = n % firstLongVal;
                        if (ERT[r][j-1] < n) n = ERT[r][j-1]; // get the min
                        ERT[r][j] = n;
                    }
                }
            } // end for p
        } // end for j
    }

    protected void discretizeMasses() {
        // compute integer masses
        for (int i=0; i  < weights.size(); ++i) {
            final Weight<T> weight = weights.get(i);
            weight.setIntegerMass((int)(weight.getMass() / precision));
        }
    }

    protected void divideByGCD() {
        if (weights.size() > 0) {
            int d = gcd(weights.get(0).getIntegerMass(), weights.get(1).getIntegerMass());
            for (int i=2; i < weights.size(); ++i) {
                d = gcd(d, weights.get(i).getIntegerMass());
                if (d == 1) return;
            }
            precision *= d;
            for (Weight<T> weight : weights) {
                weight.setIntegerMass(weight.getIntegerMass() / d);
            }
        }
    }

    protected void computeLCMs() {
        final Weight<T> first = weights.get(0);
        first.setL(1);
        first.setLcm(first.getIntegerMass());

        for(int i=1; i<weights.size();i++){
            final Weight<T> weight = weights.get(i);
            int temp = first.getIntegerMass() / gcd(first.getIntegerMass(), weight.getIntegerMass());
            weight.setL(temp);
            weight.setLcm(temp * weight.getIntegerMass());
        }
    }

    protected static int gcd(int u, int v) {
        int r = 0;

        while (v != 0) {
            r = u % v;
            u = v;
            v = r;
        }
        return u;
    }

    protected void computeErrors() {
        this.minError = 0d;
        this.maxError = 0d;
        for (Weight<T> weight : weights) {
            final double error = (precision * weight.getIntegerMass() - weight.getMass()) / weight.getMass();
            minError = Math.min(minError, error);
            maxError = Math.max(maxError, error);
        }
    }

    protected Interval integerBound(double from, double to) {
        final double fromD = Math.ceil((1 + minError) * from / precision);
        final double toD = Math.floor((1 + maxError) * to / precision);
        if (fromD > Integer.MAX_VALUE || toD > Integer.MAX_VALUE) {
            throw new ArithmeticException("Given mass is too large to decompose. Please use a smaller precision value, i.e. mass/precision have to be within 32 bit integer space");
        }
        return new Interval(
                Math.max(0, (int)fromD),
                Math.max(0, (int)toD)
        );
    }

}
