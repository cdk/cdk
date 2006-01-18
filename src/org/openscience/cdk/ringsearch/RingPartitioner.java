/*
 *  RingPartitioner.java
 *
 *  $RCSfile$    $Author$    $Date$    $Revision$
 *
 *  Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.ringsearch;

import java.util.Vector;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.Ring;
import org.openscience.cdk.interfaces.RingSet;

/**
 *  Partitions a RingSet into RingSets of connected rings. Rings which share an
 *  Atom, a Bond or three or more atoms with at least on other ring in the
 *  RingSet are considered connected.
 *
 * @cdk.module standard
 * @cdk.bug    1117775
 */
public class RingPartitioner {
    
    /**
     *  Debugging on/off
     */
    public final static boolean debug = false;
    // minimum details


    /**
     *  Partitions a RingSet into RingSets of connected rings. Rings which share
     *  an Atom, a Bond or three or more atoms with at least on other ring in
     *  the RingSet are considered connected.
     *
     *@param  ringSet  The RingSet to be partitioned
     *@return          A Vector of connected RingSets
     */
    public static Vector partitionRings(RingSet ringSet) {
        Vector ringSets = new Vector();
        if (ringSet.size() == 0) return ringSets;
        RingSet tempRingSet = null;
        Ring ring = (Ring)ringSet.get(0);
        if (ring == null) return ringSets;
        RingSet rs = ring.getBuilder().newRingSet();
        for (int f = 0; f < ringSet.size(); f++) {
            rs.add(ringSet.get(f));
        }
        do {
            ring = (Ring) rs.get(0);
            RingSet newRs = ring.getBuilder().newRingSet();
            newRs.add(ring);
            tempRingSet = walkRingSystem(rs, ring, newRs);
            if (debug) {
                System.out.println("found ringset with ringcount: " + tempRingSet.size());
            }
            ringSets.addElement(walkRingSystem(rs, ring, newRs));

        } while (rs.size() > 0);

        return ringSets;
    }


    /**
     *  Converts a RingSet to an AtomContainer.
     *
     *@param  ringSet  The RingSet to be converted.
     *@return          The AtomContainer containing the bonds and atoms of the ringSet.
     */
    public static IAtomContainer convertToAtomContainer(RingSet ringSet) {
    	Ring ring = (Ring) ringSet.get(0);
    	if (ring == null) return null;
        IAtomContainer ac = ring.getBuilder().newAtomContainer();
        for (int i = 0; i < ringSet.size(); i++) {
            ring = (Ring) ringSet.get(i);
            for (int r = 0; r < ring.getBondCount(); r++) {
            	org.openscience.cdk.interfaces.IBond bond = ring.getBondAt(r);
                if (!ac.contains(bond)) {
                    for (int j = 0; j < bond.getAtomCount(); j++) {
                        ac.addAtom(bond.getAtomAt(j));
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
    private static RingSet walkRingSystem(RingSet rs, Ring ring, RingSet newRs) {
        Ring tempRing;
        Vector tempRings = rs.getConnectedRings(ring);
        if (debug) {
            System.out.println("walkRingSystem -> tempRings.size(): " + tempRings.size());
        }
        rs.remove(ring);
        for (int f = 0; f < tempRings.size(); f++) {
            tempRing = (Ring) tempRings.elementAt(f);
            if (!newRs.contains(tempRing)) {
                newRs.add(tempRing);
                newRs.add(walkRingSystem(rs, tempRing, newRs));
            }
        }
        return newRs;
    }

}





