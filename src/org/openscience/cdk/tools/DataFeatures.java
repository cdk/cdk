/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-05-09 21:32:32 +0200 (Tue, 09 May 2006) $  
 * $Revision: 6204 $
 *
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.tools;

/**
 * Class with constants for possible data features defined in the
 * a Data Feature Ontology. Actual integers are random
 * and should <b>not</b> be used directly.
 * 
 * @author Egon Willighagen <ewilligh@uni-koeln.de>
 * @cdk.module core
 **/
public class DataFeatures {

    public final static int NONE = 0;
    
    // COORDINATE SYSTEMS
    public final static int HAS_2D_COORDINATES = 1;
    public final static int HAS_3D_COORDINATES = 2;
    
    // GRAPH FEATURES
    public final static int HAS_GRAPH_REPRESENTATION = 4;
    public final static int HAS_DIETZ_REPRESENTATION = 8;
    
    // MODEL FEATURES
    public final static int HAS_CRYSTALS = 16;
    public final static int HAS_REACTIONS = 32;
	
}

