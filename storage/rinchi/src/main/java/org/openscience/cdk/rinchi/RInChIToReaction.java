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

import io.github.dan2097.jnainchi.InchiStatus;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReaction;

/**
 * This class generates a CDK IReaction for a given IUPAC Reaction International Chemical Identifier (RInChI).
 *
 * @author Uli Fechner
 * @cdk.module rinchi
 * @cdk.githash
 */
public final class RInChIToReaction extends StatusMessagesOutput {

    private IReaction reaction;

    /**
     * Consumes a RInChI and produces a CDK Reaction.
     *
     * @param rinchi RInChI string
     */
    RInChIToReaction(String rinchi, IChemObjectBuilder builder) {
        this(rinchi, "", builder);
    }

    /**
     * Consumes a RInChI with associated auxiliary information and produces a CDK Reaction.
     *
     * @param rinchi                      RInChI string
     * @param auxInfo                     RInChI auxiliary information (AuxInfo) string
     */
    RInChIToReaction(String rinchi, String auxInfo, IChemObjectBuilder builder) {
        if (rinchi == null) {
            addMessage("RInChI string provided as argument is 'null'.", Status.ERROR);
            return;
        }
        if (auxInfo == null) {
            addMessage("RInChI auxiliary information string provided as argument is 'null'.", Status.ERROR);
            return;
        }
        if (builder == null) {
            addMessage("IChemObjectBuilder provided as argument is 'null'.", Status.ERROR);
            return;
        }

        try {
            this.reaction = generateReactionFromRinchi(rinchi, auxInfo, builder);
        } catch (CDKException exception) {
            addMessage(String.format("RInChI to Reaction failed: %s", exception.getMessage()), Status.ERROR);
        }
    }

    /**
     * Produces a reaction from given RInChI.
     * The RInChI library data structure (RinchiInput object) is converted to an {@link IReaction}.
     */
    private IReaction generateReactionFromRinchi(final String rinchi, final String rAuxInfo, IChemObjectBuilder builder) throws CDKException {
        // decompose rinchi into components
        RInChIDecomposition rinchiDecomposition = new RInChIDecomposition(rinchi, rAuxInfo).decompose();
        if (rinchiDecomposition.getStatus() == Status.ERROR) {
            throw new RInChIException(String.format("Encountered issue with decomposing RInChI and/or RAuxInfo: %s", String.join("; ", rinchiDecomposition.getMessages())));
        }

        this.reaction = builder.newReaction();
        for(RInChIDecomposition.Component component: rinchiDecomposition.getComponents()) {
            InChIToStructure inChIToStructure = InChIGeneratorFactory.getInstance().getInChIToStructure(component.getInchi(), builder);
            if (inChIToStructure.getStatus() == InchiStatus.SUCCESS) {
                switch(component.getReactionRole()) {
                    case Reactant:
                        this.reaction.addReactant(inChIToStructure.getAtomContainer());
                        break;
                    case Product:
                        this.reaction.addProduct(inChIToStructure.getAtomContainer());
                        break;
                    case Agent:
                        this.reaction.addAgent(inChIToStructure.getAtomContainer());
                        break;
                    default:
                        throw new RInChIException(String.format("Encountered unexpected reaction role: %s", component.getReactionRole().toString()));
                }
            } else {
                throw new RInChIException(String.format("Encountered issue with InChIToStructure: %s", String.join("; ", inChIToStructure.getMessage())));
            }
        }

        return reaction;
    }

    /**
     * Returns generated reaction.
     *
     * @return the reaction object generated from the RInChI
     */
    public IReaction getReaction() {
        return reaction;
    }
}
