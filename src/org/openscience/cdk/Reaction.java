/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2003-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk;

import java.io.Serializable;
import java.util.Hashtable;

import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;

/**
 * Represents the idea of a chemical reaction. The reaction consists of 
 * a set of reactants and a set of products.
 *
 * <p>The class mostly represents abstract reactions, such as 2D diagrams,
 * and is not intended to represent reaction trajectories. Such can better
 * be represented with a ChemSequence.
 *
 * @cdk.module data
 *
 * @author      Egon Willighagen <elw38@cam.ac.uk>
 * @cdk.created 2003-02-13
 * @cdk.keyword reaction
 */
public class Reaction extends ChemObject implements Serializable, IReaction, Cloneable {

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -554752558363533678L;

	protected int growArraySize = 3;

    protected org.openscience.cdk.interfaces.IMoleculeSet reactants;
    protected org.openscience.cdk.interfaces.IMoleculeSet products;
    /** These are the used solvent, catalysist etc that normally appear above
        the reaction arrow */
    protected org.openscience.cdk.interfaces.IMoleculeSet agents;
    
    protected org.openscience.cdk.interfaces.IMapping[] map;
    protected int mappingCount;
    
    private int reactionDirection;
    
    /**
     * Constructs an empty, forward reaction.
     */
    public Reaction() {
        this.reactants = new MoleculeSet();
        this.products = new MoleculeSet();
        this.agents = new MoleculeSet();
        this.map = new Mapping[growArraySize];
        mappingCount = 0;
        reactionDirection = FORWARD;
    }
    
    /**
     * Returns the number of reactants in this reaction.
     *
     * @return The number of reactants in this reaction
     */
    public int getReactantCount() {
        return reactants.getAtomContainerCount();
    }
    
    /**
     * Returns the number of products in this reaction.
     *
     * @return The number of products in this reaction
     */
    public int getProductCount() {
        return products.getAtomContainerCount();
    }

    /**
     * Returns a MoleculeSet containing the reactants in this reaction.
     *
     * @return A MoleculeSet containing the reactants in this reaction
     * @see    #setReactants
     */
    public org.openscience.cdk.interfaces.IMoleculeSet getReactants() {
        return (MoleculeSet)reactants;
    }

    /**
     * Assigns a MoleculeSet to the reactants in this reaction.
     *
     * @param setOfMolecules The new set of reactants
     * @see   #getReactants
     */
    public void setReactants(org.openscience.cdk.interfaces.IMoleculeSet setOfMolecules) {
        reactants = setOfMolecules;
	notifyChanged();
    }
	
    /**
     * Returns a MoleculeSet containing the products of this reaction.
     *
     * @return A MoleculeSet containing the products in this reaction
     * @see    #setProducts
     */
    public org.openscience.cdk.interfaces.IMoleculeSet getProducts() {
        return (MoleculeSet)products;
    }
    
	/**
     * Assigns a MoleculeSet to the products of this reaction.
     *
     * @param setOfMolecules The new set of products
     * @see   #getProducts
     */
    public void setProducts(org.openscience.cdk.interfaces.IMoleculeSet setOfMolecules) {
        products = setOfMolecules;
	notifyChanged();
    }
	
    /**
     * Returns a MoleculeSet containing the agents in this reaction.
     *
     * @return A MoleculeSet containing the agents in this reaction
     * @see    #addAgent
     */
    public org.openscience.cdk.interfaces.IMoleculeSet getAgents() {
        return (MoleculeSet)agents;
    }
    
    /**
     * Returns the mappings between the reactant and the product side.
     *
     * @return An Iterator to the Mappings.
     * @see    #addMapping
     */
    public java.util.Iterator mappings() {
        Mapping[] returnMappings = new Mapping[mappingCount];
        System.arraycopy(this.map, 0, returnMappings, 0, returnMappings.length);
        return new MappingIterator();
    }
    
    /**
     * The inner Mapping Iterator class.
     *
     */
    private class MappingIterator implements java.util.Iterator {

        private int pointer = 0;
    	
        public boolean hasNext() {
            if (pointer < mappingCount) return true;
	    return false;
        }

        public Object next() {
            return map[pointer++];
        }

        public void remove() {
            removeMapping(--pointer);
        }
    	
    }
    
    /**
     * Adds a reactant to this reaction.
     *
     * @param reactant   Molecule added as reactant to this reaction
     * @see   #getReactants
     */
    public void addReactant(org.openscience.cdk.interfaces.IMolecule reactant) {
        addReactant(reactant, 1.0);
	/* notifyChanged() is called by 
	   addReactant(Molecule reactant, double coefficient) */
    }
    
    /**
     * Adds an agent to this reaction.
     *
     * @param agent   Molecule added as agent to this reaction
     * @see   #getAgents
     */
    public void addAgent(org.openscience.cdk.interfaces.IMolecule agent) {
        agents.addAtomContainer(agent);
	notifyChanged();
    }

    /**
     * Adds a reactant to this reaction with a stoichiometry coefficient.
     *
     * @param reactant    Molecule added as reactant to this reaction
     * @param coefficient Stoichiometry coefficient for this molecule
     * @see   #getReactants
     */
    public void addReactant(org.openscience.cdk.interfaces.IMolecule reactant, double coefficient) {
        reactants.addAtomContainer(reactant, coefficient);
	notifyChanged();
    }
    
    /**
     * Adds a product to this reaction.
     *
     * @param product    Molecule added as product to this reaction
     * @see   #getProducts
     */
    public void addProduct(org.openscience.cdk.interfaces.IMolecule product) {
        this.addProduct(product, 1.0);
	/* notifyChanged() is called by 
	addProduct(Molecule product, double coefficient)*/
    }
    
    /**
     * Adds a product to this reaction.
     *
     * @param product     Molecule added as product to this reaction
     * @param coefficient Stoichiometry coefficient for this molecule
     * @see   #getProducts
     */
    public void addProduct(org.openscience.cdk.interfaces.IMolecule product, double coefficient) {
        products.addAtomContainer(product, coefficient);
	/* notifyChanged() is called by 
	   addReactant(Molecule reactant, double coefficient) */
    }
    
    /**
     * Returns the stoichiometry coefficient of the given reactant.
     *
     * @param  reactant Reactant for which the coefficient is returned.
     * @return -1, if the given molecule is not a product in this Reaction
     * @see    #setReactantCoefficient
     */
    public double getReactantCoefficient(org.openscience.cdk.interfaces.IMolecule reactant) {
        return reactants.getMultiplier(reactant);
    }
    
    /**
     * Returns the stoichiometry coefficient of the given product.
     *
     * @param  product Product for which the coefficient is returned.
     * @return -1, if the given molecule is not a product in this Reaction
     * @see    #setProductCoefficient
     */
    public double getProductCoefficient(org.openscience.cdk.interfaces.IMolecule product) {
        return products.getMultiplier(product);
    }
	
	/**
     * Sets the coefficient of a a reactant to a given value.
     *
     * @param   reactant    Reactant for which the coefficient is set
     * @param   coefficient The new coefficient for the given reactant
     * @return  true if Molecule has been found and stoichiometry has been set.
     * @see     #getReactantCoefficient
     */
    public boolean setReactantCoefficient(org.openscience.cdk.interfaces.IMolecule reactant, double coefficient) {
	notifyChanged();
        return reactants.setMultiplier(reactant, coefficient);
    }
	
	    
	/**
     * Sets the coefficient of a a product to a given value.
     *
     * @param   product     Product for which the coefficient is set
     * @param   coefficient The new coefficient for the given product
     * @return  true if Molecule has been found and stoichiometry has been set.
     * @see     #getProductCoefficient
     */
    public boolean setProductCoefficient(org.openscience.cdk.interfaces.IMolecule product, double coefficient) {
	notifyChanged();
        return products.setMultiplier(product, coefficient);
    }
	
	/**
     * Returns an array of double with the stoichiometric coefficients
	 * of the reactants.
     *
     * @return An array of double's containing the coefficients of the reactants
     * @see    #setReactantCoefficients
     */
    public double[] getReactantCoefficients() {
        return reactants.getMultipliers();
    }
	
	/**
     * Returns an array of double with the stoichiometric coefficients
	 * of the products.
     *
     * @return An array of double's containing the coefficients of the products
     * @see    #setProductCoefficients
     */
    public double[] getProductCoefficients() {
        return products.getMultipliers();
    }
	
	
	/**
     * Sets the coefficients of the reactants.
     *
     * @param   coefficients An array of double's containing the coefficients of the reactants
     * @return  true if coefficients have been set.
     * @see     #getReactantCoefficients
     */
    public boolean setReactantCoefficients(double[] coefficients) {
	notifyChanged();
        return reactants.setMultipliers(coefficients);
    }
	
	/**
     * Sets the coefficient of the products.
     *
     * @param   coefficients An array of double's containing the coefficients of the products
     * @return  true if coefficients have been set.
     * @see     #getProductCoefficients
     */
    public boolean setProductCoefficients(double[] coefficients) {
	notifyChanged();
        return products.setMultipliers(coefficients);
    }
	
    /**
     * Sets the direction of the reaction.
     *
     * @param direction The new reaction direction
     * @see   #getDirection
     */
    public void setDirection(int direction) {
	reactionDirection = direction;
	notifyChanged();
    }
    
    /**
     * Returns the direction of the reaction.
     *
     * @return The direction of this reaction (FORWARD, BACKWARD or BIDIRECTIONAL).
     * @see    #BIDIRECTIONAL
     * @see    #setDirection
     */
    public int getDirection() {
        return reactionDirection;
    }
    
    /**
     * Adds a mapping between the reactant and product side to this
     * Reaction.
     *
     * @param mapping Mapping to add.
     * @see   #mappings
     */
    public void addMapping(org.openscience.cdk.interfaces.IMapping mapping) {
        if (mappingCount + 1 >= map.length) growMappingArray();
        map[mappingCount] = mapping;
        mappingCount++;
        notifyChanged();
    }
    
    /**
     * Removes a mapping between the reactant and product side to this
     * Reaction.
     *
     * @param  pos  Position of the Mapping to remove.
     * @see   #mappings
     */
    public void removeMapping(int pos) {
		for (int i = pos; i < mappingCount - 1; i++) {
			map[i] = map[i + 1];
		}
		map[mappingCount - 1] = null;
		mappingCount--;
		notifyChanged();
	}
    
    /**
     * Retrieves a mapping between the reactant and product side to this
     * Reaction.
     *
     * @param pos Position of Mapping to get.
     */
    public IMapping getMapping(int pos) {
    	return map[pos];
    }
    
    /**
     * Get the number of mappings between the reactant and product side to this
     * Reaction.
     *
     * @return Number of stored Mappings.
     */
    public int getMappingCount() {
    	return mappingCount;
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
        StringBuffer description = new StringBuffer(64);
        description.append("Reaction(");
        description.append(getID());
        description.append(", #M:").append(mappingCount);
        description.append(", reactants=").append(reactants.toString());
        description.append(", products=").append(products.toString());
        description.append(", agents=").append(agents.toString());
        description.append(')');
        return description.toString();
    }
    
	/**
	 * Clones this <code>Reaction</code> and its content.
	 *
	 * @return  The cloned object
	 */
	public Object clone() throws CloneNotSupportedException {
		Reaction clone = (Reaction)super.clone();
        // clone the reactants, products and agents
        clone.reactants = (MoleculeSet)((MoleculeSet)reactants).clone();
        clone.agents = (MoleculeSet)((MoleculeSet)agents).clone();
        clone.products = (MoleculeSet)((MoleculeSet)products).clone();
        // create a Map of corresponding atoms for molecules (key: original Atom, 
        // value: clone Atom)
        Hashtable atomatom = new Hashtable();
        for (int i = 0; i < ((MoleculeSet)reactants).getMoleculeCount(); ++i) {
            Molecule mol = (Molecule)((MoleculeSet)reactants).getMolecule(i);
            Molecule mol2 = (Molecule)clone.reactants.getMolecule(i);
            for (int j = 0; j < mol.getAtomCount(); ++j) atomatom.put(mol.getAtom(j), mol2.getAtom(j));
        }
        
        // clone the maps
		clone.map = new Mapping[map.length];
		for (int f = 0; f < mappingCount; f++) {
			clone.map[f] = new Mapping((ChemObject)atomatom.get(map[f].getChemObject(0)), (ChemObject)atomatom.get(map[f].getChemObject(1)));
		}
		return clone;
	}
}
