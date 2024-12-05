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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
public final class RInChIDecomposition extends StatusMessagesOutput {

    public static class Component {
        private final String inchi;
        private final String auxInfo;
        private final ReactionRole reactionRole;

        Component(String inchi, String auxInfo, ReactionRole reactionRole) {
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
            return !auxInfo.isEmpty();
        }

        public ReactionRole getReactionRole() {
            return reactionRole;
        }

        @Override
        public int hashCode() {
            return Objects.hash(inchi, auxInfo, reactionRole);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Component component = (Component) o;
            return inchi.equals(component.inchi) &&
                    auxInfo.equals(component.auxInfo) &&
                    reactionRole == component.reactionRole;
        }

        @Override
        public String toString() {
            return "Component{" +
                    "inchi='" + inchi + '\'' +
                    ", auxInfo='" + auxInfo + '\'' +
                    ", reactionRole=" + reactionRole +
                    '}';
        }
    }

    static final String PATTERN_GROUP_LAYER_2 = "patternGroupLayer2";
    static final String PATTERN_GROUP_LAYER_3 = "patternGroupLayer3";
    static final String PATTERN_GROUP_LAYER_4 = "patternGroupLayer4";
    static final String PATTERN_GROUP_DIRECTION = "patternGroupDirection";
    static final String PATTERN_GROUP_NOSTRUCT_1 = "patternGroupNoStruct1";
    static final String PATTERN_GROUP_NOSTRUCT_2 = "patternGroupNoStruct2";
    static final String PATTERN_GROUP_NOSTRUCT_3 = "patternGroupNoStruct3";
    static final Pattern RINCHI_PATTERN = Pattern.compile(
            "^" +
            // layer 1, mandatory
            RInChIConstants.RINCHI_STD_HEADER +
                    // layer 2, optional
                    "(?<" + PATTERN_GROUP_LAYER_2 + ">(?:(?!/d|/u).)*?)?" +
                    // layer 3, optional
                    "(?:<>(?<" + PATTERN_GROUP_LAYER_3 + ">(?:(?!/d|/u).)*?))?" +
                    // layer 4, optional
                    "(?:<>(?<" + PATTERN_GROUP_LAYER_4 + ">(?:(?!/d|/u).)*?))?" +
                    // layer 5, optional
                    "(?:" + RInChIConstants.DIRECTION_TAG + "(?<" + PATTERN_GROUP_DIRECTION + ">[=+-]))?" +
                    // layer 6, optional
                    "(?:" + RInChIConstants.NOSTRUCT_TAG + "(?<" + PATTERN_GROUP_NOSTRUCT_1 + ">[0-9]+)" +
                    RInChIConstants.NOSTRUCT_DELIMITER + "(?<" + PATTERN_GROUP_NOSTRUCT_2 + ">[0-9]+)" +
                    "(?:" + RInChIConstants.NOSTRUCT_DELIMITER + "(?<" + PATTERN_GROUP_NOSTRUCT_3 + ">[0-9]+))?)?" +
                    "$"
    );

    private final String rinchi;
    private final String rAuxInfo;
    private IReaction.Direction reactionDirection;
    private final List<Component> components = new ArrayList<>();

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
        this.rinchi = rinchi;
        this.rAuxInfo = auxInfo;
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

    RInChIDecomposition decompose() {
        if (this.rinchi == null) {
            addMessage("RInChI string provided as input is 'null'.", Status.ERROR);
        }
        if (this.rAuxInfo == null) {
            addMessage("RInChI auxiliary info string provided as input is 'null'.", Status.ERROR);
        }
        if (this.rinchi == null || this.rAuxInfo == null) {
            return this;
        }

        try {
            // decompose rinchi
            Matcher matcher = RINCHI_PATTERN.matcher(this.rinchi);
            boolean matches = matcher.matches();
            if (!matches) {
                throw new RInChIException("Cannot decompose invalid RInChI string '" + rinchi + "'.");
            }

            // extract layer 2, 3 and 4
            List<String> layer2 = getMoleculesFromComponentLayer(matcher.group(PATTERN_GROUP_LAYER_2));
            List<String> layer3 = getMoleculesFromComponentLayer(matcher.group(PATTERN_GROUP_LAYER_3));
            List<String> layer4 = getMoleculesFromComponentLayer(matcher.group(PATTERN_GROUP_LAYER_4));

            // reaction direction (layer 5)
            this.reactionDirection = rinchiCharacterToDirection(matcher.group(PATTERN_GROUP_DIRECTION));

            // nostruct layer (layer 6)
            int noStruct1 = Integer.parseInt(matcher.group(PATTERN_GROUP_NOSTRUCT_1) == null ? "0" : matcher.group(PATTERN_GROUP_NOSTRUCT_1));
            int noStruct2 = Integer.parseInt(matcher.group(PATTERN_GROUP_NOSTRUCT_2) == null ? "0" : matcher.group(PATTERN_GROUP_NOSTRUCT_2));
            int noStruct3 = Integer.parseInt(matcher.group(PATTERN_GROUP_NOSTRUCT_3) == null ? "0" : matcher.group(PATTERN_GROUP_NOSTRUCT_3));

            // decompose RAuxInfo if provided
            List<List<String>> rAuxInfoLayers = null;
            if (!rAuxInfo.isEmpty()) {
                rAuxInfoLayers = decomposeRAuxInfo(this.rAuxInfo);
                // verify that the number of molecules in each layer is identical for rinchi and rauxinfo
                if (rAuxInfoLayers.get(0).size() != layer2.size() || rAuxInfoLayers.get(1).size() != layer3.size() || rAuxInfoLayers.get(2).size() != layer4.size()) {
                    throw new RInChIException(String.format("Different number of molecules in RInChI (%d, %d, %d) and Auxiliary Information (%s).",
                            layer2.size(), layer3.size(), layer4.size(),
                            rAuxInfoLayers.stream().map(list -> Integer.toString(list.size())).collect(Collectors.joining(", "))));
                }
            }

            // create and populate Component objects
            // If the reaction direction is reversed molecules in layer2 are products and molecules in layer3 are reactants;
            // in all other cases (forward, equilibrium/bidirectional, undirected/unknown) reactants are in layer2 and products in layer3.
            this.components.addAll(getComponentsForLayer(
                    layer2,
                    rAuxInfoLayers != null ? rAuxInfoLayers.get(0) : null,
                    this.reactionDirection == IReaction.Direction.BACKWARD ? ReactionRole.Product : ReactionRole.Reactant
            ));
            this.components.addAll(getComponentsForLayer(
                    layer3,
                    rAuxInfoLayers != null ? rAuxInfoLayers.get(1) : null,
                    this.reactionDirection == IReaction.Direction.BACKWARD ? ReactionRole.Reactant : ReactionRole.Product
            ));
            this.components.addAll(getComponentsForLayer(layer4, rAuxInfoLayers != null ? rAuxInfoLayers.get(2) : null, ReactionRole.Agent));

        } catch (RInChIException exception) {
            addMessage(exception.getMessage(), Status.ERROR);
        }

        return this;
    }

    /**
     * Decomposes the given RInChI auxiliary information string into a list of layers,
     * where each layer is further decomposed into a list of molecular components.
     *
     * @param rAuxInfo the RInChI auxiliary information string to be decomposed.
     * @return a list of layers, each represented as a list of components, extracted from the RInChI auxiliary information.
     * @throws RInChIException if the provided RInChI auxiliary information string does not start with the expected header.
     */
    List<List<String>> decomposeRAuxInfo(String rAuxInfo) throws RInChIException {
        if (!rAuxInfo.startsWith(RInChIConstants.RINCHI_AUXINFO_HEADER)) {
            throw new RInChIException("Invalid/unsupported RInChI auxiliary information string. First layer must be equal to '" + RInChIConstants.RINCHI_AUXINFO_HEADER + "'.");
        }
        String rAuxInfoWithoutFirstLayer = rAuxInfo.substring(RInChIConstants.RINCHI_AUXINFO_HEADER.length());

        // add molecule components of each layer to list
        List<List<String>> layerList = new ArrayList<>();
        String[] layers = rAuxInfoWithoutFirstLayer.split("<>");
        for (String layer : layers) {
            layerList.add(Arrays.stream(layer.split("!")).filter(s -> !s.isEmpty()).collect(Collectors.toList()));
        }

        // add empty lists to list of layers if less than three layers in RAuxInfo
        while (layerList.size() < 3) {
            layerList.add(new ArrayList<>());
        }

        return layerList;
    }

    List<String> getMoleculesFromComponentLayer(String componentLayer) {
        if (componentLayer == null || componentLayer.isEmpty()) {
            return new ArrayList<>();
        }

        return Arrays.stream(componentLayer.split("!")).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    IReaction.Direction rinchiCharacterToDirection(String reactionDirectionCharacter) {
        if (reactionDirectionCharacter == null || reactionDirectionCharacter.isEmpty()) {
            return IReaction.Direction.UNDIRECTED;
        }

        switch (reactionDirectionCharacter) {
            case RInChIConstants.DIRECTION_EQUILIBRIUM:
                return IReaction.Direction.BIDIRECTIONAL;
            case RInChIConstants.DIRECTION_FORWARD:
                return IReaction.Direction.FORWARD;
            case RInChIConstants.DIRECTION_REVERSE:
                return IReaction.Direction.BACKWARD;
            default:
                return IReaction.Direction.UNDIRECTED;
        }
    }

    List<Component> getComponentsForLayer(List<String> rinchiLayer, List<String> rAuxInfoLayer, ReactionRole reactionRole) {
        List<Component> components = new ArrayList<>();
        for (int i = 0; i < rinchiLayer.size(); i++) {
            components.add(new Component(
                    RInChIConstants.INCHI_STD_HEADER + rinchiLayer.get(i),
                    rAuxInfoLayer != null ? RInChIConstants.INCHI_AUXINFO_HEADER + rAuxInfoLayer.get(i) : "",
                    reactionRole
            ));
        }
        return components;
    }
}
