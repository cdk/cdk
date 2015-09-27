/* Copyright (C) 2005-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.protein.data;

import org.openscience.cdk.Monomer;
import org.openscience.cdk.interfaces.IPDBMonomer;

/**
 * Represents the idea of an monomer as used in PDB files. It contains extra fields
 * normally associated with atoms in such files.
 *
 * @cdk.module data
 * @cdk.githash
 *
 * @see  PDBAtom
 */
public class PDBMonomer extends Monomer implements Cloneable, IPDBMonomer {

    private static final long serialVersionUID = -7236625816763776733L;

    private String            iCode;

    /**
     * Denotes which chain in the PDB file this monomer is in.
     */
    private String            chainID;

    /**
     * Denotes which residue sequence in the current chain that this monomer is in.
     */
    private String            resSeq;

    public PDBMonomer() {
        super();
        initValues();
    }

    private void initValues() {
        iCode = null;
        chainID = null;
    }

    @Override
    public void setICode(String newICode) {
        iCode = newICode;
    }

    @Override
    public String getICode() {
        return iCode;
    }

    @Override
    public void setChainID(String newChainID) {
        chainID = newChainID;
    }

    @Override
    public String getChainID() {
        return chainID;
    }

    /**
     * Returns a one line string representation of this Atom.
     * Methods is conform RFC #9.
     *
     * @return  The string representation of this Atom
     */
    @Override
    public String toString() {
        StringBuilder description = new StringBuilder(50);
        description.append("PDBMonomer(");
        description.append(this.hashCode());
        description.append(", iCode=").append(getICode());
        description.append(", chainID=").append(getChainID()).append(", ");
        description.append(super.toString());
        description.append(')');
        return description.toString();
    }

    @Override
    public String getResSeq() {
        return resSeq;
    }

    /**
     * @inheritDoc
     */
    @Override
    public IPDBMonomer clone() throws CloneNotSupportedException {
        return (IPDBMonomer) super.clone();
    }

    @Override
    public void setResSeq(String resSeq) {
        this.resSeq = resSeq;
    }

}
