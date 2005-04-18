/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2005  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.atomtype;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.exception.CDKException;

/**
 * Classes that implement this interface are atom type matchers. They find the
 * most appropriate AtomType matching the given Atom in a given an atom type list.
 *
 * @author      mfe4
 * @cdk.created 2004-12-02
 * @cdk.module  core
 */
public interface AtomTypeMatcher {

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
    public AtomType findMatchingAtomType(AtomContainer container, Atom atom) throws CDKException;
}
