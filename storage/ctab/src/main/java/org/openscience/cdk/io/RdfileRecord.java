package org.openscience.cdk.io;

public class RdfileRecord {
    private String internalRegistryNumber;
    private String externalRegistryNumber;
    private boolean isRxnFile;
    private String content = "";
    private String dataBlock ="";

    public String getInternalRegistryNumber() {
        return internalRegistryNumber;
    }

    public void setInternalRegistryNumber(String internalRegistryNumber) {
        this.internalRegistryNumber = internalRegistryNumber;
    }

    public String getExternalRegistryNumber() {
        return externalRegistryNumber;
    }

    public void setExternalRegistryNumber(String externalRegistryNumber) {
        this.externalRegistryNumber = externalRegistryNumber;
    }

    public void setDataBlock(String dataBlock) {
        this.dataBlock = dataBlock;
    }

    public String getDataBlock() {
        return this.dataBlock;
    }

    public void setRxnFile(boolean isRxnFile) {
        this.isRxnFile = isRxnFile;
    }

    public void setMolfile(boolean isMolfile) {
        this.isRxnFile = !isMolfile;
    }

    public boolean isRxnFile() {
        return isRxnFile;
    }

    public boolean isMolfile() {
        return !isRxnFile;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(content);
        stringbuilder.append(dataBlock);
        return stringbuilder.toString();
    }
}
