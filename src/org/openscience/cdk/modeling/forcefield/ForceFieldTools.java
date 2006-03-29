package org.openscience.cdk.modeling.forcefield;

import java.util.Vector;

import javax.vecmath.GVector;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;

/**
 *  To work with the coordinates of the molecule, like get the 3d coordinates of
 *  the atoms or calculate the distance between two atoms.
 *
 * @author      vlabarta
 * @cdk.created 2005-03-03
 */
public class ForceFieldTools {

	Point3d atomiCoordinates = new Point3d();
	Point3d atomjCoordinates = new Point3d();
	Point3d atomkCoordinates = new Point3d();
	Point3d atomlCoordinates = new Point3d();
	
	/**
	 *  Constructor for the ForceFieldTools object
	 */
	public ForceFieldTools() { }


	/**
	 *  Get the coordinates 3xN vector of a molecule of N atoms from its atom
	 *  container
	 *
	 *@param  molecule  molecule store in an AtomContainer
	 *@return           GVector with 3xN coordinates (N: atom numbers)
	 */
	public GVector getCoordinates3xNVector(AtomContainer molecule) {

		//System.out.println("molecule: " + molecule.toString());
		//System.out.println("Atoms number = " + molecule.getAtomCount());
		GVector coords3d_0 = new GVector(3 * (molecule.getAtomCount()));
		//System.out.println("coords3d_0 = " + coords3d_0);

		int j = 0;

		for (int i = 0; i < molecule.getAtomCount(); i++) {
			//System.out.println("thisAtom = " + thisAtom);
			//System.out.println("thisAtom.getPoint3d() = " + thisAtom.getPoint3d());

			j = 3 * i;
			coords3d_0.setElement(j, molecule.getAtomAt(i).getX3d());
			coords3d_0.setElement(j + 1, molecule.getAtomAt(i).getY3d());
			coords3d_0.setElement(j + 2, molecule.getAtomAt(i).getZ3d());
		}

		//System.out.println("Atoms coordinates vector: " + coords3d_0);

		return coords3d_0;
	}


	/**
	 *  Get the set of N coordinates 3d of a molecule of N atoms from its atom
	 *  container
	 *
	 *@param  molecule  molecule store in an AtomContainer
	 *@return           Vector with the N coordinates 3d of a molecule of N atoms
	 */
	public Vector getPoint3dCoordinates(AtomContainer molecule) {

		//System.out.println("molecule: " + molecule.toString());
		//System.out.println("Atoms number = " + molecule.getAtomCount());
		Vector point3dCoordinates = new Vector();

		for (int i = 0; i < molecule.getAtomCount(); i++) {
			//System.out.println("thisAtom = " + thisAtom);
			//System.out.println("thisAtom.getPoint3d() = " + thisAtom.getPoint3d());

			point3dCoordinates.add(new Point3d(molecule.getAtomAt(i).getPoint3d()));
			//Point3d ia = (Point3d)point3dCoordinates.get(i);
			//System.out.println(i + "a = " + ia);
		}

		//System.out.println("Atoms 3d coordinates : " + point3dCoordinates);

		return point3dCoordinates;
	}


	/**
	 *  Calculate 3d distance between two atoms coordinates
	 *
	 *@param  atom1  First atom.
	 *@param  atom2  Second atom.
	 *@return        Distance between the two atoms.
	 */
	public double distanceBetweenTwoAtoms(Atom atom1, Atom atom2) {

		Point3d atom1Coordinates = new Point3d(atom1.getPoint3d());
		Point3d atom2Coordinates = new Point3d(atom2.getPoint3d());
		double atomsDistance = 0;
		atomsDistance = atom1Coordinates.distance(atom2Coordinates);
		//System.out.println("atomsDistance = " + atomsDistance);

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
	public double distanceBetweenTwoAtomsFrom3xNCoordinates(GVector coords3d, int atom1Position, int atom2Position) {

		Point3d atom1Coordinates = new Point3d(coords3d.getElement(3 * atom1Position), coords3d.getElement(3 * atom1Position + 1), coords3d.getElement(3 * atom1Position + 2));
		Point3d atom2Coordinates = new Point3d(coords3d.getElement(3 * atom2Position), coords3d.getElement(3 * atom2Position + 1), coords3d.getElement(3 * atom2Position + 2));
		double atomsDistance = 0;
		atomsDistance = atom1Coordinates.distance(atom2Coordinates);
		//System.out.println("atomsDistance = " + atomsDistance);

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
	public double distanceBetweenTwoAtomsFromNCoordinates3d(Vector atoms3dCoordinates, int atomNum1, int atomNum2) {

		Point3d atom13dCoord = (Point3d) atoms3dCoordinates.get(atomNum1);
		Point3d atom23dCoord = (Point3d) atoms3dCoordinates.get(atomNum2);

		double atomsDistance = 0;
		atomsDistance = atom13dCoord.distance(atom23dCoord);
		//System.out.println("atomsDistance = " + atomsDistance);

		return atomsDistance;
	}


	/**
	 *  Assign the 3D coordinates saved in a GVector back to the molecule
	 *
	 *@param  moleculeCoords  GVector with the coordinates
	 *@param  molecule        AtomContainer
	 *@return                 the molecule as AtomContainer
	 */
	public AtomContainer assignCoordinatesToMolecule(GVector moleculeCoords, AtomContainer molecule) {
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			molecule.getAtomAt(i).setX3d(moleculeCoords.getElement(i * 3));
			molecule.getAtomAt(i).setY3d(moleculeCoords.getElement(i * 3 + 1));
			molecule.getAtomAt(i).setZ3d(moleculeCoords.getElement(i * 3 + 2));
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
	public double distanceBetweenTwoAtomFromTwo3xNCoordinates(GVector atomsCoordinatesVector1, GVector atomsCoordinatesVector2, int atomNumM1, int atomNumM2) {

		double atomsDistance = 0;
		double difference = 0;
		for (int j = 0; j < 3; j++) {
			difference = atomsCoordinatesVector1.getElement(atomNumM1 * 3 + j) - atomsCoordinatesVector2.getElement(atomNumM2 * 3 + j);
			difference = Math.pow(difference, 2);
			atomsDistance = atomsDistance + difference;
		}
		atomsDistance = Math.sqrt(atomsDistance);
		//System.out.println("atomsDistance = " + atomsDistance);
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
	public double angleBetweenTwoBonds(Atom atomi, Atom atomj, Atom atomk) {

		Vector3d bondij = new Vector3d();
		bondij.sub((Tuple3d)atomi.getPoint3d(), (Tuple3d)atomj.getPoint3d());

		Vector3d bondjk = new Vector3d();
		bondjk.sub((Tuple3d)atomk.getPoint3d(), (Tuple3d)atomj.getPoint3d());

		double angleBetweenTwoBonds = Math.toDegrees(bondij.angle(bondjk));
		return angleBetweenTwoBonds;
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
	public double angleBetweenTwoBondsFrom3xNCoordinates(GVector coords3d,int atomiPosition,int atomjPosition,int atomkPosition) {

		this.atomiCoordinates.set(coords3d.getElement(3 * atomiPosition), coords3d.getElement(3 * atomiPosition + 1), coords3d.getElement(3 * atomiPosition + 2));
		this.atomjCoordinates.set(coords3d.getElement(3 * atomjPosition), coords3d.getElement(3 * atomjPosition + 1), coords3d.getElement(3 * atomjPosition + 2));
		this.atomkCoordinates.set(coords3d.getElement(3 * atomkPosition), coords3d.getElement(3 * atomkPosition + 1), coords3d.getElement(3 * atomkPosition + 2));
		
		Vector3d bondij = new Vector3d();
		bondij.sub((Tuple3d)this.atomiCoordinates, (Tuple3d)atomjCoordinates);

		Vector3d bondjk = new Vector3d();
		bondjk.sub((Tuple3d)atomkCoordinates, (Tuple3d)atomjCoordinates);

		double angleBetweenTwoBonds = Math.toDegrees(bondij.angle(bondjk));
		return angleBetweenTwoBonds;
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
	public double torsionAngle(Atom atomi, Atom atomj, Atom atomk, Atom atoml) {

		Vector3d xji = new Vector3d((Tuple3d) atomi.getPoint3d());
		xji.sub(atomj.getPoint3d());
		Vector3d xjk = new Vector3d((Tuple3d) atomk.getPoint3d());
		xjk.sub(atomj.getPoint3d());
		Vector3d xlk = new Vector3d((Tuple3d) atomk.getPoint3d());
		xlk.sub(atoml.getPoint3d());

		Vector3d v1 = new Vector3d();
		// v1 = xji x xjk / |xji x xjk|
		v1.cross(xji, xjk);
		v1.normalize();

		Vector3d v2 = new Vector3d();
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
	public double torsionAngleFrom3xNCoordinates(GVector coords3d, int atomiPosition, int atomjPosition, int atomkPosition, int atomlPosition) {

		this.atomiCoordinates.set(coords3d.getElement(3 * atomiPosition), coords3d.getElement(3 * atomiPosition + 1), coords3d.getElement(3 * atomiPosition + 2));
		this.atomjCoordinates.set(coords3d.getElement(3 * atomjPosition), coords3d.getElement(3 * atomjPosition + 1), coords3d.getElement(3 * atomjPosition + 2));
		this.atomkCoordinates.set(coords3d.getElement(3 * atomkPosition), coords3d.getElement(3 * atomkPosition + 1), coords3d.getElement(3 * atomkPosition + 2));
		this.atomlCoordinates.set(coords3d.getElement(3 * atomlPosition), coords3d.getElement(3 * atomlPosition + 1), coords3d.getElement(3 * atomlPosition + 2));
		
		Vector3d xji = new Vector3d((Tuple3d)this.atomiCoordinates);
		xji.sub(this.atomjCoordinates);
		Vector3d xjk = new Vector3d((Tuple3d)this.atomkCoordinates);
		xjk.sub(this.atomjCoordinates);
		Vector3d xlk = new Vector3d((Tuple3d)this.atomkCoordinates);
		xlk.sub(this.atomlCoordinates);
		
		Vector3d v1 = new Vector3d();	// v1 = xji x xjk / |xji x xjk|
		v1.cross(xji, xjk);
		v1.normalize();
		
		Vector3d v2 = new Vector3d();	// v2 = xjk x xlk / |xjk x xlk|
		v2.cross(xjk, xlk);
		v2.normalize();
		
		double cosang = v1.dot(v2);
		
		if (cosang >1){
			cosang = 1;
		}
		if (cosang < -1){
			cosang=-1;
		}
		
		double torsion = (double)Math.acos(cosang);
		double dot=xji.dot(v2);
		double absDot=Math.abs(dot);
		torsion = (dot/absDot > 0) ? torsion : (2 * Math.PI - torsion);
		
		//System.out.println("torsion" + torsion);

		return torsion;
	}
	
	public double toDegrees(double angleRad){
		return angleRad*180/Math.PI;
	}


}

