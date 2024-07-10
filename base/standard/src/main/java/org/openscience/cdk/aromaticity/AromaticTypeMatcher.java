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

package org.openscience.cdk.aromaticity;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElement;

/**
 * Determine the {@link org.openscience.cdk.aromaticity.AromaticType} of an
 * atom.
 */
final class AromaticTypeMatcher {

    /**
     * Safely access the charge, defaulting null to 0.
     * @param atom the atom
     * @return the formal charge
     */
    private static int charge(IAtom atom) {
        return atom.getFormalCharge() != null ? atom.getFormalCharge() : 0;
    }

    private AromaticTypeMatcher() {}

    /**
     * Safely access the hydrogen count, defaulting null to 0.
     * @param atom the atom
     * @return the hydrogen count
     */
    private static int hcount(final IAtom atom) {
        return atom.getImplicitHydrogenCount() != null
                ? atom.getImplicitHydrogenCount() : 0;
    }

    /**
     * Return the packed bond info (binfo) for a give atom. This information
     * captures the:
     * <pre>
     *  - number of attached single bonds (mask:0x000f)
     *  - number of attached cyclic double bonds (mask:0x00f0)
     *  - number of attached acyclic double bonds (mask:0x0f00)
     *  - number of other/error bonds (mask:0xf000)
     * </pre>
     * Examples:
     * <pre>
     *  - 0x0003 = 3 single bonds
     *  - 0x0012 = 2 single bonds, 1 double bond (cyclic)
     *  - 0x0102 = 2 single bonds, 1 double bond (acyclic)
     * </pre>
     *
     * @param atom the atom
     * @return the bond info
     */
    private static int binfo(final IAtom atom) {
        int binfo = atom.getImplicitHydrogenCount();
        if (atom.getBondCount() + binfo > 15)
            return 0x1000; // error
        for (IBond bond : atom.bonds()) {
            switch (bond.getOrder()) {
                case SINGLE: binfo += 0x0001; break;
                case DOUBLE:
                    binfo += bond.isInRing() ? 0x0010 : 0x0100;
                    break;
                default:
                    binfo += 0x1000;
                    break;
            }
        }
        return binfo;
    }


    private static boolean hasExoHet(IAtom atom) {
        for (IBond bond : atom.bonds()) {
            if (bond.isInRing() || bond.getOrder() != IBond.Order.DOUBLE)
                continue;
            return bond.getOther(atom).getAtomicNumber() != IAtom.C;
        }
        return false;
    }

    private static boolean hasOxide(IAtom atom) {
        for (IBond bond : atom.bonds()) {
            if (bond.isInRing())
                continue;
            IAtom nbor = bond.getOther(atom);
            if (nbor.getAtomicNumber() != IAtom.O)
                return false;
            // *=O
            if (bond.getOrder() == IBond.Order.DOUBLE)
                return true;
            // [*+]-[O-]
            return bond.getOrder() == IBond.Order.SINGLE &&
                    charge(atom) == +1 && charge(nbor) == -1;
        }
        return false;
    }

    /**
     * Determine the aromatic type of the atom.
     * <br/>
     * <b>Prerequisites: the atom and attached bonds should have their
     * ring membership marked using:
     * ({@link org.openscience.cdk.graph.Cycles#markRingAtomsAndBonds(org.openscience.cdk.interfaces.IAtomContainer)} )</b>
     *
     * @param atom the atom
     * @return the aromatic atom type
     */
    static AromaticType getType(final IAtom atom) {

        // fail-fast preconditions
        if (!atom.isInRing())
            return AromaticType.UNKNOWN;
        // P4 in the CDK_1x model is a special case
        if (atom.getBondCount() > 3 && atom.getAtomicNumber() != IElement.P)
            return AromaticType.UNKNOWN;
        if (hcount(atom) > 1 && atom.getAtomicNumber() != IElement.P)
            return AromaticType.UNKNOWN;

        int q;
        switch (atom.getAtomicNumber()) {
            case IElement.B:
                if (charge(atom) == 0) {
                    switch (binfo(atom)) {
                        case 0x0011: return AromaticType.B2;
                        case 0x0003: return AromaticType.B3;
                    }
                }
                break;

            case IElement.C:
                switch (charge(atom)) {
                    case -1:
                        switch (binfo(atom)) {
                            case 0x0011: return AromaticType.C2_MINUS;
                            case 0x0003: return AromaticType.C3_MINUS;
                        }
                        break;
                    case 0:
                        switch (binfo(atom)) {
                            case 0x0012: return AromaticType.C3;
                            case 0x0102:
                                if (hasExoHet(atom))
                                    return AromaticType.C3_ENEG_EXO;
                                return AromaticType.C3_EXO;
                        }
                        break;
                    case +1:
                        switch (binfo(atom)) {
                            case 0x0011: return AromaticType.C2_PLUS;
                            case 0x0003: return AromaticType.C3_PLUS;
                        }
                        break;
                }
                break;
            case IElement.Si:
                switch (charge(atom)) {
                    case -1:
                        switch (binfo(atom)) {
                            case 0x0011: return AromaticType.Si2_MINUS;
                            case 0x0003: return AromaticType.Si3_MINUS;
                        }
                        break;
                    case 0:
                        switch (binfo(atom)) {
                            case 0x0012: return AromaticType.Si3;
                            case 0x0102: return AromaticType.Si3_EXO;
                        }
                        break;
                    case +1:
                        switch (binfo(atom)) {
                            case 0x0011: return AromaticType.Si2_PLUS;
                            case 0x0003: return AromaticType.Si3_PLUS;
                        }
                        break;
                }
                break;

            case IElement.N:
                q = charge(atom);
                if (q == -1 && binfo(atom) == 0x0002)
                    return AromaticType.N2_MINUS;
                else if (q == 0) {
                    switch (binfo(atom)) {
                        case 0x0003: return AromaticType.N3;
                        case 0x0011: return AromaticType.N2;
                        case 0x0111:
                            if (hasOxide(atom))
                                return AromaticType.N3_OXIDE;
                            break;
                    }
                }
                else if (q == +1 && binfo(atom) == 0x0012) {
                    if (hasOxide(atom))
                        return AromaticType.N3_OXIDE_PLUS;
                    else
                        return AromaticType.N3_PLUS;
                }
                break;
            case IElement.P:
                q = charge(atom);
                if (q == -1 && binfo(atom) == 0x0002)
                    return AromaticType.P2_MINUS;
                else if (q == 0) {
                    switch (binfo(atom)) {
                        case 0x0013: return AromaticType.P4;
                        case 0x0003: return AromaticType.P3;
                        case 0x0011: return AromaticType.P2;
                        case 0x0111:
                            if (hasOxide(atom))
                                return AromaticType.P3_OXIDE;
                    }
                }
                else if (q == +1 && binfo(atom) == 0x0012) {
                    if (hasOxide(atom))
                        return AromaticType.P3_OXIDE_PLUS;
                    else
                        return AromaticType.P3_PLUS;
                }
                break;
            case IElement.As:
                q = charge(atom);
                if (q == -1 && binfo(atom) == 0x0002)
                    return AromaticType.As2_MINUS;
                else if (q == 0) {
                    switch (binfo(atom)) {
                        case 0x0003: return AromaticType.As3;
                        case 0x0011: return AromaticType.As2;
                    }
                }
                else if (q == +1) {
                    switch (binfo(atom)) {
                        case 0x0012:
                        case 0x0102: return AromaticType.As3_PLUS;
                    }
                }
                break;

            case IElement.O:
                q = charge(atom);
                if (q == 0 && binfo(atom) == 0x0002)
                    return AromaticType.O2;
                if (q == 1 && binfo(atom) == 0x0011)
                    return AromaticType.O2_PLUS;
                break;
            case IElement.S:
                q = charge(atom);
                if (q == 0) {
                    switch (binfo(atom)) {
                        case 0x0012: return AromaticType.S3;
                        case 0x0002: return AromaticType.S2;
                        case 0x0020: return AromaticType.S2_CUML;
                        case 0x0102:
                            if (hasOxide(atom))
                                return AromaticType.S3_OXIDE;
                    }
                }
                if (q == 1) {
                    switch (binfo(atom)) {
                        case 0x0011: return AromaticType.S2_PLUS;
                        case 0x0003:
                            if (hasOxide(atom))
                                return AromaticType.S3_OXIDE_PLUS;
                            return AromaticType.S3_PLUS;
                    }
                }
                break;
            case IElement.Se:
                q = charge(atom);
                if (q == 0) {
                    switch (binfo(atom)) {
                        case 0x0002:
                            return AromaticType.Se2;
                        case 0x0012:
                            return AromaticType.Se3;
                        case 0x0102:
                            if (hasOxide(atom))
                                return AromaticType.Se3_OXIDE;

                    }
                }
                if (q == 1) {
                    switch (binfo(atom)) {
                        case 0x0011:
                            return AromaticType.Se2_PLUS;
                        case 0x0003:
                            if (hasOxide(atom))
                                return AromaticType.Se3_OXIDE_PLUS;
                    }
                }
                break;
            case IElement.Te:
                q = charge(atom);
                if (q == 0) {
                    if (binfo(atom) == 0x0002) {
                        return AromaticType.Te2;
                    }
                }
                if (q == 1) {
                    if (binfo(atom) == 0x0011) {
                        return AromaticType.Te2_PLUS;
                    }
                }
                break;
        }
        return AromaticType.UNKNOWN;
    }



}
