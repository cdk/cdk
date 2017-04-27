/* Copyright (C) 2005-2007  Christian Hoppe <chhoppe@users.sf.net>
 *                    2014  Mark B Vine (orcid:0000-0002-7794-0426)
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Place aliphatic <b>chains</b> with Z matrix method. Please use {@link
 * ModelBuilder3D} to place general molecules.
 *
 * @author         chhoppe
 * @cdk.keyword    AtomPlacer3D
 * @cdk.created    2004-10-8
 * @cdk.module     builder3d
 * @cdk.githash
 * @see ModelBuilder3D
 */
public class AtomPlacer3D {

    private Map<Object, List>   pSet                    = null;
    private double[]            distances;
    private int[]               firstAtoms              = null;
    private double[]            angles                  = null;
    private int[]               secondAtoms             = null;
    private double[]            dihedrals               = null;
    private int[]               thirdAtoms              = null;
    private final static double DIHEDRAL_EXTENDED_CHAIN = (180.0 / 180) * Math.PI;
    private final static double DIHEDRAL_BRANCHED_CHAIN = 0.0;
    private final static double DEFAULT_BOND_LENGTH     = 1.5;
    private final static double DEFAULT_SP3_ANGLE       = 109.471;
    private final static double DEFAULT_SP2_ANGLE       = 120.000;
    private final static double DEFAULT_SP_ANGLE        = 180.000;

    AtomPlacer3D() {}

    /**
     *  Initialize the atomPlacer class.
     *
     * @param  parameterSet  Force Field parameter as Hashtable
     */
    public void initilize(Map parameterSet) {
        pSet = parameterSet;
    }

    /**
     *  Count and find first heavy atom(s) (non Hydrogens) in a chain.
     *
     * @param  molecule the reference molecule for searching the chain
     * @param  chain  chain to be searched
     * @return        the atom number of the first heavy atom the number of heavy atoms in the chain
     */
    public int[] findHeavyAtomsInChain(IAtomContainer molecule, IAtomContainer chain) {
        int[] heavy = {-1, -1};
        int hc = 0;
        for (int i = 0; i < chain.getAtomCount(); i++) {
            if (isHeavyAtom(chain.getAtom(i))) {
                if (heavy[0] < 0) {
                    heavy[0] = molecule.indexOf(chain.getAtom(i));
                }
                hc++;
            }
        }
        heavy[1] = hc;
        return heavy;
    }

    /**
     *  Mark all atoms in chain as placed. (CDKConstant ISPLACED)
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
     *  Method assigns 3D coordinates to the heavy atoms in an aliphatic chain.
     *
     * @param molecule        the reference molecule for the chain
     * @param  chain          the atoms to be assigned, must be connected
     * @throws CDKException the 'chain' was not a chain
     */
    public void placeAliphaticHeavyChain(IAtomContainer molecule, IAtomContainer chain) throws CDKException {
        //logger.debug("******** Place aliphatic Chain *********");
        int[] first = new int[2];
        int counter = 1;
        int nextAtomNr = 0;
        String id1 = "";
        String id2 = "";
        String id3 = "";
        first = findHeavyAtomsInChain(molecule, chain);
        distances = new double[first[1]];
        firstAtoms = new int[first[1]];
        angles = new double[first[1]];
        secondAtoms = new int[first[1]];
        dihedrals = new double[first[1]];
        thirdAtoms = new int[first[1]];
        firstAtoms[0] = first[0];
        molecule.getAtom(firstAtoms[0]).setFlag(CDKConstants.VISITED, true);
        int hybridisation = 0;
        for (int i = 0; i < chain.getAtomCount(); i++) {
            if (isHeavyAtom(chain.getAtom(i))) {
                if (!chain.getAtom(i).getFlag(CDKConstants.VISITED)) {
                    //logger.debug("Counter:" + counter);
                    nextAtomNr = molecule.indexOf(chain.getAtom(i));
                    id2 = molecule.getAtom(firstAtoms[counter - 1]).getAtomTypeName();
                    id1 = molecule.getAtom(nextAtomNr).getAtomTypeName();

                    if (molecule.getBond(molecule.getAtom(firstAtoms[counter - 1]), molecule.getAtom(nextAtomNr)) == null)
                        throw new CDKException("atoms do not form a chain, please use ModelBuilder3D");

                    distances[counter] = getBondLengthValue(id1, id2);
                    //logger.debug(" Distance:" + distances[counter]);
                    firstAtoms[counter] = nextAtomNr;
                    secondAtoms[counter] = firstAtoms[counter - 1];
                    if (counter > 1) {
                        id3 = molecule.getAtom(firstAtoms[counter - 2]).getAtomTypeName();
                        hybridisation = getHybridisationState(molecule.getAtom(firstAtoms[counter - 1]));
                        angles[counter] = getAngleValue(id1, id2, id3);
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
                        thirdAtoms[counter] = firstAtoms[counter - 2];
                        //logger.debug(" Angle:" + angles[counter]);
                    } else {
                        angles[counter] = -1;
                        thirdAtoms[counter] = -1;
                    }
                    if (counter > 2) {
                        //double bond
                        try {
                            if (getDoubleBondConfiguration2D(
                                    molecule.getBond(molecule.getAtom(firstAtoms[counter - 1]),
                                            molecule.getAtom(firstAtoms[counter - 2])),
                                    (molecule.getAtom(firstAtoms[counter])).getPoint2d(),
                                    (molecule.getAtom(firstAtoms[counter - 1])).getPoint2d(),
                                    (molecule.getAtom(firstAtoms[counter - 2])).getPoint2d(),
                                    (molecule.getAtom(firstAtoms[counter - 3])).getPoint2d()) == 5) {
                                dihedrals[counter] = DIHEDRAL_BRANCHED_CHAIN;
                            } else {
                                dihedrals[counter] = DIHEDRAL_EXTENDED_CHAIN;
                            }
                        } catch (CDKException ex1) {
                            dihedrals[counter] = DIHEDRAL_EXTENDED_CHAIN;
                        }
                    } else {
                        dihedrals[counter] = -1;
                    }
                    counter++;
                }
            }
        }
    }

    /**
     * Takes the given Z Matrix coordinates and converts them to cartesian coordinates.
     * The first Atom end up in the origin, the second on on the x axis, and the third
     * one in the XY plane. The rest is added by applying the Zmatrix distances, angles
     * and dihedrals. Assign coordinates directly to the atoms.
     *
     * @param  molecule  the molecule to be placed in 3D
     * @param  flagBranched  marks branched chain
     * author: egonw,cho
     */

    public void zmatrixChainToCartesian(IAtomContainer molecule, boolean flagBranched) {
        Point3d result = null;
        for (int index = 0; index < distances.length; index++) {
            if (index == 0) {
                result = new Point3d(0d, 0d, 0d);
            } else if (index == 1) {
                result = new Point3d(distances[1], 0d, 0d);
            } else if (index == 2) {
                result = new Point3d(-Math.cos((angles[2] / 180) * Math.PI) * distances[2] + distances[1],
                        Math.sin((angles[2] / 180) * Math.PI) * distances[2], 0d);
            } else {
                Vector3d cd = new Vector3d();
                cd.sub(molecule.getAtom(thirdAtoms[index]).getPoint3d(), molecule.getAtom(secondAtoms[index])
                        .getPoint3d());

                Vector3d bc = new Vector3d();
                bc.sub(molecule.getAtom(secondAtoms[index]).getPoint3d(), molecule.getAtom(firstAtoms[index - 3])
                        .getPoint3d());

                Vector3d n1 = new Vector3d();
                n1.cross(cd, bc);
                n1.normalize();

                Vector3d n2 = null;
                if (index == 3 && flagBranched) {
                    n2 = AtomTetrahedralLigandPlacer3D.rotate(n1, bc, DIHEDRAL_BRANCHED_CHAIN);
                } else {
                    n2 = AtomTetrahedralLigandPlacer3D.rotate(n1, bc, dihedrals[index]);
                }
                n2.normalize();

                Vector3d ba = new Vector3d();
                if (index == 3 && flagBranched) {
                    ba = AtomTetrahedralLigandPlacer3D.rotate(cd, n2, (-angles[index] / 180) * Math.PI);
                    ba = AtomTetrahedralLigandPlacer3D.rotate(ba, cd, (-angles[index] / 180) * Math.PI);
                } else {
                    ba = AtomTetrahedralLigandPlacer3D.rotate(cd, n2, (-angles[index] / 180) * Math.PI);
                }

                ba.normalize();

                Vector3d ban = new Vector3d(ba);
                ban.scale(distances[index]);

                result = new Point3d();
                result.add(molecule.getAtom(firstAtoms[index - 1]).getPoint3d(), ban);
            }
            IAtom atom = molecule.getAtom(firstAtoms[index]);
            if ((atom.getPoint3d() == null || !atom.getFlag(CDKConstants.ISPLACED))
                    && !atom.getFlag(CDKConstants.ISINRING) && isHeavyAtom(atom)) {
                atom.setPoint3d(result);
                atom.setFlag(CDKConstants.ISPLACED, true);
            }
        }
    }

    /**
     *  Gets the hybridisation state of an atom.
     *
     *@param  atom1  atom
     *@return        The hybridisationState value (sp=1;sp2=2;sp3=3)
     */
    private int getHybridisationState(IAtom atom1) {

        IBond.Order maxBondOrder = atom1.getMaxBondOrder();

        //        if (atom1.getFormalNeighbourCount() == 1 || maxBondOrder > 4) {
        if (atom1.getFormalNeighbourCount() == 1) {
            // WTF??
        } else if (atom1.getFormalNeighbourCount() == 2 || maxBondOrder == IBond.Order.TRIPLE) {
            return 1; //sp
        } else if (atom1.getFormalNeighbourCount() == 3 || (maxBondOrder == IBond.Order.DOUBLE)) {
            return 2; //sp2
        } else {
            return 3; //sp3
        }
        return -1;
    }

    /**
     *  Gets the doubleBondConfiguration2D attribute of the AtomPlacer3D object
     *  using existing 2D coordinates.
     *
     *@param  bond           the double bond
     *@param  a              coordinates (Point2d) of atom1 connected to bond
     *@param  b              coordinates (Point2d) of atom2 connected to bond
     *@param  c              coordinates (Point2d) of atom3 connected to bond
     *@param  d              coordinates (Point2d) of atom4 connected to bond
     *@return                The doubleBondConfiguration2D value
     */
    private int getDoubleBondConfiguration2D(IBond bond, Point2d a, Point2d b, Point2d c, Point2d d)
            throws CDKException {
        if (bond.getOrder() != IBond.Order.DOUBLE) {
            return 0;
        }
        // no 2D coordinates or existing configuration
        if (a == null || b == null || c == null || d == null) return 0;
        Point2d cb = new Point2d(c.x - b.x, c.y - b.y);
        Point2d xT = new Point2d(cb.x - 1, cb.y);
        a.y = a.y - b.y - xT.y;
        d.y = d.y - b.y - xT.y;
        if ((a.y > 0 && d.y > 0) || (a.y < 0 && d.y < 0)) {
            return 5;
        } else {
            return 6;
        }
    }

    /**
     *  Gets the distanceValue attribute of the parameter set.
     *
     * @param  id1            atom1 id
     * @param  id2            atom2 id
     * @return                The distanceValue value from the force field parameter set
     */
    public double getBondLengthValue(String id1, String id2) {
        String dkey = "";
        if (pSet.containsKey(("bond" + id1 + ";" + id2))) {
            dkey = "bond" + id1 + ";" + id2;
        } else if (pSet.containsKey(("bond" + id2 + ";" + id1))) {
            dkey = "bond" + id2 + ";" + id1;
        } else {
            System.out.println("KEYError: Unknown distance key in pSet: " + id2 + ";" + id1
                    + " take default bond length: " + DEFAULT_BOND_LENGTH);
            return DEFAULT_BOND_LENGTH;
        }
        return ((Double) (pSet.get(dkey).get(0))).doubleValue();
    }

    /**
     *  Gets the angleKey attribute of the AtomPlacer3D object.
     *
     * @param  id1            Description of the Parameter
     * @param  id2            Description of the Parameter
     * @param  id3            Description of the Parameter
     * @return                The angleKey value
     */
    public double getAngleValue(String id1, String id2, String id3) {
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
        return ((Double) (pSet.get(akey).get(0))).doubleValue();
    }

    /**
     *  Gets the nextUnplacedHeavyAtomWithAliphaticPlacedNeighbour from an atom container or molecule.
     *
     * @param molecule
     * @return    The nextUnplacedHeavyAtomWithAliphaticPlacedNeighbour value
     * author:    steinbeck,cho
     */
    public IAtom getNextUnplacedHeavyAtomWithAliphaticPlacedNeighbour(IAtomContainer molecule) {
        Iterator<IBond> bonds = molecule.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            if (bond.getBeg().getFlag(CDKConstants.ISPLACED) && !(bond.getEnd().getFlag(CDKConstants.ISPLACED))) {
                if (isAliphaticHeavyAtom(bond.getEnd())) {
                    return bond.getEnd();
                }
            }
            if (bond.getEnd().getFlag(CDKConstants.ISPLACED) && !(bond.getBeg().getFlag(CDKConstants.ISPLACED))) {
                if (isAliphaticHeavyAtom(bond.getBeg())) {
                    return bond.getBeg();
                }
            }
        }
        return null;
    }

    /**
     * Find the first unplaced atom.
     * 
     * @param molecule molecule being built
     * @return an unplaced heavy atom, null if none.
     */
    IAtom getUnplacedHeavyAtom(IAtomContainer molecule) {
        for (IAtom atom : molecule.atoms()) {
            if (isUnplacedHeavyAtom(atom))
                return atom;
        }
        return null;
    }

    /**
     *  Gets the nextPlacedHeavyAtomWithAliphaticPlacedNeigbor from an atom container or molecule.
     *
     * @param molecule
     * @return    The nextUnplacedHeavyAtomWithUnplacedAliphaticNeigbor
     * author: steinbeck,cho
     */
    public IAtom getNextPlacedHeavyAtomWithUnplacedAliphaticNeighbour(IAtomContainer molecule) {
        Iterator<IBond> bonds = molecule.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            IAtom atom0 = bond.getBeg();
            IAtom atom1 = bond.getEnd();
            if (atom0.getFlag(CDKConstants.ISPLACED) && !(atom1.getFlag(CDKConstants.ISPLACED))) {
                if (isAliphaticHeavyAtom(atom1) && isHeavyAtom(atom0)) {
                    return atom0;
                }
            }
            if (atom1.getFlag(CDKConstants.ISPLACED) && !(atom0.getFlag(CDKConstants.ISPLACED))) {
                if (isAliphaticHeavyAtom(atom0) && isHeavyAtom(atom1)) {
                    return atom1;
                }
            }
        }
        return null;
    }

    /**
     *  Gets the nextPlacedHeavyAtomWithUnplacedRingNeighbour attribute of the AtomPlacer3D object.
     *
     * @param molecule  The atom container under consideration
     * @return          The nextPlacedHeavyAtomWithUnplacedRingNeighbour value
     */
    public IAtom getNextPlacedHeavyAtomWithUnplacedRingNeighbour(IAtomContainer molecule) {
        Iterator<IBond> bonds = molecule.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            IAtom atom0 = bond.getBeg();
            IAtom atom1 = bond.getEnd();
            if (atom0.getFlag(CDKConstants.ISPLACED) && !(atom1.getFlag(CDKConstants.ISPLACED))) {
                if (isRingHeavyAtom(atom1) && isHeavyAtom(atom0)) {
                    return atom0;
                }
            }
            if (atom1.getFlag(CDKConstants.ISPLACED) && !(atom0.getFlag(CDKConstants.ISPLACED))) {
                if (isRingHeavyAtom(atom0) && isHeavyAtom(atom1)) {
                    return atom1;
                }
            }
        }
        return null;
    }

    /**
     *  Gets the farthestAtom attribute of the AtomPlacer3D object.
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
     *  Gets the unplacedRingHeavyAtom attribute of the AtomPlacer3D object.
     *
     * @param molecule
     * @param  atom  Description of the Parameter
     * @return       The unplacedRingHeavyAtom value
     */
    public IAtom getUnplacedRingHeavyAtom(IAtomContainer molecule, IAtom atom) {
        List<IBond> bonds = molecule.getConnectedBondsList(atom);
        IAtom connectedAtom = null;
        for (IBond bond : bonds) {
            connectedAtom = bond.getConnectedAtom(atom);
            if (isUnplacedHeavyAtom(connectedAtom) && connectedAtom.getFlag(CDKConstants.ISINRING)) {
                return connectedAtom;
            }
        }
        return connectedAtom;
    }

    /**
     *  Calculates the geometric center of all placed atoms in the atomcontainer.
     *
     * @param molecule
     * @return    Point3d the geometric center
     */
    public Point3d geometricCenterAllPlacedAtoms(IAtomContainer molecule) {
        IAtomContainer allPlacedAtoms = getAllPlacedAtoms(molecule);
        return GeometryUtil.get3DCenter(allPlacedAtoms);
    }

    /**
     *  Returns a placed atom connected to a given atom.
     *
     * @param molecule
     * @param  atom  The Atom whose placed bonding partners are to be returned
     * @return       a placed heavy atom connected to a given atom
     * author:      steinbeck
     */
    public IAtom getPlacedHeavyAtom(IAtomContainer molecule, IAtom atom) {
        List<IBond> bonds = molecule.getConnectedBondsList(atom);
        for (IBond bond : bonds) {
            IAtom connectedAtom = bond.getConnectedAtom(atom);
            if (isPlacedHeavyAtom(connectedAtom)) {
                return connectedAtom;
            }
        }
        return null;
    }

    /**
     *  Gets the first placed Heavy Atom around atomA which is not atomB.
     *
     * @param molecule
     * @param  atomA  Description of the Parameter
     * @param  atomB  Description of the Parameter
     * @return        The placedHeavyAtom value
     */
    public IAtom getPlacedHeavyAtom(IAtomContainer molecule, IAtom atomA, IAtom atomB) {
        List<IBond> bonds = molecule.getConnectedBondsList(atomA);
        for (IBond bond : bonds) {
            IAtom connectedAtom = bond.getConnectedAtom(atomA);
            if (isPlacedHeavyAtom(connectedAtom) && connectedAtom != atomB) {
                return connectedAtom;
            }
        }
        return null;
    }

    /**
     *  Gets the placed Heavy Atoms connected to an atom.
     *
     * @param molecule
     * @param  atom  The atom the atoms must be connected to.
     * @return       The placed heavy atoms.
     */
    public IAtomContainer getPlacedHeavyAtoms(IAtomContainer molecule, IAtom atom) {

        List<IBond> bonds = molecule.getConnectedBondsList(atom);
        IAtomContainer connectedAtoms = molecule.getBuilder().newInstance(IAtomContainer.class);
        IAtom connectedAtom = null;
        for (IBond bond : bonds) {
            connectedAtom = bond.getConnectedAtom(atom);
            if (isPlacedHeavyAtom(connectedAtom)) {
                connectedAtoms.addAtom(connectedAtom);
            }
        }
        return connectedAtoms;
    }

    /**
     *  Gets numberOfUnplacedHeavyAtoms (no Flag ISPLACED, no Hydrogens)
     *
     * @param  ac AtomContainer
     * @return #UnplacedAtoms
     */
    public int numberOfUnplacedHeavyAtoms(IAtomContainer ac) {
        int nUnplacedHeavyAtoms = 0;
        for (int i = 0; i < ac.getAtomCount(); i++) {
            if (isUnplacedHeavyAtom(ac.getAtom(i))) {
                nUnplacedHeavyAtoms++;
            }
        }
        return nUnplacedHeavyAtoms;
    }

    /**
     *  Gets the allPlacedAtoms attribute of the AtomPlacer3D object.
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
     *  True is all the atoms in the given AtomContainer have been placed.
     *
     * @param  ac  The AtomContainer to be searched
     * @return     True is all the atoms in the given AtomContainer have been placed
     */
    public boolean allHeavyAtomsPlaced(IAtomContainer ac) {
        for (int i = 0; i < ac.getAtomCount(); i++) {
            if (isUnplacedHeavyAtom(ac.getAtom(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     *  Determine if the atom is non-hydrogen and has not been placed.
     *
     * @param  atom The atom to be checked
     * @return      True if the atom is non-hydrogen and has not been placed
     */
    boolean isUnplacedHeavyAtom(IAtom atom) {
        return (!atom.getFlag(CDKConstants.ISPLACED) && isHeavyAtom(atom));
    }

    /**
     *  Determine if the atom is non-hydrogen and has been placed.
     *
     * @param  atom The atom to be checked
     * @return      True if the atom is non-hydrogen and has been placed
     */
    boolean isPlacedHeavyAtom(IAtom atom) {
        return atom.getFlag(CDKConstants.ISPLACED) && isHeavyAtom(atom);
    }

    /**
     *  Determine if the atom is non-hydrogen and is aliphatic.
     *
     * @param  atom The atom to be checked
     * @return      True if the atom is non-hydrogen and is aliphatic
     */
    boolean isAliphaticHeavyAtom(IAtom atom) {
        return atom.getFlag(CDKConstants.ISALIPHATIC) && isHeavyAtom(atom);
    }

    /**
     * Determine if the atom is non-hydrogen and is in a ring.
     * Ring membership is determined from a property flag only, rather than a ring
     * membership test
     *
     * @param  atom The atom to be checked
     * @return      True if the atom is non-hydrogen and is in a ring
     */
    boolean isRingHeavyAtom(IAtom atom) {
        return atom.getFlag(CDKConstants.ISINRING) && isHeavyAtom(atom);
    }

    /**
     * Determine if the atom is heavy (non-hydrogen).
     *
     * @param  atom The atom to be checked
     * @return      True if the atom is non-hydrogen
     */
    boolean isHeavyAtom(IAtom atom) {
        return !atom.getSymbol().equals("H");
    }
}
