/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
 * 
 *  Contact: cdk-devel@lists.sourceforge.net
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


/**
 *
 * A BioPolymer is a subclass of a Polymer which is supposed to store
 * additional informations about the Polymer which are connected to BioPolymers.
 *
 * @cdk.module data
 *
 * @author     Edgar Luttmann <edgar@uni-paderborn.de>
 * @cdk.created    2001-08-06 
 *
 * @cdk.keyword    polymer
 * @cdk.keyword    biopolymer
 */
public class BioPolymer extends Polymer implements java.io.Serializable, Cloneable
{

	/**
	 * Contructs a new BioPolymer to store the Monomers.
	 */
    public BioPolymer() {
        super();
    }

    /**
     * Clones this AtomParity object.
     *
     * @return  The cloned object   
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
