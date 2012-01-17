/* 
 * Copyright (C) 2011 Jonathan Alvarsson <jonalv@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
package org.openscience.cdk.fingerprint;

import java.util.BitSet;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @cdk.module test-fingerprint
 */
public class KlekotaRothFingerprinterTest extends AbstractFingerprinterTest {

	public IFingerprinter getFingerprinter() {
		return new KlekotaRothFingerprinter();
	}
	
    @Test
    public void testGetSize() throws Exception {
        IFingerprinter printer = getFingerprinter();
        Assert.assertEquals(4860, printer.getSize());
    }

    @Test
    public void testFingerprint() throws Exception {
		SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IFingerprinter printer = getFingerprinter();

		BitSet bs1 = printer.getBitFingerprint(parser.parseSmiles("C=C-C#N")).asBitSet();
		BitSet bs2 = printer.getBitFingerprint(parser.parseSmiles("C=CCC(O)CC#N")).asBitSet();

        Assert.assertEquals(4860,printer.getSize());
        
        Assert.assertTrue(FingerprinterTool.isSubset(bs2, bs1));
    }
}
