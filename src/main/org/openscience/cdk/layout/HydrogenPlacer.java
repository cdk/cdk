/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.layout;

import java.util.HashMap;

import javax.vecmath.Point2d;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.geometry.GeometryToolsInternalCoordinates;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.LoggingTool;

/**
 * This is a wrapper class for some existing methods in AtomPlacer. It helps
 * you to layout 2D and 3D coordinates for hydrogen atoms added to a molecule
 * which already has coordinates for the rest of the atoms.
 *
 * @author       Christop Steinbeck
 * @cdk.created  2003-08-06
 * @cdk.module   sdg
 * @cdk.svnrev  $Revision$
 */
public class HydrogenPlacer {
    
	public final static boolean debug = false;
	public final static boolean debug1 = false;
	
	public  void placeHydrogens2D(IAtomContainer atomContainer, double bondLength){
		this.placeHydrogens2D(atomContainer,bondLength,null);
	}
	
	public  void placeHydrogens2D(IAtomContainer atomContainer, double bondLength, HashMap renderingCoordinates){
	    LoggingTool logger = new LoggingTool(this);
	    logger.debug("Entering Hydrogen Placement...");
	    IAtom atom = null; 
	    for (int f = 0; f < atomContainer.getAtomCount();f++)
	    {
	        atom = atomContainer.getAtom(f);
//	        if (!atom.getSymbol().equals("H"))
//	        {
	            if (debug1) System.out.println("Now placing hydrogens at atom " + f);
	            logger.debug("Now placing hydrogens at atom " + f);
	            placeHydrogens2D(atomContainer, atom, bondLength, renderingCoordinates);
//	        }
	    }
	    logger.debug("Hydrogen Placement finished");
	}
	
	
	public  void placeHydrogens2D(IAtomContainer atomContainer, IAtom atom)
	{
		double bondLength = GeometryToolsInternalCoordinates.getBondLengthAverage(atomContainer);
		placeHydrogens2D(atomContainer, atom, bondLength);
		
	
	}
	
	public  void placeHydrogens2D(IAtomContainer atomContainer, IAtom atom, double bondLength){
		placeHydrogens2D(atomContainer, atom, bondLength, null);
	}
	
	public  void placeHydrogens2D(IAtomContainer atomContainer, IAtom atom, double bondLength, HashMap renderingCoordinates)
	{
		LoggingTool logger = new LoggingTool(this);
		
		//double startAngle = 0.0;
		//double addAngle = 0.0; 
		AtomPlacer atomPlacer = new AtomPlacer();
		atomPlacer.setMolecule(atomContainer);
		//Vector atomVector = new Vector();
		logger.debug("bondLength ", bondLength);
		java.util.List connectedAtoms = atomContainer.getConnectedAtomsList(atom);
		IAtomContainer placedAtoms = atomContainer.getBuilder().newAtomContainer();
		IAtomContainer unplacedAtoms = atomContainer.getBuilder().newAtomContainer();
		
		for (int f = 0; f < connectedAtoms.size(); f++) {
			IAtom conAtom = (IAtom)connectedAtoms.get(f);
			if (conAtom.getSymbol().equals("H") && conAtom.getPoint2d()==null) {
				unplacedAtoms.addAtom(conAtom);
			} else {
				placedAtoms.addAtom(conAtom);
			}
		}
		logger.debug("Atom placement before procedure:");
		logger.debug("Center atom ", atom.getSymbol(), ": ", atom.getPoint2d());
		for (int f = 0; f < unplacedAtoms.getAtomCount(); f++)
		{
			logger.debug("H-" + f, ": ", unplacedAtoms.getAtom(f).getPoint2d());
		}
        Point2d centerPlacedAtoms = null;
        if (placedAtoms.getAtomCount() > 0) {
        	if(renderingCoordinates==null)
        		centerPlacedAtoms = GeometryToolsInternalCoordinates.get2DCenter(placedAtoms);
        	else
        		centerPlacedAtoms = GeometryTools.get2DCenter(placedAtoms, renderingCoordinates);
        } else {
        	if(renderingCoordinates==null)
        		centerPlacedAtoms = atom.getPoint2d();
        	else
        		centerPlacedAtoms = (Point2d)renderingCoordinates.get(atom);
        }
        if(renderingCoordinates==null)
        	atomPlacer.distributePartners(atom, placedAtoms, centerPlacedAtoms, unplacedAtoms, bondLength);
        else
        	atomPlacer.distributePartners(atom, placedAtoms, centerPlacedAtoms, unplacedAtoms, bondLength, renderingCoordinates);
		logger.debug("Atom placement after procedure:");
		logger.debug("Center atom ", atom.getSymbol(), ": ", atom.getPoint2d());
		for (int f = 0; f < unplacedAtoms.getAtomCount(); f++)
		{
			logger.debug("H-" + f, ": ", renderingCoordinates!=null ? ((Point2d)renderingCoordinates.get(unplacedAtoms.getAtom(f))) : unplacedAtoms.getAtom(f).getPoint2d());
		}				
	}
}

