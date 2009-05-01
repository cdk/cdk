/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
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
 *
 */
package org.openscience.cdk.graph;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.Iterator;


/**
 * This class allows to iterate trough the set of all possible
 * permutations of the bond order in a given atom container. 
 * It allows to check for a dependency of algorithm results 
 * on the bond order.
 *
 * <p>The permutation code here is based on a pseudo code example 
 * on a tutorial site created and maintained by Phillip P. Fuchs:
 * <a href="http://www.geocities.com/permute_it/pseudo2.html">http://www.geocities.com/permute_it/pseudo2.html</a>.
 * 
 *@author         steinbeck
 * @cdk.svnrev  $Revision$
 *@cdk.created    2005-05-04
 *@cdk.keyword    permutation
 */
public class AtomContainerBondPermutor extends AtomContainerPermutor
{

    public AtomContainerBondPermutor(AtomContainer ac)
    {
        setAtomContainer(ac);
        N = atomContainer.getBondCount();
        initBookkeeping();
        initObjectArray();
    }

    public void initObjectArray() {
        Iterator bonds = atomContainer.bonds().iterator();
        objects = new Object[atomContainer.getBondCount()];
        int i = 0;
        while (bonds.hasNext()) {
            objects[i] = bonds.next();
            i++;
        }
    }

    AtomContainer makeResult()
    {
        IBond[] bonds = new IBond[objects.length];
        for (int f = 0; f < objects.length; f++)
        {
            bonds[f] = ((IBond)objects[f]);
        }
        AtomContainer ac = new AtomContainer(atomContainer);
        ac.setBonds(bonds);
        AtomContainer clone = null;
        try {
            clone = (AtomContainer)ac.clone();
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return clone;
    }

}

