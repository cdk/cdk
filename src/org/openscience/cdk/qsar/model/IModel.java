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

/**
 * Classes that implement this interface will build statistical models.
 * 
 * Currently the design of the modeling system is that classes based on
 * a given backend should be based of an abtract class that implements
 * this interface. See <code>RModel</code> as an example.
 *
 * @author Rajarshi Guha
 * @cdk.module qsar
 * @cdk.svnrev  $Revision$
 */
public interface IModel {

    /**
     * Builds (trains) the model.
     *
     * @throws QSARModelException if errors occur in data types, calls to the R session. See
     * the corresponding method in subclasses of this class for further details.
     */
   public void build() throws QSARModelException;
    /**
     * Makes predictions using a previously built model.
     *
     * @throws QSARModelException if errors occur in data types, calls to the R session. See
     * the corresponding method in subclasses of this class for further details.
     */
   public void predict() throws QSARModelException;
}

