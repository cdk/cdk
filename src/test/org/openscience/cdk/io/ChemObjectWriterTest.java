/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@slists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.nonotify.NNAtomContainer;
import org.openscience.cdk.nonotify.NNAtomContainerSet;
import org.openscience.cdk.nonotify.NNChemFile;
import org.openscience.cdk.nonotify.NNChemModel;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.nonotify.NNMoleculeSet;
import org.openscience.cdk.nonotify.NNReaction;

/**
 * TestCase for {@link IChemObjectWriter} implementations.
 *
 * @cdk.module test-io
 */
public abstract class ChemObjectWriterTest extends ChemObjectIOTest {

    protected static IChemObjectWriter chemObjectIO;

    public static void setChemObjectWriter(IChemObjectWriter aChemObjectWriter){
        ChemObjectIOTest.setChemObjectIO(aChemObjectWriter);
        ChemObjectWriterTest.chemObjectIO = aChemObjectWriter;
    }

    private static IChemObject[] allChemObjectsTypes = {
        new NNChemFile(), new NNChemModel(), new NNMolecule(),
        new NNReaction(), new NNAtomContainerSet(), new NNAtomContainer(),
        new NNMoleculeSet()
    };

    /**
     * Unit tests that iterates over all common objects that can be
     * serialized and tests that if it is marked as accepted with
     * <code>accepts</code>, that it can actually be written too.
     */
    @Test public void testAcceptsWriteConsistency() throws CDKException {
        Assert.assertNotNull("The IChemObjectWriter is not set.", chemObjectIO);
        for (IChemObject object : allChemObjectsTypes) {
            if (chemObjectIO.accepts(object.getClass())) {
                StringWriter writer = new StringWriter();
                chemObjectIO.setWriter(writer);
                try {
                    chemObjectIO.write(object);
                } catch (CDKException exception) {
                    if (exception.getMessage().contains("Only supported")) {
                        Assert.fail("IChemObject of type " +
                            object.getClass().getName() + " is marked as " +
                            "accepted, but failed to be written.");
                    } else {
                        throw exception;
                    }
                }
            }
        }
    }

}
