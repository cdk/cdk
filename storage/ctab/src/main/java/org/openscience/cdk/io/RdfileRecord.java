package org.openscience.cdk.io;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IReaction;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RdfileRecord {
    private final String internalRegistryNumber;
    private final String externalRegistryNumber;
    private final boolean isRxnFile;
    private String content = "";
    private IAtomContainer atomContainer;
    private IReaction reaction;
    private Map<String,String> data = new LinkedHashMap<>();

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

    void setData(Map<String,String> data) {
        this.data = data;
    }

    public Map<String,String> getData() {
        return Collections.unmodifiableMap(data);
    }

    public String getDataValue(String key) {
        return data.get(key);
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

    public IAtomContainer getAtomContainer() {
        return atomContainer;
    }

    void setAtomContainer(IAtomContainer atomContainer) {
        this.atomContainer = atomContainer;
    }

    public IReaction getReaction() {
        return reaction;
    }

    void setReaction(IReaction reaction) {
        this.reaction = reaction;
    }
}
