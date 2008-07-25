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
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

/**
 * This class matches a logical operator that connects two query bonds.
 *
 * @cdk.module  smarts
 * @cdk.svnrev  $Revision$
 * @cdk.keyword SMARTS
 */
public class LogicalOperatorBond extends SMARTSBond {
	private static final long serialVersionUID = 7508020488830371461L;

	/**
	 * Left child
	 */
	private IQueryBond left;

    /**
     * Name of the operator
     */
    private String operator;

    /**
     * Right child
     */
    private IQueryBond right;

    public IQueryBond getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public IQueryBond getRight() {
        return right;
    }

    public void setLeft(IQueryBond left) {
        this.left = left;
    }

    public void setOperator(String name) {
        this.operator = name;
    }

    public void setRight(IQueryBond right) {
        this.right = right;
    }
    
    public boolean matches(IBond bond) {
    	boolean matchesLeft = left.matches(bond);
    	if (right != null) {
    		boolean matchesRight = right.matches(bond);
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
    
    public void setAtoms(IAtom[] atoms) {
    	super.setAtoms(atoms);
    	((IBond)left).setAtoms(atoms);
    	if (right != null) ((IBond)right).setAtoms(atoms);
    }
}
