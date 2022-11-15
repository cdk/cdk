package org.openscience.cdk.io;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RdfileRecord {
    private final String internalRegistryNumber;
    private final String externalRegistryNumber;
    private final boolean isRxnFile;
    private String content = "";
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

    public String getContent() {
        return this.content;
    }
}
