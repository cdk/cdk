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

import org.openscience.cdk.*;
import org.openscience.cdk.tools.IsotopeFactory;
import java.util.Vector;

/**
 * Tool to mark ChemObject's as having a problem. There are two levels:
 * a problem, and a warning, to allow for different coloring by renderer's.
 *
 * @author   Egon Willighagen
 * @created  2003-08-11
 */ 
public class ProblemMarker {

    public static String ERROR_MARKER = "org.openscience.cdk.validate.error";
    public static String WARNING_MARKER = "org.openscience.cdk.validate.warning";
    
    public static void markWithError(ChemObject object) {
        object.setProperty(ERROR_MARKER, new Boolean(true));
    }

    public static void markWithWarning(ChemObject object) {
        object.setProperty(WARNING_MARKER, new Boolean(true));
    }

    public static void unmarkWithError(ChemObject object) {
        object.removeProperty(ERROR_MARKER);
    }

    public static void unmarkWithWarning(ChemObject object) {
        object.removeProperty(WARNING_MARKER);
    }
    
    public static void unmark(ChemObject object) {
        unmarkWithWarning(object);
        unmarkWithError(object);
    }
}
