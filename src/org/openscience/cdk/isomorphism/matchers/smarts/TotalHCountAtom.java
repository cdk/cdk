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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;

/**
 * This matcher checks the formal charge of the Atom. This cannot be matched
 * with a unpreprocessed Atom!
 * 
 * @cdk.module extra
 */
public class TotalHCountAtom extends SMARTSAtom {

	private static final long serialVersionUID = -3532280322660394553L;

	private String Elem = null;

	private int hCount;

	public TotalHCountAtom(int hCount) {
		this.hCount = hCount;
	}

	public TotalHCountAtom() {
		this.hCount = Default;
	}

	public void setSymbol(String ss) {
		Elem = ss;
	}

	public int getOperator() {
		if (ID != null && this.hCount == Default)
			return 1;
		else if (ID != null && this.hCount != Default)
			return 2;
		else if (this.hCount == Default)
			return 3;
		else if (this.hCount != Default)
			return 4;
		return 5;
	}

	public int getHH(IAtom atom) {
		return ((Integer)atom.getProperty(CDKConstants.TOTAL_H_COUNT)).intValue();
	}

	public boolean matches(IAtom atom) {
		switch (getOperator()) {
		case 1:
			return defaultOperatorCheck(atom)
					&& atom.getSymbol().equals(this.Elem);
		case 2:
			return nonDefaultOperatorCheck(atom)
					&& atom.getSymbol().equals(this.Elem);
		case 3:
			return defaultCheck(atom) && atom.getSymbol().equals(this.Elem);
		case 4:
			return nonDefaultCheck(atom);
		default:
			return false;
		}
	}

	/*
	 * public boolean matches(IAtom atom){ if(matche(atom)){
	 * if(atom.getSymbol().equals(this.Elem)) return true; else return false; }
	 * return false; }
	 */
	private boolean defaultCheck(IAtom atom) {
		if (getHH(atom) != 0)
			return true;
		return false;
	}

	private boolean nonDefaultCheck(IAtom atom) {
		if (getHH(atom) != 0) {
			if (getHH(atom) == this.hCount) {
				return true;
			}
		} else {
			if (getHH(atom) == 0) {
				return true;
			}
		}
		return false;
	}

	private boolean defaultOperatorCheck(IAtom atom) {
		if (getHH(atom) == 0)
			return true;
		return false;
	}

	private boolean nonDefaultOperatorCheck(IAtom atom) {
		if (getHH(atom) != 0 && getHH(atom) != this.hCount)
			return false;
		return false;
	}

	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("TotalHCountAtom(");
		s.append(this.hashCode() + ", ");
		s.append("HC:" + hCount);
		s.append(")");
		return s.toString();
	}
}
