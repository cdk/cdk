/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  
 */
package org.openscience.cdk.layout;

import org.openscience.cdk.*;
import org.openscience.cdk.ringsearch.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.graph.PathTools;
import javax.vecmath.*;
import java.util.Vector;
import java.lang.Math;
import java.awt.*;

/**
 * This is a wrapper class for some existing methods in AtomPlacer. It helps
 * you to layout 2D and 3D coordinates for hydrogen atoms added to a molecule
 * which already has coordinates for the rest of the atoms  
 **/

public class HydrogenPlacer
{
	public static boolean debug = true;
	
	public static void placeHydrogens2D(AtomContainer atomContainer, Atom atom)
	{
		double bondLength = GeometryTools.getScaleFactor(atomContainer, 1.0);
		AtomPlacer atomPlacer = new AtomPlacer();
		atomPlacer.setMolecule((Molecule)atomContainer);
		Vector atomVector = new Vector();
		if (debug) System.out.println("bondLength" + bondLength);
		Atom[] connectedAtoms = atomContainer.getConnectedAtoms(atom);
		AtomContainer placedAtoms = new AtomContainer();
		AtomContainer unplacedAtoms = new AtomContainer();
		for (int f = 0; f < connectedAtoms.length; f++)
		{
			if (connectedAtoms[f].getSymbol().equals("H"))
			{
				unplacedAtoms.addAtom(connectedAtoms[f]);
			}
			else
			{
				placedAtoms.addAtom(connectedAtoms[f]);
			}
		}
		placedAtoms.addAtom(atom);
		for (int f = 0; f < placedAtoms.getAtomCount(); f++)
		{
			atomVector.addElement(placedAtoms.getAtomAt(f));
		}

		atomPlacer.distributePartners(atom, placedAtoms, placedAtoms.get2DCenter(), unplacedAtoms, bondLength);
		if (debug) System.out.println("unplacedAtoms: " + unplacedAtoms);
		if (debug) System.out.println("placedAtoms: " + placedAtoms);
				
	}
}
