/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.hash.stereo.StereoEncoderFactory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.openscience.cdk.hash.BasicAtomEncoder.ATOMIC_NUMBER;
import static org.openscience.cdk.hash.BasicAtomEncoder.FORMAL_CHARGE;
import static org.openscience.cdk.hash.BasicAtomEncoder.FREE_RADICALS;
import static org.openscience.cdk.hash.BasicAtomEncoder.MASS_NUMBER;
import static org.openscience.cdk.hash.BasicAtomEncoder.ORBITAL_HYBRIDIZATION;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class HashGeneratorMakerTest {

    @Test
    public void testElemental() {
        AtomHashGenerator generator = new HashGeneratorMaker().depth(0).elemental().atomic();
        List<AtomEncoder> encoders = getEncoders((BasicAtomHashGenerator) generator);
        org.hamcrest.MatcherAssert.assertThat(encoders.size(), is(1));
        org.hamcrest.MatcherAssert.assertThat(encoders.get(0), is((AtomEncoder) ATOMIC_NUMBER));
    }

    @Test
    public void testIsotopic() {
        AtomHashGenerator generator = new HashGeneratorMaker().depth(0).isotopic().atomic();
        List<AtomEncoder> encoders = getEncoders((BasicAtomHashGenerator) generator);
        org.hamcrest.MatcherAssert.assertThat(encoders.size(), is(1));
        org.hamcrest.MatcherAssert.assertThat(encoders.get(0), is((AtomEncoder) MASS_NUMBER));
    }

    @Test
    public void testCharged() {
        AtomHashGenerator generator = new HashGeneratorMaker().depth(0).charged().atomic();
        List<AtomEncoder> encoders = getEncoders((BasicAtomHashGenerator) generator);
        org.hamcrest.MatcherAssert.assertThat(encoders.size(), is(1));
        org.hamcrest.MatcherAssert.assertThat(encoders.get(0), is((AtomEncoder) FORMAL_CHARGE));
    }

    @Test
    public void testRadical() {
        AtomHashGenerator generator = new HashGeneratorMaker().depth(0).radical().atomic();
        List<AtomEncoder> encoders = getEncoders((BasicAtomHashGenerator) generator);
        org.hamcrest.MatcherAssert.assertThat(encoders.size(), is(1));
        org.hamcrest.MatcherAssert.assertThat(encoders.get(0), is((AtomEncoder) FREE_RADICALS));
    }

    @Test
    public void testOrbital() {
        AtomHashGenerator generator = new HashGeneratorMaker().depth(0).orbital().atomic();
        List<AtomEncoder> encoders = getEncoders((BasicAtomHashGenerator) generator);
        org.hamcrest.MatcherAssert.assertThat(encoders.size(), is(1));
        org.hamcrest.MatcherAssert.assertThat(encoders.get(0), is((AtomEncoder) ORBITAL_HYBRIDIZATION));
    }

    @Test
    public void testChiral() {
        AtomHashGenerator generator = new HashGeneratorMaker().depth(0).elemental().chiral().atomic();
        assertThat(encoder(generator), is(not(StereoEncoderFactory.EMPTY)));
    }

    @Test
    public void testPerturbed() {
        AtomHashGenerator g1 = new HashGeneratorMaker().depth(0).elemental().perturbed().atomic();

        assertTrue(g1 instanceof PerturbedAtomHashGenerator);
    }

    @Test
    public void testPerturbedWith() throws NoSuchFieldException, IllegalAccessException {
        EquivalentSetFinder mock = mock(EquivalentSetFinder.class);
        AtomHashGenerator g1 = new HashGeneratorMaker().depth(0).elemental().perturbWith(mock).atomic();

        assertTrue(g1 instanceof PerturbedAtomHashGenerator);
        Field field = g1.getClass().getDeclaredField("finder");
        field.setAccessible(true);
        assertThat((EquivalentSetFinder) field.get(g1), is(sameInstance(mock)));
    }

    @Test
    public void testOrdering() {
        AtomHashGenerator g1 = new HashGeneratorMaker().depth(0).elemental().isotopic().charged().atomic();
        AtomHashGenerator g2 = new HashGeneratorMaker().depth(0).isotopic().charged().elemental().atomic();
        assertThat(getEncoders(g1).size(), is(3));
        assertThat(getEncoders(g1), is(getEncoders(g2)));
    }

    @Test(expected = NullPointerException.class)
    public void testEncode_Null() {
        new HashGeneratorMaker().encode(null);
    }

    @Test
    public void testEncode() {
        AtomEncoder e1 = mock(AtomEncoder.class);
        AtomEncoder e2 = mock(AtomEncoder.class);
        AtomHashGenerator generator = new HashGeneratorMaker().depth(0).encode(e1).encode(e2).atomic();
        List<AtomEncoder> encoders = getEncoders((BasicAtomHashGenerator) generator);
        assertThat(encoders.size(), is(2));
        assertThat(encoders.get(0), is(e1));
        assertThat(encoders.get(1), is(e2));

        generator = new HashGeneratorMaker().depth(0).encode(e2).encode(e1).atomic();
        encoders = getEncoders((BasicAtomHashGenerator) generator);
        assertThat(encoders.size(), is(2));
        assertThat(encoders.get(0), is(e2));
        assertThat(encoders.get(1), is(e1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoDepth() {
        new HashGeneratorMaker().atomic();
    }

    @Test
    public void testAtomic() {
        assertNotNull(new HashGeneratorMaker().depth(0).elemental().atomic());
    }

    @Test
    public void testMolecular() {
        assertNotNull(new HashGeneratorMaker().depth(0).elemental().molecular());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testEnsemble() {
        new HashGeneratorMaker().depth(0).elemental().ensemble();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingEncoders() {
        new HashGeneratorMaker().depth(0).atomic();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDepth() {
        new HashGeneratorMaker().depth(-1);
    }

    @Test
    public void suppressHydrogens() {
        AtomHashGenerator generator = new HashGeneratorMaker().elemental().depth(0).suppressHydrogens().atomic();
        assertThat(generator, is(instanceOf(SuppressedAtomHashGenerator.class)));
    }

    @Test
    public void testDepth() throws NoSuchFieldException, IllegalAccessException {
        AtomHashGenerator generator = new HashGeneratorMaker().depth(5).elemental().atomic();
        Field depthField = generator.getClass().getDeclaredField("depth");
        depthField.setAccessible(true);
        int value = depthField.getInt(generator);
        assertThat(value, is(5));
    }

    public static StereoEncoderFactory encoder(AtomHashGenerator generator) {
        if (generator instanceof BasicAtomHashGenerator) {
            try {
                Field f = generator.getClass().getDeclaredField("factory");
                f.setAccessible(true);
                return (StereoEncoderFactory) f.get(generator);
            } catch (NoSuchFieldException e) {
                System.err.println(e.getMessage());
            } catch (IllegalAccessException e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }

    /**
     * Extract the AtomEncoders using reflection
     *
     * @param generator
     * @return
     */
    public static List<AtomEncoder> getEncoders(AtomHashGenerator generator) {
        try {
            Field field = generator.getClass().getDeclaredField("seedGenerator");
            field.setAccessible(true);
            Object o1 = field.get(generator);
            if (o1 instanceof SeedGenerator) {
                SeedGenerator seedGenerator = (SeedGenerator) o1;
                Field f2 = seedGenerator.getClass().getDeclaredField("encoder");
                f2.setAccessible(true);
                Object o2 = f2.get(seedGenerator);
                return getEncoders((ConjugatedAtomEncoder) o2);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static List<AtomEncoder> getEncoders(ConjugatedAtomEncoder conjugated) {
        try {
            Field field = conjugated.getClass().getDeclaredField("encoders");
            field.setAccessible(true);
            return (List<AtomEncoder>) field.get(conjugated);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

}
