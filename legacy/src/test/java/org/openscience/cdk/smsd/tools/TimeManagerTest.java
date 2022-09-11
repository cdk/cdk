/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
package org.openscience.cdk.smsd.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @author Asad
 * @cdk.module test-smsd
 */
@Tag("SlowTest")
// test uses Thread.sleep...
class TimeManagerTest extends CDKTestCase {

    @Test
    void testTimeManager() throws Exception {
        TimeManager tMan = new TimeManager();
        Assertions.assertNotNull(tMan);
    }

    /**
     * Test of getElapsedTimeInHours method, of class TimeManager.
     */
    @Test
    void testGetElapsedTimeInHours() {
        TimeManager instance = new TimeManager();
        double expResult = 0.0001;
        myMethod(360);
        double result = instance.getElapsedTimeInHours();
        Assertions.assertEquals(expResult, result, 0.0001);
    }

    /**
     * Test of getElapsedTimeInMinutes method, of class TimeManager.
     */
    @Test
    void testGetElapsedTimeInMinutes() {
        TimeManager instance = new TimeManager();
        double expResult = 0.006;
        myMethod(360);
        double result = instance.getElapsedTimeInMinutes();
        Assertions.assertEquals(expResult, result, 0.006);
    }

    /**
     * Test of getElapsedTimeInSeconds method, of class TimeManager.
     */
    @Test
    void testGetElapsedTimeInSeconds() {
        TimeManager instance = new TimeManager();
        double expResult = 0.36;
        myMethod(360);
        double result = instance.getElapsedTimeInSeconds();
        Assertions.assertEquals(expResult, result, 0.36);
    }

    /**
     * Test of getElapsedTimeInMilliSeconds method, of class TimeManager.
     */
    @Test
    void testGetElapsedTimeInMilliSeconds() {
        TimeManager instance = new TimeManager();
        double expResult = 360;
        myMethod(360);
        double result = instance.getElapsedTimeInMilliSeconds();
        Assertions.assertEquals(expResult, result, 360);
    }

    void myMethod(long timeMillis) {
        try {
            Thread.sleep(timeMillis);
        } catch (InterruptedException e) {
            // ignored
        }
    }
}
