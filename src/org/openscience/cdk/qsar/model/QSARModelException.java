package org.openscience.cdk.qsar.model;

import org.openscience.cdk.exception.CDKException;

/**
 * Exception that is thrown by model routines when a problem has occured
 *
 * @author Rajarshi Guha
 * @cdk.module qsar
 */
public class QSARModelException extends CDKException {

    /**
     * Constructs a new QSARModelException with the given message.
     *
     * @param message for the constructed exception
     */
    public QSARModelException(String message) {
        super( message );
    }
}

