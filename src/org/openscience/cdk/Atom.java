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
 * @cdkPackage core
 *
 * @author     steinbeck
 * @created    2000-10-02
 * @keyword    atom
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
        protected javax.vecmath.Point2d point2D;
        /**
         *  A 3 point specifying the location of this atom in a 3D coordinate 
         *  space.
         */
        protected javax.vecmath.Point3d point3D;
        /**
         *  A 3 point specifying the location of this atom in a crystal unit cell.
         */
        protected javax.vecmath.Point3d fractionalPoint3D;
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
         * Constructs an Atom from a String containing an element symbol.
         *
         * @param   elementSymbol  The String describing the element for the Atom
         */
        public Atom(String elementSymbol)
        {
                super(elementSymbol);
                this.fractionalPoint3D = null;
                this.point3D = null;
                this.point2D = null;
        }

        /**
         * Constructs an Atom from an Element and a Point3D.
         *
         * @param   elementSymbol   The symbol of the atom
         * @param   point3D         The 3D coordinates of the atom
         */
        public Atom(String elementSymbol, javax.vecmath.Point3d point3D)
        {
                this(elementSymbol);
                this.point3D = point3D;
        }

        /**
         * Constructs an Atom from an Element and a Point2D.
         *
         * @param   elementSymbol   The Element
         * @param   point2D         The Point
         */
        public Atom(String elementSymbol, javax.vecmath.Point2d point2D)
        {
                this(elementSymbol);
                this.point2D = point2D;
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
         * @param  point2D  A point in a 2D plane
         *
         * @see    #getPoint2D
         */
        public void setPoint2D(javax.vecmath.Point2d point2D) {
                this.point2D = point2D;
        }
        /**
         *
         * Sets a point specifying the location of this
         * atom in 3D space.
         *
         * @param  point3D  A point in a 3-dimensional space
         *
         * @see    #getPoint3D
         */
        public void setPoint3D(javax.vecmath.Point3d point3D) {
                this.point3D = point3D;
        }
        /**
         * Sets a point specifying the location of this
         * atom in a Crystal unit cell.
         *
         * @param  point3D  A point in a 3d fractional unit cell space
         *
         * @see    #getFractionalPoint3D
         * @see    org.openscience.cdk.Crystal
         */
        public void setFractionalPoint3D(javax.vecmath.Point3d point3D) {
                this.fractionalPoint3D = point3D;
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
         * @see       #setPoint2D
         */
        public javax.vecmath.Point2d getPoint2D() {
                return this.point2D;
        }
        /**
         * Returns a point specifying the location of this
         * atom in a 3D space.
         *
         * @return    A point in 3-dimensional space. Null if unset.
         *
         * @see       #setPoint3D
         */
        public javax.vecmath.Point3d getPoint3D() {
                return this.point3D;
        }

        /**
         * Returns a point specifying the location of this
         * atom in a Crystal unit cell.
         *
         * @return    A point in 3d fractional unit cell space. Null if unset.
         *
         * @see       #setPoint3D
         * @see       org.openscience.cdk.CDKConstants for predefined values.
         */
        public javax.vecmath.Point3d getFractionalPoint3D() {
                return this.fractionalPoint3D;
        }

        /**
         * Returns the x coordinate for of the 2D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D
         * contain consistent information. Both are handled independently.
         *
         * @return the x coordinate for of the 2D location of this atom
         *
         * @see    #setX2D
         */
        public double getX2D() {
            if (point2D == null) {
                return 0.0;
            } else {
                return point2D.x;
            }
        }


        /**
         * Returns the y coordinate for of the 2D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D
         * contain consistent information. Both are handled independently.
         *
         * @return the y coordinate for of the 2D location of this atom
         *
         * @see    #setY2D
         */
        public double getY2D() {
            if (point2D == null) {
                return 0.0;
            } else {
                return point2D.y;
            }
        }


        /**
         * Returns the x coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D
         * contain consistent information. Both are handled independently.
         *
         * @return the x coordinate for of the 3D location of this atom
         *
         * @see    #setX3D
         */
        public double getX3D() {
            if (point3D == null) {
                return 0.0;
            } else {
                return point3D.x;
            }
        }


        /**
         * Returns the y coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D
         * contain consistent information. Both are handled independently.
         *
         * @return the y coordinate for of the 3D location of this atom
         *
         * @see    #setY3D
         */
        public double getY3D() {
            if (point3D == null) {
                return 0.0;
            } else {
                return point3D.y;
            }
        }

        /**
         * Returns the z coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D
         * contain consistent information. Both are handled independently.
         *
         * @return the z coordinate for of the 3D location of this atom
         *
         * @see    #setZ3D
         */
        public double getZ3D() {
            if (point3D == null) {
                return 0.0;
            } else {
                return point3D.z;
            }
        }

        public void setFractX3D(double x) {
            if (fractionalPoint3D == null) {
                fractionalPoint3D = new Point3d();
            }
            fractionalPoint3D.x = x;
        }
        public void setFractY3D(double y) {
            if (fractionalPoint3D == null) {
                fractionalPoint3D = new Point3d();
            }
            fractionalPoint3D.y = y;
        }
        public void setFractZ3D(double z) {
            if (fractionalPoint3D == null) {
                fractionalPoint3D = new Point3d();
            }
            fractionalPoint3D.z = z;
        }
        public double getFractX3D() {
            if (fractionalPoint3D == null) {
                return 0.0;
            } else {
                return fractionalPoint3D.x;
            }
        }
        public double getFractY3D() {
            if (fractionalPoint3D == null) {
                return 0.0;
            } else {
                return fractionalPoint3D.y;
            }
        }
        public double getFractZ3D() {
            if (fractionalPoint3D == null) {
                return 0.0;
            } else {
                return fractionalPoint3D.z;
            }
        }

        /**
         * Sets the x coordinate for of the 2D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D
         * contain consistent information. Both are handled independently.
         *
         * @param   x  the new x coordinate for of the 2D location of this atom
         *
         * @see     #getX2D
         */
        public void setX2D(double x) {
            if (point2D == null) {
                point2D = new javax.vecmath.Point2d();
            }
            point2D.x = x;
        }


        /**
         * Sets the y coordinate for of the 2D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D
         * contain consistent information. Both are handled independently.
         *
         * @param   y  the new y coordinate for of the 2D location of this atom
         *
         * @see     #getY2D
         */
        public void setY2D(double y) {
            if (point2D == null) {
                point2D = new javax.vecmath.Point2d();
            }
            point2D.y = y;
        }


        /**
         * Sets the x coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D
         * contain consistent information. Both are handled independently.
         *
         * @param   x  the new x coordinate for of the 3D location of this atom
         *
         * @see     #getX3D
         */
        public void setX3D(double x) {
            if (point3D == null) {
                point3D = new javax.vecmath.Point3d();
            }
            point3D.x = x;
        }


        /**
         * Sets the y coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D
         * contain consistent information. Both are handled independently.
         *
         * @param   y  the new y coordinate for of the 3D location of this atom
         *
         * @see     #getY3D
         */
        public void setY3D(double y) {
            if (point3D == null) {
                point3D = new javax.vecmath.Point3d();
            }
            point3D.y = y;
        }


        /**
         * Sets the z coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D
         * contain consistent information. Both are handled independently.
         *
         * @param   z  the new z coordinate for of the 3D location of this atom
         *
         * @see     #getZ3D
         */
        public void setZ3D(double z) {
            if (point3D == null) {
                point3D = new javax.vecmath.Point3d();
            }
            point3D.z = z;
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
          if (((point2D==atom.point2D) || ((point2D!=null) && (point2D.equals(atom.point2D)))) &&
              ((point3D==atom.point3D) || ((point3D!=null) && (point3D.equals(atom.point3D)))) &&
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
                s.append("2D:[" + getPoint2D() + "], ");
                s.append("3D:[" + getPoint3D() + "], ");
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





