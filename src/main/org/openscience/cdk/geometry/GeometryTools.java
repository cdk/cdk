/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.geometry;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A set of static utility classes for geometric calculations and operations.
 * This class is extensively used, for example, by JChemPaint to edit molecule.
 * All methods in this class use an external set of coordinates (e. g. from the rendererModel). If you want to change the coordinates in the atoms, use GeometryToolsInternalCoordinates.
 *
 * @author 		  Christopn Steinbeck
 * @author        seb
 * @author        Stefan Kuhn
 * @author        Egon Willighagen
 * @author        Ludovic Petain
 * @author        Christian Hoppe
 * 
 * @cdk.module    standard
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.geometry.GeometryToolsTest")
public class GeometryTools {

	private static LoggingTool logger = new LoggingTool(GeometryTools.class);

	/**
	 *  Gets the angle attribute of the GeometryTools class
	 *
	 *@param  xDiff  Description of the Parameter
	 *@param  yDiff  Description of the Parameter
	 *@return        The angle value
	 */
	public static double getAngle(double xDiff, double yDiff) {
		return GeometryToolsInternalCoordinates.getAngle(xDiff, yDiff);
	}

	/**
	 *  Gets the coordinates of two points (that represent a bond) and calculates
	 *  for each the coordinates of two new points that have the given distance
	 *  vertical to the bond.
	 *
	 *@param  coords  The coordinates of the two given points of the bond like this
	 *      [point1x, point1y, point2x, point2y]
	 *@param  dist    The vertical distance between the given points and those to
	 *      be calculated
	 *@return         The coordinates of the calculated four points
	 */
	public static int[] distanceCalculator(int[] coords, double dist) {
		return GeometryToolsInternalCoordinates.distanceCalculator(coords, dist);
	}
	
	/**
	 *  Determines if this AtomContainer contains 2D coordinates.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  m  Description of the Parameter
	 *@return    boolean indication that 2D coordinates are available
	 */
    @TestMethod("testHas2DCoordinates_IAtomContainer,testHas2DCoordinates_EmptyAtomContainer,testHas2DCoordinatesNew_IAtomContainer")
    public static boolean has2DCoordinates(IAtomContainer m) {
		return GeometryToolsInternalCoordinates.has2DCoordinatesNew(m)>0;
	}

    /**
     * Determines if this model contains 3D coordinates
     *
     * @param model The model
     * @return boolean indication that 3D coordinates are available
     */
    public static boolean has3DCoordinates(IChemModel model) {
        for (Object o : ChemModelManipulator.getAllAtomContainers(model)) {
            IAtomContainer ac = (IAtomContainer) o;
            boolean hasCoords = GeometryToolsInternalCoordinates.has3DCoordinates(ac);
            if (!hasCoords) return false;
        }
        return true;
	}
		
	/**
	 *  Determines if this model contains 3D coordinates
	 *
	 *@param  ac  Description of the Parameter
	 *@return    boolean indication that 3D coordinates are available
	 */
    @TestMethod("testHas3DCoordinates_IAtomContainer")
    public static boolean has3DCoordinates(IAtomContainer ac) {
		return GeometryToolsInternalCoordinates.has3DCoordinates(ac);
	}

    /**
     * Determines if this AtomContainer contains 2D coordinates for some or all molecules.
     * See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
     *
     * @param model The model
     * @return 0 no 2d, 1=some, 2= for each atom
     */
    public static int has2DCoordinatesNew(IChemModel model) {
        Iterator containers = ChemModelManipulator.getAllAtomContainers(model).iterator();
        int oldCoords = -1;
        while (containers.hasNext()) {
            IAtomContainer ac = (IAtomContainer) containers.next();
            int hasCoords = GeometryToolsInternalCoordinates.has2DCoordinatesNew(ac);
            if (hasCoords == 1) return 1;
            if (oldCoords != -1 && oldCoords != hasCoords) return 1;
            oldCoords = hasCoords;
        }
        return oldCoords;
	}

	/**
	 *  Determines if this AtomContainer contains 2D coordinates for some or all molecules.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  ac  Description of the Parameter
	 *@return    0 no 2d, 1=some, 2= for each atom
	 */
	public static int has2DCoordinatesNew(IAtomContainer ac) {
		return  GeometryToolsInternalCoordinates.has2DCoordinatesNew(ac);
	}
}


