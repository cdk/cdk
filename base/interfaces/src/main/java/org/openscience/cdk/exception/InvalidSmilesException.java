/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *                    2015  Egon Willighagen <egonw@users.sf.net>
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
 *
 */
package org.openscience.cdk.exception;


/**
 * Exception thrown when an error is occurred during SMILES parsing.
 *
 */
public class InvalidSmilesException extends CDKException {

    private static final long serialVersionUID = 1932255464874201495L;

    /**
     * Constructs a new exception with a custom message.
     *
     * @param message the custom message detailing what may be wrong with the SMILES.
     */
    public InvalidSmilesException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with a custom message and a stacktrace.
     *
     * @param message   the custom message detailing what may be wrong with the SMILES.
     * @param exception the underlying exception
     */
    public InvalidSmilesException(String message, Exception exception) {
        super(message, exception);
    }
}
