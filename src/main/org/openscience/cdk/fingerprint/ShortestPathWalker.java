/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2012   Syed Asad Rahman <asad@ebi.ac.uk>
 *           
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.fingerprint;

import java.util.*;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

/**
 *
 * @author Syed Asad Rahman (2012) 
 * @cdk.keyword fingerprint 
 * @cdk.keyword similarity 
 * @cdk.module fingerprint
 * @cdk.githash
 *
 */
class ShortestPathWalker {

    private static final long serialVersionUID = 0x3b728f46;
    private final IAtomContainer atomContainer;
    private final Set<String> cleanPath;
    private final List<String> pseudoAtoms;
    private int pseduoAtomCounter;
    private final Set<StringBuilder> allPaths;

    /**
     *
     * @param atomContainer
     * @throws CloneNotSupportedException
     * @throws CDKException
     */
    public ShortestPathWalker(IAtomContainer atomContainer) {
        this.cleanPath = new HashSet<String>();
        this.atomContainer = atomContainer;
        this.pseudoAtoms = new ArrayList<String>();
        this.pseduoAtomCounter = 0;
        this.allPaths = new HashSet<StringBuilder>();
        findPaths();
    }

    /**
     * @return the cleanPath
     */
    public Set<String> getPaths() {
        return Collections.unmodifiableSet(cleanPath);
    }

    /**
     * @return the cleanPath
     */
    public int getPathCount() {
        return cleanPath.size();
    }

    private void findPaths() {
        pseudoAtoms.clear();
        traverseShortestPaths();

        for (Iterator<StringBuilder> it = allPaths.iterator(); it.hasNext();) {
            StringBuilder s = it.next();
            String s1 = s.toString().trim();
            if (s1.equals("")) {
                continue;
            }
            if (!cleanPath.contains(s1)) {
                cleanPath.add(s1);
            }
        }
    }

    /*
     * This module generates shortest path between two atoms
     */
    private void traverseShortestPaths() {
        /*
         * Canonicalisation of atoms for reporting unique paths with consistency
         */
        Collection<IAtom> canonicalizeAtoms = new SimpleAtomCanonicalisation().canonicalizeAtoms(atomContainer);
        for (IAtom sourceAtom : canonicalizeAtoms) {
            StringBuilder sb = new StringBuilder();
            setAtom(sourceAtom, sb);
            if (!allPaths.contains(sb)) {
                allPaths.add(sb);
            }
            for (IAtom sinkAtom : canonicalizeAtoms) {
                sb = new StringBuilder();
                if (sourceAtom == sinkAtom) {
                    continue;
                }
                List<IAtom> shortestPath = PathTools.getShortestPath(atomContainer, sourceAtom, sinkAtom);
                if (shortestPath == null || shortestPath.isEmpty() || shortestPath.size() < 2) {
                    continue;
                }
                //System.out.println("Path length " + shortestPath.size());
                IAtom atomCurrent = shortestPath.get(0);
                for (int i = 1; i < shortestPath.size(); i++) {
                    IAtom atomNext = shortestPath.get(i);
                    setAtom(atomCurrent, sb);
                    sb.append(getBondSymbol(atomContainer.getBond(atomCurrent, atomNext)));
                    atomCurrent = atomNext;
                }
                setAtom(atomCurrent, sb);
                allPaths.add(sb);
            }
        }
    }

    private void setAtom(IAtom atomCurrent, StringBuilder sb) {
        if (atomCurrent instanceof IPseudoAtom) {
            if (!pseudoAtoms.contains(atomCurrent.getSymbol())) {
                pseudoAtoms.add(pseduoAtomCounter, atomCurrent.getSymbol());
                pseduoAtomCounter += 1;
            }
            sb.append((char) (PeriodicTable.getElementCount()
                    + pseudoAtoms.indexOf(atomCurrent.getSymbol()) + 1));
        } else {
            Integer atnum = PeriodicTable.getAtomicNumber(atomCurrent.getSymbol());
            if (atnum != null) {
                sb.append(toAtomPattern(atomCurrent));
            } else {
                sb.append((char) PeriodicTable.getElementCount() + 1);
            }
        }
    }

    private String toAtomPattern(IAtom atom) {
        return atom.getSymbol();
    }

    /**
     * Gets the bondSymbol attribute of the HashedFingerprinter class
     *
     * @param bond Description of the Parameter
     * @return The bondSymbol value
     */
    private char getBondSymbol(IBond bond) {
        if (isSP2Bond(bond)) {
            return '@';
        } else {
            switch (bond.getOrder()) {
                case SINGLE:
                    return '1';
                case DOUBLE:
                    return '2';
                case TRIPLE:
                    return '3';
                case QUADRUPLE:
                    return '4';
                default:
                    return '5';
            }
        }
    }

    /**
     * Returns true if the bond binds two atoms, and both atoms are SP2 in a ring system.
     */
    private boolean isSP2Bond(IBond bond) {
        return bond.getFlag(CDKConstants.ISAROMATIC);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String path : cleanPath) {
            sb.append(path).append("->");
        }
        return sb.toString();
    }
}
