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

package org.openscience.cdk.depict;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smarts.SmartsPattern;
import org.openscience.cdk.smiles.SmilesParser;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BondAlignment {

    static int targetBondOrder(Expr expr) {
        int lft, rgt;
        switch (expr.type()) {
            case ALIPHATIC_ORDER:
            case ORDER:
                return expr.value();
            case OR:
                lft = targetBondOrder(expr.left());
                rgt = targetBondOrder(expr.right());
                if (lft == rgt) return lft;
                if (lft == -1 || rgt == -1) return -1;
                else if (lft == 0) return rgt;
                else if (rgt == 0) return lft;
                return -1;
            case AND:
                lft = targetBondOrder(expr.left());
                rgt = targetBondOrder(expr.right());
                if (lft == rgt) return lft;
                if (lft == -1 || rgt == -1) return -1;
                else if (lft == 0) return rgt;
                else if (rgt == 0) return lft;
                return -1;
            case NOT:
                lft = targetBondOrder(expr.left());
                if (lft == 0) return 0;
                else return -1;
            default:
                return 0;
        }
    }

    private static void flipBondOrder(IBond bond) {
        if (bond.getOrder() == IBond.Order.DOUBLE)
            bond.setOrder(IBond.Order.SINGLE);
        else if (bond.getOrder() == IBond.Order.SINGLE)
            bond.setOrder(IBond.Order.DOUBLE);
    }

//    private static void walkAlternatingPath()

    private static boolean isAcceptable(int target, IBond bond) {
        if (target == 1)
            return bond.getOrder() == IBond.Order.SINGLE;
        else if (target == 2)
            return bond.getOrder() == IBond.Order.DOUBLE;
        return true;
    }

    private static boolean isCandidate(int target, IBond bond) {
        if (target == 1)
            return bond.getOrder() == IBond.Order.DOUBLE;
        else if (target == 2)
            return bond.getOrder() == IBond.Order.SINGLE;
        return true;
    }

    private static boolean walkAlternatingBond(IAtom fst, IAtom atm, IBond prev, int[] target) {

        boolean result = false;
        for (IBond bond : atm.bonds()) {
            IAtom nbor = bond.getOther(atm);
            if (bond.getFlag(IChemObject.VISITED))
                continue;
            if (!bond.isAromatic() || bond.getOrder() == prev.getOrder())
                continue;
            if (!isCandidate(target[bond.getIndex()], bond))
                continue;
//            System.err.println(atm.getSymbol() + (atm.getIndex()+1) + " " + nbor.getSymbol() + (nbor.getIndex()+1) + " " + bond.getOrder() + " " + bond.getFlag(IChemObject.VISITED));
            if (nbor == fst) {
                flipBondOrder(bond);
                return true;
            }

            bond.set(IChemObject.VISITED);
            if (walkAlternatingBond(fst, nbor, bond, target)) {
                flipBondOrder(bond);
                result = true;
                break;
            }
            bond.clear(IChemObject.VISITED);
        }
        return result;
    }

    private static boolean adjustKekulisation(IAtomContainer mol, int[] target) {
        boolean result = true;
        for (IBond b : mol.bonds())
            b.clear(IChemObject.VISITED);
        for (IBond bond : mol.bonds()) {
            if (isAcceptable(target[bond.getIndex()], bond))
                continue;
            bond.set(IChemObject.VISITED);
            if (walkAlternatingBond(bond.getBegin(), bond.getEnd(), bond, target))
                flipBondOrder(bond);
            else
                result = false;
            bond.clear(IChemObject.VISITED);
        }
//        System.err.println("result=" + result);
//        for (IBond bond : mol.bonds()) {
//            System.err.println(target[bond.getIndex()] + " " + bond.getOrder());
//        }
        return result;
    }

    public static boolean alignBondOrders(IAtomContainer mol, Map<IBond, IBond> bondMap) {
        int[] target = new int[mol.getBondCount()];
        for (Map.Entry<IBond, IBond> e : bondMap.entrySet()) {
            Expr expression = ((QueryBond) e.getKey()).getExpression();
            int expected = targetBondOrder(expression);
            if (e.getValue().isAromatic() && expected >= 0 && expected <= 2)
                target[e.getValue().getIndex()] = expected;
            else
                target[e.getValue().getIndex()] = -1;
        }
        return adjustKekulisation(mol, target);
    }

    public static boolean alignBondOrders(IAtomContainer mol, Pattern pattern) {
        for (Map<IBond, IBond> map : pattern.matchAll(mol).toBondMap()) {
            return alignBondOrders(mol, map);
        }
        return true;
    }

    public static void main(String[] args) throws CDKException, IOException {
//         SmartsPattern pattern = SmartsPattern.create("[#6D>2x>2]12:,=@[#6R]:,-@[#6R]:,=@[#6R]:,-@[#6R]:,=@[#6D>2x>2]:,-@1:,-@[#6;A,!i,D3;R]:,-@[#6D>2x>2]3:,=@[#6D>2x>2](:,-@[#6;A,!i,D3;R]:,-@2):,-@[#6R]:,=@[#6R]:,-@[#6R]:,=@[#6R]:,-@3");
//        SmartsPattern pattern = SmartsPattern.create("[#6D>2x>2]12:,=@[#6D>2x>2](:,-@[#6R]:,=@[#6R]:,-@[#6R]:,=@[#6R]:,-@1):,-@[#6;A,!i,D3;R]:,-@[#6D>2x>2]3:,=@[#6D>2x>2](:,-@[#6;A,!i,D3;R]:,-@2):,-@[#6R]:,=@[#6R]:,-@[#6R]:,=@[#6R]:,-@3");
//        SmartsPattern pattern = SmartsPattern.create("[#6iR]1:,=@[#6iR]:,-@[#6;A,!i,D3;R]:,-@[#6D>2x>2]2:,=@[#6R]:,-@[#6R]:,=@[#6R]:,-@[#6R]:,=@[#6D>2x>2]:,-@2:,-@[#6;A,!i,D3;R]:,-@1");
//        SmartsPattern pattern = SmartsPattern.create("[#6D>2x>2]12:,-@[#6R]:,=@[#6R]:,-@[#6R]:,=@[#6R]:,-@[#6D>2x>2]:,=@1:,-@[#6;A,!i,D3;R]:,-@[#6D>2x>2]3:,=@[#6D>2x>2](:,-@[#6;A,!i,D3;R]:,-@2):,-@[#6R]:,=@[#6R]:,-@[#6R]:,=@[#6R]:,-@3");
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());

//        List<IAtomContainer> mols = new ArrayList<>();
//        Set<IChemObject> highlight = new HashSet<>();
//        for (String smi : new String[]{
////                "Nc1ccc(N)c2ccccc12\tCHEMBL3183071",
////                "c1ccc2c(c1)Cc1ccccc1C2\tCHEMBL125337",
////                "NC1c2ccccc2Cc2ccccc21\tCHEMBL160610",
////                "O=C1c2ccccc2Cc2ccccc21\tCHEMBL124440",
////                "Nc1ccc(N)c2ccccc12\tCHEMBL3183071",
////                "Nc1ccc(O)c2ccccc12\tCHEMBL576321",
////                "O=C1C=CC(=O)c2ccccc21\tCHEMBL55934"
//                "CCNc1ccc(O)c2ccccc12"
//        }) {
//            IAtomContainer mol = smipar.parseSmiles(smi);
//            for (Map<IBond, IBond> map : pattern.matchAll(mol).toBondMap()) {
//                if (!alignBondOrders(mol, map)) {
//                    mol.setTitle("Computer says no!");
//                } else {
//                    mol.setTitle(null);
//                }
//                highlight.addAll(map.values());
//                break;
//            }
//            mols.add(mol);
//        }
//        new DepictionGenerator().withAtomColors()
//                                .withZoom(2)
//                                .withMolTitle()
//                                .withHighlight(highlight, new Color(0xB2E7E7))
//                                .withOuterGlowHighlight()
//                                .depict(mols, mols.size(), 1)
//                                .writeTo("/tmp/tmp.svg");
    }

}
