/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005-2007  Christian Hoppe <chhoppe@users.sf.net>
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
package org.openscience.cdk.modeling.builder3d;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 *  A set of static utility classes for geometric calculations on Atoms.
 *
 *@author         Peter Murray-Rust,chhoppe,egonw
 *@cdk.created    2003-??-??
 * @cdk.module    builder3d
 * @cdk.svnrev  $Revision$
 */
public class AtomTetrahedralLigandPlacer3D {

	private Map pSet = null;
	private final double DEFAULT_BOND_LENGTH_H = 1.0;
	//private final double DEFAULT_BOND_LENGTH_HA = 1.3;

	public final double TETRAHEDRAL_ANGLE =
			2.0 * Math.acos(1.0 / Math.sqrt(3.0));

	private final double SP2_ANGLE = 120 * Math.PI / 180;
	private final double SP_ANGLE = Math.PI;

	final static Vector3d XV = new Vector3d(1.0, 0.0, 0.0);
	final static Vector3d YV = new Vector3d(0.0, 1.0, 0.0);


	/**
	 *  Constructor for the AtomTetrahedralLigandPlacer3D object
	 */
	AtomTetrahedralLigandPlacer3D() { }


	/**
	 *  Constructor for the setParameterSet object
	 *
	 *@param  moleculeParameter  Description of the Parameter
	 */
	public void setParameterSet(Map moleculeParameter) {
		pSet = moleculeParameter;
	}


	/**
	 *  Generate coordinates for all atoms which are singly bonded and have no
	 *  coordinates. This is useful when hydrogens are present but have no coords.
	 *  It knows about C, O, N, S only and will give tetrahedral or trigonal
	 *  geometry elsewhere. Bond lengths are computed from covalent radii or taken
	 *  out of a paramter set if available. Angles are tetrahedral or trigonal
	 *
	 *@param  atomContainer  the set of atoms involved
	 *@exception  Exception  Description of the Exception
	 *@cdk.keyword           coordinate calculation
	 *@cdk.keyword           3D model
	 */
	public void add3DCoordinatesForSinglyBondedLigands(IAtomContainer atomContainer) throws Exception {
		IAtomContainer noCoords = new org.openscience.cdk.AtomContainer();
		IAtomContainer withCoords = new org.openscience.cdk.AtomContainer();
		IAtom refAtom = null;
		IAtom atomC = null;
		int nwanted = 0;
		for (int i = 0; i < atomContainer.getAtomCount(); i++) {
			refAtom = atomContainer.getAtom(i);
			if (!refAtom.getSymbol().equals("H") && hasUnsetNeighbour(refAtom, atomContainer)) {
				noCoords = getUnsetAtomsInAtomContainer(refAtom, atomContainer);
				withCoords = getPlacedAtomsInAtomContainer(refAtom, atomContainer);
				if (withCoords.getAtomCount() > 0) {
					atomC = getPlacedHeavyAtomInAtomContainer(withCoords.getAtom(0), refAtom, atomContainer);
				}
				if (refAtom.getFormalNeighbourCount() == 0 && refAtom.getSymbol().equals("C")) {
					nwanted = noCoords.getAtomCount();
				} else if (refAtom.getFormalNeighbourCount() == 0 && !refAtom.getSymbol().equals("C")) {
					nwanted = 4;
				} else {
					nwanted = refAtom.getFormalNeighbourCount() - withCoords.getAtomCount();
				}
				Point3d[] newPoints = get3DCoordinatesForLigands(refAtom,
						noCoords, withCoords, atomC, nwanted, DEFAULT_BOND_LENGTH_H, -1);
				for (int j = 0; j < noCoords.getAtomCount(); j++) {
					IAtom ligand = noCoords.getAtom(j);
					Point3d newPoint = rescaleBondLength(refAtom, ligand, newPoints[j]);
					ligand.setPoint3d(newPoint);
					ligand.setFlag(CDKConstants.ISPLACED, true);
				}

				noCoords.removeAllElements();
				withCoords.removeAllElements();
			}
		}
	}


	/**
	 *  Rescales Point2 so that length 1-2 is sum of covalent radii. 
	 *  If covalent radii cannot be found, use bond length of 1.0
	 *
	 *@param  atom1          stationary atom
	 *@param  atom2          moveable atom
	 *@param  point2         coordinates for atom 2
	 *@return                new coords for atom 2
	 *@exception  Exception  Description of the Exception
	 */
	public Point3d rescaleBondLength(IAtom atom1, IAtom atom2, Point3d point2) throws Exception {
		Point3d point1 = atom1.getPoint3d();
		Double d1 = atom1.getCovalentRadius();
		Double d2 = atom2.getCovalentRadius();
		// in case we have no covalent radii, set to 1.0
		double distance = (d1 == null || d2 == null) ? 1.0 : d1 + d2;
		if (pSet != null) {
			distance = getDistanceValue(atom1.getAtomTypeName(), atom2.getAtomTypeName());
		}
		Vector3d vect = new Vector3d(point2);
		vect.sub(point1);
		vect.normalize();
		vect.scale(distance);
		Point3d newPoint = new Point3d(point1);
		newPoint.add(vect);
		return newPoint;
	}


	/**
	 *  Adds 3D coordinates for singly-bonded ligands of a reference atom (A).
	 *  Initially designed for hydrogens. The ligands of refAtom are identified and
	 *  those with 3D coordinates used to generate the new points. (This allows
	 *  strucures with partially known 3D coordinates to be used, as when groups
	 *  are added.) "Bent" and "non-planar" groups can be formed by taking a subset
	 *  of the calculated points. Thus R-NH2 could use 2 of the 3 points calculated
	 *  from (1,iii) nomenclature: A is point to which new ones are "attached". A
	 *  may have ligands B, C... B may have ligands J, K.. points X1, X2... are
	 *  returned The cases (see individual routines, which use idealised geometry
	 *  by default): (0) zero ligands of refAtom. The resultant points are randomly
	 *  oriented: (i) 1 points required; +x,0,0 (ii) 2 points: use +x,0,0 and
	 *  -x,0,0 (iii) 3 points: equilateral triangle in xy plane (iv) 4 points
	 *  x,x,x, x,-x,-x, -x,x,-x, -x,-x,x (1a) 1 ligand(B) of refAtom which itself
	 *  has a ligand (J) (i) 1 points required; vector along AB vector (ii) 2
	 *  points: 2 vectors in ABJ plane, staggered and eclipsed wrt J (iii) 3
	 *  points: 1 staggered wrt J, the others +- gauche wrt J (1b) 1 ligand(B) of
	 *  refAtom which has no other ligands. A random J is generated and (1a)
	 *  applied (2) 2 ligands(B, C) of refAtom A (i) 1 points required; vector in
	 *  ABC plane bisecting AB, AC. If ABC is linear, no points (ii) 2 points: 2
	 *  vectors at angle ang, whose resultant is 2i (3) 3 ligands(B, C, D) of
	 *  refAtom A (i) 1 points required; if A, B, C, D coplanar, no points. else
	 *  vector is resultant of BA, CA, DA fails if atom itself has no coordinates
	 *  or >4 ligands
	 *
	 *@param  refAtom        (A) to which new ligands coordinates could be added
	 *@param  length         A-X length
	 *@param  angle          B-A-X angle (used in certain cases)
	 *@param  nwanted        Description of the Parameter
	 *@param  noCoords       Description of the Parameter
	 *@param  withCoords     Description of the Parameter
	 *@param  atomC          Description of the Parameter
	 *@return                Point3D[] points calculated. If request could not be
	 *      fulfilled (e.g. too many atoms, or strange geometry, returns empty
	 *      array (zero length, not null)
	 *@exception  Exception  Description of the Exception
	 *@cdk.keyword           coordinate generation
	 */

	public Point3d[] get3DCoordinatesForLigands(IAtom refAtom,
			IAtomContainer noCoords, IAtomContainer withCoords, IAtom atomC, int nwanted, double length, double angle) throws Exception {
		Point3d newPoints[] = new Point3d[1];

		if (noCoords.getAtomCount() == 0 && withCoords.getAtomCount() == 0) {
			return newPoints;
		}

		// too many ligands at present
		if (withCoords.getAtomCount() > 3) {
			return newPoints;
		}

        IBond.Order refMaxBondOrder = refAtom.getMaxBondOrder();
        if (refAtom.getFormalNeighbourCount() == 1) {
//        	WTF???
		} else if (refAtom.getFormalNeighbourCount() == 2 ||
				   refMaxBondOrder == IBond.Order.TRIPLE) {
			//sp
			if (angle == -1){
				angle=SP_ANGLE;
			}
			newPoints[0] = get3DCoordinatesForSPLigands(refAtom, withCoords, length, angle);
		} else if (refAtom.getFormalNeighbourCount() == 3 || 
				   (refMaxBondOrder == IBond.Order.DOUBLE)) {
			//sp2
			if (angle == -1){
				angle=SP2_ANGLE;
			}
			try {
				newPoints = get3DCoordinatesForSP2Ligands(refAtom, noCoords, withCoords, atomC, length, angle);
			} catch (Exception ex1) {
//				logger.debug("Get3DCoordinatesForLigandsERROR: Cannot place SP2 Ligands due to:" + ex1.toString());
				throw new IOException("Cannot place sp2 substituents");
			}

		} else {
			//sp3
			try {
				newPoints = get3DCoordinatesForSP3Ligands(refAtom, noCoords, withCoords, atomC, nwanted, length, angle);
			} catch (Exception ex1) {
//				logger.debug("Get3DCoordinatesForLigandsERROR: Cannot place SP3 Ligands due to:" + ex1.toString());
				throw new IOException("Cannot place sp3 substituents");
			}
		}
		//logger.debug("...Ready "+newPoints.length+" "+newPoints[0].toString());
		return newPoints;
	}

	public Point3d get3DCoordinatesForSPLigands(IAtom refAtom, IAtomContainer withCoords, double length, double angle) {
		//logger.debug(" SP Ligands start "+refAtom.getPoint3d()+" "+(withCoords.getAtomAt(0)).getPoint3d());
		Vector3d ca = new Vector3d(refAtom.getPoint3d());
		ca.sub((withCoords.getAtom(0)).getPoint3d());
		ca.normalize();
		ca.scale(length);
		Point3d newPoint = new Point3d(refAtom.getPoint3d());
		newPoint.add(ca);
		return newPoint;
	}


	/**
	 *  Main method for the calculation of the ligand coordinates for sp2 atoms.
	 *  Decides if one or two coordinates should be created
	 *
	 *@param  refAtom            central atom (Atom)
	 *@param  noCoords           Description of the Parameter
	 *@param  withCoords         Description of the Parameter
	 *@param  atomC              Description of the Parameter
	 *@param  length             Description of the Parameter
	 *@param  angle              Description of the Parameter
	 *@return                    coordinates as Points3d []
	 */
	public Point3d[] get3DCoordinatesForSP2Ligands(IAtom refAtom, IAtomContainer noCoords, IAtomContainer withCoords, IAtom atomC, double length, double angle) {
		//logger.debug(" SP2 Ligands start");
		Point3d newPoints[] = new Point3d[1];
		if (angle < 0) {
			angle = SP2_ANGLE;
		}
		if (withCoords.getAtomCount() >= 2) {
			//logger.debug("Wanted:1 "+noCoords.getAtomCount());
			newPoints[0] = calculate3DCoordinatesSP2_1(refAtom.getPoint3d(), (withCoords.getAtom(0)).getPoint3d(),
					(withCoords.getAtom(1)).getPoint3d(), length, -1 * angle);

		} else if (withCoords.getAtomCount() <= 1) {
			//logger.debug("NoCoords 2:"+noCoords.getAtomCount());
			newPoints = calculate3DCoordinatesSP2_2(refAtom.getPoint3d(), (withCoords.getAtom(0)).getPoint3d(),
					(atomC != null) ? atomC.getPoint3d() : null, length, angle);
		}
		//logger.debug("Ready SP2");
		return newPoints;
	}


	/**
	 *  Main method for the calculation of the ligand coordinates for sp3 atoms.
	 *  Decides how many coordinates should be created
	 *
	 *@param  refAtom            central atom (Atom)
	 *@param  nwanted            how many ligands should be created
	 *@param  length             bond length
	 *@param  angle              angle in a B-A-(X) system; a=central atom;
	 *      x=ligand with unknown coordinates
	 *@param  noCoords           Description of the Parameter
	 *@param  withCoords         Description of the Parameter
	 *@param  atomC              Description of the Parameter
	 *@return                    Description of the Return Value
	 */
	public Point3d[] get3DCoordinatesForSP3Ligands(IAtom refAtom, IAtomContainer noCoords, IAtomContainer withCoords, IAtom atomC, int nwanted, double length, double angle) {
		//logger.debug("SP3 Ligands start ");
		Point3d newPoints[] = new Point3d[0];
		Point3d aPoint = refAtom.getPoint3d();
		int nwithCoords = withCoords.getAtomCount();
		if (angle < 0) {
			angle = TETRAHEDRAL_ANGLE;
		}
		if (nwithCoords == 0) {
			newPoints = calculate3DCoordinates0(refAtom.getPoint3d(), nwanted, length);
		} else if (nwithCoords == 1) {
			newPoints = calculate3DCoordinates1(aPoint, (withCoords.getAtom(0)).getPoint3d(), (atomC != null) ? atomC.getPoint3d() : null, nwanted, length, angle);
		} else if (nwithCoords == 2) {
			Point3d bPoint = withCoords.getAtom(0).getPoint3d();
			Point3d cPoint = withCoords.getAtom(1).getPoint3d();
			newPoints = calculate3DCoordinates2(aPoint, bPoint, cPoint, nwanted, length, angle);
		} else if (nwithCoords == 3) {
			Point3d bPoint = withCoords.getAtom(0).getPoint3d();
			Point3d cPoint = withCoords.getAtom(1).getPoint3d();
			newPoints = new Point3d[1];
			Point3d dPoint = withCoords.getAtom(2).getPoint3d();
			newPoints[0] = calculate3DCoordinates3(aPoint, bPoint, cPoint, dPoint, length);
		}
		//logger.debug("...Ready");
		return newPoints;
	}


	/**
	 *  Calculates substituent points. Calculate substituent points for (0) zero
	 *  ligands of aPoint. The resultant points are randomly oriented: (i) 1 points
	 *  required; +x,0,0 (ii) 2 points: use +x,0,0 and -x,0,0 (iii) 3 points:
	 *  equilateral triangle in xy plane (iv) 4 points x,x,x, x,-x,-x, -x,x,-x,
	 *  -x,-x,x where 3x**2 = bond length
	 *
	 *@param  aPoint   to which substituents are added
	 *@param  nwanted  number of points to calculate (1-4)
	 *@param  length   from aPoint
	 *@return          Point3d[] nwanted points (or zero if failed)
	 */
	public Point3d[] calculate3DCoordinates0(Point3d aPoint, int nwanted, double length) {
		Point3d points[] = new Point3d[0];
		if (nwanted == 1) {
			points = new Point3d[1];
			points[0] = new Point3d(aPoint);
			points[0].add(new Vector3d(length, 0.0, 0.0));
		} else if (nwanted == 2) {
			points[0] = new Point3d(aPoint);
			points[0].add(new Vector3d(length, 0.0, 0.0));
			points[1] = new Point3d(aPoint);
			points[1].add(new Vector3d(-length, 0.0, 0.0));
		} else if (nwanted == 3) {
			points[0] = new Point3d(aPoint);
			points[0].add(new Vector3d(length, 0.0, 0.0));
			points[1] = new Point3d(aPoint);
			points[1].add(new Vector3d(-length * 0.5, -length * 0.5 * Math.sqrt(3.0), 0.0f));
			points[2] = new Point3d(aPoint);
			points[2].add(new Vector3d(-length * 0.5, length * 0.5 * Math.sqrt(3.0), 0.0f));
		} else if (nwanted == 4) {
			double dx = length / Math.sqrt(3.0);
			points[0] = new Point3d(aPoint);
			points[0].add(new Vector3d(dx, dx, dx));
			points[1] = new Point3d(aPoint);
			points[1].add(new Vector3d(dx, -dx, -dx));
			points[2] = new Point3d(aPoint);
			points[2].add(new Vector3d(-dx, -dx, dx));
			points[3] = new Point3d(aPoint);
			points[3].add(new Vector3d(-dx, dx, -dx));
		}
		return points;
	}


	/**
	 *  Calculate new point(s) X in a B-A system to form B-A-X. Use C as reference
	 *  for * staggering about the B-A bond (1a) 1 ligand(B) of refAtom (A) which
	 *  itself has a ligand (C) (i) 1 points required; vector along AB vector (ii)
	 *  2 points: 2 vectors in ABC plane, staggered and eclipsed wrt C (iii) 3
	 *  points: 1 staggered wrt C, the others +- gauche wrt C If C is null, a
	 *  random non-colinear C is generated
	 *
	 *@param  aPoint   to which substituents are added
	 *@param  nwanted  number of points to calculate (1-3)
	 *@param  length   A-X length
	 *@param  angle    B-A-X angle
	 *@param  bPoint   Description of the Parameter
	 *@param  cPoint   Description of the Parameter
	 *@return          Point3d[] nwanted points (or zero if failed)
	 */
	public Point3d[] calculate3DCoordinates1(
			Point3d aPoint, Point3d bPoint, Point3d cPoint,
			int nwanted, double length, double angle
			) {
		Point3d points[] = new Point3d[nwanted];
		// BA vector
		Vector3d ba = new Vector3d(aPoint);
		ba.sub(bPoint);
		ba.normalize();
		// if no cPoint, generate a random reference
		if (cPoint == null) {
			Vector3d cVector = getNonColinearVector(ba);
			cPoint = new Point3d(cVector);
		}
		// CB vector
		Vector3d cb = new Vector3d(bPoint);
		cb.sub(cPoint);
		cb.normalize();
		// if A, B, C colinear, replace C by random point
		double cbdotba = cb.dot(ba);
		if (cbdotba > 0.999999) {
			Vector3d cVector = getNonColinearVector(ba);
			cPoint = new Point3d(cVector);
			cb = new Vector3d(bPoint);
			cb.sub(cPoint);
		}
		// cbxba = c x b
		Vector3d cbxba = new Vector3d();
		cbxba.cross(cb, ba);
		cbxba.normalize();
		// create three perp axes ba, cbxba, and ax
		Vector3d ax = new Vector3d();
		ax.cross(cbxba, ba);
		ax.normalize();
		double drot = Math.PI * 2.0 / (double) nwanted;
		for (int i = 0; i < nwanted; i++) {
			double rot = (double) i * drot;
			points[i] = new Point3d(aPoint);
			Vector3d vx = new Vector3d(ba);
			vx.scale(-Math.cos(angle) * length);
			Vector3d vy = new Vector3d(ax);
			vy.scale(Math.cos(rot) * length);
			Vector3d vz = new Vector3d(cbxba);
			vz.scale(Math.sin(rot) * length);
			points[i].add(vx);
			points[i].add(vy);
			points[i].add(vz);
		}
		/*ax = null;
		cbxba = null;
		ba = null;
		cb = null;*/
		return points;
	}


	/**
	 *  Calculate new point(s) X in a B-A-C system, it forms a B-A(-C)-X
	 *  system. (2) 2 ligands(B, C) of refAtom A (i) 1 points required; vector in
	 *  ABC plane bisecting AB, AC. If ABC is linear, no points (ii) 2 points: 2
	 *  points X1, X2, X1-A-X2 = angle about 2i vector
	 *
	 *@param  aPoint   to which substituents are added
	 *@param  bPoint   first ligand of A
	 *@param  cPoint   second ligand of A
	 *@param  nwanted  number of points to calculate (1-2)
	 *@param  length   A-X length
	 *@param  angle    B-A-X angle
	 *@return          Point3d[] nwanted points (or zero if failed)
	 */
	public Point3d[] calculate3DCoordinates2(
			Point3d aPoint, Point3d bPoint, Point3d cPoint,
			int nwanted, double length, double angle) {
		//logger.debug("3DCoordinates2");
		Point3d newPoints[] = new Point3d[0];
		double ang2 = angle / 2.0;

		Vector3d ba = new Vector3d(aPoint);
		ba.sub(bPoint);
		Vector3d ca = new Vector3d(aPoint);
		ca.sub(cPoint);
		Vector3d baxca = new Vector3d();
		baxca.cross(ba, ca);
		if (baxca.length() < 0.00000001) {
			;
			// linear
		} else if (nwanted == 1) {
			newPoints = new Point3d[1];
			Vector3d ax = new Vector3d(ba);
			ax.add(ca);
			ax.normalize();
			ax.scale(length);
			newPoints[0] = new Point3d(aPoint);
			newPoints[0].add(ax);
		} else if (nwanted >= 2) {
			newPoints = new Point3d[2];
			Vector3d ax = new Vector3d(ba);
			ax.add(ca);
			ax.normalize();
			baxca.normalize();
			baxca.scale(Math.sin(ang2) * length);
			ax.scale(Math.cos(ang2) * length);
			newPoints[0] = new Point3d(aPoint);
			newPoints[0].add(ax);
			newPoints[0].add(baxca);
			newPoints[1] = new Point3d(aPoint);
			newPoints[1].add(ax);
			newPoints[1].sub(baxca);
		}
		baxca = null;
		ba = null;
		ca = null;
		return newPoints;
	}


	/**
	 *  Calculate new point X in a B-A(-D)-C system. It forms a B-A(-D)(-C)-X
	 *  system. (3) 3 ligands(B, C, D) of refAtom A (i) 1 points required; if A, B,
	 *  C, D coplanar, no points. else vector is resultant of BA, CA, DA
	 *
	 *@param  aPoint  to which substituents are added
	 *@param  bPoint  first ligand of A
	 *@param  cPoint  second ligand of A
	 *@param  dPoint  third ligand of A
	 *@param  length  A-X length
	 *@return         Point3d nwanted points (or null if failed (coplanar))
	 */
	public Point3d calculate3DCoordinates3(
			Point3d aPoint, Point3d bPoint, Point3d cPoint, Point3d dPoint,
			double length) {
		//logger.debug("3DCoordinates3");
		Vector3d bc = new Vector3d(bPoint);
		bc.sub(cPoint);
		Vector3d dc = new Vector3d(dPoint);
		dc.sub(cPoint);
		Vector3d ca = new Vector3d(cPoint);
		ca.sub(aPoint);

		Vector3d n1 = new Vector3d();
		Vector3d n2 = new Vector3d();

		n1.cross(bc, dc);
		n1.normalize();
		n1.scale(length);

		Vector3d ax = new Vector3d(aPoint);
		ax.add(n1);
		ax.sub(aPoint);

		Vector3d ax2 = new Vector3d(aPoint);
		ax2.add(n2);
		ax2.sub(aPoint);

		Point3d point = new Point3d(aPoint);

		double dotProduct = ca.dot(ax);
		double angle = Math.acos((dotProduct) / (ax.length() * ca.length()));

		if (angle < 1.5) {
			n2.cross(dc, bc);
			n2.normalize();
			n2.scale(length);
			point.add(n2);
		} else {
			point.add(n1);
		}

		bc = null;
		dc = null;
		ca = null;
		n1 = null;
		n2 = null;
		return point;
	}


	/**
	 *  Calculate new point in B-A-C system. It forms B-A(-X)-C system, where A is
	 *  sp2
	 *
	 *@param  aPoint  central point A (Point3d)
	 *@param  bPoint  B (Point3d)
	 *@param  cPoint  C (Point3d)
	 *@param  length  bond length
	 *@param  angle   angle between B(C)-A-X
	 *@return         new Point (Point3d)
	 */
	public Point3d calculate3DCoordinatesSP2_1(Point3d aPoint, Point3d bPoint, Point3d cPoint,
			double length, double angle) {
		//logger.debug("3DCoordinatesSP2_1");
		Vector3d ba = new Vector3d(bPoint);
		ba.sub(aPoint);
		Vector3d ca = new Vector3d(cPoint);
		ca.sub(aPoint);

		Vector3d n1 = new Vector3d();
		n1.cross(ba, ca);
		n1.normalize();

		Vector3d n2 = rotate(ba, n1, angle);
		n2.normalize();

		n2.scale(length);
		Point3d point = new Point3d(aPoint);
		point.add(n2);
		n1 = null;
		n2 = null;
		ba = null;
		ca = null;
		return point;
	}


	/**
	 *  Calculate two new points in B-A system. It forms B-A(-X)(-X) system, where
	 *  A is sp2
	 *
	 *@param  aPoint  central point A (Point3d)
	 *@param  bPoint  B (Point3d)
	 *@param  cPoint  C (Point3d)
	 *@param  length  bond length
	 *@param  angle   angle between B(C)-A-X
	 *@return         new Points (Point3d [])
	 */
	public Point3d[] calculate3DCoordinatesSP2_2(Point3d aPoint, Point3d bPoint, Point3d cPoint, double length, double angle) {
		//logger.debug("3DCoordinatesSP_2");
		Vector3d ca = new Vector3d();
		Point3d newPoints[] = new Point3d[2];
		Vector3d ba = new Vector3d(bPoint);
		ba.sub(aPoint);

		if (cPoint != null) {
			ca.x = cPoint.x - aPoint.x;
			ca.y = cPoint.y - aPoint.y;
			ca.z = cPoint.z - aPoint.z;
		} else {
			ca.x = -1 * ba.x;
			ca.y = -1 * ba.y;
			ca.z = -1.5 * ba.z;
		}

		Vector3d crossProduct = new Vector3d();
		crossProduct.cross(ba, ca);

		Vector3d n1 = rotate(ba, crossProduct, 2 * angle);
		n1.normalize();
		n1.scale(length);
		newPoints[0] = new Point3d(aPoint);
		newPoints[0].add(n1);

		Vector3d n2 = rotate(n1, ba, Math.PI);
		n2.normalize();
		n2.scale(length);
		newPoints[1] = new Point3d(aPoint);
		newPoints[1].add(n2);
		n1 = null;
		n2 = null;
		ba = null;
		ca = null;
		return newPoints;
	}


	/**
	 *  Gets the nonColinearVector attribute of the AtomLigandPlacer3D class
	 *
	 *@param  ab  Description of the Parameter
	 *@return     The nonColinearVector value
	 */
	private Vector3d getNonColinearVector(Vector3d ab) {
		Vector3d cr = new Vector3d();
		cr.cross(ab, XV);
		if (cr.length() > 0.00001) {
			return XV;
		} else {
			return YV;
		}
	}


	/**
	 *  Rotates a vector around an axis
	 *
	 *@param  vector  vector to be rotated around axis
	 *@param  axis    axis of rotation
	 *@param  angle   angle to vector rotate around
	 *@return         rotated vector
	 *author:         egonw
	 */
	public static Vector3d rotate(Vector3d vector, Vector3d axis, double angle) {
		Matrix3d rotate = new Matrix3d();
		rotate.set(new AxisAngle4d(axis.x, axis.y, axis.z, angle));
		Vector3d result = new Vector3d();
		rotate.transform(vector, result);
		return result;
	}


	/**
	 * Gets the distance between two atoms out of the parameter set.
	 *
	 *@param  id1            id of the paramter set for atom1 (atom1.getAtomTypeName())
	 *@param  id2            id of the paramter set for atom2
	 *@return                The distanceValue value
	 *@exception  Exception  Description of the Exception
	 */
	private double getDistanceValue(String id1, String id2) throws Exception {
		String dkey = "";
		if (pSet.containsKey(("bond" + id1 + ";" + id2))) {
			dkey = "bond" + id1 + ";" + id2;
		} else if (pSet.containsKey(("bond" + id2 + ";" + id1))) {
			dkey = "bond" + id2 + ";" + id1;
		} else {
//			logger.debug("DistanceKEYError:pSet has no key:" + id2 + " ; " + id1 + " take default bond length:" + DEFAULT_BOND_LENGTH_H);
			return DEFAULT_BOND_LENGTH_H;
		}
		return ((Double) (((List) pSet.get(dkey)).get(0))).doubleValue();
	}


	/**
	 *  Gets the angleKey attribute of the AtomPlacer3D object
	 *
	 *@param  id1            Description of the Parameter
	 *@param  id2            Description of the Parameter
	 *@param  id3            Description of the Parameter
	 *@return                The angleKey value
	 *@exception  Exception  Description of the Exception
	 */
	public double getAngleValue(String id1, String id2, String id3) throws Exception {
		String akey = "";
		if (pSet.containsKey(("angle" + id1 + ";" + id2 + ";" + id3))) {
			akey = "angle" + id1 + ";" + id2 + ";" + id3;
		} else if (pSet.containsKey(("angle" + id3 + ";" + id2 + ";" + id1))) {
			akey = "angle" + id3 + ";" + id2 + ";" + id1;
		} else if (pSet.containsKey(("angle" + id2 + ";" + id1 + ";" + id3))) {
			akey = "angle" + id2 + ";" + id1 + ";" + id3;
		} else if (pSet.containsKey(("angle" + id1 + ";" + id3 + ";" + id2))) {
			akey = "angle" + id1 + ";" + id3 + ";" + id2;
		} else if (pSet.containsKey(("angle" + id3 + ";" + id1 + ";" + id2))) {
			akey = "angle" + id3 + ";" + id1 + ";" + id2;
		} else if (pSet.containsKey(("angle" + id2 + ";" + id3 + ";" + id1))) {
			akey = "angle" + id2 + ";" + id3 + ";" + id1;
		} else {
			System.out.println("AngleKEYError:Unknown angle " + id1 + " " + id2 + " " + id3 + " take default angle:" + TETRAHEDRAL_ANGLE);
			return TETRAHEDRAL_ANGLE;
		}
		return ((Double) (((List) pSet.get(akey)).get(0))).doubleValue();
	}


	/**
	 *  set Atoms in respect to stereoinformation.
	 *	take placed neighbours to stereocenter
	 *		create a x b
	 *	     if right handed system (spatproduct >0)
	 *			if unplaced is not up (relativ to stereocenter)
	 *				n=b x a
	 *	     Determine angle between n and possible ligand place points
	 *	     if angle smaller than 90 degrees take this branch point
	 *
	 *@param  atomA         placed Atom - stereocenter
	 *@param  ax            bond between stereocenter and unplaced atom
	 *@param  atomB         neighbour of atomA (in plane created by atomA, atomB and atomC)
	 *@param  atomC         neighbour of atomA
	 *@param  branchPoints  the two possible placement points for unplaced atom (up and down) 
	 *@return               int value of branch point position
	 */
	public int makeStereocenter(Point3d atomA, IBond ax, Point3d atomB, Point3d atomC, Point3d[] branchPoints) {
		
		Vector3d b = new Vector3d((atomB.x - atomA.x), (atomB.y - atomA.y), (atomB.z - atomA.z));
		Vector3d c = new Vector3d((atomC.x - atomA.x), (atomC.y - atomA.y), (atomC.z - atomA.z));
		Vector3d n1 = new Vector3d();
		Vector3d n2 = null;
		n1.cross(b, c);
		n1.normalize();

		if (getSpatproduct(b, c, n1) >= 0) {
			if (ax.getStereo() != CDKConstants.STEREO_BOND_UP_INV) {
				n1.cross(c, b);
				n1.normalize();
			}
		}
		double dotProduct = 0;
		for (int i = 0; i < branchPoints.length; i++) {
			n2 = new Vector3d(branchPoints[0].x, branchPoints[0].y, branchPoints[0].z);
			dotProduct = n1.dot(n2);
			if (Math.acos(dotProduct / (n1.length() * n2.length())) < 1.6) {
				return i;
			}
		}
		return -1;
	}


	/**
	 *  Gets the spatproduct of three vectors
	 *
	 *@param  a  vector a
	 *@param  b  vector b
	 *@param  c  vector c
	 *@return    double value of the spatproduct
	 */
	public double getSpatproduct(Vector3d a, Vector3d b, Vector3d c) {
		return (c.x * (b.y * a.z - b.z * a.y) + c.y * (b.z * a.x - b.x * a.z) + c.z * (b.x * a.y - b.y * a.x));
	}


	/**
	 *  Calculates the torsionAngle of a-b-c-d 
	 *
	 *@param  a  Point3d
	 *@param  b  Point3d
	 *@param  c  Point3d
	 *@param  d  Point3d
	 *@return    The torsionAngle value
	 */
	public double getTorsionAngle(Point3d a, Point3d b, Point3d c, Point3d d) {
		Vector3d ab = new Vector3d(a.x - b.x, a.y - b.y, a.z - b.z);
		Vector3d cb = new Vector3d(c.x - b.x, c.y - b.y, c.z - b.z);
		Vector3d dc = new Vector3d(d.x - c.x, d.y - c.y, d.z - c.z);
		Vector3d bc = new Vector3d(b.x - c.x, b.y - c.y, b.z - c.z);
		Vector3d n1 = new Vector3d();
		Vector3d n2 = new Vector3d();

		n1.cross(ab, cb);
		if (getSpatproduct(ab, cb, n1) > 0) {
			n1.cross(cb, ab);
		}
		n1.normalize();
		n2.cross(dc, bc);
		if (getSpatproduct(dc, bc, n2) < 0) {
			n2.cross(bc, dc);
		}
		n2.normalize();
		return n1.dot(n2);
	}


	/**
	 *  Gets all placed neighbouring atoms of a atom
	 *
	 *@param  atom  central atom (Atom)
	 *@param  ac    the molecul
	 *@return       all connected and placed atoms to the central atom
	 *      ((AtomContainer)
	 */
	public IAtomContainer getPlacedAtomsInAtomContainer(IAtom atom, IAtomContainer ac) {

		java.util.List bonds = ac.getConnectedBondsList(atom);
		IAtomContainer connectedAtoms = new org.openscience.cdk.AtomContainer();
		IAtom connectedAtom = null;
		for (int i = 0; i < bonds.size(); i++) {
			connectedAtom = ((IBond)bonds.get(i)).getConnectedAtom(atom);
			if (connectedAtom.getFlag(CDKConstants.ISPLACED)) {
				connectedAtoms.addAtom(connectedAtom);
			}
		}
		return connectedAtoms;
	}


	/**
	 *  Gets the unsetAtomsInAtomContainer attribute of the
	 *  AtomTetrahedralLigandPlacer3D object
	 *
	 *@param  atom  Description of the Parameter
	 *@param  ac    Description of the Parameter
	 *@return       The unsetAtomsInAtomContainer value
	 */
	public IAtomContainer getUnsetAtomsInAtomContainer(IAtom atom, IAtomContainer ac) {
		java.util.List atoms = ac.getConnectedAtomsList(atom);
		IAtomContainer connectedAtoms = new org.openscience.cdk.AtomContainer();
		for (int i = 0; i < atoms.size(); i++) {
			IAtom curAtom = (IAtom)atoms.get(i);
			if (!curAtom.getFlag(CDKConstants.ISPLACED)){//&& atoms[i].getPoint3d() == null) {
				connectedAtoms.addAtom(curAtom);
			}
		}
		return connectedAtoms;
	}

	public boolean hasUnsetNeighbour(IAtom atom, IAtomContainer ac) {
		java.util.List atoms = ac.getConnectedAtomsList(atom);
		for (int i = 0; i < atoms.size(); i++) {
			IAtom curAtom = (IAtom)atoms.get(i);
			if (!curAtom.getFlag(CDKConstants.ISPLACED)) {//&& atoms[i].getPoint3d() == null) {
				return true;
			}
		}
		return false;
	}


	/**
	 *  Returns a placed neighbouring atom of a central atom atomA, which is not
	 *  atomB
	 *
	 *@param  atomA  central atom (Atom)
	 *@param  atomB  atom connected to atomA (Atom)
	 *@param  ac     molecule
	 *@return        returns a connected atom (Atom)
	 */
	public IAtom getPlacedHeavyAtomInAtomContainer(IAtom atomA, IAtom atomB, IAtomContainer ac) {
		java.util.List atoms = ac.getConnectedAtomsList(atomA);
		IAtom atom=null;
		for (int i = 0; i < atoms.size(); i++) {
			IAtom curAtom = (IAtom)atoms.get(i);
			if (curAtom.getFlag(CDKConstants.ISPLACED) && !curAtom.getSymbol().equals("H")
					 && curAtom != atomB) {
				return curAtom;
			}
		}
		return atom;
	}
}

