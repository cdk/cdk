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
 * @cdkPackage core
 *
 * @author     Egon Willighagen <elw38@cam.ac.uk>
 * @created    2003-02-13
 * @keyword    reaction
 */
public class Reaction extends ChemObject implements java.io.Serializable, Cloneable {

    public static final int FORWARD        = 1;
    public static final int BACKWARD       = 2;
    public static final int BIDIRECTIONAL = 3;
    
	protected int growArraySize = 3;

    protected Molecule[] reactants;
    protected double[] reactantStoichiometry;
    protected int reactantCount;

    protected Molecule[] products;
    protected double[] productStoichiometry;
    protected int productCount;
    
    protected Mapping[] map;
    protected int mappingCount;
    
    private int reactionDirection;
    
    /**
     * Constructs an empty, forward reaction.
     */
    public Reaction() {
        this.reactants = new Molecule[growArraySize];
        this.reactantStoichiometry = new double[growArraySize];
        reactantCount = 0;
        this.products = new Molecule[growArraySize];
        this.productStoichiometry = new double[growArraySize];
        productCount = 0;
        this.map = new Mapping[growArraySize];
        mappingCount = 0;
        reactionDirection = FORWARD;
    }
    
    /**
     * Returns the number of reactants in this reaction.
     */
    public int getReactantCount() {
        return reactantCount;
    }
    
    /**
     * Returns the number of products in this reaction.
     */
    public int getProductCount() {
        return productCount;
    }

    /**
     * Returns an array of Molecule with a length matching he number
     * of reactants in this reaction.
     */
    public Molecule[] getReactants() {
        Molecule[] returnReactants = new Molecule[getReactantCount()];
        System.arraycopy(this.reactants, 0, returnReactants, 0, returnReactants.length);
        return returnReactants;
    }

    /**
     * Returns an array of Molecule with a length matching he number
     * of products in this reaction.
     */
    public Molecule[] getProducts() {
        Molecule[] returnProducts = new Molecule[getProductCount()];
        System.arraycopy(this.products, 0, returnProducts, 0, returnProducts.length);
        return returnProducts;
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
        this.addReactant(reactant, 1.0);
    }
    
    /**
     * Adds a reactant to this reaction with a stoichiometry coefficient.
     *
     * @param reactant    Molecule added as reactant to this reaction
     * @param coefficient Stoichiometry coefficient for this molecule
     */
    public void addReactant(Molecule reactant, double coefficient) {
        if (reactantCount + 1 >= reactants.length) growReactantArray();
        reactants[reactantCount] = reactant;
        reactantStoichiometry[reactantCount] = coefficient;
        reactantCount++;
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
        if (productCount + 1 >= products.length) growProductArray();
        products[productCount] = product;
        productStoichiometry[productCount] = coefficient;
        productCount++;
    }
    
    /**
     * Returns the stoichiometry coefficient of the given reactant.
     *
     * @return -1, if the given molecule is not a product in this Reaction
     */
    public double getReactantCoefficient(Molecule reactant) {
        for (int i=0; i<reactantCount; i++) {
            if (reactants[i].equals(reactant)) {
                return reactantStoichiometry[i];
            }
        }
        return -1.0;
    }
    
    /**
     * Returns the stoichiometry coefficient of the given product.
     *
     * @return -1, if the given molecule is not a product in this Reaction
     */
    public double getProductCoefficient(Molecule product) {
        for (int i=0; i<productCount; i++) {
            if (products[i].equals(product)) {
                return productStoichiometry[i];
            }
        }
        return -1.0;
    }
    
	/**
     * Returns an array of double with the stoichiometric coefficients
	 * of the reactants.
     */
    public double[] getReactantCoefficients() {
        double[] returnCoeff = new double[this.reactantCount];
        System.arraycopy(this.reactantStoichiometry, 0, returnCoeff, 0, this.reactantCount);
        return returnCoeff;
    }
	
	/**
     * Returns an array of double with the stoichiometric coefficients
	 * of the products.
     */
    public double[] getProductCoefficients() {
        double[] returnCoeff = new double[this.productCount];
        System.arraycopy(this.productStoichiometry, 0, returnCoeff, 0, this.productCount);
        return returnCoeff;
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
    
    protected void growReactantArray() {
        Molecule[] newReactants = new Molecule[reactants.length + growArraySize];
        System.arraycopy(reactants, 0, newReactants, 0, reactants.length);
        reactants = newReactants;
        double[] newCoeffs = new double[reactantStoichiometry.length + growArraySize];
        System.arraycopy(reactantStoichiometry, 0, newCoeffs, 0, reactantStoichiometry.length);
        reactantStoichiometry = newCoeffs;
    }
    
    protected void growProductArray() {
        Molecule[] newProducts = new Molecule[products.length + growArraySize];
        System.arraycopy(products, 0, newProducts, 0, products.length);
        products = newProducts;
        double[] newCoeffs = new double[productStoichiometry.length + growArraySize];
        System.arraycopy(productStoichiometry, 0, newCoeffs, 0, productStoichiometry.length);
        productStoichiometry = newCoeffs;
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
        description.append("#R:" + reactantCount + ", ");
        description.append("#P:" + productCount + ", ");
        description.append("#M:" + mappingCount + ", ");
        Molecule[] reactants = getReactants();
        for (int i=0; i<reactantCount; i++) {
            description.append(reactants[i].toString());
        }
        Molecule[] products = getProducts();
        for (int i=0; i<productCount; i++) {
            description.append(products[i].toString());
        }
        description.append(")");
        return description.toString();
    }
    
}
