/* Atom.java
 * 
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2000  The CompChem project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
	 *  A 2D point specifying the location of this atom in a 3D coordinate 
	 *  space 
	 */
	protected javax.vecmath.Point3d point3D;
	/**
	 *  The number of implicitly bound hydrogen atoms for this atom 
	 */
	protected int hydrogenCount;
	/**
	 *  The number of atoms directly bonded to this atom 
	 */
	protected int degree;
	/**
	 *  A stereo parity descriptor for the stereochemistry of this atom 
	 */
	protected int stereoParity;
	


	/**
	 * Constructs an Atom from an Element
	 *
	 * @param   element  The Element the Atom is constructed of
	 */
	public Atom(Element element)
	{
		this.element = element;
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
	 *  
	 * Returns the degree of this atom, i.e. the number of other atoms
	 * directly bonded to it.
	 *
	 * @return    The degree of this atom 
	 */
	public int getDegree() {
		return this.degree;
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
	 *  
	 * Returns a point specifying the location of this
	 * atom in a 2D space
	 *
	 * @return    A point in a 2D plane 
	 */
	public javax.vecmath.Point2d getPoint2D() {
		return this.point2D;
	}
	/**
	 *  
	 * Returns a point specifying the location of this
	 * atom in a 3D space
	 *
	 * @return    A point in 3-dimensional space 
	 */
	public javax.vecmath.Point3d getPoint3D() {
		return this.point3D;
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
	 *  
	 * Internal method to set the degree of this atom, i.e. the number of other atoms
	 * directly bonded to it.
	 *
	 * @param  degree  The degree of this atom 
	 */
	protected void setDegree(int degree) {
		this.degree = degree;
	}
	/**
	 * increments the degree of this atom, i.e. the number of other atoms
	 * directly bonded to it.
	 */
	protected void incrementDegree()
	{
		this.degree++;
	}
	/**
	 * decrements the degree of this atom, i.e. the number of other atoms
	 * directly bonded to it.
	 */
	protected void decrementDegree()
	{
		this.degree--;
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
		s.append("Degree: " + getDegree() + "\n");
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
		Atom o = null;
		try
		{
			o = (Atom)super.clone();
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
		}
		o.point2D = this.point2D;
		o.point3D = this.point3D;
		return o;
	}


	
}

