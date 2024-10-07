/* Copyright (C) 2024 Beilstein-Institute, Uli Fechner
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
package org.openscience.cdk.rinchi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Felix Bänsch
 * @author Uli Fechner
 */
class KeyBase26Test {

    @Test
    void getBase26Triplet_2703_DZZ() {
        assertEquals("DZZ", KeyBase26.getBase26Triplet(2703));
    }

    @Test
    void getBase26Triplet_2704_FAA() {
        assertEquals("FAA", KeyBase26.getBase26Triplet(2704));
    }

    @Test
    void getBase26Triplet_2704_ZZZ() {
        assertEquals("ZZZ", KeyBase26.getBase26Triplet(16383));
    }

    @Test
    void base26Triplet_701_BAZ_Test() {
        assertEquals("BAZ", KeyBase26.getBase26Triplet(701));
    }

    @Test
    void base26Triplet_676_BAA_Test() {
        assertEquals("BAA", KeyBase26.getBase26Triplet(676));
    }

    @Test
    void base26Triplet_0_AAA_Test() {
        assertEquals("AAA", KeyBase26.getBase26Triplet(0));
    }

    @Test
    void base26Doublet_675_ZZ_Test() {
        assertEquals("ZZ", KeyBase26.getBase26Doublet(675));
    }

    @Test
    void base26Doublet_256_JW_Test() {
        assertEquals("JW", KeyBase26.getBase26Doublet(256));
    }

    @Test
    void base26DoubletTest() throws IOException {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("org.openscience.cdk.rinchi/doublets26.txt"))))) {
            final String[] doublet26 = reader.lines().toArray(String[]::new);

            for (int index = 0; index < doublet26.length; index++) {
                assertEquals(doublet26[index], KeyBase26.getBase26Doublet(index));
            }
        }
    }

    @Test
    void base26TripletTest() throws IOException {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("org.openscience.cdk.rinchi/triplet26.txt"))))) {
            final String[] triplets26 = reader.lines().toArray(String[]::new);

            for (int index = 0; index < triplets26.length; index++) {
                assertEquals(triplets26[index], KeyBase26.getBase26Triplet(index));
            }
        }
    }

    static Stream<Arguments> base26Triplet1TestMethodSource() {
        return Stream.of(
                Arguments.of(new int[]{0,   0}, "AAA"),
                Arguments.of(new int[]{255, 255}, "ZZZ"),
                Arguments.of(new int[]{128, 128}, "AEY"),
                Arguments.of(new int[]{1,   2}, "ATT"),
                Arguments.of(new int[]{64,  32}, "NFO"),
                Arguments.of(new int[]{112, 49}, "UMQ")
        );
    }

    @ParameterizedTest
    @MethodSource("base26Triplet1TestMethodSource")
    void base26Triplet1Test(final int[] input, final String expected) {
        final String actual = KeyBase26.base26Triplet1(input);
        assertEquals(expected, actual);
    }

    static Stream<Arguments> base26Triplet2TestMethodSource() {
        return Stream.of(
                Arguments.of(new int[]{0, 0,   0,   0}, "AAA"),
                Arguments.of(new int[]{0, 192, 255, 15}, "ZZZ"),
                Arguments.of(new int[]{0, 128, 170, 5}, "JPE"),
                Arguments.of(new int[]{0, 64,  85,  9}, "PDP"),
                Arguments.of(new int[]{0, 150, 200, 11}, "SWC")
        );
    }

    @ParameterizedTest
    @MethodSource("base26Triplet2TestMethodSource")
    void base26Triplet2Test(final int[] input, final String expected) {
        final String actual = KeyBase26.base26Triplet2(input);
        assertEquals(expected, actual);
    }

    static Stream<Arguments> base26Triplet3TestMethodSource() {
        return Stream.of(
                Arguments.of(new int[]{0, 0, 0, 0,   0,   0}, "AAA"),
                Arguments.of(new int[]{0, 0, 0, 240, 255, 3}, "ZZZ"),
                Arguments.of(new int[]{0, 0, 0, 128, 127, 2}, "QDO"),
                Arguments.of(new int[]{0, 0, 0, 27,  12,  0}, "AHL")
        );
    }

    @ParameterizedTest
    @MethodSource("base26Triplet3TestMethodSource")
    void base26Triplet3Test(final int[] input, final String expected) {
        final String actual = KeyBase26.base26Triplet3(input);
        assertEquals(expected, actual);
    }

    static Stream<Arguments> base26Triplet4TestMethodSource() {
        return Stream.of(
                Arguments.of(new int[]{0, 0, 0, 0, 0, 0,   0}, "AAA"),
                Arguments.of(new int[]{0, 0, 0, 0, 0, 252, 255}, "ZZZ"),
                Arguments.of(new int[]{0, 0, 0, 0, 0, 127, 2}, "AGD"),
                Arguments.of(new int[]{0, 0, 0, 0, 0, 1,   89}, "JLC")
        );
    }

    @ParameterizedTest
    @MethodSource("base26Triplet4TestMethodSource")
    void base26Triplet4Test(final int[] input, final String expected) {
        final String actual = KeyBase26.base26Triplet4(input);
        assertEquals(expected, actual);
    }

    static Stream<Arguments> base26DoubletForBits56To64MethodSource() {
        return Stream.of(
                Arguments.of(new int[]{0, 0, 0, 0, 0, 0, 0, 0,   0}, "AA"),
                Arguments.of(new int[]{0, 0, 0, 0, 0, 0, 0, 255, 1}, "TR"),
                Arguments.of(new int[]{0, 0, 0, 0, 0, 0, 0, 203, 0}, "HV")
        );
    }

    @ParameterizedTest
    @MethodSource("base26DoubletForBits56To64MethodSource")
    void base26DoubletForBits56To64Test(final int[] input, final String expected) {
        final String actual = KeyBase26.base26DoubletForBits56To64(input);
        assertEquals(expected, actual);
    }

}