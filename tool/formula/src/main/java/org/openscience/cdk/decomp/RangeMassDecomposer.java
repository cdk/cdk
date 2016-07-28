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
 * Decomposes a given mass over an alphabet, returning all decompositions which masses equals the given mass
 * considering a given deviation.
 * In contrast to {@link MassDecomposer}, MassDecomposerFast calculates the decompositions with the help of an ERT containing deviation information, not requiring to iterate over all different integer mass values {@cdk.cite Duehrkop2013}.
 * @param <T>
 *     type of characters of the alphabetr
 * @author Marcus Ludwig, Kai Dührkop
 *
 */
public class RangeMassDecomposer<T> extends MassDecomposer<T>{

    /**
     * Avoid locks by making ERTs volatile. This leads to the situation that several threads might accidentally compute
     * the same ERT tables. However, as soon as an ERT table is written it is synchronized around all threads. After
     * writing an ERT table it is never changed, so additional locking is not necessary.
     */
    protected volatile int[][][] ERTs;

    /**
     * @param alphabet the alphabet the mass is decomposed over
     */
    public RangeMassDecomposer(Alphabet<T> alphabet) {
        super(alphabet);
        this.ERTs = new int[0][][];
    }

    /**
     * Check if a mass is decomposable. This is done in constant time (especially: it is very very very fast!).
     * But it doesn't check if there is a valid decomposition. Therefore, even if the method returns true,
     * all decompositions may be invalid for the given validator or given bounds.
     * #decompose(mass) uses this function before starting the decomposition, therefore this method should only
     * be used if you don't want to start the decomposition algorithm.
     * @return
     */
    @Override
    public boolean maybeDecomposable(double from, double to) {
        init();
        final int[][][] ERTs = this.ERTs;
        //normal version seems to be faster, because it returns after first hit
        final Interval range = integerBound(from, to);
        final int a = weights.get(0).getIntegerMass();
        for (int i = range.getMin(); i <= range.getMax(); ++i) {
            final int r = i % a;
            if (i >= ERTs[0][r][weights.size()-1]) return true;
        }
        return false;
    }

    @Override
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
        final int deviation = interval.getMax()-interval.getMin();
        final int[][][] _ERTs_ = this.ERTs;
        //calculate the required ERTs
        if ((1<<(ERTs.length-1)) <=deviation){
            calcERT(deviation);
        }
        final int[][][] ERTs = this.ERTs;

        //take ERT with required deviation
        int[][] currentERT;
        if (deviation==0) currentERT = ERTs[0];
        else currentERT = ERTs[32-Integer.numberOfLeadingZeros(deviation)];

        return new DecompIteratorImpl<>(currentERT, interval.getMin(), interval.getMax(), from, to, minValues, boundsarray, alphabet, weights, orderedCharacterIds.clone());
    }

    /**
     * computes all decompositions for the given mass. The runtime depends only on the number of characters and the
     * number of decompositions. Therefore this method is very fast as long as the number of decompositions is low.
     * Unfortunately, the number of decompositions increases nearly exponential in the number of characters and in the
     * input mass.
     *
     * This function can be called in multiple threads in parallel, because it does not modify the decomposer
     */
    @Override
    public List<int[]> decompose(double from, double to, Map<T, Interval> boundaries){
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

        if (interval.getMax()<interval.getMin()) rawDecompositions = new ArrayList<int[]>();
        else rawDecompositions = integerDecompose(interval.getMax(), interval.getMax() - interval.getMin(), boundsarray);
        for (int i=0; i < rawDecompositions.size(); ++i) {
            final int[] decomp = rawDecompositions.get(i);
            if (!minAllZero) {
                for (int j=0; j < minValues.length; ++j) {
                    decomp[j] += minValues[j];
                }
            }
            final double realMass = calcMass(decomp);
            if (realMass >= from && realMass <= to) results.add(decomp);
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

        protected final int[] j, m, lbound, r;
        protected final int k, a, deviation, ERTdev;
        protected boolean flagWhile, rewind;
        protected int i;



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

            k = weights.size();
            j = new int[k];
            m = new int[k];
            lbound = new int[k];
            r = new int[k];
            flagWhile = false; // flag wether we are in the while-loop or not
            a = weights.get(0).getIntegerMass();
            // Init
            for (int i=1; i<k; ++i){
                lbound[i] = Integer.MAX_VALUE; // this is just to ensure, that lbound < m in the first iteration
            }

            i = k-1;
            m[i] = maxIntegerMass; // m[i] corresponds to M, m[i-1] ^= m
            this.rewind=false;
            this.deviation = maxIntegerMass-minIntegerMass;
            this.ERTdev = Integer.highestOneBit(deviation);

        }

        @Override
        public boolean next() {
            while (decomposeRangeIntegerMass()) {
                if (checkCompomere()) return true;
            }
            return false;
        }

        private boolean decomposable(int i, int m, int a) {
            return (m>=0) && ERT[(m % a)][i] <= m;
        }

        private boolean decomposeRangeIntegerMass() {
            if (rewind) {
                afterFindingADecomposition();
                rewind=false;
            }
            while (i != k){
                if (i == 0){
                    final int v = (m[i]/a);
                    if (v <= maxValues[0]) {
                        buffer[0] = v;
                        rewind = true;
                        return true;
                    }
                    ++i; // "return" from recursion
                    flagWhile = true; // in this recursion-depth we are in the while-loop, cause the next recursion (the one we just exited) was called
                    m[i-1] -= weights.get(i).getLcm(); // execute the rest of the while
                    buffer[i] += weights.get(i).getL();
                } else {
                    if (flagWhile){
                        if (m[i-1] >= lbound[i] && buffer[i] <= maxValues[i]){ //currently in while loop
                            --i; // "do" recursive call
                        } else {
                            flagWhile = false; //
                        }
                    } else { //we are in the for-loop
                        if (j[i] < weights.get(i).getL() && m[i]-j[i]*weights.get(i).getIntegerMass()>=0){
                            buffer[i] = j[i];
                            m[i-1] = m[i]-j[i]*weights.get(i).getIntegerMass();
                            r[i] = m[i-1]%a;
                            //changed from normal algorithm: you have to look up the minimum at 2 position
                            int pos = r[i]-deviation+ERTdev;
                            if (pos<0) pos += ERT.length;
                            lbound[i] = Math.min(ERT[r[i]][i-1], ERT[pos][i-1]);
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
                                m[i-1] -= weights.get(i).getLcm(); // execute the rest of the while
                                buffer[i] += weights.get(i).getL();
                            }
                        }
                    }
                } // end if i == 0
            } // end while

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

            ++i; // "return" from recursion
            flagWhile = true; // in this recursion-depth we are in the while-loop, cause the next recursion (the one we just exited) was called
            m[i-1] -= weights.get(i).getLcm(); // execute the rest of the while
            buffer[i] += weights.get(i).getL();
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

    /**
     * decomposes an interval of masses with mass as UPPER mass and all other masses below within deviation
     * Example: mass = 18, deviation 3 {@literal ->} decompose 18,17,16,15
     * @param mass
     * @param deviation
     * @param bounds
     * @return
     */
    protected ArrayList<int[]> integerDecompose(int mass, int deviation, int[] bounds){
        assert (deviation<weights.get(0).getIntegerMass()); //todo throw Exception or not that problematic?
        final int[][][] _ERTs_ = this.ERTs;
        //calculate the required ERTs
        if ((1<<(ERTs.length-1)) <=deviation){
            calcERT(deviation);
        }
        final int[][][] ERTs = this.ERTs;

        {
            int largest = 0;
            for (int[][] t : ERTs) {
                for (int[] r : t) {
                    for (int v : r) {
                        if (v < Integer.MAX_VALUE) {
                            largest = Math.max(largest, v);
                        }
                    }
                }
            }
        }

        //take ERT with required deviation
        int[][] currentERT;
        if (deviation==0) currentERT = ERTs[0];
        else currentERT = ERTs[32-Integer.numberOfLeadingZeros(deviation)];
        int ERTdev = Integer.highestOneBit(deviation);
        ArrayList<int[]> result = new ArrayList<int[]>();

        int k = weights.size();
        int[] c = new int[k], deepCopy;
        int[] j = new int[k], m = new int[k], lbound = new int[k], r = new int[k];
        boolean flagWhile = false; // flag wether we are in the while-loop or not
        final int a = weights.get(0).getIntegerMass();
        // Init
        for (int i=1; i<k; ++i){
            lbound[i] = Integer.MAX_VALUE; // this is just to ensure, that lbound < m in the first iteration
        }

        int i = k-1;
        m[i] = mass; // m[i] corresponds to M, m[i-1] ^= m
        while (i != k){
            if (i == 0){
                deepCopy = c.clone();
                deepCopy[0] = (m[i]/a);
                if (deepCopy[0] <= bounds[0]) result.add(deepCopy);
                ++i; // "return" from recursion
                flagWhile = true; // in this recursion-depth we are in the while-loop, cause the next recursion (the one we just exited) was called
                m[i-1] -= weights.get(i).getLcm(); // execute the rest of the while
                c[i] += weights.get(i).getL();
            } else {
                if (flagWhile){
                    if (m[i-1] >= lbound[i] && c[i] <= bounds[i]){ //currently in while loop
                        --i; // "do" recursive call
                    } else {
                        flagWhile = false; //
                    }
                } else { //we are in the for-loop
                    if (j[i] < weights.get(i).getL() && m[i]-j[i]*weights.get(i).getIntegerMass()>=0){
                        c[i] = j[i];
                        m[i-1] = m[i]-j[i]*weights.get(i).getIntegerMass();
                        r[i] = m[i-1]%a;
                        //changed from normal algorithm: you have to look up the minimum at 2 position
                        int pos = r[i]-deviation+ERTdev;
                        if (pos<0) pos += currentERT.length;
                        lbound[i] = Math.min(currentERT[r[i]][i-1], currentERT[pos][i-1]);
                        flagWhile = true; // call the while loop
                        ++j[i];
                    } else { //exit for loop
                        // reset "function variables"
                        lbound[i] = Integer.MAX_VALUE;
                        j[i] = 0;
                        c[i] = 0;
                        ++i; // "return" from recursion
                        if (i != k) { // only if we are not done
                            flagWhile = true; // in this recursion-depth we are in the while-loop, cause the next recursion was called
                            m[i-1] -= weights.get(i).getLcm(); // execute the rest of the while
                            c[i] += weights.get(i).getL();
                        }
                    }
                }
            } // end if i == 0
        } // end while

        return result;
    } // end function

    /**
     * calculates ERTs to look up whether a mass or lower masses within a certain deviation are decomposable.
     * only ERTs for deviation 2^x are calculated
     * @param deviation
     */
    protected void calcERT(int deviation){
        final int[][][] ERTs = this.ERTs;
        final int currentLength = ERTs.length;

        // we have to extend the ERT table

        int[][] lastERT = ERTs[ERTs.length-1];
        int[][] nextERT = new int[lastERT.length][weights.size()];
        if (currentLength==1){
            //first line compares biggest residue and 0
            for (int j = 0; j < weights.size(); j++) {
                nextERT[0][j] = Math.min(lastERT[nextERT.length-1][j], lastERT[0][j]);
            }
            for (int i = 1; i < nextERT.length; i++) {
                for (int j = 0; j < weights.size(); j++) {
                    nextERT[i][j] = Math.min(lastERT[i][j], lastERT[i-1][j]);
                }
            }
        } else {
            int step = (1<<(currentLength-2));
            for (int i = step; i < nextERT.length; i++) {
                for (int j = 0; j < weights.size(); j++) {
                    nextERT[i][j] = Math.min(lastERT[i][j], lastERT[i-step][j]);
                }
            }
            //first lines compared with last lines (greatest residues) because of modulo's cyclic characteristic
            for (int i = 0; i < step; i++) {
                for (int j = 0; j < weights.size(); j++) {
                    nextERT[i][j] = Math.min(lastERT[i][j], lastERT[i+nextERT.length-step][j]);
                }
            }
        }

        // now store newly calculated ERT
        synchronized(this) {
            final int[][][] tables = this.ERTs;
            if (tables.length == currentLength) {
                this.ERTs = Arrays.copyOf(this.ERTs, this.ERTs.length+1);
                this.ERTs[this.ERTs.length-1] = nextERT;
            } else {
                // another background thread did already compute the ERT. So we don't have to do this again
            }
        }
        // recursively calculate ERTs for higher deviations
        // current ERT is already sufficient
        if ((1<<(currentLength-1)) <= deviation) calcERT(deviation);
    }

    @Override
    protected void calcERT(){
        int firstLongVal = weights.get(0).getIntegerMass();
        int[][] ERT = new int[firstLongVal][weights.size()];
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
                    for (long i = 1; i < ERT.length/d; ++i){ // i is just a counter
                        n += weights.get(j).getIntegerMass();
                        if (n < 0) {
                            throw new ArithmeticException("Integer overflow occurs. DECOMP cannot calculate decompositions for the given alphabet as it exceeds the 32 bit integer space. Please use a smaller precision value.");
                        }
                        r = n % firstLongVal;
                        if (ERT[r][j-1] < n) n = ERT[r][j-1]; // get the min
                        ERT[r][j] = n;
                    }
                }
            } // end for p
        } // end for j
        synchronized (this) {
            if (this.ERTs.length == 0) {
                this.ERT = ERT;
                this.ERTs = new int[][][]{ERT};
            }
        }
    }
}
