/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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
 * which already has coordinates for the rest of the atoms.
 *
 * @author   Christop Steinbeck
 * @cdk.created  2003-08-06
 */
public class HydrogenPlacer {
    
	public static boolean debug = false;
	public static boolean debug1 = false;
	
	public  void placeHydrogens2D(AtomContainer atomContainer, double bondLength){
		LoggingTool logger = new LoggingTool("org.openscience.cdk.layout.HydrogenPlacer");
		logger.debug("Entering Hydrogen Placement...");
		Atom atom = null; 
		for (int f = 0; f < atomContainer.getAtomCount();f++)
		{
			atom = atomContainer.getAtomAt(f);
			if (!atom.getSymbol().equals("H"))
			{
				if (debug1) System.out.println("Now placing hydrogens at atom " + f);
				logger.debug("Now placing hydrogens at atom " + f);
        placeHydrogens2D(atomContainer, atom, bondLength);
   		}
		}
		logger.debug("Hydrogen Placement finished");
	}
	
	
	public  void placeHydrogens2D(AtomContainer atomContainer, Atom atom)
	{
		double bondLength = GeometryTools.getBondLengthAverage(atomContainer);
		placeHydrogens2D(atomContainer, atom, bondLength);
		
	
	}
	
	public  void placeHydrogens2D(AtomContainer atomContainer, Atom atom, double bondLength)
	{
		LoggingTool logger = new LoggingTool("org.openscience.cdk.layout.HydrogenPlacer");
		
		double startAngle = 0.0;
		double addAngle = 0.0; 
		AtomPlacer atomPlacer = new AtomPlacer();
		atomPlacer.setMolecule((Molecule)atomContainer);
		Vector atomVector = new Vector();
		logger.debug("bondLength ", bondLength);
		Atom[] connectedAtoms = atomContainer.getConnectedAtoms(atom);
		AtomContainer placedAtoms = new AtomContainer();
		AtomContainer unplacedAtoms = new AtomContainer();
		
		for (int f = 0; f < connectedAtoms.length; f++) {
			if (connectedAtoms[f].getSymbol().equals("H") && connectedAtoms[f].getPoint2d()==null) {
		    unplacedAtoms.addAtom(connectedAtoms[f]);
			} else {
				placedAtoms.addAtom(connectedAtoms[f]);
			}
		}
		logger.debug("Atom placement before procedure:");
		logger.debug("Center atom ", atom.getSymbol(), ": ", atom.getPoint2d());
		for (int f = 0; f < unplacedAtoms.getAtomCount(); f++)
		{
			logger.debug("H-" + f, ": ", unplacedAtoms.getAtomAt(f).getPoint2d());
		}
        Point2d centerPlacedAtoms = null;
        if (placedAtoms.getAtomCount() > 0) {
            centerPlacedAtoms = GeometryTools.get2DCenter(placedAtoms);
        } else {
            centerPlacedAtoms = atom.getPoint2d();
        }
		atomPlacer.distributePartners(atom, placedAtoms, centerPlacedAtoms, unplacedAtoms, bondLength);
		logger.debug("Atom placement after procedure:");
		logger.debug("Center atom ", atom.getSymbol(), ": ", atom.getPoint2d());
		for (int f = 0; f < unplacedAtoms.getAtomCount(); f++)
		{
			logger.debug("H-" + f, ": ", unplacedAtoms.getAtomAt(f).getPoint2d());
		}				
	}
}

