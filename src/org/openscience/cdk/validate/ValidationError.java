/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */
package org.openscience.cdk.validate;

import org.openscience.cdk.ChemObject;

/**
 * Error found during sematical validation of a ChemObject.
 *
 * @author   Egon Willighagen
 * @created  2003-03-28
 *
 * @see      org.openscience.cdk.ChemObject
 * @keyword atom, chemical validation
 */ 
public class ValidationError {
    
    /** ChemObject which has the error. */
    private ChemObject object;
    /** String representation of the found error. */
    private String error;

    public ValidationError(ChemObject object, String error) {
        this.object = object;
        this.error = error;
    }
    
    public ChemObject getChemObject() {
        return this.object;
    }
    
    public String getError() {
        return this.error;
    }
}
