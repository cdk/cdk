/* Copyright (C) 2007-2008  Egon Willighagen <egonw@users.sf.net>
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

import java.util.Map;

import org.junit.Assert;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Helper class that all atom type matcher test classes must implement.
 * It keeps track of the atom types which have been tested, to ensure
 * that all atom types are tested.
 *
 * @cdk.module test-core
 * @cdk.bug    1890702
 */
abstract public class AbstractSybylAtomTypeTest extends AbstractAtomTypeTest {

    private final static String            ATOMTYPE_LIST = "sybyl-atom-types.owl";

    protected final static AtomTypeFactory factory       = AtomTypeFactory
                                                                 .getInstance("org/openscience/cdk/dict/data/"
                                                                         + ATOMTYPE_LIST,
                                                                         SilentChemObjectBuilder.getInstance());

    @Override
    public String getAtomTypeListName() {
        return ATOMTYPE_LIST;
    };

    @Override
    public AtomTypeFactory getFactory() {
        return factory;
    }

    @Override
    public IAtomTypeMatcher getAtomTypeMatcher(IChemObjectBuilder builder) {
        return SybylAtomTypeMatcher.getInstance(builder);
    }

    @Override
    public void assertAtomTypes(Map<String, Integer> testedAtomTypes, String[] expectedTypes, IAtomContainer mol)
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
}
