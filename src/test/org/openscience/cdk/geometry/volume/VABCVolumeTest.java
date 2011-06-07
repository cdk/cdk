/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.geometry.volume;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

public class VABCVolumeTest {

    private static SmilesParser smilesParser;

    @BeforeClass
    public static void setup() {
        smilesParser = new SmilesParser(
            NoNotificationChemObjectBuilder.getInstance()
        );
    }

    /**
     * Test from Table 1 from {@cdk.cite Zhao2003}.
     */
    @Test
    public void testMethane() throws InvalidSmilesException {
        IMolecule methane = smilesParser.parseSmiles("C");
        double volume = VABCVolume.calculate(methane);
        Assert.assertEquals(25.83, volume, 0.01);
    }

}
