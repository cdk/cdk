/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.interfaces;

import java.util.Iterator;

/**
 * Represents the idea of a chemical reaction. The reaction consists of
 * a set of reactants and a set of products.
 *
 * <p>The class mostly represents abstract reactions, such as 2D diagrams,
 * and is not intended to represent reaction trajectories. Such can better
 * be represented with a ChemSequence.
 *
 * @cdk.module  interfaces
 * @cdk.githash
 *
 * @author Egon Willighagen &lt;elw38@cam.ac.uk&gt;
 * @cdk.created 2003-02-13
 * @cdk.keyword reaction
 */
public interface IReaction extends IChemObject, Iterable<IAtomContainer> {

    /**
     * Permissible reaction directions.
     */
    enum Direction {
        /** Reaction equilibrium which is (almost) fully on the product side.
         *  Often denoted with a forward arrow. */
        FORWARD,
        /** Reaction equilibrium which is (almost) fully on the reactant side.
         *  Often denoted with a backward arrow. */
        BACKWARD,
        /** Reaction equilibrium state. Often denoted by a stacked arrows, one
         *  forwards, one backwards. */
        BIDIRECTIONAL,
        /** Reaction is a no-go, Often denoted by a cross or hash arrow. */
        NO_GO,
        /** Indicate the precursors for a given molecule,
         *  note this will make swap where reactant/products appear.
         *  Usually denoted with an open arrow. */
        RETRO_SYNTHETIC,
        /** Reaction shows interconversion between resonance forms. Usually
         *  denoted by a double-headed arrow. */
        RESONANCE,
        /**
         * Reaction equilibrium is not known to be fully on reactant/product
         * or in equilibrium state.
         * Also used to represent a "net reaction" composed of several
         * elementary steps.
         * Often denoted by a "=" sign
         */
        UNDIRECTED
    }

    /**
     * Returns the number of reactants in this reaction.
     *
     * @return The number of reactants in this reaction
     */
    int getReactantCount();

    /**
     * Returns the number of products in this reaction.
     *
     * @return The number of products in this reaction
     */
    int getProductCount();

    /**
     * Returns a IAtomContaineSet containing the reactants in this reaction.
     *
     * @return A IAtomContaineSet containing the reactants in this reaction
     * @see    #setReactants
     */
    IAtomContainerSet getReactants();

    /**
     * Assigns a IAtomContaineSet to the reactants in this reaction.
     *
     *
     * @param  reactants The new set of reactants
     * @see              #getReactants
     */
    void setReactants(IAtomContainerSet reactants);

    /**
     * Returns a IAtomContaineSet containing the products of this reaction.
     *
     * @return A IAtomContaineSet containing the products in this reaction
     * @see    #setProducts
     */
    IAtomContainerSet getProducts();

    /**
     * Assigns a IAtomContaineSet to the products of this reaction.
     *
     *
     * @param products The new set of products
     * @see            #getProducts
     */
    void setProducts(IAtomContainerSet products);

    /**
     * Returns a IAtomContaineSet containing the agents in this reaction.
     *
     * @return A IAtomContaineSet containing the agents in this reaction
     * @see    #addAgent
     */
    IAtomContainerSet getAgents();

    /**
     * An iterator for all the containers in this reaction.
     * The containers will be provided reactants then agents, then products.
     * @return the iterator
     */
    @Override
    default Iterator<IAtomContainer> iterator() {
        return new Iterator<IAtomContainer>() {

            private final Iterator[] sets = new Iterator[]{
                    getReactants().iterator(),
                    getAgents().iterator(),
                    getProducts().iterator()
            };
            int pos = 0; // 0 = reactant, 1 = agents, 2 = products
            IAtomContainer next;

            private IAtomContainer loadNext() {
                if (next != null)
                    return next;
                while (pos < sets.length) {
                    if (sets[pos].hasNext()) {
                        next = (IAtomContainer) sets[pos].next();
                        break;
                    }
                    pos++;
                }
                return next;
            }

            @Override
            public boolean hasNext() {
                return loadNext() != null;
            }

            @Override
            public IAtomContainer next() {
                IAtomContainer res = loadNext();
                next = null;
                return res;
            }
        };
    }

    /**
     * Returns the mappings between the reactant and the product side.
     *
     * @return An {@link Iterable} to the Mappings.
     * @see    #addMapping
     */
    Iterable<IMapping> mappings();

    /**
     * Adds a reactant to this reaction.
     *
     * @param reactant   Molecule added as reactant to this reaction
     * @see   #getReactants
     */
    void addReactant(IAtomContainer reactant);

    /**
     * Adds an agent to this reaction.
     *
     * @param agent   Molecule added as agent to this reaction
     * @see   #getAgents
     */
    void addAgent(IAtomContainer agent);

    /**
     * Adds a reactant to this reaction with a stoichiometry coefficient.
     *
     * @param reactant    Molecule added as reactant to this reaction
     * @param coefficient Stoichiometry coefficient for this molecule
     * @see   #getReactants
     */
    void addReactant(IAtomContainer reactant, Double coefficient);

    /**
     * Adds a product to this reaction.
     *
     * @param product    Molecule added as product to this reaction
     * @see   #getProducts
     */
    void addProduct(IAtomContainer product);

    /**
     * Adds a product to this reaction.
     *
     * @param product     Molecule added as product to this reaction
     * @param coefficient Stoichiometry coefficient for this molecule
     * @see   #getProducts
     */
    void addProduct(IAtomContainer product, Double coefficient);

    /**
     * Returns the stoichiometry coefficient of the given reactant.
     *
     * @param  reactant Reactant for which the coefficient is returned.
     * @return -1, if the given molecule is not a product in this Reaction
     * @see    #setReactantCoefficient
     */
    Double getReactantCoefficient(IAtomContainer reactant);

    /**
     * Returns the stoichiometry coefficient of the given product.
     *
     * @param  product Product for which the coefficient is returned.
     * @return -1, if the given molecule is not a product in this Reaction
     * @see    #setProductCoefficient
     */
    Double getProductCoefficient(IAtomContainer product);

    /**
     * Sets the coefficient of a a reactant to a given value.
     *
     * @param   reactant    Reactant for which the coefficient is set
     * @param   coefficient The new coefficient for the given reactant
     * @return  true if Molecule has been found and stoichiometry has been set.
     * @see     #getReactantCoefficient
     */
    boolean setReactantCoefficient(IAtomContainer reactant, Double coefficient);

    /**
     * Sets the coefficient of a a product to a given value.
     *
     * @param   product     Product for which the coefficient is set
     * @param   coefficient The new coefficient for the given product
     * @return  true if Molecule has been found and stoichiometry has been set.
     * @see     #getProductCoefficient
     */
    boolean setProductCoefficient(IAtomContainer product, Double coefficient);

    /**
     * Returns an array of double with the stoichiometric coefficients
     * of the reactants.
     *
     * @return An array of double's containing the coefficients of the reactants
     * @see    #setReactantCoefficients
     */
    Double[] getReactantCoefficients();

    /**
     * Returns an array of double with the stoichiometric coefficients
     * of the products.
     *
     * @return An array of double's containing the coefficients of the products
     * @see    #setProductCoefficients
     */
    Double[] getProductCoefficients();

    /**
     * Sets the coefficients of the reactants.
     *
     * @param   coefficients An array of double's containing the coefficients of the reactants
     * @return  true if coefficients have been set.
     * @see     #getReactantCoefficients
     */
    boolean setReactantCoefficients(Double[] coefficients);

    /**
     * Sets the coefficient of the products.
     *
     * @param   coefficients An array of double's containing the coefficients of the products
     * @return  true if coefficients have been set.
     * @see     #getProductCoefficients
     */
    boolean setProductCoefficients(Double[] coefficients);

    /**
     * Sets the direction of the reaction.
     *
     * @param direction The new reaction direction
     * @see   #getDirection
     */
    void setDirection(IReaction.Direction direction);

    /**
     * Returns the {@link IReaction.Direction} of the reaction.
     *
     * @return The direction of this reaction (FORWARD, BACKWARD or BIDIRECTIONAL).
     * @see    #setDirection(Direction)
     */
    IReaction.Direction getDirection();

    /**
     * Adds a mapping between the reactant and product side to this
     * Reaction.
     *
     * @param mapping Mapping to add.
     * @see   #mappings
     */
    void addMapping(IMapping mapping);

    /**
     * Removes a mapping between the reactant and product side to this
     * Reaction.
     *
     * @param  pos  Position of the Mapping to remove.
     * @see   #mappings
     */
    void removeMapping(int pos);

    /**
     * Retrieves a mapping between the reactant and product side to this
     * Reaction.
     *
     * @param pos Position of Mapping to get.
     * @return the mapping identified by the specified position
     */
    IMapping getMapping(int pos);

    /**
     * Get the number of mappings between the reactant and product side to this
     * Reaction.
     *
     * @return Number of stored Mappings.
     */
    int getMappingCount();

}
