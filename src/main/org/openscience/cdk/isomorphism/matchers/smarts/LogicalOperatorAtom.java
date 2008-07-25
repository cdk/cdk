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
package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;

/**
 * This class matches a logical operator that connects two query atoms
 *
 * @cdk.module  smarts
 * @cdk.svnrev  $Revision$
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
    
    /* (non-Javadoc)
     * @see org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom#matches(org.openscience.cdk.interfaces.IAtom)
     */
    public boolean matches(IAtom atom) {
    	boolean val = false;
    	boolean matchesLeft = left.matches(atom);
    	if (right != null) {
    		if ("and".equals(operator) && matchesLeft) {
        		boolean matchesRight = right.matches(atom);
    			val = matchesLeft && matchesRight;
    		} else if ("or".equals(operator)) {
        		boolean matchesRight = right.matches(atom);
    			val = matchesLeft || matchesRight;
    		}
    	} else {
    		if ("not".equals(operator)) {
    			val = (!matchesLeft);
    		} else {
    			val = matchesLeft;
    		}
    	}
    	return val;
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.ChemObject#getFlag(int)
     */
    public boolean getFlag(int flagType) {
    	boolean val = false;
    	boolean leftFlag = left.getFlag(flagType);
    	if (right != null) {
    		if ("and".equals(operator) && leftFlag) {
        		boolean rightFlag = right.getFlag(flagType);
    			val = leftFlag && rightFlag;
    		} else if ("or".equals(operator)) {
        		boolean rightFlag = right.getFlag(flagType);
    			val = leftFlag || rightFlag;
    		}
    	} else {
    		if ("not".equals(operator)) {
    			val = (!leftFlag);
    		} else {
    			val = leftFlag;
    		}
    	}
    	return val;
    }
}
