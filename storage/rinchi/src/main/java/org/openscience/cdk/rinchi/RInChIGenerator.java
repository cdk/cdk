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
 * @author Felix Bänsch
 * @author Uli Fechner
 * @cdk.module rinchi
 * @cdk.githash
 */

public final class RInChIGenerator extends StatusMessagesOutput {
    private static final ILoggingTool LOGGER = LoggingToolFactory.createLoggingTool(RInChIGenerator.class);
    private static final int NUMBER_OF_COMPONENTS = 3;
    private static final EnumSet<RInChIOption> DEFAULT_OPTIONS = EnumSet.noneOf(RInChIOption.class);

    private final EnumSet<RInChIOption> rinchiOptions;
    private String rinchi;
    private String auxInfo;
    private String shortRinchiKeyOutput;
    private String longRinchiKeyOutput;
    private String webRinchiKeyOutput;

    private List<RInChIComponent> reactants;
    private List<RInChIComponent> products;
    private List<RInChIComponent> agents;
    private List<RInChIComponent>[] components;
    private int[] noStructCounts;
    private InChILayers[] layers;
    private IReaction.Direction direction;

    /**
     * Generates RInChI from a CDK Reaction.
     *
     * @param reaction reaction to generate RInChI for
     * @param options  zero or more optional RInChI generation options
     */
    @SuppressWarnings("unchecked")
    RInChIGenerator(IReaction reaction, RInChIOption... options) {
        this.rinchiOptions = ((options == null || options.length == 0) ? DEFAULT_OPTIONS : EnumSet.copyOf(Arrays.asList(options)));

        if (reaction == null) {
            addMessage("IReaction object provided as input is 'null'.", Status.ERROR);
            return;
        }

        this.reactants = new ArrayList<>();
        this.products = new ArrayList<>();
        this.agents = new ArrayList<>();
        this.components = (List<RInChIComponent>[]) new List[NUMBER_OF_COMPONENTS];
        this.noStructCounts = new int[NUMBER_OF_COMPONENTS];
        this.layers = new InChILayers[NUMBER_OF_COMPONENTS];

        try {
            this.extractComponents(reaction);
        } catch (CDKException exception) {
            addMessage(exception.getMessage(), Status.ERROR);
        }

        this.rinchi = this.generateRInChI();
        this.auxInfo = this.generateRAuxInfo();
        this.longRinchiKeyOutput = this.generateLongKey();
        this.shortRinchiKeyOutput = this.generateShortKey();
        this.webRinchiKeyOutput = this.generateWebKey();
    }

    /**
     * Extracts components from the given reaction and initializes the corresponding RInChI components.
     *
     * <p>This method creates and populates lists for reactants, products, and agents by generating
     * {@link RInChIComponent} instances for each {@link IAtomContainer} in the reaction. It sorts these
     * components based on their InChI representations and organizes them into appropriate layers.</p>
     *
     * <p>The method performs the following steps:
     * <ul>
     *   <li>Iterates through the reactants, products, and agents of the reaction, generating a
     *       {@link RInChIComponent} for each and adding it to the respective list.</li>
     *   <li>Sorts the lists of reactants, products, and agents based on their InChI values.</li>
     *   <li>Determines whether products come before reactants based on their InChI comparison.</li>
     *   <li>Sets the direction of the reaction (FORWARD or BACKWARD) and creates corresponding
     *       {@link InChILayers} instances for each component type.</li>
     *   <li>Handles cases where the reaction is bidirectional or requires equilibrium adjustment.</li>
     * </ul>
     * The extracted components are stored in instance variables for further processing.</p>
     *
     * @param reaction The reaction from which components will be extracted.
     * @throws CDKException If there is an error in generating InChI components from atom containers.
     */
    private void extractComponents(final IReaction reaction) throws CDKException {
        //create InChIComponent for each component
        for (IAtomContainer ac : reaction.getReactants()) {
            this.reactants.add(new RInChIComponent(getInChIGenerator(ac)));
        }
        for (IAtomContainer ac : reaction.getProducts()) {
            this.products.add(new RInChIComponent(getInChIGenerator(ac)));
        }
        for (IAtomContainer ac : reaction.getAgents()) {
            this.agents.add(new RInChIComponent(getInChIGenerator(ac)));
        }
        // sort components lexicographically by InChI
        this.reactants.sort(Comparator.comparing(RInChIComponent::getInchi));
        this.products.sort(Comparator.comparing(RInChIComponent::getInchi));
        this.agents.sort(Comparator.comparing(RInChIComponent::getInchi));

        if (!this.isProductsFirst()) {
            this.components[0] = this.reactants;
            this.noStructCounts[0] = (int) this.reactants.stream().filter(RInChIComponent::isNoStructure).count();
            this.components[1] = this.products;
            this.noStructCounts[1] = (int) this.products.stream().filter(RInChIComponent::isNoStructure).count();
            this.direction = IReaction.Direction.FORWARD;
            this.layers[0] = new InChILayers(this.reactants);
            this.layers[1] = new InChILayers(this.products);
        } else {
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

        if (this.rinchiOptions.contains(RInChIOption.FORCE_EQUILIBRIUM) || reaction.getDirection() == IReaction.Direction.BIDIRECTIONAL)
            this.direction = IReaction.Direction.BIDIRECTIONAL;
    }

    /**
     * Generates the RInChI string representation for the reaction.
     *
     * <p>This method constructs the RInChI string by combining the InChI strings of all
     * components (reactants and products) along with reaction direction and information
     * regarding no-structure components.</p>
     *
     * <p>The RInChI string consists of the following parts:
     * <ul>
     *   <li>RInChI standard header</li>
     *   <li>InChI strings of all non-null, structured components, separated by component delimiters</li>
     *   <li>Reaction direction tag and its corresponding character representation</li>
     *   <li>Information regarding no-structure components, if any, including their counts</li>
     * </ul>
     *
     * @return RInChI string of the IReaction this object was instantiated with
     */
    private String generateRInChI() {
        //RInChI StringBuilder
        final StringBuilder sb = new StringBuilder();
        sb.append(RInChIConstants.RINCHI_STD_HEADER);

        //add components
        for (int i = 0; i < this.components.length; i++) {
            final String componentString = this.components[i].stream()
                    .filter(Objects::nonNull)
                    .filter(c -> !c.isNoStructure())
                    .map(c -> c.getInchi().substring(RInChIConstants.INCHI_STD_HEADER.length()))
                    .collect(Collectors.joining(RInChIConstants.DELIMITER_COMPONENT));
            sb.append(componentString);
            if (i < NUMBER_OF_COMPONENTS - 1 && !this.components[i + 1].isEmpty())
                sb.append(RInChIConstants.DELIMITER_GROUP);
        }

        //add direction
        sb.append(RInChIConstants.DIRECTION_TAG).append(this.directionToRInChIChar(this.direction));

        //add no structs layer
        if (Arrays.stream(this.noStructCounts).anyMatch(c -> c > 0)) {
            sb.append(RInChIConstants.NOSTRUCT_TAG);
            for (int i = 0; i < this.noStructCounts.length; i++) {
                sb.append(this.noStructCounts[i]);
                if (i < this.noStructCounts.length - 1)
                    sb.append(RInChIConstants.NOSTRUCT_DELIMITER);
            }
        }

        return sb.toString();
    }

    /**
     * Generates the RInChI auxiliary information (AuxInfo) string.
     *
     * <p>This method constructs the RInChI AuxInfo by combining the auxiliary information
     * of all components (reactants and products) in the reaction.</p>
     *
     * <p>The AuxInfo string is composed of the following parts:
     * <ul>
     *   <li>RInChI AuxInfo header</li>
     *   <li>AuxInfo for each component, excluding no-structure components</li>
     *   <li>Component and group delimiters to separate the AuxInfo data of different components and groups</li>
     * </ul>
     *
     * @return RAuxInfo string
     */
    private String generateRAuxInfo() {
        final StringBuilder sb = new StringBuilder();
        sb.append(RInChIConstants.RINCHI_AUXINFO_HEADER);

        //add components
        for (int i = 0; i < this.components.length; i++) {
            final String componentString = this.components[i].stream()
                    .filter(Objects::nonNull)
                    .filter(c -> !c.isNoStructure())
                    .map(c -> c.getAuxInfo().substring(RInChIConstants.INCHI_AUXINFO_HEADER.length()))
                    .collect(Collectors.joining(RInChIConstants.DELIMITER_COMPONENT));
            sb.append(componentString);
            if (i < NUMBER_OF_COMPONENTS - 1 && !this.components[i + 1].isEmpty())
                sb.append(RInChIConstants.DELIMITER_GROUP);
        }

        return sb.toString();
    }

    /**
     * Generates the Long-RInChI-Key.
     *
     * <p>This method constructs the long RInChI key by combining the reaction direction,
     * the InChI keys of the components (reactants and products), and indicators for no-structure entities.</p>
     *
     * <p>The key is composed of the following parts:
     * <ul>
     *   <li>RInChI header and version identifier</li>
     *   <li>Reaction direction</li>
     *   <li>Empty 12-character hash placeholder</li>
     *   <li>InChI keys of all components, separated by component and group delimiters</li>
     *   <li>If no-structure entities exist, they are indicated by a special character</li>
     * </ul>
     * @return Long-RInChI-Key
     */
    private String generateLongKey() {
        final StringBuilder sb = new StringBuilder();
        sb.append(RInChIConstants.RINCHI_LONG_KEY_HEADER);
        sb.append(RInChIConstants.RINCHI_KEY_VERSION_ID_HEADER);
        sb.append(RInChIConstants.KEY_DELIMITER_BLOCK);
        sb.append(this.directionToRInChIKeyChar(this.direction));
        sb.append(RInChIConstants.HASH_12_EMPTY_STRING, 0, 4);
        sb.append(RInChIConstants.KEY_DELIMITER_BLOCK);
        final String result = sb.toString();

        for (int i = 0; i < this.components.length; i++) {
            final String componentString = this.components[i].stream()
                    .filter(Objects::nonNull)
                    .filter(c -> !c.isNoStructure())
                    .map(RInChIComponent::getInchiKey)
                    .collect(Collectors.joining(RInChIConstants.KEY_DELIMITER_COMPONENT));
            sb.append(componentString);
            if (this.noStructCounts[i] != 0) {
                sb.append(RInChIConstants.KEY_DELIMITER_COMPONENT);
                sb.append(RInChIConstants.NOSTRUCT_RINCHI_LONGKEY);
            }
            if (i < NUMBER_OF_COMPONENTS - 1 && !this.components[i + 1].isEmpty())
                sb.append(RInChIConstants.KEY_DELIMITER_GROUP);
        }
        if (result.contentEquals(sb)) {
            return result.substring(0, result.length() - RInChIConstants.KEY_DELIMITER_BLOCK.length());
        } else {
            return sb.toString();
        }
    }

    /**
     * Generates the short form of the RInChI key.
     *
     * <p>This method constructs a compact RInChI key by including the reaction direction,
     * major and minor hashes of all components, and a count of no-structure entities.</p>
     *
     * <p>The key format is composed of several parts:
     * <ul>
     *   <li>RInChI header and version identifier</li>
     *   <li>Reaction direction</li>
     *   <li>Empty minor hash placeholder (constant)</li>
     *   <li>Major and minor hashes for each layer in {@code this.layers}</li>
     *   <li>Character representation of the no-structure counts</li>
     * </ul>
     * @return Short-RInChI-Key or {@code null} if an error prevents generation of the key
     */
    private String generateShortKey() {
        final StringBuilder sb = new StringBuilder();
        sb.append(RInChIConstants.RINCHI_SHORT_KEY_HEADER);
        sb.append(RInChIConstants.RINCHI_KEY_VERSION_ID_HEADER);
        sb.append(RInChIConstants.KEY_DELIMITER_COMPONENT);
        sb.append(this.directionToRInChIKeyChar(this.direction));
        sb.append(RInChIConstants.HASH_04_EMPTY_STRING);

        final StringBuilder allMajors = new StringBuilder();
        final StringBuilder allMinors = new StringBuilder();

        try {
            for (final InChILayers layers : this.layers) {
                allMajors.append(RInChIConstants.KEY_DELIMITER_BLOCK).append(layers.majorHash());
                allMinors.append(RInChIConstants.KEY_DELIMITER_BLOCK).append(layers.minorHash());
            }
        } catch (NoSuchAlgorithmException exception) {
            addMessage(exception.getMessage(), Status.ERROR);
            return null;
        }

        sb.append(allMajors);
        sb.append(allMinors);
        sb.append(RInChIConstants.KEY_DELIMITER_BLOCK);
        for (int noStructCount : this.noStructCounts) {
            sb.append(this.noStructCountToRInChIKeyChar(noStructCount));
        }
        return sb.toString();
    }

    /**
     * Generates a Web-RInChI-Key.
     *
     * <p>This method aggregates all unique InChI strings from the reaction components into a set.
     * It then constructs an {@link InChILayers} object to process and append these unique InChI strings.
     * The final web key is built by concatenating the RInChI header, major and minor hash extensions
     * generated from the InChI layers, and the suffix "SA".</p>
     *
     * @return Web-RInChI-Key or {@code null} if an error prevents generation of the key
     */
    private String generateWebKey() {
        try {
            // Create a unique list of all component InChIs.
            final Set<String> uniqueInchis = new TreeSet<>();
            for (List<RInChIComponent> rInChIComponents : this.components) {
                for (RInChIComponent component : rInChIComponents) {
                    uniqueInchis.add(component.getInchi());
                }
            }

            final InChILayers allInChILayers = new InChILayers();
            for (String inchi : uniqueInchis) {
                allInChILayers.append(inchi);
            }

            return RInChIConstants.RINCHI_WEB_KEY_HEADER +
                    allInChILayers.majorHashExtended() +
                    RInChIConstants.KEY_DELIMITER_BLOCK +
                    allInChILayers.minorHashExtended() +
                    "SA";
        } catch (CDKException | NoSuchAlgorithmException exception) {
            addMessage(exception.getMessage(), Status.ERROR);
            return null;
        }
    }

    /**
     * Returns an {@link InChIGenerator} for the given atom container using the InChI library.
     *
     * <p>This method uses the {@link InChIGeneratorFactory} to create an {@link InChIGenerator} for the provided
     * {@link IAtomContainer}. The default {@link InchiOptions} are used for generating the InChI. If the generation
     * is successful, the generator is returned. Otherwise, a warning is logged, and the method returns {@code null}.</p>
     *
     * <p>If an exception occurs during the process (e.g., due to an issue with the chemistry library), the error is logged,
     * and {@code null} is returned.</p>
     *
     * @param atomContainer the {@link IAtomContainer} to generate an InChI for
     * @return the {@link InChIGenerator} if successful, otherwise {@code null}
     */
    private InChIGenerator getInChIGenerator(IAtomContainer atomContainer) {
        try {
            final InchiOptions options = new InchiOptions.InchiOptionsBuilder().build();
            final InChIGenerator generator = InChIGeneratorFactory.getInstance().getInChIGenerator(atomContainer, options);
            if (generator.getStatus() == InchiStatus.SUCCESS)
                return generator;
            else {
                addMessage("InChIGenerator did not returned status success" +
                        (generator.getMessage() != null && !generator.getMessage().isEmpty() ? (": " + generator.getMessage()) : "") + ".", Status.WARNING);
                return null;
            }
        } catch (CDKException exception) {
            addMessage(exception.toString(), Status.ERROR);
            return null;
        }
    }

    /**
     * Converts a reaction direction into the corresponding RInChI character representation.
     *
     * <p>This method maps the {@link IReaction.Direction} enum values to predefined constants in
     * {@link RInChIConstants}:
     * <ul>
     *   <li>{@code FORWARD} maps to {@code RInChIConsts.DIRECTION_FORWARD}.</li>
     *   <li>{@code BACKWARD} maps to {@code RInChIConsts.DIRECTION_REVERSE}.</li>
     *   <li>{@code BIDIRECTIONAL} maps to {@code RInChIConsts.DIRECTION_EQUILIBRIUM}.</li>
     * </ul>
     * If the direction is unsupported or unrecognized, it logs an error message and returns {@code 0}.</p>
     *
     * @param direction the reaction direction, represented by the {@link IReaction.Direction} enum
     * @return the corresponding RInChI character for the direction, or {@code 0} if the direction is unsupported
     */
    private char directionToRInChIChar(IReaction.Direction direction) {
        switch (direction) {
            case FORWARD:
                return RInChIConstants.DIRECTION_FORWARD;
            case BACKWARD:
                return RInChIConstants.DIRECTION_REVERSE;
            case BIDIRECTIONAL:
                return RInChIConstants.DIRECTION_EQUILIBRIUM;
            default:
                addMessage(String.format("Unsupported reaction direction: %s", direction), Status.ERROR);
                return 0;
        }
    }

    /**
     * Converts a reaction direction into a corresponding single character key.
     *
     * <p>This method maps the {@link IReaction.Direction} enum values to specific characters:
     * <ul>
     *   <li>{@code FORWARD} maps to 'F'.</li>
     *   <li>{@code BACKWARD} maps to 'B'.</li>
     *   <li>{@code BIDIRECTIONAL} maps to 'E'.</li>
     * </ul>
     * If the direction is unsupported or unrecognized, it logs an error message and returns {@code 0}.</p>
     *
     * @param direction the reaction direction, represented by the {@link IReaction.Direction} enum
     * @return the corresponding character key ('F', 'B', or 'E'), or {@code 0} if the direction is unsupported
     */
    private char directionToRInChIKeyChar(IReaction.Direction direction) {
        switch (direction) {
            case FORWARD:
                return 'F';
            case BACKWARD:
                return 'B';
            case BIDIRECTIONAL:
                return 'E';
            default:
                addMessage(String.format("Unsupported reaction direction: %s.", direction), Status.ERROR);
                return 0;
        }
    }

    /**
     * Determines if the first product's InChI string is lexicographically greater than the first reactant's InChI string.
     *
     * <p>This method compares the InChI strings of the first reactant and the first product to determine their ordering.
     * If the product's InChI string is lexicographically greater than the reactant's, the method returns {@code true}, indicating that
     * products should come first in the ordering. If the reactant comes first or if either list is empty or null, the method returns {@code false}.</p>
     *
     * <p>Comparisons are based on the Unicode value of the characters in the InChI strings.</p>
     *
     * @return {@code true} if the first product's InChI is lexicographically greater than the first reactant's InChI, {@code false} otherwise
     */
    private boolean isProductsFirst() {
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

    /**
     * Converts the given count of "no structure" components to a corresponding character.
     *
     * <p>This method is used to represent the number of components that have no structure as a single character.
     * The character is determined as follows:</p>
     * <ul>
     *     <li>If the count is 0, the character 'Z' is returned.</li>
     *     <li>If the count is greater than 24, the character 'Y' is returned.</li>
     *     <li>Otherwise, the character is determined by converting the count into an alphabetical character ('A' for 1, 'B' for 2, ..., 'X' for 24).</li>
     * </ul>
     *
     * @param count the number of components with no structure
     * @return the corresponding character representation of the count
     */
    private char noStructCountToRInChIKeyChar(int count) {
        if (count == 0)
            return 'Z';
        if (count > 24)
            return 'Y';
        else
            return (char) ('A' + count - 1);
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
