/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.isomorphism.matchers;


import java.util.HashSet;
import org.openscience.cdk.Atom;
import org.openscience.cdk.PseudoAtom;


/**
 * A QueryAtom that matches all symbols but those in this container.
 * You may add symbols to this container. This QueryAtom will only give a match
 * if it does NOT contain the symbol of the Atom to match (example: add "C" to get
 * a match for all non-"C"-Atoms).
 * 
 * @see SymbolSetQueryAtom
 * @cdk.module extra
 */
public class InverseSymbolSetQueryAtom extends PseudoAtom implements QueryAtom {
    
    private HashSet symbols = new HashSet();
    
    public InverseSymbolSetQueryAtom() {}
    
	public boolean matches(Atom atom) {
        return !symbols.contains(atom.getSymbol());
    };

    public void addSymbol(String symbol) {
        symbols.add(symbol);
    }
    
    public void removeSymbol(String symbol) {
        symbols.remove(symbol);
    }
    
    public boolean hasSymbol(String symbol) {
        return symbols.contains(symbol);
    }
    
    public HashSet getSymbolSet() {
        return symbols;
    }
    
    public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("SymbolSetQueryAtom(");
		s.append(this.hashCode() + ", ");
		s.append(symbols.toString());
		s.append(")");
		return s.toString();
    }
}

