/*
 *  Copyright (C) 2004  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.openscience.cdk.qsar.model;

import org.openscience.cdk.exception.CDKException;

/**
 * Exception that is thrown by model routines when a problem has occured
 *
 * @author Rajarshi Guha
 * @cdk.module qsar
 */
public class QSARModelException extends CDKException {

    /**
     * Constructs a new QSARModelException with the given message.
     *
     * @param message for the constructed exception
     */
    public QSARModelException(String message) {
        super( message );
    }
}

