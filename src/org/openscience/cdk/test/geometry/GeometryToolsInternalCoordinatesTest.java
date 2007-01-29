/* $Revision: 7691 $ $Author: egonw $ $Date: 2007-01-11 12:47:48 +0100 (Thu, 11 Jan 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.geometry;

import javax.vecmath.Point2d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.geometry.GeometryToolsInternalCoordinates;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.nonotify.NNAtom;
import org.openscience.cdk.nonotify.NNAtomContainer;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class GeometryToolsInternalCoordinatesTest extends CDKTestCase {
    
    public GeometryToolsInternalCoordinatesTest(String name) {
        super(name);
    }
    
	public static Test suite() {
		return new TestSuite(GeometryToolsInternalCoordinatesTest.class);
	}

	public void testTranslateAllPositive_IAtomContainer() {
		IAtomContainer container = new NNAtomContainer();
		IAtom atom = new NNAtom(Elements.CARBON);
		atom.setPoint2d(new Point2d(-3, -2));
		container.addAtom(atom);
		GeometryToolsInternalCoordinates.translateAllPositive(container);
		assertTrue(0 <= atom.getPoint2d().x);
		assertTrue(0 <= atom.getPoint2d().y);
	}
	
}


