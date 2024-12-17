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
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.test.CDKTestCase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Uli Fechner
 * @cdk.module test-rinchi
 */
class RInChIToReactionTest extends CDKTestCase {
    private final SmilesGenerator smilesGenerator = new SmilesGenerator(SmiFlavor.Canonical);

    @Test
    public void rinchiToReaction_rinchiIsNull_test() {
        // arrange
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        // act
        RInChIToReaction rInChIToReaction = new RInChIToReaction(null, builder);
        // assert
        assertThat(rInChIToReaction.getStatus()).isEqualTo(StatusMessagesOutput.Status.ERROR);
        assertThat(rInChIToReaction.getMessages()).containsExactly("RInChI string provided as argument is 'null'.");
    }

    @Test
    public void rinchiToReaction_builderIsNull_test() {
        // act
        RInChIToReaction rInChIToReaction = new RInChIToReaction("", null);
        // assert
        assertThat(rInChIToReaction.getStatus()).isEqualTo(StatusMessagesOutput.Status.ERROR);
        assertThat(rInChIToReaction.getMessages()).containsExactly("IChemObjectBuilder provided as argument is 'null'.");
    }

    private static Stream<Arguments> rinchiToReaction_argumentProvider() {
        return Stream.of(
                Arguments.of(
                        "RInChI=1.00.1S//d+/x1-1-0",
                        "",
                        "",
                        StatusMessagesOutput.Status.ERROR,
                        Arrays.asList("RInChI to Reaction failed: Encountered issue with decomposing RInChI and/or RAuxInfo: Cannot decompose invalid RInChI string 'RInChI=1.00.1S//d+/x1-1-0'.")
                ),
                Arguments.of(
                        "RInChI=1.00.1S/CH4/h1H4!ClH/h1H<>CH4O/c1-2/h2H,1H3!ClH/h1H<>H2O/h1H2/d+",
                        "RAuxInfo=1.00.1/0/N:1/rA:1nC/rB:/rC:4.2,-5.8,0;!0/N:1/rA:1nCl/rB:/rC:6.475,-5.875,0;<>0/N:1,2/rA:2nCO/rB:s1;/rC:10.675,-5.675,0;10.675,-4.675,0;!0/N:1/rA:1nCl/rB:/rC:13.7,-5.7,0;<>0/N:1/rA:1nO/rB:/rC:8.725,-5.025,0;",
                        "Cl.C>O>Cl.OC",
                        StatusMessagesOutput.Status.SUCCESS,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "RInChI=1.00.1S/C15H11NO3S/c1-8(15(18)19)9-4-5-12-11(7-9)13(17)10-3-2-6-16-14(10)20-12/h2-8H,1H3,(H,18,19)!ClH/h1H<>C15H13NO2S/c1-9(15(17)18)10-4-5-13-12(7-10)8-11-3-2-6-16-14(11)19-13/h2-7,9H,8H2,1H3,(H,17,18)<>C4H8O2/c1-2-6-4-3-5-1/h1-4H2/d+",
                        "",
                        "Cl.O=C(O)C(C1=CC=C2SC3=NC=CC=C3C(=O)C2=C1)C>O1CCOCC1>O=C(O)C(C1=CC=C2SC3=NC=CC=C3CC2=C1)C",
                        StatusMessagesOutput.Status.SUCCESS,
                        Collections.emptyList()
                ),
                Arguments.of(
                        "RInChI=1.00.1S/C10H14O3S/c1-6-5-7(11)8(9(12)13-6)14-10(2,3)4/h5,11H,1-4H3<>C4H10O/c1-4(2,3)5/h5H,1-3H3!C6H6O3S/c1-3-2-4(7)5(10)6(8)9-3/h2,7,10H,1H3!H2O4S/c1-5(2,3)4/h(H2,1,2,3,4)/d-",
                        "RAuxInfo=1.00.1/0/N:8,12,13,14,9,7,10,3,4,1,11,5,6,2/E:(2,3,4)/rA:14nCSCCOOCCCCOCCC/rB:s1;s2;s3;d4;s4;s6;s7;w7;w3s9;s10;s1;s1;s1;/rC:;;;;;;;;;;;;;;" +
                                "<>0/N:3,4,5,1,2/E:(1,2,3)/rA:5nCOCCC/rB:s1;s1;s1;s1;/rC:;;;;;!0/N:5,3,4,2,9,7,1,8,6,10/rA:10nOCCCCOCOCS/rB:s1;s2;w3;s4;s4;s6;d7;w2s7;s9;/rC:;;;;;;;;;;" +
                                "!1/N:2,3,4,5,1/E:(1,2,3,4)/CRV:5.6/rA:5nSOOOO/rB:d1;d1;s1;s1;/rC:;;;;;",
                        "O=C1OC(=CC(O)=C1S)C.O=S(=O)(O)O.OC(C)(C)C>>O=C1OC(=CC(O)=C1SC(C)(C)C)C",
                        StatusMessagesOutput.Status.SUCCESS,
                        Collections.emptyList()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("rinchiToReaction_argumentProvider")
    public void rinchiToReaction_test(String rinchi, String rAuxInfo, String expectedReaction, StatusMessagesOutput.Status expectedStatus, List<String> expectedMessages) throws CDKException {
        // arrange
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        // act
        RInChIToReaction rInChIToReaction = new RInChIToReaction(rinchi, builder);
        // assert
        assertThat(rInChIToReaction.getStatus()).isEqualTo(expectedStatus);
        assertThat(rInChIToReaction.getMessages()).containsExactlyElementsOf(expectedMessages);
        if (expectedStatus == StatusMessagesOutput.Status.SUCCESS) {
            assertThat(rInChIToReaction.getReaction()).isNotNull();
            String reactionSmiles = smilesGenerator.create(rInChIToReaction.getReaction());
            assertThat(reactionSmiles).isEqualTo(expectedReaction);
        }
    }

}