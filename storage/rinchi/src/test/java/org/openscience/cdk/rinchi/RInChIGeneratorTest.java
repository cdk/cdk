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
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLRXNV2000Reader;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.io.RdfileReader;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.openscience.cdk.rinchi.RInChIGeneratorTest.Messages.ELEMENT_R_NOT_RECOGNISED;
import static org.openscience.cdk.rinchi.RInChIGeneratorTest.Messages.EMPTY_STRUCTURE;
import static org.openscience.cdk.rinchi.StatusMessagesOutput.Status.*;

/**
 * @author Nikolay Kochev
 * @author Uli Fechner
 * @cdk.module test-rinchi
 */
class RInChIGeneratorTest extends CDKTestCase {

    enum Messages {
        ELEMENT_R_NOT_RECOGNISED("InChIGenerator did not return status success: Element name R is not recognised."),
        EMPTY_STRUCTURE("InChIGenerator did not return status success: Empty structure.");

        private final String text;

        Messages(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    private static final IChemObjectBuilder BUILDER = SilentChemObjectBuilder.getInstance();

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
        assertThat(actual).isFalse();
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
        assertThat(actual).isTrue();
    }

    @Test
    void isProductsFirst_reactantOneNostruct_test() {
        // arrange
        final RInChIComponent rInChIComponentReactantOne = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentReactantOne.isNoStructure()).thenReturn(true);
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
        assertThat(actual).isTrue();
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
        assertThat(actual).isTrue();
    }

    @Test
    void isProductsFirst_reactantOneNoStruct_productOneNostruct_test() {
        // arrange
        final RInChIComponent rInChIComponentReactantOne = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentReactantOne.isNoStructure()).thenReturn(true);
        final RInChIComponent rInChIComponentReactantTwo = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentReactantTwo.getInchi()).thenReturn("InChI=1S/YYYY");
        final List<RInChIComponent> reactants = new ArrayList<>();
        reactants.add(rInChIComponentReactantOne);
        reactants.add(rInChIComponentReactantTwo);
        final RInChIComponent rInChIComponentProductOne = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentProductOne.isNoStructure()).thenReturn(true);
        final RInChIComponent rInChIComponentProductTwo = Mockito.mock(RInChIComponent.class);
        Mockito.when(rInChIComponentProductTwo.getInchi()).thenReturn("InChI=1S/CCCC");
        final List<RInChIComponent> products = new ArrayList<>();
        products.add(rInChIComponentProductOne);
        products.add(rInChIComponentProductTwo);
        final RInChIGenerator generator = new RInChIGenerator();

        // act
        final boolean actual = generator.isProductsFirst(reactants, products);

        // assert
        assertThat(actual).isTrue();
    }

    @Test
    void benzene_kekulized_test() {
        //Create kekulized benzene
        IAtomContainer molecule = BUILDER.newAtomContainer();
        IAtom atom0 = SilentChemObjectBuilder.getInstance().newAtom();
        atom0.setAtomicNumber(6);
        atom0.setImplicitHydrogenCount(1);
        atom0.setIsAromatic(true);
        molecule.addAtom(atom0);
        IAtom atom1 = SilentChemObjectBuilder.getInstance().newAtom();
        atom1.setAtomicNumber(6);
        atom1.setImplicitHydrogenCount(1);
        atom1.setIsAromatic(true);
        molecule.addAtom(atom1);
        IAtom atom2 = SilentChemObjectBuilder.getInstance().newAtom();
        atom2.setAtomicNumber(6);
        atom2.setImplicitHydrogenCount(1);
        atom2.setIsAromatic(true);
        molecule.addAtom(atom2);
        IAtom atom3 = SilentChemObjectBuilder.getInstance().newAtom();
        atom3.setAtomicNumber(6);
        atom3.setImplicitHydrogenCount(1);
        atom3.setIsAromatic(true);
        molecule.addAtom(atom3);
        IAtom atom4 = SilentChemObjectBuilder.getInstance().newAtom();
        atom4.setAtomicNumber(6);
        atom4.setImplicitHydrogenCount(1);
        atom4.setIsAromatic(true);
        molecule.addAtom(atom4);
        IAtom atom5 = SilentChemObjectBuilder.getInstance().newAtom();
        atom5.setAtomicNumber(6);
        atom5.setImplicitHydrogenCount(1);
        atom5.setIsAromatic(true);
        molecule.addAtom(atom5);
        IBond bond0 = new Bond(atom0 ,atom1 ,IBond.Order.DOUBLE);
        molecule.addBond(bond0);
        IBond bond1 = new Bond(atom1 ,atom2 ,IBond.Order.SINGLE);
        molecule.addBond(bond1);
        IBond bond2 = new Bond(atom2 ,atom3 ,IBond.Order.DOUBLE);
        molecule.addBond(bond2);
        IBond bond3 = new Bond(atom3 ,atom4 ,IBond.Order.SINGLE);
        molecule.addBond(bond3);
        IBond bond4 = new Bond(atom4 ,atom5 ,IBond.Order.DOUBLE);
        molecule.addBond(bond4);
        IBond bond5 = new Bond(atom0 ,atom5 ,IBond.Order.SINGLE);
        molecule.addBond(bond5);

        //Create reaction and set benzene as a reagent
        IReaction reaction = BUILDER.newReaction();
        reaction.addReactant(molecule);

        //Generate RInChI
        RInChIGenerator generator = new RInChIGenerator().generate(reaction);
        assertThat(generator.getStatus()).isEqualTo(StatusMessagesOutput.Status.SUCCESS);
        assertThat(generator.getRInChI()).isEqualTo("RInChI=1.00.1S/<>C6H6/c1-2-4-6-5-3-1/h1-6H/d-");
        assertThat(generator.getLongRInChIKey()).isEqualTo("Long-RInChIKey=SA-BUHFF---UHOVQNZJYSORNB-UHFFFAOYSA-N");
        assertThat(generator.getShortRInChIKey()).isEqualTo("Short-RInChIKey=SA-BUHFF-UHFFFADPSC-UHOVQNZJYS-UHFFFADPSC-NUHFF-NUHFF-NUHFF-ZZZ");
        assertThat(generator.getWebRInChIKey()).isEqualTo("Web-RInChIKey=UHOVQNZJYSORNBOAP-NUHFFFADPSCTJSA");
    }

    @Test
    void benzene_aromatized_test() {
        //Create aromatic benzene for testing conversion of CDK bonds of type UNSET flagged as aromatic
        IAtomContainer molecule = BUILDER.newAtomContainer();
        IAtom atom0 = SilentChemObjectBuilder.getInstance().newAtom();
        atom0.setAtomicNumber(6);
        atom0.setImplicitHydrogenCount(1);
        atom0.setIsAromatic(true);
        molecule.addAtom(atom0);
        IAtom atom1 = SilentChemObjectBuilder.getInstance().newAtom();
        atom1.setAtomicNumber(6);
        atom1.setImplicitHydrogenCount(1);
        atom1.setIsAromatic(true);
        molecule.addAtom(atom1);
        IAtom atom2 = SilentChemObjectBuilder.getInstance().newAtom();
        atom2.setAtomicNumber(6);
        atom2.setImplicitHydrogenCount(1);
        atom2.setIsAromatic(true);
        molecule.addAtom(atom2);
        IAtom atom3 = SilentChemObjectBuilder.getInstance().newAtom();
        atom3.setAtomicNumber(6);
        atom3.setImplicitHydrogenCount(1);
        atom3.setIsAromatic(true);
        molecule.addAtom(atom3);
        IAtom atom4 = SilentChemObjectBuilder.getInstance().newAtom();
        atom4.setAtomicNumber(6);
        atom4.setImplicitHydrogenCount(1);
        atom4.setIsAromatic(true);
        molecule.addAtom(atom4);
        IAtom atom5 = SilentChemObjectBuilder.getInstance().newAtom();
        atom5.setAtomicNumber(6);
        atom5.setImplicitHydrogenCount(1);
        atom5.setIsAromatic(true);
        molecule.addAtom(atom5);
        IBond bond0 = new Bond(atom0 ,atom1 ,IBond.Order.UNSET);
        bond0.setIsAromatic(true);
        molecule.addBond(bond0);
        IBond bond1 = new Bond(atom1 ,atom2 ,IBond.Order.UNSET);
        bond1.setIsAromatic(true);
        molecule.addBond(bond1);
        IBond bond2 = new Bond(atom2 ,atom3 ,IBond.Order.UNSET);
        bond2.setIsAromatic(true);
        molecule.addBond(bond2);
        IBond bond3 = new Bond(atom3 ,atom4 ,IBond.Order.UNSET);
        bond3.setIsAromatic(true);
        molecule.addBond(bond3);
        IBond bond4 = new Bond(atom4 ,atom5 ,IBond.Order.UNSET);
        bond4.setIsAromatic(true);
        molecule.addBond(bond4);
        IBond bond5 = new Bond(atom0 ,atom5 ,IBond.Order.UNSET);
        bond5.setIsAromatic(true);
        molecule.addBond(bond5);

        //Create reaction and set benzene as a reagent
        IReaction reaction = BUILDER.newReaction();
        reaction.addReactant(molecule);

        //Generate RInChI
        RInChIGenerator generator = new RInChIGenerator().generate(reaction);
        // since jna-inchi does not support IBond.Order.UNSET this will always fail
        assertThat(generator.getMessages().get(0)).isEqualTo("Unable to extract components from given reaction: Failed to generate InChI: Unsupported bond type");
        assertThat(generator.getStatus()).isEqualTo(StatusMessagesOutput.Status.ERROR);
    }

    @Test
    void radical_doublet_test() {
        //Create propane doublet radical (monovalent)
        IAtomContainer molecule = BUILDER.newAtomContainer();
        IAtom atom0 = SilentChemObjectBuilder.getInstance().newAtom();
        atom0.setAtomicNumber(6);
        atom0.setImplicitHydrogenCount(2);
        molecule.addAtom(atom0);
        IAtom atom1 = SilentChemObjectBuilder.getInstance().newAtom();
        atom1.setAtomicNumber(6);
        atom1.setImplicitHydrogenCount(2);
        molecule.addAtom(atom1);
        IAtom atom2 = SilentChemObjectBuilder.getInstance().newAtom();
        atom2.setAtomicNumber(6);
        atom2.setImplicitHydrogenCount(3);
        molecule.addAtom(atom2);
        IBond bond0 = new Bond(atom0 ,atom1 ,IBond.Order.SINGLE);
        molecule.addBond(bond0);
        IBond bond1 = new Bond(atom1 ,atom2 ,IBond.Order.SINGLE);
        molecule.addBond(bond1);
        //Set radical info
        atom0.setProperty(CDKConstants.SPIN_MULTIPLICITY, MDLV2000Writer.SPIN_MULTIPLICITY.Monovalent);
        molecule.addSingleElectron(0);

        //Create reaction and set propane as a reagent
        IReaction reaction = BUILDER.newReaction();
        reaction.addReactant(molecule);

        //Generate RInChI
        RInChIGenerator generator = new RInChIGenerator().generate(reaction);
        assertThat(generator.getStatus()).isEqualTo(StatusMessagesOutput.Status.SUCCESS);
        assertThat(generator.getRInChI()).isEqualTo("RInChI=1.00.1S/<>C3H7/c1-3-2/h1,3H2,2H3/d-");
        assertThat(generator.getAuxInfo()).isEqualTo("RAuxInfo=1.00.1/<>0/N:1,3,2/CRV:1d/rA:3nC.2CC/rB:s1;s2;/rC:;;;");
    }

    @Test
    void radical_triplet_test() {
        //Create propane triple radical (divalent)
        //!!! propane singlet radical produces the same RAuxInfo (bug or feature in RInChI - unknown ??)
        IAtomContainer molecule = BUILDER.newAtomContainer();
        IAtom atom0 = SilentChemObjectBuilder.getInstance().newAtom();
        atom0.setAtomicNumber(6);
        atom0.setImplicitHydrogenCount(1);
        molecule.addAtom(atom0);
        IAtom atom1 = SilentChemObjectBuilder.getInstance().newAtom();
        atom1.setAtomicNumber(6);
        atom1.setImplicitHydrogenCount(2);
        molecule.addAtom(atom1);
        IAtom atom2 = SilentChemObjectBuilder.getInstance().newAtom();
        atom2.setAtomicNumber(6);
        atom2.setImplicitHydrogenCount(3);
        molecule.addAtom(atom2);
        IBond bond0 = new Bond(atom0 ,atom1 ,IBond.Order.SINGLE);
        molecule.addBond(bond0);
        IBond bond1 = new Bond(atom1 ,atom2 ,IBond.Order.SINGLE);
        molecule.addBond(bond1);
        //Set radical info
        atom0.setProperty(CDKConstants.SPIN_MULTIPLICITY, MDLV2000Writer.SPIN_MULTIPLICITY.DivalentTriplet);
        molecule.addSingleElectron(0);
        molecule.addSingleElectron(0);

        //Create reaction and set propane as a reagent
        IReaction reaction = SilentChemObjectBuilder.getInstance().newReaction();
        reaction.addReactant(molecule);

        //Generate RInChI
        RInChIGenerator generator = new RInChIGenerator().generate(reaction);
        assertThat(generator.getStatus()).isEqualTo(StatusMessagesOutput.Status.SUCCESS);
        assertThat(generator.getRInChI()).isEqualTo("RInChI=1.00.1S/<>C3H6/c1-3-2/h1H,3H2,2H3/d-");
        assertThat(generator.getAuxInfo()).isEqualTo("RAuxInfo=1.00.1/<>0/N:1,3,2/CRV:1t/rA:3nC.3CC/rB:s1;s2;/rC:;;;");
    }

    @Test
    void test01() {
        //Create Diels–Alder Reaction
        //Reactant 1
        IAtomContainer molecule1 = BUILDER.newAtomContainer();
        IAtom reactantAtom1 = SilentChemObjectBuilder.getInstance().newAtom();
        reactantAtom1.setAtomicNumber(6);
        IAtom reactantAtom2 = SilentChemObjectBuilder.getInstance().newAtom();
        reactantAtom2.setAtomicNumber(6);
        molecule1.addAtom(reactantAtom1);
        molecule1.addAtom(reactantAtom2);
        molecule1.addBond(new Bond(reactantAtom1, reactantAtom2, IBond.Order.DOUBLE));
        //Reactant 2
        IAtomContainer molecule2 = BUILDER.newAtomContainer();
        IAtom reactantAtom3 = SilentChemObjectBuilder.getInstance().newAtom();
        reactantAtom3.setAtomicNumber(6);
        IAtom reactantAtom4 = SilentChemObjectBuilder.getInstance().newAtom();
        reactantAtom4.setAtomicNumber(6);
        IAtom reactantAtom5 = SilentChemObjectBuilder.getInstance().newAtom();
        reactantAtom5.setAtomicNumber(6);
        IAtom reactantAtom6 = SilentChemObjectBuilder.getInstance().newAtom();
        reactantAtom6.setAtomicNumber(6);
        molecule2.addAtom(reactantAtom3);
        molecule2.addAtom(reactantAtom4);
        molecule2.addAtom(reactantAtom5);
        molecule2.addAtom(reactantAtom6);
        molecule2.addBond(new Bond (reactantAtom3, reactantAtom4, IBond.Order.DOUBLE));
        molecule2.addBond(new Bond (reactantAtom4, reactantAtom5, IBond.Order.SINGLE));
        molecule2.addBond(new Bond (reactantAtom5, reactantAtom6, IBond.Order.DOUBLE));
        //Product
        IAtomContainer molecule3 = BUILDER.newAtomContainer();
        IAtom productAtom1 = SilentChemObjectBuilder.getInstance().newAtom();
        productAtom1.setAtomicNumber(6);
        IAtom productAtom2 = SilentChemObjectBuilder.getInstance().newAtom();
        productAtom2.setAtomicNumber(6);
        IAtom productAtom3 = SilentChemObjectBuilder.getInstance().newAtom();
        productAtom3.setAtomicNumber(6);
        IAtom productAtom4 = SilentChemObjectBuilder.getInstance().newAtom();
        productAtom4.setAtomicNumber(6);
        IAtom productAtom5 = SilentChemObjectBuilder.getInstance().newAtom();
        productAtom5.setAtomicNumber(6);
        IAtom productAtom6 = SilentChemObjectBuilder.getInstance().newAtom();
        productAtom6.setAtomicNumber(6);
        molecule3.addAtom(productAtom1);
        molecule3.addAtom(productAtom2);
        molecule3.addAtom(productAtom3);
        molecule3.addAtom(productAtom4);
        molecule3.addAtom(productAtom5);
        molecule3.addAtom(productAtom6);
        molecule3.addBond(new Bond (productAtom1, productAtom2, IBond.Order.DOUBLE));
        molecule3.addBond(new Bond (productAtom2, productAtom3, IBond.Order.SINGLE));
        molecule3.addBond(new Bond (productAtom3, productAtom4, IBond.Order.SINGLE));
        molecule3.addBond(new Bond (productAtom4, productAtom5, IBond.Order.SINGLE));
        molecule3.addBond(new Bond (productAtom5, productAtom6, IBond.Order.SINGLE));
        molecule3.addBond(new Bond (productAtom6, productAtom1, IBond.Order.SINGLE));

        //Create reaction and set reagents and products
        IReaction reaction = BUILDER.newReaction();
        reaction.addReactant(molecule1);
        reaction.addReactant(molecule2);
        reaction.addProduct(molecule3);

        //Generate RInChI
        RInChIGenerator generator1 = new RInChIGenerator().generate(reaction);
        assertThat(generator1.getStatus()).isEqualTo(StatusMessagesOutput.Status.SUCCESS);
        assertThat(generator1.getRInChI()).endsWith("/d+");

        //Generate RInChI with option ForceEquilibrium
        RInChIGenerator generatorEquilibrium = new RInChIGenerator(RInChIOptions.builder().forceEquilibrium(true).build()).generate(reaction);
        assertThat(generatorEquilibrium.getRInChI()).endsWith("/d=");

        //Create reverse reaction and generate RInChI
        IReaction reaction2 = BUILDER.newReaction();
        reaction2.addReactant(molecule3);
        reaction2.addProduct(molecule1);
        reaction2.addProduct(molecule2);
        RInChIGenerator generator2 = new RInChIGenerator().generate(reaction2);
        assertThat(generator2.getRInChI()).endsWith("/d-");

        //Backward, forward and equilibrium RInChIs differ only in their last char
        int n = generator1.getRInChI().length();
        assertThat(generator1.getRInChI().substring(0, n-1)).isEqualTo(generator2.getRInChI().substring(0, n-1));
        assertThat(generator1.getRInChI().substring(0,n-1)).isEqualTo(generatorEquilibrium.getRInChI().substring(0,n-1));

        //Backward, forward and equilibrium RInChIs-Keys differ only in their 19-th char
        assertThat(generator1.getLongRInChIKey().substring(0, 18)).isEqualTo(generator2.getLongRInChIKey().substring(0, 18));
        assertThat(generator1.getLongRInChIKey().substring(19)).isEqualTo(generator2.getLongRInChIKey().substring(19));
        assertThat(generator1.getLongRInChIKey().charAt(18)).isEqualTo('F');
        assertThat(generatorEquilibrium.getLongRInChIKey().charAt(18)).isEqualTo('E');
        assertThat(generator2.getLongRInChIKey().charAt(18)).isEqualTo('B');
    }

    @Test
    void r01_1_struct_reactant_1_nostruct_product_test() throws Exception {
        // examples/1_reactant_-_A.rxn
        // Note: Atom type 'A' will set status to WARNING, RInChI does not log this.
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r01_1_struct_reactant_1_nostruct_product.rxn",
                "org.openscience.cdk.rinchi/r01_1_struct_reactant_1_nostruct_product-rinchi.txt",
                WARNING,
                Collections.singletonList(ELEMENT_R_NOT_RECOGNISED.toString()));
    }

    @Test
    void r02_1_struct_reactant_0_product_test() throws Exception {
        // 1_reactant_-_no_product.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r02_1_struct_reactant_0_product.rxn", "org.openscience.cdk.rinchi/r02_1_struct_reactant_0_product-rinchi.txt");
    }

    @Test
    void r03_1_struct_reactant_1_nostruct_product_test() throws Exception {
        // 1_reactant_-_no_structure.rxn
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r03_1_struct_reactant_1_nostruct_product.rxn",
                "org.openscience.cdk.rinchi/r03_1_struct_reactant_1_nostruct_product-rinchi.txt",
                WARNING,
                Collections.singletonList(EMPTY_STRUCTURE.toString()));
    }

    @Test
    void r04_1_struct_reactant_1_nostruct_product_test() throws Exception {
        // 1_reactant_-_R.rxn
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r04_1_struct_reactant_1_nostruct_product_test.rxn",
                "org.openscience.cdk.rinchi/r04_1_struct_reactant_1_nostruct_product_test-rinchi.txt",
                WARNING,
                Collections.singletonList(ELEMENT_R_NOT_RECOGNISED.toString()));
    }

    @Test
    void r05_0_reactant_1_struct_product_test() throws Exception {
        // no_reactants_one_product.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r05_0_reactant_1_struct_product.rxn", "org.openscience.cdk.rinchi/r05_0_reactant_1_struct_product-rinchi.txt");
    }

    @Disabled("AND enantiomer with stereocenters set to undefined in RAuxInfo.")
    @Test
    void r06_2_struct_reactant_1_struct_product_1_nostruct_product_test() throws Exception {
        // nostruct_one_in_products.rxn
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r06_2_struct_reactant_1_struct_product_1_nostruct_product.rxn",
                "org.openscience.cdk.rinchi/r06_2_struct_reactant_1_struct_product_1_nostruct_product-rinchi.txt",
                WARNING,
                Collections.singletonList(EMPTY_STRUCTURE.toString()));
    }

    @Disabled("AND enantiomer with stereocenters set to undefined in RAuxInfo.")
    @Test
    void r07_2_struct_reactant_1_nostruct_reactant_1_struct_product_test() throws Exception {
        // nostruct_one_in_reactants.rxn
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r07_2_struct_reactant_1_nostruct_reactant_1_struct_product.rxn",
                "org.openscience.cdk.rinchi/r07_2_struct_reactant_1_nostruct_reactant_1_struct_product-rinchi.txt",
                WARNING,
                Collections.singletonList(EMPTY_STRUCTURE.text));
    }

    @Disabled("AND enantiomer with stereocenters set to undefined in RAuxInfo.")
    @Test
    void r08_2_struct_reactant_2_nostruct_reactant_1_struct_product_test() throws Exception {
        // nostruct_two_in_reactants.rxn
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r08_2_struct_reactant_2_nostruct_reactant_1_struct_product.rxn",
                "org.openscience.cdk.rinchi/r08_2_struct_reactant_2_nostruct_reactant_1_struct_product-rinchi.txt",
                WARNING,
                Arrays.asList(EMPTY_STRUCTURE.toString(), EMPTY_STRUCTURE.toString()));
    }

    @Disabled("AND enantiomer with stereocenters set to undefined in RAuxInfo.")
    @Test
    void r09_1_struct_reactant_1_nostruct_reactant_1_struct_product_test() throws Exception {
        // R005a.rxn
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r09_1_struct_reactant_1_nostruct_reactant_1_struct_product.rxn",
                "org.openscience.cdk.rinchi/r09_1_struct_reactant_1_nostruct_reactant_1_struct_product-rinchi.txt");
    }

    @Test
    void r10_1_struct_reactant_1_struct_product_test() throws Exception {
        // Tautomerization_01.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r10_1_struct_reactant_1_struct_product.rxn", "org.openscience.cdk.rinchi/r10_1_struct_reactant_1_struct_product-rinchi.txt");
    }

    @Test
    void r11_2_struct_reactant_0_product_test() throws Exception {
        // two_reactants_no_products.rxn
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r11_2_struct_reactant_0_product.rxn",
                "org.openscience.cdk.rinchi/r11_2_struct_reactant_0_product-rinchi.txt");
    }

    @Test
    void r12_1_nostruct_reactant_1_nostruct_product_test() throws Exception {
        // ok__nostruct-A.rxn
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r12_1_nostruct_reactant_1_nostruct_product.rxn",
                "org.openscience.cdk.rinchi/r12_1_nostruct_reactant_1_nostruct_product-rinchi.txt",
                WARNING,
                Arrays.asList(ELEMENT_R_NOT_RECOGNISED.toString(), EMPTY_STRUCTURE.toString()));
    }

    @Test
    void r13_1_nostruct_reactant_1_nostruct_product_test() throws Exception {
        // ok__R-A.rxn
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r13_1_nostruct_reactant_1_nostruct_product.rxn",
                "org.openscience.cdk.rinchi/r13_1_nostruct_reactant_1_nostruct_product-rinchi.txt",
                WARNING,
                Arrays.asList(ELEMENT_R_NOT_RECOGNISED.toString(), ELEMENT_R_NOT_RECOGNISED.toString())); // note: two R atoms cause 2 messages
    }

    @Disabled("This input raises an error in https://iupac-inchi.github.io/InChI-Web-Demo/ and thus ends up with no RInChI, RAuxInfo, RInChI keys.")
    @Test
    void r14_1_struct_reactant_R_1_struct_product_A_test() throws Exception {
        // err__R_reactant-A_product.rxn
        // individual structures of reaction returns no inchis, which leads to a reaction with two no structs and in our code an empty Rinchi (RInChI=1.00.1S//d+/u1-1-0)
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r14_1_struct_reactant_R_1_struct_product_A.rxn",
                "org.openscience.cdk.rinchi/r14_1_struct_reactant_R_1_struct_product_A-rinchi.txt",
                ERROR,
                Arrays.asList(ELEMENT_R_NOT_RECOGNISED.toString(), ELEMENT_R_NOT_RECOGNISED.toString())); // note: two R atoms cause 2 messages
    }

    @Test
    void r15_4_struct_reactant_1_struct_product_test() throws Exception {
        // err__R_reactant-A_product.rxn
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r15_4_struct_reactant_1_struct_product.rxn", "org.openscience.cdk.rinchi/r15_4_struct_reactant_1_struct_product-rinchi.txt");
    }

    @Disabled("rinchi web demo includes all agents and catalysts, we don't atm, CDK loads 5 structs.")
    @Test
    void r16_rinchi_repo_1_variation_4_steps_test() throws Exception {
        // file taken from https://github.com/IUPAC-InChI/RInChI/blob/d122a78457c592b9728906f3c0b565a2c2c5d6dd/src/test/RDfiles/1_variation_4_steps.rdf
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r16_rinchi_repo_1_variation_4_steps.rdf", "org.openscience.cdk.rinchi/r16_rinchi_repo_1_variation_4_steps-rinchi.txt");
    }

    @Test
    void r17_rinchi_repo_5_variations_4_step_each_test() throws Exception {
        // file taken from https://github.com/IUPAC-InChI/RInChI/blob/d122a78457c592b9728906f3c0b565a2c2c5d6dd/src/test/RDfiles/1_variation_4_steps.rdf
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r17_rinchi_repo_5_variations_1_step_each.rxn", "org.openscience.cdk.rinchi/r17_rinchi_repo_5_variations_1_step_each-rinchi.txt");
    }

    @Disabled("This input raises an error in https://iupac-inchi.github.io/InChI-Web-Demo/ and thus ends up with no RInChI, RAuxInfo, RInChI keys.")
    @Test
    void r18_error_asterisk_reactant_test() throws Exception {
        // err__star_reactant-product.rdf
        // Asterisk in first structure causes error, it is somewhat strange that this causes an error although the second structure is fine
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r18_error_asterisk_reactant.rxn",
                "org.openscience.cdk.rinchi/r18_error_asterisk_reactant-rinchi.txt",
                WARNING,
                Collections.singletonList(ELEMENT_R_NOT_RECOGNISED.toString()));
    }

    @Disabled("RInChI only reads 1 reactant and 1 product, whereas MDLRXNV2000Reader gets 2 additional agents.")
    @Test
    void r19_rinchi_repo_example_01_ccr_test() throws Exception {
        // Example_01_CCR.rdf
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r19_rinchi_repo_example_01_ccr.rxn",
                "org.openscience.cdk.rinchi/r19_rinchi_repo_example_01_ccr-rinchi.txt");
    }

    @Test
    void r20_rinchi_repo_example_03_metab_udm_test() throws Exception {
        // Example_03_metab_UDM.rdf
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r20_rinchi_repo_example_03_metab_udm.rxn", "org.openscience.cdk.rinchi/r20_rinchi_repo_example_03_metab_udm-rinchi.txt");
    }

    @Disabled("RInChI only reads 1 reactant and 1 product, whereas MDLRXNV2000Reader gets 2 additional agents.")
    @Test
    void r21_rinchi_repo_example_04_simple_test() throws Exception {
        // Example_04_simple.rdf
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r21_rinchi_repo_example_04_simple.rxn", "org.openscience.cdk.rinchi/r21_rinchi_repo_example_04_simple-rinchi.txt");
    }

    @Disabled("RInChI only reads 1 reactant and 1 product, whereas MDLRXNV2000Reader gets additional agents.")
    @Test
    void r22_rinchi_repo_example_05_groups_udm_test() throws Exception {
        // Example_05_groups_UDM
        // diff in rxn file, only reactants and agents in line 5
        rxnFileRinchiFullInformationFileTest("org.openscience.cdk.rinchi/r22_rinchi_repo_example_05_groups_udm.rxn", "org.openscience.cdk.rinchi/r22_rinchi_repo_example_05_groups_udm-rinchi.txt");
    }

    @Disabled("org.openscience.cdk.exception.CDKException: invalid symbol: X")
    @Test
    void r23_rinchi_repo_1_nostruct_reactant_1_struct_product_test() throws Exception {
        // ok__nostruct-X.rdf
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r23_rinchi_repo_1_nostruct_reactant_1_struct_product.rxn",
                "org.openscience.cdk.rinchi/r23_rinchi_repo_1_nostruct_reactant_1_struct_product-rinchi.txt"
        );
    }

    @Disabled("org.openscience.cdk.exception.CDKException: invalid symbol: X")
    @Test
    void r24_rinchi_repo_1_struct_reactant_R_1_struct_product_X_test() throws Exception {
        // ok__R-X.rdf
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r24_rinchi_repo_1_struct_reactant_R_1_struct_product_X.rxn",
                "org.openscience.cdk.rinchi/r24_rinchi_repo_1_struct_reactant_R_1_struct_product_X-rinchi.txt"
        );
    }

    @Test
    void r25_rinchi_repo_2_reactant_asterisk_1_nostruct_product_test() throws Exception {
        // ok__star_star-nostruct.rdf
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r25_rinchi_repo_2_reactant_asterisk_1_nostruct_product.rxn",
                "org.openscience.cdk.rinchi/r25_rinchi_repo_2_reactant_asterisk_1_nostruct_product-rinchi.txt",
                WARNING,
                Arrays.asList(ELEMENT_R_NOT_RECOGNISED.toString(), ELEMENT_R_NOT_RECOGNISED.toString(), EMPTY_STRUCTURE.toString()));
    }


    @Test
    void r26_1_nostruct_reactant_1_nostruct_product_1_nostruct_agent_test() throws Exception {
        // ok__star_star-nostruct.rdf
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r26_1_nostruct_reactant_1_nostruct_product_1_nostruct_agent_test.rxn",
                "org.openscience.cdk.rinchi/r26_1_nostruct_reactant_1_nostruct_product_1_nostruct_agent_test.txt",
                WARNING,
                Arrays.asList(ELEMENT_R_NOT_RECOGNISED.toString(), ELEMENT_R_NOT_RECOGNISED.toString(), ELEMENT_R_NOT_RECOGNISED.toString()));
    }

    @Test
    void r27_uspto_1976_US03930949_16_test() throws Exception {
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r27_uspto_1976_US03930949_16_test.rxn",
                "org.openscience.cdk.rinchi/r27_uspto_1976_US03930949_16_test.txt"
        );
    }

    @Test
    void r28_test() throws Exception {
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r28.rxn",
                "org.openscience.cdk.rinchi/r28.txt"
        );
    }

    @Test
    void r29_forceEquilibrium_test() throws Exception {
        // Cambridge_rxnfiles/appel.rxn
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r29_forceEquilibrium.rxn",
                "org.openscience.cdk.rinchi/r29_forceEquilibrium.txt",
                SUCCESS,
                new ArrayList<>(),
                RInChIOptions.builder().forceEquilibrium(true).timeoutMillisecondsPerComponent(1000).build()
        );
    }

    @Disabled("RAuxInfo difference: rinchi web demo /rA:9nCCCOCOOOO vs CDK /rA:9nC.?CCOCOOOO")
    @Test
    void r30_malate_oxaloacetate_notChiral_forceEquilibrium_test() throws Exception {
        // Cambridge_rxnfiles/appel.rxn
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r30_malate_oxaloacetate_notChiral_forceEquilibrium.rxn",
                "org.openscience.cdk.rinchi/r30_malate_oxaloacetate_notChiral_forceEquilibrium.txt",
                SUCCESS,
                new ArrayList<>(),
                RInChIOptions.builder().forceEquilibrium(true).timeoutMillisecondsPerComponent(1000).build()
        );
    }

    @Disabled("RAuxInfo difference: rinchi web demo //rA:9cCCCOCOOOO vs CDK /rA:9nC.eCCOCOOOO")
    @Test
    void r31_malate_oxaloacetate_chiral_forceEquilibrium_test() throws Exception {
        // Cambridge_rxnfiles/appel.rxn
        rxnFileRinchiFullInformationFileTest(
                "org.openscience.cdk.rinchi/r31_malate_oxaloacetate_chiral_forceEquilibrium.rxn",
                "org.openscience.cdk.rinchi/r31_malate_oxaloacetate_chiral_forceEquilibrium.txt",
                SUCCESS,
                new ArrayList<>(),
                RInChIOptions.builder().forceEquilibrium(true).timeoutMillisecondsPerComponent(1000).build()
        );
    }

    void rxnFileRinchiFullInformationFileTest(final String reactionFile, final String rinchiFile) throws Exception {
        rxnFileRinchiFullInformationFileTest(reactionFile, rinchiFile, SUCCESS, new ArrayList<>());
    }
    
    void rxnFileRinchiFullInformationFileTest(final String reactionFile, final String rinchiFile, StatusMessagesOutput.Status status, List<String> messages) throws Exception {
        rxnFileRinchiFullInformationFileTest(reactionFile, rinchiFile, status, messages, RInChIOptions.DEFAULT_OPTIONS);
    }

    void rxnFileRinchiFullInformationFileTest(final String reactionFile, final String rinchiFile, StatusMessagesOutput.Status status, List<String> messages, RInChIOptions options) throws Exception {
        // arrange
        final IReaction reaction = readReactionFromRxnFile(reactionFile);
        final Map<String, String> rinchiFullInformation = readRinchiFullInformationFromResourceFile(rinchiFile);

        // act
        final RInChIGenerator generator = new RInChIGenerator(options).generate(reaction);

        // assert
        assertThat(generator).isNotNull();
        Assertions.assertEquals(rinchiFullInformation.get("RInChI"), generator.getRInChI(), "RinChI:");
        Assertions.assertEquals(rinchiFullInformation.get("RAuxInfo"), generator.getAuxInfo(), "RAuxInfo:");
        Assertions.assertEquals(rinchiFullInformation.get("Long-RInChIKey"), generator.getLongRInChIKey(), "Long-RInChIKey:");
        Assertions.assertEquals(rinchiFullInformation.get("Short-RInChIKey"), generator.getShortRInChIKey(), "Short-RInChIKey:");
        Assertions.assertEquals(rinchiFullInformation.get("Web-RInChIKey"), generator.getWebRInChIKey(), "Web-RInChIKey:");
        Assertions.assertEquals(status, generator.getStatus(), "RInChI status:");
        List<String> genMessages = new ArrayList<>(generator.getMessages());
        Collections.sort(messages);
        Collections.sort(genMessages);
        Assertions.assertIterableEquals(messages, genMessages, "Status messages:");
    }

    private IReaction readReactionFromRxnFile(String filename) throws Exception {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filename)) {
            final MDLRXNV2000Reader reader = new MDLRXNV2000Reader(inputStream, Mode.RELAXED);
            return reader.read(SilentChemObjectBuilder.getInstance().newReaction());
        }
    }

    private Map<String, String> readRinchiFullInformationFromResourceFile(final String filename) throws IOException, URISyntaxException {
        final String[] rinchiPrefixes = new String[]{"RInChI", "RAuxInfo", "Long-RInChIKey", "Short-RInChIKey", "Web-RInChIKey"};
        final Map<String, String> rinchiFullInformation = new HashMap<>();

        final URL resource = this.getClass().getClassLoader().getResource(filename);
        assertThat(resource).describedAs(String.format("File %s not found in classpath!", filename)).isNotNull();
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