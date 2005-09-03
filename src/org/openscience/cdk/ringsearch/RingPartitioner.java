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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.ringsearch;

import java.util.Vector;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Ring;
import org.openscience.cdk.RingSet;

/**
 *  Partitions a RingSet into RingSets of connected rings. Rings which share an
 *  Atom, a Bond or three or more atoms with at least on other ring in the
 *  RingSet are considered connected.
 *
 * @cdk.module standard
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
        Ring ring;
        RingSet tempRingSet = null;
        //RingSet rs = (RingSet)ringSet.clone();
        RingSet rs = new RingSet();
        for (int f = 0; f < ringSet.size(); f++) {
            rs.addElement(ringSet.elementAt(f));
        }
        do {
            ring = (Ring) rs.elementAt(0);
            RingSet newRs = new RingSet();
            newRs.addElement(ring);
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
    public static AtomContainer convertToAtomContainer(RingSet ringSet) {
        AtomContainer ac = new org.openscience.cdk.AtomContainer();
        for (int i = 0; i < ringSet.size(); i++) {
            Ring ring = (Ring) ringSet.get(i);
            for (int r = 0; r < ring.getBondCount(); r++) {
            	org.openscience.cdk.interfaces.Bond bond = ring.getBondAt(r);
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
        rs.removeElement(ring);
        for (int f = 0; f < tempRings.size(); f++) {
            tempRing = (Ring) tempRings.elementAt(f);
            if (!newRs.contains(tempRing)) {
                newRs.addElement(tempRing);
                newRs.add(walkRingSystem(rs, tempRing, newRs));
            }
        }
        return newRs;
    }

}





