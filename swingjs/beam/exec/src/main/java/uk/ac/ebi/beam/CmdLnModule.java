/*
 * Copyright (c) 2015. John May
 */

package uk.ac.ebi.beam;

/**
 * A plugable command line module providing functionality
 * from the primary dispatch {@link Main}. Modules should
 * be implemented and added to an SPI
 * (META-INF/services/uk.ac.ebi.beam.CmdLnModule).
 *
 * @see <a href="https://docs.oracle.com/javase/tutorial/ext/basics/spi.html">SPI</a>
 */
public interface CmdLnModule {

    /**
     * The module name is a concise, often abbreviated, description
     * of the module function. It is used to reference the
     * functionality from the primary dispatch:
     * <pre>{@code $ beam {module.name} {module.args}}</pre>
     *
     * @return module name
     */
    String name();

    /**
     * Displays the usage/help for this module.
     *
     * @return help info
     */
    String getHelpInfo();

    /**
     * Executes the module with the specified arguments. The
     * arguments do not include the module name.
     *
     * @param args command line arguments
     */
    void exec(String[] args);
}
