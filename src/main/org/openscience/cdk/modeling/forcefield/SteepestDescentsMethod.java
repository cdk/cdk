/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Violeta Labarta <vlabarta@users.sf.net>
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
package org.openscience.cdk.modeling.forcefield;

import javax.vecmath.GVector;

//import org.openscience.cdk.tools.LoggingTool;


/**
 * Find a direction from a point of the coordinates space using the steepest descents approach.
 *
 * @author      vlabarta
 * @cdk.module  forcefield
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword steepest descent
 */
public class SteepestDescentsMethod {
	GVector sk = null;
	//private LoggingTool logger;


	public SteepestDescentsMethod() {        
		//logger = new LoggingTool(this);
	}


	/**
	 *  Constructor for the SteepestDescentsMethod object
	 *
	 *@param  coords3d  Coordinates from current point
	 */
	public SteepestDescentsMethod(GVector coords3d) {
		sk = new GVector(coords3d.getSize());
	}


	/**
	 *  sk=-gK/|gk|
	 *
	 * @param  gk  Gradient at coordinates Xk
	 */
	public void setSk(GVector gk) {
		sk.set(gk);
		sk.normalize();
		sk.scale(-1);
		//logger.debug("vectorS = " + sk);
		return;
	}

}

