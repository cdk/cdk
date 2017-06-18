/* Copyright (C) 2001-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *               2013,2016  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

/**
 * List of isotopes. Data is taken from the <a href="https://github.com/egonw/bodr">Blue Obelisk Data Repository</a>,
 * <a href="https://github.com/egonw/bodr/releases/tag/BODR-10">version 10</a> {@cdk.cite BODR10}.
 * The data set is described in the first Blue Obelisk paper {@cdk.cite Guha2006}.
 *
 * <p>The <code>isotopes.dat</code> file that is used by this class is a binary class
 * of this data, improving loading times over the BODR XML representation. It is created
 * from the original BODR files using tools from the <code>cdk-build-util</code>
 * repository.
 *
 * @author      egonw
 * @cdk.module  core
 * @cdk.githash
 */
public class Isotopes extends IsotopeFactory {

    private static Isotopes myself = null;

    /**
     * Returns a singleton instance of this class.
     *
     * @return the singleton
     * @throws IOException when reading of the data file did not work
     */
    public static Isotopes getInstance() throws IOException {
        if (myself == null) myself = new Isotopes();
        return myself;
    }

    private Isotopes() throws IOException {
        String configFile = "org/openscience/cdk/config/data/isotopes.dat";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(configFile);
        int streamSize = ins.available();
        ReadableByteChannel fcIn = Channels.newChannel(ins);
        ByteBuffer bin = ByteBuffer.allocate(streamSize);
        fcIn.read(bin);
        fcIn.close();
        ins.close();
        bin.position(0);
        int isotopeCount = bin.getInt();
        for (int i = 0; i < isotopeCount; i++) {
            int atomicNum = (int) bin.get();
            int massNum = (int) bin.getShort();
            double exactMass = bin.getDouble();
            double natAbund = bin.get() == 1 ? bin.getDouble() : 0.0;
            IIsotope isotope = new BODRIsotope(PeriodicTable.getSymbol(atomicNum), atomicNum, massNum, exactMass,
                    natAbund);
            add(isotope);
        }
    }
}
