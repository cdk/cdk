package org.openscience.cdk.io;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IReaction;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
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

    public String getInternalRegistryNumber() {
        return internalRegistryNumber;
    }

    public String getExternalRegistryNumber() {
        return externalRegistryNumber;
    }

    void setData(Map<Object,Object> data) {
        this.data = data;
    }

    Map<Object,Object> getData() {
        return Collections.unmodifiableMap(data);
    }

    public boolean isRxnFile() {
        return isRxnFile;
    }

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

    public IAtomContainer getAtomContainer() {
        if (chemObject instanceof IAtomContainer) {
            return (IAtomContainer) chemObject;
        }

        return null;
    }

    public IReaction getReaction() {
        if (chemObject instanceof IReaction) {
            return (IReaction) chemObject;
        }

        return null;
    }
}
