/* $RCSfile$   
 * $Author$   
 * $Date$   
 * $Revision$
 * 
 * Copyright (C) 2001-2007  The Chemistry Development Kit (CKD) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.exception;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * Exception that is thrown when an Atom is requested or required that
 * does not exist in the relevant environment.
 *
 * @cdk.module core
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.exception.NoSuchAtomExceptionTest")
public class NoSuchAtomException extends CDKException {

    private static final long serialVersionUID = -6367051798808824272L;

    /**
     * Constructs a new NoSuchAtomException with the given message.
     *
     * @param message for the constructed exception
     */
    @TestMethod("testNoSuchAtomException_String")
    public NoSuchAtomException(String message) {
        super( message );
    }
}
