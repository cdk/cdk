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

import org.junit.Test;
import org.openscience.cdk.io.formats.GamessFormat;
import org.openscience.cdk.io.formats.INChIFormat;
import org.openscience.cdk.io.formats.INChIPlainTextFormat;
import org.openscience.cdk.io.formats.VASPFormat;

/**
 * TestCase for the instantiation and functionality of the {@link ReaderFactory} for
 * io classes currently in 'cdk-extra'.
 *
 * @cdk.module test-extra
 */
public class ExtraReaderFactoryTest extends AbstractReaderFactoryTest {

    private ReaderFactory factory = new ReaderFactory();

    @Test
    public void testINChI() throws Exception {
        expectReader("data/inchi/guanine.inchi.xml", INChIFormat.getInstance(), -1, -1);
    }

    @Test
    public void testINChIPlainText() throws Exception {
        expectReader("data/inchi/guanine.inchi", INChIPlainTextFormat.getInstance(), -1, -1);
    }

    @Test
    public void testVASP() throws Exception {
        expectReader("data/vasp/LiMoS2_optimisation_ISIF3.vasp", VASPFormat.getInstance(), -1, -1);
    }

    @Test
    public void testGamess() throws Exception {
        expectReader("data/gamess/ch3oh_gam.out", GamessFormat.getInstance(), -1, -1);
    }

}
