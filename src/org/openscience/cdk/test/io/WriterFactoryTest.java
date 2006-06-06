/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-05-11 18:34:42 +0200 (Thu, 11 May 2006) $
 * $Revision: 6228 $
 *
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.io;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.io.WriterFactory;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.DataFeatures;

/**
 * TestCase for the reading CML files using a few test files
 * in data/cmltest as found in the Jmol distribution
 * (http://jmol.sf.org/).
 *
 * @cdk.module test-io
 */
public class WriterFactoryTest extends CDKTestCase {

    private WriterFactory factory;
    
    public WriterFactoryTest(String name) {
        super(name);
        factory = new WriterFactory();
    }

    public static Test suite() {
        return new TestSuite(WriterFactoryTest.class);
    }

    public void testFindChemFormats() {
        IChemFormat[] formats = factory.findChemFormats(DataFeatures.HAS_3D_COORDINATES);
        assertNotNull(formats);
        assertTrue(formats.length > 0);
    }
}
