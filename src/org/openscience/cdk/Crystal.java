/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */

package org.openscience.cdk;

import org.openscience.cdk.exception.*;
import javax.vecmath.Point3d;

/**
 * Class representing a molecular crystal.
 * The crystal is described with molecules in fractional
 * coordinates and three cell axes: a,b and c.
 *
 * The crystal is designed to store only the assymetric atoms. 
 * Though this is not enforced, it is assumed by all methods.
 *
 * @keyword crystal
 */
public class Crystal extends AtomContainer implements java.io.Serializable, Cloneable
{

    /** x,y,z components of a axis */
    private double ax,ay,az;
    /** x,y,z components of b axis */
    private double bx,by,bz;
    /** x,y,z components of c axis */
    private double cx,cy,cz;

    /**
     * Number of symmetry related atoms.
     */
    private int Z = 1;

    /**
     * Number of symmetry related atoms.
     */
    private String spaceGroup = "P1";

    /**
     * Constructs a new crystal with zero length cell axis.
     */
    public Crystal() {
        setZeroAxes();
    }

    /**
     * Constructs a new crystal with zero length cell axis
     * and adds the atoms in the AtomContainer as cell content.
     *
     * @param ac  the AtomContainer providing the atoms and bonds
     */
    public Crystal(AtomContainer ac) {
        this();
        add(ac);
    }

    /**
     * Sets the A unit cell axes in carthesian coordinates in a 
     * eucledian space.
     *
     * @param     x     the x coordinate of the vector
     * @param     y     the y coordinate of the vector
     * @param     z     the z coordinate of the vector
     *
     * @see       #getA
     */
    public void setA(double x, double y, double z) {
        ax = x; ay = y; az = z;
    }

    /**
     * Gets the A unit cell axes in carthesian coordinates
     * as a three element double array.
     *
     * @return a 3-item double array with the x,y and z coordinate
     *
     * @see       #setA
     */
    public double[] getA() {
        double[] result = new double[3];
        result[0] = ax;
        result[1] = ay;
        result[2] = az;
        return result;
    }

    /**
     * Adds the atoms in the AtomContainer as cell content.
     */
    public void add(AtomContainer ac) {
        super.add(ac);
    }

    /**
     * Adds the atom to the crystal. Symmetry related atoms should
     * not be added unless P1 space group is used.
     */
    public void addAtom(Atom a) {
        super.addAtom(a);
    }

    /**
     * Sets the B unit cell axes in carthesian coordinates.
     *
     * @param     x     the x coordinate of the vector
     * @param     y     the y coordinate of the vector
     * @param     z     the z coordinate of the vector
     *
     * @see       #getB
     */
    public void setB(double x, double y, double z) {
        bx = x; by = y; bz = z;
    }

    /**
     * Gets the B unit cell axes in carthesian coordinates
     * as a three element double array.
     *
     * @return a 3-item double array with the x,y and z coordinate
     *
     * @see       #setB
     */
    public double[] getB() {
        double[] result = new double[3];
        result[0] = bx;
        result[1] = by;
        result[2] = bz;
        return result;
    }

    /**
     * Sets the C unit cell axes in carthesian coordinates.
     *
     * @param     x     the x coordinate of the vector
     * @param     y     the y coordinate of the vector
     * @param     z     the z coordinate of the vector
     *
     * @see       #getC
     */
    public void setC(double x, double y, double z) {
        cx = x; cy = y; cz = z;
    }

    /**
     * Gets the C unit cell axes in carthesian coordinates
     * as a three element double array.
     *
     * @return a 3-item double array with the x,y and z coordinate
     *
     * @see       #setC
     */
    public double[] getC() {
        double[] result = new double[3];
        result[0] = cx;
        result[1] = cy;
        result[2] = cz;
        return result;
    }

    /**
     * Gets the space group of this crystal.
     *
     * @return the space group of this crystal structure
     *
     * @see       #setSpaceGroup
     */
    public String getSpaceGroup() {
        return spaceGroup;
    }

    /**
     * Sets the space group of this crystal. If not space group
     * is not recognized then P1 is assumed.
     *
     * Recognized space group strings:
     *   "P1"
     *   "P 2_1 2_1 2_1"
     *
     * @param   group  the space group of this crystal structure
     *
     * @see       #getSpaceGroup
     */
    public void setSpaceGroup(String group) {
        if ("P1".equals(group)) {
            setZ(1);
        } else if ("P 2_1 2_1 2_1".equals(group)) {
            setZ(4);
        } else {
            return;
        }
        spaceGroup = group;
    }

    /**
     * Gets the number of asymmetric parts in the unit cell.
     *
     * @return Z
     */
    public int getZ() {
        return Z;
    }

    /**
     *  Converts the cell into a P1 cell.
     *  The function assumes that unit cell axes are properly set.
     *
     * Recognized space group strings:
     *   "P1"
     *   "P 2_1 2_1 2_1"
     *
     * @return The Crystal with P1 space group.
     */
    public Crystal getP1Cell() {
        Crystal result = (Crystal)this.clone();
        if ("P 2_1 2_1 2_1".equals(spaceGroup)) {
            for (int i =0; i < this.getAtomCount(); i++) {
                Atom a = this.getAtomAt(i);
                /* symmetry operations:

                identity (skipped) :   x      y      z
                                        -x+0.5 -y      z+0.5   I
                                        -x      y+0.5 -z+0.5   II
                                        x+0.5 -y+0.5 -z        III
                */

                // do not take into account moving into unit cell
                Point3d point = a.getPoint3D();

                if (point != null) {
                    // point I
                    Point3d newPoint = new Point3d();
                    newPoint.x = -1.0*point.x + 0.5*(ax + bx + cx);
                    newPoint.y = -1.0*point.y;
                    newPoint.z =      point.z + 0.5*(az + bz + cz);
                    Atom syma = (Atom)a.clone();
                    syma.setPoint3D(newPoint);
                    result.addAtom(syma);

                    // point II
                    newPoint.x = -1.0*point.x + 0.5*(ax + bx + cx);
                    newPoint.y = -1.0*point.y;
                    newPoint.z =      point.z + 0.5*(az + bz + cz);
                    syma = (Atom)a.clone();
                    syma.setPoint3D(newPoint);
                    result.addAtom(syma);

                    // point III
                    newPoint.x = -1.0*point.x + 0.5*(ax + bx + cx);
                    newPoint.y = -1.0*point.y;
                    newPoint.z =      point.z + 0.5*(az + bz + cz);
                    syma = (Atom)a.clone();
                    syma.setPoint3D(newPoint);
                    result.addAtom(syma);
                } else {
                    System.err.println("WARN: Did not copy 3D coordinates!");
                }
            }
        } else {
            // assume P1
        }
        return result;
    }

    /**
     *  Makes a clone of this crystal.
     *
     * @return The cloned crystal.
     */
    public Object clone() {
        Crystal o = null;
        try {
            o = (Crystal)super.clone();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        o.setSpaceGroup(this.getSpaceGroup());
        o.setA(this.ax, this.ay, this.az);
        o.setB(this.bx, this.by, this.bz);
        o.setC(this.cx, this.cy, this.cz);
        return o;
    }

    /**
     * Returns a String representation of this crystal.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Crystal:\n");
        sb.append("  space group: " + getSpaceGroup() + "\n");
        sb.append("  Z: " + getZ() + "\n");
        sb.append("  a : " + ax + ", " + ay + ", " + az + "\n");
        sb.append("  b : " + bx + ", " + by + ", " + bz + "\n");
        sb.append("  c : " + cx + ", " + cy + ", " + cz + "\n");
        sb.append("  atoms: " + getAtomCount()*getZ() + "\n");
        sb.append("  assym atoms: " + getAtomCount() + "\n");
        return sb.toString();
    }

    /**
     *  Initializes the unit cell axes to zero length.
     */
    private void setZeroAxes() {
        ax = 0.0; ay = 0.0; az = 0.0;
        bx = 0.0; by = 0.0; bz = 0.0;
        cx = 0.0; cy = 0.0; cz = 0.0;
    }

    /**
     * Sets the number of asymmetric parts in the unit cell.
     * This is used internally only by setSpaceGroup()
     *
     * @param   z Z
     * @see     setSpaceGroup()
     */
    private void setZ(int z) {
        Z = z;
    }

}
