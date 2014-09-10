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
package org.openscience.cdk.io;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.silent.ChemFile;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;

/**
 * TestCase for the reading CIF files using a test file with the {@link CIFReader}.
 *
 * @cdk.module test-io
 */
public class CIFReaderTest extends ChemObjectIOTest {

    @BeforeClass
    public static void setup() {
        setChemObjectIO(new CIFReader());
    }

    /**
     * Ensure a CIF file from the crystallography open database can be read.
     * Example input <a href="http://www.crystallography.net/1100784.cif">1100784</a>.
     */
    @Test
    public void cod1100784() throws IOException, CDKException {
        InputStream in = getClass().getResourceAsStream("1100784.cif");
        CIFReader cifReader = new CIFReader(in);
        //        try {
        IChemFile chemFile = cifReader.read(new ChemFile());
        Assert.assertThat(chemFile.getChemSequenceCount(), is(1));
        Assert.assertThat(chemFile.getChemSequence(0).getChemModelCount(), is(1));
        Assert.assertNotNull(chemFile.getChemSequence(0).getChemModel(0).getCrystal());
        //        } finally {
        cifReader.close();
        //        }
    }

}
