/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.debug;

import java.util.Map;

import org.openscience.cdk.Reaction;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Debugging data class.
 * 
 * @author     egonw
 * @cdk.module datadebug
 * @cdk.svnrev  $Revision$
 */
public class DebugReaction extends Reaction
    implements IReaction {

    private static final long serialVersionUID = -8958358842308217875L;
    
    LoggingTool logger = new LoggingTool(DebugReaction.class);

    public DebugReaction() {
    	super();
    }

    public void addListener(IChemObjectListener col) {
		logger.debug("Adding listener: ", col);
		super.addListener(col);
	}

	public int getListenerCount() {
		logger.debug("Getting listener count: ", super.getListenerCount());
		return super.getListenerCount();
	}

	public void removeListener(IChemObjectListener col) {
		logger.debug("Removing listener: ", col);
		super.removeListener(col);
	}

	public void notifyChanged() {
		logger.debug("Notifying changed");
		super.notifyChanged();
	}

	public void notifyChanged(IChemObjectChangeEvent evt) {
		logger.debug("Notifying changed event: ", evt);
		super.notifyChanged(evt);
	}

	public void setProperty(Object description, Object property) {
		logger.debug("Setting property: ", description + "=" + property);
		super.setProperty(description, property);
	}

	public void removeProperty(Object description) {
		logger.debug("Removing property: ", description);
		super.removeProperty(description);
	}

	public Object getProperty(Object description) {
		logger.debug("Getting property: ", description + "=" + super.getProperty(description));
		return super.getProperty(description);
	}

	public Map<Object,Object> getProperties() {
		logger.debug("Getting properties");
		return super.getProperties();
	}

	public String getID() {
		logger.debug("Getting ID: ", super.getID());
		return super.getID();
	}

	public void setID(String identifier) {
		logger.debug("Setting ID: ", identifier);
		super.setID(identifier);
	}

	public void setFlag(int flag_type, boolean flag_value) {
		logger.debug("Setting flag: ", flag_type + "=" + flag_value);
		super.setFlag(flag_type, flag_value);
	}

	public boolean getFlag(int flag_type) {
		logger.debug("Setting flag: ", flag_type + "=" + super.getFlag(flag_type));
		return super.getFlag(flag_type);
	}

	public void setProperties(Map<Object,Object> properties) {
		logger.debug("Setting properties: ", properties);
		super.setProperties(properties);
	}

	public void setFlags(boolean[] flagsNew) {
		logger.debug("Setting flags:", flagsNew.length);
		super.setFlags(flagsNew);
	}

	public boolean[] getFlags() {
		logger.debug("Getting flags:", super.getFlags().length);
		return super.getFlags();
	}

	public Object clone() throws CloneNotSupportedException {
        Object clone = null;
        try {
        	clone = super.clone();
        } catch (Exception exception) {
        	logger.error("Could not clone DebugAtom: " + exception.getMessage(), exception);
        	logger.debug(exception);
        }
        return clone;
	}

	public IChemObjectBuilder getBuilder() {
		return DebugChemObjectBuilder.getInstance();
	}

	public int getReactantCount() {
		logger.debug("Getting reactant count: ", super.getReactantCount());
		return super.getReactantCount();
	}

	public int getProductCount() {
		logger.debug("Getting product count: ", super.getProductCount());
		return super.getProductCount();
	}

	public IMoleculeSet getReactants() {
		logger.debug("Getting reactants: ", super.getReactants());
		return super.getReactants();
	}

	public void setReactants(IMoleculeSet reactants) {
		logger.debug("Setting reactants: ", reactants);
		super.setReactants(reactants);
	}

	public IMoleculeSet getProducts() {
		logger.debug("Getting products: ", super.getProducts());
		return super.getProducts();
	}

	public void setProducts(IMoleculeSet products) {
		logger.debug("Setting products: ", products);
		super.setProducts(products);
	}

	public IMoleculeSet getAgents() {
		logger.debug("Getting agents: ", super.getAgents());
		return super.getAgents();
	}

	public void addReactant(IMolecule reactant) {
		logger.debug("Adding reactant: ", reactant);
		super.addReactant(reactant);
	}

	public void addAgent(IMolecule agent) {
		logger.debug("Adding agent: ", agent);
		super.addAgent(agent);
	}

	public void addReactant(IMolecule reactant, Double coefficient) {
		logger.debug("Adding reactant with coefficient: ", reactant, ""+coefficient);
		super.addReactant(reactant, coefficient);
	}

	public void addProduct(IMolecule product) {
		logger.debug("Adding product: ", product);
		super.addProduct(product);
	}

	public void addProduct(IMolecule product, Double coefficient) {
		logger.debug("Adding product with coefficient: ", product, ""+coefficient);
		super.addProduct(product, coefficient);
	}

	public Double getReactantCoefficient(IMolecule reactant) {
		logger.debug("Setting reactant coefficient: ", reactant, ""+ super.getReactantCoefficient(reactant));
		return super.getReactantCoefficient(reactant);
	}

	public Double getProductCoefficient(IMolecule product) {
		logger.debug("Setting product coefficient: ", product, ""+ super.getProductCoefficient(product));
		return super.getProductCoefficient(product);
	}

	public boolean setReactantCoefficient(IMolecule reactant, Double coefficient) {
		logger.debug("Setting reactant coefficient: ", reactant, ""+coefficient);
		return super.setReactantCoefficient(reactant, coefficient);
	}

	public boolean setProductCoefficient(IMolecule product, Double coefficient) {
		logger.debug("Setting product coefficient: ", product, ""+coefficient);
		return super.setProductCoefficient(product, coefficient);
	}

	public Double[] getReactantCoefficients() {
		logger.debug("Getting reactant coefficients: ", super.getReactantCoefficients().length);
		return super.getReactantCoefficients();
	}

	public Double[] getProductCoefficients() {
		logger.debug("Getting product coefficients: ", super.getProductCoefficients().length);
		return super.getProductCoefficients();
	}

	public boolean setReactantCoefficients(Double[] coefficients) {
		logger.debug("Setting reactant coefficients: ", coefficients.length);
		return super.setReactantCoefficients(coefficients);
	}

	public boolean setProductCoefficients(Double[] coefficients) {
		logger.debug("Setting product coefficients: ", coefficients.length);
		return super.setProductCoefficients(coefficients);
	}

	public void setDirection(IReaction.Direction direction) {
		logger.debug("Setting direction: ", direction);
		super.setDirection(direction);
	}

	public IReaction.Direction getDirection() {
		logger.debug("Getting direction: ", super.getDirection());
		return super.getDirection();
	}

}
