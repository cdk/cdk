/*
 * $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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


/**
 * Class representing a molecular crystal. 
 * The crystal is described with molecules in fractional
 * coordinates and three cell axes: a,b and c.
 *
 * @keyword crystal
 */
public class MolecularCrystal extends SetOfMolecules {

    /** x,y,z components of a axis */
    private double ax,ay,az;
    /** x,y,z components of b axis */
    private double bx,by,bz;
    /** x,y,z components of c axis */
    private double cx,cy,cz;

    public MolecularCrystal() {
        ax = 0.0; ay = 0.0; az = 0.0;
        bx = 0.0; by = 0.0; bz = 0.0;
        cx = 0.0; cy = 0.0; cz = 0.0;
    }
}
