/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk;


/**
 * Represents the idea of a chemical reaction. The reaction consists of 
 * a set of reactants and a set of products.
 *
 * <p>The class mostly represents abstract reactions, such as 2D diagrams,
 * and is not intended to represent reaction trajectories. Such can better
 * be represented with a ChemSequence.
 *
 * @cdk.module core
 *
 * @author     Egon Willighagen <elw38@cam.ac.uk>
 * @cdk.created    2003-02-13
 * @cdk.keyword    reaction
 */
public class Reaction extends ChemObject implements java.io.Serializable, Cloneable {

    public static final int FORWARD        = 1;
    public static final int BACKWARD       = 2;
    public static final int BIDIRECTIONAL = 3;
    
	protected int growArraySize = 3;

    protected SetOfMolecules reactants;
    protected SetOfMolecules products;
    /** These are the used solvent, catalysist etc that normally appear above
        the reaction arrow */
    protected SetOfMolecules agents;
    
    protected Mapping[] map;
    protected int mappingCount;
    
    private int reactionDirection;
    
    /**
     * Constructs an empty, forward reaction.
     */
    public Reaction() {
        this.reactants = new SetOfMolecules();
        this.products = new SetOfMolecules();
        this.agents = new SetOfMolecules();
        this.map = new Mapping[growArraySize];
        mappingCount = 0;
        reactionDirection = FORWARD;
    }
    
    /**
     * Returns the number of reactants in this reaction.
     */
    public int getReactantCount() {
        return reactants.getAtomContainerCount();
    }
    
    /**
     * Returns the number of products in this reaction.
     */
    public int getProductCount() {
        return products.getAtomContainerCount();
    }

    /**
     * Returns an SetOfMolecules containing the reactants in this reaction.
     */
    public SetOfMolecules getReactants() {
        return reactants;
    }

    /**
     * Returns an SetOfMolecules containing the products in this reaction.
     */
    public SetOfMolecules getProducts() {
        return products;
    }
    
    /**
     * Returns an SetOfMolecules containing the agents in this reaction.
     */
    public SetOfMolecules getAgents() {
        return agents;
    }
    
    /**
     * Returns an array of Molecule with a length matching he number
     * of products in this reaction.
     */
    public Mapping[] getMappings() {
        Mapping[] returnMappings = new Mapping[mappingCount];
        System.arraycopy(this.map, 0, returnMappings, 0, returnMappings.length);
        return returnMappings;
    }
    
    /**
     * Adds a reactant to this reaction.
     *
     * @param reactant   Molecule added as reactant to this reaction
     */
    public void addReactant(Molecule reactant) {
        addReactant(reactant, 1.0);
    }
    
    /**
     * Adds an agent to this reaction.
     *
     * @param agent   Molecule added as agent to this reaction
     */
    public void addAgent(Molecule agent) {
        agents.addAtomContainer(agent);
    }

    /**
     * Adds a reactant to this reaction with a stoichiometry coefficient.
     *
     * @param reactant    Molecule added as reactant to this reaction
     * @param coefficient Stoichiometry coefficient for this molecule
     */
    public void addReactant(Molecule reactant, double coefficient) {
        reactants.addAtomContainer(reactant, coefficient);
    }
    
    /**
     * Adds a product to this reaction.
     *
     * @param product    Molecule added as product to this reaction
     */
    public void addProduct(Molecule product) {
        this.addProduct(product, 1.0);
    }
    
    /**
     * Adds a product to this reaction.
     *
     * @param product     Molecule added as product to this reaction
     * @param coefficient Stoichiometry coefficient for this molecule
     */
    public void addProduct(Molecule product, double coefficient) {
        products.addAtomContainer(product, coefficient);
    }
    
    /**
     * Returns the stoichiometry coefficient of the given reactant.
     *
     * @return -1, if the given molecule is not a product in this Reaction
     */
    public double getReactantCoefficient(Molecule reactant) {
        return reactants.getMultiplier(reactant);
    }
    
    /**
     * Returns the stoichiometry coefficient of the given product.
     *
     * @return -1, if the given molecule is not a product in this Reaction
     */
    public double getProductCoefficient(Molecule product) {
        return products.getMultiplier(product);
    }
	
	/**
     * Sets the coefficient of a a reactant to a given value.
     *
     * @return  true if Molecule has been found and stoichiometry has been set.
     */
    public boolean setReactantCoefficient(Molecule reactant, double coefficient) {
        return reactants.setMultiplier(reactant, coefficient);
    }
	
	    
	/**
     * Sets the coefficient of a a product to a given value.
     *
     * @return  true if Molecule has been found and stoichiometry has been set.
     */
    public boolean setProductCoefficient(Molecule product, double coefficient) {
        return products.setMultiplier(product, coefficient);
    }
	
	/**
     * Returns an array of double with the stoichiometric coefficients
	 * of the reactants.
     */
    public double[] getReactantCoefficients() {
        return reactants.getMultipliers();
    }
	
	/**
     * Returns an array of double with the stoichiometric coefficients
	 * of the products.
     */
    public double[] getProductCoefficients() {
        return products.getMultipliers();
    }
	
	
	/**
     * Sets the coefficient of the reactants.
     *
     * @return  true if coefficients have been set.
     */
    public boolean setReactantCoefficients(double[] reactantCoefficients) {
        return reactants.setMultipliers(reactantCoefficients);
    }
	
	/**
     * Sets the coefficient of the products.
     *
     * @return  true if coefficients have been set.
     */
    public boolean setProductCoefficients(double[] productCoefficients) {
        return products.setMultipliers(productCoefficients);
    }
	
    /**
     * Sets the direction of the reaction.
     */
    public void setDirection(int direction) {
        reactionDirection = direction;
    }
    
    /**
     * Returns the direction of the reaction.
     */
    public int getDirection() {
        return reactionDirection;
    }
    
    public void addMapping(Mapping mapping) {
        if (mappingCount + 1 >= map.length) growMappingArray();
        map[mappingCount] = mapping;
        mappingCount++;
    }
    
    protected void growMappingArray() {
        Mapping[] newMap = new Mapping[map.length + growArraySize];
        System.arraycopy(map, 0, newMap, 0, map.length);
        map = newMap;
    }

    /**
     * Returns a one line string representation of this Atom.
     * Methods is conform RFC #9.
     *
     * @return  The string representation of this Atom
     */
    public String toString() {
        StringBuffer description = new StringBuffer();
        description.append("Reaction(");
        description.append(getID() + ", ");
        description.append("#M:" + mappingCount + ", ");
        description.append("reactants=" + reactants.toString() + ", ");
        description.append("products=" + products.toString() + ", ");
        description.append("agents=" + agents.toString());
        description.append(")");
        return description.toString();
    }
    
}
