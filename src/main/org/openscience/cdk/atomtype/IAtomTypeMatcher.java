/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2005-2007  Matteo Floris <mfe4@users.sf.net>
 *                    2008  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.atomtype;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;

/**
 * Classes that implement this interface are atom type matchers. They find the
 * most appropriate AtomType matching the given Atom in a given atom type list.
 *
 * @author      mfe4
 * @cdk.created 2004-12-02
 * @cdk.module  core
 * @cdk.svnrev  $Revision$
 * 
 * @see         IAtomTypeGuesser
 */
public interface IAtomTypeMatcher {

     /**
      * Method that assigns an atom type to a given atom belonging to an atom 
      * container.
      *
      * @param  container AtomContainer of which the <code>atom</code> is part
      * @param  atom      Atom for which a matching atom type is searched
      * @return           The matching AtomType
      * @throws           CDKException when something went wrong with going through
      *                   the AtomType's
      */
    public IAtomType findMatchingAtomType(IAtomContainer container, IAtom atom) throws CDKException;

    /**
     * Method that assigns atom types to atoms in the given atom container.
     *
     * @param  container AtomContainer for which atom types are perceived
     * @return           The matching AtomType
     * @throws           CDKException when something went wrong with going through
     *                   the AtomType's
     */
   public IAtomType[] findMatchingAtomType(IAtomContainer container) throws CDKException;

}
