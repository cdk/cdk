/* Copyright (C) 2022  Nikolay Kochev <nick@uni-plovdiv.net>, Uli Fechner
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLRXNV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.test.CDKTestCase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Nikolay Kochev
 * @author Uli Fechner
 */
class RInChIGeneratorTest extends CDKTestCase {

    @Test
    void noStructCountToRInChIKeyChar_countLtZero_test() {
        final RInChIGenerator generator = new RInChIGenerator();
        final Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> generator.noStructCountToRInChIKeyChar(-3));
        Assertions.assertEquals("Negative count of -3 of no-structures.", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"0, Z",
            "1, A",
            "2, B",
            "3, C",
            "4, D",
            "5, E",
            "6, F",
            "7, G",
            "8, H",
            "9, I",
            "10, J",
            "11, K",
            "12, L",
            "13, M",
            "14, N",
            "15, O",
            "16, P",
            "17, Q",
            "18, R",
            "19, S",
            "20, T",
            "21, U",
            "22, V",
            "23, W",
            "24, X",
            "25, Y",
            "26, Y",
            "34, Y"
    })
    void noStructCountToRInChIKeyChar_validValues_test(final int count, final char expected) {
        final RInChIGenerator generator = new RInChIGenerator();
        Assertions.assertEquals(expected, generator.noStructCountToRInChIKeyChar(count));
    }

    @Test
    void isProductsFirst_reactantsFirst_test() {
        // arrange
        final RInChIComponent rInChIComponentReactantOne = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentReactantOne.getInchi()).thenReturn("InChI=1S/Br2/c1-2");
        final RInChIComponent rInChIComponentReactantTwo = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentReactantTwo.getInchi()).thenReturn("InChI=1S/CH4/h1H4");
        final List<RInChIComponent> reactants = new ArrayList<>();
        reactants.add(rInChIComponentReactantOne);
        reactants.add(rInChIComponentReactantTwo);
        final RInChIComponent rInChIComponentProductOne = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentProductOne.getInchi()).thenReturn("InChI=1S/BrH/h1H");
        final RInChIComponent rInChIComponentProductTwo = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentProductTwo.getInchi()).thenReturn("InChI=1S/CH3Br/c1-2/h1H3");
        final List<RInChIComponent> products = new ArrayList<>();
        products.add(rInChIComponentProductOne);
        products.add(rInChIComponentProductTwo);
        final RInChIGenerator generator = new RInChIGenerator();

        // act
        final boolean actual = generator.isProductsFirst(reactants, products);

        // assert
        String[] temp = {"InChI=1S/BrH/h1H", "InChI=1S/Br2/c1-2"};
        Arrays.stream(temp).sorted().forEach(System.out::println);

        assertFalse(actual);
    }

    @Test
    void isProductsFirst_productsFirst_test() {
        // arrange
        final RInChIComponent rInChIComponentReactantOne = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentReactantOne.getInchi()).thenReturn("InChI=1S/XXXX");
        final RInChIComponent rInChIComponentReactantTwo = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentReactantTwo.getInchi()).thenReturn("InChI=1S/YYYY");
        final List<RInChIComponent> reactants = new ArrayList<>();
        reactants.add(rInChIComponentReactantOne);
        reactants.add(rInChIComponentReactantTwo);
        final RInChIComponent rInChIComponentProductOne = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentProductOne.getInchi()).thenReturn("InChI=1S/BBBB");
        final RInChIComponent rInChIComponentProductTwo = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentProductTwo.getInchi()).thenReturn("InChI=1S/CCCC");
        final List<RInChIComponent> products = new ArrayList<>();
        products.add(rInChIComponentProductOne);
        products.add(rInChIComponentProductTwo);
        final RInChIGenerator generator = new RInChIGenerator();

        // act
        final boolean actual = generator.isProductsFirst(reactants, products);

        // assert
        assertTrue(actual);
    }

    @Test
    void isProductsFirst_reactantOneNostruct_test() {
        // arrange
        final RInChIComponent rInChIComponentReactantOne = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentReactantOne.getInchi()).thenReturn(null);
        final RInChIComponent rInChIComponentReactantTwo = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentReactantTwo.getInchi()).thenReturn("InChI=1S/YYYY");
        final List<RInChIComponent> reactants = new ArrayList<>();
        reactants.add(rInChIComponentReactantOne);
        reactants.add(rInChIComponentReactantTwo);
        final RInChIComponent rInChIComponentProductOne = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentProductOne.getInchi()).thenReturn("InChI=1S/BBBB");
        final RInChIComponent rInChIComponentProductTwo = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentProductTwo.getInchi()).thenReturn("InChI=1S/CCCC");
        final List<RInChIComponent> products = new ArrayList<>();
        products.add(rInChIComponentProductOne);
        products.add(rInChIComponentProductTwo);
        final RInChIGenerator generator = new RInChIGenerator();

        // act
        final boolean actual = generator.isProductsFirst(reactants, products);

        // assert
        assertTrue(actual);
    }

    @Test
    void isProductsFirst_productOneNostruct_test() {
        // arrange
        final RInChIComponent rInChIComponentReactantOne = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentReactantOne.getInchi()).thenReturn("InChI=1S/XXXX");
        final RInChIComponent rInChIComponentReactantTwo = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentReactantTwo.getInchi()).thenReturn("InChI=1S/YYYY");
        final List<RInChIComponent> reactants = new ArrayList<>();
        reactants.add(rInChIComponentReactantOne);
        reactants.add(rInChIComponentReactantTwo);
        final RInChIComponent rInChIComponentProductOne = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentProductOne.getInchi()).thenReturn(null);
        final RInChIComponent rInChIComponentProductTwo = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentProductTwo.getInchi()).thenReturn("InChI=1S/CCCC");
        final List<RInChIComponent> products = new ArrayList<>();
        products.add(rInChIComponentProductOne);
        products.add(rInChIComponentProductTwo);
        final RInChIGenerator generator = new RInChIGenerator();

        // act
        final boolean actual = generator.isProductsFirst(reactants, products);

        // assert
        assertTrue(actual);
    }

    @Test
    void isProductsFirst_reactantOneNoStruct_productOneNostruct_test() {
        // arrange
        final RInChIComponent rInChIComponentReactantOne = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentReactantOne.getInchi()).thenReturn(null);
        final RInChIComponent rInChIComponentReactantTwo = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentReactantTwo.getInchi()).thenReturn("InChI=1S/YYYY");
        final List<RInChIComponent> reactants = new ArrayList<>();
        reactants.add(rInChIComponentReactantOne);
        reactants.add(rInChIComponentReactantTwo);
        final RInChIComponent rInChIComponentProductOne = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentProductOne.getInchi()).thenReturn(null);
        final RInChIComponent rInChIComponentProductTwo = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentProductTwo.getInchi()).thenReturn("InChI=1S/CCCC");
        final List<RInChIComponent> products = new ArrayList<>();
        products.add(rInChIComponentProductOne);
        products.add(rInChIComponentProductTwo);
        final RInChIGenerator generator = new RInChIGenerator();

        // act
        final boolean actual = generator.isProductsFirst(reactants, products);

        // assert
        assertTrue(actual);
    }

    @Disabled("Atom 'A' will cause Status: WARNING")
    @Test
    void r01_1_struct_reactant_1_nostruct_product_test() throws Exception {
        // examples/1_reactant_-_A.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r01_1_struct_reactant_1_nostruct_product.rxn", "org.openscience.cdk.rinchi/r01_1_struct_reactant_1_nostruct_product-rinchi.txt");
    }

    @Disabled("RAuxInfo uses wrong decimal delimiter.")
    @Test
    void r02_1_struct_reactant_0_product_test() throws Exception {
        // 1_reactant_-_no_product.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r02_1_struct_reactant_0_product.rxn", "org.openscience.cdk.rinchi/r02_1_struct_reactant_0_product-rinchi.txt");
    }

    @Disabled("Empty structure will cause status: WARNING")
    @Test
    void r03_1_struct_reactant_1_nostruct_product_test() throws Exception {
        // 1_reactant_-_no_structure.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r03_1_struct_reactant_1_nostruct_product.rxn", "org.openscience.cdk.rinchi/r03_1_struct_reactant_1_nostruct_product-rinchi.txt");
    }

    @Disabled("Atom 'R' will cause Status: WARNING")
    @Test
    void r04_1_struct_reactant_1_nostruct_product_test() throws Exception {
        // 1_reactant_-_R.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r04_1_struct_reactant_1_nostruct_product_test.rxn", "org.openscience.cdk.rinchi/r04_1_struct_reactant_1_nostruct_product_test-rinchi.txt");
    }

    @Disabled("RAuxInfo uses wrong decimal delimiter.")
    @Test
    void r05_0_reactant_1_struct_product_test() throws Exception {
        // no_reactants_one_product.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r05_0_reactant_1_struct_product.rxn", "org.openscience.cdk.rinchi/r05_0_reactant_1_struct_product-rinchi.txt");
    }

    @Disabled("Metalatom and Nostruct cause status WARNING.")
    @Test
    void r06_2_struct_reactant_1_struct_product_1_nostruct_product_test() throws Exception {
        // nostruct_one_in_products.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r06_2_struct_reactant_1_struct_product_1_nostruct_product.rxn", "org.openscience.cdk.rinchi/r06_2_struct_reactant_1_struct_product_1_nostruct_product-rinchi.txt");
    }

    @Disabled("Metalatom and Nostruct cause status WARNING.")
    @Test
    void r07_2_struct_reactant_1_nostruct_reactant_1_struct_product_test() throws Exception {
        // nostruct_one_in_reactants.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r07_2_struct_reactant_1_nostruct_reactant_1_struct_product.rxn", "org.openscience.cdk.rinchi/r07_2_struct_reactant_1_nostruct_reactant_1_struct_product-rinchi.txt");
    }

    @Disabled("Metalatom and Nostruct cause status WARNING.")
    @Test
    void r08_2_struct_reactant_2_nostruct_reactant_1_struct_product_test() throws Exception {
        // nostruct_two_in_reactants.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r08_2_struct_reactant_2_nostruct_reactant_1_struct_product.rxn", "org.openscience.cdk.rinchi/r08_2_struct_reactant_2_nostruct_reactant_1_struct_product-rinchi.txt");
    }

    @Disabled("Metalatom causes status WARNING.")
    @Test
    void r09_1_struct_reactant_1_nostruct_reactant_1_struct_product_test() throws Exception {
        // R005a.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r09_1_struct_reactant_1_nostruct_reactant_1_struct_product.rxn", "org.openscience.cdk.rinchi/r09_1_struct_reactant_1_nostruct_reactant_1_struct_product-rinchi.txt");
    }

    @Disabled("RAuxInfo uses wrong decimal delimiter.")
    @Test
    void r10_1_struct_reactant_1_struct_product_test() throws Exception {
        // Tautomerization_01.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r10_1_struct_reactant_1_struct_product.rxn", "org.openscience.cdk.rinchi/r10_1_struct_reactant_1_struct_product-rinchi.txt");
    }

    @Disabled("Undefined stereo causes status WARNING.")
    @Test
    void r11_2_struct_reactant_0_product_test() throws Exception {
        // two_reactants_no_products.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r11_2_struct_reactant_0_product.rxn", "org.openscience.cdk.rinchi/r11_2_struct_reactant_0_product-rinchi.txt");
    }

    @Disabled("R atom and no struct cause status WARNING.")
    @Test
    void r12_1_nostruct_reactant_1_nostruct_product_test() throws Exception {
        // ok__nostruct-A.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r12_1_nostruct_reactant_1_nostruct_product.rxn", "org.openscience.cdk.rinchi/r12_1_nostruct_reactant_1_nostruct_product-rinchi.txt");
    }

    @Disabled("R atoms cause status WARNING.")
    @Test
    void r13_1_nostruct_reactant_1_nostruct_product_test() throws Exception {
        // ok__R-A.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r13_1_nostruct_reactant_1_nostruct_product.rxn", "org.openscience.cdk.rinchi/r13_1_nostruct_reactant_1_nostruct_product-rinchi.txt");
    }

    @Disabled("R atoms cause status WARNING.")
    @Test
    void r14_1_struct_reactant_R_1_struct_product_A_test() throws Exception {
        // err__R_reactant-A_product.rxn
        // This input raises an error in https://iupac-inchi.github.io/InChI-Web-Demo/, so probably okay to assert for error status code from RinchiGenerator
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r14_1_struct_reactant_R_1_struct_product_A.rxn", "org.openscience.cdk.rinchi/r14_1_struct_reactant_R_1_struct_product_A-rinchi.txt");
    }

    @Disabled("RAuxInfo has wrong decimal separator.")
    @Test
    void r15_4_struct_reactant_1_struct_product_test() throws Exception {
        // err__R_reactant-A_product.rxn
        // TODO This input raises an error in https://iupac-inchi.github.io/InChI-Web-Demo/, so should assess here that RinchiGenerator returns a status error
        // If the file is used with the RInChI WebDemo (same url) and the functionality "Convert RXN/RD file to RInChI" is used it returns a valid RInChI without any error
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r15_4_struct_reactant_1_struct_product.rxn", "org.openscience.cdk.rinchi/r15_4_struct_reactant_1_struct_product-rinchi.txt");
    }

    @Disabled("rinchi web demo includes all agents and catalysts, we don't atm, CDK loads 5 structs")
    @Test
    void r16_rinchi_repo_1_variation_4_steps_test() throws Exception {
        // file taken from https://github.com/IUPAC-InChI/RInChI/blob/d122a78457c592b9728906f3c0b565a2c2c5d6dd/src/test/RDfiles/1_variation_4_steps.rdf
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r16_rinchi_repo_1_variation_4_steps.rdf", "org.openscience.cdk.rinchi/r16_rinchi_repo_1_variation_4_steps-rinchi.txt");
    }

    @Disabled("RAuxInfo has wrong decimal separator.")
    @Test
    void r17_rinchi_repo_5_variations_4_step_each_test() throws Exception {
        // file taken from https://github.com/IUPAC-InChI/RInChI/blob/d122a78457c592b9728906f3c0b565a2c2c5d6dd/src/test/RDfiles/1_variation_4_steps.rdf
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r17_rinchi_repo_5_variations_1_step_each.rxn", "org.openscience.cdk.rinchi/r17_rinchi_repo_5_variations_1_step_each-rinchi.txt");
    }

    @Disabled("'R' and undefined stereo cause status WARNING.")
    @Test
    void r18_error_asterisk_reactant_test() throws Exception {
        // err__star_reactant-product.rdf
        // TODO This input raises an error in https://iupac-inchi.github.io/InChI-Web-Demo/, so should assess here that RinchiGenerator returns a status error
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r18_error_asterisk_reactant.rxn", "org.openscience.cdk.rinchi/r18_error_asterisk_reactant-rinchi.txt");
    }

    @Disabled("Undefined stereo causes status WARNING.")
    @Test
    void r19_rinchi_repo_example_01_ccr_test() throws Exception {
        // Example_01_CCR.rdf
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r19_rinchi_repo_example_01_ccr.rxn", "org.openscience.cdk.rinchi/r19_rinchi_repo_example_01_ccr-rinchi.txt");
    }

    @Disabled("RAuxInfo has wrong decimal separator.")
    @Test
    void r20_rinchi_repo_example_03_metab_udm_test() throws Exception {
        // Example_03_metab_UDM.rdf
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r20_rinchi_repo_example_03_metab_udm.rxn", "org.openscience.cdk.rinchi/r20_rinchi_repo_example_03_metab_udm-rinchi.txt");
    }

    @Disabled("agents etc are missing, in the .txt file.")
    @Test
    void r21_rinchi_repo_example_04_simple_test() throws Exception {
        // Example_04_simple.rdf
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r21_rinchi_repo_example_04_simple.rxn", "org.openscience.cdk.rinchi/r21_rinchi_repo_example_04_simple-rinchi.txt");
    }

    @Disabled("agents etc are missing, in the .txt file.")
    @Test
    void r22_rinchi_repo_example_05_groups_udm_test() throws Exception {
        // Example_05_groups_UDM
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r22_rinchi_repo_example_05_groups_udm.rxn", "org.openscience.cdk.rinchi/r22_rinchi_repo_example_05_groups_udm-rinchi.txt");
    }

    @Disabled("org.openscience.cdk.exception.CDKException: invalid symbol: X")
    @Test
    void r23_rinchi_repo_example_05_groups_udm_test() throws Exception {
        // ok__nostruct-X.rdf
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r23_rinchi_repo_1_nostruct_reactant_1_struct_product.rxn", "org.openscience.cdk.rinchi/r23_rinchi_repo_1_nostruct_reactant_1_struct_product-rinchi.txt");
    }

    @Disabled("org.openscience.cdk.exception.CDKException: invalid symbol: X")
    @Test
    void r24_rinchi_repo_1_struct_reactant_R_1_struct_product_X_test() throws Exception {
        // ok__R-X.rdf
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r24_rinchi_repo_1_struct_reactant_R_1_struct_product_X.rxn", "org.openscience.cdk.rinchi/r24_rinchi_repo_1_struct_reactant_R_1_struct_product_X-rinchi.txt");
    }

    @Disabled("'R' atom and empty struct cause status WARNING.")
    @Test
    void r25_rinchi_repo_2_reactant_asterisk_1_nostruct_product_test() throws Exception {
        // ok__star_star-nostruct.rdf
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r25_rinchi_repo_2_reactant_asterisk_1_nostruct_product.rxn", "org.openscience.cdk.rinchi/r25_rinchi_repo_2_reactant_asterisk_1_nostruct_product-rinchi.txt");
    }

    void rxnFileRinchiFullInformationFileTest(final String reactionFile, final String rinchiFile) throws Exception {
        // arrange
        final IReaction reaction = readReactionFromRxnFile(reactionFile);
        final Map<String, String> rinchiFullInformation = readRinchiFullInformationFromResourceFile(rinchiFile);

        // act
        final RInChIGenerator generator = RInChIGeneratorFactory.getInstance().getRInChIGenerator(reaction);

        // assert
        assertNotNull(generator);
        System.out.println(generator.getMessages());
        Assertions.assertEquals(StatusMessagesOutput.Status.SUCCESS, generator.getStatus(), "RInChI status:");
        Assertions.assertEquals(rinchiFullInformation.get("RInChI"), generator.getRInChI(), "RinChI:");
        Assertions.assertEquals(rinchiFullInformation.get("RAuxInfo"), generator.getAuxInfo(), "RAuxInfo:");
        Assertions.assertEquals(rinchiFullInformation.get("Long-RInChIKey"), generator.getLongRInChIKey(), "Long-RInChIKey:");
        Assertions.assertEquals(rinchiFullInformation.get("Short-RInChIKey"), generator.getShortRInChIKey(), "Short-RInChIKey:");
        Assertions.assertEquals(rinchiFullInformation.get("Web-RInChIKey"), generator.getWebRInChIKey(), "Web-RInChIKey:");
    }

    private IReaction readReactionFromRxnFile(String filename) throws Exception {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filename)) {

            final MDLRXNV2000Reader reader = new MDLRXNV2000Reader(inputStream, Mode.STRICT);
            return reader.read(SilentChemObjectBuilder.getInstance().newReaction());
        }
    }

    private Map<String, String> readRinchiFullInformationFromResourceFile(final String filename) throws IOException, URISyntaxException {
        final String[] rinchiPrefixes = new String[]{"RInChI", "RAuxInfo", "Long-RInChIKey", "Short-RInChIKey", "Web-RInChIKey"};
        final Map<String, String> rinchiFullInformation = new HashMap<>();

        final URL resource = this.getClass().getClassLoader().getResource(filename);
        assertNotNull(resource, String.format("File %s not found in classpath!", filename));
        final Path path = Paths.get(resource.toURI());
        final List<String> lines = Files.readAllLines(path);

        for (final String line : lines) {
            for (final String rinchiPrefix : rinchiPrefixes) {
                if (line.startsWith(rinchiPrefix + "=")) {
                    rinchiFullInformation.put(rinchiPrefix, line);
                }
            }
        }

        return rinchiFullInformation;
    }

}