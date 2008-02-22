/*
 *  Copyright (C) 2004-2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.qsar.model;

import org.openscience.cdk.exception.CDKException;

/**
 * Exception that is thrown by model routines when a problem has occured.
 *
 * @author Rajarshi Guha
 * @cdk.module qsar
 * @cdk.svnrev  $Revision$
 */
public class QSARModelException extends CDKException {

    private static final long serialVersionUID = 4931287199065879144L;

    /**
     * Constructs a new QSARModelException with the given message.
     *
     * @param message for the constructed exception
     */
    public QSARModelException(String message) {
        super( message );
    }
}

