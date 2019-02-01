/* Copyright (C) 2006-2007  Todd Martin (Environmental Protection Agency) <Martin.Todd@epamail.epa.gov>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.atomtype;

import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;

/**
 * Determines the EState atom types.
 *
 * @author Todd Martin
 * @author nick
 * @cdk.module standard
 * @cdk.githash
 * @cdk.keyword atom type, E-state
 */
public class EStateAtomTypeMatcher implements IAtomTypeMatcher {

    IRingSet ringSet = null;

    public void setRingSet(IRingSet rs) {
        ringSet = rs;
    }

    @Override
    public IAtomType[] findMatchingAtomTypes(IAtomContainer atomContainer) throws CDKException {
        IAtomType[] types = new IAtomType[atomContainer.getAtomCount()];
        int typeCounter = 0;
        for (IAtom atom : atomContainer.atoms()) {
            types[typeCounter] = findMatchingAtomType(atomContainer, atom);
            typeCounter++;
        }
        return types;
    }

    @Override
    public IAtomType findMatchingAtomType(IAtomContainer atomContainer, IAtom atom) {

        IAtomType atomType = null;
        try {
            String fragment = "";
            int NumHAtoms = atom.getImplicitHydrogenCount() != null ?
                            atom.getImplicitHydrogenCount() : 0;
            int NumSingleBonds2 = NumHAtoms;
            int NumDoubleBonds2 = 0;
            int NumTripleBonds2 = 0;
            int NumAromaticBonds2 = 0;
            int NumAromaticBondsTotal2 = 0;

            String element = atom.getSymbol();

            List<IAtom> attachedAtoms = atomContainer.getConnectedAtomsList(atom);

            for (int j = 0; j <= attachedAtoms.size() - 1; j++) {
                IAtom attached = (IAtom) attachedAtoms.get(j);
                IBond b = atomContainer.getBond(atom, attached);
                if (attached.getSymbol().equals("H")) NumHAtoms++;

                if (atom.getFlag(CDKConstants.ISAROMATIC) && attached.getFlag(CDKConstants.ISAROMATIC)) {

                    boolean SameRing = inSameAromaticRing(atomContainer, atom, attached, ringSet);

                    if (SameRing) {
                        NumAromaticBonds2++;
                        if (element.equals("N")) {
                            if (b.getOrder() == IBond.Order.SINGLE) NumAromaticBondsTotal2++;
                            if (b.getOrder() == IBond.Order.DOUBLE)
                                NumAromaticBondsTotal2 = NumAromaticBondsTotal2 + 2;
                        }
                    } else {
                        if (b.getOrder() == IBond.Order.SINGLE) NumSingleBonds2++;
                        if (b.getOrder() == IBond.Order.DOUBLE) NumDoubleBonds2++;
                        if (b.getOrder() == IBond.Order.TRIPLE) NumTripleBonds2++;
                    }

                } else {

                    if (b.getOrder() == IBond.Order.SINGLE) NumSingleBonds2++;
                    if (b.getOrder() == IBond.Order.DOUBLE) NumDoubleBonds2++;
                    if (b.getOrder() == IBond.Order.TRIPLE) NumTripleBonds2++;
                }
            }
            NumSingleBonds2 = NumSingleBonds2 - NumHAtoms;

            // assign frag here
            fragment = "S";

            for (int j = 0; j <= NumTripleBonds2 - 1; j++) {
                fragment += "t";
            }

            for (int j = 0; j <= NumDoubleBonds2 - 1; j++) {
                fragment += "d";
            }

            for (int j = 0; j <= NumSingleBonds2 - 1; j++) {
                fragment += "s";
            }

            for (int j = 0; j <= NumAromaticBonds2 - 1; j++) {
                fragment += "a";
            }

            fragment += element;

            if (atom.getFormalCharge() == 1) {
                fragment += "p";
            } else if (atom.getFormalCharge() == -1) {
                fragment += "m";
            }

            if (NumHAtoms == 1)
                fragment += "H";
            else if (NumHAtoms > 1) fragment += ("H" + NumHAtoms);

            atomType = atom.getBuilder().newInstance(IAtomType.class, fragment, atom.getSymbol());
            atomType.setFormalCharge(atom.getFormalCharge());
            if (atom.getFlag(CDKConstants.ISAROMATIC)) atomType.setFlag(CDKConstants.ISAROMATIC, true);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return atomType;
    }

    public static boolean inSameAromaticRing(IAtomContainer m, IAtom atom1, IAtom atom2, IRingSet rs) {
        if (rs == null) return false;
        for (int i = 0; i <= rs.getAtomContainerCount() - 1; i++) {
            IRing r = (IRing) rs.getAtomContainer(i);
            if (r.contains(atom1) && r.contains(atom2)) {
                if (isAromaticRing(r)) return (true);
            }
        }
        return false;
    }

    static boolean isAromaticRing(IRing ring) {
        for (int i = 0; i < ring.getAtomCount(); i++)
            if (!ring.getAtom(i).getFlag(CDKConstants.ISAROMATIC)) return (false);

        return (true);
    }

}
