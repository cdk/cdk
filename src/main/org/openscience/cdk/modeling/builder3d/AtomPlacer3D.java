/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2005-2007  Christian Hoppe <chhoppe@users.sf.net>
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.modeling.builder3d;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Place aliphatic chains with Z matrix method.
 *
 * @author         chhoppe
 * @cdk.keyword    AtomPlacer3D
 * @cdk.created    2004-10-8
 * @cdk.module     builder3d
 * @cdk.svnrev  $Revision$
 */
public class AtomPlacer3D {

	private Map<Object,List> pSet = null;
	private double[] distances;
	private int[] first_atoms = null;
	private double[] angles = null;
	private int[] second_atoms = null;
	private double[] dihedrals = null;
	private int[] third_atoms = null;
	private final static double DIHEDRAL_EXTENDED_CHAIN = (180.0 / 180) * Math.PI;
	private final static double DIHEDRAL_BRANCHED_CHAIN = 0.0;
	private final static double DEFAULT_BOND_LENGTH = 1.5;
	private final static double DEFAULT_SP3_ANGLE = 109.471;
	private final static double DEFAULT_SP2_ANGLE = 120.000;
	private final static double DEFAULT_SP_ANGLE = 180.000;
	
	AtomPlacer3D(){}
	
	/**
	 *  Initialize the atomPlacer class
	 * 
	 * @param  parameterSet  Force Field parameter as Hashtable
	 */
	public void initilize(Map parameterSet) {
		pSet = parameterSet;
	}

	/**
	 *  Count and find first heavy atom(s) (non Hydrogens) in a chain
	 *
	 * @param  chain  chain to be searched
	 * @return        the atom number of the first heavy atom the number of heavy atoms in the chain
	 */
	public int[] findHeavyAtomsInChain(IAtomContainer molecule, IAtomContainer chain) {
		int[] heavy = {-1, -1};
		int hc = 0;
		for (int i = 0; i < chain.getAtomCount(); i++) {
			if (!(chain.getAtom(i).getSymbol()).equals("H")) {
				if (heavy[0] < 0) {
					heavy[0] = molecule.getAtomNumber(chain.getAtom(i));
				}
				hc++;
			}
		}
		heavy[1] = hc;
		return heavy;
	}


	/**
	 *  Mark all atoms in chain as placed (CDKConstant ISPLACED)
	 *
	 * @param  ac  chain
	 * @return     chain all atoms marked as placed
	 */
	public IAtomContainer markPlaced(IAtomContainer ac) {
		for (int i = 0; i < ac.getAtomCount(); i++) {
			ac.getAtom(i).setFlag(CDKConstants.ISPLACED, true);
		}
		return ac;
	}

	
	/**
	 *  Method assigns 3Dcoordinates to the heavy atoms in an aliphatic chain
	 *
	 * @param  chain          the atoms to be assigned, must be connected
	 * @exception  Exception  Description of the Exception
	 */
	public void placeAliphaticHeavyChain(IAtomContainer molecule, IAtomContainer chain) throws Exception {
		//logger.debug("******** Place aliphatic Chain *********");
		int[] first = new int[2];
		int counter = 1;
		int nextAtomNr = 0;
		String ID1 = "";
		String ID2 = "";
		String ID3 = "";
		first = findHeavyAtomsInChain(molecule,chain);
		distances = new double[first[1]];
		first_atoms = new int[first[1]];
		angles = new double[first[1]];
		second_atoms = new int[first[1]];
		dihedrals = new double[first[1]];
		third_atoms = new int[first[1]];
		first_atoms[0] = first[0];
		molecule.getAtom(first_atoms[0]).setFlag(CDKConstants.VISITED, true);
		int hybridisation = 0;
		for (int i = 0; i < chain.getAtomCount(); i++) {
			if (!(chain.getAtom(i).getSymbol()).equals("H") &
					!chain.getAtom(i).getFlag(CDKConstants.VISITED)) {
				//logger.debug("Counter:" + counter);
				nextAtomNr = molecule.getAtomNumber(chain.getAtom(i));
				ID2 = molecule.getAtom(first_atoms[counter - 1]).getAtomTypeName();
				ID1 = molecule.getAtom(nextAtomNr).getAtomTypeName();
				distances[counter] = getBondLengthValue(ID1, ID2);
				//logger.debug(" Distance:" + distances[counter]);
				first_atoms[counter] = nextAtomNr;
				second_atoms[counter] = first_atoms[counter - 1];
				if (counter > 1) {
					ID3 = molecule.getAtom(first_atoms[counter - 2]).getAtomTypeName();
					hybridisation = getHybridisationState(molecule.getAtom(first_atoms[counter - 1]));
					angles[counter] = getAngleValue(ID1, ID2, ID3);
					//Check if sp,sp2
					if (angles[counter] == -1) {
						if (hybridisation == 3) {
							angles[counter] = DEFAULT_SP3_ANGLE;
						} else if (hybridisation == 2) {
							angles[counter] = DEFAULT_SP2_ANGLE;
						} else if (hybridisation == 1) {
							angles[counter] = DEFAULT_SP_ANGLE;
						}
					}
					third_atoms[counter] = first_atoms[counter - 2];
					//logger.debug(" Angle:" + angles[counter]);
				} else {
					angles[counter] = -1;
					third_atoms[counter] = -1;
				}
				if (counter > 2) {
					//double bond
					try{
						if (getDoubleBondConfiguration2D( molecule.getBond(molecule.getAtom(first_atoms[counter-1]),molecule.getAtom(first_atoms[counter-2])),
										(molecule.getAtom(first_atoms[counter])).getPoint2d(),(molecule.getAtom(first_atoms[counter-1])).getPoint2d(),
										(molecule.getAtom(first_atoms[counter-2])).getPoint2d(),(molecule.getAtom(first_atoms[counter-3])).getPoint2d())
							==5){
							dihedrals[counter] = DIHEDRAL_BRANCHED_CHAIN;
						}else{ dihedrals[counter] = DIHEDRAL_EXTENDED_CHAIN;}
					}catch(Exception ex1){
						dihedrals[counter] = DIHEDRAL_EXTENDED_CHAIN;
					}
				} else {
					dihedrals[counter] = -1;
				}
				counter++;
			}
		}
	}

	/**
	 * Takes the given Z Matrix coordinates and converts them to cartesian coordinates.
	 * The first Atom end up in the origin, the second on on the x axis, and the third
	 * one in the XY plane. The rest is added by applying the Zmatrix distances, angles
	 * and dihedrals. Assign coordinates directly to the atoms.
	 *
	 * @param  flag_branched  marks branched chain
	 * author: egonw,cho
	 */

	public void zmatrixChainToCartesian(IAtomContainer molecule, boolean flag_branched) {
		Point3d result = null;
		for (int index = 0; index < distances.length; index++) {
			if (index == 0) {
				result = new Point3d(0d, 0d, 0d);
			} else if (index == 1) {
				result = new Point3d(distances[1], 0d, 0d);
			} else if (index == 2) {
				result = new Point3d(-Math.cos((angles[2] / 180) * Math.PI) * distances[2] + distances[1],
						Math.sin((angles[2] / 180) * Math.PI) * distances[2],
						0d);
			} else {
				Vector3d cd = new Vector3d();
				cd.sub((molecule.getAtom(third_atoms[index])).getPoint3d(), (molecule.getAtom(second_atoms[index])).getPoint3d());

				Vector3d bc = new Vector3d();
				bc.sub(molecule.getAtom(second_atoms[index]).getPoint3d(), molecule.getAtom(first_atoms[index - 3]).getPoint3d());

				Vector3d n1 = new Vector3d();
				n1.cross(cd, bc);
				n1.normalize();

				Vector3d n2 = null;
				if (index == 3 & flag_branched) {
					n2 = AtomTetrahedralLigandPlacer3D.rotate(n1, bc, DIHEDRAL_BRANCHED_CHAIN);
				} else {
					n2 = AtomTetrahedralLigandPlacer3D.rotate(n1, bc, dihedrals[index]);
				}
				n2.normalize();

				Vector3d ba = new Vector3d();
				if (index == 3 & flag_branched) {
					ba = AtomTetrahedralLigandPlacer3D.rotate(cd, n2, (-angles[index] / 180) * Math.PI);
					ba = AtomTetrahedralLigandPlacer3D.rotate(ba, cd, (-angles[index] / 180) * Math.PI);
				} else {
					ba = AtomTetrahedralLigandPlacer3D.rotate(cd, n2, (-angles[index] / 180) * Math.PI);
				}

				ba.normalize();

				Vector3d ban = new Vector3d(ba);
				ban.scale(distances[index]);

				result = new Point3d();
				result.add(molecule.getAtom(first_atoms[index - 1]).getPoint3d(), ban);
			}

			if ((molecule.getAtom(first_atoms[index]).getPoint3d() == null || !(molecule.getAtom(first_atoms[index])).getFlag(CDKConstants.ISPLACED))
					 && !(molecule.getAtom(first_atoms[index])).getFlag(CDKConstants.ISINRING)
					 && !(molecule.getAtom(first_atoms[index])).getSymbol().equals("H")) {
				molecule.getAtom(first_atoms[index]).setPoint3d(result);
				molecule.getAtom(first_atoms[index]).setFlag(CDKConstants.ISPLACED, true);
			}
		}
	}


	/**
	 *  Gets the hybridisationState of an atom
	 *
	 *@param  atom1  atom
	 *@return        The hybridisationState value (sp=1;sp2=2;sp3=3)
	 */
	private int getHybridisationState(IAtom atom1) {

        IBond.Order maxBondOrder = atom1.getMaxBondOrder();

//        if (atom1.getFormalNeighbourCount() == 1 || maxBondOrder > 4) {
        if (atom1.getFormalNeighbourCount() == 1) {
        	// WTF??
		} else if (atom1.getFormalNeighbourCount() == 2 ||
				   maxBondOrder == IBond.Order.TRIPLE) {
			//sp
			return 1;
		} else if (atom1.getFormalNeighbourCount() == 3 ||
				   (maxBondOrder == IBond.Order.DOUBLE)) {
			//sp2
			return 2;
		} else {
			//sp3
			return 3;
		}
		return -1;
	}
	
	
	/**
	 *  Gets the doubleBondConfiguration2D attribute of the AtomPlacer3D object
	 *
	 *@param  bond           the double bond
	 *@param  a              coordinates (Point2d) of atom1 connected to bond
	 *@param  b              coordinates (Point2d) of atom2 connected to bond
	 *@param  c              coordinates (Point2d) of atom3 connected to bond
	 *@param  d              coordinates (Point2d) of atom4 connected to bond
	 *@return                The doubleBondConfiguration2D value
	 *@exception  Exception  Description of the Exception
	 */
	private int getDoubleBondConfiguration2D(IBond bond,Point2d a, Point2d b,Point2d c,Point2d d) throws Exception{
		if (bond.getOrder() != IBond.Order.DOUBLE){
			return 0;
		}
		Point2d cb=new Point2d(c.x-b.x,c.y-b.y);
		Point2d xT=new Point2d(cb.x-1,cb.y);
		a.y=a.y-b.y-xT.y;
		d.y=d.y-b.y-xT.y;
		if ((a.y>0 && d.y>0)||(a.y<0 && d.y<0)){
			return 5;
		}else {return 6;}
	}

	/**
	 *  Gets the distanceValue attribute of the parameter set
	 *
	 * @param  id1            atom1 id
	 * @param  id2            atom2 id
	 * @return                The distanceValue value from the force field parameter set
	 * @exception  Exception  Description of the Exception
	 */
	public double getBondLengthValue(String id1, String id2) throws Exception {
		String dkey = "";
		if (pSet.containsKey(("bond" + id1 + ";" + id2))) {
			dkey="bond" + id1 + ";" + id2;
		}else if (pSet.containsKey(("bond" + id2 + ";" + id1))) {
			dkey = "bond" + id2 + ";" + id1;
		} else {
			System.out.println("KEYError:Unknown distance key in pSet: " + id2 + " ;" + id1+" take default bon length:"+DEFAULT_BOND_LENGTH);
			return DEFAULT_BOND_LENGTH;
		}
		return ((Double) (((List) pSet.get(dkey)).get(0))).doubleValue();
	}

	/**
	 *  Gets the angleKey attribute of the AtomPlacer3D object
	 *
	 * @param  id1            Description of the Parameter
	 * @param  id2            Description of the Parameter
	 * @param  id3            Description of the Parameter
	 * @return                The angleKey value
	 * @exception  Exception  Description of the Exception
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
			//logger.debug("KEYErrorAngle:Unknown angle key in pSet: " +id2 + " ; " + id3 + " ; " + id1+" take default angle:"+DEFAULT_ANGLE);
			return -1;
		}
		return ((Double) (((List) pSet.get(akey)).get(0))).doubleValue();
	}


	/**
	 *  Gets the nextUnplacedHeavyAtomWithAliphaticPlacedNeighbour from an atom container or molecule
	 *
	 * @return    The nextUnplacedHeavyAtomWithAliphaticPlacedNeighbour value
	 * author:    steinbeck,cho
	 */
	public IAtom getNextUnplacedHeavyAtomWithAliphaticPlacedNeighbour(IAtomContainer molecule) {
        Iterator bonds = molecule.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();
			if (bond.getAtom(0).getFlag(CDKConstants.ISPLACED) & !(bond.getAtom(1).getFlag(CDKConstants.ISPLACED))) {
				if (bond.getAtom(1).getFlag(CDKConstants.ISALIPHATIC) & !bond.getAtom(1).getSymbol().equals("H")) {
					return bond.getAtom(1);
				}
			}
			if (bond.getAtom(1).getFlag(CDKConstants.ISPLACED) & !(bond.getAtom(0).getFlag(CDKConstants.ISPLACED))) {
				if (bond.getAtom(0).getFlag(CDKConstants.ISALIPHATIC) & !bond.getAtom(0).getSymbol().equals("H")) {
					return bond.getAtom(0);
				}
			}
		}
		return null;
	}

	/**
	 *  Gets the nextPlacedHeavyAtomWithAliphaticPlacedNeigbor from an atom container or molecule
	 *
	 * @return    The nextUnplacedHeavyAtomWithUnplacedAliphaticNeigbor
	 * author: steinbeck,cho
	 */
	public IAtom getNextPlacedHeavyAtomWithUnplacedAliphaticNeighbour(IAtomContainer molecule) {
        Iterator bonds = molecule.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();
			IAtom atom0 = bond.getAtom(0);
			IAtom atom1 = bond.getAtom(1);
			if (atom0.getFlag(CDKConstants.ISPLACED) & !(atom1.getFlag(CDKConstants.ISPLACED))) {
				if (atom1.getFlag(CDKConstants.ISALIPHATIC) & !atom0.getSymbol().equals("H") & !atom1.getSymbol().equals("H")) {
					return atom0;
				}
			}
			if (atom1.getFlag(CDKConstants.ISPLACED) & !(atom0.getFlag(CDKConstants.ISPLACED))) {
				if (atom0.getFlag(CDKConstants.ISALIPHATIC) & !atom1.getSymbol().equals("H") & !atom0.getSymbol().equals("H")) {
					return atom1;
				}
			}
		}
		return null;
	}

	/**
	 *  Gets the nextPlacedHeavyAtomWithUnplacedRingNeighbour attribute of the AtomPlacer3D object
	 *
	 * @return    The nextPlacedHeavyAtomWithUnplacedRingNeighbour value
	 */
	public IAtom getNextPlacedHeavyAtomWithUnplacedRingNeighbour(IAtomContainer molecule) {
//		IBond[] bonds = molecule.getBonds();
        Iterator bonds = molecule.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();
			IAtom atom0 = bond.getAtom(0);
			IAtom atom1 = bond.getAtom(1);
			if (atom0.getFlag(CDKConstants.ISPLACED) & !(atom1.getFlag(CDKConstants.ISPLACED))) {
				if (atom1.getFlag(CDKConstants.ISINRING) & !atom0.getSymbol().equals("H") & !atom1.getSymbol().equals("H")) {
					return atom0;
				}
			}
			if (atom1.getFlag(CDKConstants.ISPLACED) & !(atom0.getFlag(CDKConstants.ISPLACED))) {
				if (atom0.getFlag(CDKConstants.ISINRING) & !atom1.getSymbol().equals("H") & !atom0.getSymbol().equals("H")) {
					return atom1;
				}
			}
		}
		return null;
	}

	/**
	 *  Gets the farthestAtom attribute of the AtomPlacer3D object
	 *
	 * @param  refAtomPoint  Description of the Parameter
	 * @param  ac            Description of the Parameter
	 * @return               The farthestAtom value
	 */
	public IAtom getFarthestAtom(Point3d refAtomPoint, IAtomContainer ac) {
		double distance = 0;
		IAtom atom = null;
		for (int i = 0; i < ac.getAtomCount(); i++) {
			if (ac.getAtom(i).getPoint3d() != null) {
				if (Math.abs(refAtomPoint.distance(ac.getAtom(i).getPoint3d())) > distance) {
					atom = ac.getAtom(i);
					distance = Math.abs(refAtomPoint.distance(ac.getAtom(i).getPoint3d()));
				}
			}
		}
		return atom;
	}

	/**
	 *  Gets the unplacedRingHeavyAtom attribute of the AtomPlacer3D object
	 *
	 * @param  atom  Description of the Parameter
	 * @return       The unplacedRingHeavyAtom value
	 */
	public IAtom getUnplacedRingHeavyAtom(IAtomContainer molecule, IAtom atom) {
		java.util.List<IBond> bonds = molecule.getConnectedBondsList(atom);
		IAtom connectedAtom = null;
		for (int i = 0; i < bonds.size(); i++) {
			connectedAtom = ((IBond)bonds.get(i)).getConnectedAtom(atom);
			if (!connectedAtom.getFlag(CDKConstants.ISPLACED) && !connectedAtom.getSymbol().equals("H") && connectedAtom.getFlag(CDKConstants.ISINRING)) {
				return connectedAtom;
			}
		}
		return connectedAtom;
	}

	/**
	 *  Calculates the geometric center of all placed atoms in the atomcontainer
	 *
	 * @return    Point3d the geometric center
	 */
	public Point3d geometricCenterAllPlacedAtoms(IAtomContainer molecule) {
		IAtomContainer allPlacedAtoms = getAllPlacedAtoms(molecule);
		return GeometryTools.get3DCenter(allPlacedAtoms);
	}


	/**
	 *  Returns a placed atom connected to a given atom
	 *
	 * @param  atom  The Atom whose placed bonding partners are to be returned
	 * @return       a placed heavy atom connected to a given atom
	 * author:      steinbeck
	 */
	public IAtom getPlacedHeavyAtom(IAtomContainer molecule, IAtom atom) {
		java.util.List<IBond> bonds = molecule.getConnectedBondsList(atom);
		for (int i = 0; i < bonds.size(); i++) {
			IAtom connectedAtom = ((IBond)bonds.get(i)).getConnectedAtom(atom);
			if (connectedAtom.getFlag(CDKConstants.ISPLACED) & !connectedAtom.getSymbol().equals("H")) {
				return connectedAtom;
			}
		}
		return null;
	}


	/**
	 *  Gets the first placed Heavy Atom around atomA which is not atomB
	 *
	 * @param  atomA  Description of the Parameter
	 * @param  atomB  Description of the Parameter
	 * @return        The placedHeavyAtom value
	 */
	public IAtom getPlacedHeavyAtom(IAtomContainer molecule, IAtom atomA, IAtom atomB) {
		java.util.List<IBond> bonds = molecule.getConnectedBondsList(atomA);
		for (int i = 0; i < bonds.size(); i++) {
			IAtom connectedAtom = ((IBond)bonds.get(i)).getConnectedAtom(atomA);
			if (connectedAtom.getFlag(CDKConstants.ISPLACED) && !connectedAtom.getSymbol().equals("H")
					 && connectedAtom != atomB) {
				return connectedAtom;
			}
		}
		return null;
	}

	/**
	 *  Gets the placed Heavy Atoms connected to an atom.
	 *
	 * @param  atom  The atom the atoms must be connected to.
	 * @return       The placed heavy atoms.
	 */
	public IAtomContainer getPlacedHeavyAtoms(IAtomContainer molecule, IAtom atom) {

		java.util.List<IBond> bonds = molecule.getConnectedBondsList(atom);
		IAtomContainer connectedAtoms = molecule.getBuilder().newAtomContainer();
		IAtom connectedAtom = null;
		for (int i = 0; i < bonds.size(); i++) {
			connectedAtom = ((IBond)bonds.get(i)).getConnectedAtom(atom);
			if (connectedAtom.getFlag(CDKConstants.ISPLACED) & !(connectedAtom.getSymbol().equals("H"))) {
				connectedAtoms.addAtom(connectedAtom);
			}
		}
		return connectedAtoms;
	}
	
	/**
	 *  Gets numberOfUnplacedHeavyAtoms (no Flag ISPLACED, no Hydrogens)
	 *
	 * @param  ac AtomContainer
	 * @return int #UnplacedAtoms 
	 */
	public int numberOfUnplacedHeavyAtoms(IAtomContainer ac) {
		int nUnplacedHeavyAtoms=0;
		for (int i = 0; i < ac.getAtomCount(); i++) {
			if (!ac.getAtom(i).getFlag(CDKConstants.ISPLACED) && !ac.getAtom(i).equals("H")) {
				nUnplacedHeavyAtoms+=1;
			}
		}
		return nUnplacedHeavyAtoms;
	}
	
	/**
	 *  Gets the allPlacedAtoms attribute of the AtomPlacer3D object
	 *
	 * @return    The allPlacedAtoms value
	 */
	private IAtomContainer getAllPlacedAtoms(IAtomContainer molecule) {
		IAtomContainer placedAtoms = new AtomContainer();
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			if (molecule.getAtom(i).getFlag(CDKConstants.ISPLACED)) {
				placedAtoms.addAtom(molecule.getAtom(i));
			}
		}
		return placedAtoms;
	}

	/**
	 *  True is all the atoms in the given AtomContainer have been placed
	 *
	 * @param  ac  The AtomContainer to be searched
	 * @return     True is all the atoms in the given AtomContainer have been placed
	 */
	public boolean allHeavyAtomsPlaced(IAtomContainer ac) {
		for (int i = 0; i < ac.getAtomCount(); i++) {
			if (!ac.getAtom(i).getFlag(CDKConstants.ISPLACED) & !(ac.getAtom(i).getSymbol().equals("H"))) {
				return false;
			}
		}
		return true;
	}
}


