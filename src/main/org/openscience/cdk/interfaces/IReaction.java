/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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


/**
 * Represents the idea of a chemical reaction. The reaction consists of 
 * a set of reactants and a set of products.
 *
 * <p>The class mostly represents abstract reactions, such as 2D diagrams,
 * and is not intended to represent reaction trajectories. Such can better
 * be represented with a ChemSequence.
 *
 * @cdk.module  interfaces
 * @cdk.svnrev  $Revision$
 *
 * @author      Egon Willighagen <elw38@cam.ac.uk>
 * @cdk.created 2003-02-13
 * @cdk.keyword reaction
 */
public interface IReaction extends IChemObject {

	public enum Direction {
		/** Reaction equilibrium which is (almost) fully on the product side. Often denoted with a forward arrow. */
		FORWARD,
		/** Reaction equilibrium which is (almost) fully on the reactant side. Often denoted with a backward arrow. */
		BACKWARD,
		/** Reaction equilibrium state. Often denoted by a double arrow. */
		BIDIRECTIONAL
	}
	
    /**
     * Returns the number of reactants in this reaction.
     *
     * @return The number of reactants in this reaction
     */
    public int getReactantCount();
    
    /**
     * Returns the number of products in this reaction.
     *
     * @return The number of products in this reaction
     */
    public int getProductCount();

    /**
     * Returns a IMoleculeSet containing the reactants in this reaction.
     *
     * @return A IMoleculeSet containing the reactants in this reaction
     * @see    #setReactants
     */
    public IMoleculeSet getReactants();

    /**
     * Assigns a IMoleculeSet to the reactants in this reaction.
     *
     * @param  reactants The new set of reactants
     * @see              #getReactants
     */
    public void setReactants(IMoleculeSet reactants);
	
    /**
     * Returns a IMoleculeSet containing the products of this reaction.
     *
     * @return A IMoleculeSet containing the products in this reaction
     * @see    #setProducts
     */
    public IMoleculeSet getProducts();
    
	/**
     * Assigns a IMoleculeSet to the products of this reaction.
     *
     * @param products The new set of products
     * @see            #getProducts
     */
    public void setProducts(IMoleculeSet products);
	
    /**
     * Returns a IMoleculeSet containing the agents in this reaction.
     *
     * @return A IMoleculeSet containing the agents in this reaction
     * @see    #addAgent
     */
    public IMoleculeSet getAgents();
    
    /**
     * Returns the mappings between the reactant and the product side.
     *
     * @return An {@link Iterable} to the Mappings.
     * @see    #addMapping
     */
    public Iterable<IMapping> mappings();
    
    /**
     * Adds a reactant to this reaction.
     *
     * @param reactant   Molecule added as reactant to this reaction
     * @see   #getReactants
     */
    public void addReactant(IMolecule reactant);
    
    /**
     * Adds an agent to this reaction.
     *
     * @param agent   Molecule added as agent to this reaction
     * @see   #getAgents
     */
    public void addAgent(IMolecule agent);
    
    /**
     * Adds a reactant to this reaction with a stoichiometry coefficient.
     *
     * @param reactant    Molecule added as reactant to this reaction
     * @param coefficient Stoichiometry coefficient for this molecule
     * @see   #getReactants
     */
    public void addReactant(IMolecule reactant, Double coefficient);
    
    /**
     * Adds a product to this reaction.
     *
     * @param product    Molecule added as product to this reaction
     * @see   #getProducts
     */
    public void addProduct(IMolecule product);
    
    /**
     * Adds a product to this reaction.
     *
     * @param product     Molecule added as product to this reaction
     * @param coefficient Stoichiometry coefficient for this molecule
     * @see   #getProducts
     */
    public void addProduct(IMolecule product, Double coefficient);
    
    /**
     * Returns the stoichiometry coefficient of the given reactant.
     *
     * @param  reactant Reactant for which the coefficient is returned.
     * @return -1, if the given molecule is not a product in this Reaction
     * @see    #setReactantCoefficient
     */
    public Double getReactantCoefficient(IMolecule reactant);
    
    /**
     * Returns the stoichiometry coefficient of the given product.
     *
     * @param  product Product for which the coefficient is returned.
     * @return -1, if the given molecule is not a product in this Reaction
     * @see    #setProductCoefficient
     */
    public Double getProductCoefficient(IMolecule product);
	
	/**
     * Sets the coefficient of a a reactant to a given value.
     *
     * @param   reactant    Reactant for which the coefficient is set
     * @param   coefficient The new coefficient for the given reactant
     * @return  true if Molecule has been found and stoichiometry has been set.
     * @see     #getReactantCoefficient
     */
    public boolean setReactantCoefficient(IMolecule reactant, Double coefficient);	
	    
	/**
     * Sets the coefficient of a a product to a given value.
     *
     * @param   product     Product for which the coefficient is set
     * @param   coefficient The new coefficient for the given product
     * @return  true if Molecule has been found and stoichiometry has been set.
     * @see     #getProductCoefficient
     */
    public boolean setProductCoefficient(IMolecule product, Double coefficient);
    
	/**
     * Returns an array of double with the stoichiometric coefficients
	 * of the reactants.
     *
     * @return An array of double's containing the coefficients of the reactants
     * @see    #setReactantCoefficients
     */
    public Double[] getReactantCoefficients();
	
	/**
     * Returns an array of double with the stoichiometric coefficients
	 * of the products.
     *
     * @return An array of double's containing the coefficients of the products
     * @see    #setProductCoefficients
     */
    public Double[] getProductCoefficients();
	
	/**
     * Sets the coefficients of the reactants.
     *
     * @param   coefficients An array of double's containing the coefficients of the reactants
     * @return  true if coefficients have been set.
     * @see     #getReactantCoefficients
     */
    public boolean setReactantCoefficients(Double[] coefficients);
	
	/**
     * Sets the coefficient of the products.
     *
     * @param   coefficients An array of double's containing the coefficients of the products
     * @return  true if coefficients have been set.
     * @see     #getProductCoefficients
     */
    public boolean setProductCoefficients(Double[] coefficients);
    
    /**
     * Sets the direction of the reaction.
     *
     * @param direction The new reaction direction
     * @see   #getDirection
     */
    public void setDirection(IReaction.Direction direction);
    
    /**
     * Returns the {@link IReaction.Direction} of the reaction.
     *
     * @return The direction of this reaction (FORWARD, BACKWARD or BIDIRECTIONAL).
     * @see    #setDirection(Direction)
     */
    public IReaction.Direction getDirection();
    
    /**
     * Adds a mapping between the reactant and product side to this
     * Reaction.
     *
     * @param mapping Mapping to add.
     * @see   #mappings
     */
    public void addMapping(IMapping mapping);
    
    /**
     * Removes a mapping between the reactant and product side to this
     * Reaction.
     *
     * @param  pos  Position of the Mapping to remove.
     * @see   #mappings
     */
    public void removeMapping(int pos);
    
    /**
     * Retrieves a mapping between the reactant and product side to this
     * Reaction.
     *
     * @param pos Position of Mapping to get.
     */
    public IMapping getMapping(int pos);
    
    /**
     * Get the number of mappings between the reactant and product side to this
     * Reaction.
     *
     * @return Number of stored Mappings.
     */
    public int getMappingCount();
    
    
}
