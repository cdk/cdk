/* Copyright (C) 2005-2015  Egon Willighagen <egonw@users.sf.net>
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

import javax.vecmath.Point3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IPDBAtom;

/**
 * Represents the idea of an atom as used in PDB files. It contains extra fields
 * normally associated with atoms in such files.
 *
 * @cdk.module data
 * @cdk.githash
 *
 * @see  Atom
 */
public class PDBAtom extends Atom implements Cloneable, IPDBAtom {

    /**
     * Determines if a deserialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href="http://java.sun.com/products/jdk/1.1/docs/guide/serialization/spec/version.doc.html">details</a>.
     */
    private static final long serialVersionUID = 7670650135045832543L;

    private String            record;
    private double            tempFactor;
    private String            resName;
    private String            iCode;
    private double            occupancy;
    private String            name;
    private String            chainID;
    private String            altLoc;
    private String            segID;
    private int               serial;
    private String            resSeq;
    private boolean           oxt;
    private boolean           hetAtom;

    /**
     * Constructs an IPDBAtom from a Element.
     *
     * @param element IElement to copy information from
     */
    public PDBAtom(IElement element) {
        super(element);
        initValues();
    }

    /**
     * Constructs an {@link IPDBAtom} from a String containing an element symbol.
     *
     * @param symbol  The String describing the element for the PDBAtom
     */
    public PDBAtom(String symbol) {
        super(symbol);
        initValues();
    }

    /**
     * Constructs an {@link IPDBAtom} from an Element and a Point3d.
     *
     * @param  symbol     The symbol of the atom
     * @param  coordinate The 3D coordinates of the atom
     */
    public PDBAtom(String symbol, Point3d coordinate) {
        super(symbol, coordinate);
        initValues();
    }

    private void initValues() {
        record = null;
        tempFactor = -1.0;
        resName = null;
        iCode = null;
        occupancy = -1.0;
        name = null;
        chainID = null;
        altLoc = null;
        segID = null;
        serial = 0;
        resSeq = null;

        oxt = false;
        hetAtom = false;

        super.charge = Double.valueOf(0.0);
        super.formalCharge = Integer.valueOf(0);
    }

    /**
     * get one entire line from the PDB entry file which describe the IPDBAtom.
     * It consists of 80 columns.
     *
     * @return a String with all information
     */
    @Override
    public String getRecord() {
        return record;
    }

    /**
     * set one entire line from the PDB entry file which describe the IPDBAtom.
     * It consists of 80 columns.
     *
     * @param newRecord A String with all information
     */
    @Override
    public void setRecord(String newRecord) {
        record = newRecord;
    }

    /**
     * get the Temperature factor of this atom.
     *
     * @return the Temperature factor of this atom
     */
    @Override
    public Double getTempFactor() {
        return tempFactor;
    }

    /**
     * set the Temperature factor of this atom.
     *
     * @param newTempFactor  the Temperature factor of this atom
     */
    @Override
    public void setTempFactor(Double newTempFactor) {
        tempFactor = newTempFactor;
    }

    /**
     * set the Residue name of this atom.
     *
     * @param newResName  the Residue name of this atom
     */
    @Override
    public void setResName(String newResName) {
        resName = newResName;
    }

    /**
     * get the Residue name of this atom.
     *
     * @return the Residue name of this atom
     */
    @Override
    public String getResName() {
        return resName;
    }

    /**
     * set the Code for insertion of residues of this atom.
     *
     * @param newICode  the Code for insertion of residues of this atom
     */
    @Override
    public void setICode(String newICode) {
        iCode = newICode;
    }

    /**
     * get Code for insertion of residues of this atom.
     *
     * @return the Code for insertion of residues of this atom
     */
    @Override
    public String getICode() {
        return iCode;
    }

    /**
     * set the Atom name of this atom.
     *
     * @param newName  the Atom name of this atom
     */
    @Override
    public void setName(String newName) {
        name = newName;
    }

    /**
     * get the Atom name of this atom.
     *
     * @return the Atom name of this atom
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * set the Chain identifier of this atom.
     *
     * @param newChainID  the Chain identifier of this atom
     */
    @Override
    public void setChainID(String newChainID) {
        chainID = newChainID;
    }

    /**
     * get the Chain identifier of this atom.
     *
     * @return the Chain identifier of this atom
     */
    @Override
    public String getChainID() {
        return chainID;
    }

    /**
     * set the Alternate location indicator of this atom.
     *
     * @param newAltLoc  the Alternate location indicator of this atom
     */
    @Override
    public void setAltLoc(String newAltLoc) {
        altLoc = newAltLoc;
    }

    /**
     * get the Alternate location indicator of this atom.
     *
     * @return the Alternate location indicator of this atom
     */
    @Override
    public String getAltLoc() {
        return altLoc;
    }

    /**
     * set the Segment identifier, left-justified of this atom.
     *
     * @param newSegID  the Segment identifier, left-justified of this atom
     */
    @Override
    public void setSegID(String newSegID) {
        segID = newSegID;
    }

    /**
     * get the Segment identifier, left-justified of this atom.
     *
     * @return the Segment identifier, left-justified of this atom
     */
    @Override
    public String getSegID() {
        return segID;
    }

    /**
     * set the Atom serial number of this atom.
     *
     * @param newSerial  the Atom serial number of this atom
     */
    @Override
    public void setSerial(Integer newSerial) {
        serial = newSerial;
    }

    /**
     * get the Atom serial number of this atom.
     *
     * @return the Atom serial number of this atom
     */
    @Override
    public Integer getSerial() {
        return serial;
    }

    /**
     * set the Residue sequence number of this atom.
     *
     * @param newResSeq  the Residue sequence number of this atom
     */
    @Override
    public void setResSeq(String newResSeq) {
        resSeq = newResSeq;
    }

    /**
     * get the Residue sequence number of this atom.
     *
     * @return the Residue sequence number of this atom
     */
    @Override
    public String getResSeq() {
        return resSeq;
    }

    @Override
    public void setOxt(Boolean newOxt) {
        oxt = newOxt;
    }

    @Override
    public Boolean getOxt() {
        return oxt;
    }

    @Override
    public void setHetAtom(Boolean newHetAtom) {
        hetAtom = newHetAtom;
    }

    @Override
    public Boolean getHetAtom() {
        return hetAtom;
    }

    /**
     * set the Occupancy of this atom.
     *
     * @param newOccupancy  the Occupancy of this atom
     */
    @Override
    public void setOccupancy(Double newOccupancy) {
        occupancy = newOccupancy;
    }

    /**
     * get the Occupancy of this atom.
     *
     * @return the Occupancy of this atom
     */
    @Override
    public Double getOccupancy() {
        return occupancy;
    }

    /**
     * Returns a one line string representation of this Atom.
     * Methods is conform RFC #9.
     *
     * @return  The string representation of this Atom
     */
    @Override
    public String toString() {
        StringBuilder description = new StringBuilder(150);
        description.append("PDBAtom(");
        description.append(this.hashCode());
        description.append(", altLoc=").append(getAltLoc());
        description.append(", chainID=").append(getChainID());
        description.append(", iCode=").append(getICode());
        description.append(", name=").append(getName());
        description.append(", resName=").append(getResName());
        description.append(", resSeq=").append(getResSeq());
        description.append(", segID=").append(getSegID());
        description.append(", serial=").append(getSerial());
        description.append(", tempFactor=").append(getTempFactor());
        description.append(", oxt=").append(getOxt());
        description.append(", hetatm=").append(getHetAtom()).append(", ");
        description.append(super.toString());
        description.append(')');
        return description.toString();
    }

}
