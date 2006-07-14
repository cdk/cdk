/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
 *
 * @author      Egon Willighagen <elw38@cam.ac.uk>
 * @cdk.created 2003-02-13
 * @cdk.keyword reaction
 */
public interface IReaction extends IChemObject {

	/** Reaction of which the equilibrium is not set. */
    public static final int UNKNOWN_DIRECTION = 0;
    /** Reaction equalibrium which is (almost) fully on the product side. 
        Often denoted with a forward arrow. */
    public static final int FORWARD           = 1;
    /** Reaction equalibrium which is (almost) fully on the reactant side. 
        Often denoted with a backward arrow. */
    public static final int BACKWARD          = 2;
    /** Reaction equalibrium state. Often denoted by a double arrow. */
    public static final int BIDIRECTIONAL     = 3;

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
     * Returns a ISetOfMolecules containing the reactants in this reaction.
     *
     * @return A ISetOfMolecules containing the reactants in this reaction
     * @see    #setReactants
     */
    public ISetOfMolecules getReactants();

    /**
     * Assigns a ISetOfMolecules to the reactants in this reaction.
     *
     * @param  reactants The new set of reactants
     * @see              #getReactants
     */
    public void setReactants(ISetOfMolecules reactants);
	
    /**
     * Returns a ISetOfMolecules containing the products of this reaction.
     *
     * @return A ISetOfMolecules containing the products in this reaction
     * @see    #setProducts
     */
    public ISetOfMolecules getProducts();
    
	/**
     * Assigns a ISetOfMolecules to the products of this reaction.
     *
     * @param products The new set of products
     * @see            #getProducts
     */
    public void setProducts(ISetOfMolecules products);
	
    /**
     * Returns a ISetOfMolecules containing the agents in this reaction.
     *
     * @return A ISetOfMolecules containing the agents in this reaction
     * @see    #addAgent
     */
    public ISetOfMolecules getAgents();
    
    /**
     * Returns the mappings between the reactant and the product side.
     *
     * @return An array of Mapping's.
     * @see    #addMapping
     */
    public IMapping[] getMappings();
    
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
    public void addReactant(IMolecule reactant, double coefficient);
    
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
    public void addProduct(IMolecule product, double coefficient);
    
    /**
     * Returns the stoichiometry coefficient of the given reactant.
     *
     * @param  reactant Reactant for which the coefficient is returned.
     * @return -1, if the given molecule is not a product in this Reaction
     * @see    #setReactantCoefficient
     */
    public double getReactantCoefficient(IMolecule reactant);
    
    /**
     * Returns the stoichiometry coefficient of the given product.
     *
     * @param  product Product for which the coefficient is returned.
     * @return -1, if the given molecule is not a product in this Reaction
     * @see    #setProductCoefficient
     */
    public double getProductCoefficient(IMolecule product);
	
	/**
     * Sets the coefficient of a a reactant to a given value.
     *
     * @param   reactant    Reactant for which the coefficient is set
     * @param   coefficient The new coefficient for the given reactant
     * @return  true if Molecule has been found and stoichiometry has been set.
     * @see     #getReactantCoefficient
     */
    public boolean setReactantCoefficient(IMolecule reactant, double coefficient);	
	    
	/**
     * Sets the coefficient of a a product to a given value.
     *
     * @param   product     Product for which the coefficient is set
     * @param   coefficient The new coefficient for the given product
     * @return  true if Molecule has been found and stoichiometry has been set.
     * @see     #getProductCoefficient
     */
    public boolean setProductCoefficient(IMolecule product, double coefficient);
    
	/**
     * Returns an array of double with the stoichiometric coefficients
	 * of the reactants.
     *
     * @return An array of double's containing the coefficients of the reactants
     * @see    #setReactantCoefficients
     */
    public double[] getReactantCoefficients();
	
	/**
     * Returns an array of double with the stoichiometric coefficients
	 * of the products.
     *
     * @return An array of double's containing the coefficients of the products
     * @see    #setProductCoefficients
     */
    public double[] getProductCoefficients();
	
	/**
     * Sets the coefficients of the reactants.
     *
     * @param   coefficients An array of double's containing the coefficients of the reactants
     * @return  true if coefficients have been set.
     * @see     #getReactantCoefficients
     */
    public boolean setReactantCoefficients(double[] coefficients);
	
	/**
     * Sets the coefficient of the products.
     *
     * @param   coefficients An array of double's containing the coefficients of the products
     * @return  true if coefficients have been set.
     * @see     #getProductCoefficients
     */
    public boolean setProductCoefficients(double[] coefficients);
    
    /**
     * Sets the direction of the reaction.
     *
     * @param direction The new reaction direction
     * @see   #getDirection
     */
    public void setDirection(int direction);
    
    /**
     * Returns the direction of the reaction.
     *
     * @return The direction of this reaction (FORWARD, BACKWARD or BIDIRECTIONAL).
     * @see    #BIDIRECTIONAL
     * @see    #setDirection
     */
    public int getDirection();
    
    /**
     * Adds a mapping between the reactant and product side to this
     * Reaction.
     *
     * @param mapping Mapping to add.
     * @see   #getMappings
     */
    public void addMapping(org.openscience.cdk.interfaces.IMapping mapping);
    
}
