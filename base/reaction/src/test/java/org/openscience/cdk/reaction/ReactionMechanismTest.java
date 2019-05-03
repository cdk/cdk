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
package org.openscience.cdk.reaction;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.CDKTestCase;

/**
 * Tests for IReactionProcess implementations.
 *
 * @cdk.module test-reaction
 */
public abstract class ReactionMechanismTest extends CDKTestCase {

    protected static IReactionMechanism reactionMechanism;

    /**
     * Defining reaction mechanism.
     *
     * @param descriptorClass
     * @throws Exception
     */
    public static void setMechanism(Class<?> descriptorClass) throws Exception {
        if (ReactionMechanismTest.reactionMechanism == null) {
            Object descriptor = (Object) descriptorClass.newInstance();
            if (!(descriptor instanceof IReactionMechanism)) {
                throw new CDKException("The passed reaction class must be a IReactionMechanism");
            }
            ReactionMechanismTest.reactionMechanism = (IReactionMechanism) descriptor;
        }
    }

    /**
     * Makes sure that the extending class has set the super.descriptor.
     * Each extending class should have this bit of code (JUnit3 formalism):
     * <pre>
     * public void setUp() {
     *   // Pass a Class, not an Object!
     *   setDescriptor(SomeDescriptor.class);
     * }
     *
     * <p>The unit tests in the extending class may use this instance, but
     * are not required.
     *
     * </pre>
     */
    @Test
    public void testHasSetSuperDotDescriptor() {
        Assert.assertNotNull("The extending class must set the super.descriptor in its setUp() method.",
                reactionMechanism);
    }

}
