/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */
package org.openscience.cdk.io;

import org.junit.Assert;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.IChemFormatMatcher;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

import java.io.InputStream;

/**
 * TestCase for the instantiation and functionality of the {@link org.openscience.cdk.io.ReaderFactory}.
 *
 * @cdk.module test-io
 */
public class AbstractReaderFactoryTest {

    private ReaderFactory factory = new ReaderFactory();

    void expectReader(String filename, IResourceFormat expectedFormat, int expectedAtomCount, int expectedBondCount)
            throws Exception {
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Assert.assertNotNull("Cannot find file: " + filename, ins);
        if (expectedFormat instanceof IChemFormatMatcher) {
            factory.registerFormat((IChemFormatMatcher) expectedFormat);
        }
        ISimpleChemObjectReader reader = factory.createReader(ins);
        Assert.assertNotNull(reader);
        Assert.assertEquals(((IChemFormat) expectedFormat).getReaderClassName(), reader.getClass().getName());
        // now try reading something from it
        IChemObject[] objects = {new ChemFile(), new ChemModel(), new AtomContainer(), new Reaction()};
        boolean read = false;
        for (int i = 0; (i < objects.length && !read); i++) {
            if (reader.accepts(objects[i].getClass())) {
                IChemObject chemObject = reader.read(objects[i]);
                Assert.assertNotNull("Reader accepted a " + objects[i].getClass().getName() + " but failed to read it",
                        chemObject);
                assertAtomCount(expectedAtomCount, chemObject);
                assertBondCount(expectedBondCount, chemObject);
                read = true;
            }
        }
        if (read) {
            // ok, reseting worked
        } else {
            Assert.fail("Reading an IChemObject from the Reader did not work properly.");
        }
    }

    void assertBondCount(int expectedBondCount, IChemObject chemObject) {
        if (expectedBondCount != -1) {
            if (chemObject instanceof IChemFile) {
                Assert.assertEquals(expectedBondCount, ChemFileManipulator.getBondCount((IChemFile) chemObject));
            } else if (chemObject instanceof IChemModel) {
                Assert.assertEquals(expectedBondCount, ChemModelManipulator.getBondCount((IChemModel) chemObject));
            } else if (chemObject instanceof IAtomContainer) {
                Assert.assertEquals(expectedBondCount, ((IAtomContainer) chemObject).getBondCount());
            } else if (chemObject instanceof IReaction) {
                Assert.assertEquals(expectedBondCount, ReactionManipulator.getBondCount((IReaction) chemObject));
            }
        }
    }

    void assertAtomCount(int expectedAtomCount, IChemObject chemObject) {
        if (expectedAtomCount != -1) {
            if (chemObject instanceof IChemFile) {
                Assert.assertEquals(expectedAtomCount, ChemFileManipulator.getAtomCount((IChemFile) chemObject));
            } else if (chemObject instanceof IChemModel) {
                Assert.assertEquals(expectedAtomCount, ChemModelManipulator.getAtomCount((IChemModel) chemObject));
            } else if (chemObject instanceof IAtomContainer) {
                Assert.assertEquals(expectedAtomCount, ((IAtomContainer) chemObject).getAtomCount());
            } else if (chemObject instanceof IReaction) {
                Assert.assertEquals(expectedAtomCount, ReactionManipulator.getAtomCount((IReaction) chemObject));
            }
        }
    }

}
