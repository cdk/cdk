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

import static org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;


/**
 * Assign roles to atoms based on electron counting. 
 * 
 * @author John Mayfield
 */
final class BasicRoleTyper {

    private enum AtomType {
        TentativeCarbonAcceptor(Role.Conjugated),
        TentativeCarbonDonor(Role.None),
        CarbonGroupAcceptor(Role.Acceptor),
        CarbonGroupDonor(Role.Donor),
        NitrogenGroupAcceptor(Role.Acceptor),
        NitrogenGroupDonor(Role.Donor),
        OxygenGroupAcceptor(Role.Acceptor),
        OxygenGroupDonor(Role.Donor),
        Sp2(Role.Conjugated),
        Other(Role.None);

        private final Role role;

        AtomType(Role role) {
            this.role = role;
        }
    }
    
    private BasicRoleTyper() {
        
    }

    static Role[] assignRoles(IAtomContainer mol,
                              boolean carbons) {
        
        // assign the initial types
        AtomType[] types = new AtomType[mol.getAtomCount()];
        for (int v = 0; v < mol.getAtomCount(); v++) {
            types[v] = type(mol.getAtom(v));
        }

        // augment roles (i.e. carbon donors, distance etc)
        if (carbons) {
            for (int v = 0; v < mol.getAtomCount(); v++) {
                
                if (types[v] == AtomType.TentativeCarbonAcceptor) {
                    types[v] = AtomType.CarbonGroupAcceptor;
                }
                
                // only Sp3 carbons adjacent to a donor, acceptor or conjugate
                // are included
                if (types[v] == AtomType.TentativeCarbonDonor) {
                    IAtom atom = mol.getAtom(v);
                    for (IBond bond : atom.bonds()) {
                        IAtom nbor = bond.getOther(atom);
                        if (types[nbor.getIndex()].role != Role.None) {
                            types[v] = AtomType.CarbonGroupDonor;
                            break;
                        }
                    }
                }
            }
        }
        
        // propagate the roles of each atom type
        Role[] roles = new Role[types.length];
        for (int v = 0; v < types.length; v++) {
            roles[v] = types[v].role;
        }

        // correct roles for double bonds that can't be moved, a double bond can
        // not be moved if one of the connected atoms has no role assigned
        for (int v = 0; v < mol.getAtomCount(); v++) {
            IAtom atom = mol.getAtom(v);
            for (IBond bond : atom.bonds()) {
                IAtom nbor = bond.getOther(atom);
                int w = nbor.getIndex();
                if (w < v) {
                    if ((roles[v] == Role.None || roles[w] == Role.None) && bond.getOrder() == IBond.Order.DOUBLE) {
                        roles[v] = Role.None;
                        roles[w] = Role.None;
                        break;
                    }
                }
            }
        }

        return roles;
    }

    private static AtomType type(IAtom atom) {

        int   charge    = atom.getFormalCharge();
        int   hydrogens = atom.getImplicitHydrogenCount();

        if (charge > 1 || charge < -1)
            return AtomType.Other;

        // count adjacent single (sigma) and double (pi) bonds, any other bond
        // triggers exit - we are not interested in these
        int sigma = hydrogens, pi = 0;
        for (IBond bond : atom.bonds()) {
            switch (bond.getOrder()) {
                case SINGLE:
                    sigma++;
                    break;
                case DOUBLE:
                    pi++;
                    break;
                default:
                    return AtomType.Other;
            }
        }

        final Elements elem = Elements.ofNumber(atom.getAtomicNumber());

        // given the element assign the preliminary sub type
        switch (elem) {

            case Carbon:

                if (charge == 0) {
                    // carbon is a potential H donor/acceptors
                    if (sigma == 2 && pi == 1)
                        return AtomType.TentativeCarbonAcceptor;
                    if (sigma == 4 && pi == 0 && hydrogens > 0)
                        return AtomType.TentativeCarbonDonor;
                }
                else {
                    //  charge is -1 or +1
                    if (sigma == 1 && pi == 1)
                        return AtomType.TentativeCarbonAcceptor;
                }

                break;

            // could also include phosphorus
            case Nitrogen: 
                
                if (charge == 0) {
                    if (sigma == 1 && pi == 1)
                        return AtomType.NitrogenGroupAcceptor;
                    else if (sigma == 3 && pi == 0 && hydrogens > 0)
                        return AtomType.NitrogenGroupDonor;
                    else if (sigma == 1 && pi == 2)
                        return AtomType.Other; // could allow in future
                }
                else if (charge == 1 && sigma == 2 && pi == 1) {
                    return AtomType.Sp2;
                }

                break;

            case Phosphorus:
            case Arsenic:
            case Antimony:
                if (charge == 0 && sigma == 1 && pi == 1
                        || charge == 1 && sigma == 2 && pi == 1)
                    return AtomType.Sp2;
                break;

            case Oxygen:
            case Sulfur:
            case Selenium:
            case Tellurium:

                if (charge != 0)
                    break;
                if (sigma == 0 && pi == 1)
                    return AtomType.OxygenGroupAcceptor;
                if (sigma == 2 && pi == 0 && hydrogens > 0)
                    return AtomType.OxygenGroupDonor;

                break;
        }

        return AtomType.Other;
    }

}
