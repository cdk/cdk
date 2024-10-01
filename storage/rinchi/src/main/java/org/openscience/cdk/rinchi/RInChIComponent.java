package org.openscience.cdk.rinchi;

import io.github.dan2097.jnainchi.InchiStatus;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;

class RInChIComponent extends StatusMessagesOutput {

    private boolean isNoStructure;
    private String inchi;
    private String auxInfo;
    private String inchiKey;

    protected RInChIComponent(InChIGenerator gen){
        if (gen == null || gen.getStatus() == InchiStatus.ERROR || gen.getInchi() == null || gen.getInchi().isEmpty()) {
            this.isNoStructure = true;
            return;
        }
        this.inchi = gen.getInchi();
        this.auxInfo = gen.getAuxInfo();
        try {
            this.inchiKey = gen.getInchiKey();
        } catch (CDKException e) {
            addMessage(e.getMessage(), Status.ERROR);
        }
    }

    protected boolean isNoStructure() {
        return this.isNoStructure;
    }

    protected String getInchi() {
        return this.inchi;
    }

    protected String getAuxInfo() {
        return this.auxInfo;
    }

    protected String getInchiKey() {
        return this.inchiKey;
    }
}
