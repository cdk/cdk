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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import javax.vecmath.GVector;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.Vector;

/**
 *  To work with the coordinates of the molecule, like get the 3d coordinates of
 *  the atoms or calculate the distance between two atoms.
 *
 * @author      vlabarta
 * @cdk.svnrev  $Revision$
 * @cdk.created 2005-03-03
 */
public abstract class ForceFieldTools {

    static Vector3d xji = null;
    static Vector3d xjk = null;
    static Vector3d xlk = null;
    static Vector3d v1 = null;
    static Vector3d v2 = null;

    static Point3d atomiCoordinates = new Point3d();
    static Point3d atomjCoordinates = new Point3d();
    static Point3d atomkCoordinates = new Point3d();
    static Point3d atomlCoordinates = new Point3d();
    static Point3d atom1Coordinates = new Point3d();
    static Point3d atom2Coordinates = new Point3d();



    /**
     *  Get the coordinates 3xN vector of a molecule of N atoms from its atom
     *  container
     *
     *@param  molecule  molecule store in an AtomContainer
     *@return           GVector with 3xN coordinates (N: atom numbers)
     */
    public static  GVector getCoordinates3xNVector(IAtomContainer molecule) {

        //logger.debug("molecule: " + molecule.toString());
        //logger.debug("Atoms number = " + molecule.getAtomCount());
        GVector coords3d_0 = new GVector(3 * (molecule.getAtomCount()));
        //logger.debug("coords3d_0 = " + coords3d_0);

        int j;

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            //logger.debug("thisAtom = " + thisAtom);
            //logger.debug("thisAtom.getPoint3d() = " + thisAtom.getPoint3d());

            j = 3 * i;
            coords3d_0.setElement(j, molecule.getAtom(i).getPoint3d().x);
            coords3d_0.setElement(j + 1, molecule.getAtom(i).getPoint3d().y);
            coords3d_0.setElement(j + 2, molecule.getAtom(i).getPoint3d().z);
        }

        //logger.debug("Atoms coordinates vector: " + coords3d_0);

        return coords3d_0;
    }


    /**
     *  Get the set of N coordinates 3d of a molecule of N atoms from its atom
     *  container
     *
     *@param  molecule  molecule store in an AtomContainer
     *@return           Vector with the N coordinates 3d of a molecule of N atoms
     */
    public static  Vector getPoint3dCoordinates(IAtomContainer molecule) {

        //logger.debug("molecule: " + molecule.toString());
        //logger.debug("Atoms number = " + molecule.getAtomCount());
        Vector point3dCoordinates = new Vector();

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            //logger.debug("thisAtom = " + thisAtom);
            //logger.debug("thisAtom.getPoint3d() = " + thisAtom.getPoint3d());

            point3dCoordinates.add(new Point3d(molecule.getAtom(i).getPoint3d()));
            //Point3d ia = (Point3d)point3dCoordinates.get(i);
            //logger.debug(i + "a = " + ia);
        }

        //logger.debug("Atoms 3d coordinates : " + point3dCoordinates);

        return point3dCoordinates;
    }


    /**
     *  Calculate 3d distance between two atoms coordinates
     *
     *@param  atom1  First atom.
     *@param  atom2  Second atom.
     *@return        Distance between the two atoms.
     */
    public static  double distanceBetweenTwoAtoms(IAtom atom1, IAtom atom2) {

        atom1Coordinates = atom1.getPoint3d();
        atom2Coordinates = atom2.getPoint3d();

        double atomsDistance = atom1Coordinates.distance(atom2Coordinates);

        System.out.println("distanceBetweenTwoAtoms, atomsDistance = " + atomsDistance);

        return atomsDistance;
    }


    /**
     *  Calculate 3d distance between two atoms in one molecule from its 3xN
     *  coordinate vector.
     *
     *@param  coords3d  Molecule 3xN coordinates.
     *@param  atom1Position               Atom position in the molecule (from 0 to N-1) for the first atom.
     *@param  atom2Position               Atom position in the molecule (from 0 to N-1) for the second atom.
     *@return                         Distance between the two atoms.
     */
    public static double distanceBetweenTwoAtomsFrom3xNCoordinates(GVector coords3d, int atom1Position, int atom2Position) {

        atom1Coordinates.x = coords3d.getElement(3 * atom1Position);
        atom1Coordinates.y = coords3d.getElement(3 * atom1Position + 1);
        atom1Coordinates.z = coords3d.getElement(3 * atom1Position + 2);
        atom2Coordinates.x = coords3d.getElement(3 * atom2Position);
        atom2Coordinates.y = coords3d.getElement(3 * atom2Position + 1);
        atom2Coordinates.z = coords3d.getElement(3 * atom2Position + 2);

        double atomsDistance;
        atomsDistance = atom1Coordinates.distance(atom2Coordinates);

        return atomsDistance;
    }


    /**
     *  Calculate 3d distance between two atoms in one molecule from its N
     *  coordinates 3d
     *
     *@param  atoms3dCoordinates  Vector with the N coordinates 3d
     *@param  atomNum1            Atom position in the 3xN coordinates vector (from
     *      0 to N-1) for the first atom.
     *@param  atomNum2            Atom position in the 3xN coordinates vector (from
     *      0 to N-1) for the second atom.
     *@return                     Distance between the two atoms in the molecule.
     */
    public static  double distanceBetweenTwoAtomsFromNCoordinates3d(Vector atoms3dCoordinates, int atomNum1, int atomNum2) {

        Point3d atom13dCoord = (Point3d) atoms3dCoordinates.get(atomNum1);
        Point3d atom23dCoord = (Point3d) atoms3dCoordinates.get(atomNum2);

        double atomsDistance;
        atomsDistance = atom13dCoord.distance(atom23dCoord);
        //logger.debug("atomsDistance = " + atomsDistance);

        return atomsDistance;
    }


    /**
     *  Assign the 3D coordinates saved in a GVector back to the molecule
     *
     *@param  moleculeCoords  GVector with the coordinates
     *@param  molecule        AtomContainer
     *@return                 the molecule as AtomContainer
     */
    public static  IAtomContainer assignCoordinatesToMolecule(GVector moleculeCoords, IAtomContainer molecule) {
        for (int i = 0; i < molecule.getAtomCount(); i++) {
            molecule.getAtom(i).setPoint3d(
            	new Point3d(
            		moleculeCoords.getElement(i * 3),
            		moleculeCoords.getElement(i * 3 + 1),
            		moleculeCoords.getElement(i * 3 + 2)
            	)
            );
        }
        return molecule;
    }


    /**
     *  Calculate 3d distance between two atoms from two different 3xN coordinate
     *  vectors.
     *
     *@param  atomsCoordinatesVector1  3xN coordinates from the first molecule.
     *@param  atomsCoordinatesVector2  3xN coordinates from the second molecule.
     *@param  atomNumM1                Atom position in the first molecule.
     *@param  atomNumM2                Atom position in the second molecule.
     *@return                          Distance between the two atoms.
     */
    public static  double distanceBetweenTwoAtomFromTwo3xNCoordinates(GVector atomsCoordinatesVector1, GVector atomsCoordinatesVector2, int atomNumM1, int atomNumM2) {

        double atomsDistance = 0;
        double difference;
        for (int j = 0; j < 3; j++) {
            difference = atomsCoordinatesVector1.getElement(atomNumM1 * 3 + j) - atomsCoordinatesVector2.getElement(atomNumM2 * 3 + j);
            difference = Math.pow(difference, 2);
            atomsDistance = atomsDistance + difference;
        }
        atomsDistance = Math.sqrt(atomsDistance);
        //logger.debug("atomsDistance = " + atomsDistance);
        return atomsDistance;
    }


    /**
     *  calculates the angle between two bonds, i-j and j-k, given the atoms i,j and k.
     *
     *@param  atomi  Atom i.
     *@param  atomj  Atom j.
     *@param  atomk  Atom k.
     *@return        Angle value.
     */
    public static  double angleBetweenTwoBonds(IAtom atomi, IAtom atomj, IAtom atomk) {

        Vector3d bondij = new Vector3d();
        bondij.sub(atomi.getPoint3d(), atomj.getPoint3d());

        Vector3d bondjk = new Vector3d();
        bondjk.sub(atomk.getPoint3d(), atomj.getPoint3d());

        return Math.toDegrees(bondij.angle(bondjk));
    }

    /**
     *  calculates the angle between two bonds, i-j and j-k, given the 3xN atoms coordinates and the atom i,j and k positions.
     *
     *@param  coords3d  3xN atoms coordinates.
     *@param  atomiPosition  Atom i position.
     *@param  atomjPosition  Atom j position.
     *@param  atomkPosition  Atom k position.
     *@return        Angle value.
     */
    public static  double angleBetweenTwoBondsFrom3xNCoordinates(GVector coords3d,int atomiPosition,int atomjPosition,int atomkPosition) {

        atomiCoordinates.set(coords3d.getElement(3 * atomiPosition), coords3d.getElement(3 * atomiPosition + 1), coords3d.getElement(3 * atomiPosition + 2));
        atomjCoordinates.set(coords3d.getElement(3 * atomjPosition), coords3d.getElement(3 * atomjPosition + 1), coords3d.getElement(3 * atomjPosition + 2));
        atomkCoordinates.set(coords3d.getElement(3 * atomkPosition), coords3d.getElement(3 * atomkPosition + 1), coords3d.getElement(3 * atomkPosition + 2));

        Vector3d bondij = new Vector3d();
        bondij.sub(atomiCoordinates, atomjCoordinates);

        Vector3d bondjk = new Vector3d();
        bondjk.sub(atomkCoordinates, atomjCoordinates);

        return Math.toDegrees(bondij.angle(bondjk));
    }



    /**
     *  Calculate the torsion angle from 4 atoms i,j,k and l, where i-j, j-k, and
     *  k-l are bonded pairs.
     *
     *@param  atomi  Atom i.
     *@param  atomj  Atom j.
     *@param  atomk  Atom k.
     *@param  atoml  Atom l.
     *@return        Torsion angle value.
     */
    public static  double torsionAngle(IAtom atomi, IAtom atomj, IAtom atomk, IAtom atoml) {

        xji = new Vector3d(atomi.getPoint3d());
        xji.sub(atomj.getPoint3d());
        xjk = new Vector3d(atomk.getPoint3d());
        xjk.sub(atomj.getPoint3d());
        xlk = new Vector3d(atomk.getPoint3d());
        xlk.sub(atoml.getPoint3d());

        v1 = new Vector3d();
        // v1 = xji x xjk / |xji x xjk|
        v1.cross(xji, xjk);
        v1.normalize();

        v2 = new Vector3d();
        // v2 = xjk x xlk / |xjk x xlk|
        v2.cross(xjk, xlk);
        v2.normalize();

        double torsion = v1.dot(v2);
        //Math.toDegrees(v1.angle(v2));
        System.out.println("torsion = " + torsion);

        return torsion;
    }


    /**
     *  Calculate the torsion angle from 4 atoms i,j,k and l positions, where i-j, j-k, and k-l are bonded pairs.
     *
     *@param  coords3d  3xN atoms coordinates.
     *@param  atomiPosition  Atom i position.
     *@param  atomjPosition  Atom j position.
     *@param  atomkPosition  Atom k position.
     *@param  atomlPosition  Atom l position.
     *@return        Torsion angle value.
     */
    public static  double torsionAngleFrom3xNCoordinates(GVector coords3d, int atomiPosition, int atomjPosition, int atomkPosition, int atomlPosition) {

        atomiCoordinates.set(coords3d.getElement(3 * atomiPosition), coords3d.getElement(3 * atomiPosition + 1), coords3d.getElement(3 * atomiPosition + 2));
        atomjCoordinates.set(coords3d.getElement(3 * atomjPosition), coords3d.getElement(3 * atomjPosition + 1), coords3d.getElement(3 * atomjPosition + 2));
        atomkCoordinates.set(coords3d.getElement(3 * atomkPosition), coords3d.getElement(3 * atomkPosition + 1), coords3d.getElement(3 * atomkPosition + 2));
        atomlCoordinates.set(coords3d.getElement(3 * atomlPosition), coords3d.getElement(3 * atomlPosition + 1), coords3d.getElement(3 * atomlPosition + 2));

        xji = new Vector3d(atomiCoordinates);
        xji.sub(atomjCoordinates);
        xjk = new Vector3d(atomkCoordinates);
        xjk.sub(atomjCoordinates);
        xlk = new Vector3d(atomkCoordinates);
        xlk.sub(atomlCoordinates);

        v1 = new Vector3d();	// v1 = xji x xjk / |xji x xjk|
        v1.cross(xji, xjk);
        v1.normalize();

        v2 = new Vector3d();	// v2 = xjk x xlk / |xjk x xlk|
        v2.cross(xjk, xlk);
        v2.normalize();

        double cosang = v1.dot(v2);

        if (cosang >1){
            cosang = 1;
        }
        if (cosang < -1){
            cosang=-1;
        }

        double torsion = Math.acos(cosang);
        double dot=xji.dot(v2);
        double absDot=Math.abs(dot);
        torsion = (dot/absDot > 0) ? torsion : (2 * Math.PI - torsion);

        //logger.debug("torsion" + torsion);

        return torsion;
    }

    public static  double toDegrees(double angleRad){
        return angleRad*180/Math.PI;
    }


}

