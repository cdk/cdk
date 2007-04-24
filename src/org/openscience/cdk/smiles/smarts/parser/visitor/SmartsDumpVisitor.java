package org.openscience.cdk.smiles.smarts.parser.visitor;

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
import org.openscience.cdk.smiles.smarts.parser.ASTPrimitiveAtomExpression;
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
import org.openscience.cdk.smiles.smarts.parser.SimpleNode;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParserVisitor;

public class SmartsDumpVisitor implements SMARTSParserVisitor {
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
    public Object visit(ASTBond node, Object data) {
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
        System.out.println(indentString() + node);
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
    public Object visit(ASTLogicalExpression node, Object data) {
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
    public Object visit(ASTDegree node, Object data){
        System.out.println(indentString() + node + " " + node.getDegree());
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
    public Object visit(ASTRingSize node, Object data){
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
