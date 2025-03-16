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
import org.openscience.cdk.interfaces.ICDKObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReaction;

/**
 * This class generates a CDK IReaction for a given IUPAC Reaction International Chemical Identifier (RInChI).
 * <p>
 * At its core, the conversion of a given RInChI to a CDK reaction object is based on the
 * <a href="https://github.com/dan2097/jna-inchi">JNA wrapper</a> for the native
 * <a href="https://github.com/IUPAC-InChI/InChI">InChI C++ library</a>.
 * Consequently, any limitation of {@link InChIToStructure} also impacts on the conversion implemented in
 * this class.
 * </p>
 * <p>
 * Please note that there are no exceptions thrown if an issue is encountered during processing. Instead,
 * a {@link Status} can be retrieved with {@link #getStatus()} that should be assessed. If the status is
 * not {@link Status#SUCCESS} emitted messages can be accessed with {@link #getMessages()}. These
 * messages should capture relevant information about what exactly went wrong.
 * </p>
 * Given a RInChI string a CDK reaction object can be produced as follows:
 * <pre>
 *     IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
 *     String rinchi = ....
 *     RInChIToReaction rinchiToReaction = new RInChIToReaction(rinchi, builder);
 *     if (rinchiToReaction.getStatus() == Status.SUCCESS) {
 *         IReaction reaction = rinchiToReaction.getReaction();
 *     } else {
 *         System.out.printf("RInChIToReaction came back with status %s: %s",
 *           rinchiToReaction.getStatus(), String.join("; ", rinchiToReaction.getMessages()));
 *     }
 * </pre>
 *
 * @author Uli Fechner
 * @cdk.githash
 */
public final class RInChIToReaction extends StatusMessagesOutput {

    private IReaction reaction;

    /**
     * Consumes a RInChI and produces a CDK Reaction.
     *
     * @param rinchi RInChI string
     * @param builder a builder to instantiate {@link ICDKObject} instances
     */
    RInChIToReaction(String rinchi, IChemObjectBuilder builder) {
        if (rinchi == null) {
            addMessage("RInChI string provided as argument is 'null'.", Status.ERROR);
            return;
        }
        if (builder == null) {
            addMessage("IChemObjectBuilder provided as argument is 'null'.", Status.ERROR);
            return;
        }

        try {
            this.reaction = generateReactionFromRinchi(rinchi, builder);
        } catch (CDKException exception) {
            addMessage(String.format("RInChI to Reaction failed: %s", exception.getMessage()), Status.ERROR);
        }
    }

    /**
     * Generates a chemical reaction from the provided RInChI string.
     *
     * @param rinchi the RInChI string representing the reaction
     * @param builder a builder to create instances of chemical objects
     * @return the IReaction object constructed from the given RInChI
     * @throws CDKException if there is an error during the decomposition of RInChI or conversion to chemical structures
     */
    private IReaction generateReactionFromRinchi(final String rinchi, IChemObjectBuilder builder) throws CDKException {
        // decompose rinchi into components
        RInChIDecomposition rinchiDecomposition = new RInChIDecomposition(rinchi).decompose();
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
