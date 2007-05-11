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
package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;

/**
 * This class matches a logical operator that connects two query atoms
 *
 * @author Dazhi Jiao
 * @cdk.created 2007-05-10
 * @cdk.module smarts
 * @cdk.keyword SMARTS 
 */
public class LogicalOperatorAtom extends SMARTSAtom {
	private static final long serialVersionUID = -5752396252307536738L;

	/**
	 * Left child
	 */
	private IQueryAtom left;

    /**
     * Name of operator
     */
    private String operator;

    /**
     * Right child
     */
    private IQueryAtom right;

    public IQueryAtom getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public IQueryAtom getRight() {
        return right;
    }

    public void setLeft(IQueryAtom left) {
        this.left = left;
    }

    public void setOperator(String name) {
        this.operator = name;
    }

    public void setRight(IQueryAtom right) {
        this.right = right;
    }
    
    public boolean matches(IAtom atom) {
    	boolean matchesLeft = left.matches(atom);
    	if (right != null) {
    		boolean matchesRight = right.matches(atom);
    		if ("and".equals(operator)) {
    			return matchesLeft && matchesRight;
    		} else if ("or".equals(operator)) {
    			return matchesLeft || matchesRight;
    		} else {
    			return false;
    		}
    	} else {
    		if ("not".equals(operator)) {
    			return (!matchesLeft);
    		} else {
    			return matchesLeft;
    		}
    	}
    }
    
    // TODO: This might not be true 
    // is [a;A] aliphatic or aromatic?
    // how about [aR]?
    public boolean getFlag(int flagType) {
    	boolean leftFlag = left.getFlag(flagType);
    	if (right != null) {
    		boolean rightFlag = right.getFlag(flagType);
    		return leftFlag || rightFlag;
    	} else {
    		if ("not".equals(operator)) {
    			return (!leftFlag);
    		} else {
    			return leftFlag;
    		}
    	}
    }
}
