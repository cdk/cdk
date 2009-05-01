/* $Revision$ $Author$ $Date$    
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
 */
package org.openscience.cdk.ringsearch;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  Partitions a RingSet into RingSets of connected rings. Rings which share an
 *  Atom, a Bond or three or more atoms with at least on other ring in the
 *  RingSet are considered connected.
 *
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.ringsearch.RingPartitionerTest")
public class RingPartitioner {
    
    /**
     *  Debugging on/off
     */
    public final static boolean debug = false;
    // minimum details


    /**
     *  Partitions a RingSet into RingSets of connected rings. Rings which share
     *  an Atom, a Bond or three or more atoms with at least on other ring in
     *  the RingSet are considered connected. Thus molecules such as azulene and
     * indole will return a List with 1 element.
     *
     * <p>Note that an isolated ring is considered to be <i>self-connect</i>. As a result
     * a molecule such as biphenyl will result in a 2-element List being returned (each
     * element corresponding to a phenyl ring).
     *
     *@param  ringSet  The RingSet to be partitioned
     *@return          A {@link List} of connected RingSets
     */
    @TestMethod("testPartitionIntoRings")
    public static List<IRingSet> partitionRings(IRingSet ringSet) {
        List<IRingSet> ringSets = new ArrayList<IRingSet>();
        if (ringSet.getAtomContainerCount() == 0) return ringSets;
        IRing ring = (IRing)ringSet.getAtomContainer(0);
        if (ring == null) return ringSets;
        IRingSet rs = ring.getBuilder().newRingSet();
        for (int f = 0; f < ringSet.getAtomContainerCount(); f++) {
            rs.addAtomContainer(ringSet.getAtomContainer(f));
        }
        do {
            ring = (IRing) rs.getAtomContainer(0);
            IRingSet newRs = ring.getBuilder().newRingSet();
            newRs.addAtomContainer(ring);
            ringSets.add(walkRingSystem(rs, ring, newRs));

        } while (rs.getAtomContainerCount() > 0);

        return ringSets;
    }


    /**
     *  Converts a RingSet to an AtomContainer.
     *
     *@param  ringSet  The RingSet to be converted.
     *@return          The AtomContainer containing the bonds and atoms of the ringSet.
     */
    @TestMethod("testConvertToAtomContainer_IRingSet")
    public static IAtomContainer convertToAtomContainer(IRingSet ringSet) {
    	IRing ring = (IRing) ringSet.getAtomContainer(0);
    	if (ring == null) return null;
        IAtomContainer ac = ring.getBuilder().newAtomContainer();
        for (int i = 0; i < ringSet.getAtomContainerCount(); i++) {
            ring = (IRing) ringSet.getAtomContainer(i);
            for (int r = 0; r < ring.getBondCount(); r++) {
            	IBond bond = ring.getBond(r);
                if (!ac.contains(bond)) {
                    for (int j = 0; j < bond.getAtomCount(); j++) {
                        ac.addAtom(bond.getAtom(j));
                    }
                    ac.addBond(bond);
                }
            }
        }
        return ac;
    }


    /**
     *  Perform a walk in the given RingSet, starting at a given Ring and
     *  recursivly searching for other Rings connected to this ring. By doing
     *  this it finds all rings in the RingSet connected to the start ring,
     *  putting them in newRs, and removing them from rs.
     *
     *@param  rs     The RingSet to be searched
     *@param  ring   The ring to start with
     *@param  newRs  The RingSet containing all Rings connected to ring
     *@return        newRs The RingSet containing all Rings connected to ring
     */
    private static IRingSet walkRingSystem(IRingSet rs, IRing ring, IRingSet newRs) {
        IRing tempRing;
        IRingSet tempRings = rs.getConnectedRings(ring);
//        logger.debug("walkRingSystem -> tempRings.size(): " + tempRings.size());
        rs.removeAtomContainer(ring);
        for (IAtomContainer container : tempRings.atomContainers()) {
            tempRing = (IRing)container;
            if (!newRs.contains(tempRing)) {
                newRs.addAtomContainer(tempRing);
                newRs.add(walkRingSystem(rs, tempRing, newRs));
            }
        }
        return newRs;
    }

}





