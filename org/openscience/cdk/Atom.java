/* 
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 *  */
package org.openscience.cdk;

import javax.vecmath.*;
/**
 *  Represents the idea of an chemical atom 
 *
 * @author     steinbeck 
 * @created    October 2, 2000 
 */
public class Atom extends ChemObject implements Cloneable {
        /**
         *  The element type of this atom 
         */
        protected Element element;
        /* Let's keep this exact specification
         * of what kind of point2d we're talking of here,
         * sinces there are so many around in the java standard api */
        /**
         *  A 2D point specifying the location of this atom in a 2D coordinate 
         *  space 
         */
        protected javax.vecmath.Point2d point2D;
        /**
         *  A 3 point specifying the location of this atom in a 3D coordinate 
         *  space 
         */
        protected javax.vecmath.Point3d point3D;
        /**
         *  The number of implicitly bound hydrogen atoms for this atom 
         */
        protected int hydrogenCount;
        /**
         *  A stereo parity descriptor for the stereochemistry of this atom 
         */
        protected int stereoParity;
        /**
         *  The partial charge of the atom 
         */
        protected double charge;
        

        /**
         * Constructs an Atom from a String containing an element symbol
         *
         * @param   element  The String describing the element for the Atom 
         */
        public Atom(String elementString)
        {
                this(new Element(elementString));
        }

        /**
         * Constructs an Atom from an Element
         *
         * @param   element  The Element the Atom is constructed of
         */
        public Atom(Element element)
        {
                this.element = element;
                point2D = new javax.vecmath.Point2d();
                point3D = new javax.vecmath.Point3d();
        }


        /**
         * Constructs an Atom from an Element and a Point3D
         *
         * @param   element   The Element
         * @param   point3D   The Point
         */
        public Atom(Element element, javax.vecmath.Point3d point3D)
        {
                this(element);
                this.point3D = point3D;
        }

        /**
         * Constructs an Atom from an Element and a Point2D
         *
         * @param   element   The Element
         * @param   point2D   The Point
         */
        public Atom(Element element, javax.vecmath.Point2d point2D)
        {
                this(element);
                this.point2D = point2D;
        }

        /**
         *  Sets the partial charge of this atom
         *
         * @param  element  The partial charge
         */
        public void setCharge(double charge) {
               this.charge = charge;
        }

        /**
         *  Returns the partial charge of this atom
         */
        public double getCharge() {
               return this.charge;
        }

        /**
         *  Sets the element type of this atom 
         *
         * @param  element  The element type to be assigned to this atom. 
         */
        public void setElement(Element element) {
                this.element = element;
        }
        /**
         *  Sets the hydrogen count of this atom. 
         *
         * @param  hydrogenCount  The number of hydrogen atoms bonded to this atom. 
         */
        public void setHydrogenCount(int hydrogenCount) {
                this.hydrogenCount = hydrogenCount;
        }
        /**
         *  
         * Sets a point specifying the location of this
         * atom in a 2D space
         *
         * @param  point2D  A point in a 2D plane 
         */
        public void setPoint2D(javax.vecmath.Point2d point2D) {
                this.point2D = point2D;
        }
        /**
         *  
         * Sets a point specifying the location of this
         * atom in 3D space
         *
         * @param  point3D  A point in a 3-dimensional space 
         */
        public void setPoint3D(javax.vecmath.Point3d point3D) {
                this.point3D = point3D;
        }
        /**
         *  Sets the stereo parity for this atom 
         *
         * @param  stereoParity  The stereo parity for this atom 
         * @see                  org.openscience.cdk.CDKConstants 
         *      org.openscience.cdk.CDKConstants for predefined values. 
         */
        public void setStereoParity(int stereoParity) {
                this.stereoParity = stereoParity;
        }
        /**
         *  Returns an Element representing the element type of this Atom. 
         *
         * @return    An Element representing the element type of this Atom. 
         */
        public Element getElement() {
                return this.element;
        }
        /**
         *  Returns the hydrogen count of this atom 
         *
         * @return    The hydrogen count of this atom. 
         */
        public int getHydrogenCount() {
                return this.hydrogenCount;
        }
        /**
         * Returns a point specifying the location of this
         * atom in a 2D space
         *
         * @return    A point in a 2D plane 
         */
        public javax.vecmath.Point2d getPoint2D() {
                return this.point2D;
        }
        /**
         * Returns a point specifying the location of this
         * atom in a 3D space
         *
         * @return    A point in 3-dimensional space 
         */
        public javax.vecmath.Point3d getPoint3D() {
                return this.point3D;
        }
        

        /**
         * Returns the x coordinate for of the 2D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D 
         * contain consistent information. Both are handled independently.
         *
         * @return the x coordinate for of the 2D location of this atom     
         */
        public double getX2D()
        {
                return point2D.x;
        }
        

        /**
         * Returns the y coordinate for of the 2D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D 
         * contain consistent information. Both are handled independently.
         *
         * @return the y coordinate for of the 2D location of this atom     
         */
        public double getY2D()
        {
              return point2D.y;
        }
        

        /**
         * Returns the x coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D 
         * contain consistent information. Both are handled independently.
         *
         * @return the x coordinate for of the 3D location of this atom     
         */
        public double getX3D()
        {
                return point3D.x;
        }
        

        /**
         * Returns the y coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D 
         * contain consistent information. Both are handled independently.
         *
         * @return the y coordinate for of the 3D location of this atom     
         */
        public double getY3D()
        {
                return point3D.y;
        }
        
        /**
         * Returns the z coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D 
         * contain consistent information. Both are handled independently.
         *
         * @return the z coordinate for of the 3D location of this atom     
         */

        public double getZ3D()
        {
                return point3D.z;
        }
        

        /**
         * Sets the x coordinate for of the 2D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D 
         * contain consistent information. Both are handled independently.
     *
         * @param   x  the new x coordinate for of the 2D location of this atom
         */
        public void setX2D(double x)
        {
                point2D.x = x;
        }
        

        /**
         * Sets the y coordinate for of the 2D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D 
         * contain consistent information. Both are handled independently.
         *
         * @param   y  the new y coordinate for of the 2D location of this atom
         */
        public void setY2D(double y)
        {
                point2D.y = y;
        }


        /**
         * Sets the x coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D 
         * contain consistent information. Both are handled independently.
         *
         * @param   x  the new x coordinate for of the 3D location of this atom
         */
        public void setX3D(double x)
        {
                point3D.x = x;
        }


        /**
         * Sets the y coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D 
         * contain consistent information. Both are handled independently.
         *
         * @param   y  the new y coordinate for of the 3D location of this atom
         */
        public void setY3D(double y)
        {
                point3D.y = y;
        }


        /**
         * Sets the z coordinate for of the 3D location of this atom.
         * You should know your context here. There is no guarantee that point2D and point3D 
         * contain consistent information. Both are handled independently.
         *
         * @param   z  the new z coordinate for of the 3D location of this atom
         */
        public void setZ3D(double z)
        {
                point3D.z = z;
        }
        

        /**
         *  Returns the stereo parity of this atom 
         *
         * @return    The stereo parity for this atom 
         * @see       org.openscience.cdk.CDKConstants 
         *      org.openscience.cdk.CDKConstants for predefined values. 
         */
        public int getStereoParity() {
                return this.stereoParity;
        }
        /**
         * Returns a string representation of this Atom.
         *
         * @return  The string representation of this Atom   
         */
        public String toString()
        {
                StringBuffer s = new StringBuffer();
                s.append("Atom " + getElement().getSymbol() + "\n");
                s.append("Hydrogen count: " + getHydrogenCount() + "\n");
                s.append("Stereo Parity: " + getStereoParity() + "\n");
                s.append("2D coordinates: " + getPoint2D() + "\n");
                s.append("3D coordinates: " + getPoint3D() + "\n");
                                
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





