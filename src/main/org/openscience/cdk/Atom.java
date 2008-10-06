/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2000-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk;

import java.io.Serializable;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IElement;

/**
 * Represents the idea of an chemical atom.
 *
 * <p>An Atom class is instantiated with at least the atom symbol:
 * <pre>
 *   Atom a = new Atom("C");
 * </pre>
 *
 * <p>Once instantiated all field not filled by passing parameters
 * to the constructor are null. Atoms can be configured by using
 * the IsotopeFactory.configure() method:
 * <pre>
 *   IsotopeFactory if = IsotopeFactory.getInstance(a.getBuilder());
 *   if.configure(a);
 * </pre>
 *
 * <p>More examples about using this class can be found in the
 * Junit test for this class.
 *
 * @cdk.module data
 * @cdk.svnrev  $Revision$
 *
 * @author     steinbeck
 * @cdk.created    2000-10-02
 * @cdk.keyword    atom
 *
 * @see  org.openscience.cdk.config.IsotopeFactory#getInstance(org.openscience.cdk.interfaces.IChemObjectBuilder)
 */
public class Atom extends AtomType implements IAtom, Serializable, Cloneable  {
    
	/* Let's keep this exact specification
	 * of what kind of point2d we're talking of here,
	 * since there are so many around in the java standard api */

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -3137373012494608794L;
	
		/**
         *  A 2D point specifying the location of this atom in a 2D coordinate 
         *  space.
         */
        protected javax.vecmath.Point2d point2d = (Point2d) CDKConstants.UNSET;
        /**
         *  A 3 point specifying the location of this atom in a 3D coordinate 
         *  space.
         */
        protected javax.vecmath.Point3d point3d = (Point3d) CDKConstants.UNSET;
        /**
         *  A 3 point specifying the location of this atom in a crystal unit cell.
         */
        protected javax.vecmath.Point3d fractionalPoint3d = (Point3d) CDKConstants.UNSET;
        /**
         *  The number of implicitly bound hydrogen atoms for this atom.
         */
        protected Integer hydrogenCount = (Integer) CDKConstants.UNSET;
        /**
         *  A stereo parity descriptor for the stereochemistry of this atom.
         */
        protected Integer stereoParity = (Integer) CDKConstants.UNSET;
        /**
         *  The partial charge of the atom.
         *
         * The default value is {@link CDKConstants.UNSET} and serves to provide a check whether the charge has been
         * set or not
         */
        protected Double charge = (Double) CDKConstants.UNSET;
        
        /**
         * Constructs an completely unset Atom.
         */
        public Atom() {
            super((String)null);
        }

    /**
     * Constructs an Atom from a String containing an element symbol.
     *
     * @param elementSymbol The String describing the element for the Atom
     */
    public Atom(String elementSymbol) {
        super(elementSymbol);      
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
    	 * Constructs an isotope by copying the symbol, atomic number,
    	 * flags, identifier, exact mass, natural abundance, mass 
    	 * number, maximum bond order, bond order sum, van der Waals
    	 * and covalent radii, formal charge, hybridization, electron
    	 * valency, formal neighbour count and atom type name from the 
    	 * given IAtomType. It does not copy the listeners and
    	 * properties. If the element is an instanceof
    	 * IAtom, then the 2D, 3D and fractional coordinates, partial
    	 * atomic charge, hydrogen count and stereo parity are copied
    	 * too.
    	 * 
    	 * @param element IAtomType to copy information from
    	 */
    	public Atom(IElement element) {
    		super(element);
    		if (element instanceof IAtom) {
    			if (((IAtom)element).getPoint2d() != null) {
    				this.point2d = new Point2d(((IAtom)element).getPoint2d());
                } else {
                    this.point2d = null;
                }
                if (((IAtom)element).getPoint3d() != null) {
                    this.point3d = new Point3d(((IAtom)element).getPoint3d());
                } else {
                    this.point3d = null;
                }
                if (((IAtom)element).getFractionalPoint3d() != null) {
                    this.fractionalPoint3d = new Point3d(((IAtom)element).getFractionalPoint3d());
                } else {
                    this.fractionalPoint3d = null;
                }
    			this.hydrogenCount = ((IAtom)element).getHydrogenCount();
    			this.charge = ((IAtom)element).getCharge();
    			this.stereoParity = ((IAtom)element).getStereoParity();
    		}
    	}

    	/**
         *  Sets the partial charge of this atom.
         *
         * @param  charge  The partial charge
         *
         * @see    #getCharge
         */
        public void setCharge(Double charge) {
        	this.charge = charge;
        	notifyChanged();
        }

        /**
         *  Returns the partial charge of this atom.
         *
         * If the charge has not been set the return value is Double.NaN
         *
         * @return the charge of this atom
         *
         * @see    #setCharge
         */
        public Double getCharge() {
               return this.charge;
        }

        /**
         *  Sets the number of implicit hydrogen count of this atom.
         *
         * @param  hydrogenCount  The number of hydrogen atoms bonded to this atom.
         *
         * @see    #getHydrogenCount
         */
        public void setHydrogenCount(Integer hydrogenCount) {
        	this.hydrogenCount = hydrogenCount;
        	notifyChanged();
        }

        /**
         *  Returns the hydrogen count of this atom.
         *
         * @return    The hydrogen count of this atom.
         *
         * @see       #setHydrogenCount
         */
        public Integer getHydrogenCount() {
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
		notifyChanged();
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
		notifyChanged();
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
		notifyChanged();
        }
        /**
         * Sets the stereo parity for this atom.
         *
         * @param  stereoParity  The stereo parity for this atom
         *
         * @see    org.openscience.cdk.CDKConstants for predefined values.
         * @see    #getStereoParity
         */
        public void setStereoParity(Integer stereoParity) {
        	this.stereoParity = stereoParity;
        	notifyChanged();
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
         * @see       #setFractionalPoint3d
         * @see       org.openscience.cdk.CDKConstants for predefined values.
         */
        public javax.vecmath.Point3d getFractionalPoint3d() {
                return this.fractionalPoint3d;
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
        public Integer getStereoParity() {
            return this.stereoParity;
        }

        /**
         * Compares a atom with this atom.
         *
         * @param     object of type Atom
         * @return    true, if the atoms are equal
         */
        public boolean compare(Object object)
        {
          if (!(object instanceof IAtom)) {
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
                StringBuffer stringContent = new StringBuffer(64);
                stringContent.append("Atom(").append(hashCode());
                if (getSymbol() != null) {
                	stringContent.append(", S:").append(getSymbol());
                }
                if (getHydrogenCount() != null) {
                	stringContent.append(", H:").append(getHydrogenCount());
                }
                if (getStereoParity() != null) {
                	stringContent.append(", SP:").append(getStereoParity());
                }
                if (getPoint2d() != null) {
                	stringContent.append(", 2D:[").append(getPoint2d()).append(']');
                }
                if (getPoint3d() != null) {
                	stringContent.append(", 3D:[").append(getPoint3d()).append(']');
                }
                if (getFractionalPoint3d() != null) {
                	stringContent.append(", F3D:[").append(getFractionalPoint3d());
                }
                if (getCharge() != null) {
                	stringContent.append(", C:").append(getCharge());
                }
                stringContent.append(", ").append(super.toString());
                stringContent.append(')');
                return stringContent.toString();
        }
        

        /**
         * Clones this atom object and its content.
         *
         * @return  The cloned object   
         */
        public Object clone() throws CloneNotSupportedException {
            Object clone = super.clone();
            if (point2d != null) {
                ((Atom)clone).setPoint2d(new Point2d(point2d.x, point2d.y));
            }
            if (point3d != null) {
                ((Atom)clone).setPoint3d(new Point3d(point3d.x, point3d.y, point3d.z));
            }
            if (fractionalPoint3d != null) {
                ((Atom)clone).setFractionalPoint3d(new Point3d(fractionalPoint3d.x, fractionalPoint3d.y, fractionalPoint3d.z));
            }
            return clone;
        }
        
}





