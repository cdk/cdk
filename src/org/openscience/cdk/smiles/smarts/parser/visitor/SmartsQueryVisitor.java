package org.openscience.cdk.smiles.smarts.parser.visitor;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.SymbolQueryAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AliphaticAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.DegreeAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.ImplicitHCountAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.LogicalOperator;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;
import org.openscience.cdk.isomorphism.matchers.smarts.TotalHCountAtom;
import org.openscience.cdk.smiles.smarts.parser.ASTAliphatic;
import org.openscience.cdk.smiles.smarts.parser.ASTAnyAtom;
import org.openscience.cdk.smiles.smarts.parser.ASTAromatic;
import org.openscience.cdk.smiles.smarts.parser.ASTAtomicMass;
import org.openscience.cdk.smiles.smarts.parser.ASTAtomicNumber;
import org.openscience.cdk.smiles.smarts.parser.ASTBond;
import org.openscience.cdk.smiles.smarts.parser.ASTCharge;
import org.openscience.cdk.smiles.smarts.parser.ASTChirality;
import org.openscience.cdk.smiles.smarts.parser.ASTDegree;
import org.openscience.cdk.smiles.smarts.parser.ASTElement;
import org.openscience.cdk.smiles.smarts.parser.ASTExplicitAtom;
import org.openscience.cdk.smiles.smarts.parser.ASTExplicitHighAndBond;
import org.openscience.cdk.smiles.smarts.parser.ASTExplicitHighAndExpression;
import org.openscience.cdk.smiles.smarts.parser.ASTGroup;
import org.openscience.cdk.smiles.smarts.parser.ASTImplicitHCount;
import org.openscience.cdk.smiles.smarts.parser.ASTImplicitHighAndBond;
import org.openscience.cdk.smiles.smarts.parser.ASTImplicitHighAndExpression;
import org.openscience.cdk.smiles.smarts.parser.ASTLogicalExpression;
import org.openscience.cdk.smiles.smarts.parser.ASTLowAndBond;
import org.openscience.cdk.smiles.smarts.parser.ASTLowAndExpression;
import org.openscience.cdk.smiles.smarts.parser.ASTNotBond;
import org.openscience.cdk.smiles.smarts.parser.ASTNotExpression;
import org.openscience.cdk.smiles.smarts.parser.ASTOrBond;
import org.openscience.cdk.smiles.smarts.parser.ASTOrExpression;
import org.openscience.cdk.smiles.smarts.parser.ASTReaction;
import org.openscience.cdk.smiles.smarts.parser.ASTRecursiveSmartsExpression;
import org.openscience.cdk.smiles.smarts.parser.ASTRingConnectivity;
import org.openscience.cdk.smiles.smarts.parser.ASTRingMembership;
import org.openscience.cdk.smiles.smarts.parser.ASTRingSize;
import org.openscience.cdk.smiles.smarts.parser.ASTSimpleBond;
import org.openscience.cdk.smiles.smarts.parser.ASTSmarts;
import org.openscience.cdk.smiles.smarts.parser.ASTStart;
import org.openscience.cdk.smiles.smarts.parser.ASTTotalConnectivity;
import org.openscience.cdk.smiles.smarts.parser.ASTTotalHCount;
import org.openscience.cdk.smiles.smarts.parser.ASTValence;
import org.openscience.cdk.smiles.smarts.parser.Node;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParserConstants;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParserVisitor;
import org.openscience.cdk.smiles.smarts.parser.SimpleNode;
import org.openscience.cdk.tools.LoggingTool;

public class SmartsQueryVisitor implements SMARTSParserVisitor {
    private final static LoggingTool logger = new LoggingTool(
            SmartsQueryVisitor.class);

    public Object visit(SimpleNode node, Object data) {
        return null;
    }

    public Object visit(ASTStart node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    // TODO: No QueryReaction API
    public Object visit(ASTReaction node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    // TODO: No SmartsGroup API
    public Object visit(ASTGroup node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    public Object visit(ASTSmarts node, Object data) {
        IQueryAtomContainer atomContainer = new QueryAtomContainer();
        IQueryAtom atom = null;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child instanceof ASTLogicalExpression) {
                Object obj = child.jjtAccept(this, data);
                if (obj instanceof SMARTSAtom) {
                    atom = (SMARTSAtom) atom;
                    atomContainer.addAtom((SMARTSAtom) obj);
                } else { // cases where recursive smarts occurs
                    logger.info("Recursive Smarts not supported");
                }
            } else if (child instanceof ASTBond) {
                i++; // read two node
                ASTSmarts nextChild = (ASTSmarts) node.jjtGetChild(i);
                // the next child should be another smarts expression
                IQueryAtomContainer childContainer = (IQueryAtomContainer) nextChild
                        .jjtAccept(this, data);
                SMARTSAtom atom2 = (SMARTSAtom) childContainer.getAtom(0);
                SMARTSBond bond = (SMARTSBond) child.jjtAccept(this,
                        new IAtom[] { atom, atom2 });
                // TODO: Add all atoms in the child container. Is this correct?
                atomContainer.addBond(bond);
                atomContainer.add(childContainer);
            } else if (child instanceof ASTSmarts) { // implicit single bond
                // be another smarts. Connect them
                IQueryAtomContainer childContainer = (IQueryAtomContainer) child
                        .jjtAccept(this, data);
                SMARTSAtom atom2 = (SMARTSAtom) childContainer.getAtom(0);
                // there is a possibility that this is an implicit aromatic bond!
                SMARTSBond bond;
                if (isAromatic(atom) && isAromatic(atom2)) {
                    bond = new AromaticQueryBond();
                    bond.setAtoms(new IAtom[] { atom, atom2 });
                } else {
                    bond = new OrderQueryBond(atom, atom2, 1.0d);
                }

                // TODO: Add all atoms in the child container. Is this correct?
                atomContainer.addBond(bond);
                atomContainer.add(childContainer);
            } else if (child instanceof ASTExplicitAtom) {
                atom = (IQueryAtom) child.jjtAccept(this, data);
                atomContainer.addAtom(atom);
            }
        }
        return atomContainer;
    }

    // TODO: need full implementation of Logical Bond API
    public Object visit(ASTNotBond node, Object data) {
        if (node.getType() == SMARTSParserConstants.NOT) {
            logger.info("Logical Bond not supported");
        }
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    // TODO: need full implementation of Logical Bond API
    public Object visit(ASTBond node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    // TODO: need full implementation of Logical Bond API
    public Object visit(ASTImplicitHighAndBond node, Object data) {
        if (node.jjtGetNumChildren() > 1) {
            logger.info("Logical Bond not supported");
        }
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    // TODO: need full implementation of Logical Bond API
    public Object visit(ASTLowAndBond node, Object data) {
        if (node.jjtGetNumChildren() > 1) {
            logger.info("Logical Bond not supported");
        }
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    // TODO: need full implementation of Logical Bond API
    public Object visit(ASTOrBond node, Object data) {
        if (node.jjtGetNumChildren() > 1) {
            logger.info("Logical Bond not supported");
        }
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    // TODO: need full implementation of Logical Bond API
    public Object visit(ASTExplicitHighAndBond node, Object data) {
        if (node.jjtGetNumChildren() > 1) {
            logger.info("Logical Bond not supported");
        }
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    private boolean isAromatic(IAtom atom) {
        if (atom instanceof AromaticAtom) {
            return true;
        }
        if (atom instanceof SMARTSAtom) {
            //TODO: check the atoms recursively. But right now I am very lazy :)
            return false;
        }
        return false;
    }
    
    public Object visit(ASTSimpleBond node, Object data) {
        IAtom[] atoms = (IAtom[]) data;
        SMARTSBond bond = null;
        switch (node.getBondType()) {
        case SMARTSParserConstants.S_BOND:
            bond = new OrderQueryBond((IQueryAtom) atoms[0],
                    (IQueryAtom) atoms[1], 1.0d);
            break;
        case SMARTSParserConstants.D_BOND:
            bond = new OrderQueryBond((IQueryAtom) atoms[0],
                    (IQueryAtom) atoms[1], 2.0d);
            break;
        case SMARTSParserConstants.T_BOND:
            bond = new OrderQueryBond((IQueryAtom) atoms[0],
                    (IQueryAtom) atoms[1], 3.0d);
            break;
        case SMARTSParserConstants.ANY_BOND:
            bond = new AnyOrderQueryBond();
            bond.setAtoms(atoms);
            break;
        case SMARTSParserConstants.AR_BOND:
            bond = new AromaticQueryBond();
            bond.setAtoms(atoms);
            break;
        case SMARTSParserConstants.R_BOND:
        case SMARTSParserConstants.UP_S_BOND:
        case SMARTSParserConstants.DN_S_BOND:
        case SMARTSParserConstants.UP_OR_UNSPECIFIED_S_BOND:
        case SMARTSParserConstants.DN_OR_UNSPECIFIED_S_BOND:
            logger.info("Bond type not supported. Deem as single bond");
            bond = new OrderQueryBond((IQueryAtom) atoms[0],
                    (IQueryAtom) atoms[1], 1.0d);
            break;
        default:
            logger.error("Un parsed bond: " + node.toString());
            break;
        }
        return bond;
    }

    public Object visit(ASTLogicalExpression node, Object data) {
        Object obj = node.jjtGetChild(0).jjtAccept(this, null);
        if (obj instanceof LogicalOperator) {
            LogicalOperator op = (LogicalOperator) obj;
            if (checkRecursiveSmarts(op)) { // check if contains recursive
                                            // smarts
                SMARTSAtom atom = new SMARTSAtom();
                atom.setLogicalExpression(op);
                return atom;
            } else {
                logger.info("Recursive Smarts not supported");
                return null;
                // throw new CDKException("Recursive Smarts Not Supported");
                /*
                 * SMARTSAtomContainer container = new SMARTSAtomContainer();
                 * container.setLogicalExpression(op); return container;
                 */
            }
        }
        // If not operator, then it should be an explicit atom
        return obj;
    }

    /**
     * Returns false if an operand is recursive smarts
     * 
     * @param op
     * @return
     */
    private boolean checkRecursiveSmarts(LogicalOperator op) {
        Object left = op.getLeft();
        if (left instanceof IQueryAtomContainer) { // recursive smarts
            return false;
        } else if (left instanceof LogicalOperator) {
            boolean leftAtomOnly = checkRecursiveSmarts((LogicalOperator) left);
            if (!leftAtomOnly) {
                return false;
            }
        }
        Object right = op.getRight();
        if (right instanceof IQueryAtomContainer) { // recursive smarts
            return false;
        } else if (right instanceof LogicalOperator) {
            boolean rightAtomOnly = checkRecursiveSmarts((LogicalOperator) right);
            return rightAtomOnly;
        }
        return true;
    }

    public Object visit(ASTRecursiveSmartsExpression node, Object data) {
        logger.info("Recursive Smarts not supported");
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    public Object visit(ASTElement node, Object data) {
        String symbol = node.getSymbol();
        SMARTSAtom atom = new SMARTSAtom();
        if ("o".equals(symbol) || "n".equals(symbol) || "c".equals(symbol)) {
            LogicalOperator op = new LogicalOperator();
            op.setName("and");
            IAtom left = new SymbolQueryAtom();
            left.setSymbol(symbol.toUpperCase());
            op.setLeft(left);
            op.setRight(new AromaticAtom());
            atom.setLogicalExpression(op);
        } else {
            SymbolQueryAtom left = new SymbolQueryAtom();
            left.setSymbol(symbol);
            LogicalOperator op = new LogicalOperator();
            op.setName("and");
            op.setLeft(left);
            op.setRight(new AliphaticAtom());
            atom.setLogicalExpression(op);
        }
        return atom;
    }

    public Object visit(ASTTotalHCount node, Object data) {
        TotalHCountAtom atom = new TotalHCountAtom(node.getCount());
        return atom;
    }

    public Object visit(ASTImplicitHCount node, Object data) {
        ImplicitHCountAtom atom = new ImplicitHCountAtom(node.getCount());
        return atom;
    }

    public Object visit(ASTDegree node, Object data) {
        DegreeAtom atom = new DegreeAtom(node.getDegree());
        return atom;
    }

    public Object visit(ASTAtomicNumber node, Object data) {
        logger.info("AotmicNumber Query Atom not implemented");
        return data;
    }

    public Object visit(ASTCharge node, Object data) {
        logger.info("Charge Atom not implemented");
        return data;
    }

    public Object visit(ASTRingConnectivity node, Object data) {
        logger.info("RingConnectivity Query Atom not implemented");
        return data;
    }

    public Object visit(ASTTotalConnectivity node, Object data) {
        logger.info("RingConnectivity Query Atom not implemented");
        return data;
    }

    public Object visit(ASTValence node, Object data) {
        logger.info("RingConnectivity Query Atom not implemented");
        return data;
    }

    public Object visit(ASTRingMembership node, Object data) {
        logger.info("RingConnectivity Query Atom not implemented");
        return data;
    }

    public Object visit(ASTRingSize node, Object data) {
        logger.info("RingConnectivity Query Atom not implemented");
        return data;
    }

    public Object visit(ASTAliphatic node, Object data) {
        AliphaticAtom atom = new AliphaticAtom();
        return atom;
    }

    public Object visit(ASTAromatic node, Object data) {
        AromaticAtom atom = new AromaticAtom();
        return atom;
    }

    public Object visit(ASTAnyAtom node, Object data) {
        AnyAtom atom = new AnyAtom();
        return atom;
    }

    public Object visit(ASTAtomicMass node, Object data) {
        logger.info("RingConnectivity Query Atom not implemented");
        return data;
    }

    public Object visit(ASTChirality node, Object data) {
        logger.info("RingConnectivity Query Atom not implemented");
        return data;
    }

    public Object visit(ASTLowAndExpression node, Object data) {
        LogicalOperator op = new LogicalOperator();
        Object left = node.jjtGetChild(0).jjtAccept(this, data);
        if (node.jjtGetNumChildren() == 1) {
            // one child, means this operator is a leaf
            return left;
        }
        op.setName("and");
        op.setLeft(left);
        op.setRight(node.jjtGetChild(1).jjtAccept(this, data));
        return op;
    }

    public Object visit(ASTOrExpression node, Object data) {
        LogicalOperator op = new LogicalOperator();
        Object left = node.jjtGetChild(0).jjtAccept(this, data);
        if (node.jjtGetNumChildren() == 1) {
            // one child, means this operator is a leaf
            return left;
        }
        op.setName("or");
        op.setLeft(left);
        op.setRight(node.jjtGetChild(1).jjtAccept(this, data));
        return op;
    }

    public Object visit(ASTNotExpression node, Object data) {
        LogicalOperator op = new LogicalOperator();
        Object left = node.jjtGetChild(0).jjtAccept(this, data);
        if (node.getType() == SMARTSParserConstants.DEFAULT) {
            return left;
        }
        op.setName("not");
        op.setLeft(left);
        return op;
    }

    public Object visit(ASTExplicitHighAndExpression node, Object data) {
        LogicalOperator op = new LogicalOperator();
        Object left = node.jjtGetChild(0).jjtAccept(this, data);
        if (node.getType() == SMARTSParserConstants.DEFAULT) {
            return left;
        }
        op.setName("and");
        op.setLeft(left);
        return op;
    }

    public Object visit(ASTImplicitHighAndExpression node, Object data) {
        LogicalOperator op = new LogicalOperator();
        Object left = node.jjtGetChild(0).jjtAccept(this, data);
        if (node.jjtGetNumChildren() == 1) {
            return left;
        }
        if (node.jjtGetNumChildren() == 2) {
            op.setName("and");
            op.setLeft(left);
            Object obj = node.jjtGetChild(1).jjtAccept(this, data);
            op.setRight(obj);
        }
        return op;
    }

    public Object visit(ASTExplicitAtom node, Object data) {
        String symbol = node.getSymbol();
        if ("*".equals(symbol)) {
            return new AnyAtom();
        } else if ("A".equals(symbol)) {
            return new AliphaticAtom();
        } else if ("a".equals(symbol)) {
            return new AromaticAtom();
        } else if ("o".equals(symbol) || "n".equals(symbol)
                || "c".equals(symbol)) {
            LogicalOperator op = new LogicalOperator();
            op.setName("and");
            IAtom left = new SymbolQueryAtom();
            left.setSymbol(symbol.toUpperCase());
            op.setLeft(left);
            op.setRight(new AromaticAtom());
            SMARTSAtom atom = new SMARTSAtom();
            atom.setLogicalExpression(op);
            return atom;
        } else {
            SymbolQueryAtom left = new SymbolQueryAtom();
            left.setSymbol(symbol);
            LogicalOperator op = new LogicalOperator();
            op.setName("and");
            op.setLeft(left);
            op.setRight(new AliphaticAtom());
            SMARTSAtom atom = new SMARTSAtom();
            atom.setLogicalExpression(op);
            return atom;
        }
    }
}
