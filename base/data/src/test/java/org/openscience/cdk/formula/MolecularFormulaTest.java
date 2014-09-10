/* Copyright (C) 2007  Miguel Rojasch <miguelrojasch@users.sf.net>
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
 *
 */
package org.openscience.cdk.formula;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.AbstractMolecularFormulaTest;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;

/**
 * Checks the functionality of the MolecularFormula.
 *
 * @cdk.module test-data
 *
 * @see MolecularFormula
 */
public class MolecularFormulaTest extends AbstractMolecularFormulaTest {

    @BeforeClass
    public static void setUp() {
        setBuilder(DefaultChemObjectBuilder.getInstance());
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testMolecularFormula() {

        IMolecularFormula mf = getBuilder().newInstance(IMolecularFormula.class);
        Assert.assertNotNull(mf);
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

        Assert.assertTrue(mf.isTheSame(carb, carb));
        Assert.assertTrue(mf.isTheSame(carb, anotherCarb));
        Assert.assertFalse(mf.isTheSame(carb, h));
    }
}
