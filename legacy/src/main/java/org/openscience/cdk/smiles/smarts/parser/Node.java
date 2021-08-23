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
 * All AST nodes must implement this interface. It provides basic machinery for constructing the
 * parent and child relationships between nodes.
 *
 * <p>Automatically generated by JJTree
 *
 * @author Dazhi Jiao
 * @cdk.created 2007-04-24
 * @cdk.module smarts
 * @cdk.githash
 * @cdk.keyword SMARTS AST
 */
@Deprecated
public interface Node {

    /**
     * This method is called after the node has been made the current node. It indicates that child
     * nodes can now be added to it.
     */
    void jjtOpen();

    /** This method is called after all the child nodes have been added. */
    void jjtClose();

    /** This pair of methods are used to inform the node of its parent. */
    void jjtSetParent(Node n);

    Node jjtGetParent();

    /** This method tells the node to add its argument to the node's list of children. */
    void jjtAddChild(Node n, int i);

    /** This method returns a child node. The children are numbered from zero, left to right. */
    Node jjtGetChild(int i);

    /** Return the number of children the node has. */
    int jjtGetNumChildren();

    /** Accept the visitor. * */
    Object jjtAccept(SMARTSParserVisitor visitor, Object data);

    /**
     * Removes a child from this node
     *
     * @param i
     */
    void jjtRemoveChild(int i);
}
