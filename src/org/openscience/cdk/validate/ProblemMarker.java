/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.validate;

/**
 * Tool to mark IChemObject's as having a problem. There are two levels:
 * a problem, and a warning, to allow for different coloring by renderer's.
 *
 * @cdk.module standard
 *
 * @author   Egon Willighagen
 * @cdk.created  2003-08-11
 */
public class ProblemMarker {

    public static String ERROR_MARKER = "org.openscience.cdk.validate.error";
    public static String WARNING_MARKER = "org.openscience.cdk.validate.warning";
    
    public static void markWithError(org.openscience.cdk.interfaces.IChemObject object) {
        object.setProperty(ERROR_MARKER, new Boolean(true));
    }

    public static void markWithWarning(org.openscience.cdk.interfaces.IChemObject object) {
        object.setProperty(WARNING_MARKER, new Boolean(true));
    }

    public static void unmarkWithError(org.openscience.cdk.interfaces.IChemObject object) {
        object.removeProperty(ERROR_MARKER);
    }

    public static void unmarkWithWarning(org.openscience.cdk.interfaces.IChemObject object) {
        object.removeProperty(WARNING_MARKER);
    }
    
    public static void unmark(org.openscience.cdk.interfaces.IChemObject object) {
        unmarkWithWarning(object);
        unmarkWithError(object);
    }
}
