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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.*;
import org.openscience.cdk.smiles.smarts.parser.*;
import org.openscience.cdk.tools.LoggingTool;

import java.util.ArrayList;
import java.util.List;

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
 * @cdk.svnrev  $Revision$
 * @cdk.keyword SMARTS AST
 */
public class SmartsQueryVisitor implements SMARTSParserVisitor {
	// current atoms with a ring identifier 
	private RingIdentifierAtom[] ringAtoms;
	// current atoms in recursive smarts with a ring identifier
	private RingIdentifierAtom[] recursiveRingAtoms;
	// query 
	private IQueryAtomContainer query;
	// Whether is parsing a recursive smarts
	private boolean isParsingRS;
	// Recursive smarts query
	private IQueryAtomContainer rsQuery;
	
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
			
			// if there is already a RingIdentifierAtom, create a bond between 
			// them and add the bond to the query
			int ringId = ringIdentifier.getRingId();
			if (isParsingRS) {
				if (recursiveRingAtoms[ringId] == null) {
					recursiveRingAtoms[ringId] = ringIdAtom;
				} else {
					IQueryBond ringBond;
					// first check if the two bonds ma
					if (recursiveRingAtoms[ringId].getRingBond() == null) {
						if (ringIdAtom.getRingBond() == null) {
							if (atom instanceof AromaticSymbolAtom && 
									recursiveRingAtoms[ringId].getAtom() instanceof AromaticSymbolAtom) {
								ringBond = new AromaticQueryBond();
							} else {
								ringBond = new RingBond();
							}
						} else {
							ringBond = ringIdAtom.getRingBond();
						}
					} else {
						// Here I assume the bond are always same. This should be checked by the parser already
						ringBond = recursiveRingAtoms[ringId].getRingBond();
					}
					((IBond)ringBond).setAtoms(new IAtom[] { recursiveRingAtoms[ringId].getAtom(), atom });
					rsQuery.addBond((IBond)ringBond);
				}
				
				// update the recursiveRingAtom reference
				recursiveRingAtoms[ringId] = ringIdAtom;				
			} else {
				if (ringAtoms[ringId] == null) {
					ringAtoms[ringId] = ringIdAtom;
				} else {
					IQueryBond ringBond;
					// first check if the two bonds ma
					if (ringAtoms[ringId].getRingBond() == null) {
						if (ringIdAtom.getRingBond() == null) {
							if (atom instanceof AromaticSymbolAtom && 
									ringAtoms[ringId].getAtom() instanceof AromaticSymbolAtom) {
								ringBond = new AromaticQueryBond();
							} else {
								ringBond = new RingBond();
							}
						} else {
							ringBond = ringIdAtom.getRingBond();
						}
					} else {
						// Here I assume the bond are always same. This should be checked by the parser already
						ringBond = ringAtoms[ringId].getRingBond();
					}
					((IBond)ringBond).setAtoms(new IAtom[] { ringAtoms[ringId].getAtom(), atom });
					query.addBond((IBond)ringBond);
				}
				
				// update the ringAtom reference
				ringAtoms[ringId] = ringIdAtom;
			}
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
		List<IAtomContainer> atomContainers = new ArrayList<IAtomContainer>();
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			ringAtoms = new RingIdentifierAtom[10];
			query = new QueryAtomContainer();
			node.jjtGetChild(i).jjtAccept(this, null);
			atomContainers.add(query);
		}
		logger.info("Only return the first smarts. Group not supported.");
		return atomContainers.get(0); 
	}
	
	public Object visit(ASTSmarts node, Object data) {
		SMARTSAtom atom = null;
		SMARTSBond bond = null;
		
		ASTAtom first = (ASTAtom)node.jjtGetChild(0);
		atom = (SMARTSAtom)first.jjtAccept(this, null);
		if (data != null) { // this is a sub smarts
			bond = (SMARTSBond)((Object[])data)[1];	
			if (bond == null) { // since no bond was specified it could be aromatic or single
                bond = new AromaticOrSingleQueryBond();
				bond.setAtoms(new IAtom[] {atom, (SMARTSAtom)((Object[])data)[0]});
			} else {
				bond.setAtoms(new IAtom[] {(SMARTSAtom)((Object[])data)[0], atom});
			}
			if (isParsingRS) rsQuery.addBond(bond);
			else query.addBond(bond);
			bond = null;
		}
		if (isParsingRS) rsQuery.addAtom(atom);
		else query.addAtom(atom);
		
		for (int i = 1; i < node.jjtGetNumChildren(); i++) {
			Node child = node.jjtGetChild(i);
			if (child instanceof ASTLowAndBond) {
				bond = (SMARTSBond) child.jjtAccept(this, data);
			} else if (child instanceof ASTAtom) {
				SMARTSAtom newAtom = (SMARTSAtom)child.jjtAccept(this, null);
				if (bond == null) { // since no bond was specified it could be aromatic or single
                    bond = new AromaticOrSingleQueryBond();
				}
				bond.setAtoms(new IAtom[] {atom, newAtom});
				if (isParsingRS) {
					rsQuery.addBond(bond);
					rsQuery.addAtom(newAtom);
				} else {
					query.addBond(bond);
					query.addAtom(newAtom);
				}
				
				atom = newAtom;
				bond = null;
			} else if (child instanceof ASTSmarts) { // another smarts
				child.jjtAccept(this, new Object[] {atom, bond});
				bond = null;
			}
		}

		return isParsingRS ? rsQuery: query;
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
			bond = new OrderQueryBond(IBond.Order.SINGLE);
			break;
		case SMARTSParserConstants.D_BOND:
			bond = new OrderQueryBond(IBond.Order.DOUBLE);
			break;
		case SMARTSParserConstants.T_BOND:
			bond = new OrderQueryBond(IBond.Order.TRIPLE);
			break;
		case SMARTSParserConstants.ANY_BOND:
			bond = new AnyOrderQueryBond();
			break;
		case SMARTSParserConstants.AR_BOND:
			bond = new AromaticQueryBond();
			break;
		case SMARTSParserConstants.R_BOND:
			bond = new RingBond();
			break;
		case SMARTSParserConstants.UP_S_BOND:
			bond = new StereoBond();
			bond.setOrder(IBond.Order.SINGLE);
			bond.setStereo(CDKConstants.STEREO_BOND_UP);
			break;
		case SMARTSParserConstants.DN_S_BOND:
			bond = new StereoBond();
			bond.setOrder(IBond.Order.SINGLE);
			bond.setStereo(CDKConstants.STEREO_BOND_DOWN);
			break;
		case SMARTSParserConstants.UP_OR_UNSPECIFIED_S_BOND:
			LogicalOperatorBond logical = new LogicalOperatorBond();
			logical.setOperator("or");
			StereoBond bond1 = new StereoBond();
			bond1.setOrder(IBond.Order.SINGLE);
			bond1.setStereo(CDKConstants.STEREO_BOND_UP);
			logical.setLeft(bond1);
			StereoBond bond2 = new StereoBond();
			bond2.setOrder(IBond.Order.SINGLE);
			bond2.setStereo(CDKConstants.STEREO_BOND_UNDEFINED);
			logical.setRight(bond2);
			bond = logical;
			break;
		case SMARTSParserConstants.DN_OR_UNSPECIFIED_S_BOND:
			logical = new LogicalOperatorBond();
			logical.setOperator("or");
			bond1 = new StereoBond();
			bond1.setOrder(IBond.Order.SINGLE);
			bond1.setStereo(CDKConstants.STEREO_BOND_DOWN);
			logical.setLeft(bond1);
			bond2 = new StereoBond();
			bond2.setOrder(IBond.Order.SINGLE);
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
		rsQuery = new QueryAtomContainer();
		recursiveRingAtoms = new RingIdentifierAtom[10];
		isParsingRS = true;
		node.jjtGetChild(0).jjtAccept(this, null);
		isParsingRS = false;

        return new RecursiveSmartsAtom(rsQuery);
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
				|| "s".equals(symbol) || "p".equals(symbol) || "as".equals(symbol)
				|| "se".equals(symbol)) {
			String atomSymbol = symbol.substring(0,1).toUpperCase() + symbol.substring(1);
			atom = new AromaticSymbolAtom(atomSymbol);
		} else {
			atom = new AliphaticSymbolAtom(symbol);
		}
		return atom;
	}

	public Object visit(ASTTotalHCount node, Object data) {
		return new TotalHCountAtom(node.getCount());
	}

	public Object visit(ASTImplicitHCount node, Object data) {
		return new ImplicitHCountAtom(node.getCount());
	}

	public Object visit(ASTExplicitConnectivity node, Object data) {
		return new ExplicitConnectionAtom(node.getNumOfConnection());
	}

	public Object visit(ASTAtomicNumber node, Object data) {
		return new AtomicNumberAtom(node.getNumber());
	}

    public Object visit(ASTHybrdizationNumber node, Object data) {
        return new HybridizationNumberAtom(node.getHybridizationNumber());
    }

    public Object visit(ASTCharge node, Object data) {
		if (node.isPositive()) {
			return new FormalChargeAtom(node.getCharge());
		} else {
			return new FormalChargeAtom(0 - node.getCharge());
		}
	}

	public Object visit(ASTRingConnectivity node, Object data) {
		return new TotalRingConnectionAtom(node.getNumOfConnection());
	}

    public Object visit(ASTPeriodicGroupNumber node, Object data) {
        return new PeriodicGroupNumberAtom(node.getGroupNumber());
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
        return new AliphaticAtom();
	}

    public Object visit(ASTNonCHHeavyAtom node, Object data) {
        return new NonCHHeavyAtom();
    }

    public Object visit(ASTAromatic node, Object data) {
        return new AromaticAtom();
	}

	public Object visit(ASTAnyAtom node, Object data) {
        return new AnyAtom();
	}

	public Object visit(ASTAtomicMass node, Object data) {
        return new MassAtom(node.getMass());
	}

	public Object visit(ASTChirality node, Object data) {
		ChiralityAtom atom = new ChiralityAtom();
		atom.setDegree(node.getDegree());
		atom.setClockwise(node.isClockwise());
		atom.setUnspecified(node.isUnspecified());
		return atom;
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
				|| "c".equals(symbol) || "s".equals(symbol)
				|| "p".equals(symbol) || "as".equals(symbol)
				|| "se".equals(symbol)) {
			String atomSymbol = symbol.substring(0,1).toUpperCase() + symbol.substring(1);
			atom = new AromaticSymbolAtom(atomSymbol);
		} else if ("H".equals(symbol)) {
			atom = new HydrogenAtom();
			atom.setSymbol(symbol.toUpperCase());
		} else {
			atom = new AliphaticSymbolAtom(symbol);
		}
		return atom;
	}
}
