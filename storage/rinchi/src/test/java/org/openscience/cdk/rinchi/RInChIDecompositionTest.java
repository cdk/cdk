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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openscience.cdk.ReactionRole;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.test.CDKTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Uli Fechner
 */
class RInChIDecompositionTest extends CDKTestCase {

    static Stream<Arguments> rinchiCharacterToDirectionArgumentsProvider() {
        return Stream.of(
                Arguments.of("+", IReaction.Direction.FORWARD),
                Arguments.of("-", IReaction.Direction.BACKWARD),
                Arguments.of("=", IReaction.Direction.BIDIRECTIONAL),
                Arguments.of("", IReaction.Direction.UNDIRECTED),
                Arguments.of(null, IReaction.Direction.UNDIRECTED)
        );
    }

    @ParameterizedTest
    @MethodSource("rinchiCharacterToDirectionArgumentsProvider")
    void rinchiCharacterToDirection_test(String rinchiCharacter, IReaction.Direction expectedDirection) {
        // arrange
        RInChIDecomposition rInChIDecomposition = new RInChIDecomposition(null);
        // act
        IReaction.Direction actual = rInChIDecomposition.rinchiCharacterToDirection(rinchiCharacter);
        // assert
        assertThat(actual).isEqualTo(expectedDirection);
    }

    @ParameterizedTest
    @CsvSource(nullValues = "null", value = {
            "RInChI=1.00.1S/, '', null, null, null, null, null, null, null",
            "RInChI=1.00.1S//d+/u1-1, '', null, null, +, 1, 1, null",
            "RInChI=1.00.1S//d+/u1-1-0, '', null, null, +, 1, 1, 0",
            "RInChI=1.00.1S//d-/u4-6-0, '', null, null, -, 4, 6, 0",
            "RInChI=1.00.1S//d=/u0-2-0, '', null, null, =, 0, 2, 0",
            "RInChI=1.00.1S//d=/u11-10-2, '', null, null, =, 11, 10, 2",
            "RInChI=1.00.1S/CH3Cl/c1-2/h1H3<>CH4/h1H4<>H2O/h1H2/d-, CH3Cl/c1-2/h1H3, CH4/h1H4, H2O/h1H2, -, null, null, null",
            "RInChI=1.00.1S/<>CH4/h1H4/d-/u1-0-0, '', CH4/h1H4, null, -, 1, 0, 0",
            "RInChI=1.00.1S/<><>ClH/h1H/d+/u1-1-0, '', '', ClH/h1H, +, 1, 1, 0",
            "'RInChI=1.00.1S/C2H4O2/c1-2(3)4/h1H3,(H,3,4)!C2H6O/c1-2-3/h3H,2H2,1H3<>C4H8O2/c1-3-6-4(2)5/h3H2,1-2H3!H2O/h1H2<>H2O4S/c1-5(2,3)4/h(H2,1,2,3,4)/d+', " +
                    "'C2H4O2/c1-2(3)4/h1H3,(H,3,4)!C2H6O/c1-2-3/h3H,2H2,1H3', 'C4H8O2/c1-3-6-4(2)5/h3H2,1-2H3!H2O/h1H2', 'H2O4S/c1-5(2,3)4/h(H2,1,2,3,4)', +, null, null, null",
            "'RInChI=1.00.1S/C6H12O/c1-4-6(3)5(2)7-6/h5H,4H2,1-3H3/t5-,6-/m0/s1!H2O/h1H2/p-1<>C6H14O2/c1-4-6(3,8)5(2)7/h5,7-8H,4H2,1-3H3/t5-,6+/m1/s1/d+', " +
                    "'C6H12O/c1-4-6(3)5(2)7-6/h5H,4H2,1-3H3/t5-,6-/m0/s1!H2O/h1H2/p-1', 'C6H14O2/c1-4-6(3,8)5(2)7/h5,7-8H,4H2,1-3H3/t5-,6+/m1/s1', null, +, null, null, null",
            "'RInChI=1.00.1S/<>C8H8/c1-2-8-6-4-3-5-7-8/h2-7H,1H2/d-/u1-0-0', '', 'C8H8/c1-2-8-6-4-3-5-7-8/h2-7H,1H2', null, -, 1, 0, 0",
    })
    void rinchiPattern_matchesIsTrue_test(String rinchi, String layer2Expected, String layer3Expected, String layer4Expected,
                                          String layer5Expected, String noStruct1Expected, String noStruct2Expected, String noStruct3Expected) {
        // act
        Matcher matcher = RInChIDecomposition.RINCHI_PATTERN.matcher(rinchi);
        // assert
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(RInChIDecomposition.PATTERN_GROUP_LAYER_2)).isEqualTo(layer2Expected);
        assertThat(matcher.group(RInChIDecomposition.PATTERN_GROUP_LAYER_3)).isEqualTo(layer3Expected);
        assertThat(matcher.group(RInChIDecomposition.PATTERN_GROUP_LAYER_4)).isEqualTo(layer4Expected);
        assertThat(matcher.group(RInChIDecomposition.PATTERN_GROUP_DIRECTION)).isEqualTo(layer5Expected);
        assertThat(matcher.group(RInChIDecomposition.PATTERN_GROUP_NOSTRUCT_1)).isEqualTo(noStruct1Expected);
        assertThat(matcher.group(RInChIDecomposition.PATTERN_GROUP_NOSTRUCT_2)).isEqualTo(noStruct2Expected);
        assertThat(matcher.group(RInChIDecomposition.PATTERN_GROUP_NOSTRUCT_3)).isEqualTo(noStruct3Expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "RInChI=1.00.1S//d-/p43",
            "RInChI=1.00.1S//u0-1-0/p43",
            "RInChI=1.00.1S//u0-1-0/d=",
            "RInChI=1.00.1S/CH3Cl/c1-2/h1H3<>CH4/h1H4/d-/p43",
            "RInChI=1.00.1S/<>CH4/h1H4/d-/p43",
            "RInChI=1.00.1S/<><>H2O/h1H2/d-/p43",
            "RInChI=1.00.1S/CH3Cl/c1-2/h1H3<>CH4/h1H4<>H2O/h1H2/d-/p43",
    })
    void rinchiPattern_matchesIsFalse_test(String rinchi) {
        // act
        Matcher matcher = RInChIDecomposition.RINCHI_PATTERN.matcher(rinchi);
        // assert
        assertThat(matcher.matches()).isFalse();
    }

    @Test
    void decomposeRAuxInfo_invalid_test() {
        // arrange
        RInChIDecomposition rInChIDecomposition = new RInChIDecomposition(null);
        // act & assert
        assertThatThrownBy(() -> rInChIDecomposition.decomposeRAuxInfo(""))
                .isInstanceOf(RInChIException.class)
                .hasMessage("Invalid/unsupported RInChI auxiliary information string. First layer must be equal to 'RAuxInfo=1.00.1/'.");
    }

    static Stream<Arguments> decomposeRAuxInfoArgumentsProvider() {
        return Stream.of(
                Arguments.of("RAuxInfo=1.00.1/", new ArrayList<>(), new ArrayList<>(), new ArrayList<>()),
                Arguments.of("RAuxInfo=1.00.1/0/N:1,3,2,4/E:(1,2)/rA:4nCCCO/rB:s1;s2;d2;/rC:11.734,-5.5,0;12.6,-5,0;13.466,-5.5,0;12.6,-4,0;" +
                                "<>0/N:1,3,2,4/E:(1,2)/rA:4nCCCO/rB:s1;s2;s2;/rC:5.167,-5.425,0;6.033,-4.925,0;6.899,-5.425,0;6.033,-3.925,0;",
                        Collections.singletonList("0/N:1,3,2,4/E:(1,2)/rA:4nCCCO/rB:s1;s2;d2;/rC:11.734,-5.5,0;12.6,-5,0;13.466,-5.5,0;12.6,-4,0;"),
                        Collections.singletonList("0/N:1,3,2,4/E:(1,2)/rA:4nCCCO/rB:s1;s2;s2;/rC:5.167,-5.425,0;6.033,-4.925,0;6.899,-5.425,0;6.033,-3.925,0;"),
                        new ArrayList<>()),
                Arguments.of("RAuxInfo=1.00.1/<>0/N:1/rA:1nO/rB:/rC:10.825,-4.55,0;",
                        new ArrayList<>(),
                        Collections.singletonList("0/N:1/rA:1nO/rB:/rC:10.825,-4.55,0;"),
                        new ArrayList<>()
                ),
                Arguments.of("RAuxInfo=1.00.1/<><>0/N:1/rA:1nO/rB:/rC:9.225,-3.775,0;",
                        new ArrayList<>(),
                        new ArrayList<>(),
                        Collections.singletonList("0/N:1/rA:1nO/rB:/rC:9.225,-3.775,0;")
                ),
                Arguments.of("RAuxInfo=1.00.1/0/N:1,3,4,2/E:(1,2,3)/rA:4nCCCC/rB:s1;s2;s2;/rC:4.509,-5.125,0;5.375,-4.625,0;6.241,-5.125,0;5.375,-3.625,0;" +
                                "<>0/N:4,1,3,2/E:(2,3)/rA:4nCCCC/rB:s1;s2;d2;/rC:11.592,-4.95,0;12.458,-4.45,0;13.324,-4.95,0;12.458,-3.45,0;" +
                                "<>0/N:1,2/E:(1,2)/rA:2nOO/rB:d1;/rC:8.3,-3.75,0;9.3,-3.75,0;",
                        Collections.singletonList("0/N:1,3,4,2/E:(1,2,3)/rA:4nCCCC/rB:s1;s2;s2;/rC:4.509,-5.125,0;5.375,-4.625,0;6.241,-5.125,0;5.375,-3.625,0;"),
                        Collections.singletonList("0/N:4,1,3,2/E:(2,3)/rA:4nCCCC/rB:s1;s2;d2;/rC:11.592,-4.95,0;12.458,-4.45,0;13.324,-4.95,0;12.458,-3.45,0;"),
                        Collections.singletonList("0/N:1,2/E:(1,2)/rA:2nOO/rB:d1;/rC:8.3,-3.75,0;9.3,-3.75,0;")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("decomposeRAuxInfoArgumentsProvider")
    void decomposeRAuxInfo_test(String rAuxInfo, List<String> layer1, List<String> layer2, List<String> layer3) throws RInChIException {
        // arrange
        RInChIDecomposition rInChIDecomposition = new RInChIDecomposition(null);
        // act
        List<List<String>> actual = rInChIDecomposition.decomposeRAuxInfo(rAuxInfo);
        // assert
        assertThat(actual).hasSize(3);
        assertThat(actual.get(0)).isEqualTo(layer1);
        assertThat(actual.get(1)).isEqualTo(layer2);
        assertThat(actual.get(2)).isEqualTo(layer3);
    }

    @Test
    void decompose_rinchiNull_test() {
        // arrange
        RInChIDecomposition rInChIDecomposition = new RInChIDecomposition(null);
        // act
        rInChIDecomposition.decompose();
        // assert
        assertThat(rInChIDecomposition.getStatus()).isEqualTo(StatusMessagesOutput.Status.ERROR);
        assertThat(rInChIDecomposition.getMessages()).containsExactly("RInChI string provided as input is 'null'.");
    }

    @Test
    void decompose_rinchiAndAuxInfoNull_test() {
        // arrange
        RInChIDecomposition rInChIDecomposition = new RInChIDecomposition(null, null);
        // act
        rInChIDecomposition.decompose();
        // assert
        assertThat(rInChIDecomposition.getStatus()).isEqualTo(StatusMessagesOutput.Status.ERROR);
        assertThat(rInChIDecomposition.getMessages()).containsExactly("RInChI string provided as input is 'null'.", "RInChI auxiliary info string provided as input is 'null'.");
    }

    @Test
    void decompose_invalidRinchi_test() {
        // arrange
        RInChIDecomposition rInChIDecomposition = new RInChIDecomposition("RInChI=1.00.1S/CH3Cl/c1-2/h1H3<>CH4/h1H4<>H2O/h1H2/d-/p43");
        // act
        rInChIDecomposition.decompose();
        // assert
        assertThat(rInChIDecomposition.getStatus()).isEqualTo(StatusMessagesOutput.Status.ERROR);
        assertThat(rInChIDecomposition.getMessages()).containsExactly("Cannot decompose invalid RInChI string 'RInChI=1.00.1S/CH3Cl/c1-2/h1H3<>CH4/h1H4<>H2O/h1H2/d-/p43'.");
    }

    @Test
    void decompose_differentNumberOfMoleculesInRinchiAndRAuxInfo_test() {
        // arrange
        RInChIDecomposition rInChIDecomposition = new RInChIDecomposition(
                "RInChI=1.00.1S/C3H6O/c1-3(2)4/h1-2H3<>C4H10/c1-4(2)3/h4H,1-3H3<>H2O/h1H2/d-",
                "RAuxInfo=1.00.1/0/N:1,3,2,4/E:(1,2)/rA:4nCCCO/rB:s1;s2;d2;/rC:10.817,-5.65,0;11.683,-5.15,0;12.549,-5.65,0;11.683,-4.15,0;<><>0/N:1/rA:1nO/rB:/rC:7.975,-4.525,0;");
        // act
        rInChIDecomposition.decompose();
        // assert
        assertThat(rInChIDecomposition.getStatus()).isEqualTo(StatusMessagesOutput.Status.ERROR);
        assertThat(rInChIDecomposition.getMessages()).containsExactly("Different number of molecules in RInChI (1, 1, 1) and Auxiliary Information (1, 0, 1).");
    }

    static Stream<Arguments> decompose_rinchiOnly_argumentsProvider() {
        return Stream.of(
                Arguments.of(
                        "RInChI=1.00.1S/C3H6O/c1-3(2)4/h1-2H3<>C4H10/c1-4(2)3/h4H,1-3H3/d-",
                        IReaction.Direction.BACKWARD,
                        Arrays.asList(
                                new RInChIDecomposition.Component("InChI=1S/C3H6O/c1-3(2)4/h1-2H3", "", ReactionRole.Product),
                                new RInChIDecomposition.Component("InChI=1S/C4H10/c1-4(2)3/h4H,1-3H3", "", ReactionRole.Reactant)
                        )
                ),
                Arguments.of(
                        "RInChI=1.00.1S/C3H6O/c1-3(2)4/h1-2H3<>C4H10/c1-4(2)3/h4H,1-3H3<>H2O/h1H2/d=",
                        IReaction.Direction.BIDIRECTIONAL,
                        Arrays.asList(
                                new RInChIDecomposition.Component("InChI=1S/C3H6O/c1-3(2)4/h1-2H3", "", ReactionRole.Reactant),
                                new RInChIDecomposition.Component("InChI=1S/C4H10/c1-4(2)3/h4H,1-3H3", "", ReactionRole.Product),
                                new RInChIDecomposition.Component("InChI=1S/H2O/h1H2", "", ReactionRole.Agent)
                        )
                ),
                Arguments.of(
                        "RInChI=1.00.1S/C3H6O/c1-3(2)4/h1-2H3<>C4H10/c1-4(2)3/h4H,1-3H3/d+",
                        IReaction.Direction.FORWARD,
                        Arrays.asList(
                                new RInChIDecomposition.Component("InChI=1S/C3H6O/c1-3(2)4/h1-2H3", "", ReactionRole.Reactant),
                                new RInChIDecomposition.Component("InChI=1S/C4H10/c1-4(2)3/h4H,1-3H3", "", ReactionRole.Product)
                        )
                ),
                Arguments.of(
                        "RInChI=1.00.1S/C3H6O/c1-3(2)4/h1-2H3<>C4H10/c1-4(2)3/h4H,1-3H3",
                        IReaction.Direction.UNDIRECTED,
                        Arrays.asList(
                                new RInChIDecomposition.Component("InChI=1S/C3H6O/c1-3(2)4/h1-2H3", "", ReactionRole.Reactant),
                                new RInChIDecomposition.Component("InChI=1S/C4H10/c1-4(2)3/h4H,1-3H3", "", ReactionRole.Product)
                        )
                ),
                Arguments.of(
                        "RInChI=1.00.1S/C3H6O/c1-3(2)4/h1-2H3<>C4H10/c1-4(2)3/h4H,1-3H3/d+/u0-0-1",
                        IReaction.Direction.FORWARD,
                        Arrays.asList(
                                new RInChIDecomposition.Component("InChI=1S/C3H6O/c1-3(2)4/h1-2H3", "", ReactionRole.Reactant),
                                new RInChIDecomposition.Component("InChI=1S/C4H10/c1-4(2)3/h4H,1-3H3", "", ReactionRole.Product)
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("decompose_rinchiOnly_argumentsProvider")
    void decompose_rinchiOnly_test(String rinchi, IReaction.Direction direction, List<RInChIDecomposition.Component> components) {
        // arrange
        RInChIDecomposition rInChIDecomposition = new RInChIDecomposition(rinchi);
        // act
        RInChIDecomposition actual = rInChIDecomposition.decompose();
        // assert
        assertThat(actual.getStatus()).isEqualTo(StatusMessagesOutput.Status.SUCCESS);
        assertThat(actual.getReactionDirection()).isEqualTo(direction);
        assertThat(actual.getComponents()).containsExactlyElementsOf(components);
    }

    static Stream<Arguments> decompose_rinchiAndRAuxInfo_argumentsProvider() {
        return Stream.of(
                Arguments.of(
                        "RInChI=1.00.1S/C2H4O/c1-2-3/h2H,1H3!H2O/h1H2<>C2H4O2/c1-2(3)4/h1H3,(H,3,4)/d-",
                        "RAuxInfo=1.00.1/0/N:1,2,3/rA:4nCCOH/rB:s1;d2;s2;/rC:12.9299,-4.4486,0;13.7587,-4.9272,0;14.6201,-4.2658,0;13.7587,-5.8842,0;!0/N:1/rA:1nO/rB:/rC:16.175,-4.8,0;" +
                                "<>1/N:1,2,3,4/E:(3,4)/rA:4nCCOO/rB:s1;d2;s2;/rC:5.883,-4.35,0;6.749,-4.85,0;7.649,-4.159,0;6.749,-5.85,0;",
                        IReaction.Direction.BACKWARD,
                        Arrays.asList(
                                new RInChIDecomposition.Component(
                                        "InChI=1S/C2H4O/c1-2-3/h2H,1H3",
                                        "AuxInfo=1/0/N:1,2,3/rA:4nCCOH/rB:s1;d2;s2;/rC:12.9299,-4.4486,0;13.7587,-4.9272,0;14.6201,-4.2658,0;13.7587,-5.8842,0;",
                                        ReactionRole.Product
                                ),
                                new RInChIDecomposition.Component(
                                        "InChI=1S/H2O/h1H2",
                                        "AuxInfo=1/0/N:1/rA:1nO/rB:/rC:16.175,-4.8,0;",
                                        ReactionRole.Product
                                ),
                                new RInChIDecomposition.Component(
                                        "InChI=1S/C2H4O2/c1-2(3)4/h1H3,(H,3,4)",
                                        "AuxInfo=1/1/N:1,2,3,4/E:(3,4)/rA:4nCCOO/rB:s1;d2;s2;/rC:5.883,-4.35,0;6.749,-4.85,0;7.649,-4.159,0;6.749,-5.85,0;",
                                        ReactionRole.Reactant
                                )
                        )
                ),
                Arguments.of(
                        "RInChI=1.00.1S/C2H4O/c1-2-3/h2H,1H3!H2O/h1H2<>C2H4O2/c1-2(3)4/h1H3,(H,3,4)<>Co/d-",
                        "RAuxInfo=1.00.1/0/N:1,2,3/rA:4nCCOH/rB:s1;d2;s2;/rC:12.9299,-4.4486,0;13.7587,-4.9272,0;14.6201,-4.2658,0;13.7587,-5.8842,0;!0/N:1/rA:1nO/rB:/rC:16.175,-4.8,0;" +
                                "<>1/N:1,2,3,4/E:(3,4)/rA:4nCCOO/rB:s1;d2;s2;/rC:5.883,-4.35,0;6.749,-4.85,0;7.649,-4.159,0;6.749,-5.85,0;<>0/N:1/rA:1nCo/rB:/rC:9.825,-4.175,0;",
                        IReaction.Direction.BACKWARD,
                        Arrays.asList(
                                new RInChIDecomposition.Component(
                                        "InChI=1S/C2H4O/c1-2-3/h2H,1H3",
                                        "AuxInfo=1/0/N:1,2,3/rA:4nCCOH/rB:s1;d2;s2;/rC:12.9299,-4.4486,0;13.7587,-4.9272,0;14.6201,-4.2658,0;13.7587,-5.8842,0;",
                                        ReactionRole.Product
                                ),
                                new RInChIDecomposition.Component(
                                        "InChI=1S/H2O/h1H2",
                                        "AuxInfo=1/0/N:1/rA:1nO/rB:/rC:16.175,-4.8,0;",
                                        ReactionRole.Product
                                ),
                                new RInChIDecomposition.Component(
                                        "InChI=1S/C2H4O2/c1-2(3)4/h1H3,(H,3,4)",
                                        "AuxInfo=1/1/N:1,2,3,4/E:(3,4)/rA:4nCCOO/rB:s1;d2;s2;/rC:5.883,-4.35,0;6.749,-4.85,0;7.649,-4.159,0;6.749,-5.85,0;",
                                        ReactionRole.Reactant
                                ),
                                new RInChIDecomposition.Component("InChI=1S/Co", "AuxInfo=1/0/N:1/rA:1nCo/rB:/rC:9.825,-4.175,0;", ReactionRole.Agent)
                        )
                ),
                Arguments.of(
                        "RInChI=1.00.1S/<>C2H4O/c1-2-3/h2H,1H3/d+/u1-0-0",
                        "RAuxInfo=1.00.1/<>0/N:1,2,3/rA:4nCCOH/rB:s1;d2;s2;/rC:12.9299,-4.4486,0;13.7587,-4.9272,0;14.6201,-4.2658,0;13.7587,-5.8842,0;",
                        IReaction.Direction.FORWARD,
                        Collections.singletonList(
                                new RInChIDecomposition.Component(
                                        "InChI=1S/C2H4O/c1-2-3/h2H,1H3", "AuxInfo=1/0/N:1,2,3/rA:4nCCOH/rB:s1;d2;s2;/rC:12.9299,-4.4486,0;13.7587,-4.9272,0;14.6201,-4.2658,0;13.7587,-5.8842,0;",
                                        ReactionRole.Product
                                )
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("decompose_rinchiAndRAuxInfo_argumentsProvider")
    void decompose_rinchiAndRAuxInfo_test(String rinchi, String rAuxInfo, IReaction.Direction direction, List<RInChIDecomposition.Component> components) {
        // arrange
        RInChIDecomposition rInChIDecomposition = new RInChIDecomposition(rinchi, rAuxInfo);
        // act
        RInChIDecomposition actual = rInChIDecomposition.decompose();
        // assert
        assertThat(actual.getStatus()).isEqualTo(StatusMessagesOutput.Status.SUCCESS);
        assertThat(actual.getReactionDirection()).isEqualTo(direction);
        assertThat(actual.getComponents()).containsExactlyElementsOf(components);
    }
}