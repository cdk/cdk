/* Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.tools;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.ProteinBuilderTool;

/**
 * @cdk.module test-pdb
 */
public class ProteinBuilderToolTest extends CDKTestCase {

    @Test
    public void testCreateProtein() throws Exception {
        IBioPolymer protein = ProteinBuilderTool.createProtein("GAGA", SilentChemObjectBuilder.getInstance());
        Assert.assertNotNull(protein);
        Assert.assertEquals(4, protein.getMonomerCount());
        Assert.assertEquals(1, protein.getStrandCount());
        Assert.assertEquals(18 + 1, protein.getAtomCount());
        // 1=terminal oxygen
        Assert.assertEquals(14 + 3 + 1, protein.getBondCount());
        // 3 = extra back bone bonds, 1=bond to terminal oxygen
    }

}
