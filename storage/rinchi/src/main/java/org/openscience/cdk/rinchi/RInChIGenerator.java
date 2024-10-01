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

import io.github.dan2097.jnainchi.InchiOptions;
import io.github.dan2097.jnainchi.InchiStatus;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

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

public final class RInChIGenerator extends StatusMessagesOutput {

    private static final int COMPONENT_NUM = 3;
    private static final ILoggingTool LOGGER = LoggingToolFactory.createLoggingTool(RInChIGenerator.class);
    private static final EnumSet<RInChIOption> DEFAULT_OPTIONS = EnumSet.noneOf(RInChIOption.class);

    private final EnumSet<RInChIOption> rinchiOptions;
    private String rinchi;
    private String auxInfo;
    private String shortRinchiKeyOutput;
    private String longRinchiKeyOutput;
    private String webRinchiKeyOutput;
    private boolean productsFirst;
    private List<RInChIComponent> reactants;
    private List<RInChIComponent> products;
    private List<RInChIComponent> agents;
    private List<RInChIComponent>[] components;
    private InChILayers[] layers;
    private int[] noStructCounts;
    private IReaction.Direction direction;

    /**
     * Generates RInChI from a CDK Reaction.
     *
     * @param reaction reaction to generate RInChI for
     * @param options  zero or more optional RInChI generation options
     */
    RInChIGenerator(IReaction reaction, RInChIOption... options) {
        this.rinchiOptions = ((options == null || options.length == 0) ? DEFAULT_OPTIONS : EnumSet.copyOf(Arrays.asList(options)));

        if (reaction == null) {
            addMessage("IReaction object provided as input is 'null'.", Status.ERROR);
            return;
        }
        reactants = new ArrayList<>();
        this.products = new ArrayList<>();
        this.agents = new ArrayList<>();
        this.components = new List[COMPONENT_NUM];
        this.noStructCounts = new int[COMPONENT_NUM];
        this.layers = new InChILayers[COMPONENT_NUM];

        try {
            this.extractComponents(reaction);
        } catch (CDKException e) {
            throw new RuntimeException(e);
        }
        this.generateRInChI();
        this.generateRAuxInfo();
        this.generateLongKey();
        try {
            this.generateShortKey();
            this.generateWebKey();
        } catch (NoSuchAlgorithmException | CDKException e) {
            addMessage("Error in creating short key.", Status.WARNING);
        }
    }

    private void extractComponents(final IReaction reaction) throws CDKException {
        //create InChIComponent for each component
        for(IAtomContainer ac : reaction.getReactants()){
            this.reactants.add(new RInChIComponent(getInChIGen(ac)));
        }
        for(IAtomContainer ac : reaction.getProducts()){
            this.products.add(new RInChIComponent(getInChIGen(ac)));
        }
        for(IAtomContainer ac : reaction.getAgents()){
            this.agents.add(new RInChIComponent(getInChIGen(ac)));
        }
        //sort components by InChI
        this.reactants.sort(Comparator.comparing(RInChIComponent::getInchi));
        this.products.sort(Comparator.comparing(RInChIComponent::getInchi));
        this.agents.sort(Comparator.comparing(RInChIComponent::getInchi));

        if (!this.isProductsFirst()){
            this.components[0] = this.reactants;
            this.noStructCounts[0] = (int) this.reactants.stream().filter(RInChIComponent::isNoStructure).count();
            this.components[1] = this.products;
            this.noStructCounts[1] = (int) this.products.stream().filter(RInChIComponent::isNoStructure).count();
            this.direction = IReaction.Direction.FORWARD;
            this.layers[0] = new InChILayers(this.reactants);
            this.layers[1] = new InChILayers(this.products);
        } else {
            this.productsFirst = true;
            this.components[0] = this.products;
            this.noStructCounts[0] = (int) this.products.stream().filter(RInChIComponent::isNoStructure).count();
            this.components[1] = this.reactants;
            this.noStructCounts[1] = (int) this.reactants.stream().filter(RInChIComponent::isNoStructure).count();
            this.direction = IReaction.Direction.BACKWARD;
            this.layers[0] = new InChILayers(this.products);
            this.layers[1] = new InChILayers(this.reactants);
        }
        this.components[2] = this.agents;
        this.noStructCounts[2] = (int) this.agents.stream().filter(RInChIComponent::isNoStructure).count();
        this.layers[2] = new InChILayers(this.agents);
        if(this.rinchiOptions.contains(RInChIOption.FORCE_EQUILIBRIUM) || reaction.getDirection() == IReaction.Direction.BIDIRECTIONAL)
            this.direction = IReaction.Direction.BIDIRECTIONAL;
    }

    private void generateRInChI() {
        // TODO implement logic here
        //RInChI StringBuilder
        StringBuilder sb = new StringBuilder();
        sb.append(RInChIConsts.RINCHI_STD_HEADER);
        //add components
        for (int i = 0; i < this.components.length; i++) {
            String componentString = this.components[i].stream().filter(Objects::nonNull).
                    filter(c -> !c.isNoStructure()).
                    map(c -> c.getInchi().substring(RInChIConsts.INCHI_STD_HEADER.length())).
                    collect(Collectors.joining(RInChIConsts.DELIM_COMP));
            sb.append(componentString);
            if (i < COMPONENT_NUM - 1 && !this.components[i + 1].isEmpty())
                sb.append(RInChIConsts.DELIM_GROUP);
        }
        //add direction
        sb.append(RInChIConsts.DIRECTION_TAG).append(this.directionToRInChIChar(this.direction));
        //add no structs layer
        if (Arrays.stream(this.noStructCounts).anyMatch(c -> c > 0)){
            sb.append(RInChIConsts.NOSTRUCT_TAG);
            for(int i = 0; i < this.noStructCounts.length; i++) {
                sb.append(this.noStructCounts[i]);
                if (i < this.noStructCounts.length - 1)
                    sb.append(RInChIConsts.NOSTRUCT_DELIM);
            }
        }
        this.rinchi = sb.toString();
    }

    private void generateRAuxInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(RInChIConsts.RINCHI_AUXINFO_HEADER);
        //add components
        for (int i = 0; i < this.components.length; i++) {
            String componentString = this.components[i].stream().filter(Objects::nonNull).
                    filter(c -> !c.isNoStructure()).
                    map(c -> c.getAuxInfo().substring(RInChIConsts.INCHI_AUXINFO_HEADER.length())).
                    collect(Collectors.joining(RInChIConsts.DELIM_COMP));
            sb.append(componentString);
            if (i < COMPONENT_NUM - 1 && !this.components[i + 1].isEmpty())
                sb.append(RInChIConsts.DELIM_GROUP);
        }
        this.auxInfo = sb.toString();
    }

    private void generateLongKey() {
        StringBuilder sb = new StringBuilder();
        sb.append(RInChIConsts.RINCHI_LONG_KEY_HEADER);
        sb.append(RInChIConsts.RINCHI_KEY_VERSION_ID_HEADER);
        sb.append(RInChIConsts.KEY_DELIM_BLOCK);
        sb.append(this.directionToKeyChar(this.direction));
        sb.append(RInChIConsts.HASH_12_EMPTY_STRING, 0, 4);
        sb.append(RInChIConsts.KEY_DELIM_BLOCK);
        String result = sb.toString();
        for (int i = 0; i < this.components.length; i++) {
            String componentString = this.components[i].stream().filter(Objects::nonNull).
                    filter(c -> !c.isNoStructure()).
                    map(RInChIComponent::getInchiKey).
                    collect(Collectors.joining(RInChIConsts.KEY_DELIM_COMP));
            sb.append(componentString);
            if (this.noStructCounts[i] != 0) {
                sb.append(RInChIConsts.KEY_DELIM_COMP);
                sb.append(RInChIConsts.NOSTRUCT_RINCHI_LONGKEY);
            }
            if (i < COMPONENT_NUM - 1 && !this.components[i + 1].isEmpty())
                sb.append(RInChIConsts.KEY_DELIM_GROUP);
        }
        if (result.contentEquals(sb)) {
            this.longRinchiKeyOutput = result.substring(0, result.length() - RInChIConsts.KEY_DELIM_BLOCK.length());
        } else {
            this.longRinchiKeyOutput = sb.toString();
        }
    }

    private void generateShortKey() throws NoSuchAlgorithmException {
        StringBuilder sb = new StringBuilder();
        sb.append(RInChIConsts.RINCHI_SHORT_KEY_HEADER);
        sb.append(RInChIConsts.RINCHI_KEY_VERSION_ID_HEADER);
        sb.append(RInChIConsts.KEY_DELIM_COMP);
        sb.append(this.directionToKeyChar(this.direction));
        sb.append(RInChIConsts.HASH_04_EMPTY_STRING);

        StringBuilder allMajors = new StringBuilder();
        StringBuilder allMinors = new StringBuilder();

        for (int i = 0; i < this.layers.length; i++) {
            InChILayers layers = this.layers[i];
            allMajors.append(RInChIConsts.KEY_DELIM_BLOCK).append(layers.majorHash());
            allMinors.append(RInChIConsts.KEY_DELIM_BLOCK).append(layers.minorHash());
        }

        sb.append(allMajors);
        sb.append(allMinors);
        sb.append(RInChIConsts.KEY_DELIM_BLOCK);
        for (int noStructCount : this.noStructCounts) {
            sb.append(this.noStructCountToChar(noStructCount));
        }
        this.shortRinchiKeyOutput = sb.toString();
    }

    private void generateWebKey() throws CDKException, NoSuchAlgorithmException {
        // Create a unique list of all component InChIs.
        Set<String> uniqueInchis = new TreeSet<>();
        for (List<RInChIComponent> rInChIComponents : this.components) {
            for (RInChIComponent component : rInChIComponents) {
                uniqueInchis.add(component.getInchi());
            }
        }
        InChILayers allInChILayers = new InChILayers();
        for (String inchi : uniqueInchis) {
            allInChILayers.append(inchi);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(RInChIConsts.RINCHI_WEB_KEY_HEADER);
        sb.append(allInChILayers.majorHashExt());
        sb.append(RInChIConsts.KEY_DELIM_BLOCK);
        sb.append(allInChILayers.minorHashExt());
        sb.append("SA");
        this.webRinchiKeyOutput = sb.toString();
    }

    private InChIGenerator getInChIGen(IAtomContainer atomContainer) {
        try {
            InchiOptions options = new InchiOptions.InchiOptionsBuilder().build();
            InChIGenerator gen = InChIGeneratorFactory.getInstance().getInChIGenerator(atomContainer, options);
            if (gen.getStatus() == InchiStatus.SUCCESS)
                return gen;
            else {
                addMessage("InChIGenerator not returned status success.", Status.WARNING);
                return null;
            }
        } catch (CDKException e) {
            addMessage(e.getMessage(), Status.ERROR);
            return null;
        }
    }

    private char directionToRInChIChar(IReaction.Direction direction) {
        switch (direction) {
            case FORWARD:
                return RInChIConsts.DIRECTION_FORWARD;
            case BACKWARD:
                return RInChIConsts.DIRECTION_REVERSE;
            case BIDIRECTIONAL:
                return RInChIConsts.DIRECTION_EQUILIBRIUM;
            default:
                addMessage("Unsupported reaction direction.", Status.ERROR);
                return 0;
        }
    }

    private char directionToKeyChar(IReaction.Direction direction) {
        switch (direction) {
            case FORWARD:
                return 'F';
            case BACKWARD:
                return 'B';
            case BIDIRECTIONAL:
                return 'E';
            default:
                addMessage("Unsupported reaction direction.", Status.ERROR);
                return 0;
        }
    }

    private boolean isProductsFirst(){
        String reactant1 = "";
        if (!this.reactants.isEmpty() && this.reactants.get(0) != null) {
            reactant1 = this.reactants.get(0).getInchi();
        }
        String product1 = "";
        if (!this.products.isEmpty() && this.products.get(0) != null) {
            product1 = this.products.get(0).getInchi();
        }
        return reactant1.compareTo(product1) > 0;
    }

    private char noStructCountToChar(int count) {
        if (count == 0)
            return 'Z';
        if (count > 24)
            return 'Y';
        else
            return (char)('A' + count - 1);
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
}
