/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2000-2004  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk;

import javax.vecmath.Point3d;

/**
 * Represents the idea of an chemical atom.
 *
 * <p>An Atom class is instantiated with at least the atom symbol:
 * <pre>
 *   Atom a = new Atom("C");
 * </pre>
 *
 * <p>Once instantiated all field not filled by passing parameters
 * to the constructured are null. Atoms can be configured by using
 * the IsotopeFactory.configure() method:
 * <pre>
 *   IsotopeFactory if = IsotopeFactory.getInstance();
 *   if.configure(a);
 * </pre>
 *
 * <p>More examples about using this class can be found in the
 * Junit test for this class.
 *
 * @cdk.module core
 *
 * @author     steinbeck
 * @cdk.created    2000-10-02
 * @cdk.keyword    atom
 *
 * @see  org.openscience.cdk.tools.IsotopeFactory#getInstance()
 */
public class Atom extends AtomType implements java.io.Serializable, Cloneable  {
    
        /* Let's keep this exact specification
         * of what kind of point2d we're talking of here,
         * sinces there are so many around in the java standard api */

        /**
         *  A 2D point specifying the location of this atom in a 2D coordinate 
         *  space.
         */
        protected javax.vecmath.Point2d point2d;
        /**
         *  A 3 point specifying the location of this atom in a 3D coordinate 
         *  space.
         */
        protected javax.vecmath.Point3d point3d;
        /**
         *  A 3 point specifying the location of this atom in a crystal unit cell.
         */
        protected javax.vecmath.Point3d fractionalPoint3d;
        /**
         *  The number of implicitly bound hydrogen atoms for this atom.
         */
        protected int hydrogenCount;
        /**
         *  A stereo parity descriptor for the stereochemistry of this atom.
         */
        protected int stereoParity;
        /**
         *  The partial charge of the atom.
         */
        protected double charge;

        /**
         * Constructs an completely unset Atom.
         */
        public Atom() {
            this(null);
        }
        
        /**
         * Constructs an Atom from a String containing an element symbol.
         *
         * @param   elementSymbol  The String describing the element for the Atom
         */
        public Atom(String elementSymbol)
        {
                super(elementSymbol);
                this.fractionalPoint3d = null;
                this.point3d = null;
                this.point2d = null;
        }

        /**
         * Constructs an Atom from an Element and a Point3d.
         *
         * @param   elementSymbol   The symbol of the atom
         * @param   point3d         The 3D coordinates of the atom
         */
        public Atom(String elementSymbol, javax.vecmath.Point3d point3d)
        {
                this(elementSymbol);
                this.point3d = point3d;
        }

        /**
         * Constructs an Atom from an Element and a Point2d.
         *
         * @param   elementSymbol   The Element
         * @param   point2d         The Point
         */
        public Atom(String elementSymbol, javax.vecmath.Point2d point2d)
        {
                this(elementSymbol);
                this.point2d = point2d;
        }

        /**
         *  Sets the partial charge of this atom.
         *
         * @param  charge  The partial charge
         *
         * @see    #getCharge
         */
        public void setCharge(double charge) {
               this.charge = charge;
        }

        /**
         *  Returns the partial charge of this atom.
         *
         * @return the charge of this atom
         *
         * @see    #setCharge
         */
        public double getCharge() {
               return this.charge;
        }

        /**
         *  Sets the hydrogen count of this atom.
         *
         * @param  hydrogenCount  The number of hydrogen atoms bonded to this atom.
         *
         * @see    #getHydrogenCount
         */
        public void setHydrogenCount(int hydrogenCount) {
                this.hydrogenCount = hydrogenCount;
        }

        /**
         *  Returns the hydrogen count of this atom.
         *
         * @return    The hydrogen count of this atom.
         *
         * @see       #setHydrogenCount
         */
        public int getHydrogenCount() {
                return this.hydrogenCount;
        }

        /**
         *
         * Sets a point specifying the location of this
         * atom in a 2D space.
         *
         * @param  point2d  A point in a 2D plane
         *
         * @see    #getPoint2d
         */
        public void setPoint2d(javax.vecmath.Point2d point2d) {
                this.point2d = point2d;
        }
        /**
         *
         * Sets a point specifying the location of this
         * atom in 3D space.
         *
         * @param  point3d  A point in a 3-dimensional space
         *
         * @see    #getPoint3d
         */
        public void setPoint3d(javax.vecmath.Point3d point3d) {
                this.point3d = point3d;
        }
        /**
         * Sets a point specifying the location of this
         * atom in a Crystal unit cell.
         *
         * @param  point3d  A point in a 3d fractional unit cell space
         *
         * @see    #getFractionalPoint3d
         * @see    org.openscience.cdk.Crystal
         */
        public void setFractionalPoint3d(javax.vecmath.Point3d point3d) {
                this.fractionalPoint3d = point3d;
        }
        /**
         * Sets the stereo parity for this atom.
         *
         * @param  stereoParity  The stereo parity for this atom
         *
         * @see    org.openscience.cdk.CDKConstants for predefined values.
         * @see    #getStereoParity
         */
        public void setStereoParity(int stereoParity) {
                this.stereoParity = stereoParity;
        }

        /**
         * Returns a point specifying the location of this
         * atom in a 2D space.
         *
         * @return    A point in a 2D plane. Null if unset.
         *
         * @see       #setPoint2d
         */
        public javax.vecmath.Point2d getPoint2d() {
                return this.point2d;
        }
        /**
         * Returns a point specifying the location of this
         * atom in a 3D space.
         *
         * @return    A point in 3-dimensional space. Null if unset.
         *
         * @see       #setPoint3d
         */
        public javax.vecmath.Point3d getPoint3d() {
                return this.point3d;
        }

        /**
         * Returns a point specifying the location of this
         * atom in a Crystal unit cell.
         *
         * @return    A point in 3d fractional unit cell space. Null if unset.
         *
         * @see       #setPoint3d
         * @see       org.openscience.cdk.CDKConstants for predefined values.
         */
        public javax.vecmath.Point3d getFractionalPoint3d() {
                return this.fractionalPoint3d;
        }

        /**
         * Returns the x coordinate for of the 2D location of this atom.
         * You should know your context here. There is no guarantee that point2d and point3d
         * contain consistent information. Both are handled independently.
         *
         * @return the x coordinate for of the 2D location of this atom
         *
         * @see    #setX2d
         */
        public double getX2d() {
            if (point2d == null) {
                return 0.0;
            } else {
                return point2d.x;
            }
        }


        /**
         * Returns the y coordinate for of the 2D location of this atom.
         * You should know your context here. There is no guarantee that point2d and point3d
         * contain consistent information. Both are handled independently.
         *
         * @return the y coordinate for of the 2D location of this atom
         *
         * @see    #setY2d
         */
        public double getY2d() {
            if (point2d == null) {
                return 0.0;
            } else {
                return point2d.y;
            }
        }


        /**
         * Returns the x coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2d and point3d
         * contain consistent information. Both are handled independently.
         *
         * @return the x coordinate for of the 3D location of this atom
         *
         * @see    #setX3d
         */
        public double getX3d() {
            if (point3d == null) {
                return 0.0;
            } else {
                return point3d.x;
            }
        }


        /**
         * Returns the y coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2d and point3d
         * contain consistent information. Both are handled independently.
         *
         * @return the y coordinate for of the 3D location of this atom
         *
         * @see    #setY3d
         */
        public double getY3d() {
            if (point3d == null) {
                return 0.0;
            } else {
                return point3d.y;
            }
        }

        /**
         * Returns the z coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2d and point3d
         * contain consistent information. Both are handled independently.
         *
         * @return the z coordinate for of the 3D location of this atom
         *
         * @see    #setZ3d
         */
        public double getZ3d() {
            if (point3d == null) {
                return 0.0;
            } else {
                return point3d.z;
            }
        }

        public void setFractX3d(double x) {
            if (fractionalPoint3d == null) {
                fractionalPoint3d = new Point3d();
            }
            fractionalPoint3d.x = x;
        }
        public void setFractY3d(double y) {
            if (fractionalPoint3d == null) {
                fractionalPoint3d = new Point3d();
            }
            fractionalPoint3d.y = y;
        }
        public void setFractZ3d(double z) {
            if (fractionalPoint3d == null) {
                fractionalPoint3d = new Point3d();
            }
            fractionalPoint3d.z = z;
        }
        public double getFractX3d() {
            if (fractionalPoint3d == null) {
                return 0.0;
            } else {
                return fractionalPoint3d.x;
            }
        }
        public double getFractY3d() {
            if (fractionalPoint3d == null) {
                return 0.0;
            } else {
                return fractionalPoint3d.y;
            }
        }
        public double getFractZ3d() {
            if (fractionalPoint3d == null) {
                return 0.0;
            } else {
                return fractionalPoint3d.z;
            }
        }

        /**
         * Sets the x coordinate for of the 2D location of this atom.
         * You should know your context here. There is no guarantee that point2d and point3d
         * contain consistent information. Both are handled independently.
         *
         * @param   x  the new x coordinate for of the 2D location of this atom
         *
         * @see     #getX2d
         */
        public void setX2d(double x) {
            if (point2d == null) {
                point2d = new javax.vecmath.Point2d();
            }
            point2d.x = x;
        }


        /**
         * Sets the y coordinate for of the 2D location of this atom.
         * You should know your context here. There is no guarantee that point2d and point3d
         * contain consistent information. Both are handled independently.
         *
         * @param   y  the new y coordinate for of the 2D location of this atom
         *
         * @see     #getY2d
         */
        public void setY2d(double y) {
            if (point2d == null) {
                point2d = new javax.vecmath.Point2d();
            }
            point2d.y = y;
        }


        /**
         * Sets the x coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2d and point3d
         * contain consistent information. Both are handled independently.
         *
         * @param   x  the new x coordinate for of the 3D location of this atom
         *
         * @see     #getX3d
         */
        public void setX3d(double x) {
            if (point3d == null) {
                point3d = new javax.vecmath.Point3d();
            }
            point3d.x = x;
        }


        /**
         * Sets the y coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2d and point3d
         * contain consistent information. Both are handled independently.
         *
         * @param   y  the new y coordinate for of the 3D location of this atom
         *
         * @see     #getY3d
         */
        public void setY3d(double y) {
            if (point3d == null) {
                point3d = new javax.vecmath.Point3d();
            }
            point3d.y = y;
        }


        /**
         * Sets the z coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2d and point3d
         * contain consistent information. Both are handled independently.
         *
         * @param   z  the new z coordinate for of the 3d location of this atom
         *
         * @see     #getZ3d
         */
        public void setZ3d(double z) {
            if (point3d == null) {
                point3d = new javax.vecmath.Point3d();
            }
            point3d.z = z;
        }


        /**
         *  Returns the stereo parity of this atom. It uses the predefined values
         *  found in CDKConstants.
         *
         * @return    The stereo parity for this atom
         *
         * @see       org.openscience.cdk.CDKConstants
         * @see       #setStereoParity
         */
        public int getStereoParity() {
            return this.stereoParity;
        }

        /**
         * Compares a atom with this atom.
         *
         * @param     object of type Atom
         * @return    return true, if the atoms are equal
         */
        public boolean compare(Object object)
        {
          if (!(object instanceof Atom)) {
              return false;
          }
          if (!super.compare(object)) {
              return false;
          }
          Atom atom = (Atom) object;
          if (((point2d==atom.point2d) || ((point2d!=null) && (point2d.equals(atom.point2d)))) &&
              ((point3d==atom.point3d) || ((point3d!=null) && (point3d.equals(atom.point3d)))) &&
              (hydrogenCount==atom.hydrogenCount) &&
              (stereoParity==atom.stereoParity) &&
              (charge==atom.charge)) {
              return true;
          }
          return false;
        }

        /**
         * Returns a one line string representation of this Atom.
         * Methods is conform RFC #9.
         *
         * @return  The string representation of this Atom
         */
        public String toString() {
                StringBuffer s = new StringBuffer();
                s.append("Atom(");
                s.append(this.hashCode() + ", ");
                s.append(getSymbol() + ", ");
                s.append("H:" + getHydrogenCount() + ", ");
                s.append("SP:" + getStereoParity() + ", ");
                s.append("2D:[" + getPoint2d() + "], ");
                s.append("3D:[" + getPoint3d() + "], ");
                s.append("Fract3D:[" + getFractionalPoint3d() + "], ");
                s.append("C:" + getCharge() + ", ");
                s.append("FC:" + getFormalCharge());
                s.append(", " + super.toString());
                s.append(")");
                return s.toString();
        }
        

        /**
         * Clones this atom object.
         *
         * @return  The cloned object   
         */
        public Object clone()
        {
                Object o = null;
                try
                {
                        o = super.clone();
                }
                catch (Exception e)
                {
                        e.printStackTrace(System.err);
                }
                return o;
        }


        
}





