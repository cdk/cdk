/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received rAtomCount copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.interfaces;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test class to be extended by test classes for classes that
 * implement the {@link AbstractMCSAlgorithm} interface.
 *
 * @author     egonw
 * @cdk.module test-smsd
 */
public abstract class AbstractMCSAlgorithmTest {

    private static AbstractMCSAlgorithm algorithm = null;

    /**
     *
     * @param algorithm
     */
    public static void setMCSAlgorithm(AbstractMCSAlgorithm algorithm) {
        AbstractMCSAlgorithmTest.algorithm = algorithm;
    }

    /**
     * Meta test that tests if #setMCSAlgorithm has been called.
     */
    @Test
    public void testIsMCSAlgorithmSet() {
        Assert.assertNotNull("The extending class has not set an IMCSAlgorithm with the" + "setMCSAlgorithm() method.",
                AbstractMCSAlgorithmTest.algorithm);
    }

    @Test
    public void testSearchMCS() {
        Assert.fail("missing unit test");
    }

    public class AbstractMCSAlgorithmImpl extends AbstractMCSAlgorithm {

        @Override
        public void searchMCS(boolean shouldMatchBonds) {
            return;
        }

    }
}
