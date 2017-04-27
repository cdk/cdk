package org.openscience.cdk.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * @author maclean
 * @cdk.module test-group
 */
public class AtomContainerPrinter {

    public static void print(IAtomContainer atomContainer) {
        System.out.println(AtomContainerPrinter.toString(atomContainer));
    }

    public static String toString(IAtomContainer atomContainer) {
        return AtomContainerPrinter.toString(atomContainer, new Permutation(atomContainer.getAtomCount()));
    }

    public static String toString(IAtomContainer atomContainer, boolean sortEdges) {
        Permutation identity = new Permutation(atomContainer.getAtomCount());
        return toString(atomContainer, identity, sortEdges);
    }

    public static String toString(IAtomContainer atomContainer, Permutation permutation) {
        return toString(atomContainer, permutation, false); // don't sort by default?
    }

    public static String toString(IAtomContainer atomContainer, Permutation permutation, boolean sortEdges) {
        StringBuffer sb = new StringBuffer();
        int atomCount = atomContainer.getAtomCount();
        IAtom[] pAtoms = new IAtom[atomCount];
        for (int i = 0; i < atomCount; i++) {
            pAtoms[permutation.get(i)] = atomContainer.getAtom(i);
        }
        for (int i = 0; i < atomCount; i++) {
            sb.append(pAtoms[i].getSymbol()).append(i);
        }
        sb.append(" ");

        int i = 0;
        List<String> edgeStrings = null;
        if (sortEdges) {
            edgeStrings = new ArrayList<String>();
        }
        for (IBond bond : atomContainer.bonds()) {
            int a0 = atomContainer.indexOf(bond.getBeg());
            int a1 = atomContainer.indexOf(bond.getEnd());
            int pA0 = permutation.get(a0);
            int pA1 = permutation.get(a1);
            char o = bondOrderToChar(bond.getOrder());
            if (sortEdges) {
                String edgeString;
                if (pA0 < pA1) {
                    edgeString = pA0 + ":" + pA1 + "(" + o + ")";
                } else {
                    edgeString = pA1 + ":" + pA0 + "(" + o + ")";
                }
                edgeStrings.add(edgeString);
            } else {
                if (pA0 < pA1) {
                    sb.append(pA0 + ":" + pA1 + "(" + o + ")");
                } else {
                    sb.append(pA1 + ":" + pA0 + "(" + o + ")");
                }
            }
            if (!sortEdges && i < atomContainer.getBondCount() - 1) {
                sb.append(',');
            }
            i++;
        }
        if (sortEdges) {
            Collections.sort(edgeStrings);
            i = 0;
            for (String edgeString : edgeStrings) {
                sb.append(edgeString);
                if (i < atomContainer.getBondCount() - 1) {
                    sb.append(",");
                }
            }
        }
        return sb.toString();
    }

    private static char bondOrderToChar(IBond.Order order) {
        switch (order) {
            case SINGLE:
                return '1';
            case DOUBLE:
                return '2';
            case TRIPLE:
                return '3';
            case QUADRUPLE:
                return '4';
            case UNSET:
                return '?';
            default:
                return '?';
        }
    }

    private static IBond.Order charToBondOrder(char orderChar) {
        switch (orderChar) {
            case '1':
                return IBond.Order.SINGLE;
            case '2':
                return IBond.Order.DOUBLE;
            case '3':
                return IBond.Order.TRIPLE;
            case '4':
                return IBond.Order.QUADRUPLE;
            case '?':
                return IBond.Order.UNSET;
            default:
                return IBond.Order.UNSET;
        }
    }

    public static IAtomContainer fromString(String acpString, IChemObjectBuilder builder) {
        int gapIndex = acpString.indexOf(' ');
        if (gapIndex == -1) {
            gapIndex = acpString.length();
        }

        IAtomContainer atomContainer = builder.newInstance(IAtomContainer.class);
        String elementString = acpString.substring(0, gapIndex);
        // skip the atom number, as this is just a visual convenience
        for (int index = 0; index < elementString.length(); index += 2) {
            String elementSymbol = String.valueOf(elementString.charAt(index));
            atomContainer.addAtom(builder.newInstance(IAtom.class, elementSymbol));
        }

        // no bonds
        if (gapIndex >= acpString.length() - 1) {
            return atomContainer;
        }

        String bondString = acpString.substring(gapIndex + 1);
        for (String bondPart : bondString.split(",")) {
            int colonIndex = bondPart.indexOf(':');
            int openBracketIndex = bondPart.indexOf('(');
            int closeBracketIndex = bondPart.indexOf(')');
            int a0 = Integer.parseInt(bondPart.substring(0, colonIndex));
            int a1 = Integer.parseInt(bondPart.substring(colonIndex + 1, openBracketIndex));
            char o = bondPart.substring(openBracketIndex + 1, closeBracketIndex).charAt(0);
            atomContainer.addBond(a0, a1, charToBondOrder(o));
        }
        return atomContainer;
    }
}
