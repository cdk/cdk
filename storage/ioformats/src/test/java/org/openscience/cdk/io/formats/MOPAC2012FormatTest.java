/*
 * Copyright (C) 2014  The Chemistry Development Kit (CDK) project
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

import org.junit.Assert;
import org.junit.Test;

/**
 * @cdk.module test-ioformats
 */
public class MOPAC2012FormatTest extends ChemFormatMatcherTest {

    public MOPAC2012FormatTest() {
        super.setChemFormatMatcher((IChemFormatMatcher)MOPAC2012Format.getInstance());
    }

    @Test public void matchesMopac2012() throws Exception {
        Assert.assertTrue(((MOPAC2012Format)MOPAC2012Format.getInstance()).matches(0, "MOPAC2012"));
    }
}
