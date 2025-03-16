/* Copyright (C) 2024 Uli Fechner
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.rinchi;

/**
 * Options for RInChI processing.
 * <br><br>
 * A builder is used to put together the desired options:
 * <pre>
 * // default options use a 5000 msec timeout per reaction component
 * RInChIOptions optionsDefault = RInChIOptions.builder().build();
 *
 * // the timeout can also be specified
 * RInChIOptions optionsWithTimeout = RInChIOptions.builder().timeoutMillisecondsPerComponent(5000).build();
 * </pre>
 *
 * @author Uli Fechner
 * @cdk.githash
 */
public final class RInChIOptions {
    /**
     * Default configuration for RInChI processing options with a timeout of 5000 milliseconds per reaction component.
     */
    public static final RInChIOptions DEFAULT_OPTIONS = RInChIOptions.builder().timeoutMillisecondsPerComponent(5000).build();

    private final static long NO_TIMEOUT = -1;
    private final boolean forceEquilibrium;

    private final long timeoutMillisecondsPerComponent;

    private RInChIOptions(final boolean forceEquilibrium, final long timeoutMilliseconds) {
        this.forceEquilibrium = forceEquilibrium;
        this.timeoutMillisecondsPerComponent = timeoutMilliseconds;
    }

    /**
     * Determines whether the force equilibrium option is enabled.
     *
     * @return true if force equilibrium is enabled, false otherwise.
     */
    public boolean isForceEquilibrium() {
        return forceEquilibrium;
    }

    /**
     * Checks if a timeout is set for the components.
     *
     * @return true if a timeout is set, false if there is no timeout.
     */
    public boolean hasTimeout() {
        return timeoutMillisecondsPerComponent != NO_TIMEOUT;
    }

    /**
     * Retrieves the timeout value in milliseconds that is set for processing each reaction component.
     * A value of {@link #NO_TIMEOUT} indicates no timeout is.
     *
     * @return timeout in milliseconds for each reaction component
     */
    public long getTimeoutMillisecondsPerComponent() {
        return timeoutMillisecondsPerComponent;
    }

    /**
     * Returns a RInChIOptionBuilder.
     *
     * @return new RInChIOptionBuilder instance for building RInChIOptions with custom settings
     */
    public static RInChIOptionBuilder builder() {
        return new RInChIOptionBuilder();
    }

    /**
     * Builder class for constructing instances of RInChIOptions with customized settings.
     * <br>
     * Default values are
     * <ul>
     * <li>{@code forceEquilibrium}: false</li>
     * <li>{@code timeout}: not specified</li>
     * </ul>
     */
    public static class RInChIOptionBuilder {
        private boolean forceEquilibrium = false;
        private long timeoutMillisecondsPerComponent = NO_TIMEOUT;

        private RInChIOptionBuilder() {}

        /**
         * Sets the option to force equilibrium processing in the RInChIOptions.
         *
         * @param forceEquilibrium a boolean indicating whether to force equilibrium processing
         * @return the current instance of RInChIOptionBuilder
         */
        public RInChIOptionBuilder forceEquilibrium(boolean forceEquilibrium) {
            this.forceEquilibrium = forceEquilibrium;
            return this;
        }

        /**
         * Sets the timeout in milliseconds for processing each reaction component in this RInChIOptions.
         *
         * @param timeoutMillisecondsPerComponent the timeout duration for each component, must be zero or positive
         * @return the current instance of RInChIOptionBuilder
         * @throws IllegalArgumentException if the specified timeout is negative
         */
        public RInChIOptionBuilder timeoutMillisecondsPerComponent(final long timeoutMillisecondsPerComponent) {
            if (timeoutMillisecondsPerComponent < 0) {
                throw new IllegalArgumentException("Timeout must be greater than or equal to zero.");
            }
            this.timeoutMillisecondsPerComponent = timeoutMillisecondsPerComponent;
            return this;
        }

        /**
         * Creates an instance of {@code RInChIOptions} using the configuration of this {code }RInChIOptionBuilder}.
         *
         * @return new RInChIOptions instance
         */
        public RInChIOptions build() {
            return new RInChIOptions(forceEquilibrium, timeoutMillisecondsPerComponent);
        }
    }
}