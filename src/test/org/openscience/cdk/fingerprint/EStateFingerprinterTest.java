/* $Revision: 10903 $ $Author: egonw $ $Date: 2008-05-07 09:48:07 -0400 (Wed, 07 May 2008) $    
 * 
 * Copyright (C) 2008 Rajarshi Guha
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * Contact: rajarshi@users.sourceforge.net
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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LoggingTool;

import java.util.BitSet;

/**
 * @cdk.module test-fingerprint
 */
public class EStateFingerprinterTest extends NewCDKTestCase {

	private static LoggingTool logger = new LoggingTool(EStateFingerprinterTest.class);

	public EStateFingerprinterTest() {
		super();
	}

    @Test
    public void testFingerprint() throws Exception {
		SmilesParser parser = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
		IFingerprinter printer = new EStateFingerprinter();

		BitSet bs1 = printer.getFingerprint(parser.parseSmiles("C=C-C#N"));
		BitSet bs2 = printer.getFingerprint(parser.parseSmiles("C=CCC(O)CC#N"));

        Assert.assertEquals(79,printer.getSize());
        
        Assert.assertTrue(bs1.get(7));
        Assert.assertTrue(bs1.get(10));
        Assert.assertTrue(FingerprinterTool.isSubset(bs2, bs1));
    }

}