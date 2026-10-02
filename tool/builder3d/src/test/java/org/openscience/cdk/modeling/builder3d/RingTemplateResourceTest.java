/* Copyright (C) 2026  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.modeling.builder3d;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RingTemplateResourceTest {

    @Test
    void isotopeRecordsHaveNonNegativeMassNumbers() throws Exception {
        InputStream ins = TemplateHandler3D.class.getResourceAsStream(TemplateHandler3D.TEMPLATE_PATH);
        Assertions.assertNotNull(ins);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new GZIPInputStream(ins), StandardCharsets.US_ASCII))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.startsWith("M  ISO"))
                    continue;

                String[] fields = line.trim().split("\\s+");
                int count = Integer.parseInt(fields[2]);
                for (int i = 0; i < count; i++) {
                    int massNumber = Integer.parseInt(fields[4 + 2 * i]);
                    Assertions.assertTrue(massNumber >= 0,
                            "Negative absolute isotope mass at line " + lineNumber + ": " + line);
                }
            }
        }
    }
}
