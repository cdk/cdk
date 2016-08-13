/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.smiles.smarts.parser;

/**
 * An AST node. It represents the low precedence and logical operator (;) used
 * between atomic primitives or recursive smarts in smarts.
 *
 * @author Dazhi Jiao
 * @cdk.created 2007-04-24
 * @cdk.module smarts
 * @cdk.githash
 * @cdk.keyword SMARTS
 */
class ASTLowAndExpression extends SimpleNode {

    private int mapIdx = 0;

    /**
     * Creates a new instance.
     */
    public ASTLowAndExpression(int id) {
        super(id);
    }

    /**
     * Creates a new instance.
     */
    public ASTLowAndExpression(SMARTSParser p, int id) {
        super(p, id);
    }

    public int getMapIdx() {
        return mapIdx;
    }

    public void setMapIdx(int mapIdx) {
        this.mapIdx = mapIdx;
    }

    /*
         * (non-Javadoc)
         * @see
         * org.openscience.cdk.smiles.smarts.parser.SimpleNode#jjtAccept(org.openscience
         * .cdk.smiles.smarts.parser.SMARTSParserVisitor, java.lang.Object)
         */
    @Override
    public Object jjtAccept(SMARTSParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public void insertLeafChild(Node node) {
        Node firstNode = this.jjtGetChild(0);
        while (!(firstNode instanceof ASTImplicitHighAndExpression)) {
            firstNode = firstNode.jjtGetChild(0);
        }
        ASTImplicitHighAndExpression insert = new ASTImplicitHighAndExpression(
                SMARTSParser.JJTIMPLICITHIGHANDEXPRESSION);
        insert.jjtAddChild(node, 0);
        insert.jjtAddChild(firstNode.jjtGetChild(0), 1);
        firstNode.jjtRemoveChild(0);
        firstNode.jjtAddChild(insert, 0);
    }
}
