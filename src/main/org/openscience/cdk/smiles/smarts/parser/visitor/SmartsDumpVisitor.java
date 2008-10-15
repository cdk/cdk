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
 * An AST Tree visitor. It dumps the whole AST tree into console
 *
 * @author Dazhi Jiao
 * @cdk.created 2007-04-24
 * @cdk.module smarts
 * @cdk.svnrev  $Revision$
 * @cdk.keyword SMARTS AST
 */
public class SmartsDumpVisitor implements SMARTSParserVisitor {
    public Object visit(ASTRingIdentifier node, Object data) {
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
	}

	public Object visit(ASTAtom node, Object data) {
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
	}
	private int indent = 0;

    private String indentString() {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < indent; ++i) {
        sb.append("  ");
      }
      return sb.toString();
    }
    
    public Object visit(SimpleNode node, Object data) {
        System.out.println(indentString() + node +
        ": acceptor not unimplemented in subclass?");
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;        
    }
    public Object visit(ASTStart node, Object data) {
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }
    public Object visit(ASTReaction node, Object data) {
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }
    public Object visit(ASTGroup node, Object data) {
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }
    public Object visit(ASTSmarts node, Object data) {
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }

    public Object visit(ASTNotBond node, Object data) {
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }
    public Object visit(ASTSimpleBond node, Object data) {
        System.out.println(indentString() + node + " [" + node.getBondType() + "]");
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
        
    public Object visit(ASTImplicitHighAndBond node, Object data) {
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }
    
    public Object visit(ASTLowAndBond node, Object data) {
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }
    
    public Object visit(ASTOrBond node, Object data) {
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }
    
    public Object visit(ASTExplicitHighAndBond node, Object data) {
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    

    public Object visit(ASTElement node, Object data) {
        System.out.println(indentString() + node + " " + node.getSymbol());
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }
    
     public Object visit(ASTRecursiveSmartsExpression node, Object data) {
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }
    public Object visit(ASTPrimitiveAtomExpression node, Object data) {
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
    public Object visit(ASTTotalHCount node, Object data) {
        System.out.println(indentString() + node + " " + node.getCount());
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
    public Object visit(ASTImplicitHCount node, Object data){
        System.out.println(indentString() + node + " " + node.getCount());
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
    public Object visit(ASTExplicitConnectivity node, Object data){
        System.out.println(indentString() + node + " " + node.getNumOfConnection());
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
    public Object visit(ASTAtomicNumber node, Object data){
        System.out.println(indentString() + node + " " + node.getNumber());
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }

    public Object visit(ASTHybrdizationNumber node, Object data) {
        System.out.println(indentString() + node + " " + node.getHybridizationNumber());
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }

    public Object visit(ASTCharge node, Object data){
        System.out.println(indentString() + node + " " + node.getCharge());
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
    public Object visit(ASTRingConnectivity node, Object data){
        System.out.println(indentString() + node + " " + node.getNumOfConnection());
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }

    public Object visit(ASTPeriodicGroupNumber node, Object data) {
        System.out.println(indentString() + node + " " + node.getGroupNumber());
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }

    public Object visit(ASTTotalConnectivity node, Object data){
        System.out.println(indentString() + node + " " + node.getNumOfConnection());
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
    public Object visit(ASTValence node, Object data){
        System.out.println(indentString() + node + " " + node.getOrder());
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
    public Object visit(ASTRingMembership node, Object data){
        System.out.println(indentString() + node + " " + node.getNumOfMembership());
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
    public Object visit(ASTSmallestRingSize node, Object data){
        System.out.println(indentString() + node + " " + node.getSize());
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
    public Object visit(ASTAliphatic node, Object data){
        System.out.println(indentString() +  node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }

    public Object visit(ASTNonCHHeavyAtom node, Object data) {
        System.out.println(indentString() +  node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }

    public Object visit(ASTAromatic node, Object data){
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
    public Object visit(ASTAnyAtom node, Object data){
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
    public Object visit(ASTAtomicMass node, Object data){
        System.out.println(indentString() + node + " " + node.getMass());
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
    public Object visit(ASTChirality node, Object data){
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
    public Object visit(ASTLowAndExpression node, Object data){
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
    public Object visit(ASTOrExpression node, Object data){
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }        
    public Object visit(ASTNotExpression node, Object data){
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
    public Object visit(ASTExplicitHighAndExpression node, Object data){
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }    
    public Object visit(ASTImplicitHighAndExpression node, Object data){
        System.out.println(indentString() + node);
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }  
    public Object visit(ASTExplicitAtom node, Object data){
        System.out.println(indentString() + node + " " + node.getSymbol());
        ++indent;
        data = node.childrenAccept(this, data);
        --indent;
        return data;
    }     
}
