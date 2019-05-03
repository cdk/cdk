/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.geometry.cip;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Concept of a ligand in CIP terms, reflecting a side chain of a central atom that can
 * have precedence over another.
 *
 * @cdk.module cip
 * @cdk.githash
 */
public class Ligand implements ILigand {

    private IAtomContainer container;
    private IAtom          centralAtom;
    private IAtom          ligandAtom;
    private VisitedAtoms   visitedAtoms;

    public Ligand(IAtomContainer container, VisitedAtoms visitedAtoms, IAtom centralAtom, IAtom ligandAtom) {
        this.container = container;
        this.centralAtom = centralAtom;
        this.ligandAtom = ligandAtom;
        this.visitedAtoms = new VisitedAtoms();
        this.visitedAtoms.visited(visitedAtoms);
        this.visitedAtoms.visited(centralAtom);
    }

    /**
     * {@link IAtomContainer} of which this ligand is part.
     *
     * @return the IAtomContainer
     */
    @Override
    public IAtomContainer getAtomContainer() {
        return container;
    }

    /**
     * The central {@link IAtom} to which this ligand is connected via one {@link IBond}.
     *
     * @return the central atom
     */
    @Override
    public IAtom getCentralAtom() {
        return centralAtom;
    }

    /**
     * {@link IAtom} of the ligand that is connected to the chiral {@link IAtom} via
     * one {@link IBond}.
     *
     * @return the ligand atom
     */
    @Override
    public IAtom getLigandAtom() {
        return ligandAtom;
    }

    /** {@inheritDoc} */
    @Override
    public VisitedAtoms getVisitedAtoms() {
        return visitedAtoms;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isVisited(IAtom atom) {
        return visitedAtoms.isVisited(atom);
    }

}
