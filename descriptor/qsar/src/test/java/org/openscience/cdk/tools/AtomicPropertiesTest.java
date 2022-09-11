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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-qsar
 */
class AtomicPropertiesTest extends CDKTestCase {

    @Test
    void testGetInstance() throws IOException {
        AtomicProperties props = AtomicProperties.getInstance();
        Assertions.assertNotNull(props);
        // test singleton pattern
        AtomicProperties props2 = AtomicProperties.getInstance();
        Assertions.assertEquals(props2, props);
    }

    @Test
    void testGetMass() throws Exception {
        AtomicProperties props = AtomicProperties.getInstance();
        double mass = props.getMass("C");
        Assertions.assertTrue(mass > 0);
    }

    @Test
    void testGetNormalizedMass() throws Exception {
        AtomicProperties props = AtomicProperties.getInstance();
        double mass = props.getNormalizedMass("C");
        Assertions.assertTrue(mass > 0);
    }

    @Test
    void testGetPolarizability() throws Exception {
        AtomicProperties props = AtomicProperties.getInstance();
        double polar = props.getPolarizability("C");
        Assertions.assertTrue(polar > 0);
    }

    @Test
    void testGetNormalizedPolarizability() throws Exception {
        AtomicProperties props = AtomicProperties.getInstance();
        double polar = props.getNormalizedPolarizability("C");
        Assertions.assertTrue(polar > 0);
    }

    @Test
    void testGetVdWVolume() throws Exception {
        AtomicProperties props = AtomicProperties.getInstance();
        double vol = props.getVdWVolume("C");
        Assertions.assertTrue(vol > 0);
    }

    @Test
    void testGetNormalizedVdWVolume() throws Exception {
        AtomicProperties props = AtomicProperties.getInstance();
        double vol = props.getNormalizedVdWVolume("C");
        Assertions.assertTrue(vol > 0);
    }

    @Test
    void testGetElectronegativity() throws Exception {
        AtomicProperties props = AtomicProperties.getInstance();
        double eneg = props.getElectronegativity("C");
        Assertions.assertTrue(eneg > 0);
    }

    @Test
    void testGetNormalizedElectronegativity() throws Exception {
        AtomicProperties props = AtomicProperties.getInstance();
        double eneg = props.getNormalizedElectronegativity("C");
        Assertions.assertTrue(eneg > 0);
    }
}
