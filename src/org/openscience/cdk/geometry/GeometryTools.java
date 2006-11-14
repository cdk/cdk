/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A set of static utility classes for geometric calculations and operations.
 * This class is extensively used, for example, by JChemPaint to edit molecule.
 * All methods in this class use an external set of coordinates (e. g. from the rendererModel). If you want to change the coordinates in the atoms, use GeometryToolsInternalCoordinates.
 *
 * @author        seb
 * @author        Stefan Kuhn
 * @author        Egon Willighagen
 * @author        Ludovic Petain
 * @author        Christian Hoppe
 * 
 * @cdk.module    standard
 */
public class GeometryTools {

	private static LoggingTool logger = new LoggingTool(GeometryTools.class);


	/**
	 *  Adds an automatically calculated offset to the coordinates of all atoms
	 *  such that all coordinates are positive and the smallest x or y coordinate
	 *  is exactly zero, using an external set of coordinates.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atomCon  AtomContainer for which all the atoms are translated to
	 *      positive coordinates
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 */
	public static void translateAllPositive(IAtomContainer atomCon,HashMap renderingCoordinates) {
		double minX = Double.MAX_VALUE;
		double
				minY = Double.MAX_VALUE;
		java.util.Iterator atoms = atomCon.atoms();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
			if (renderingCoordinates.get(atom) == null && atom.getPoint2d()!=null) {
				renderingCoordinates.put(atom,new Point2d(atom.getPoint2d().x,atom.getPoint2d().y));
			}
			if (renderingCoordinates.get(atom) != null) {
				if (((Point2d)renderingCoordinates.get(atom)).x < minX) {
					minX = ((Point2d)renderingCoordinates.get(atom)).x;
				}
				if (((Point2d)renderingCoordinates.get(atom)).y < minY) {
					minY = ((Point2d)renderingCoordinates.get(atom)).y;
				}
			}
		}
		logger.debug("Translating: minx=" + minX + ", minY=" + minY);
		translate2D(atomCon, minX * -1, minY * -1, renderingCoordinates);
	}


	/**
	 *  Translates the given molecule by the given Vector, using an external set of coordinates.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atomCon  The molecule to be translated
	 *@param  transX   translation in x direction
	 *@param  transY   translation in y direction
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 */
	public static void translate2D(IAtomContainer atomCon, double transX, double transY,HashMap renderingCoordinates) {
		translate2D(atomCon, new Vector2d(transX, transY), renderingCoordinates);
	}


	/**
	 *  Multiplies all the coordinates of the atoms of the given molecule with the
	 *  scalefactor, using an external set of coordinates..
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atomCon      The molecule to be scaled
	 *@param  scaleFactor  Description of the Parameter
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 */
	public static void scaleMolecule(IAtomContainer atomCon, double scaleFactor, HashMap renderingCoordinates) {
		for (int i = 0; i < atomCon.getAtomCount(); i++) {
			if(renderingCoordinates.get(atomCon.getAtom(i))!=null){
				((Point2d)renderingCoordinates.get(atomCon.getAtom(i))).x *= scaleFactor;
				((Point2d)renderingCoordinates.get(atomCon.getAtom(i))).y *= scaleFactor;
			}
		}
	}


    /**
	 *  Scales a molecule such that it fills a given percentage of a given
	 *  dimension, using an external set of coordinates
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atomCon     The molecule to be scaled
	 *@param  areaDim     The dimension to be filled
	 *@param  fillFactor  The percentage of the dimension to be filled
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 */
	public static void scaleMolecule(IAtomContainer atomCon, Dimension areaDim, double fillFactor, HashMap renderingCoordinates) {
		Dimension molDim = get2DDimension(atomCon, renderingCoordinates);
		double widthFactor = (double) areaDim.width / (double) molDim.width;
		double heightFactor = (double) areaDim.height / (double) molDim.height;
		double scaleFactor = Math.min(widthFactor, heightFactor) * fillFactor;
		scaleMolecule(atomCon, scaleFactor, renderingCoordinates);
	}


	/**
	 *  Returns the java.awt.Dimension of a molecule
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atomCon  of which the dimension should be returned
	 *@return          The java.awt.Dimension of this molecule
	 */
	public static Dimension get2DDimension(IAtomContainer atomCon, HashMap renderingCoordinates) {
		double[] minmax = getMinMax(atomCon, renderingCoordinates);
		double maxX = minmax[2];
		double
				maxY = minmax[3];
		double
				minX = minmax[0];
		double
				minY = minmax[1];
		return new Dimension((int) (maxX - minX + 1), (int) (maxY - minY + 1));
	}
	
	/**
	 *  Returns the java.awt.Dimension of a MoleculeSet
	 *  See comment for center(IMoleculeSet setOfMolecules, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  setOfMolecules Of which the dimension should be returned
	 *@return The java.awt.Dimension of this MoleculeSet
	 */
	public static Dimension get2DDimension(IMoleculeSet setOfMolecules, HashMap renderingCoordinates) {
		double[] minmax = getMinMax(setOfMolecules, renderingCoordinates);
		double maxX = minmax[2];
		double
				maxY = minmax[3];
		double
				minX = minmax[0];
		double
				minY = minmax[1];
		return new Dimension((int) (maxX - minX + 1), (int) (maxY - minY + 1));
	}


	/**
	 *  Returns the minimum and maximum X and Y coordinates of the atoms in the
	 *  AtomContainer. The output is returned as: <pre>
	 *   minmax[0] = minX;
	 *   minmax[1] = minY;
	 *   minmax[2] = maxX;
	 *   minmax[3] = maxY;
	 * </pre>
	 * See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  container  Description of the Parameter
	 *@return            An four int array as defined above.
	 */
	public static double[] getMinMax(IAtomContainer container, HashMap renderingCoordinates) {
		double maxX = Double.MIN_VALUE;
		double
				maxY = Double.MIN_VALUE;
		double
				minX = Double.MAX_VALUE;
		double
				minY = Double.MAX_VALUE;
		for (int i = 0; i < container.getAtomCount(); i++) {
			IAtom atom = container.getAtom(i);
			if (renderingCoordinates.get(atom) != null) {
				if (((Point2d)renderingCoordinates.get(atom)).x > maxX) {
					maxX = ((Point2d)renderingCoordinates.get(atom)).x;
				}
				if (((Point2d)renderingCoordinates.get(atom)).x < minX) {
					minX = ((Point2d)renderingCoordinates.get(atom)).x;
				}
				if (((Point2d)renderingCoordinates.get(atom)).y > maxY) {
					maxY = ((Point2d)renderingCoordinates.get(atom)).y;
				}
				if (((Point2d)renderingCoordinates.get(atom)).y < minY) {
					minY = ((Point2d)renderingCoordinates.get(atom)).y;
				}
			}
		}
		double[] minmax = new double[4];
		minmax[0] = minX;
		minmax[1] = minY;
		minmax[2] = maxX;
		minmax[3] = maxY;
		return minmax;
	}
	
	/**
	 *  Returns the minimum and maximum X and Y coordinates of the molecules in the
	 *  MoleculeSet. The output is returned as: <pre>
	 *   minmax[0] = minX;
	 *   minmax[1] = minY;
	 *   minmax[2] = maxX;
	 *   minmax[3] = maxY;
	 * </pre>
	 * See comment for center(IMoleculeSet setOfMolecules, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@return            An four int array as defined above.
	 */
	public static double[] getMinMax(IMoleculeSet setOfMolecules, HashMap renderingCoordinates) {
		double maxX = Double.MIN_VALUE;
		double
				maxY = Double.MIN_VALUE;
		double
				minX = Double.MAX_VALUE;
		double
				minY = Double.MAX_VALUE;
		for(int j = 0; j < setOfMolecules.getAtomContainerCount() ; j++){
			IAtomContainer container = setOfMolecules.getAtomContainer(j);
			for (int i = 0; i < container.getAtomCount(); i++) {
				IAtom atom = container.getAtom(i);
				if (renderingCoordinates.get(atom) != null) {
					if (((Point2d)renderingCoordinates.get(atom)).x > maxX) {
						maxX = ((Point2d)renderingCoordinates.get(atom)).x;
					}
					if (((Point2d)renderingCoordinates.get(atom)).x < minX) {
						minX = ((Point2d)renderingCoordinates.get(atom)).x;
					}
					if (((Point2d)renderingCoordinates.get(atom)).y > maxY) {
						maxY = ((Point2d)renderingCoordinates.get(atom)).y;
					}
					if (((Point2d)renderingCoordinates.get(atom)).y < minY) {
						minY = ((Point2d)renderingCoordinates.get(atom)).y;
					}
				}
			}
		}
		double[] minmax = new double[4];
		minmax[0] = minX;
		minmax[1] = minY;
		minmax[2] = maxX;
		minmax[3] = maxY;
		return minmax;
	}


	/**
	 *  Translates a molecule from the origin to a new point denoted by a vector, using an external set of coordinates.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atomCon  molecule to be translated
	 *@param  vector   dimension that represents the translation vector
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 */
	public static void translate2D(IAtomContainer atomCon, Vector2d vector, HashMap renderingCoordinates) {
		java.util.Iterator atoms = atomCon.atoms();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
			if (renderingCoordinates.get(atom) == null && atom.getPoint2d()!=null) {
				renderingCoordinates.put(atom,new Point2d(atom.getPoint2d().x,atom.getPoint2d().y));
			}
			if (renderingCoordinates.get(atom) != null) {
				((Point2d)renderingCoordinates.get(atom)).add(vector);
			} else {
				logger.warn("Could not translate atom in 2D space");
			}
		}
	}
	
	/**
	 *  Translates a molecule from the origin to a new point denoted by a vector.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atomCon  molecule to be translated
	 *@param  p        Description of the Parameter
	 */
	public static void translate2DCentreOfMassTo(IAtomContainer atomCon, Point2d p, HashMap renderingCoordinates) {
		Point2d com = get2DCentreOfMass(atomCon, renderingCoordinates);
		Vector2d translation = new Vector2d(p.x - com.x, p.y - com.y);
		java.util.Iterator atoms = atomCon.atoms();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
			if (renderingCoordinates.get(atom) != null) {
				((Point2d)renderingCoordinates.get(atom)).add(translation);
			}
		}
	}

	
	/**
	 *  Translates the geometric 2DCenter of the given
	 *  AtomContainer container to the specified Point2d p.
	 *
	 *@param  container  AtomContainer which should be translated.
	 *@param  p          New Location of the geometric 2D Center.
	 *@see #get2DCenter
	 *@see #translate2DCentreOfMassTo
	 */
	public static void translate2DCenterTo(IAtomContainer container, Point2d p, HashMap renderingCoordinates) {
		Point2d com = get2DCenter(container, renderingCoordinates);
		Vector2d translation = new Vector2d(p.x - com.x, p.y - com.y);
		java.util.Iterator atoms = container.atoms();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
			if (renderingCoordinates.get(atom) != null) {
				((Point2d)renderingCoordinates.get(atom)).add(translation);
			}
		}
	}
	
	/**
	 *  Calculates the center of mass for the <code>Atom</code>s in the
	 *  AtomContainer for the 2D coordinates.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  ac      AtomContainer for which the center of mass is calculated
	 *@return         Description of the Return Value
	 *@cdk.keyword    center of mass
	 */
	public static Point2d get2DCentreOfMass(IAtomContainer ac, HashMap renderingCoordinates) {
		double x = 0.0;
		double y = 0.0;

		double totalmass = 0.0;

		java.util.Iterator atoms = ac.atoms();
		while (atoms.hasNext()) {
			IAtom a = (IAtom) atoms.next();
			double mass = a.getExactMass();
			totalmass += mass;
			x += mass * ((Point2d)renderingCoordinates.get(a)).x;
			y += mass * ((Point2d)renderingCoordinates.get(a)).y;
		}

		return new Point2d(x / totalmass, y / totalmass);
	}


	/**
	 *  Centers the molecule in the given area, using an external set of coordinates
	 *  Attention: Many methods in this class working on coordinates exist in two versions: One with a HashMap as last parameter, one without
	 *  this. The difference is as follows: The methods without the HashMap change the coordinates in the Atoms of the AtomContainer. The methods with the HashMaps
	 *  expect in this HashMaps pairs of atoms and Point2ds. They work on the Point2ds associated with a particular atom and leave the atom itself
	 *  unchanged. If there is no entry in the HashMap for an atom, they put the coordinates from the Atom in this HashMap and then work on the HashMap.
	 *
	 *
	 *@param  atomCon  molecule to be centered
	 *@param  areaDim  dimension in which the molecule is to be centered
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 */
	public static void center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) {
		Dimension molDim = get2DDimension(atomCon, renderingCoordinates);
		int transX = (areaDim.width - molDim.width) / 2;
		int transY = (areaDim.height - molDim.height) / 2;
		translateAllPositive(atomCon,renderingCoordinates);
		translate2D(atomCon, new Vector2d(transX, transY),renderingCoordinates);
	}


	/**
	 *  Calculates the center of the given atoms and returns it as a Point2d, using
	 *  an external set of coordinates
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atoms  The vector of the given atoms
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 *@return        The center of the given atoms as Point2d
	 *
	 */
	public static Point2d get2DCenter(IAtom[] atoms, HashMap renderingCoordinates) {
		IAtom atom;
		double x = 0;
		double y = 0;
		for (int f = 0; f < atoms.length; f++) {
			atom = atoms[f];
			if (renderingCoordinates.get(atom) != null) {
				x += ((Point2d)renderingCoordinates.get(atom)).x;
				y += ((Point2d)renderingCoordinates.get(atom)).y;
			}
		}
		return new Point2d(x / (double) atoms.length, y / (double) atoms.length);
	}


	/**
	 *  Calculates the center of the given atoms and returns it as a Point2d, using
	 *  an external set of coordinates
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atoms  The Iterator of the given atoms
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 *@return        The center of the given atoms as Point2d
	 *
	 */
	public static Point2d get2DCenter(Iterator atoms, HashMap renderingCoordinates) {
		IAtom atom;
		double x = 0;
		double y = 0;
		int length = 0;
		while (atoms.hasNext()) {
			atom = (IAtom)atoms.next();
			if (renderingCoordinates.get(atom) != null) {
				x += ((Point2d)renderingCoordinates.get(atom)).x;
				y += ((Point2d)renderingCoordinates.get(atom)).y;
			}
			++length;
		}
		return new Point2d(x / (double) length, y / (double) length);
	}
	

	/**
	 *  Returns the geometric center of all the atoms in the atomContainer.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  container  Description of the Parameter
	 **@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 *@return            the geometric center of the atoms in this atomContainer
	 */
	public static Point2d get2DCenter(IAtomContainer container, HashMap renderingCoordinates) {
		double centerX = 0;
		double centerY = 0;
		double counter = 0;
		java.util.Iterator atoms = container.atoms();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
			if (atom.getPoint2d() != null) {
				centerX += ((Point2d)renderingCoordinates.get(atom)).x;
				centerY += ((Point2d)renderingCoordinates.get(atom)).y;
				counter++;
			}
		}
        return new Point2d(centerX / (counter), centerY / (counter));
	}
	
	
	/**
	 *  Writes the coordinates of the atoms participating the given bond into an
	 *  array, using renderingCoordinates, using an external set of coordinates.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  bond  The given bond
	 *@param  renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 *@return       The array with the coordinates
	 */
	public static int[] getBondCoordinates(IBond bond, HashMap renderingCoordinates) {
		if (renderingCoordinates.get(bond.getAtom(0)) == null && bond.getAtom(0).getPoint2d()!=null) {
			renderingCoordinates.put(bond.getAtom(0),new Point2d(bond.getAtom(0).getPoint2d().x,bond.getAtom(0).getPoint2d().y));
		}
		if (renderingCoordinates.get(bond.getAtom(1)) == null && bond.getAtom(1).getPoint2d()!=null) {
			renderingCoordinates.put(bond.getAtom(1),new Point2d(bond.getAtom(1).getPoint2d().x,bond.getAtom(1).getPoint2d().y));
		}
		if (bond.getAtom(0).getPoint2d() == null || bond.getAtom(1).getPoint2d() == null) {
			logger.error("getBondCoordinates() called on Bond without 2D coordinates!");
			return new int[0];
		}
		int beginX = (int) ((Point2d)renderingCoordinates.get(bond.getAtom(0))).x;
		int endX = (int) ((Point2d)renderingCoordinates.get(bond.getAtom(1))).x;
		int beginY = (int) ((Point2d)renderingCoordinates.get(bond.getAtom(0))).y;
		int endY = (int) ((Point2d)renderingCoordinates.get(bond.getAtom(1))).y;
        return new int[]{beginX, beginY, endX, endY};
	}


    /**
     * Returns the atom of the given molecule that is closest to the given
     * coordinates, using an external set of coordinates.
     * See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
     *
     * @param xPosition            The x coordinate
     * @param yPosition            The y coordinate
     * @param renderingCoordinates The set of coordinates to use coming from RendererModel2D
     * @return The atom that is closest to the given coordinates
     */
    public static IAtom getClosestAtom(int xPosition, int yPosition, IChemModel model, IAtom ignore, HashMap renderingCoordinates) {
        IAtom closestAtom = null;
        IAtom currentAtom;
        double smallestMouseDistance = -1;
        double mouseDistance;
        double atomX;
        double atomY;

        List atomContainerList = ChemModelManipulator.getAllAtomContainers(model);
        Iterator iterator = atomContainerList.iterator();
        while(iterator.hasNext())
        {
        	IAtomContainer ac = (IAtomContainer)iterator.next();
            for (int j = 0; j < ac.getAtomCount(); j++) {
                currentAtom = ac.getAtom(j);
                if (renderingCoordinates.get(currentAtom) == null && currentAtom.getPoint2d() != null) {
                    renderingCoordinates.put(currentAtom, new Point2d(currentAtom.getPoint2d().x, currentAtom.getPoint2d().y));
                }
                if (currentAtom != ignore && renderingCoordinates.get(currentAtom) != null) {
                    atomX = ((Point2d) renderingCoordinates.get(currentAtom)).x;
                    atomY = ((Point2d) renderingCoordinates.get(currentAtom)).y;
                    mouseDistance = Math.sqrt(Math.pow(atomX - xPosition, 2) + Math.pow(atomY - yPosition, 2));
                    if (mouseDistance < smallestMouseDistance || smallestMouseDistance == -1) {
                        smallestMouseDistance = mouseDistance;
                        closestAtom = currentAtom;
                    }
                }
            }
        }
        return closestAtom;
    }


	/**
	 *  Returns the bond of the given molecule that is closest to the given
	 *  coordinates.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  xPosition  The x coordinate
	 *@param  yPosition  The y coordinate
	 *@param  atomCon    The molecule that is searched for the closest bond
	 *@return            The bond that is closest to the given coordinates
	 */
	public static IBond getClosestBond(int xPosition, int yPosition, IAtomContainer atomCon, HashMap renderingCoordinates) {
		Point2d bondCenter;
		IBond closestBond = null;
		IBond currentBond;
		double smallestMouseDistance = -1;
		double mouseDistance;
		IBond[] bonds = atomCon.getBonds();
		for (int i = 0; i < bonds.length; i++) {
			currentBond = bonds[i];
			bondCenter = get2DCenter(currentBond.atoms(),renderingCoordinates);
			mouseDistance = Math.sqrt(Math.pow(bondCenter.x - xPosition, 2) + Math.pow(bondCenter.y - yPosition, 2));
			if (mouseDistance < smallestMouseDistance || smallestMouseDistance == -1) {
				smallestMouseDistance = mouseDistance;
				closestBond = currentBond;
			}
		}
		return closestBond;
	}


	/**
	 *  Sorts a Vector of atoms such that the 2D distances of the atom locations
	 *  from a given point are smallest for the first atoms in the vector
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  point  The point from which the distances to the atoms are measured
	 *@param  atoms  The atoms for which the distances to point are measured
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 */
	public static void sortBy2DDistance(IAtom[] atoms, Point2d point, HashMap renderingCoordinates) {
		double distance1;
		double distance2;
		IAtom atom1;
		IAtom atom2;
		boolean doneSomething;
		do {
			doneSomething = false;
			for (int f = 0; f < atoms.length - 1; f++) {
				atom1 = atoms[f];
				atom2 = atoms[f + 1];
				if(renderingCoordinates.get(atom1)==null)
					renderingCoordinates.put(atom1,atom1.getPoint2d());
				if(renderingCoordinates.get(atom2)==null)
					renderingCoordinates.put(atom2,atom2.getPoint2d());
				distance1 = point.distance(((Point2d)renderingCoordinates.get(atom1)));
				distance2 = point.distance(((Point2d)renderingCoordinates.get(atom2)));
				if (distance2 < distance1) {
					atoms[f] = atom2;
					atoms[f + 1] = atom1;
					doneSomething = true;
				}
			}
		} while (doneSomething);
	}

	
	/**
	 *  Determines the scale factor for displaying a structure loaded from disk in
	 *  a frame, using an external set of coordinates. An average of all bond length values is produced and a scale
	 *  factor is determined which would scale the given molecule such that its
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  ac          The AtomContainer for which the ScaleFactor is to be
	 *      calculated
	 *@param  bondLength  The target bond length
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 *@return             The ScaleFactor with which the AtomContainer must be
	 *      scaled to have the target bond length
	 */

	public static double getScaleFactor(IAtomContainer ac, double bondLength, HashMap renderingCoordinates) {
		java.util.Iterator atoms = ac.atoms();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
			if (renderingCoordinates.get(atom) == null && atom.getPoint2d()!=null) {
				renderingCoordinates.put(atom,new Point2d(atom.getPoint2d().x,atom.getPoint2d().y));
			}
		}
		double currentAverageBondLength = getBondLengthAverage(ac,renderingCoordinates);
		if(currentAverageBondLength==0 || Double.isNaN(currentAverageBondLength))
			return 1;
		return bondLength / currentAverageBondLength;
	}


	/**
	 *  An average of all 2D bond length values is produced, using an external set of coordinates. Bonds which have
	 *  Atom's with no coordinates are disregarded.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  ac  The AtomContainer for which the average bond length is to be
	 *      calculated
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 *@return     the average bond length
	 */
	public static double getBondLengthAverage(IAtomContainer ac, HashMap renderingCoordinates) {
		double bondLengthSum = 0;
		IBond[] bonds = ac.getBonds();
		int bondCounter = 0;
		for (int f = 0; f < bonds.length; f++) {
			IBond bond = bonds[f];
			org.openscience.cdk.interfaces.IAtom atom1 = bond.getAtom(0);
			org.openscience.cdk.interfaces.IAtom atom2 = bond.getAtom(1);
			if (renderingCoordinates.get(atom1) != null &&
					renderingCoordinates.get(atom2) != null) {
				bondCounter++;
				bondLengthSum += getLength2D(bond, renderingCoordinates);
			}
		}
		return bondLengthSum / bondCounter;
	}


	/**
	 *  Returns the geometric length of this bond in 2D space, using an external set of coordinates
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  bond  Description of the Parameter
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 *@return       The geometric length of this bond
	 */
	public static double getLength2D(IBond bond, HashMap renderingCoordinates) {
		if (bond.getAtom(0) == null ||
				bond.getAtom(1) == null) {
			return 0.0;
		}
		Point2d p1 = ((Point2d)renderingCoordinates.get(bond.getAtom(0)));
		Point2d p2 = ((Point2d)renderingCoordinates.get(bond.getAtom(1)));
		if (p1 == null || p2 == null) {
			return 0.0;
		}
		return p1.distance(p2);
	}

	
	/**
	 *  Determines the best alignment for the label of an atom in 2D space. It
	 *  returns 1 if left aligned, and -1 if right aligned.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  container  Description of the Parameter
	 *@param  atom       Description of the Parameter
	 *@return            The bestAlignmentForLabel value
	 */
	public static int getBestAlignmentForLabel(IAtomContainer container, IAtom atom, HashMap renderingCoordinates) {
		List connectedAtoms = container.getConnectedAtomsList(atom);
		int overallDiffX = 0;
		for (int i = 0; i < connectedAtoms.size(); i++) {
			IAtom connectedAtom = (IAtom)connectedAtoms.get(i);
			overallDiffX = overallDiffX + (int) (((Point2d)renderingCoordinates.get(connectedAtom)).x - ((Point2d)renderingCoordinates.get(atom)).x);
		}
		if (overallDiffX <= 0) {
			return 1;
		} else {
			return -1;
		}
	}
	
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
	public static boolean has2DCoordinates(IAtomContainer m) {
		return GeometryToolsInternalCoordinates.has2DCoordinatesNew(m)>0;
	}
	
	/**
	 *  Determines if this model contains 3D coordinates
	 *
	 *@param  m  Description of the Parameter
	 *@return    boolean indication that 3D coordinates are available
	 */
	public static boolean has3DCoordinates(IAtomContainer ac) {
		return GeometryToolsInternalCoordinates.has3DCoordinates(ac);
	}
	
	/**
	 *  Determines if this AtomContainer contains 2D coordinates for some or all molecules.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  m  Description of the Parameter
	 *@return    0 no 2d, 1=some, 2= for each atom
	 */
	public static int has2DCoordinatesNew(IAtomContainer ac) {
		return  GeometryToolsInternalCoordinates.has2DCoordinatesNew(ac);
	}
	
	public static void makeRenderingCoordinates(IMoleculeSet molset, HashMap renderingCoordinates){
		Iterator mols=molset.molecules();
		while(mols.hasNext()){
			IAtomContainer ac=(IAtomContainer)mols.next();
			java.util.Iterator atoms = ac.atoms();
			while (atoms.hasNext()) {
				IAtom atom = (IAtom)atoms.next();
				renderingCoordinates.put(atom,new Point2d(atom.getPoint2d().x,atom.getPoint2d().y));
			}
		}
	}
}


