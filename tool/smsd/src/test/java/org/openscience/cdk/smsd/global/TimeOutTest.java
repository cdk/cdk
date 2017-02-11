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
package org.openscience.cdk.smsd.global;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit testing for the {@link TimeOut} class.
 *
 * @author     egonw
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 *
 * @cdk.module test-smsd
 * @cdk.require java1.6+
 */
public class TimeOutTest {

    @Test
    public void testGetInstance() {
        Assert.assertNotNull(TimeOut.getInstance());
    }

    @Test
    public void testSetTimeOut() {
        TimeOut timeOut = TimeOut.getInstance();
        timeOut.setTimeOut(0.1);
        Assert.assertEquals(0.1, timeOut.getTimeOut(), 0.0001);
        timeOut.setTimeOut(0.2);
        Assert.assertEquals(0.2, timeOut.getTimeOut(), 0.0001);
    }

    /**
     * Test of getTimeOut method, of class TimeOut.
     */
    @Test
    public void testGetTimeOut() {
        TimeOut instance = new TimeOut();
        instance.setTimeOut(10);
        double expResult = 10.0;
        double result = instance.getTimeOut();
        Assert.assertEquals(expResult, result, 10.0);
    }

    /**
     * Test of isTimeOutFlag method, of class TimeOut.
     */
    @Test
    public void testIsTimeOutFlag() {
        TimeOut instance = new TimeOut();
        instance.setTimeOut(10);
        instance.setTimeOutFlag(true);
        boolean expResult = true;
        boolean result = instance.isTimeOutFlag();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTimeOutFlag method, of class TimeOut.
     */
    @Test
    public void testSetTimeOutFlag() {
        boolean timeOut = true;
        TimeOut instance = new TimeOut();
        instance.setTimeOut(10);
        instance.setTimeOutFlag(timeOut);
        boolean expResult = false;
        boolean result = instance.isTimeOutFlag();
        assertNotSame(expResult, result);
    }
}
