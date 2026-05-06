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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openscience.cdk.exception.CDKException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 *  @author Uli Fechner
 */
class InChILayersTest {

    @Test
    void append_emptyInchi_Test() throws CDKException {
        // arrange
        final InChILayers inChILayers = new InChILayers();

        // act
        inChILayers.append("");

        // assert
        assertThat(inChILayers.getMajors()).isEmpty();
        assertThat(inChILayers.getMinors()).isEmpty();
        assertThat(inChILayers.getProtonCount()).isEqualTo(0);
    }

    @Test
    void append_noLayersInchi_Test() {
        // arrange
        final InChILayers inChILayers = new InChILayers();

        // act & assert
        assertThatThrownBy(() -> inChILayers.append("InChI=1S")).isInstanceOf(CDKException.class);
    }

    @Test
    void append_noStandardInchi_Test() {
        // arrange
        final InChILayers inChILayers = new InChILayers();

        // act & assert
        assertThatThrownBy(() -> inChILayers.append("InChI=1A/C2H6O/c1-2-3/h3H,2H2,1H3")).isInstanceOf(CDKException.class);
    }

    @Test
    void append_versionTwoInchi_Test() {
        // arrange
        final InChILayers inChILayers = new InChILayers();

        // act & assert
        assertThatThrownBy(() -> inChILayers.append("InChI=2S/C2H6O/c1-2-3/h3H,2H2,1H3")).isInstanceOf(CDKException.class);
    }

    @Test
    void append_invalidStartsWithInchi_Test() {
        // arrange
        final InChILayers inChILayers = new InChILayers();

        // act & assert
        assertThatThrownBy(() -> inChILayers.append("inchi=1S/C2H6O/c1-2-3/h3H,2H2,1H3")).isInstanceOf(CDKException.class);
    }

    static Stream<Arguments> append_validInchi_Test_MethodSource() {
        return Stream.of(
                Arguments.of("InChI=1S/C2H6", "C2H6", "", 0),
                Arguments.of("InChI=1S/C2H6/c1-2", "C2H6/c1-2", "", 0),
                Arguments.of("InChI=1S/C2H6/c1-2/h1-2H3", "C2H6/c1-2/h1-2H3", "", 0),
                Arguments.of("InChI=1S/C3H8NO/c1-3(5)4-2/h3-4H,1-2H3/q-1", "C3H8NO/c1-3(5)4-2/h3-4H,1-2H3/q-1", "", 0),
                Arguments.of("InChI=1S/C8H12N.ClH/c1-9(2)8-6-4-3-5-7-8;/h4-7H,3H2,1-2H3;1H/q+1;/p-1",
                        "C8H12N.ClH/c1-9(2)8-6-4-3-5-7-8;/h4-7H,3H2,1-2H3;1H/q+1;", "", -1),
                Arguments.of("InChI=1S/C10H13N5O3/c11-8-7-9(14-10(17)13-8)15(4-12-7)6-2-1-5(3-16)18-6/h4-6,16H,1-3H2,(H3,11,13,14,17)/t5-,6+/m0/s1",
                        "C10H13N5O3/c11-8-7-9(14-10(17)13-8)15(4-12-7)6-2-1-5(3-16)18-6/h4-6,16H,1-3H2,(H3,11,13,14,17)", "t5-,6+/m0/s1", 0),
                Arguments.of("InChI=1S/C10H20O/c1-7(2)9-5-4-8(3)6-10(9)11/h7-11H,4-6H2,1-3H3/t8-,9-,10+/m1/s1",
                        "C10H20O/c1-7(2)9-5-4-8(3)6-10(9)11/h7-11H,4-6H2,1-3H3", "t8-,9-,10+/m1/s1", 0),
                Arguments.of("InChI=1S/ClH/h1H/p-1", "ClH/h1H", "", -1),
                Arguments.of("InChI=1S/C3H8/c1-3-2/h3H2,1-2H3/i1+1", "C3H8/c1-3-2/h3H2,1-2H3", "i1+1", 0),
                Arguments.of("InChI=1S/", "/", "", 0)
        );
    }

    @ParameterizedTest
    @MethodSource("append_validInchi_Test_MethodSource")
    void append_validInchi_Test(final String inchi, final String majorsExpected, final String minorsExpected, final int protonCountExpected) throws CDKException {
        // arrange
        final InChILayers inChILayers = new InChILayers();

        // act
        inChILayers.append(inchi);

        // assert
        assertThat(inChILayers.getMajors()).isEqualTo(majorsExpected);
        assertThat(inChILayers.getMinors()).isEqualTo(minorsExpected);
        assertThat(inChILayers.getProtonCount()).isEqualTo(protonCountExpected);
    }

    static Stream<Arguments> protonCount2CharTestMethodSource() {
        return Stream.of(
                Arguments.of(-25, 'A'),
                Arguments.of(-13, 'A'),
                Arguments.of(-12, 'B'),
                Arguments.of(-11, 'C'),
                Arguments.of(-10, 'D'),
                Arguments.of(-9, 'E'),
                Arguments.of(-8, 'F'),
                Arguments.of(-7, 'G'),
                Arguments.of(-6, 'H'),
                Arguments.of(-5, 'I'),
                Arguments.of(-4, 'J'),
                Arguments.of(-3, 'K'),
                Arguments.of(-2, 'L'),
                Arguments.of(-1, 'M'),
                Arguments.of(0, 'N'),
                Arguments.of(1, 'O'),
                Arguments.of(2, 'P'),
                Arguments.of(3, 'Q'),
                Arguments.of(4, 'R'),
                Arguments.of(5, 'S'),
                Arguments.of(6, 'T'),
                Arguments.of(7, 'U'),
                Arguments.of(8, 'V'),
                Arguments.of(9, 'W'),
                Arguments.of(10, 'X'),
                Arguments.of(11, 'Y'),
                Arguments.of(12, 'Z'),
                Arguments.of(13, 'A'),
                Arguments.of(19, 'A')
        );
    }

    @ParameterizedTest
    @MethodSource("protonCount2CharTestMethodSource")
    void protonCount2CharTest (final int protonCount, final char expected) {
        final char actual = InChILayers.protonCount2Char(protonCount);
        assertThat(actual).isEqualTo(expected);
    }
}