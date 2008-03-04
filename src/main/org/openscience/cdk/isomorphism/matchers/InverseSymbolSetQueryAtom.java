/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.isomorphism.matchers;

import java.util.HashSet;

import org.openscience.cdk.interfaces.IAtom;

/**
 *  A QueryAtom that matches all symbols but those in this container. You may
 *  add symbols to this container. This QueryAtom will only give a match if it
 *  does NOT contain the symbol of the Atom to match (example: add "C" to get a
 *  match for all non-"C"-Atoms).
 *
 *@author        kha
 * @cdk.svnrev  $Revision$
 *@cdk.created   2004-09-16
 *@see           SymbolSetQueryAtom
 *@cdk.module    isomorphism
 */
public class InverseSymbolSetQueryAtom extends org.openscience.cdk.PseudoAtom implements IQueryAtom {

    private static final long serialVersionUID = -6570190504347822438L;
    
    private HashSet symbols = new HashSet();


    /**
     *  Constructor for the InverseSymbolSetQueryAtom object
     */
    public InverseSymbolSetQueryAtom() { }
    public void setOperator(String str){}

    /**
     *  The matches implementation of the QueryAtom interface.
     *
     *@param  atom  The atom to be matched by this QueryAtom
     *@return       true if Atom matched
     */
    public boolean matches(IAtom atom) {
        return !symbols.contains(atom.getSymbol());
    }


    /**
     *  Add a symbol to this QueryAtom
     *
     *@param  symbol  The symbol to add
     */
    public void addSymbol(String symbol) {
        symbols.add(symbol);
    }


    /**
     *  Remove a symbol from this QueryAtom
     *
     *@param  symbol  The symbol to remove
     */
    public void removeSymbol(String symbol) {
        symbols.remove(symbol);
    }


    /**
     *  Check whether a symbol is already registered
     *
     *@param  symbol  The symbol to check for
     *@return         true if symbol already registered
     */
    public boolean hasSymbol(String symbol) {
        return symbols.contains(symbol);
    }


    /**
     *  Retrieve the Set of symbols
     *
     *@return    The symbol Set
     */
    public HashSet getSymbolSet() {
        return symbols;
    }


    /**
     *  The toString method
     *
     *@return    The String representation of this object.
     */
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("InverseSymbolSetQueryAtom(");
        s.append(this.hashCode() + ", ");
        s.append(symbols.toString());
        s.append(")");
        return s.toString();
    }
}

