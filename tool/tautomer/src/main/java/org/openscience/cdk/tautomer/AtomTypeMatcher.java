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
final class AtomTypeMatcher {

    private enum AtomType {
        PossibleCarbonAcceptor(Tautomers.Role.Conjugated),
        PossibleCarbonDonor(Tautomers.Role.None),
        CarbonGroupAcceptor(Tautomers.Role.Acceptor),
        CarbonGroupDonor(Tautomers.Role.Donor),
        NitrogenGroupAcceptor(Tautomers.Role.Acceptor),
        NitrogenGroupDonor(Tautomers.Role.Donor),
        NitroOxide(Tautomers.Role.Conjugated),
        OxygenGroupAcceptor(Tautomers.Role.Acceptor),
        OxygenGroupDonor(Tautomers.Role.Donor),
        Sp2(Tautomers.Role.Conjugated),
        Other(Tautomers.Role.None);

        private final Tautomers.Role role;

        AtomType(Tautomers.Role role) {
            this.role = role;
        }
    }
    
    private AtomTypeMatcher() {
        
    }

    static Tautomers.Role[] assignRoles(IAtomContainer mol) {
        return assignRoles(mol, EnumSet.noneOf(Tautomers.Type.class));
    }

    static Tautomers.Role[] assignRoles(IAtomContainer mol, Set<Tautomers.Type> opts) {
        
        // assign the initial types
        AtomType[] types = new AtomType[mol.getAtomCount()];
        for (int v = 0; v < mol.getAtomCount(); v++) {
            types[v] = type(mol.getAtom(v));
        }

        // augment roles (i.e. carbon donors, distance etc)
        if (opts.contains(Tautomers.Type.CARBON_SHIFTS)) {

            boolean[] near = new boolean[mol.getAtomCount()];
            for (int v=0; v<mol.getAtomCount(); v++) {
                Tautomers.Role role = types[v].role;
                if (role == Tautomers.Role.Donor || role == Tautomers.Role.Acceptor) {
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
                if (types[v] == AtomType.PossibleCarbonDonor) {
                    types[v] = near[v] ? AtomType.CarbonGroupDonor : AtomType.Other;
                } else if (types[v] == AtomType.PossibleCarbonAcceptor) {
                    if (near[v])
                        types[v] = AtomType.CarbonGroupAcceptor;
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
            if (roles[begIdx] == Tautomers.Role.None)
                roles[endIdx] = Tautomers.Role.None;
            else if (roles[endIdx] == Tautomers.Role.None)
                roles[begIdx] = Tautomers.Role.None;
            else if (types[begIdx] == AtomType.NitroOxide &&
                    types[endIdx] == AtomType.OxygenGroupAcceptor)
                roles[endIdx] = Tautomers.Role.None;
            else if (types[endIdx] == AtomType.NitroOxide &&
                    types[begIdx] == AtomType.OxygenGroupAcceptor)
                roles[begIdx] = Tautomers.Role.None;
        }

        return roles;
    }

    private static AtomType type(IAtom atom) {
        return type(atom, EnumSet.noneOf(Tautomers.Type.class));
    }

    private static AtomType type(IAtom atom, Set<Tautomers.Type> opts) {

        int   charge    = atom.getFormalCharge();
        int   implh = atom.getImplicitHydrogenCount();

        if (charge > 1 || charge < -1)
            return AtomType.Other;

        // count adjacent single (sigma) and double (pi) bonds, any other bond
        // triggers exit - we are not interested in these
        int binfo = implh;
        for (IBond bond : atom.bonds()) {
            switch (bond.getOrder()) {
                case SINGLE: binfo += 0x000001; break;
                case DOUBLE: binfo += 0x000100; break;
                case TRIPLE: binfo += 0x010000; break;
                default: return AtomType.Other;
            }
        }

        final Elements elem = Elements.ofNumber(atom.getAtomicNumber());

        // given the element assign the preliminary sub,type
        switch (elem) {

            case Carbon:

                if (charge == 0) {
                    // carbon is a potential H donor/acceptors
                    if (binfo == 0x0102)
                        return AtomType.PossibleCarbonAcceptor;
                    if (binfo == 0x000200 || binfo == 0x010001)
                        return AtomType.Other; // Sp1
                    if (binfo == 0x04 && implh > 0)
                        return AtomType.PossibleCarbonDonor;
                }
                else {
                    //  charge is -1 or +1
                    if (binfo == 0x0101)
                        return AtomType.PossibleCarbonAcceptor;
                }

                break;

            // could also include phosphorus?
            case Nitrogen: 
                
                if (charge == 0) {
                    if (binfo == 0x0101)
                        return AtomType.NitrogenGroupAcceptor;
                    else if (binfo == 0x0003 && implh > 0)
                        return AtomType.NitrogenGroupDonor;
                    else if (binfo == 0x0201)
                        return AtomType.NitroOxide; // N (V)
                } else if (charge == 1 && binfo == 0x0102) {
                    return AtomType.Sp2;
                }

                break;

            case Phosphorus:
            case Arsenic:
            case Antimony:
                if (charge == 0 && binfo == 0x0101 ||
                    charge == 1 && binfo == 0x0102)
                    return AtomType.Sp2;
                break;

            case Oxygen:
            case Sulfur:
            case Selenium:
            case Tellurium:
                if (charge != 0)
                    break;
                if (binfo == 0x0100)
                    return AtomType.OxygenGroupAcceptor;
                if (binfo == 0x0002 && implh > 0)
                    return AtomType.OxygenGroupDonor;

                break;
        }

        return AtomType.Other;
    }
}

