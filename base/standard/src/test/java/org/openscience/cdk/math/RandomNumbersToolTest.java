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

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 */
class RandomNumbersToolTest {

    RandomNumbersToolTest() {
        super();
    }

    @Test
    void testGetRandomSeed() {
        testSetRandomSeed_long();
    }

    @Test
    void testSetRandomSeed_long() {
        long seed = System.currentTimeMillis();
        RandomNumbersTool.setRandomSeed(seed);
        Assertions.assertEquals(seed, RandomNumbersTool.getRandomSeed());
    }

    @Test
    void testSetRandom() {
        Random rng = new Random();
        RandomNumbersTool.setRandom(rng);
        Assertions.assertEquals(rng, RandomNumbersTool.getRandom());
    }

    @Test
    void testRandomInt() {
        int random = RandomNumbersTool.randomInt();
        Assertions.assertTrue(random == 0 || random == 1);
    }

    @Test
    void testRandomBoolean() {
        boolean random = RandomNumbersTool.randomBoolean();
        Assertions.assertTrue(random || !random);
    }

    @Test
    void testRandomLong() {
        long random = RandomNumbersTool.randomLong();
        Assertions.assertTrue(random >= 0l);
        Assertions.assertTrue(random <= 1l);
    }

    @Test
    void testRandomLong_long_long() {
        long lower_limit = 2l;
        long upper_limit = 4l;
        long random = RandomNumbersTool.randomLong(lower_limit, upper_limit);
        Assertions.assertTrue(random >= lower_limit);
        Assertions.assertTrue(random <= upper_limit);
    }

    @Test
    void testRandomDouble() {
        double random = RandomNumbersTool.randomDouble();
        Assertions.assertTrue(random >= 0.0);
        Assertions.assertTrue(random <= 1.0);
    }

    @Test
    void testRandomDouble_double_double() {
        double lower_limit = 2.0;
        double upper_limit = 4.0;
        double random = RandomNumbersTool.randomDouble(lower_limit, upper_limit);
        Assertions.assertTrue(random >= lower_limit);
        Assertions.assertTrue(random <= upper_limit);
    }

    @Test
    void testRandomFloat() {
        float random = RandomNumbersTool.randomFloat();
        Assertions.assertTrue(random >= 0.0);
        Assertions.assertTrue(random <= 1.0);
    }

    @Test
    void testRandomFloat_float_float() {
        float lower_limit = (float) 2.0;
        float upper_limit = (float) 4.0;
        float random = RandomNumbersTool.randomFloat(lower_limit, upper_limit);
        Assertions.assertTrue(random >= lower_limit);
        Assertions.assertTrue(random <= upper_limit);
    }

    @Test
    void testRandomBit() {
        int random = RandomNumbersTool.randomBit();
        Assertions.assertTrue(random == 0 || random == 1);
    }

    @Test
    void testRandomInt_int_int() {
        int random = RandomNumbersTool.randomInt(0, 5);
        Assertions.assertTrue(random == 0 || random == 1 || random == 2 || random == 3 || random == 4 || random == 5);
    }

    @Disabled("Test based on random probability - random failures")
    void testFlipCoin() {
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
        Assertions.assertEquals(0.5, (double) ntrue / ntry, 0.001);
        Assertions.assertEquals(0.5, (double) nfalse / ntry, 0.001);
    }

    @Disabled("Test based on random probability - random failures")
    void testGaussianFloat() {
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
        Assertions.assertTrue(sd >= (dev - epsilon)
                && sd <= (dev + epsilon), "Estimated SD does not match to 2 decimal places");
    }

    @Disabled("Test based on random probability - random failures")
    void testGaussianDouble() {
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
        Assertions.assertTrue(sd >= (dev - epsilon)
                && sd <= (dev + epsilon), "Estimated SD does not match to 2 decimal places");
    }

    @Disabled("Test based on random probability - random failures")
    void testExponentialDouble() {
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

        Assertions.assertTrue(m >= (mean - epsilon)
                && m <= (mean + epsilon), "Estimated mean does not match to 2 decimal places " + m);
    }
}
