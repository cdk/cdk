/* $Revision: 11005 $ $Author: miguelrojasch $ $Date: 2008-05-15 16:24:13 +0200 (Thu, 15 May 2008) $
 *
 * Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.tools;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;

/**
* TestSuite that runs all tests.
*
* @cdk.module test-ionpot
*/
public class IonizationPotentialToolTest  extends NewCDKTestCase{

	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
    LonePairElectronChecker lpcheck = new LonePairElectronChecker();
	/**
	 * Constructor of the IonizationPotentialToolTest.
	 */
	public IonizationPotentialToolTest() {
        super();
    }
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test public void testIonizationPotentialTool()	{
    	
		Assert.assertNotNull(new IonizationPotentialTool());
	}
    
}
