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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.AbstractAdductFormulaTest;
import org.openscience.cdk.interfaces.IAdductFormula;
import org.openscience.cdk.interfaces.IMolecularFormula;

/**
 * Checks the functionality of the AdductFormula.
 *
 * @cdk.module test-data
 *
 * @see AdductFormula
 */
public class AdductFormulaTest extends AbstractAdductFormulaTest {

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
    public void testAdductFormula() {
        IAdductFormula mfS = new AdductFormula();
        Assert.assertNotNull(mfS);
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testAdductFormula_IMolecularFormula() {
        IAdductFormula mfS = new AdductFormula(getBuilder().newInstance(IMolecularFormula.class));
        Assert.assertEquals(1, mfS.size());
    }
}
