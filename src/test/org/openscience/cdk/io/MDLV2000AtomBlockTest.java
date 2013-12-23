/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *  
 * Contact: cdk-devel@lists.sourceforge.net
 *  
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above 
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.io;

import org.junit.Test;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 * @cdk.module test-io
 */
public class MDLV2000AtomBlockTest {

    private final MDLV2000Reader     reader  = new MDLV2000Reader();
    private final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    @Test public void lonePairAtomSymbol() throws Exception {
        assertTrue(MDLV2000Reader.isPseudoElement("LP"));
    }

    @Test public void atomListAtomSymbol() throws Exception {
        assertTrue(MDLV2000Reader.isPseudoElement("L"));
    }

    @Test public void heavyAtomSymbol() throws Exception {
        assertTrue(MDLV2000Reader.isPseudoElement("A"));
    }

    @Test public void hetroAtomSymbol() throws Exception {
        assertTrue(MDLV2000Reader.isPseudoElement("Q"));
    }

    @Test public void unspecifiedAtomSymbol() throws Exception {
        assertTrue(MDLV2000Reader.isPseudoElement("*"));
    }

    @Test public void rGroupAtomSymbol() throws Exception {
        assertTrue(MDLV2000Reader.isPseudoElement("R"));
    }

    @Test public void rGroupAtomSymbol_hash() throws Exception {
        assertTrue(MDLV2000Reader.isPseudoElement("R#"));
    }

    @Test public void rGroupAtomSymbol_digits() throws Exception {
        for (int i = 1; i < 100; i++)
            assertTrue(MDLV2000Reader.isPseudoElement("R" + i));
    }
    
    @Test public void invalidAtomSymbol() throws Exception {
        assertFalse(MDLV2000Reader.isPseudoElement("RNA"));
        assertFalse(MDLV2000Reader.isPseudoElement("DNA"));
        assertFalse(MDLV2000Reader.isPseudoElement("ACP"));
    }
    
    @Test public void readMDLCoordinate() throws Exception {
        assertThat(MDLV2000Reader.readMDLCoordinate("    7.8089", 0),
                   is(closeTo(7.8089, 0.1)));
    }

    @Test public void readMDLCoordinate_negative() throws Exception {
        assertThat(MDLV2000Reader.readMDLCoordinate("   -2.0012", 0),
                   is(closeTo(-2.0012, 0.1)));
    }

    @Test public void readMDLCoordinate_offset() throws Exception {
        assertThat(MDLV2000Reader.readMDLCoordinate("   -2.0012    7.8089", 10),
                   is(closeTo(7.8089, 0.1)));
    }

}
