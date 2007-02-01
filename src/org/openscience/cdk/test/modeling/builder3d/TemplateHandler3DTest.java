/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-31 18:46:38 +0100 (Wed, 31 Jan 2007) $
 *
 *  Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.test.modeling.builder3d;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.modeling.builder3d.TemplateHandler3D;
import org.openscience.cdk.test.CDKTestCase;
/**
 *  Description of the Class
 *
 * @cdk.module test-builder3d
 *
 *@author     chhoppe
 *@author     Christoph Steinbeck
 *@cdk.created    2004-11-04
 */
public class TemplateHandler3DTest extends CDKTestCase {
	
	boolean standAlone = false;
	private List inputList = null;
	

	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(TemplateHandler3DTest.class);
	}
	
	/**
	 *  Sets the standAlone attribute 
	 *
	 *@param  standAlone  The new standAlone value
	 */
	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}
	
	public void testGetInstance()
	{
		TemplateHandler3D th3d = null;
		try
		{
			  th3d = TemplateHandler3D.getInstance();
		}
		catch(CDKException exc)
		{
			fail();
		}
		//System.out.println("TemplateCount = " + th3d.getTemplateCount());
		assertEquals(th3d.getTemplateCount(), 10751);
	
	}
}
