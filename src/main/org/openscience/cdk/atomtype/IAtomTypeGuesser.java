/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; version 2.1.
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

import java.util.List;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;

/**
 * Classes that implement this interface are atom type guessers. As compared
 * to the IAtomTypeMatcher, this guesser has room for missing information.
 * Not uncommonly, one bit of information is missing.
 *
 * @author      egonw
 * @cdk.created 2006-09-22
 * @cdk.module  core
 * @cdk.svnrev  $Revision$
 * 
 * @see         IAtomTypeMatcher
 */
public interface IAtomTypeGuesser {

     /**
      * Method that returns an iterator with a suitable list of atom types
      * given the provided atom.
      *
      * @param  container AtomContainer of which the <code>atom</code> is part
      * @param  atom      Atom for which a matching atom type is searched
      * @return           The matching AtomTypes
      * @throws           CDKException when something went wrong with going through
      *                   the AtomType's
      */
    public List<IAtomType> possibleAtomTypes(IAtomContainer container, IAtom atom) throws CDKException;
}
