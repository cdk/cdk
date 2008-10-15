/* $Revision$ $Author$ $Date$ 
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
 * Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * (or see http://www.gnu.org/copyleft/lesser.html)
 */
package org.openscience.cdk.smiles.smarts.parser.visitor;

import org.openscience.cdk.smiles.smarts.parser.*;

/**
 * An AST tree visitor. It is a prototype that translate Smarts to MQL. 
 * It is far from fully functioning.
 *
 * @author Dazhi Jiao
 * @cdk.created 2007-04-24
 * @cdk.module smarts
 * @cdk.svnrev  $Revision$
 * @cdk.keyword SMARTS AST
 */
public class Smarts2MQLVisitor implements SMARTSParserVisitor {
    public Object visit(ASTRingIdentifier node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	String symbolIdentified = "";
    boolean not = false;
    
    public Object visit(ASTAtom node, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(SimpleNode node, Object data) {
        return node.childrenAccept(this, data);
    }

    public Object visit(ASTStart node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    public Object visit(ASTReaction node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    public Object visit(ASTGroup node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    public Object visit(ASTSmarts node, Object data) {
        String local = "";
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child instanceof ASTAtom) {
                local = (String)child.jjtAccept(this, local);
            } else if (child instanceof ASTLowAndBond) {
                i++;
                Node nextChild = node.jjtGetChild(i); // the next child should
                // be another smarts
                String bond = (String)child.jjtAccept(this, local);
                local = local + bond;
                local = (String) nextChild.jjtAccept(this, local);
            } else if (child instanceof ASTSmarts) { // implicit single bond
                if (!"".equals(local)) 
                    local = local + "-";
                local = (String) child.jjtAccept(this, local);
            } else if (child instanceof ASTExplicitAtom) {
                if (!"".equals(local)) 
                    local = local + "-";
                local = (String) child.jjtAccept(this, local);
            }
        }        
        return data + local;
    }

    // TODO: Accept only one bond. Need to find out whether MQL supports
    // logical bonds
    public Object visit(ASTLowAndBond node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    // TODO: Accept only one bond. Need to find out whether MQL supports
    // logical bonds
    public Object visit(ASTOrBond node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    // TODO: Accept only one bond. Need to find out whether MQL supports
    // logical bonds
    public Object visit(ASTExplicitHighAndBond node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    // TODO: Accept only one bond. Need to find out whether MQL supports
    // logical bonds
    public Object visit(ASTImplicitHighAndBond node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    // TODO: Accept only one bond. Need to find out whether MQL supports
    // logical bonds
    public Object visit(ASTNotBond node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, data);
    }

    public Object visit(ASTSimpleBond node, Object data) {
        String bond = "";
        int bondType = node.getBondType();
        switch (bondType) {
        case SMARTSParserConstants.ANY_BOND:
            bond = "~";
            break;
        case SMARTSParserConstants.S_BOND:
            bond = "-";
            break;
        case SMARTSParserConstants.D_BOND:
            bond = "=";
            break;
        case SMARTSParserConstants.T_BOND:
            bond = "#";
            break;
        case SMARTSParserConstants.AR_BOND:
            bond = ":";
            break;
        case SMARTSParserConstants.R_BOND:
            bond = "$~1"; // TODO: only one ring is assumed here. Should handle more
            break;
        case SMARTSParserConstants.UP_S_BOND:
        case SMARTSParserConstants.DN_S_BOND:
        case SMARTSParserConstants.UP_OR_UNSPECIFIED_S_BOND:
        case SMARTSParserConstants.DN_OR_UNSPECIFIED_S_BOND:
            bond = "-";
            break;
        }
        return bond;
    }

    public Object visit(ASTExplicitAtom node, Object data) {
        data = data + node.getSymbol(); // TODO: ring handling!
        return data;
    }

    public Object visit(ASTLowAndExpression node, Object data) {
        String left = (String)node.jjtGetChild(0).jjtAccept(this, data);
        if (node.jjtGetNumChildren() == 1) {
            return left;
        }
        String right = (String)node.jjtGetChild(1).jjtAccept(this, data);
        if ("".equals(left)) {
            return right;
        } else if ("".equals(right)) {
            return left;
        } else {
            return left + "&" + right;
        }
    }

    public Object visit(ASTOrExpression node, Object data) {
        String left = (String)node.jjtGetChild(0).jjtAccept(this, data);
        if (node.jjtGetNumChildren() == 1) {
            return left;
        }
        String right = (String)node.jjtGetChild(1).jjtAccept(this, data);
        if ("".equals(left)) {
            return right;
        } else if ("".equals(right)) {
            return left;
        } else {
            return left + "|" + right;
        }
    }

    // TODO: the precedence needs to be addressed
    public Object visit(ASTExplicitHighAndExpression node, Object data) {
        String left = (String)node.jjtGetChild(0).jjtAccept(this, data);
        if (node.jjtGetNumChildren() == 1) {
            return left;
        }
        String right = (String)node.jjtGetChild(1).jjtAccept(this, data);
        if ("".equals(left)) {
            return right;
        } else if ("".equals(right)) {
            return left;
        } else {
            return left + "&" + right;
        }
    }
//  TODO: the precedence needs to be addressed
    public Object visit(ASTImplicitHighAndExpression node, Object data) {
        String left = (String)node.jjtGetChild(0).jjtAccept(this, data);
        if (node.jjtGetNumChildren() == 1) {
            return left;
        }
        String right = (String)node.jjtGetChild(1).jjtAccept(this, data);
        if ("".equals(left)) {
            return right;
        } else if ("".equals(right)) {
            return left;
        } else {
            return left + "&" + right;
        }
    }

    public Object visit(ASTNotExpression node, Object data) {
        // well, I know there is a not in MQL :)
        if (node.getType() == SMARTSParserConstants.NOT) {
            not = true;
        } else {
            not = false;
        }
        String str = "";
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            str += node.jjtGetChild(i).jjtAccept(this, data);
        }        
        return str;
    }

    // TODO: I don't think this is implemented in MQL. Throw an exception/warning?
    public Object visit(ASTRecursiveSmartsExpression node, Object data) {
        return null;
    }

    public Object visit(ASTTotalHCount node, Object data) {
//      TODO: a property? not sure. just making things up here :)
        return data;
    }

    public Object visit(ASTImplicitHCount node, Object data) {
        // TODO: a property? not sure. just making things up here :)
        return data;
    }

    public Object visit(ASTExplicitConnectivity node, Object data) {
//      TODO: a property? not sure. just making things up here :)
        return data;
    }

    public Object visit(ASTAtomicNumber node, Object data) {
//      TODO: a property? not sure. just making things up here :)
        return data;
    }

    public Object visit(ASTHybrdizationNumber node, Object data) {
        return data;
    }

    public Object visit(ASTCharge node, Object data) {
//      TODO: a property? not sure. just making things up here :)
        return data;
    }

    public Object visit(ASTRingConnectivity node, Object data) {

        return data;
    }

    public Object visit(ASTPeriodicGroupNumber node, Object data) {
        return data;
    }

    public Object visit(ASTTotalConnectivity node, Object data) {
//      TODO: a property? not sure. just making things up here :)
        return data;
    }

    public Object visit(ASTValence node, Object data) {
//      TODO: a property? not sure. just making things up here :)
        return data;
    }

    public Object visit(ASTRingMembership node, Object data) {
//      TODO: "ring" is a property, but how about the number of rings?
        return "ring";
    }

    public Object visit(ASTSmallestRingSize node, Object data) {
        return data;
    }

    public Object visit(ASTAliphatic node, Object data) {
        return data;
    }

    public Object visit(ASTNonCHHeavyAtom node, Object data) {
        return data;
    }

    public Object visit(ASTAromatic node, Object data) {
        return data;
    }

    public Object visit(ASTAnyAtom node, Object data) {
        return data;
    }

    public Object visit(ASTAtomicMass node, Object data) {
        return data;
    }

    public Object visit(ASTChirality node, Object data) {
        return data;
    }

    public Object visit(ASTElement node, Object data) {
        symbolIdentified = node.getSymbol();
        return "";
    }

}
