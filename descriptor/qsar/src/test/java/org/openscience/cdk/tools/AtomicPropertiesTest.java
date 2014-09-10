/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * @cdk.module test-qsar
 */
public class AtomicPropertiesTest extends CDKTestCase {

    @Test
    public void testGetInstance() throws IOException {
        AtomicProperties props = AtomicProperties.getInstance();
        Assert.assertNotNull(props);
        // test singleton pattern
        AtomicProperties props2 = AtomicProperties.getInstance();
        Assert.assertEquals(props2, props);
    }

    @Test
    public void testGetMass() throws Exception {
        AtomicProperties props = AtomicProperties.getInstance();
        double mass = props.getMass("C");
        Assert.assertTrue(mass > 0);
    }

    @Test
    public void testGetNormalizedMass() throws Exception {
        AtomicProperties props = AtomicProperties.getInstance();
        double mass = props.getNormalizedMass("C");
        Assert.assertTrue(mass > 0);
    }

    @Test
    public void testGetPolarizability() throws Exception {
        AtomicProperties props = AtomicProperties.getInstance();
        double polar = props.getPolarizability("C");
        Assert.assertTrue(polar > 0);
    }

    @Test
    public void testGetNormalizedPolarizability() throws Exception {
        AtomicProperties props = AtomicProperties.getInstance();
        double polar = props.getNormalizedPolarizability("C");
        Assert.assertTrue(polar > 0);
    }

    @Test
    public void testGetVdWVolume() throws Exception {
        AtomicProperties props = AtomicProperties.getInstance();
        double vol = props.getVdWVolume("C");
        Assert.assertTrue(vol > 0);
    }

    @Test
    public void testGetNormalizedVdWVolume() throws Exception {
        AtomicProperties props = AtomicProperties.getInstance();
        double vol = props.getNormalizedVdWVolume("C");
        Assert.assertTrue(vol > 0);
    }

    @Test
    public void testGetElectronegativity() throws Exception {
        AtomicProperties props = AtomicProperties.getInstance();
        double eneg = props.getElectronegativity("C");
        Assert.assertTrue(eneg > 0);
    }

    @Test
    public void testGetNormalizedElectronegativity() throws Exception {
        AtomicProperties props = AtomicProperties.getInstance();
        double eneg = props.getNormalizedElectronegativity("C");
        Assert.assertTrue(eneg > 0);
    }
}
