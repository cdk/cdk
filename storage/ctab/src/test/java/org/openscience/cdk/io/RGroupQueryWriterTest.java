/*
 * Copyright (C) 2010  Mark Rijnbeek <mark_rynbeek@users.sf.net>
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may
 * distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.io;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.RGroupQuery;
import org.openscience.cdk.test.io.ChemObjectIOTest;

/**
 * JUnit tests for {@link org.openscience.cdk.io.RGroupQueryWriter}.
 * Idea: read the test RGfiles into an object model, then writes the
 * same model out as an RGfile again without changing anything. Then
 * check that the original inputfile and the outputfile have the same content.
 *
 * @cdk.module test-io
 * @author Mark Rijnbeek
 */
public class RGroupQueryWriterTest extends ChemObjectIOTest {

    private static IChemObjectBuilder builder;

    @BeforeAll
    public static void setup() {
        builder = DefaultChemObjectBuilder.getInstance();
        setChemObjectIO(new RGroupQueryWriter());
    }

    @Test
    public void testRgroupQueryFile_1() throws Exception {
        String rgFile = recreate("rgfile.1.mol");

        Assertions.assertEquals(0, countSubstring("AAL", rgFile), "AAL lines");
        Assertions.assertEquals(1, countSubstring("LOG", rgFile), "LOG lines");
        Assertions.assertEquals(3, countSubstring("APO", rgFile), "APO lines");
        Assertions.assertTrue(rgFile.contains("M  LOG  1   1   0   1   0,1-3"));
        Assertions.assertEquals(59, countSubstring("\n", rgFile), "Total #lines");
    }

    @Test
    public void testRgroupQueryFile_2() throws Exception {
        String rgFile = recreate("rgfile.2.mol");

        Assertions.assertEquals(1, countSubstring("AAL", rgFile), "AAL lines");
        Assertions.assertEquals(3, countSubstring("LOG", rgFile), "LOG lines");
        Assertions.assertEquals(5, countSubstring("APO", rgFile), "APO lines");
        Assertions.assertTrue(rgFile.contains("M  RGP  4   1  11   2   2   3   2   4   1"));
        Assertions.assertEquals(107, countSubstring("\n", rgFile), "Total #lines");
    }

    @Test
    public void testRgroupQueryFile_3() throws Exception {
        String rgFile = recreate("rgfile.3.mol");
        Assertions.assertEquals(2, countSubstring("AAL", rgFile), "AAL lines");
        Assertions.assertEquals(1, countSubstring("LOG", rgFile), "LOG lines");
        Assertions.assertEquals(2, countSubstring("APO", rgFile), "APO lines");
        Assertions.assertEquals(66, countSubstring("\n", rgFile), "Total #lines");
        Assertions.assertTrue(rgFile.contains("M  RGP  2   5   1   7   1"));
    }

    @Test
    public void testRgroupQueryFile_4() throws Exception {
        String rgFile = recreate("rgfile.4.mol");
        Assertions.assertEquals(0, countSubstring("AAL", rgFile), "AAL lines");
        Assertions.assertEquals(3, countSubstring("\\$CTAB", rgFile), "\\$CTAB lines");
        // the R-group is detached, we don't write APO lines (unlike the 0 value APO in the input file)
        Assertions.assertEquals(0, countSubstring("APO", rgFile), "APO lines");
        Assertions.assertEquals(46, countSubstring("\n", rgFile), "Total #lines");
        Assertions.assertTrue(rgFile.contains("M  RGP  1   6   1"));
    }

    @Test
    public void testRgroupQueryFile_5() throws Exception {
        String rgFile = recreate("rgfile.5.mol");
        Assertions.assertEquals(4, countSubstring("LOG", rgFile), "LOG lines");
        Assertions.assertEquals(0, countSubstring("APO", rgFile), "APO lines");
        Assertions.assertEquals(2, countSubstring("M  RGP", rgFile), "M  RGP lines"); //overflow
        Assertions.assertEquals(132, countSubstring("\n", rgFile), "Total #lines");
    }

    @Test
    public void testRgroupQueryFile_6() throws Exception {
        String rgFile = recreate("rgfile.6.mol");
        Assertions.assertEquals(1, countSubstring("AAL", rgFile), "AAL lines");
        Assertions.assertEquals(3, countSubstring("LOG", rgFile), "LOG lines");
        Assertions.assertEquals(1, countSubstring("APO", rgFile), "APO lines");
        Assertions.assertEquals(57, countSubstring("\n", rgFile), "Total #lines");
    }

    @Test
    public void testRgroupQueryFile_7() throws Exception {
        String rgFile = recreate("rgfile.7.mol");
        Assertions.assertEquals(1, countSubstring("LOG", rgFile), "LOG lines");
        Assertions.assertEquals(2, countSubstring("APO", rgFile), "APO lines");
        Assertions.assertTrue(rgFile.contains("M  RGP  3   4  32   6  32   7  32"));
        Assertions.assertEquals(53, countSubstring("\n", rgFile), "Total #lines");
    }

    private int countSubstring(String regExp, String text) {
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(text); // get a matcher object
        int count = 0;
        while (m.find())
            count++;
        return count;
    }

    private String recreate(String file) throws CDKException {
        StringWriter sw = new StringWriter();
        RGroupQueryWriter rgw = new RGroupQueryWriter(sw);
        InputStream ins = this.getClass().getResourceAsStream(file);
        RGroupQueryReader reader = new RGroupQueryReader(ins);
        RGroupQuery rGroupQuery = reader.read(new RGroupQuery(DefaultChemObjectBuilder.getInstance()));
        rgw.write(rGroupQuery);
        String out = sw.toString();
        return out;

    }

}
