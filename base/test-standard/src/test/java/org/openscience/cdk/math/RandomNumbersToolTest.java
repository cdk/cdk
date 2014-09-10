/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.math.RandomNumbersTool;
import org.openscience.cdk.CDKTestCase;

import java.util.Random;

/**
 * @cdk.module test-standard
 */
public class RandomNumbersToolTest extends CDKTestCase {

    public RandomNumbersToolTest() {
        super();
    }

    @Test
    public void testGetRandomSeed() {
        testSetRandomSeed_long();
    }

    @Test
    public void testSetRandomSeed_long() {
        long seed = System.currentTimeMillis();
        RandomNumbersTool.setRandomSeed(seed);
        Assert.assertEquals(seed, RandomNumbersTool.getRandomSeed());
    }

    @Test
    public void testSetRandom() {
        Random rng = new Random();
        RandomNumbersTool.setRandom(rng);
        Assert.assertEquals(rng, RandomNumbersTool.getRandom());
    }

    @Test
    public void testRandomInt() {
        int random = RandomNumbersTool.randomInt();
        Assert.assertTrue(random == 0 || random == 1);
    }

    @Test
    public void testRandomBoolean() {
        boolean random = RandomNumbersTool.randomBoolean();
        Assert.assertTrue(random || !random);
    }

    @Test
    public void testRandomLong() {
        long random = RandomNumbersTool.randomLong();
        Assert.assertTrue(random >= 0l);
        Assert.assertTrue(random <= 1l);
    }

    @Test
    public void testRandomLong_long_long() {
        long lower_limit = 2l;
        long upper_limit = 4l;
        long random = RandomNumbersTool.randomLong(lower_limit, upper_limit);
        Assert.assertTrue(random >= lower_limit);
        Assert.assertTrue(random <= upper_limit);
    }

    @Test
    public void testRandomDouble() {
        double random = RandomNumbersTool.randomDouble();
        Assert.assertTrue(random >= 0.0);
        Assert.assertTrue(random <= 1.0);
    }

    @Test
    public void testRandomDouble_double_double() {
        double lower_limit = 2.0;
        double upper_limit = 4.0;
        double random = RandomNumbersTool.randomDouble(lower_limit, upper_limit);
        Assert.assertTrue(random >= lower_limit);
        Assert.assertTrue(random <= upper_limit);
    }

    @Test
    public void testRandomFloat() {
        float random = RandomNumbersTool.randomFloat();
        Assert.assertTrue(random >= 0.0);
        Assert.assertTrue(random <= 1.0);
    }

    @Test
    public void testRandomFloat_float_float() {
        float lower_limit = (float) 2.0;
        float upper_limit = (float) 4.0;
        float random = RandomNumbersTool.randomFloat(lower_limit, upper_limit);
        Assert.assertTrue(random >= lower_limit);
        Assert.assertTrue(random <= upper_limit);
    }

    @Test
    public void testRandomBit() {
        int random = RandomNumbersTool.randomBit();
        Assert.assertTrue(random == 0 || random == 1);
    }

    @Test
    public void testRandomInt_int_int() {
        int random = RandomNumbersTool.randomInt(0, 5);
        Assert.assertTrue(random == 0 || random == 1 || random == 2 || random == 3 || random == 4 || random == 5);
    }

    @Category(SlowTest.class)
    @Test
    public void testFlipCoin() {
        int ntry = 1000000;
        double p = 0.5;
        int ntrue = 0;
        int nfalse = 0;
        for (int i = 0; i < ntry; i++) {
            if (RandomNumbersTool.flipCoin(p))
                ntrue += 1;
            else
                nfalse += 1;
        }
        Assert.assertEquals(0.5, (double) ntrue / ntry, 0.001);
        Assert.assertEquals(0.5, (double) nfalse / ntry, 0.001);
    }

    @Test
    public void testGaussianFloat() {
        float dev = (float) 1.0;
        float epsilon = 0.01f;

        int ntry = 100000;
        float[] values = new float[ntry];
        for (int i = 0; i < ntry; i++)
            values[i] = RandomNumbersTool.gaussianFloat(dev);

        // no get the sd of the values
        float mean = 0.0f;
        for (int i = 0; i < ntry; i++)
            mean += values[i];
        mean = mean / ntry;

        float sd = 0.0f;
        for (int i = 0; i < ntry; i++)
            sd += (values[i] - mean) * (values[i] - mean);
        sd = (float) Math.sqrt(sd / (ntry - 1));
        Assert.assertTrue("Estimated SD does not match to 2 decimal places", sd >= (dev - epsilon)
                && sd <= (dev + epsilon));
    }

    @Test
    public void testGaussianDouble() {
        double dev = 2.0;
        double epsilon = 0.01;
        int ntry = 100000;
        double[] values = new double[ntry];
        for (int i = 0; i < ntry; i++)
            values[i] = RandomNumbersTool.gaussianDouble(dev);

        // no get the sd of the values
        double mean = 0.0f;
        for (int i = 0; i < ntry; i++)
            mean += values[i];
        mean = mean / ntry;

        double sd = 0.0f;
        for (int i = 0; i < ntry; i++)
            sd += (values[i] - mean) * (values[i] - mean);
        sd = Math.sqrt(sd / (ntry - 1));
        Assert.assertTrue("Estimated SD does not match to 2 decimal places", sd >= (dev - epsilon)
                && sd <= (dev + epsilon));
    }

    @Test
    public void testExponentialDouble() {
        double mean = 1.0f;
        double epsilon = 0.01f;
        int ntry = 100000;
        double[] values = new double[ntry];

        for (int i = 0; i < ntry; i++)
            values[i] = RandomNumbersTool.exponentialDouble(mean);

        // no get the mean of the values
        double m = 0.0f;
        for (int i = 0; i < ntry; i++)
            m += values[i];
        m = m / ntry;

        Assert.assertTrue("Estimated mean does not match to 2 decimal places " + m, m >= (mean - epsilon)
                && m <= (mean + epsilon));
    }
}
