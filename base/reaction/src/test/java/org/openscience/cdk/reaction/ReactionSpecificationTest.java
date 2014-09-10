/* Copyright (C) 2008  Miguel Rojas <miguelrojasch@users.sf.net>
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
import org.openscience.cdk.CDKTestCase;

/**
 * @cdk.module test-reaction
 */
public class ReactionSpecificationTest extends CDKTestCase {

    public ReactionSpecificationTest() {
        super();
    }

    private final static String REAC_REF         = "bla";
    private final static String REAC_IMPL_TITLE  = "bla2";
    private final static String REAC_IMPL_VENDOR = "bla3";
    private final static String REAC_IMPL_ID     = "bla4";

    @Test
    public void testReactionSpecification_String_String_String_String() {
        ReactionSpecification spec = new ReactionSpecification(REAC_REF, REAC_IMPL_TITLE, REAC_IMPL_ID,
                REAC_IMPL_VENDOR);
        Assert.assertNotNull(spec);
    }

    @Test
    public void testGetImplementationVendor() {
        ReactionSpecification spec = new ReactionSpecification(REAC_REF, REAC_IMPL_TITLE, REAC_IMPL_ID,
                REAC_IMPL_VENDOR);
        Assert.assertEquals(REAC_IMPL_VENDOR, spec.getImplementationVendor());
    }

    @Test
    public void testGetSpecificationReference() {
        ReactionSpecification spec = new ReactionSpecification(REAC_REF, REAC_IMPL_TITLE, REAC_IMPL_ID,
                REAC_IMPL_VENDOR);
        Assert.assertEquals(REAC_REF, spec.getSpecificationReference());
    }

    @Test
    public void testGetImplementationIdentifier() {
        ReactionSpecification spec = new ReactionSpecification(REAC_REF, REAC_IMPL_TITLE, REAC_IMPL_ID,
                REAC_IMPL_VENDOR);
        Assert.assertEquals(REAC_IMPL_ID, spec.getImplementationIdentifier());
    }

    @Test
    public void testGetImplementationTitle() {
        ReactionSpecification spec = new ReactionSpecification(REAC_REF, REAC_IMPL_TITLE, REAC_IMPL_ID,
                REAC_IMPL_VENDOR);
        Assert.assertEquals(REAC_IMPL_TITLE, spec.getImplementationTitle());
    }

}
