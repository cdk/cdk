/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.interfaces.AbstractMolecularFormulaTest;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;

/**
 * Checks the functionality of the {@link MolecularFormula}.
 *
 * @cdk.module test-silent
 */
public class MolecularFormulaTest extends AbstractMolecularFormulaTest {

    @BeforeAll
    public static void setUp() {
        setBuilder(SilentChemObjectBuilder.getInstance());
    }

    @Test
    public void testMolecularFormula() {
        IMolecularFormula mf = new MolecularFormula();
        Assertions.assertNotNull(mf);
    }

    @Test
    public void testIsTheSame_IIsotope_IIsotope() throws IOException {
        MolecularFormula mf = new MolecularFormula();
        IIsotope carb = getBuilder().newInstance(IIsotope.class, "C");
        IIsotope anotherCarb = getBuilder().newInstance(IIsotope.class, "C");
        IIsotope h = getBuilder().newInstance(IIsotope.class, "H");

        carb.setExactMass(12.0);
        anotherCarb.setExactMass(12.0);
        h.setExactMass(1.0);

        carb.setNaturalAbundance(34.0);
        anotherCarb.setNaturalAbundance(34.0);
        h.setNaturalAbundance(99.0);

        Assertions.assertTrue(mf.isTheSame(carb, carb));
        Assertions.assertTrue(mf.isTheSame(carb, anotherCarb));
        Assertions.assertFalse(mf.isTheSame(carb, h));
    }
}
