/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
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
package org.openscience.cdk.reaction;

import java.util.ArrayList;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;

/**
 * Classes that implement this interface are reaction mechanisms.
 *
 * @author      Miguel Rojas
 * @cdk.module  reaction
 */
public interface IReactionMechanism {
	
    
    /** 
     * Initiates the process for the given mechanism. The atoms to apply are mapped between
     * reactants and products.
     *
     * @param moleculeSet The IMoleculeSet to apply the mechanism
     * @param atomList    The list of atoms taking part in the mechanism
     * @param bondList    The list of bonds taking part in the mechanism
     * @return            The Reaction mechanism
     * 
     * @throws CDKException if an error occurs during the reaction process. 
     * See documentation for individual reaction processes
     */
    public IReaction initiate(IMoleculeSet moleculeSet, ArrayList<IAtom> atomList, ArrayList<IBond> bondList) throws CDKException;

}
