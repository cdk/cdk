/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.atomtype;

import org.openscience.cdk.atomtype.IAtomTypeMatcher;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Interface for {@link org.openscience.cdk.atomtype.IAtomTypeMatcher} unit tests. It provides various methods
 * to allow such unit test classes to extend {@link AbstractAtomTypeTest} and
 * take advantage of the functionality that abstract class provides.
 *
 * @cdk.githash
 * @cdk.module test-core
 */
public interface IAtomTypeTest {

    /**
     * Returns a name for the atom type scheme being tested.
     *
     * @return a String of the name.
     */
    String getAtomTypeListName();

    /**
     * Returns an {@link AtomTypeFactory} instance for the atom type scheme
     * being tested. It is used to provide a list of atom types the scheme
     * defines.
     *
     * @return an {@link AtomTypeFactory} instance
     */
    AtomTypeFactory getFactory();

    /**
     * The {@link org.openscience.cdk.atomtype.IAtomTypeMatcher} being tested.
     *
     * @param builder the {@link IChemObjectBuilder} used to create atom types.
     * @return return an {@link org.openscience.cdk.atomtype.IAtomTypeMatcher} instance
     */
    IAtomTypeMatcher getAtomTypeMatcher(IChemObjectBuilder builder);

}
