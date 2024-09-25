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

import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.*;

/**
 * This class generates the IUPAC Reaction International Chemical Identifier (RInChI) for a CDK IReaction object.
 * <br>
 * Given an IReaction, RInChI, RAuxInfo, Long-RInChIKey, Short-RInChIKey and Web-RInChIKey can be generated with:
 * <pre>
 *     // all that's needed is an IReaction object, e.g., by loading an RXN file
 *     IReaction reaction = ....;
 *     RInChIGenerator generator = RInChIGeneratorFactory.getInstance().getRInChIGenerator(reaction);
 *     String rinchi = generator.getRInChI();
 *     String rAuxInfo = generator.getAuxInfo();
 *     String longKey = generator.getLongRInChIKey();
 *     String shortKey = generator.getShortRInChIKey();
 *     String webKey = generator.getWebRInChIKey();
 * </pre>
 *
 * @author Uli Fechner
 * @cdk.module rinchi
 * @cdk.githash
 */

public final class RInChIGenerator {

    public enum Status {
        /** Success; no errors or warnings. */
        SUCCESS,
        /** Success; warning(s) issued. */
        WARNING,
        /** Error; no result was obtained. */
        ERROR
    }

    private static final ILoggingTool LOGGER = LoggingToolFactory.createLoggingTool(RInChIGenerator.class);
    private static final EnumSet<RInChIOption> DEFAULT_OPTIONS = EnumSet.noneOf(RInChIOption.class);

    private final EnumSet<RInChIOption> rinchiOptions;
    private String rinchi;
    private String auxInfo;
    private String shortRinchiKeyOutput;
    private String longRinchiKeyOutput;
    private String webRinchiKeyOutput;
    private Status status;
    private final List<String> messages = new ArrayList<>();

    /**
     * Generates RInChI from a CDK Reaction.
     *
     * @param reaction reaction to generate RInChI for
     * @param options  zero or more optional RInChI generation options
     */
    RInChIGenerator(IReaction reaction, RInChIOption... options) {
        this.rinchiOptions = ((options == null || options.length == 0) ? DEFAULT_OPTIONS : EnumSet.copyOf(Arrays.asList(options)));
        generateRinchiFromReaction(reaction);
    }

    private void generateRinchiFromReaction(final IReaction reaction) {
        if (reaction == null) {
            this.status = Status.ERROR;
            this.messages.add("IReaction object provided as input is 'null'.");
            return;
        }

        // TODO implement logic here
    }

    /**
     * Gets generated RInChI string.
     *
     * @return generated RInChI
     */
    public String getRInChI() {
        return this.rinchi;
    }

    /**
     * Gets auxiliary information.
     *
     * @return RInChI AuxInfo
     */
    public String getAuxInfo() {
        return this.auxInfo;
    }

    /**
     * Returns Short-RInChIKey.
     *
     * @return Short-RInChIKey
     */
    public String getShortRInChIKey() {
        return this.shortRinchiKeyOutput;
    }

    /**
     * Returns Long-RInChIKey.
     *
     * @return Long-RInChIKey
     */
    public String getLongRInChIKey() {
        return this.longRinchiKeyOutput;
    }

    /**
     * Returns Web-RInChIKey.
     *
     * @return Web-RInChIKey
     */
    public String getWebRInChIKey() {
        return this.webRinchiKeyOutput;
    }

    /**
     * Returns the status of the RInChI output.
     *
     * @return the status
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * Retrieves an unmodifiable list of messages generated during the RInChI generation process.
     *
     * @return a list of messages
     */
    public List<String> getMessages() {
        return Collections.unmodifiableList(messages);
    }
}
