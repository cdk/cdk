/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@slists.sourceforge.net
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
package org.openscience.cdk.io.formats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;

import com.google.common.io.CharStreams;
import org.junit.Assert;
import org.junit.Test;

/**
 * @cdk.module test-ioformats
 */
abstract public class ChemFormatMatcherTest extends ChemFormatTest {

    private IChemFormatMatcher matcher;

    public void setChemFormatMatcher(IChemFormatMatcher matcher) {
        super.setChemFormat(matcher);
        this.matcher = matcher;
    }

    @Test
    public void testChemFormatMatcherSet() {
        Assert.assertNotNull("You must use setChemFormatMatcher() to set the IChemFormatMatcher object.", matcher);
    }

    protected boolean matches(String header) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(header));
        return matcher.matches(CharStreams.readLines(reader)).matched();
    }

    @Test
    public void testMatches() throws Exception {
        Assert.assertTrue(true);
        // positive testing is done by the ReaderFactoryTest, and
        // negative tests are given below
    }

    @Test
    public void testNoLines() {
        Assert.assertFalse(matcher.matches(Collections.<String> emptyList()).matched());
    }

    @Test
    public void testMatchesEmptyString() {
        Assert.assertFalse(matcher.matches(Arrays.asList("")).matched());
    }

    @Test
    public void testMatchesLoremIpsum() {
        Assert.assertFalse(matcher
                .matches(
                        Arrays.asList("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Etiam accumsan metus ut nulla."))
                .matched());
    }
}
