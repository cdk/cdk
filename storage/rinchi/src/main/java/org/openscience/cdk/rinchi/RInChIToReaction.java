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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class generates the IUPAC Reaction International Chemical Identifier (RInChI) for a CDK IReaction object.
 * <br>
 * Given a RInChI and optionally its RAuxInfo here is how to generate an IReaction:
 * <pre>
 *     RInChIToReaction rinchiToReaction = RInChIGeneratorFactory.getInstance().getRInChIToReaction(rinchi, rAuxInfo);
 *     IReaction reaction = rinchiToReaction.getReaction();
 *
 *     // if a RAuxInfo isn't available an overloaded method can be called
 *     RInChIToReaction rinchiToReactionNoRauxinfo = RInChIGeneratorFactory.getInstance().getRInChIToReaction(rinchi);
 *     IReaction reaction2 = rinchiToReactionNoRauxinfo.getReaction();
 * </pre>
 *
 * @author Uli Fechner
 * @cdk.module rinchi
 * @cdk.githash
 */
public final class RInChIToReaction {

    public enum Status {
        /** Success; no errors or warnings. */
        SUCCESS,
        /** Success; warning(s) issued. */
        WARNING,
        /** Error; no result was obtained. */
        ERROR
    }

    private IReaction reaction;
    private Status status;
    private final List<String> messages = new ArrayList<>();

    /**
     * Consumes a RInChI and produces a CDK Reaction.
     *
     * @param rinchi RInChI string
     */
    protected RInChIToReaction(String rinchi) {
        this(rinchi, "");
    }

    /**
     * Consumes a RInChI with associated auxiliary information and produces a CDK Reaction.
     *
     * @param rinchi                      RInChI string
     * @param auxInfo                     RInChI auxiliary information (AuxInfo) string
     */
    protected RInChIToReaction(String rinchi, String auxInfo) {
        // TODO consider removing exceptions and setting status/msg
        if (rinchi == null)
            throw new IllegalArgumentException("Null RInChI string provided");
        if (auxInfo == null)
            throw new IllegalArgumentException("Null RInChI aux info string provided");

        generateReactionFromRinchi();
    }

    /**
     * Produces a reaction from given RInChI.
     * The RInChI library data structure (RinchiInput object) is converted to an {@link IReaction}.
     */
    void generateReactionFromRinchi() {
        // TODO implement logic here
    }

    /**
     * Returns generated reaction.
     *
     * @return the reaction object generated from the RInChI
     */
    public IReaction getReaction() {
        return reaction;
    }

    /**
     * Access the status of the RInChI output.
     *
     * @return the status
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * Returns an unmodifiable list of warning and error messages associated with generating the reaction.
     *
     * @return an unmodifiable list of messages
     */
    public List<String> getMessages() {
        return Collections.unmodifiableList(this.messages);
    }
}
