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
 * An AST node. It represents charges (+/-) in smarts.
 *
 * @author Dazhi Jiao
 * @cdk.created 2007-04-24
 * @cdk.module smarts
 * @cdk.svnrev  $Revision$
 * @cdk.keyword SMARTS
 */
public class ASTCharge extends SimpleNode {
    /**
     * The value of the charge.
     */
    private int charge;

    /**
     * Whether the charge is positive.
     */
    private boolean isPositive;

    /**
     * Creates a new instance.
     */
    public ASTCharge(int id) {
        super(id);
    }

    /**
     * Creates a new instance.
     */
    public ASTCharge(SMARTSParser p, int id) {
        super(p, id);
    }

    /**
     * Returns the charge value.
     */
    public int getCharge() {
        return charge;
    }

    /**
     * Returns true if charge is positive.
     */
    public boolean isPositive() {
        return isPositive;
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.smiles.smarts.parser.SimpleNode#jjtAccept(org.openscience.cdk.smiles.smarts.parser.SMARTSParserVisitor, java.lang.Object)
     */
    public Object jjtAccept(SMARTSParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Sets the charge value.
     */
    public void setCharge(int charge) {
        this.charge = charge;
    }

    /**
     * Sets whether the charge is positive.
     */
    public void setPositive(boolean isPositive) {
        this.isPositive = isPositive;
    }

}
