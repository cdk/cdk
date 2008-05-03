/* $RCSfile: $
 * $Author$
 * $Date$  
 * $Revision$
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
 *
 */
package org.openscience.cdk.tools;

import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.features.MoleculeFeaturesTool;

/**
 * Utility that helps determine which data features are present.
 * 
 * @author egonw
 * @cdk.svnrev  $Revision$
 * 
 * @see    org.openscience.cdk.tools.DataFeatures
 */
public class DataFeaturesTool {

	/**
	 * Determines the features present in the given IMolecule.
	 *  
	 * @param molecule IMolecule to determine the features off
	 * 
	 * @return integer representation of the present features 
	 */
	public static int getSupportedDataFeatures(IMolecule molecule) {
		int features = DataFeatures.NONE;
		if (MoleculeFeaturesTool.hasElementSymbols(molecule))
			features = features | DataFeatures.HAS_ATOM_ELEMENT_SYMBOL;
		if (GeometryTools.has2DCoordinates(molecule))
			features = features | DataFeatures.HAS_2D_COORDINATES;
		if (GeometryTools.has3DCoordinates(molecule))
			features = features | DataFeatures.HAS_3D_COORDINATES;
		if (CrystalGeometryTools.hasCrystalCoordinates(molecule))
			features = features | DataFeatures.HAS_FRACTIONAL_CRYSTAL_COORDINATES;
		if (MoleculeFeaturesTool.hasFormalCharges(molecule))
			features = features | DataFeatures.HAS_ATOM_FORMAL_CHARGES;
		if (MoleculeFeaturesTool.hasPartialCharges(molecule))
			features = features | DataFeatures.HAS_ATOM_PARTIAL_CHARGES;
		if (MoleculeFeaturesTool.hasGraphRepresentation(molecule))
			features = features | DataFeatures.HAS_GRAPH_REPRESENTATION;
		return features;
	}
	
	
}
