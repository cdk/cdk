/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * @cdk.module test-extra
 */
public class HOSECodeAnalyserTest extends CDKTestCase {

    @Test
    public void testGetElements_String() {
        List<String> elements = HOSECodeAnalyser.getElements("CCY(CF,C,/C,,&/&)//");
        Assert.assertEquals(3, elements.size());
        Assert.assertTrue(elements.contains("C"));
        Assert.assertTrue(elements.contains("F"));
        Assert.assertTrue(elements.contains("Br"));
    }

    @Test
    public void testCode1() {
        List<String> elements = HOSECodeAnalyser.getElements("*C*CC(*C,*C,=C/*C,*&,CC/*&O,=OO,%N),C,,C,/");
        Assert.assertEquals(3, elements.size());
        Assert.assertTrue(elements.contains("C"));
        Assert.assertTrue(elements.contains("O"));
        Assert.assertTrue(elements.contains("N"));
    }

}
