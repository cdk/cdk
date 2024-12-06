/* Copyright (C) 2024 Uli Fechner
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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openscience.cdk.test.CDKTestCase;

import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Uli Fechner
 * @cdk.module test-rinchi
 */
class RInChIHashTest extends CDKTestCase {

    static Stream<Arguments> generateSha2TestMethodSource() {
        return Stream.of(
                Arguments.of("", new int[] {227, 176, 196, 66, 152, 252, 28, 20, 154, 251, 244, 200, 153, 111, 185, 36, 39, 174, 65, 228, 100, 155, 147, 76, 164, 149, 153, 27, 120, 82, 184, 85}),
                Arguments.of("ThisIsATestString", new int[] {42, 245, 196, 144, 255, 213, 6, 57, 248, 23, 13, 121, 8, 9, 166, 120, 154, 151, 5, 162, 36, 249, 121, 209, 181, 27, 56, 226, 43, 213, 177, 4}),
                Arguments.of("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec ac mauris velit. Praesent massa ligula.",
                        new int[] {48, 231, 46, 181, 243, 243, 158, 236, 14, 180, 139, 19, 71, 125, 230, 132, 8, 167, 129, 98, 101, 200, 128, 182, 239, 244, 117, 243, 44, 95, 106, 97})
        );
    }

    @ParameterizedTest
    @MethodSource("generateSha2TestMethodSource")
    void generateSha2Test(final String input, final int[] expected) throws NoSuchAlgorithmException {
        assertThat(RInChIHash.generateSha2(input)).isEqualTo(expected);
    }

    public static Stream<Arguments> hash04CharTestMethodSource() {
        return Stream.of(
                Arguments.of("", "UHFF"),
                Arguments.of("TestInput", "BGJU")
        );
    }

    @ParameterizedTest
    @MethodSource("hash04CharTestMethodSource")
    void hash04CharTest(final String input, final String expected) throws NoSuchAlgorithmException {
        assertThat(RInChIHash.hash04char(input)).isEqualTo(expected);
    }

    public static Stream<Arguments> hash10CharTestMethodSource() {
        return Stream.of(
                Arguments.of("", "UHFFFADPSC"),
                Arguments.of("TestInput", "BGJUCBOEFH")
        );
    }

    @ParameterizedTest
    @MethodSource("hash10CharTestMethodSource")
    void hash10CharTest(final String input, final String expected) throws NoSuchAlgorithmException {
        assertThat(RInChIHash.hash10char(input)).isEqualTo(expected);
    }

    public static Stream<Arguments> hash12CharTestMethodSource() {
        return Stream.of(
                Arguments.of("", "UHFFFADPSCTJ"),
                Arguments.of("TestInput", "BGJUCBOEFHMY")
        );
    }

    @ParameterizedTest
    @MethodSource("hash12CharTestMethodSource")
    void hash12CharTest(final String input, final String expected) throws NoSuchAlgorithmException {
        assertThat(RInChIHash.hash12char(input)).isEqualTo(expected);
    }

    public static Stream<Arguments> hash14CharTestMethodSource() {
        return Stream.of(
                Arguments.of("", "UHFFFADPSCTJAU"),
                Arguments.of("TestInput", "BGJUCBOEFHMYQD")
        );
    }

    @ParameterizedTest
    @MethodSource("hash14CharTestMethodSource")
    void hash14CharTest(final String input, final String expected) throws NoSuchAlgorithmException {
        assertThat(RInChIHash.hash14char(input)).isEqualTo(expected);
    }

    public static Stream<Arguments> hash17CharTestMethodSource() {
        return Stream.of(
                Arguments.of("", "UHFFFADPSCTJAUYIS"),
                Arguments.of("TestInput", "BGJUCBOEFHMYQDJZH")
        );
    }

    @ParameterizedTest
    @MethodSource("hash17CharTestMethodSource")
    void hash17CharTest(final String input, final String expected) throws NoSuchAlgorithmException {
        assertThat(RInChIHash.hash17char(input)).isEqualTo(expected);
    }
}