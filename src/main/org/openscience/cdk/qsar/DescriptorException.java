/*
 *  Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.exception.CDKException;

/**
 * Exception that is thrown by descriptor routines when a problem has occured.
 *
 * @cdk.module qsar
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.qsar.DescriptorExceptionTest")
public class DescriptorException extends CDKException {

    private static final long serialVersionUID = 2564845219649348102L;

    /**
     * Constructs a new DescriptorException with the given message.
     *
     * @param message for the constructed exception
     */
    public DescriptorException(String message) {
        super( message );
    }

    /**
     *  Constructs a new DescriptorException with from the supplied descriptor name and associated message.
     *
     *  @param name Name of the descriptor raising the exception
     *  @param message The message associated with the exception
     */
    public DescriptorException(String name, String message) {
        super( name + ": " + message );
    }
        
}

