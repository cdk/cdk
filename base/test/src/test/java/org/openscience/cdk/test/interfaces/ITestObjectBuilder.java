/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.openscience.cdk.interfaces.IChemObject;

/**
 * Interfaces for objects that create new, clean test objects to be used by
 * unit testing for the module <code>data</code>, <code>datadebug</code> and
 * <code>nonotify</code>.
 *
 * @cdk.module  test-interfaces
 */
public interface ITestObjectBuilder {

    /**
     * Returns a clean new test object.
     * @return a new test object
     */
    IChemObject newTestObject();

}
