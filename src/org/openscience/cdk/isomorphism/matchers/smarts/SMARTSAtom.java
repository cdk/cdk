/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;

/**
 * This is just a simple proof of concept, and far from a functional SMARTSAtom.
 * 
 * @cdk.module extra
 */
public class SMARTSAtom extends org.openscience.cdk.PseudoAtom implements
        IQueryAtom {
    public String ID;

    public SMARTSAtom() {
    }

    public void setOperator(String str) {
        ID = str;
    }

    private LogicalOperator logicalExpression;

    public LogicalOperator getLogicalExpression() {
        return logicalExpression;
    }

    public void setLogicalExpression(LogicalOperator logicalExpression) {
        this.logicalExpression = logicalExpression;
    }

    public boolean matches(IAtom atom) {
        if (logicalExpression != null) {
            return checkLogicalExpression(atom, logicalExpression);
        }
        return false;
    }

    private boolean checkLogicalExpression(IAtom atom, LogicalOperator op) {
        if ("PSEUDO".equals(op.getName())) {
            return ((IQueryAtom) op.getLeft()).matches(atom);
        }
        Object lo = op.getLeft();
        boolean lb = false;
        if (lo instanceof IQueryAtom) {
            lb = ((IQueryAtom) lo).matches(atom);
        } else if (lo instanceof LogicalOperator) {
            lb = checkLogicalExpression(atom, (LogicalOperator) lo);
        }
        Object ro = op.getRight();
        boolean rb = true;
        if (ro != null) {
            if (ro instanceof IQueryAtom) {
                rb = ((IQueryAtom) ro).matches(atom);
            } else if (ro instanceof LogicalOperator) {
                rb = checkLogicalExpression(atom, (LogicalOperator) ro);
            }
        }
        if ("and".equals(op.getName())) {
            return (lb && rb);
        } else if ("or".equals(op.getName())) {
            return (lb || rb);
        } else if ("not".equals(op.getName())) {
            return (!lb);
        } else {
            return false;
        }
    }

}
