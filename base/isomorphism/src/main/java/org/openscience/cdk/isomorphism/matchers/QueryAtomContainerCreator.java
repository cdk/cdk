/*  Copyright (C) 2004-2008  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.isomorphism.matchers;

import java.util.Iterator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;

/**
 *@cdk.module   isomorphism
 * @cdk.githash
 */
public class QueryAtomContainerCreator {

    /**
     * Creates a QueryAtomContainer with SymbolQueryAtom's, AromaticQueryBond's and
     * OrderQueryBond's. If a IBond of the input <code>container</code> is flagged
     * aromatic, then it disregards bond order information and only match against
     * an aromatic target atom instead.
     *
     *@param  container  The AtomContainer that stands as model
     *@return            The new QueryAtomContainer created from container.
     */
    public static QueryAtomContainer createBasicQueryContainer(IAtomContainer container) {
        QueryAtomContainer queryContainer = new QueryAtomContainer(container.getBuilder());
        for (int i = 0; i < container.getAtomCount(); i++) {
            QueryAtom qatom = new QueryAtom(Expr.Type.ELEMENT,
                                            container.getAtom(i).getAtomicNumber());
            qatom.setSymbol(container.getAtom(i).getSymbol()); // backwards compatibility
            queryContainer.addAtom(qatom);
        }
        Iterator<IBond> bonds = container.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            int index1 = container.indexOf(bond.getBegin());
            int index2 = container.indexOf(bond.getEnd());
            if (bond.isAromatic()) {
                QueryBond qbond = new QueryBond(queryContainer.getAtom(index1),
                                                 queryContainer.getAtom(index2),
                                                 Expr.Type.IS_AROMATIC);
                queryContainer.addBond(qbond);
            } else {
                QueryBond qbond = new QueryBond(queryContainer.getAtom(index1),
                                                queryContainer.getAtom(index2),
                                                Expr.Type.ALIPHATIC_ORDER,
                                                bond.getOrder().numeric());
                qbond.setOrder(bond.getOrder()); // backwards compatibility
                queryContainer.addBond(qbond);
            }
        }
        return queryContainer;
    }

    /**
     * Creates a QueryAtomContainer with SymbolQueryAtom's and OrderQueryBond's. Unlike
     * <code>createBasicQueryContainer</code>, it disregards aromaticity flags.
     *
     * @param  container  The AtomContainer that stands as model
     * @return            The new QueryAtomContainer created from container.
     */
    public static QueryAtomContainer createSymbolAndBondOrderQueryContainer(IAtomContainer container) {
        QueryAtomContainer queryContainer = new QueryAtomContainer(container.getBuilder());
        for (int i = 0; i < container.getAtomCount(); i++) {
            QueryAtom qatom = new QueryAtom(Expr.Type.ELEMENT,
                                            container.getAtom(i).getAtomicNumber());
            qatom.setSymbol(container.getAtom(i).getSymbol()); // backwards compatibility
            queryContainer.addAtom(qatom);
        }
        Iterator<IBond> bonds = container.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();
            int index1 = container.indexOf(bond.getBegin());
            int index2 = container.indexOf(bond.getEnd());
            QueryBond qbond = new QueryBond(queryContainer.getAtom(index1),
                                            queryContainer.getAtom(index2),
                                            Expr.Type.ORDER,
                                            bond.getOrder().numeric());
            qbond.setOrder(bond.getOrder()); // backwards compatibility
            queryContainer.addBond(qbond);
        }
        return queryContainer;
    }

    /**
     *  Creates a QueryAtomContainer with SymbolAncChargeQueryAtom's and
     *  OrderQueryBond's.
     *
     *@param  container  The AtomContainer that stands as model
     *@return            The new QueryAtomContainer created from container.
     */
    public static QueryAtomContainer createSymbolAndChargeQueryContainer(IAtomContainer container) {
        QueryAtomContainer queryContainer = new QueryAtomContainer(container.getBuilder());
        for (int i = 0; i < container.getAtomCount(); i++) {
            Expr expr = new Expr(Expr.Type.ELEMENT, container.getAtom(i).getAtomicNumber());
            Integer q = container.getAtom(i).getFormalCharge();
            if (q == null) q = 0;
            expr.and(new Expr(Expr.Type.FORMAL_CHARGE, q));
            QueryAtom qatom = new QueryAtom(expr);
            // backwards compatibility
            qatom.setSymbol(container.getAtom(i).getSymbol());
            qatom.setFormalCharge(q);
            queryContainer.addAtom(qatom);
        }
        Iterator<IBond> bonds = container.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            int index1 = container.indexOf(bond.getBegin());
            int index2 = container.indexOf(bond.getEnd());
            if (bond.isAromatic()) {
                QueryBond qbond = new QueryBond(queryContainer.getAtom(index1),
                                                queryContainer.getAtom(index2),
                                                Expr.Type.IS_AROMATIC);
                queryContainer.addBond(qbond);
            } else {
                QueryBond qbond = new QueryBond(queryContainer.getAtom(index1),
                                                queryContainer.getAtom(index2),
                                                Expr.Type.ORDER,
                                                bond.getOrder().numeric());
                qbond.setOrder(bond.getOrder()); // backwards compatibility
                queryContainer.addBond(qbond);
            }
        }
        return queryContainer;
    }

    public static QueryAtomContainer createSymbolChargeIDQueryContainer(IAtomContainer container) {
        QueryAtomContainer queryContainer = new QueryAtomContainer(container.getBuilder());
        for (int i = 0; i < container.getAtomCount(); i++) {
            queryContainer.addAtom(new SymbolChargeIDQueryAtom(container.getAtom(i)));
        }
        Iterator<IBond> bonds = container.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            int index1 = container.indexOf(bond.getBegin());
            int index2 = container.indexOf(bond.getEnd());
            if (bond.isAromatic()) {
                QueryBond qbond = new QueryBond(queryContainer.getAtom(index1),
                                                queryContainer.getAtom(index2),
                                                Expr.Type.IS_AROMATIC);
                queryContainer.addBond(qbond);
            } else {
                QueryBond qbond = new QueryBond(queryContainer.getAtom(index1),
                                                queryContainer.getAtom(index2),
                                                Expr.Type.ORDER,
                                                bond.getOrder().numeric());
                qbond.setOrder(bond.getOrder()); // backwards compatibility
                queryContainer.addBond(qbond);
            }
        }
        return queryContainer;
    }

    /**
     *  Creates a QueryAtomContainer with AnyAtoms / Aromatic Atoms and OrderQueryBonds / AromaticQueryBonds.
     *  It uses the CDKConstants.ISAROMATIC flag to determine the aromaticity of container.
     *
     *@param  container    The AtomContainer that stands as model
     *@param  aromaticity  True = use aromaticity flags to create AtomaticAtoms and AromaticQueryBonds
     *@return              The new QueryAtomContainer created from container
     */
    public static QueryAtomContainer createAnyAtomContainer(IAtomContainer container, boolean aromaticity) {
        QueryAtomContainer queryContainer = new QueryAtomContainer(container.getBuilder());

        for (int i = 0; i < container.getAtomCount(); i++) {
            if (aromaticity && container.getAtom(i).getFlag(CDKConstants.ISAROMATIC)) {
                queryContainer.addAtom(new QueryAtom(Expr.Type.IS_AROMATIC));
            } else {
                queryContainer.addAtom(new QueryAtom(Expr.Type.TRUE));
            }
        }

        Iterator<IBond> bonds = container.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            int index1 = container.indexOf(bond.getBegin());
            int index2 = container.indexOf(bond.getEnd());
            if (aromaticity && bond.isAromatic()) {
                QueryBond qbond = new QueryBond(queryContainer.getAtom(index1),
                                                queryContainer.getAtom(index2),
                                                Expr.Type.IS_AROMATIC);
                queryContainer.addBond(qbond);
            } else {
                QueryBond qbond = new QueryBond(queryContainer.getAtom(index1),
                                                queryContainer.getAtom(index2),
                                                aromaticity ? Expr.Type.ALIPHATIC_ORDER : Expr.Type.ORDER,
                                                bond.getOrder().numeric());
                qbond.setOrder(bond.getOrder()); // backwards compatibility
                queryContainer.addBond(qbond);
            }
        }
        return queryContainer;
    }

    /**
     * Creates a QueryAtomContainer with wildcard atoms and wildcard bonds.
     * 
     * This method thus allows the user to search based only on connectivity.
     *
     * @param container   The AtomContainer that stands as the model
     * @param aromaticity If True, aromaticity flags are checked to create AromaticAtoms
     *                    and AromaticQueryBonds
     * @return The new QueryAtomContainer
     */
    public static QueryAtomContainer createAnyAtomAnyBondContainer(IAtomContainer container, boolean aromaticity) {
        QueryAtomContainer queryContainer = new QueryAtomContainer(container.getBuilder());

        for (int i = 0; i < container.getAtomCount(); i++) {
            if (aromaticity && container.getAtom(i).getFlag(CDKConstants.ISAROMATIC)) {
                queryContainer.addAtom(new QueryAtom(Expr.Type.IS_AROMATIC));
            } else {
                queryContainer.addAtom(new QueryAtom(Expr.Type.TRUE));
            }
        }

        Iterator<IBond> bonds = container.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            int index1 = container.indexOf(bond.getBegin());
            int index2 = container.indexOf(bond.getEnd());
            queryContainer.addBond(new QueryBond(queryContainer.getAtom(index1), queryContainer.getAtom(index2), Expr.Type.TRUE));
        }
        return queryContainer;
    }

    /**
     *  Creates a QueryAtomContainer with SymbolQueryAtom's and
     *  OrderQueryBond's. Each PseudoAtom will be replaced by a
     *  AnyAtom
     *
     *@param  container  The AtomContainer that stands as model
     *@return            The new QueryAtomContainer created from container.
     */
    public static QueryAtomContainer createAnyAtomForPseudoAtomQueryContainer(IAtomContainer container) {
        QueryAtomContainer queryContainer = new QueryAtomContainer(container.getBuilder());
        for (int i = 0; i < container.getAtomCount(); i++) {
            if (container.getAtom(i) instanceof IPseudoAtom) {
                queryContainer.addAtom(new QueryAtom(Expr.Type.TRUE));
            } else {
                queryContainer.addAtom(new QueryAtom(Expr.Type.ELEMENT,
                                                     container.getAtom(i).getAtomicNumber()));
            }

        }
        Iterator<IBond> bonds = container.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            int index1 = container.indexOf(bond.getBegin());
            int index2 = container.indexOf(bond.getEnd());
            if (bond.isAromatic()) {
                QueryBond qbond = new QueryBond(queryContainer.getAtom(index1),
                                                queryContainer.getAtom(index2),
                                                Expr.Type.IS_AROMATIC);
                queryContainer.addBond(qbond);
            } else {
                QueryBond qbond = new QueryBond(queryContainer.getAtom(index1),
                                                queryContainer.getAtom(index2),
                                                Expr.Type.ORDER,
                                                bond.getOrder().numeric());
                qbond.setOrder(bond.getOrder()); // backwards compatibility
                queryContainer.addBond(qbond);
            }
        }
        return queryContainer;
    }
}
