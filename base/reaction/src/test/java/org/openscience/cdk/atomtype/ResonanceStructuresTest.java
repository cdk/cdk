/* Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
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
package org.openscience.cdk.atomtype;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.StructureResonanceGeneratorTest;

/**
 * @cdk.module test-reaction
 */
public class ResonanceStructuresTest extends CDKTestCase {

    private final static IChemObjectBuilder builder;
    private final static CDKAtomTypeMatcher matcher;

    static {
        builder = SilentChemObjectBuilder.getInstance();
        matcher = CDKAtomTypeMatcher.getInstance(builder);
    }

    /**
     * Constructor of the ResonanceStructuresTest.
     */
    public ResonanceStructuresTest() {
        super();
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       StructureResonanceGeneratorTest#testGetAllStructures_IAtomContainer()
     * @cdk.inchi InChI=1/C8H10/c1-7-5-3-4-6-8(7)2/h3-6H,1-2H3
     */
    @Test
    public void testGetAllStructures_IAtomContainer() throws Exception {

        //COMPOUND
        //O=C([H])C(=[O+])C([H])([H])[H]
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.getAtom(3).setFormalCharge(1);
        molecule.addSingleElectron(new SingleElectron(molecule.getAtom(3)));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addBond(0, 2, IBond.Order.SINGLE);
        molecule.addBond(2, 3, IBond.Order.DOUBLE);
        molecule.addBond(2, 4, IBond.Order.SINGLE);
        molecule.addBond(0, 5, IBond.Order.SINGLE);
        molecule.addBond(4, 6, IBond.Order.SINGLE);
        molecule.addBond(4, 7, IBond.Order.SINGLE);
        molecule.addBond(4, 8, IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp2", "O.sp2", "C.sp2", "O.plus.sp2.radical", "C.sp3", "H", "H", "H", "H"};
        Assert.assertEquals(expectedTypes.length, molecule.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = molecule.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(molecule, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom, perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }
        //
        //		//FRAGMENT_1
        //		//
        //		IAtomContainer expectedStructure = builder.newInstance(IAtomContainer.class);
        //		expectedStructure.addAtom(builder.newInstance(IAtom.class,"C"));
        //		expectedStructure.addAtom(builder.newInstance(IAtom.class,"C"));
        //		expectedStructure.addAtom(builder.newInstance(IAtom.class,"C"));
        //		expectedStructure.addAtom(builder.newInstance(IAtom.class,"C"));
        //		expectedStructure.addAtom(builder.newInstance(IAtom.class,"C"));
        //		expectedStructure.addAtom(builder.newInstance(IAtom.class,"C"));
        //		expectedStructure.addAtom(builder.newInstance(IAtom.class,"C"));
        //		expectedStructure.addAtom(builder.newInstance(IAtom.class,"C"));
        //		expectedStructure.addBond(0,1,IBond.Order.DOUBLE);
        //		expectedStructure.addBond(1,2,IBond.Order.SINGLE);
        //		expectedStructure.addBond(2,3,IBond.Order.DOUBLE);
        //		expectedStructure.addBond(3,4,IBond.Order.SINGLE);
        //		expectedStructure.addBond(4,5,IBond.Order.DOUBLE);
        //		expectedStructure.addBond(5,0,IBond.Order.SINGLE);
        //		expectedStructure.addBond(0,6,IBond.Order.SINGLE);
        //		expectedStructure.addBond(1,7,IBond.Order.SINGLE);
        //		addExplicitHydrogens(expectedStructure);
        //
        //		String[] expectedTypes1 = {
        //			"C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2",
        //			"C.sp3", "C.sp3", "H", "H", "H", "H", "H", "H", "H",
        //			"H", "H", "H"
        //		};
        //		Assert.assertEquals(expectedTypes.length, expectedStructure.getAtomCount());
        //		for (int i=0; i<expectedTypes1.length; i++) {
        //			IAtom nextAtom = expectedStructure.getAtom(i);
        //			IAtomType perceivedType = matcher.findMatchingAtomType(expectedStructure, nextAtom);
        //			Assert.assertNotNull(
        //				"Missing atom type for: " + nextAtom,
        //				perceivedType
        //			);
        //			Assert.assertEquals(
        //				"Incorrect atom type perceived for: " + nextAtom,
        //				expectedTypes1[i], perceivedType.getAtomTypeName()
        //			);
        //		}
    }

    /**
     * A unit test suite for JUnit. Compound and its fragments to be tested
     * @throws Exception
     *
     * @see       StructureResonanceGeneratorTest#test12DimethylBenzene()
     * @cdk.inchi InChI=1/C8H10/c1-7-5-3-4-6-8(7)2/h3-6H,1-2H3
     */
    @Test
    public void test12DimethylBenzene() throws Exception {

        //COMPOUND
        //[H]C1=C([H])C([H])=C(C(=C1([H]))C([H])([H])[H])C([H])([H])[H]
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addBond(5, 0, IBond.Order.DOUBLE);
        molecule.addBond(0, 6, IBond.Order.SINGLE);
        molecule.addBond(1, 7, IBond.Order.SINGLE);
        addExplicitHydrogens(molecule);

        String[] expectedTypes = {"C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp3", "C.sp3", "H", "H",
                "H", "H", "H", "H", "H", "H", "H", "H"};
        Assert.assertEquals(expectedTypes.length, molecule.getAtomCount());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom nextAtom = molecule.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(molecule, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom, perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes[i],
                    perceivedType.getAtomTypeName());
        }

        //FRAGMENT_1
        //[H]C=1C([H])=C([H])C(=C(C=1([H]))C([H])([H])[H])C([H])([H])[H]
        IAtomContainer expectedStructure = builder.newInstance(IAtomContainer.class);
        expectedStructure.addAtom(builder.newInstance(IAtom.class, "C"));
        expectedStructure.addAtom(builder.newInstance(IAtom.class, "C"));
        expectedStructure.addAtom(builder.newInstance(IAtom.class, "C"));
        expectedStructure.addAtom(builder.newInstance(IAtom.class, "C"));
        expectedStructure.addAtom(builder.newInstance(IAtom.class, "C"));
        expectedStructure.addAtom(builder.newInstance(IAtom.class, "C"));
        expectedStructure.addAtom(builder.newInstance(IAtom.class, "C"));
        expectedStructure.addAtom(builder.newInstance(IAtom.class, "C"));
        expectedStructure.addBond(0, 1, IBond.Order.DOUBLE);
        expectedStructure.addBond(1, 2, IBond.Order.SINGLE);
        expectedStructure.addBond(2, 3, IBond.Order.DOUBLE);
        expectedStructure.addBond(3, 4, IBond.Order.SINGLE);
        expectedStructure.addBond(4, 5, IBond.Order.DOUBLE);
        expectedStructure.addBond(5, 0, IBond.Order.SINGLE);
        expectedStructure.addBond(0, 6, IBond.Order.SINGLE);
        expectedStructure.addBond(1, 7, IBond.Order.SINGLE);
        addExplicitHydrogens(expectedStructure);

        String[] expectedTypes1 = {"C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp3", "C.sp3", "H", "H",
                "H", "H", "H", "H", "H", "H", "H", "H"};
        Assert.assertEquals(expectedTypes.length, expectedStructure.getAtomCount());
        for (int i = 0; i < expectedTypes1.length; i++) {
            IAtom nextAtom = expectedStructure.getAtom(i);
            IAtomType perceivedType = matcher.findMatchingAtomType(expectedStructure, nextAtom);
            Assert.assertNotNull("Missing atom type for: " + nextAtom, perceivedType);
            Assert.assertEquals("Incorrect atom type perceived for: " + nextAtom, expectedTypes1[i],
                    perceivedType.getAtomTypeName());
        }
    }

}
