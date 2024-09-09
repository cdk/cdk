/*
 * Copyright (C) 2022 Uli Fechner
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.openscience.cdk.io;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IReaction;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The RdfileRecord class represents a single record of an RD file.
 * <br><br>
 * <ul>
 * Publicly accessible are
 * <li>the record's {@link #getInternalRegistryNumber() internal} or
 * {@link #getExternalRegistryNumber() external} registry number,</li>
 * <li>methods to asses whether the record is a {@link #isRxnFile() reaction}
 * or a {@link #isMolfile() molecule}, and</li>
 * <li>getters for the {@link #getAtomContainer() IAtomContainer} or
 * {@link #getReaction() reaction} that represents the record.</li>
 * </ul>
 *
 * @see RdfileReader
 * @author Uli Fechner
 */
public final class RdfileRecord {
    enum CTAB_VERSION {
        V2000,
        V3000
    }

    private final String internalRegistryNumber;
    private final String externalRegistryNumber;
    private final boolean isRxnFile;
    private String content = "";
    private CTAB_VERSION ctabVersion;
    private Map<Object,Object> data = new LinkedHashMap<>();
    private IChemObject chemObject;

    RdfileRecord(String internalRegistryNumber, String externalRegistryNumber, boolean isRxnFile) {
        this.internalRegistryNumber = internalRegistryNumber;
        this.externalRegistryNumber = externalRegistryNumber;
        this.isRxnFile = isRxnFile;
    }

    /**
     * Retrieves the internal registry number of the RdfileRecord.
     *
     * @return The internal registry number of the RdfileRecord.
     */
    public String getInternalRegistryNumber() {
        return internalRegistryNumber;
    }

    /**
     * Retrieves the external registry number of the RdfileRecord.
     *
     * @return The external registry number of the RdfileRecord.
     */
    public String getExternalRegistryNumber() {
        return externalRegistryNumber;
    }

    void setData(Map<Object,Object> data) {
        this.data = data;
    }

    Map<Object,Object> getData() {
        return Collections.unmodifiableMap(data);
    }

    /**
     * Checks if the RdfileRecord represents a reaction file.
     *
     * @return true if the RdfileRecord represents a reaction file, false otherwise
     */
    public boolean isRxnFile() {
        return isRxnFile;
    }

    /**
     * Checks if the RdfileRecord represents a molfile.
     *
     * @return true if the RdfileRecord represents a molfile, false otherwise
     */
    public boolean isMolfile() {
        return !isRxnFile;
    }

    void setContent(String content) {
        this.content = content;
    }

    String getContent() {
        return this.content;
    }

    CTAB_VERSION getCtabVersion() {
        return ctabVersion;
    }

    void setCtabVersion(CTAB_VERSION ctabVersion) {
        this.ctabVersion = ctabVersion;
    }

    void setChemObject(IChemObject chemObject) {
        this.chemObject = chemObject;
    }

    /**
     * Retrieves the AtomContainer from the RdfileRecord.
     *
     * @return the AtomContainer object if the record represents a molecule, {@code null} otherwise
     */
    public IAtomContainer getAtomContainer() {
        if (chemObject instanceof IAtomContainer) {
            return (IAtomContainer) chemObject;
        }

        return null;
    }

    /**
     * Retrieves the reaction object from the RdfileRecord.
     *
     * @return the reaction object if the RdfileRecord represents a reaction, {@code null} otherwise
     */
    public IReaction getReaction() {
        if (chemObject instanceof IReaction) {
            return (IReaction) chemObject;
        }

        return null;
    }
}
