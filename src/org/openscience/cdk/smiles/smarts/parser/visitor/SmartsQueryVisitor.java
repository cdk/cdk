/* $Revision: $ $Author: $ $Date: $ 
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

import java.util.ArrayList;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.AliphaticAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AliphaticSymbolAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticSymbolAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AtomicNumberAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.DegreeAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.ImplicitHCountAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.LogicalOperatorAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.LogicalOperatorBond;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.RingBond;
import org.openscience.cdk.isomorphism.matchers.smarts.RingIdentifierAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.RingMembershipAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SmallestRingAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.StereoBond;
import org.openscience.cdk.isomorphism.matchers.smarts.TotalConnectionAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.TotalHCountAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.TotalRingConnectionAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.TotalValencyAtom;
import org.openscience.cdk.smiles.smarts.parser.ASTAliphatic;
import org.openscience.cdk.smiles.smarts.parser.ASTAnyAtom;
import org.openscience.cdk.smiles.smarts.parser.ASTAromatic;
import org.openscience.cdk.smiles.smarts.parser.ASTAtom;
import org.openscience.cdk.smiles.smarts.parser.ASTAtomicMass;
import org.openscience.cdk.smiles.smarts.parser.ASTAtomicNumber;
import org.openscience.cdk.smiles.smarts.parser.ASTCharge;
import org.openscience.cdk.smiles.smarts.parser.ASTChirality;
import org.openscience.cdk.smiles.smarts.parser.ASTElement;
import org.openscience.cdk.smiles.smarts.parser.ASTExplicitAtom;
import org.openscience.cdk.smiles.smarts.parser.ASTExplicitConnectivity;
import org.openscience.cdk.smiles.smarts.parser.ASTExplicitHighAndBond;
import org.openscience.cdk.smiles.smarts.parser.ASTExplicitHighAndExpression;
import org.openscience.cdk.smiles.smarts.parser.ASTGroup;
import org.openscience.cdk.smiles.smarts.parser.ASTImplicitHCount;
import org.openscience.cdk.smiles.smarts.parser.ASTImplicitHighAndBond;
import org.openscience.cdk.smiles.smarts.parser.ASTImplicitHighAndExpression;
import org.openscience.cdk.smiles.smarts.parser.ASTLowAndBond;
import org.openscience.cdk.smiles.smarts.parser.ASTLowAndExpression;
import org.openscience.cdk.smiles.smarts.parser.ASTNotBond;
import org.openscience.cdk.smiles.smarts.parser.ASTNotExpression;
import org.openscience.cdk.smiles.smarts.parser.ASTOrBond;
import org.openscience.cdk.smiles.smarts.parser.ASTOrExpression;
import org.openscience.cdk.smiles.smarts.parser.ASTReaction;
import org.openscience.cdk.smiles.smarts.parser.ASTRecursiveSmartsExpression;
import org.openscience.cdk.smiles.smarts.parser.ASTRingConnectivity;
import org.openscience.cdk.smiles.smarts.parser.ASTRingIdentifier;
import org.openscience.cdk.smiles.smarts.parser.ASTRingMembership;
import org.openscience.cdk.smiles.smarts.parser.ASTSimpleBond;
import org.openscience.cdk.smiles.smarts.parser.ASTSmallestRingSize;
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

/**
 * An AST tree visitor. It builds an instance of <code>QueryAtomContainer</code>
 * from the AST tree.
 * 
 * To use this visitor:
 * <pre>
 * SMARTSParser parser = new SMARTSParser(new java.io.StringReader("C*C"));
 * ASTStart ast = parser.start();
 * SmartsQueryVisitor visitor = new SmartsQueryVisitor();
 * QueryAtomContainer query = visitor.visit(ast, null);
 * </pre>
 *
 * @author Dazhi Jiao
 * @cdk.created 2007-04-24
 * @cdk.module smarts
 * @cdk.keyword SMARTS AST
 */
public class SmartsQueryVisitor implements SMARTSParserVisitor {
	private RingIdentifierAtom[] ringAtoms;
	private IQueryAtomContainer query;
	
	public Object visit(ASTRingIdentifier node, Object data) {
		IQueryAtom atom = (IQueryAtom)data;
		RingIdentifierAtom ringIdAtom = new RingIdentifierAtom();
		ringIdAtom.setAtom(atom);
		IQueryBond bond;
		if (node.jjtGetNumChildren() == 0) { // implicit bond
			bond = null;
		} else {
			bond = (IQueryBond)node.jjtGetChild(0).jjtAccept(this, data);
		}
		ringIdAtom.setRingBond(bond);
		return ringIdAtom;
	}

	public Object visit(ASTAtom node, Object data) {
		IQueryAtom atom = (IQueryAtom)node.jjtGetChild(0).jjtAccept(this, data);
		for (int i = 1; i < node.jjtGetNumChildren(); i++) { // if there are ring identifiers
			ASTRingIdentifier ringIdentifier = (ASTRingIdentifier)node.jjtGetChild(i);
			RingIdentifierAtom ringIdAtom = (RingIdentifierAtom)ringIdentifier.jjtAccept(this, atom);
			
			// if there is alreay a RingIdentifierAtom, create a bond betwee them
			// and add the bond to the query
			int ringId = ringIdentifier.getRingId();
			
			if (ringAtoms[ringId] == null) {
				ringAtoms[ringId] = ringIdAtom;
			} else {
				IQueryBond ringBond;
				// first check if the two bonds ma
				if (ringAtoms[ringId].getRingBond() == null) {
					if (ringIdAtom.getRingBond() == null) {
						if (atom instanceof AromaticSymbolAtom && 
								ringAtoms[ringId].getAtom() instanceof AromaticSymbolAtom) {
							ringBond = new AromaticQueryBond(ringAtoms[ringId].getAtom(), atom, 1.5d);
						} else {
							ringBond = new OrderQueryBond(ringAtoms[ringId].getAtom(), atom, 1.0d);
						}
					} else {
						ringBond = ringIdAtom.getRingBond();
						((IBond)ringBond).setAtoms(new IAtom[] { ringAtoms[ringId].getAtom(), atom });
					}
				} else {
					// Here I assume the bond are always same. This should be checked by the parser already
					ringBond = ringAtoms[ringId].getRingBond();
					((IBond)ringBond).setAtoms(new IAtom[] { ringAtoms[ringId].getAtom(), atom });
				}
				query.addBond((IBond)ringBond);
			}
			
			// update the ringAtom reference
			ringAtoms[ringId] = ringIdAtom;
		}
		return atom;
	}

	private final static LoggingTool logger = new LoggingTool(
			SmartsQueryVisitor.class);
	
	/**
	 * Creates a new instance
	 */
	public SmartsQueryVisitor() {
		super();
	}
	
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
		ArrayList atomContainerList = new ArrayList();
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			ringAtoms = new RingIdentifierAtom[10];
			query = new QueryAtomContainer();
			node.jjtGetChild(i).jjtAccept(this, null);
			atomContainerList.add(query);
		}
		logger.info("Only return the first smarts. Group not supported.");
		return atomContainerList.get(0); 
	}
	
	public Object visit(ASTSmarts node, Object data) {
		SMARTSAtom atom = null;
		SMARTSBond bond = null;
		
		ASTAtom first = (ASTAtom)node.jjtGetChild(0);
		atom = (SMARTSAtom)first.jjtAccept(this, null);
		if (data != null) { // this is a sub smarts
			bond = (SMARTSBond)((Object[])data)[1];	
			if (bond == null) {
				if (((SMARTSAtom)((Object[])data)[0]).getFlag(CDKConstants.ISAROMATIC) &&
						atom.getFlag(CDKConstants.ISAROMATIC)) {
					bond = new AromaticQueryBond(atom, (SMARTSAtom)((Object[])data)[0], 1.5d);
				} else {
					bond = new OrderQueryBond(atom, (SMARTSAtom)((Object[])data)[0], 1.0d);
				}
			} else {
				bond.setAtoms(new IAtom[] {(SMARTSAtom)((Object[])data)[0], atom});
			}
			query.addBond(bond);
			bond = null;
		}
		query.addAtom(atom);
		
		for (int i = 1; i < node.jjtGetNumChildren(); i++) {
			Node child = node.jjtGetChild(i);
			if (child instanceof ASTLowAndBond) {
				bond = (SMARTSBond) child.jjtAccept(this, data);
			} else if (child instanceof ASTAtom) {
				SMARTSAtom newAtom = (SMARTSAtom)child.jjtAccept(this, null);
				if (bond == null) {
					if (newAtom.getFlag(CDKConstants.ISAROMATIC) &&
							atom.getFlag(CDKConstants.ISAROMATIC)) {
						bond = new AromaticQueryBond(atom, newAtom, 1.5d);
					} else {
						bond = new OrderQueryBond(atom, newAtom, 1.0d);
					}
				} else {
					bond.setAtoms(new IAtom[] {atom, newAtom});
				}
				query.addBond(bond);
				query.addAtom(newAtom);
				
				atom = newAtom;
				bond = null;
			} else if (child instanceof ASTSmarts) { // another smarts
				child.jjtAccept(this, new Object[] {atom, bond});
				bond = null;
			}
		}

		return query;
	}

	public Object visit(ASTNotBond node, Object data) {
		Object left = node.jjtGetChild(0).jjtAccept(this, data);
		if (node.getType() == SMARTSParserConstants.DEFAULT) {
			return left;
		}
		LogicalOperatorBond bond = new LogicalOperatorBond();
		bond.setOperator("not");
		bond.setLeft((IQueryBond) left);
		return bond;
	}

	public Object visit(ASTImplicitHighAndBond node, Object data) {
		Object left = node.jjtGetChild(0).jjtAccept(this, data);
		if (node.jjtGetNumChildren() == 1) {
			return left;
		}
		LogicalOperatorBond bond = new LogicalOperatorBond();
		bond.setOperator("and");
		bond.setLeft((IQueryBond) left);
		IQueryBond right = (IQueryBond) node.jjtGetChild(1).jjtAccept(this,
				data);
		bond.setRight(right);
		return bond;
	}

	public Object visit(ASTLowAndBond node, Object data) {
		Object left = node.jjtGetChild(0).jjtAccept(this, data);
		if (node.jjtGetNumChildren() == 1) {
			return left;
		}
		LogicalOperatorBond bond = new LogicalOperatorBond();
		bond.setOperator("and");
		bond.setLeft((IQueryBond) left);
		IQueryBond right = (IQueryBond) node.jjtGetChild(1).jjtAccept(this,
				data);
		bond.setRight(right);
		return bond;
	}

	public Object visit(ASTOrBond node, Object data) {
		Object left = node.jjtGetChild(0).jjtAccept(this, data);
		if (node.jjtGetNumChildren() == 1) {
			return left;
		}
		LogicalOperatorBond bond = new LogicalOperatorBond();
		bond.setOperator("or");
		bond.setLeft((IQueryBond) left);
		IQueryBond right = (IQueryBond) node.jjtGetChild(1).jjtAccept(this,
				data);
		bond.setRight(right);
		return bond;
	}

	public Object visit(ASTExplicitHighAndBond node, Object data) {
		Object left = node.jjtGetChild(0).jjtAccept(this, data);
		if (node.jjtGetNumChildren() == 1) {
			return left;
		}
		LogicalOperatorBond bond = new LogicalOperatorBond();
		bond.setOperator("and");
		bond.setLeft((IQueryBond) left);
		IQueryBond right = (IQueryBond) node.jjtGetChild(1).jjtAccept(this,
				data);
		bond.setRight(right);
		return bond;
	}

	public Object visit(ASTSimpleBond node, Object data) {
		SMARTSBond bond = null;
		switch (node.getBondType()) {
		case SMARTSParserConstants.S_BOND:
			bond = new OrderQueryBond();
			bond.setOrder(1.0d);
			break;
		case SMARTSParserConstants.D_BOND:
			bond = new OrderQueryBond();
			bond.setOrder(2.0d);
			break;
		case SMARTSParserConstants.T_BOND:
			bond = new OrderQueryBond();
			bond.setOrder(3.0d);
			break;
		case SMARTSParserConstants.ANY_BOND:
			bond = new AnyOrderQueryBond();
			break;
		case SMARTSParserConstants.AR_BOND:
			bond = new AromaticQueryBond();
			break;
		case SMARTSParserConstants.R_BOND:
			//TODO: Ring bond
			bond = new RingBond();
			break;
		case SMARTSParserConstants.UP_S_BOND:
			bond = new StereoBond();
			bond.setOrder(1.0d);
			bond.setStereo(CDKConstants.STEREO_BOND_UP);
			break;
		case SMARTSParserConstants.DN_S_BOND:
			bond = new StereoBond();
			bond.setOrder(1.0d);
			bond.setStereo(CDKConstants.STEREO_BOND_DOWN);
			break;
		case SMARTSParserConstants.UP_OR_UNSPECIFIED_S_BOND:
			LogicalOperatorBond logical = new LogicalOperatorBond();
			logical.setOperator("or");
			StereoBond bond1 = new StereoBond();
			bond1.setOrder(1.0d);
			bond1.setStereo(CDKConstants.STEREO_BOND_UP);
			logical.setLeft(bond1);
			StereoBond bond2 = new StereoBond();
			bond2.setOrder(1.0d);
			bond2.setStereo(CDKConstants.STEREO_BOND_UNDEFINED);
			logical.setRight(bond2);
			bond = logical;
			break;
		case SMARTSParserConstants.DN_OR_UNSPECIFIED_S_BOND:
			logical = new LogicalOperatorBond();
			logical.setOperator("or");
			bond1 = new StereoBond();
			bond1.setOrder(1.0d);
			bond1.setStereo(CDKConstants.STEREO_BOND_DOWN);
			logical.setLeft(bond1);
			bond2 = new StereoBond();
			bond2.setOrder(1.0d);
			bond2.setStereo(CDKConstants.STEREO_BOND_UNDEFINED);
			logical.setRight(bond2);
			bond = logical;
			break;
		default:
			logger.error("Un parsed bond: " + node.toString());
			break;
		}
		return bond;
	}

	public Object visit(ASTRecursiveSmartsExpression node, Object data) {
		logger.info("Recursive Smarts not supported");
		// return an always true atom
		return new SMARTSAtom() {
			private static final long serialVersionUID = 157241524033283896L;
			public boolean matches(IAtom atom) {
				return true;
			}
		};
	}

	public ASTStart getRoot(Node node) {
		if (node instanceof ASTStart) {
			return (ASTStart) node;
		}
		return getRoot(node.jjtGetParent());
	}

	public Object visit(ASTElement node, Object data) {
		String symbol = node.getSymbol();
		SMARTSAtom atom;
		if ("o".equals(symbol) || "n".equals(symbol) || "c".equals(symbol)
				|| "s".equals(symbol)) {
			atom = new AromaticSymbolAtom();
			atom.setSymbol(symbol.toUpperCase());
		} else {
			atom = new AliphaticSymbolAtom();
			atom.setSymbol(symbol);
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

	public Object visit(ASTExplicitConnectivity node, Object data) {
		DegreeAtom atom = new DegreeAtom(node.getNumOfConnection());
		return atom;
	}

	public Object visit(ASTAtomicNumber node, Object data) {
		return new AtomicNumberAtom(node.getNumber());
	}

	public Object visit(ASTCharge node, Object data) {
		if (node.isPositive()) {
			return new DegreeAtom(node.getCharge());
		} else {
			return new DegreeAtom(0 - node.getCharge());
		}
	}

	public Object visit(ASTRingConnectivity node, Object data) {
		return new TotalRingConnectionAtom(node.getNumOfConnection());
	}

	public Object visit(ASTTotalConnectivity node, Object data) {
		return new TotalConnectionAtom(node.getNumOfConnection());
	}

	public Object visit(ASTValence node, Object data) {
		return new TotalValencyAtom(node.getOrder());
	}

	public Object visit(ASTRingMembership node, Object data) {
		return new RingMembershipAtom(node.getNumOfMembership());
	}

	public Object visit(ASTSmallestRingSize node, Object data) {
		return new SmallestRingAtom(node.getSize());
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
		logger.info("AtomicMass Query Atom not implemented");
		return data;
	}

	public Object visit(ASTChirality node, Object data) {
		logger.info("Chirality Query Atom not implemented");
		return data;
	}

	public Object visit(ASTLowAndExpression node, Object data) {
		Object left = node.jjtGetChild(0).jjtAccept(this, data);
		if (node.jjtGetNumChildren() == 1) {
			return left;
		}
		LogicalOperatorAtom atom = new LogicalOperatorAtom();
		atom.setOperator("and");
		atom.setLeft((IQueryAtom) left);
		IQueryAtom right = (IQueryAtom) node.jjtGetChild(1).jjtAccept(this,
				data);
		atom.setRight(right);
		return atom;
	}

	public Object visit(ASTOrExpression node, Object data) {
		Object left = node.jjtGetChild(0).jjtAccept(this, data);
		if (node.jjtGetNumChildren() == 1) {
			return left;
		}
		LogicalOperatorAtom atom = new LogicalOperatorAtom();
		atom.setOperator("or");
		atom.setLeft((IQueryAtom) left);
		IQueryAtom right = (IQueryAtom) node.jjtGetChild(1).jjtAccept(this,
				data);
		atom.setRight(right);
		return atom;
	}

	public Object visit(ASTNotExpression node, Object data) {
		Object left = node.jjtGetChild(0).jjtAccept(this, data);
		if (node.getType() == SMARTSParserConstants.DEFAULT) {
			return left;
		}
		LogicalOperatorAtom atom = new LogicalOperatorAtom();
		atom.setOperator("not");
		atom.setLeft((IQueryAtom) left);
		return atom;
	}

	public Object visit(ASTExplicitHighAndExpression node, Object data) {
		Object left = node.jjtGetChild(0).jjtAccept(this, data);
		if (node.jjtGetNumChildren() == 1) {
			return left;
		}
		LogicalOperatorAtom atom = new LogicalOperatorAtom();
		atom.setOperator("and");
		atom.setLeft((IQueryAtom) left);
		IQueryAtom right = (IQueryAtom) node.jjtGetChild(1).jjtAccept(this,
				data);
		atom.setRight(right);
		return atom;
	}

	public Object visit(ASTImplicitHighAndExpression node, Object data) {
		Object left = node.jjtGetChild(0).jjtAccept(this, data);
		if (node.jjtGetNumChildren() == 1) {
			return left;
		}
		LogicalOperatorAtom atom = new LogicalOperatorAtom();
		atom.setOperator("and");
		atom.setLeft((IQueryAtom) left);
		IQueryAtom right = (IQueryAtom) node.jjtGetChild(1).jjtAccept(this,
				data);
		atom.setRight(right);
		return atom;
	}

	public Object visit(ASTExplicitAtom node, Object data) {
		IQueryAtom atom = null;
		String symbol = node.getSymbol();
		if ("*".equals(symbol)) {
			atom = new AnyAtom();
		} else if ("A".equals(symbol)) {
			atom = new AliphaticAtom();
		} else if ("a".equals(symbol)) {
			atom = new AromaticAtom();
		} else if ("o".equals(symbol) || "n".equals(symbol)
				|| "c".equals(symbol) || "s".equals(symbol)) {
			atom = new AromaticSymbolAtom();
			atom.setSymbol(symbol.toUpperCase());
		} else {
			atom = new AliphaticSymbolAtom();
			atom.setSymbol(symbol);
		}
		return atom;
	}
}
