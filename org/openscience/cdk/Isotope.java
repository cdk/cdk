/* Isotope.java
 *
 * $RCSfile$    $Author$    $Date$    $Revision$
 * 
 * Copyright (C) 1997-2001  The JChemPaint project
 * 
 * Contact: steinbeck@ice.mpg.de
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
 * 
 */

package org.openscience.cdk;


/** Isotope.java
 *	Used to store data of a particular isotope
 */
 
public class Isotope extends Element implements Cloneable{
	
	public double exactMass = (double)-1;
	public double naturalAbundance = (double)-1;
	
    public Isotope(int atomicNumber, String elementSymbol, double atomicMass, double exactMass, double nA)
    {
	    super(elementSymbol, atomicNumber, atomicMass);
        this.exactMass = exactMass; 
        this. naturalAbundance = nA;
    }
    
        public Isotope(int atomicNumber, String elementSymbol, double exactMass, double nA)
    {
	this(atomicNumber, elementSymbol, (double)Math.round(exactMass), exactMass, nA);
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

	public String toString(){
		String s = "[" + atomicMass + "]";
		s += symbol + ": exact mass = " + exactMass;
		s += "; relative natural abundance = " + naturalAbundance;
		return s;	
	}
	
}
