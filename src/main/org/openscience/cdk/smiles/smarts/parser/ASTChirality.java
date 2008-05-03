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
package org.openscience.cdk.smiles.smarts.parser;

/**
 * An AST node. It represents chirality (@, @@, etc) in smarts.
 * 
 * @author Dazhi Jiao
 * @cdk.created 2007-04-24
 * @cdk.module smarts
 * @cdk.svnrev  $Revision$
 * @cdk.keyword SMARTS
 */
public class ASTChirality extends SimpleNode {
	private boolean unspecified = false;
	private boolean clockwise = true;
	private int degree = -1;
	/**
	 * Creates a new instance
	 *
	 * @param id
	 */
	public ASTChirality(int id) {
		super(id);
	}

	/**
	 * Creates a new instance
	 *
	 * @param p
	 * @param id
	 */
	public ASTChirality(SMARTSParser p, int id) {
		super(p, id);
	}

	/* (non-Javadoc)
	 * @see org.openscience.cdk.smiles.smarts.parser.SimpleNode#jjtAccept(org.openscience.cdk.smiles.smarts.parser.SMARTSParserVisitor, java.lang.Object)
	 */
	public Object jjtAccept(SMARTSParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}

	public boolean isUnspecified() {
		return unspecified;
	}

	public void setUnspecified(boolean unspecified) {
		this.unspecified = unspecified;
	}

	public boolean isClockwise() {
		return clockwise;
	}

	public void setClockwise(boolean clockwise) {
		this.clockwise = clockwise;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}
	
	
}
