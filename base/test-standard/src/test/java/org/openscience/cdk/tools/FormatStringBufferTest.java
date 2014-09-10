/* Copyright (C) 2006-2007  The Chemistry Development Kit (CKD) project
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.tools.FormatStringBuffer;

/**
 * @author     egonw
 * @cdk.module test-standard
 */
public class FormatStringBufferTest extends CDKTestCase {

    private FormatStringBuffer fsb;

    public FormatStringBufferTest() {
        super();
    }

    @Before
    public void setUp() {
        fsb = new FormatStringBuffer("[%s]");
    }

    @Test
    public void testFormat_String() {
        fsb.reset("[%s]").format("test");
        Assert.assertEquals("[test]", fsb.toString());

        fsb.reset("[%5s]").format("test");
        Assert.assertEquals("[ test]", fsb.toString());
    }

    @Test
    public void testToString() {
        fsb.reset("[%-5s]").format("test");
        Assert.assertEquals("[test ]", fsb.toString());

        fsb.reset("[%5.2s]").format("test");
        Assert.assertEquals("[   te]", fsb.toString());

        fsb.reset("[%-5.2s]").format("test");
        Assert.assertEquals("[te   ]", fsb.toString());
    }

    @Test
    public void testFormat_char() {
        fsb.reset("[%c]").format('A');
        Assert.assertEquals("[A]", fsb.toString());

        fsb.reset("[%2c]").format('A');
        Assert.assertEquals("[ A]", fsb.toString());

        fsb.reset("[%-2c]").format('A');
        Assert.assertEquals("[A ]", fsb.toString());
    }

    @Test
    public void testFormat_double() {
        fsb.reset("[%f]").format(3.1415);
        Assert.assertEquals("[3.1415]", fsb.toString());

        fsb.reset("[%g]").format(3.1415);
        Assert.assertEquals("[3.1415]", fsb.toString());

        fsb.reset("[%+f]").format(3.1415);
        Assert.assertEquals("[+3.1415]", fsb.toString());

        fsb.reset("[%+10f]").format(3.1415);
        Assert.assertEquals("[   +3.1415]", fsb.toString());

        fsb.reset("[%-+10f]").format(3.1415);
        Assert.assertEquals("[+3.1415   ]", fsb.toString());

        fsb.reset("[%.3f]").format(3.1415);
        Assert.assertEquals("[3.142]", fsb.toString());

        fsb.reset("[%e]").format(3.1415);
        Assert.assertEquals("[3.1415e00]", fsb.toString());

        fsb.reset("[%+e]").format(3.1415);
        Assert.assertEquals("[+3.1415e00]", fsb.toString());

        fsb.reset("[%+11e]").format(3.1415);
        Assert.assertEquals("[ +3.1415e00]", fsb.toString());

        fsb.reset("[%-+11e]").format(3.1415);
        Assert.assertEquals("[+3.1415e00 ]", fsb.toString());

        fsb.reset("[%.3e]").format(3.1415);
        Assert.assertEquals("[3.142e00]", fsb.toString());

        fsb.reset("[%E]").format(3.1415);
        Assert.assertEquals("[3.1415E00]", fsb.toString());
    }

    @Test
    public void testFormat_int() {
        fsb.reset("[%d]").format(600);
        Assert.assertEquals("[600]", fsb.toString());

        fsb.reset("[%5d]").format(600);
        Assert.assertEquals("[  600]", fsb.toString());

        fsb.reset("[%5d]").format(-600);
        Assert.assertEquals("[ -600]", fsb.toString());

        fsb.reset("[%05d]").format(600);
        Assert.assertEquals("[00600]", fsb.toString());

        fsb.reset("[%05d]").format(-600);
        Assert.assertEquals("[-0600]", fsb.toString());

        fsb.reset("[%x]").format(10);
        Assert.assertEquals("[a]", fsb.toString());

        fsb.reset("[%X]").format(10);
        Assert.assertEquals("[A]", fsb.toString());

        fsb.reset("[%o]").format(10);
        Assert.assertEquals("[12]", fsb.toString());

        fsb.reset("[%4X]").format(10);
        Assert.assertEquals("[   A]", fsb.toString());

        fsb.reset("[%#4x]").format(10);
        Assert.assertEquals("[ 0xa]", fsb.toString());

        fsb.reset("[%#4o]").format(10);
        Assert.assertEquals("[ 012]", fsb.toString());

        fsb.reset("[%#04x]").format(10);
        Assert.assertEquals("[0x0a]", fsb.toString());

        fsb.reset("[%#04o]").format(10);
        Assert.assertEquals("[0012]", fsb.toString());

        fsb.reset();
        Assert.assertEquals("[%#04o]", fsb.toString());
    }

    @Test
    public void testFormat_long() {
        fsb.reset("[%d]").format((long) 600);
        Assert.assertEquals("[600]", fsb.toString());

        fsb.reset("[%5d]").format((long) 600);
        Assert.assertEquals("[  600]", fsb.toString());

        fsb.reset("[%5d]").format((long) -600);
        Assert.assertEquals("[ -600]", fsb.toString());

        fsb.reset("[%05d]").format((long) 600);
        Assert.assertEquals("[00600]", fsb.toString());

        fsb.reset("[%05d]").format((long) -600);
        Assert.assertEquals("[-0600]", fsb.toString());

        fsb.reset("[%x]").format((long) 10);
        Assert.assertEquals("[a]", fsb.toString());

        fsb.reset("[%X]").format((long) 10);
        Assert.assertEquals("[A]", fsb.toString());

        fsb.reset("[%o]").format((long) 10);
        Assert.assertEquals("[12]", fsb.toString());

        fsb.reset("[%4X]").format((long) 10);
        Assert.assertEquals("[   A]", fsb.toString());

        fsb.reset("[%#4x]").format((long) 10);
        Assert.assertEquals("[ 0xa]", fsb.toString());

        fsb.reset("[%#4o]").format((long) 10);
        Assert.assertEquals("[ 012]", fsb.toString());

        fsb.reset("[%#04x]").format((long) 10);
        Assert.assertEquals("[0x0a]", fsb.toString());

        fsb.reset("[%#04o]").format((long) 10);
        Assert.assertEquals("[0012]", fsb.toString());
    }

    @Test
    public void testReset() {
        fsb.reset();
        Assert.assertEquals("[%s]", fsb.toString());
    }

    @Test
    public void testReset_String() {
        fsb.reset("[%#04o]").format((long) 10);
        fsb.reset();
        Assert.assertEquals("[%#04o]", fsb.toString());
    }
}
