/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.openscience.cdk.test.qsar;

import org.openscience.cdk.qsar.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.exception.CDKException;

public class IsProtonInAromaticSystemDescriptorTest extends TestCase {
	
	public  IsProtonInAromaticSystemDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(IsProtonInAromaticSystemDescriptorTest.class);
	}
	
	public void testIsProtonInAromaticSystemDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Descriptor descriptor = new IsProtonInAromaticSystemDescriptor();
		Object[] params = {new Integer(0), new Boolean(false)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		AtomContainer mol = sp.parseSmiles("Cc1ccccc1"); 
		assertFalse(((Boolean)descriptor.calculate(mol)).booleanValue());
	}
}

