package org.openscience.cdk.rinchi;

import org.openscience.cdk.exception.CDKException;

public class RInChIException extends CDKException {
    public RInChIException(String message) {
        super(message);
    }

    public RInChIException(String message, Throwable cause) {
        super(message, cause);
    }
}
