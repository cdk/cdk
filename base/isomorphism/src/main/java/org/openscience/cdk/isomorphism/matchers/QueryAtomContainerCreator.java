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

import org.openscience.cdk.AtomRef;
import org.openscience.cdk.BondRef;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IStereoElement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Utilities for creating queries from 'real' molecules. Note that most of this
 * functionality has now been replaced by the
 * {@link QueryAtomContainer#create(IAtomContainer, Expr.Type...)} method and
 * the documentation simply indicates what settings are used.
 */
public class QueryAtomContainerCreator {

    /**
     * Creates a QueryAtomContainer with the following settings:
     *
     * <pre>
     * QueryAtomContainer.create(container,
     *                           Expr.Type.ALIPHATIC_ELEMENT,
     *                           Expr.Type.AROMATIC_ELEMENT,
     *                           Expr.Type.IS_AROMATIC,
     *                           Expr.Type.ALIPHATIC_ORDER,
     *                           Expr.Type.STEREOCHEMISTRY);
     * </pre>
     *
     * @param container The AtomContainer that stands as model
     * @return The new QueryAtomContainer created from container.
     */
    public static QueryAtomContainer createBasicQueryContainer(IAtomContainer container) {
        return QueryAtomContainer.create(container,
                                         Expr.Type.ALIPHATIC_ELEMENT,
                                         Expr.Type.AROMATIC_ELEMENT,
                                         Expr.Type.IS_AROMATIC,
                                         Expr.Type.ALIPHATIC_ORDER,
                                         Expr.Type.STEREOCHEMISTRY);
    }

    /**
     * Creates a QueryAtomContainer with the following settings:
     *
     * <pre>
     * QueryAtomContainer.create(container,
     *                           Expr.Type.ELEMENT,
     *                           Expr.Type.ORDER);
     * </pre>
     *
     * @param container The AtomContainer that stands as model
     * @return The new QueryAtomContainer created from container.
     */
    public static QueryAtomContainer createSymbolAndBondOrderQueryContainer(IAtomContainer container) {
        return QueryAtomContainer.create(container,
                                         Expr.Type.ELEMENT,
                                         Expr.Type.ORDER);
    }

    /**
     * Creates a QueryAtomContainer with the following settings:
     *
     * <pre>
     * QueryAtomContainer.create(container,
     *                           Expr.Type.ELEMENT,
     *                           Expr.Type.FORMAL_CHARGE,
     *                           Expr.Type.IS_AROMATIC,
     *                           Expr.Type.ORDER);
     * </pre>
     *
     * @param container The AtomContainer that stands as model
     * @return The new QueryAtomContainer created from container.
     */
    public static QueryAtomContainer createSymbolAndChargeQueryContainer(IAtomContainer container) {
        return QueryAtomContainer.create(container,
                                         Expr.Type.ELEMENT,
                                         Expr.Type.FORMAL_CHARGE,
                                         Expr.Type.IS_AROMATIC,
                                         Expr.Type.ORDER);
    }

    public static QueryAtomContainer createSymbolChargeIDQueryContainer(IAtomContainer container) {
        QueryAtomContainer queryContainer = new QueryAtomContainer(container.getBuilder());
        for (int i = 0; i < container.getAtomCount(); i++) {
            queryContainer.addAtom(new SymbolChargeIDQueryAtom(container.getAtom(i)));
        }
        Iterator<IBond> bonds = container.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond   = bonds.next();
            int   index1 = container.indexOf(bond.getBegin());
            int   index2 = container.indexOf(bond.getEnd());
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
     * Creates a QueryAtomContainer with the following settings:
     *
     * <pre>
     * // aromaticity = true
     * QueryAtomContainer.create(container,
     *                           Expr.Type.IS_AROMATIC,
     *                           Expr.Type.ALIPHATIC_ORDER);
     * // aromaticity = false
     * QueryAtomContainer.create(container,
     *                           Expr.Type.ORDER);
     * </pre>
     *
     * @param container   The AtomContainer that stands as model
     * @param aromaticity option flag
     * @return The new QueryAtomContainer created from container.
     */
    public static QueryAtomContainer createAnyAtomContainer(IAtomContainer container, boolean aromaticity) {
        if (aromaticity)
            return QueryAtomContainer.create(container,
                                             Expr.Type.IS_AROMATIC,
                                             Expr.Type.ALIPHATIC_ORDER);
        else
            return QueryAtomContainer.create(container,
                                             Expr.Type.ORDER);
    }

    /**
     * Creates a QueryAtomContainer with the following settings:
     *
     * <pre>
     * // aromaticity = true
     * QueryAtomContainer.create(container,
     *                           Expr.Type.IS_AROMATIC);
     * // aromaticity = false
     * QueryAtomContainer.create(container);
     * </pre>
     *
     * @param container   The AtomContainer that stands as model
     * @param aromaticity option flag
     * @return The new QueryAtomContainer created from container.
     */
    public static QueryAtomContainer createAnyAtomAnyBondContainer(IAtomContainer container, boolean aromaticity) {
        if (aromaticity)
            return QueryAtomContainer.create(container, Expr.Type.IS_AROMATIC);
        else
            return QueryAtomContainer.create(container);
    }

    /**
     * Creates a QueryAtomContainer with the following settings:
     *
     * <pre>
     * QueryAtomContainer.create(container,
     *                           Expr.Type.ELEMENT,
     *                           Expr.Type.IS_AROMATIC,
     *                           Expr.Type.ALIPHATIC_ORDER);
     * </pre>
     *
     * @param container The AtomContainer that stands as model
     * @return The new QueryAtomContainer created from container.
     */
    public static QueryAtomContainer createAnyAtomForPseudoAtomQueryContainer(IAtomContainer container) {
        return QueryAtomContainer.create(container,
                                         Expr.Type.ELEMENT,
                                         Expr.Type.IS_AROMATIC,
                                         Expr.Type.ALIPHATIC_ORDER);
    }

    static boolean isSimpleHydrogen(Expr expr) {
        switch (expr.type()) {
            case ELEMENT:
            case ALIPHATIC_ELEMENT:
                return expr.value() == 1;
            default:
                return false;
        }
    }

    public static IAtomContainer suppressQueryHydrogens(IAtomContainer mol) {

        // pre-checks
        for (IAtom atom : mol.atoms()) {
            if (!(AtomRef.deref(atom) instanceof QueryAtom))
                throw new IllegalArgumentException("Non-query atoms found!");
        }
        for (IBond bond : mol.bonds()) {
            if (!(BondRef.deref(bond) instanceof QueryBond))
                throw new IllegalArgumentException("Non-query bonds found!");
        }

        Map<IChemObject,IChemObject> plainHydrogens = new HashMap<>();
        for (IAtom atom : mol.atoms()) {
            int hcnt = 0;
            for (IAtom nbor : mol.getConnectedAtomsList(atom)) {
                QueryAtom qnbor = (QueryAtom) AtomRef.deref(nbor);
                if (mol.getConnectedBondsCount(nbor) == 1 &&
                    isSimpleHydrogen(qnbor.getExpression())) {
                    hcnt++;
                    plainHydrogens.put(nbor, atom);
                }
            }
            if (hcnt > 0) {
                QueryAtom qatom = (QueryAtom) AtomRef.deref(atom);
                Expr e = qatom.getExpression();
                Expr hexpr = new Expr();
                for (int i = 0; i < hcnt; i++)
                    hexpr.and(new Expr(Expr.Type.TOTAL_H_COUNT, i).negate());
                e.and(hexpr);
            }
        }

        // nothing to do
        if (plainHydrogens.isEmpty())
            return mol;

        IAtomContainer res = new QueryAtomContainer(mol.getBuilder());
        for (IAtom atom : mol.atoms()) {
            if (!plainHydrogens.containsKey(atom))
                res.addAtom(atom);
        }
        for (IBond bond : mol.bonds()) {
            if (!plainHydrogens.containsKey(bond.getBegin()) &&
                !plainHydrogens.containsKey(bond.getEnd()))
                res.addBond(bond);
        }
        for (IStereoElement se : mol.stereoElements()) {
            res.addStereoElement(se.map(plainHydrogens));
        }

        return res;
    }
}
