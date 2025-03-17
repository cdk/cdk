/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.silent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.AbstractChemObjectBuilderTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReaction;

/**
 * Checks the functionality of the {@link IChemObjectBuilder} {@link SilentChemObjectBuilder} implementation.
 *
 */
class SilentChemObjectBuilderTest extends AbstractChemObjectBuilderTest {

    @BeforeAll
    static void setUp() {
        setRootObject(new ChemObject());
    }

    @Test
    void testGetInstance() {
        Object builder = SilentChemObjectBuilder.getInstance();
        Assertions.assertNotNull(builder);
        Assertions.assertTrue(builder instanceof IChemObjectBuilder);
        Assertions.assertTrue(builder.getClass().getName().contains("SilentChemObjectBuilder"));
    }

    @Test
    void testNewAtom_empty() {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtom atom = builder.newAtom();
        Assertions.assertNotNull(atom);
        Assertions.assertTrue(atom.getBuilder() instanceof IChemObjectBuilder);
        Assertions.assertTrue(atom.getBuilder().getClass().getName().contains("SilentChemObjectBuilder"));
    }

    @Test
    void testNewBond_empty() {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IBond bond = builder.newBond();
        Assertions.assertNotNull(bond);
        Assertions.assertTrue(bond.getBuilder() instanceof IChemObjectBuilder);
        Assertions.assertTrue(bond.getBuilder().getClass().getName().contains("SilentChemObjectBuilder"));
    }

    @Test
    void testNewAtomContainer_empty() {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer atomContainer = builder.newAtomContainer();
        Assertions.assertNotNull(atomContainer);
        Assertions.assertTrue(atomContainer.getBuilder() instanceof IChemObjectBuilder);
        Assertions.assertTrue(atomContainer.getBuilder().getClass().getName().contains("SilentChemObjectBuilder"));
    }

    @Test
    void testNewReaction_empty() {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IReaction reaction = builder.newReaction();
        Assertions.assertNotNull(reaction);
        Assertions.assertTrue(reaction.getBuilder() instanceof IChemObjectBuilder);
        Assertions.assertTrue(reaction.getBuilder().getClass().getName().contains("SilentChemObjectBuilder"));
    }
}
