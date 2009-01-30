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

//import org.openscience.cdk.tools.LoggingTool;


/**			
 *  Cutoffs and Smoothing Functions.
 *
 *@author     vlabarta
 *@cdk.created    February 28, 2005
 *@cdk.module     forcefield
 * @cdk.svnrev  $Revision$
 */
public class SmoothingFunctions {
	double[] s = null;	// Smoothing function
	double cutoffr0 = 5;	// For Smoothing function (s)
	double cutoffr1 = 6;	// For Smoothing function (s)
	double dampingFactor = 1;	// For Smoothing function (s)
	//private LoggingTool logger;


	/**
	 *  Constructor for the SmoothingFunctions object
	 */
	public SmoothingFunctions() {        
		//logger = new LoggingTool(this);
	}


	/**
	 *  Calculate the smoothing function from atom distances.
	 *
	 *
	 *@param  atomDistances       3d distance between the atoms.
	 */
	public void setSmoothingFunction(double[] atomDistances) {
		
		s = new double[atomDistances.length];

		for (int i=0; i<atomDistances.length; i++) {

			if (atomDistances[i] < cutoffr0) {
				s[i] = 1;
			} else if (atomDistances[i] > cutoffr1) {
				s[i] = 0;
			} else {
				s[i] = 1 - dampingFactor * ((atomDistances[i] - cutoffr0) / (cutoffr1 - cutoffr0));
			}
			//logger.debug("s[" + i + "] = " + s[i]);
		}
	}


	/**
	 *  Get the smoothing function value for every atom distance.
	 *
	 *
	 *@return	smoothing function value for every atom distance.
	 */
	public double[] getSmoothingFunction() {
		return s;
	}	

}
