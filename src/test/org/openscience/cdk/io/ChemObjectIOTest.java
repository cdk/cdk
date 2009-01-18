/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.io.formats.IResourceFormat;

/**
 * TestCase for CDK IO classes.
 *
 * @cdk.module test-io
 */
public abstract class ChemObjectIOTest extends NewCDKTestCase {

    protected static IChemObjectIO chemObjectIO;

    public static void setChemObjectIO(IChemObjectIO aChemObjectIO) {
        chemObjectIO = aChemObjectIO;
    }

    @Test public void testChemObjectIOSet() {
        Assert.assertNotNull(
            "You must use setChemObjectIO() to set the IChemObjectIO object.",
            chemObjectIO
        );
    }

    @Test public void testGetFormat() {
        IResourceFormat format = chemObjectIO.getFormat();
        Assert.assertNotNull(
            "The IChemObjectIO.getFormat method returned null.",
            format
        );
    }

    @Test public void testAcceptsAtLeastOne() {
        Class[] objects = {
            IChemFile.class, IChemModel.class, IMolecule.class,
            IReaction.class
        };
        boolean oneAccepted = false;
        for (int i=0; (i<objects.length && !oneAccepted); i++) {
            if (chemObjectIO.accepts(objects[i])) {
                oneAccepted = true;
            }
        }
        Assert.assertTrue("At least one of the following IChemObect's should be accepted: IChemFile, IChemModel, IMolecule, IReaction", oneAccepted);
    }

}
