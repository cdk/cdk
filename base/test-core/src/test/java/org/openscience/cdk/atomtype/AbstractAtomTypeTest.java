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
package org.openscience.cdk.atomtype;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.NoSuchAtomTypeException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * Helper class that all atom type matcher test classes must implement.
 * It keeps track of the atom types which have been tested, to ensure
 * that all atom types are tested.
 *
 * @cdk.module test-core
 * @cdk.bug    1890702
 */
abstract public class AbstractAtomTypeTest extends CDKTestCase implements IAtomTypeTest {

    /**
     * Helper method to test if atom types are correctly perceived. Meanwhile, it maintains a list
     * of atom types that have been tested so far, which allows testing afterwards that all atom
     * types are at least tested once.
     *
     * @param testedAtomTypes   List of atom types tested so far.
     * @param expectedTypes     Expected atom types for the atoms given in <code>mol</code>.
     * @param mol               The <code>IAtomContainer</code> with <code>IAtom</code>s for which atom types should be perceived.
     * @throws Exception     Thrown if something went wrong during the atom type perception.
     */
    public void assertAtomTypes(Map<String, Integer> testedAtomTypes, String[] expectedTypes, IAtomContainer mol)
            throws Exception {
        Assert.assertEquals("The number of expected atom types is unequal to the number of atoms",
                expectedTypes.length, mol.getAtomCount());
        IAtomTypeMatcher atm = getAtomTypeMatcher(mol.getBuilder());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom testedAtom = mol.getAtom(i);
            IAtomType foundType = atm.findMatchingAtomType(mol, testedAtom);
            assertAtomType(testedAtomTypes, "Incorrect perception for atom " + i, expectedTypes[i], foundType);
            assertConsistentProperties(mol, testedAtom, foundType);
            // test for bug #1890702: configure, and then make sure the same atom type is perceived
            AtomTypeManipulator.configure(testedAtom, foundType);
            IAtomType secondType = atm.findMatchingAtomType(mol, testedAtom);
            assertAtomType(testedAtomTypes,
                    "Incorrect perception *after* assigning atom type properties for atom " + i, expectedTypes[i],
                    secondType);
        }
    }

    public void assertAtomTypeNames(Map<String, Integer> testedAtomTypes, String[] expectedTypes, IAtomContainer mol)
            throws Exception {
        Assert.assertEquals("The number of expected atom types is unequal to the number of atoms",
                expectedTypes.length, mol.getAtomCount());
        IAtomTypeMatcher atm = getAtomTypeMatcher(mol.getBuilder());
        for (int i = 0; i < expectedTypes.length; i++) {
            IAtom testedAtom = mol.getAtom(i);
            IAtomType foundType = atm.findMatchingAtomType(mol, testedAtom);
            assertAtomType(testedAtomTypes, "Incorrect perception for atom " + i, expectedTypes[i], foundType);
        }
    }

    /**
     * Method that tests if the matched <code>IAtomType</code> and the <code>IAtom</code> are
     * consistent. For example, it tests if hybridization states and formal charges are equal.
     *
     * @cdk.bug 1897589
     */
    private void assertConsistentProperties(IAtomContainer mol, IAtom atom, IAtomType matched) {
        // X has no properties; nothing to match
        if ("X".equals(matched.getAtomTypeName())) {
            return;
        }

        if (atom.getHybridization() != CDKConstants.UNSET && matched.getHybridization() != CDKConstants.UNSET) {
            Assert.assertEquals("Hybridization does not match", atom.getHybridization(), matched.getHybridization());
        }
        if (atom.getFormalCharge() != CDKConstants.UNSET && matched.getFormalCharge() != CDKConstants.UNSET) {
            Assert.assertEquals("Formal charge does not match", atom.getFormalCharge(), matched.getFormalCharge());
        }
        List<IBond> connections = mol.getConnectedBondsList(atom);
        int connectionCount = connections.size();
        if (matched.getFormalNeighbourCount() != CDKConstants.UNSET) {
            Assert.assertFalse("Number of neighbors is too high", connectionCount > matched.getFormalNeighbourCount());
        }
        if (matched.getMaxBondOrder() != null) {
            Order expectedMax = matched.getMaxBondOrder();
            for (IBond bond : connections) {
                IBond.Order order = bond.getOrder();
                if (order != CDKConstants.UNSET && order != IBond.Order.UNSET) {
                    if (BondManipulator.isHigherOrder(order, expectedMax)) {
                        Assert.fail("At least one bond order exceeds the maximum for the atom type");
                    }
                } else if (bond.getFlag(CDKConstants.SINGLE_OR_DOUBLE)) {
                    if (expectedMax != IBond.Order.SINGLE && expectedMax != IBond.Order.DOUBLE) {
                        Assert.fail("A single or double flagged bond does not match the bond order of the atom type");
                    }
                }
            }
        }
    }

    public void assertAtomType(Map<String, Integer> testedAtomTypes, String expectedID, IAtomType foundAtomType) {
        this.assertAtomType(testedAtomTypes, "", expectedID, foundAtomType);
    }

    public void assertAtomType(Map<String, Integer> testedAtomTypes, String error, String expectedID,
            IAtomType foundAtomType) {
        addTestedAtomType(testedAtomTypes, expectedID);

        Assert.assertNotNull("No atom type was recognized, but expected: " + expectedID, foundAtomType);
        Assert.assertEquals(error, expectedID, foundAtomType.getAtomTypeName());
    }

    private void addTestedAtomType(Map<String, Integer> testedAtomTypes, String expectedID) {
        if (testedAtomTypes == null) {
            testedAtomTypes = new HashMap<String, Integer>();
        }

        try {
            IAtomType type = getFactory().getAtomType(expectedID);
            Assert.assertNotNull("Attempt to test atom type which is not defined in the " + getAtomTypeListName()
                    + ": " + expectedID, type);
        } catch (NoSuchAtomTypeException exception) {
            System.err.println("Attempt to test atom type which is not defined in the " + getAtomTypeListName()
                    + ": " + exception.getMessage());
        }
        if (testedAtomTypes.containsKey(expectedID)) {
            // increase the count, so that redundancy can be calculated
            testedAtomTypes.put(expectedID, 1 + testedAtomTypes.get(expectedID));
        } else {
            testedAtomTypes.put(expectedID, 1);
        }
    }

    public void testForDuplicateDefinitions() {
        IAtomType[] expectedTypesArray = getFactory().getAllAtomTypes();
        Set<String> alreadyDefinedTypes = new HashSet<String>();

        for (int i = 0; i < expectedTypesArray.length; i++) {
            String definedType = expectedTypesArray[i].getAtomTypeName();
            if (alreadyDefinedTypes.contains(definedType)) {
                Assert.fail("Duplicate atom type definition in XML: " + definedType);
            }
            alreadyDefinedTypes.add(definedType);
        }
    }

    public static void countTestedAtomTypes(Map<String, Integer> testedAtomTypesMap, AtomTypeFactory factory) {
        Set<String> testedAtomTypes = new HashSet<String>();
        testedAtomTypes.addAll(testedAtomTypesMap.keySet());

        Set<String> definedTypes = new HashSet<String>();
        IAtomType[] expectedTypesArray = factory.getAllAtomTypes();
        for (int i = 0; i < expectedTypesArray.length; i++) {
            definedTypes.add(expectedTypesArray[i].getAtomTypeName());
        }

        if (definedTypes.size() == testedAtomTypes.size() && definedTypes.containsAll(testedAtomTypes)) {
            // all is fine
        } else if (definedTypes.size() > testedAtomTypes.size()) {
            // more atom types defined than tested
            int expectedTypeCount = definedTypes.size();
            definedTypes.removeAll(testedAtomTypes);
            String errorMessage = "Atom types defined but not tested:";
            for (String notTestedType : definedTypes) {
                errorMessage += " " + notTestedType;
            }
            if (expectedTypeCount != testedAtomTypes.size()) {
                Assert.fail(errorMessage);
            }
        } else { // testedAtomTypes.size() > definedTypes.size()
            // more atom types tested than defined
            int testedTypeCount = testedAtomTypes.size();
            testedAtomTypes.removeAll(definedTypes);
            String errorMessage = "Atom types tested but not defined:";
            for (String notDefined : testedAtomTypes) {
                errorMessage += " " + notDefined;
            }
            if (testedTypeCount != testedAtomTypes.size()) {
                Assert.fail(errorMessage);
            }
        }
    }

}
