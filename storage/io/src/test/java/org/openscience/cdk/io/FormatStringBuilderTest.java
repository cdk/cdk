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
package org.openscience.cdk.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @author     egonw
 */
class FormatStringBuilderTest extends CDKTestCase {

    private FormatStringBuilder fsb;

    FormatStringBuilderTest() {
        super();
    }

    @BeforeEach
    void setUp() {
        fsb = new FormatStringBuilder("[%s]");
    }

    @Test
    void testFormat_String() {
        fsb.reset("[%s]").format("test");
        Assertions.assertEquals("[test]", fsb.toString());

        fsb.reset("[%5s]").format("test");
        Assertions.assertEquals("[ test]", fsb.toString());
    }

    @Test
    void testToString() {
        fsb.reset("[%-5s]").format("test");
        Assertions.assertEquals("[test ]", fsb.toString());

        fsb.reset("[%5.2s]").format("test");
        Assertions.assertEquals("[   te]", fsb.toString());

        fsb.reset("[%-5.2s]").format("test");
        Assertions.assertEquals("[te   ]", fsb.toString());
    }

    @Test
    void testFormat_char() {
        fsb.reset("[%c]").format('A');
        Assertions.assertEquals("[A]", fsb.toString());

        fsb.reset("[%2c]").format('A');
        Assertions.assertEquals("[ A]", fsb.toString());

        fsb.reset("[%-2c]").format('A');
        Assertions.assertEquals("[A ]", fsb.toString());
    }

    @Test
    void testFormat_double() {
        fsb.reset("[%f]").format(3.1415);
        Assertions.assertEquals("[3.1415]", fsb.toString());

        fsb.reset("[%g]").format(3.1415);
        Assertions.assertEquals("[3.1415]", fsb.toString());

        fsb.reset("[%+f]").format(3.1415);
        Assertions.assertEquals("[+3.1415]", fsb.toString());

        fsb.reset("[%+10f]").format(3.1415);
        Assertions.assertEquals("[   +3.1415]", fsb.toString());

        fsb.reset("[%-+10f]").format(3.1415);
        Assertions.assertEquals("[+3.1415   ]", fsb.toString());

        fsb.reset("[%.3f]").format(3.1415);
        Assertions.assertEquals("[3.142]", fsb.toString());

        fsb.reset("[%e]").format(3.1415);
        Assertions.assertEquals("[3.1415e00]", fsb.toString());

        fsb.reset("[%+e]").format(3.1415);
        Assertions.assertEquals("[+3.1415e00]", fsb.toString());

        fsb.reset("[%+11e]").format(3.1415);
        Assertions.assertEquals("[ +3.1415e00]", fsb.toString());

        fsb.reset("[%-+11e]").format(3.1415);
        Assertions.assertEquals("[+3.1415e00 ]", fsb.toString());

        fsb.reset("[%.3e]").format(3.1415);
        Assertions.assertEquals("[3.142e00]", fsb.toString());

        fsb.reset("[%E]").format(3.1415);
        Assertions.assertEquals("[3.1415E00]", fsb.toString());
    }

    @Test
    void testFormat_int() {
        fsb.reset("[%d]").format(600);
        Assertions.assertEquals("[600]", fsb.toString());

        fsb.reset("[%5d]").format(600);
        Assertions.assertEquals("[  600]", fsb.toString());

        fsb.reset("[%5d]").format(-600);
        Assertions.assertEquals("[ -600]", fsb.toString());

        fsb.reset("[%05d]").format(600);
        Assertions.assertEquals("[00600]", fsb.toString());

        fsb.reset("[%05d]").format(-600);
        Assertions.assertEquals("[-0600]", fsb.toString());

        fsb.reset("[%x]").format(10);
        Assertions.assertEquals("[a]", fsb.toString());

        fsb.reset("[%X]").format(10);
        Assertions.assertEquals("[A]", fsb.toString());

        fsb.reset("[%o]").format(10);
        Assertions.assertEquals("[12]", fsb.toString());

        fsb.reset("[%4X]").format(10);
        Assertions.assertEquals("[   A]", fsb.toString());

        fsb.reset("[%#4x]").format(10);
        Assertions.assertEquals("[ 0xa]", fsb.toString());

        fsb.reset("[%#4o]").format(10);
        Assertions.assertEquals("[ 012]", fsb.toString());

        fsb.reset("[%#04x]").format(10);
        Assertions.assertEquals("[0x0a]", fsb.toString());

        fsb.reset("[%#04o]").format(10);
        Assertions.assertEquals("[0012]", fsb.toString());

        fsb.reset();
        Assertions.assertEquals("[%#04o]", fsb.toString());
    }

    @Test
    void testFormat_long() {
        fsb.reset("[%d]").format((long) 600);
        Assertions.assertEquals("[600]", fsb.toString());

        fsb.reset("[%5d]").format((long) 600);
        Assertions.assertEquals("[  600]", fsb.toString());

        fsb.reset("[%5d]").format((long) -600);
        Assertions.assertEquals("[ -600]", fsb.toString());

        fsb.reset("[%05d]").format((long) 600);
        Assertions.assertEquals("[00600]", fsb.toString());

        fsb.reset("[%05d]").format((long) -600);
        Assertions.assertEquals("[-0600]", fsb.toString());

        fsb.reset("[%x]").format((long) 10);
        Assertions.assertEquals("[a]", fsb.toString());

        fsb.reset("[%X]").format((long) 10);
        Assertions.assertEquals("[A]", fsb.toString());

        fsb.reset("[%o]").format((long) 10);
        Assertions.assertEquals("[12]", fsb.toString());

        fsb.reset("[%4X]").format((long) 10);
        Assertions.assertEquals("[   A]", fsb.toString());

        fsb.reset("[%#4x]").format((long) 10);
        Assertions.assertEquals("[ 0xa]", fsb.toString());

        fsb.reset("[%#4o]").format((long) 10);
        Assertions.assertEquals("[ 012]", fsb.toString());

        fsb.reset("[%#04x]").format((long) 10);
        Assertions.assertEquals("[0x0a]", fsb.toString());

        fsb.reset("[%#04o]").format((long) 10);
        Assertions.assertEquals("[0012]", fsb.toString());
    }

    @Test
    void testReset() {
        fsb.reset();
        Assertions.assertEquals("[%s]", fsb.toString());
    }

    @Test
    void testReset_String() {
        fsb.reset("[%#04o]").format((long) 10);
        fsb.reset();
        Assertions.assertEquals("[%#04o]", fsb.toString());
    }
}
