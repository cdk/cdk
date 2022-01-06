/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.fingerprint;

import org.junit.Test;
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
    };

    /* override if method is implemented */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetCountFingerprint() throws Exception {
        getBitFingerprinter().getCountFingerprint(mock(IAtomContainer.class));
    }

    /* override if method is implemented */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetRawFingerprint() throws Exception {
        getBitFingerprinter().getRawFingerprint(mock(IAtomContainer.class));
    }
}
