/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.modeling.builder3d;

import java.util.Hashtable;

import javax.vecmath.Point3d;
import javax.vecmath.Point2d;
import javax.vecmath.Vector3d;
import java.util.Vector;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;


/**
 * Place aliphatic chains with Z matrix method.
 *
 * @author         chhoppe
 * @cdk.keyword    AtomPlacer3D
 * @cdk.created    2004-10-8
 */
public class AtomPlacer3D {

	private Hashtable pSet = null;
	private double[] distances;
	private int[] first_atoms = null;
	private double[] angles = null;
	private int[] second_atoms = null;
	private double[] dihedrals = null;
	private int[] third_atoms = null;
	private final static double DIHEDRAL_EXTENDED_CHAIN = (180.0 / 180) * Math.PI;
	private final static double DIHEDRAL_BRANCHED_CHAIN = 0.0;
	private final static double DEFAULT_BOND_LENGTH = 1.5;
	private final static double DEFAULT_ANGLE = 109.471;
	
	AtomPlacer3D(){}
	
	/**
	 *  Initialize the atomPlacer class
	 * 
	 * @param  parameterSet  Force Field parameter as Hashtable
	 */
	public void initilize( Hashtable parameterSet) {
		pSet = parameterSet;
	}

	/**
	 *  Count and find first heavy atom(s) (non Hydrogens) in a chain
	 *
	 * @param  chain  chain to be searched
	 * @return        the atom number of the first heavy atom the number of heavy atoms in the chain
	 */
	public int[] findHeavyAtomsInChain(AtomContainer molecule, AtomContainer chain) {
		int[] heavy = {-1, -1};
		int hc = 0;
		for (int i = 0; i < chain.getAtomCount(); i++) {
			if (!(chain.getAtomAt(i).getSymbol()).equals("H")) {
				if (heavy[0] < 0) {
					heavy[0] = molecule.getAtomNumber(chain.getAtomAt(i));
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
	public AtomContainer markPlaced(AtomContainer ac) {
		for (int i = 0; i < ac.getAtomCount(); i++) {
			ac.getAtomAt(i).setFlag(CDKConstants.ISPLACED, true);
		}
		return ac;
	}

	
	/**
	 *  Method assigns 3Dcoordinates to the heavy atoms in an aliphatic chain
	 *
	 * @param  chain          the atoms to be assigned, must be connected
	 * @exception  Exception  Description of the Exception
	 */
	public void placeAliphaticHeavyChain(AtomContainer molecule, AtomContainer chain) throws Exception {
		//System.out.println("******** Place aliphatic Chain *********");
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
		molecule.getAtomAt(first_atoms[0]).setFlag(CDKConstants.VISITED, true);

		for (int i = 0; i < chain.getAtomCount(); i++) {
			if (!(chain.getAtomAt(i).getSymbol()).equals("H") &
					!chain.getAtomAt(i).getFlag(CDKConstants.VISITED)) {
				//System.out.print("Counter:" + counter);
				nextAtomNr = molecule.getAtomNumber(chain.getAtomAt(i));
				ID2 = molecule.getAtomAt(first_atoms[counter - 1]).getID();
				ID1 = molecule.getAtomAt(nextAtomNr).getID();
				distances[counter] = getBondLengthValue(ID1, ID2);
				//System.out.print(" Distance:" + distances[counter]);
				first_atoms[counter] = nextAtomNr;
				second_atoms[counter] = first_atoms[counter - 1];
				if (counter > 1) {
					ID3 = molecule.getAtomAt(first_atoms[counter - 2]).getID();
					angles[counter] = getAngleValue(ID1, ID2, ID3);
					third_atoms[counter] = first_atoms[counter - 2];
					//System.out.println(" Angle:" + angles[counter]);
				} else {
					angles[counter] = -1;
					third_atoms[counter] = -1;
				}
				if (counter > 2) {
					//double bond
					try{
						if (getDoubleBondConfiguration2D( molecule.getBond(molecule.getAtomAt(first_atoms[counter-1]),molecule.getAtomAt(first_atoms[counter-2])),
										(molecule.getAtomAt(first_atoms[counter])).getPoint2d(),(molecule.getAtomAt(first_atoms[counter-1])).getPoint2d(),
										(molecule.getAtomAt(first_atoms[counter-2])).getPoint2d(),(molecule.getAtomAt(first_atoms[counter-3])).getPoint2d())
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

	public void zmatrixChainToCartesian(AtomContainer molecule, boolean flag_branched) {
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
				cd.sub((molecule.getAtomAt(third_atoms[index])).getPoint3d(), (molecule.getAtomAt(second_atoms[index])).getPoint3d());

				Vector3d bc = new Vector3d();
				bc.sub(molecule.getAtomAt(second_atoms[index]).getPoint3d(), molecule.getAtomAt(first_atoms[index - 3]).getPoint3d());

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
				result.add(molecule.getAtomAt(first_atoms[index - 1]).getPoint3d(), ban);
			}

			if ((molecule.getAtomAt(first_atoms[index]).getPoint3d() == null || !(molecule.getAtomAt(first_atoms[index])).getFlag(CDKConstants.ISPLACED))
					 && !(molecule.getAtomAt(first_atoms[index])).getFlag(CDKConstants.ISINRING)
					 && !(molecule.getAtomAt(first_atoms[index])).getSymbol().equals("H")) {
				molecule.getAtomAt(first_atoms[index]).setPoint3d(result);
				molecule.getAtomAt(first_atoms[index]).setFlag(CDKConstants.ISPLACED, true);
			}
		}
	}


	
	private int getDoubleBondConfiguration2D(Bond bond,Point2d a, Point2d b,Point2d c,Point2d d) throws Exception{
		if (bond.getOrder()<1.5 || bond.getOrder()>2){
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
		return ((Double) (((Vector) pSet.get(dkey)).get(0))).doubleValue();
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
			System.out.println("KEYErrorAngle:Unknown angle key in pSet: " +id2 + " ; " + id3 + " ; " + id1+" take default angle:"+DEFAULT_ANGLE);
			return DEFAULT_ANGLE;
		}
		return ((Double) (((Vector) pSet.get(akey)).get(0))).doubleValue();
	}


	/**
	 *  Gets the nextUnplacedHeavyAtomWithAliphaticPlacedNeighbour from an atom container or molecule
	 *
	 * @return    The nextUnplacedHeavyAtomWithAliphaticPlacedNeighbour value
	 * author:    steinbeck,cho
	 */
	public Atom getNextUnplacedHeavyAtomWithAliphaticPlacedNeighbour(AtomContainer molecule) {
		Bond[] bonds = molecule.getBonds();
		for (int i = 0; i < bonds.length; i++) {
			Atom[] atoms = bonds[i].getAtoms();
			if (atoms[0].getFlag(CDKConstants.ISPLACED) & !(atoms[1].getFlag(CDKConstants.ISPLACED))) {
				if (atoms[1].getFlag(CDKConstants.ISALIPHATIC) & !atoms[1].getSymbol().equals("H")) {
					return atoms[1];
				}
			}
			if (atoms[1].getFlag(CDKConstants.ISPLACED) & !(atoms[0].getFlag(CDKConstants.ISPLACED))) {
				if (atoms[0].getFlag(CDKConstants.ISALIPHATIC) & !atoms[0].getSymbol().equals("H")) {
					return atoms[0];
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
	public Atom getNextPlacedHeavyAtomWithUnplacedAliphaticNeighbour(AtomContainer molecule) {
		Bond[] bonds = molecule.getBonds();
		for (int i = 0; i < bonds.length; i++) {
			Atom[] atoms = bonds[i].getAtoms();
			if (atoms[0].getFlag(CDKConstants.ISPLACED) & !(atoms[1].getFlag(CDKConstants.ISPLACED))) {
				if (atoms[1].getFlag(CDKConstants.ISALIPHATIC) & !atoms[0].getSymbol().equals("H") & !atoms[1].getSymbol().equals("H")) {
					return atoms[0];
				}
			}
			if (atoms[1].getFlag(CDKConstants.ISPLACED) & !(atoms[0].getFlag(CDKConstants.ISPLACED))) {
				if (atoms[0].getFlag(CDKConstants.ISALIPHATIC) & !atoms[1].getSymbol().equals("H") & !atoms[0].getSymbol().equals("H")) {
					return atoms[1];
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
	public Atom getNextPlacedHeavyAtomWithUnplacedRingNeighbour(AtomContainer molecule) {
		Bond[] bonds = molecule.getBonds();
		for (int i = 0; i < bonds.length; i++) {
			Atom[] atoms = bonds[i].getAtoms();
			if (atoms[0].getFlag(CDKConstants.ISPLACED) & !(atoms[1].getFlag(CDKConstants.ISPLACED))) {
				if (atoms[1].getFlag(CDKConstants.ISINRING) & !atoms[0].getSymbol().equals("H") & !atoms[1].getSymbol().equals("H")) {
					return atoms[0];
				}
			}
			if (atoms[1].getFlag(CDKConstants.ISPLACED) & !(atoms[0].getFlag(CDKConstants.ISPLACED))) {
				if (atoms[0].getFlag(CDKConstants.ISINRING) & !atoms[1].getSymbol().equals("H") & !atoms[0].getSymbol().equals("H")) {
					return atoms[1];
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
	public Atom getFarthestAtom(Point3d refAtomPoint, AtomContainer ac) {
		double distance = 0;
		Atom atom = null;
		for (int i = 0; i < ac.getAtomCount(); i++) {
			if (ac.getAtomAt(i).getPoint3d() != null) {
				if (Math.abs(refAtomPoint.distance(ac.getAtomAt(i).getPoint3d())) > distance) {
					atom = ac.getAtomAt(i);
					distance = Math.abs(refAtomPoint.distance(ac.getAtomAt(i).getPoint3d()));
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
	public Atom getUnplacedRingHeavyAtom(AtomContainer molecule, Atom atom) {

		Bond[] bonds = molecule.getConnectedBonds(atom);
		Atom connectedAtom = null;
		for (int i = 0; i < bonds.length; i++) {
			connectedAtom = bonds[i].getConnectedAtom(atom);
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
	public Point3d geometricCenterAllPlacedAtoms(AtomContainer molecule) {
		AtomContainer allPlacedAtoms = getAllPlacedAtoms(molecule);
		return allPlacedAtoms.get3DCenter();
	}

	/**
	 *  Returns an unplaced atom connected to a given atom
	 *
	 * @param  atom  The Atom whose unplaced bonding partners are to be returned
	 * @return       an unplaced heavy atom connected to a given atom
	 * author:      steinbeck,cho
	 */
	public Atom getUnplacedHeavyAtom(AtomContainer molecule, Atom atom) {

		Bond[] bonds = molecule.getConnectedBonds(atom);
		Atom connectedAtom = null;
		for (int i = 0; i < bonds.length; i++) {
			connectedAtom = bonds[i].getConnectedAtom(atom);
			if (!connectedAtom.getFlag(CDKConstants.ISPLACED) & !connectedAtom.getSymbol().equals("H")) {
				return connectedAtom;
			}
		}
		return connectedAtom;
	}

	/**
	 *  Returns a placed atom connected to a given atom
	 *
	 * @param  atom  The Atom whose placed bonding partners are to be returned
	 * @return       a placed heavy atom connected to a given atom
	 * author:      steinbeck
	 */
	public Atom getPlacedHeavyAtom(AtomContainer molecule, Atom atom) {

		Bond[] bonds = molecule.getConnectedBonds(atom);
		Atom connectedAtom = null;
		for (int i = 0; i < bonds.length; i++) {
			connectedAtom = bonds[i].getConnectedAtom(atom);
			if (connectedAtom.getFlag(CDKConstants.ISPLACED) & !connectedAtom.getSymbol().equals("H")) {
				return connectedAtom;
			}
		}
		return connectedAtom;
	}

	/**
	 *  Gets the placedAtom attribute of the AtomPlacer3D object
	 *
	 * @param  atomA  Description of the Parameter
	 * @param  atomB  Description of the Parameter
	 * @return        The placedAtom value
	 */
	public Atom getPlacedAtom(AtomContainer molecule, Atom atomA, Atom atomB) {

		Bond[] bonds = molecule.getConnectedBonds(atomA);
		Atom connectedAtom = null;
		for (int i = 0; i < bonds.length; i++) {
			connectedAtom = bonds[i].getConnectedAtom(atomA);
			if (connectedAtom.getFlag(CDKConstants.ISPLACED) && connectedAtom != atomB) {
				return connectedAtom;
			}
		}
		return connectedAtom;
	}

	/**
	 *  Gets the placedHeavyAtom attribute of the AtomPlacer3D object
	 *
	 * @param  atomA  Description of the Parameter
	 * @param  atomB  Description of the Parameter
	 * @return        The placedHeavyAtom value
	 */
	public Atom getPlacedHeavyAtom(AtomContainer molecule, Atom atomA, Atom atomB) {
		Bond[] bonds = molecule.getConnectedBonds(atomA);
		Atom connectedAtom = null;
		for (int i = 0; i < bonds.length; i++) {
			connectedAtom = bonds[i].getConnectedAtom(atomA);
			if (connectedAtom.getFlag(CDKConstants.ISPLACED) && !connectedAtom.getSymbol().equals("H")
					 && connectedAtom != atomB) {
				return connectedAtom;
			}
		}
		return connectedAtom;
	}

	/**
	 *  Gets the placedHeavyAtoms attribute of the AtomPlacer3D object
	 *
	 * @param  atom  Description of the Parameter
	 * @return       The placedHeavyAtoms value
	 */
	public AtomContainer getPlacedHeavyAtoms(AtomContainer molecule, Atom atom) {

		Bond[] bonds = molecule.getConnectedBonds(atom);
		AtomContainer connectedAtoms = new AtomContainer();
		Atom connectedAtom = null;
		for (int i = 0; i < bonds.length; i++) {
			connectedAtom = bonds[i].getConnectedAtom(atom);
			if (connectedAtom.getFlag(CDKConstants.ISPLACED) & !(connectedAtom.getSymbol().equals("H"))) {
				connectedAtoms.addAtom(connectedAtom);
			}
		}
		return connectedAtoms;
	}

	/**
	 *  Gets all unplacedAtoms around a atom
	 *
	 * @param  molecule AtomContainer
	 * @param  atom  atom: center of search
	 * @return       AtomContainer of unplaced atoms
	 */
	public AtomContainer getUnplacedAtoms(AtomContainer molecule, Atom atom) {

		Bond[] bonds = molecule.getConnectedBonds(atom);
		AtomContainer connectedAtoms = new AtomContainer();
		Atom connectedAtom = null;
		for (int i = 0; i < bonds.length; i++) {
			connectedAtom = bonds[i].getConnectedAtom(atom);
			if (!connectedAtom.getFlag(CDKConstants.ISPLACED)) {
				connectedAtoms.addAtom(connectedAtom);
			}
		}
		return connectedAtoms;
	}
	
	/**
	 *  Gets numberOfUnplacedHeavyAtoms (!Flag ISPLACED !H)
	 *
	 * @param  ac AtomContainer
	 * @return       int #UnplacedAtoms 
	 */
	public int numberOfUnplacedHeavyAtoms(AtomContainer ac) {
		int nUnplacedHeavyAtoms=0;
		for (int i = 0; i < ac.getAtomCount(); i++) {
			if (!ac.getAtomAt(i).getFlag(CDKConstants.ISPLACED) && !ac.getAtomAt(i).equals("H")) {
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
	public AtomContainer getAllPlacedAtoms(AtomContainer molecule) {
		AtomContainer placedAtoms = new AtomContainer();
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			if (molecule.getAtomAt(i).getFlag(CDKConstants.ISPLACED)) {
				placedAtoms.addAtom(molecule.getAtomAt(i));
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
	public boolean allHeavyAtomsPlaced(AtomContainer ac) {
		for (int i = 0; i < ac.getAtomCount(); i++) {
			if (!ac.getAtomAt(i).getFlag(CDKConstants.ISPLACED) & !(ac.getAtomAt(i).getSymbol().equals("H"))) {
				return false;
			}
		}
		return true;
	}
}


