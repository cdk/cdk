/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.GamessFormat;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.IChemFormatMatcher;
import org.openscience.cdk.io.formats.INChIFormat;
import org.openscience.cdk.io.formats.INChIPlainTextFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.VASPFormat;

import java.io.InputStream;

/**
 * TestCase for the instantiation and functionality of the {@link ReaderFactory} for
 * io classes currently in 'cdk-extra'.
 *
 * @cdk.module test-extra
 */
class ExtraReaderFactoryTest {

    private final ReaderFactory factory = new ReaderFactory();

    void expectReader(String filename,
                      IResourceFormat expectedFormat)
            throws Exception {
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Assertions.assertNotNull(ins, "Cannot find file: " + filename);
        if (expectedFormat instanceof IChemFormatMatcher) {
            factory.registerFormat((IChemFormatMatcher) expectedFormat);
        }
        ISimpleChemObjectReader reader = factory.createReader(ins);
        Assertions.assertNotNull(reader);
        Assertions.assertEquals(((IChemFormat) expectedFormat).getReaderClassName(), reader.getClass().getName());
        // now try reading something from it
        IChemObject[] objects = {new ChemFile(), new ChemModel(), DefaultChemObjectBuilder.getInstance().newAtomContainer(), new Reaction()};
        boolean read = false;
        for (int i = 0; (i < objects.length && !read); i++) {
            if (reader.accepts(objects[i].getClass())) {
                IChemObject chemObject = reader.read(objects[i]);
                Assertions.assertNotNull(chemObject, "Reader accepted a " + objects[i].getClass().getName() + " but failed to read it");
                read = true;
            }
        }
        Assertions.assertTrue(read, "Reading an IChemObject from the Reader did not work properly.");
    }

    @Test
    void testINChI() throws Exception {
        expectReader("org/openscience/cdk/io/guanine.inchi.xml", INChIFormat.getInstance());
    }

    @Test
    void testINChIPlainText() throws Exception {
        expectReader("org/openscience/cdk/io/guanine.inchi", INChIPlainTextFormat.getInstance());
    }

    @Test
    void testVASP() throws Exception {
        expectReader("org/openscience/cdk/io/LiMoS2_optimisation_ISIF3.vasp", VASPFormat.getInstance());
    }

    @Test
    void testGamess() throws Exception {
        expectReader("org/openscience/cdk/io/ch3oh_gam.out", GamessFormat.getInstance());
    }

}
