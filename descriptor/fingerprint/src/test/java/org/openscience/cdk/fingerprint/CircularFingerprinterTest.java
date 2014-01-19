
/* $Revision$ $Author$ $Date$
 *
 * Copyright (c) 2014 Collaborative Drug Discovery, Inc. <alex@collaborativedrug.com>
 *
 * Implemented by Alex M. Clark, produced by Collaborative Drug Discovery, Inc.
 * Made available to the CDK community under the terms of the GNU LGPL.
 *
 *    http://collaborativedrug.com
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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.BitSet;

/**
 * @cdk.module test-standard
 */
public class CircularFingerprinterTest extends AbstractFixedLengthFingerprinterTest
{
	private static ILoggingTool logger=LoggingToolFactory.createLoggingTool(CircularFingerprinterTest.class);

    @Test
    public void testFingerprints() throws Exception 
    {
    	logger.info("CircularFingerprinter test: loading source materials");
    	//if (true) throw new CDKException("It hurts!");
    
		/*SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
		IFingerprinter printer = new GraphOnlyFingerprinter();
		
		IBitFingerprint bs1 = printer.getBitFingerprint(parser.parseSmiles("C=C-C#N"));
		System.out.println("----");
		IBitFingerprint bs2 = printer.getBitFingerprint(parser.parseSmiles("CCCN"));
		
		Assert.assertEquals(bs1, bs2);*/
		
		logger.info("CircularFingerprinter test: completed without any problems");
	}
}

