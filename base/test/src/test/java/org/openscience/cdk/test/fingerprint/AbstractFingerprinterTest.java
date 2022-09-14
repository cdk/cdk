/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.fingerprint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;

import static org.mockito.Mockito.mock;

/**
 * @cdk.module test
 */
public abstract class AbstractFingerprinterTest extends CDKTestCase {

    public IFingerprinter getBitFingerprinter() {
        throw new IllegalAccessError("This method should be overwritten " + "by subclasses unit tests");
    }

    /* override if method is implemented */
    @Test
    public void testGetCountFingerprint() throws Exception {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> {
            getBitFingerprinter().getCountFingerprint(mock(IAtomContainer.class));
        });
    }

    /* override if method is implemented */
    @Test
    public void testGetRawFingerprint() throws Exception {
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> {
            getBitFingerprinter().getRawFingerprint(mock(IAtomContainer.class));
        });
    }
}
