/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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

import javax.vecmath.*;

/**
 * Represents the idea of a chemical reaction. The reaction consists of 
 * a set of reactants and a set of products.
 *
 * @author     Egon Willighagen <elw38@cam.ac.uk>
 * @created    2003-02-13
 * @keyword    reaction
 */
public class Reaction extends ChemObject implements Cloneable {

	protected int growArraySize = 2;

    protected Molecule[] reactants;
    protected int reactantCount;    
    protected Molecule[] products;    
    protected int productCount;    
    
    /**
     * Constructs an empty reaction;
     */
    public Reaction() {
        this.reactants = new Molecule[growArraySize];
        reactantCount = 0;
        this.products = new Molecule[growArraySize];
        productCount = 0;
    }
    
    public int getReactantCount() {
        return reactantCount;
    }
    
    public int getProductCount() {
        return productCount;
    }

    public Molecule[] getReactants() {
        Molecule[] returnReactants = new Molecule[getReactantCount()];
        System.arraycopy(this.reactants, 0, returnReactants, 0, returnReactants.length);
        return returnReactants;
    }

    public Molecule[] getProducts() {
        Molecule[] returnProducts = new Molecule[getProductCount()];
        System.arraycopy(this.products, 0, returnProducts, 0, returnProducts.length);
        return returnProducts;
    }
    
    public void addReactant(Molecule reactant) {
        if (reactantCount + 1 >= reactants.length) growReactantArray();
        reactants[reactantCount] = reactant;
        reactantCount++;
    }
    
    public void addProduct(Molecule product) {
        if (productCount + 1 >= products.length) growProductArray();
        products[productCount] = product;
        productCount++;
    }
    
    protected void growReactantArray() {
        Molecule[] newReactants = new Molecule[reactants.length + growArraySize];
        System.arraycopy(reactants, 0, newReactants, 0, reactants.length);
        reactants = newReactants;
    }
    
    protected void growProductArray() {
        Molecule[] newProducts = new Molecule[products.length + growArraySize];
        System.arraycopy(products, 0, newProducts, 0, products.length);
        products = newProducts;
    }
}
