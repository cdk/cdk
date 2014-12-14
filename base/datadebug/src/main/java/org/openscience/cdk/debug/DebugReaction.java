/* Copyright (C) 2005-2007  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Debugging data class.
 *
 * @author     egonw
 * @cdk.module datadebug
 * @cdk.githash
 */
public class DebugReaction extends Reaction implements IReaction {

    private static final long serialVersionUID = -8958358842308217875L;

    ILoggingTool              logger           = LoggingToolFactory.createLoggingTool(DebugReaction.class);

    public DebugReaction() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public void addListener(IChemObjectListener col) {
        logger.debug("Adding listener: ", col);
        super.addListener(col);
    }

    /** {@inheritDoc} */
    @Override
    public int getListenerCount() {
        logger.debug("Getting listener count: ", super.getListenerCount());
        return super.getListenerCount();
    }

    /** {@inheritDoc} */
    @Override
    public void removeListener(IChemObjectListener col) {
        logger.debug("Removing listener: ", col);
        super.removeListener(col);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyChanged() {
        logger.debug("Notifying changed");
        super.notifyChanged();
    }

    /** {@inheritDoc} */
    @Override
    public void notifyChanged(IChemObjectChangeEvent evt) {
        logger.debug("Notifying changed event: ", evt);
        super.notifyChanged(evt);
    }

    /** {@inheritDoc} */
    @Override
    public void setProperty(Object description, Object property) {
        logger.debug("Setting property: ", description + "=" + property);
        super.setProperty(description, property);
    }

    /** {@inheritDoc} */
    @Override
    public void removeProperty(Object description) {
        logger.debug("Removing property: ", description);
        super.removeProperty(description);
    }

    /** {@inheritDoc} */
    @Override
    public <T> T getProperty(Object description) {
        logger.debug("Getting property: ", description + "=" + super.getProperty(description));
        return super.getProperty(description);
    }

    /** {@inheritDoc} */
    @Override
    public Map<Object, Object> getProperties() {
        logger.debug("Getting properties");
        return super.getProperties();
    }

    /** {@inheritDoc} */
    @Override
    public String getID() {
        logger.debug("Getting ID: ", super.getID());
        return super.getID();
    }

    /** {@inheritDoc} */
    @Override
    public void setID(String identifier) {
        logger.debug("Setting ID: ", identifier);
        super.setID(identifier);
    }

    /** {@inheritDoc} */
    @Override
    public void setFlag(int flagType, boolean flagValue) {
        logger.debug("Setting flag: ", flagType + "=" + flagValue);
        super.setFlag(flagType, flagValue);
    }

    /** {@inheritDoc} */
    @Override
    public boolean getFlag(int flagType) {
        logger.debug("Setting flag: ", flagType + "=" + super.getFlag(flagType));
        return super.getFlag(flagType);
    }

    /** {@inheritDoc} */
    @Override
    public void addProperties(Map<Object, Object> properties) {
        logger.debug("Setting properties: ", properties);
        super.addProperties(properties);
    }

    /** {@inheritDoc} */
    @Override
    public void setFlags(boolean[] flagsNew) {
        logger.debug("Setting flags:", flagsNew.length);
        super.setFlags(flagsNew);
    }

    /** {@inheritDoc} */
    @Override
    public boolean[] getFlags() {
        logger.debug("Getting flags:", super.getFlags().length);
        return super.getFlags();
    }

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
    public IChemObjectBuilder getBuilder() {
        return DebugChemObjectBuilder.getInstance();
    }

    /** {@inheritDoc} */
    @Override
    public int getReactantCount() {
        logger.debug("Getting reactant count: ", super.getReactantCount());
        return super.getReactantCount();
    }

    /** {@inheritDoc} */
    @Override
    public int getProductCount() {
        logger.debug("Getting product count: ", super.getProductCount());
        return super.getProductCount();
    }

    /** {@inheritDoc} */
    @Override
    public IAtomContainerSet getReactants() {
        logger.debug("Getting reactants: ", super.getReactants());
        return super.getReactants();
    }

    /** {@inheritDoc} */
    @Override
    public void setReactants(IAtomContainerSet reactants) {
        logger.debug("Setting reactants: ", reactants);
        super.setReactants(reactants);
    }

    /** {@inheritDoc} */
    @Override
    public IAtomContainerSet getProducts() {
        logger.debug("Getting products: ", super.getProducts());
        return super.getProducts();
    }

    /** {@inheritDoc} */
    @Override
    public void setProducts(IAtomContainerSet products) {
        logger.debug("Setting products: ", products);
        super.setProducts(products);
    }

    /** {@inheritDoc} */
    @Override
    public IAtomContainerSet getAgents() {
        logger.debug("Getting agents: ", super.getAgents());
        return super.getAgents();
    }

    /** {@inheritDoc} */
    @Override
    public void addReactant(IAtomContainer reactant) {
        logger.debug("Adding reactant: ", reactant);
        super.addReactant(reactant);
    }

    /** {@inheritDoc} */
    @Override
    public void addAgent(IAtomContainer agent) {
        logger.debug("Adding agent: ", agent);
        super.addAgent(agent);
    }

    /** {@inheritDoc} */
    @Override
    public void addReactant(IAtomContainer reactant, Double coefficient) {
        logger.debug("Adding reactant with coefficient: ", reactant, "" + coefficient);
        super.addReactant(reactant, coefficient);
    }

    /** {@inheritDoc} */
    @Override
    public void addProduct(IAtomContainer product) {
        logger.debug("Adding product: ", product);
        super.addProduct(product);
    }

    /** {@inheritDoc} */
    @Override
    public void addProduct(IAtomContainer product, Double coefficient) {
        logger.debug("Adding product with coefficient: ", product, "" + coefficient);
        super.addProduct(product, coefficient);
    }

    /** {@inheritDoc} */
    @Override
    public Double getReactantCoefficient(IAtomContainer reactant) {
        logger.debug("Setting reactant coefficient: ", reactant, "" + super.getReactantCoefficient(reactant));
        return super.getReactantCoefficient(reactant);
    }

    /** {@inheritDoc} */
    @Override
    public Double getProductCoefficient(IAtomContainer product) {
        logger.debug("Setting product coefficient: ", product, "" + super.getProductCoefficient(product));
        return super.getProductCoefficient(product);
    }

    /** {@inheritDoc} */
    @Override
    public boolean setReactantCoefficient(IAtomContainer reactant, Double coefficient) {
        logger.debug("Setting reactant coefficient: ", reactant, "" + coefficient);
        return super.setReactantCoefficient(reactant, coefficient);
    }

    /** {@inheritDoc} */
    @Override
    public boolean setProductCoefficient(IAtomContainer product, Double coefficient) {
        logger.debug("Setting product coefficient: ", product, "" + coefficient);
        return super.setProductCoefficient(product, coefficient);
    }

    /** {@inheritDoc} */
    @Override
    public Double[] getReactantCoefficients() {
        logger.debug("Getting reactant coefficients: ", super.getReactantCoefficients().length);
        return super.getReactantCoefficients();
    }

    /** {@inheritDoc} */
    @Override
    public Double[] getProductCoefficients() {
        logger.debug("Getting product coefficients: ", super.getProductCoefficients().length);
        return super.getProductCoefficients();
    }

    /** {@inheritDoc} */
    @Override
    public boolean setReactantCoefficients(Double[] coefficients) {
        logger.debug("Setting reactant coefficients: ", coefficients.length);
        return super.setReactantCoefficients(coefficients);
    }

    /** {@inheritDoc} */
    @Override
    public boolean setProductCoefficients(Double[] coefficients) {
        logger.debug("Setting product coefficients: ", coefficients.length);
        return super.setProductCoefficients(coefficients);
    }

    /** {@inheritDoc} */
    @Override
    public void setDirection(IReaction.Direction direction) {
        logger.debug("Setting direction: ", direction);
        super.setDirection(direction);
    }

    /** {@inheritDoc} */
    @Override
    public IReaction.Direction getDirection() {
        logger.debug("Getting direction: ", super.getDirection());
        return super.getDirection();
    }

}
