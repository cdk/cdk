/*
 * Copyright (c) 2024 John Mayfield
 *   
 * Contact: cdk-devel@lists.sourceforge.net
 *   
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above 
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.tautomer;

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.EnumSet;
import java.util.Set;


/**
 * Assign tautomer atom types/roles to atoms based on electron counting.
 * 
 * @author John Mayfield
 */
final class TautTypeMatcher {

    private enum TautType {
        PossibleCarbonAcceptor(Tautomers.Role.C),
        PossibleCarbonDonor(Tautomers.Role.X),
        CarbonGroupAcceptor(Tautomers.Role.A),
        CarbonGroupDonor(Tautomers.Role.D),
        NitrogenGroupAcceptor(Tautomers.Role.A),
        NitrogenGroupDonor(Tautomers.Role.D),
        NitroOxide(Tautomers.Role.C),
        OxygenGroupAcceptor(Tautomers.Role.A),
        OxygenGroupDonor(Tautomers.Role.D),
        Sp2(Tautomers.Role.C),
        Other(Tautomers.Role.X);

        private final Tautomers.Role role;

        TautType(Tautomers.Role role) {
            this.role = role;
        }
    }
    
    private TautTypeMatcher() {
        
    }

    static Tautomers.Role[] assignRoles(IAtomContainer mol) {
        return assignRoles(mol, EnumSet.noneOf(Tautomers.Type.class));
    }

    static Tautomers.Role[] assignRoles(IAtomContainer mol, Set<Tautomers.Type> opts) {
        
        // assign the initial types
        TautType[] types = new TautType[mol.getAtomCount()];
        for (int v = 0; v < mol.getAtomCount(); v++) {
            types[v] = type(mol.getAtom(v));
        }

        // augment roles (i.e. carbon donors, distance etc)
        if (opts.contains(Tautomers.Type.CARBON_SHIFTS)) {

            boolean[] near = new boolean[mol.getAtomCount()];
            for (int v=0; v<mol.getAtomCount(); v++) {
                Tautomers.Role role = types[v].role;
                if (role == Tautomers.Role.D || role == Tautomers.Role.A) {
                    IAtom hetero = mol.getAtom(v);
                    for (IBond bond : hetero.bonds()) {
                        IAtom nbor = bond.getOther(hetero);
                        near[nbor.getIndex()] = true;
                        for (IBond bond2 : nbor.bonds())
                            near[bond2.getOther(nbor).getIndex()] = true;
                    }
                }
            }

            for (int v = 0; v < mol.getAtomCount(); v++) {
                IAtom atom = mol.getAtom(v);
                // only Sp3 carbons adjacent to a donor, acceptor or conjugated
                // atom are included
                if (types[v] == TautType.PossibleCarbonDonor) {
                    types[v] = near[v] ? TautType.CarbonGroupDonor : TautType.Other;
                } else if (types[v] == TautType.PossibleCarbonAcceptor) {
                    if (near[v])
                        types[v] = TautType.CarbonGroupAcceptor;
                }
            }
        }

        // propagate the roles of each atom type
        Tautomers.Role[] roles = new Tautomers.Role[types.length];
        for (int v = 0; v < types.length; v++) {
            roles[v] = types[v].role;
        }

        // correct roles for double bonds that can't be moved, a double bond can
        // not be moved if one of the connected atoms has no role assigned
        // or nitro groups =O
        for (IBond bond : mol.bonds()) {
            if (bond.getOrder() != IBond.Order.DOUBLE)
                continue;
            int begIdx = bond.getBegin().getIndex();
            int endIdx = bond.getEnd().getIndex();
            if (roles[begIdx] == Tautomers.Role.X)
                roles[endIdx] = Tautomers.Role.X;
            else if (roles[endIdx] == Tautomers.Role.X)
                roles[begIdx] = Tautomers.Role.X;
            else if (types[begIdx] == TautType.NitroOxide &&
                    types[endIdx] == TautType.OxygenGroupAcceptor)
                roles[endIdx] = Tautomers.Role.X;
            else if (types[endIdx] == TautType.NitroOxide &&
                    types[begIdx] == TautType.OxygenGroupAcceptor)
                roles[begIdx] = Tautomers.Role.X;
        }

        return roles;
    }

    private static TautType type(IAtom atom) {
        return type(atom, EnumSet.noneOf(Tautomers.Type.class));
    }

    private static TautType type(IAtom atom, Set<Tautomers.Type> opts) {

        int   charge = atom.getFormalCharge();
        int   implh  = atom.getImplicitHydrogenCount();

        if (charge > 1 || charge < -1)
            return TautType.Other;

        // count adjacent single (sigma) and double (pi) bonds, any other bond
        // triggers exit - we are not interested in these
        int binfo = implh;
        for (IBond bond : atom.bonds()) {
            switch (bond.getOrder()) {
                case SINGLE: binfo += 0x000001; break;
                case DOUBLE: binfo += 0x000100; break;
                case TRIPLE: binfo += 0x010000; break;
                default:
                    return TautType.Other;
            }
        }

        final Elements elem = Elements.ofNumber(atom.getAtomicNumber());

        // given the element assign the preliminary sub,type
        switch (elem) {

            case Carbon:

                if (charge == 0) {
                    // carbon is a potential H donor/acceptors
                    if (binfo == 0x0102)
                        return TautType.PossibleCarbonAcceptor;
                    if (binfo == 0x000200 || binfo == 0x010001)
                        return TautType.Other; // Sp1
                    if (binfo == 0x04 && implh > 0)
                        return TautType.PossibleCarbonDonor;
                }
                else {
                    //  charge is -1 or +1
                    if (binfo == 0x0101)
                        return TautType.PossibleCarbonAcceptor;
                }

                break;

            // could also include phosphorus?
            case Nitrogen: 
                
                if (charge == 0) {
                    if (binfo == 0x0101)
                        return TautType.NitrogenGroupAcceptor;
                    else if (binfo == 0x0003 && implh > 0)
                        return TautType.NitrogenGroupDonor;
                    else if (binfo == 0x0201)
                        return TautType.NitroOxide; // N (V)
                } else if (charge == 1 && binfo == 0x0102) {
                    return TautType.Sp2;
                }

                break;

            case Phosphorus:
            case Arsenic:
            case Antimony:
                if (charge == 0 && binfo == 0x0101 ||
                    charge == 1 && binfo == 0x0102)
                    return TautType.Sp2;
                break;

            case Oxygen:
            case Sulfur:
            case Selenium:
            case Tellurium:
                if (charge != 0)
                    break;
                if (binfo == 0x0100)
                    return TautType.OxygenGroupAcceptor;
                if (binfo == 0x0002 && implh > 0)
                    return TautType.OxygenGroupDonor;

                break;
        }

        return TautType.Other;
    }
}

