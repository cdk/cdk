/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.math;


import java.util.Random;

/**
 * Class supplying useful methods to generate random numbers.
 * This class isn't supposed to be instantiated. You should use it by calling
 * its static methods.
 *
 * @cdk.module standard
 * @cdk.githash
 */
public class RandomNumbersTool extends Random {

    private static final long serialVersionUID = -8238833473383641882L;

    private static Random     random;
    private static long       randomSeed;

    static {
        randomSeed = System.currentTimeMillis();
        random = new java.util.Random(randomSeed);
    }

    /**
     * Sets the base generator to be used by this class.
     * 
     *
     * @param base_random a <code>java.util.Random</code> subclass.
     */
    public static void setRandom(Random base_random) {
        random = base_random;
    }

    /**
     * Sets the seed of this random number generator using a single
     * <code>long</code> seed.
     *
     * @param new_seed the seed to be used by the random number generator.
     */
    public static void setRandomSeed(long new_seed) {
        randomSeed = new_seed;
        random.setSeed(randomSeed);
    }

    /**
     * Returns the seed being used by this random number generator.
     * 
     *
     * @return the <code>long</code> seed.
     */
    public static long getRandomSeed() {
        return randomSeed;
    }

    /**
     * Returns the instance of Random used by this class.
     *
     * @return An object of Random
     */
    public static Random getRandom() {
        return random;
    }

    /**
     * Generates a random integer between <code>0</code> and <code>1</code>.
     * 
     *
     * @return a random integer between <code>0</code> and <code>1</code>.
     */
    public static int randomInt() {
        return randomInt(0, 1);
    }

    /**
     * Generates a random integer between the specified values.
     * 
     *
     * @param lo the lower bound for the generated integer.
     * @param hi the upper bound for the generated integer.
     * @return a random integer between <code>lo</code> and <code>hi</code>.
     */
    public static int randomInt(int lo, int hi) {
        return (Math.abs(random.nextInt()) % (hi - lo + 1)) + lo;
    }

    /**
     * Generates a random long between <code>0</code> and <code>1</code>.
     * 
     *
     * @return a random long between <code>0</code> and <code>1</code>.
     */
    public static long randomLong() {
        return randomLong(0, 1);
    }

    /**
     * Generates a random long between the specified values.
     * 
     *
     * @param lo the lower bound for the generated long.
     * @param hi the upper bound for the generated long.
     * @return a random long between <code>lo</code> and <code>hi</code>.
     */
    public static long randomLong(long lo, long hi) {
        return nextLong(random, hi - lo + 1L) + lo;
    }

    /**
     * Access the next long random number between 0 and n.
     *
     * @param rng random number generator
     * @param n   max value
     * @return a long random number between 0 and n
     * @see <a href="http://stackoverflow.com/questions/2546078/java-random-long-number-in-0-x-n-range">Random Long Number in range, Stack Overflow</a>
     */
    private static long nextLong(Random rng, long n) {
        if (n <= 0) throw new IllegalArgumentException("n must be greater than 0");
        long bits, val;
        do {
            bits = (rng.nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits - val + (n - 1) < 0L);
        return val;
    }

    /**
     * Generates a random float between <code>0</code> and <code>1</code>.
     * 
     *
     * @return a random float between <code>0</code> and <code>1</code>.
     */
    public static float randomFloat() {
        return random.nextFloat();
    }

    /**
     * Generates a random float between the specified values.
     * 
     *
     * @param lo the lower bound for the generated float.
     * @param hi the upper bound for the generated float.
     * @return a random float between <code>lo</code> and <code>hi</code>.
     */
    public static float randomFloat(float lo, float hi) {
        return (hi - lo) * random.nextFloat() + lo;
    }

    /**
     * Generates a random double between <code>0</code> and <code>1</code>.
     * 
     *
     * @return a random double between <code>0</code> and <code>1</code>.
     */
    public static double randomDouble() {
        return random.nextDouble();
    }

    /**
     * Generates a random double between the specified values.
     * 
     *
     * @param lo the lower bound for the generated double.
     * @param hi the upper bound for the generated double.
     * @return a random double between <code>lo</code> and <code>hi</code>.
     */
    public static double randomDouble(double lo, double hi) {
        return (hi - lo) * random.nextDouble() + lo;
    }

    /**
     * Generates a random boolean.
     * 
     *
     * @return a random boolean.
     */
    public static boolean randomBoolean() {
        return (randomInt() == 1);
    }

    /**
     * Generates a random bit: either <code>0</code> or <code>1</code>.
     * 
     *
     * @return a random bit.
     */
    public static int randomBit() {
        return randomInt();
    }

    /**
     * Returns a boolean value based on a biased coin toss.
     * 
     *
     * @param p the probability of success.
     * @return <code>true</code> if a success was found; <code>false</code>
     *         otherwise.
     */
    public static boolean flipCoin(double p) {
        return (randomDouble() < p ? true : false);
    }

    /**
     * Generates a random float from a Gaussian distribution with the specified
     * deviation.
     * 
     *
     * @param dev the desired deviation.
     * @return a random float from a Gaussian distribution with deviation
     *         <code>dev</code>.
     */
    public static float gaussianFloat(float dev) {
        return (float) random.nextGaussian() * dev;
    }

    /**
     * Generates a random double from a Gaussian distribution with the specified
     * deviation.
     * 
     *
     * @param dev the desired deviation.
     * @return a random double from a Gaussian distribution with deviation
     *         <code>dev</code>.
     */
    public static double gaussianDouble(double dev) {
        return random.nextGaussian() * dev;
    }

    /**
     * Generates a random double from an Exponential distribution with the specified
     * mean value.
     * 
     *
     * @param mean the desired mean value.
     * @return a random double from an Exponential distribution with mean value
     *         <code>mean</code>.
     */
    public static double exponentialDouble(double mean) {
        return -mean * Math.log(randomDouble());
    }
}
