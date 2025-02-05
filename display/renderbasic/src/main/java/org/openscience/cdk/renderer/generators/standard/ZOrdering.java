/*
 * Copyright (C) 2024 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.renderer.generators.standard;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.graph.invariant.Canon;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupType;

import javax.vecmath.Point2d;
import java.util.List;

/**
 * Algorithmic Z-Ordering assignment.
 * <br/>
 * Z-Ordering is used to determine which bond is "in-front" in a 2D depiction.
 * This class uses some heuristics roughly based on the procedure described by
 * {@cdk.cite Clark13}.
 */
final class ZOrdering {

    /**
     * Assign the Z-ordering to the bonds in the provided molecule. Any existing
     * Z-ordering will be left alone, if you want to overwrite it then you
     * should clear the ordering first.
     *
     * @param mol the molecule
     */
    static void assign(final IAtomContainer mol) {

        // ensure positional variation/multi attach are always "in front", this
        // a bit of a fudge, really we should treat them as bonded and consider
        // cues from wedges etc
        raiseMultiattach(mol);

        for (IAtomContainer part : ConnectivityChecker.partitionIntoMolecules(mol)) {
            long[] z = init(part);
            // CDK canonicalization is stable and so we c
            z = Canon.label(part, GraphUtil.toAdjList(part), z);
            for (IBond bond : part.bonds()) {
                if (bond.getProperty(CDKConstants.Z_ORDER) != null)
                    continue;
                bond.setProperty(CDKConstants.Z_ORDER,
                                 (int)Math.max(z[bond.getBegin().getIndex()],
                                               z[bond.getEnd().getIndex()]));
            }
        }
    }

    /**
     * Artificially raise all bonds that are multiattach
     * @param mol the molecule
     */
    private static void raiseMultiattach(IAtomContainer mol) {
        List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgroups == null)
            return;
        for (Sgroup sgroup : sgroups) {
            if (sgroup.getType() != SgroupType.ExtMulticenter)
                continue;
            for (IBond bond : sgroup.getBonds()) {
                if (bond.getProperty(CDKConstants.Z_ORDER) == null)
                    bond.setProperty(CDKConstants.Z_ORDER, 999999);
            }
        }
    }

    /**
     * Get the initial Z-ordering based on cues (wedged bonds)
     */
    private static long[] init(IAtomContainer mol) {
        long[] z = new long[mol.getAtomCount()];
        if (initUsingInternalWedges(mol, z))
            return z;
        if (initUsingExternalWedges(mol, z))
            return z;
        // pick a random atom
        IAtom low = null;
        for (IAtom atom : mol.atoms()) {
            Point2d p = atom.getPoint2d();
            assert p != null;
            if (low == null || p.y < low.getPoint2d().y)
                low = atom;
        }
        if (low != null) {
            z[low.getIndex()] += 10;
        }
        return z;
    }

    /**
     * Set the initial ordering based on non-terminal wedge up/down and bold
     * bonds.
     *
     * @param mol the molecule
     * @param z the z-ordering
     * @return some values were initialized
     */
    private static boolean initUsingInternalWedges(IAtomContainer mol, long[] z) {
        boolean init = false;
        for (IBond bond : mol.bonds()) {
            IAtom bgn = bond.getBegin();
            IAtom end = bond.getEnd();
            if (bgn.getBondCount() == 1 ||
                end.getBondCount() == 1)
                 continue;
            switch (bond.getDisplay()) {
                case WedgedHashBegin:
                case WedgeEnd:
                    z[bgn.getIndex()] += 10;
                    z[end.getIndex()] -= 10;
                    init = true;
                    break;
                case WedgedHashEnd:
                case WedgeBegin:
                    z[bgn.getIndex()] -= 10;
                    z[end.getIndex()] += 10;
                    init = true;
                    break;
                case Bold:
                    z[bgn.getIndex()] += 10;
                    z[end.getIndex()] += 10;
                    init = true;
                    break;
                case Hash:
                    z[bgn.getIndex()] -= 10;
                    z[end.getIndex()] -= 10;
                    init = true;
                    break;
            }
        }
        return init;
    }

    /**
     * Set the initial ordering based on terminal (either atom has degree=1) wedge up/down and bold
     * bonds.
     *
     * @param mol the molecule
     * @param z the z-ordering
     * @return some values were initialized
     */
    private static boolean initUsingExternalWedges(IAtomContainer mol, long[] z) {
        boolean init = false;
        for (IBond bond : mol.bonds()) {
            IAtom bgn = bond.getBegin();
            IAtom end = bond.getEnd();
            if (bgn.getBondCount() != 1 && end.getBondCount() != 1)
                continue;
            switch (bond.getDisplay()) {
                case WedgedHashBegin:
                case WedgeEnd:
                    z[bgn.getIndex()] -= 10;
                    z[end.getIndex()] += 10;

                    break;
                case WedgedHashEnd:
                case WedgeBegin:
                    z[bgn.getIndex()] += 10;
                    z[end.getIndex()] -= 10;
                    init = true;
                    break;
            }
        }
        return init;
    }
}
