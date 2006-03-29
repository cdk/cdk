/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 * 
 */

package org.openscience.cdk.atomtype;

/**
 * An interface providing predefined values for a number of
 * constants used for classification of ring systems in the
 * atom typing process (Zero is set to undefined). Classes using these constants should
 * <b>not</b> implement this interface, but use it like:
 * <pre>
 *   int IS_IN_HETRING = CDKRingConstants.IS_IN_HETRING;
 *   or as Flag:
 *   atom.setFlag(CDKConstants.IS_IN_HETRING, true);
 * </pre>
 *
 * <p>The lazyCreation patch has been applied to this class.
 *
 *	Hetero ring systems must get an even number, carbon ring systems
 *	an uneven number.
 *
 * @cdk.module data
 *
 * @cdk.keyword bond order
 * @cdk.keyword stereochemistry
 */
public class CDKChemicalRingConstants {
	
	/** Is in hetereo ring system. */
    public final static int ISNOT_IN_RING = -1;
	
    /** Is in hetereo ring system. */
    public final static int IS_IN_HETRING = 0;
       
    /** Is in carbon ring system. */
    public final static int IS_IN_CARBRING = 1;
    
    /** Is in ring system. */
    public final static int IS_IN_RING = 3;
       
    /** Is in pyrole */
    public final static int PYROLE = 4;

    /** Is in furan */
    public final static int FURAN = 6;

    /** Is in thiophen */
    public final static int THIOPHEN = 8;
    
    /** Is in pyridin */
    public final static int PYRIDIN  = 10;
    
    /** Is in pyrimidin */
    public final static int PYRIMIDIN = 12;
                
    /** Is in brenzol */
    public final static int BENZOL  = 5;
 
}


