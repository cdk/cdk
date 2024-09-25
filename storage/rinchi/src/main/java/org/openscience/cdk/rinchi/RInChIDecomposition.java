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

import org.openscience.cdk.ReactionRole;
import org.openscience.cdk.interfaces.IReaction;

import java.util.Collections;
import java.util.List;

/**
 * This class decomposes a RInChI into the individual InChIs and auxiliary Information (if available)
 * of each reaction component.
 * Moreover, roles of individual components (reactant, product, agent) and the reaction direction are returned.
 * <br>
 * A RInChI and its associated RAuxInfo can be decomposed into the constituent InChIs and AuxInfo as follows:
 * <pre>
 * RInChIDecomposition rinchiDecomposition = RInChIGeneratorFactory.getInstance().getRInChIDecomposition(rinchi);
 * List&lt;String&gt; inchis = rinchiDecomposition.getInchis();
 * List&lt;String&gt; auxInfos = rinchiDecomposition.getAuxInfo();
 * // getting the roles of the individual reaction components and the direction of the reaction
 * List&lt;ReactionComponentRole&gt; roles =  rinchiDecomposition.getReactionComponentRoles();
 * ReactionDirection direction = rinchiDecomposition.getReactionDirection();
 *
 * // there are also utility methods to get a map of (Inchi, AuxInfo) pairs ...
 * Map&lt;String,String&gt; inchiAuxInfoMap = rinchiDecomposition.getInchiAuxInfoMap();
 * // ... and a map of (inchi, reaction component roles) pairs
 * Map&lt;String,ReactionComponentRole&gt; inchiReactionComponentRoleMap = rinchiDecomposition.getInchiReactionComponentRoleMap();
 * </pre>
 *
 * @author Uli Fechner
 * @cdk.module rinchi
 * @cdk.githash
 */
public final class RInChIDecomposition extends StatusMessagesOutput{

    public static class Component {
        private final String inchi;
        private final String auxInfo;
        private final ReactionRole reactionRole;

        private Component(String inchi, String auxInfo, ReactionRole reactionRole) {
            this.inchi = inchi;
            this.auxInfo = auxInfo;
            this.reactionRole = reactionRole;
        }

        public String getInchi() {
            return inchi;
        }

        public String getAuxInfo() {
            return auxInfo;
        }

        public boolean hasAuxInfo() {
            return auxInfo != null;
        }

        public ReactionRole getReactionRole() {
            return reactionRole;
        }
    }

    private IReaction.Direction reactionDirection;
    private List<Component> components;

    /**
     * Decomposes a RInChI into a set of InChIs.
     *
     * @param rinchi RInChI string
     */
    RInChIDecomposition(String rinchi) {
        this(rinchi, "");
    }

    /**
     * Decomposes a RInChI and its auxiliary information into a set of InChIs and AuxInfo.
     *
     * @param rinchi  RInChI string
     * @param auxInfo RInChI aux info string
     */
    RInChIDecomposition(String rinchi, String auxInfo) {
        if (rinchi == null) {
            addMessage("RInChI string provided as input is 'null'.", Status.ERROR);
            return;
        }
        if (auxInfo == null) {
            addMessage("RInChI auxiliary info string provided as input is 'null'.", Status.ERROR);
            return;
        }

        decompose(rinchi, auxInfo);
    }

    private void decompose(String rinchi, String auxInfo) {
        // TODO implement logic
    }

    /**
     * Retrieves the list of reaction components generated during the RInChI
     * decomposition process. Each component contains InChI, its role in the
     * reaction, and auxiliary information if provided.
     *
     * @return an unmodifiable list of reaction components
     */
    public List<Component> getComponents() {
        return Collections.unmodifiableList(this.components);
    }

    /**
     * Returns RInChI reaction direction.
     *
     * @return the reaction direction of the RInChI
     */
    public IReaction.Direction getReactionDirection() {
        return this.reactionDirection;
    }
}
