/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.io;

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.test.io.SimpleChemObjectReaderTest;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * @cdk.module test-io
 */
class PCSubstanceXMLReaderTest extends SimpleChemObjectReaderTest {

    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(PCSubstanceXMLReaderTest.class);

    @BeforeAll
    static void setup() throws Exception {
        setSimpleChemObjectReader(new PCSubstanceXMLReader(), "sid577309.xml");
    }

    @Test
    void testAccepts() throws Exception {
        PCSubstanceXMLReader reader = new PCSubstanceXMLReader();
        Assertions.assertTrue(reader.accepts(AtomContainer.class));
    }

    @Test
    void testReading() throws Exception {
        String filename = "sid577309.xml";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getResourceAsStream(filename);
        PCSubstanceXMLReader reader = new PCSubstanceXMLReader(ins);
        IAtomContainer molecule = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
        Assertions.assertNotNull(molecule);

        // check atom stuff
        Assertions.assertEquals(19, molecule.getAtomCount());
        Assertions.assertTrue(molecule.getAtom(0) instanceof IPseudoAtom);

        // check bond stuff
        Assertions.assertEquals(19, molecule.getBondCount());
        Assertions.assertNotNull(molecule.getBond(3));
    }
}
