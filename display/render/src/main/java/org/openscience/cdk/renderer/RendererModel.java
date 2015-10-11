/* Copyright (C) 2008-2009  Gilleain Torrance <gilleain@users.sf.net>
 *               2008-2009  Arvid Berg <goglepox@users.sf.net>
 *                    2009  Stefan Kuhn <shk3@users.sf.net>
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
 *  */
package org.openscience.cdk.renderer;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.event.ICDKChangeListener;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.IGeneratorParameter;
import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;

/**
 * Model for {@link IRenderer} that contains settings for drawing objects.
 *
 * @author maclean
 * @cdk.module render
 * @cdk.githash
 */
public class RendererModel implements Serializable, Cloneable {

    private static final long                  serialVersionUID     = -4420308906715213445L;

    /* If true, the class will notify its listeners of changes */
    private boolean                            notification         = true;

    private transient List<ICDKChangeListener> listeners            = new ArrayList<ICDKChangeListener>();

    private Map<IAtom, String>                 toolTipTextMap       = new HashMap<IAtom, String>();

    private IAtom                              highlightedAtom      = null;

    private IBond                              highlightedBond      = null;

    private IAtomContainer                     externalSelectedPart = null;

    private IAtomContainer                     clipboardContent     = null;

    private IChemObjectSelection               selection;

    private Map<IAtom, IAtom>                  merge                = new HashMap<IAtom, IAtom>();

    /**
     * Color of a selection.
     */
    public static class SelectionColor extends AbstractGeneratorParameter<Color> {

        /** {@inheritDoc} */
        @Override
        public Color getDefault() {
            return new Color(0x49DFFF);
        }
    }

    /**
     * The color used to highlight external selections.
     */
    public static class ExternalHighlightColor extends AbstractGeneratorParameter<Color> {

        /** {@inheritDoc} */
        @Override
        public Color getDefault() {
            return Color.gray;
        }
    }

    private IGeneratorParameter<Color> externalHighlightColor = new ExternalHighlightColor();

    /**
     * Padding between molecules in a grid or row.
     */
    public static class Padding extends AbstractGeneratorParameter<Double> {

        /** {@inheritDoc} */
        @Override
        public Double getDefault() {
            return 16d;
        }
    }

    /**
     * The color hash is used to color substructures.
     */
    public static class ColorHash extends AbstractGeneratorParameter<Map<IChemObject, Color>> {

        /** {@inheritDoc} */
        @Override
        public Map<IChemObject, Color> getDefault() {
            return new Hashtable<IChemObject, Color>();
        }
    }

    private IGeneratorParameter<Map<IChemObject, Color>> colorHash           = new ColorHash();

    /**
     * Size of title font relative compared to atom symbols
     */
    public static class TitleFontScale extends AbstractGeneratorParameter<Double> {

        /** {@inheritDoc} */
        @Override
        public Double getDefault() {
            return 0.8d;
        }
    }

    /**
     * Color of title text.
     */
    public static class TitleColor extends AbstractGeneratorParameter<Color> {

        /** {@inheritDoc} */
        @Override
        public Color getDefault() {
            return Color.RED;
        }
    }

    /**
     * If format supports it (e.g. SVG) should marked up elements (id and classes)
     * be output.
     */
    public static class MarkedOutput extends AbstractGeneratorParameter<Boolean> {

        /** {@inheritDoc} */
        @Override
        public Boolean getDefault() {
            return true;
        }
    }

    /**
     * A map of {@link IGeneratorParameter} class names to instances.
     */
    private Map<String, IGeneratorParameter<?>>          renderingParameters = new HashMap<String, IGeneratorParameter<?>>();

    /**
     * Construct a renderer model with no parameters. To put parameters into
     * the model, use the registerParameters method.
     */
    public RendererModel() {
        renderingParameters.put(colorHash.getClass().getName(), colorHash);
        renderingParameters.put(externalHighlightColor.getClass().getName(), externalHighlightColor);
        renderingParameters.put(SelectionColor.class.getName(), new SelectionColor());
        renderingParameters.put(Padding.class.getName(), new Padding());
        renderingParameters.put(TitleFontScale.class.getName(), new TitleFontScale());
        renderingParameters.put(TitleColor.class.getName(), new TitleColor());
        renderingParameters.put(MarkedOutput.class.getName(), new MarkedOutput());
    }

    /**
     * Returns all {@link IGeneratorParameter}s for the current
     * {@link RendererModel}.
     *
     * @return a new List with {@link IGeneratorParameter}s
     */
    public List<IGeneratorParameter<?>> getRenderingParameters() {
        List<IGeneratorParameter<?>> parameters = new ArrayList<IGeneratorParameter<?>>();
        parameters.addAll(renderingParameters.values());
        return parameters;
    }

    /**
     * Returns the {@link IGeneratorParameter} for the active {@link IRenderer}.
     * It returns a new instance of it was unregistered.
     *
     * @param param {@link IGeneratorParameter} to get the value of.
     * @return the {@link IGeneratorParameter} instance with the active value.
     */
    public <T extends IGeneratorParameter<?>> T getParameter(Class<T> param) {
        if (renderingParameters.containsKey(param.getName())) return (T) renderingParameters.get(param.getName());

        // the parameter was not registered yet, so we throw an exception to
        // indicate that the API is not used correctly.
        throw new IllegalAccessError("You requested the active parameter of type " + param.getName() + ", but it "
                + "has not been registered yet. Did you " + "make sure the IGeneratorParameter is registered, by "
                + "registring the appropriate IGenerator? Alternatively, "
                + "you can use getDefault() to query the default value for " + "any parameter on the classpath.");
    }

    /**
     * Returns the default value for the {@link IGeneratorParameter} for the
     * active {@link IRenderer}.
     *
     * @param param {@link IGeneratorParameter} to get the value of.
     * @return the default value for which the type is defined by the provided
     *         {@link IGeneratorParameter}-typed <code>param</code> parameter.
     *
     * @see #get(Class)
     */
    public <T extends IGeneratorParameter<S>, S> S getDefault(Class<T> param) {
        if (renderingParameters.containsKey(param.getName())) return getParameter(param).getDefault();

        // OK, this parameter is not registered, but that's fine, as we are
        // only to return the default value anyway...
        try {
            return param.newInstance().getDefault();
        } catch (InstantiationException exception) {
            throw new IllegalArgumentException("Could not instantiate a default " + param.getClass().getName(),
                    exception);
        } catch (IllegalAccessException exception) {
            throw new IllegalArgumentException("Could not instantiate a default " + param.getClass().getName(),
                    exception);
        }
    }

    /**
     * Sets the {@link IGeneratorParameter} for the active {@link IRenderer}.
     * @param <T>
     *
     * @param paramType {@link IGeneratorParameter} to set the value of.
     * @param value     new {@link IGeneratorParameter} value
     */
    public <T extends IGeneratorParameter<S>, S, U extends S> void set(Class<T> paramType, U value) {
        T parameter = getParameter(paramType);
        parameter.setValue(value);
    }

    /**
     * Returns the {@link IGeneratorParameter} for the active {@link IRenderer}.
     * @param <T>
     *
     * @param paramType {@link IGeneratorParameter} to get the value of.
     * @return the {@link IGeneratorParameter} value.
     *
     * @see #getParameter(Class)
     */
    public <T extends IGeneratorParameter<S>, S> S get(Class<T> paramType) {
        return getParameter(paramType).getValue();
    }

    /**
     * Registers rendering parameters from {@link IGenerator}s
     * with this model.
     *
     * @param generator
     */
    public void registerParameters(IGenerator<? extends IChemObject> generator) {
        for (IGeneratorParameter<?> param : generator.getParameters()) {
            renderingParameters.put(param.getClass().getName(), param);
        }
    }

    /**
     * Set the selected {@link IChemObject}s.
     *
     * @param selection an {@link IChemObjectSelection} with selected
     *                  {@link IChemObject}s
     */
    public void setSelection(IChemObjectSelection selection) {
        this.selection = selection;
    }

    /**
     * Returns an {@link IChemObjectSelection} with the currently selected
     * {@link IChemObject}s.
     *
     * @return the current selected {@link IChemObject}s
     */
    public IChemObjectSelection getSelection() {
        return this.selection;
    }

    /**
     * This is the central facility for handling "merges" of atoms. A merge occures if during moving atoms an atom is in Range of another atom.
     * These atoms are then put into the merge map as a key-value pair. During the move, the atoms are then marked by a circle and on releasing the mouse
     * they get actually merged, meaning one atom is removed and bonds pointing to this atom are made to point to the atom it has been marged with.
     *
     * @return Returns the merge.map
     */
    public Map<IAtom, IAtom> getMerge() {
        return merge;
    }

    /**
     * Returns the atom currently highlighted.
     *
     * @return the atom currently highlighted
     */
    public IAtom getHighlightedAtom() {
        return this.highlightedAtom;
    }

    /**
     * Sets the atom currently highlighted.
     *
     * @param highlightedAtom
     *            The atom to be highlighted
     */
    public void setHighlightedAtom(IAtom highlightedAtom) {
        if ((this.highlightedAtom != null) || (highlightedAtom != null)) {
            this.highlightedAtom = highlightedAtom;
            fireChange();
        }
    }

    /**
     * Returns the Bond currently highlighted.
     *
     * @return the Bond currently highlighted
     */
    public IBond getHighlightedBond() {
        return this.highlightedBond;
    }

    /**
     * Sets the Bond currently highlighted.
     *
     * @param highlightedBond
     *            The Bond to be currently highlighted
     */
    public void setHighlightedBond(IBond highlightedBond) {
        if ((this.highlightedBond != null) || (highlightedBond != null)) {
            this.highlightedBond = highlightedBond;
            fireChange();
        }
    }

    /**
     * Returns the atoms and bonds on the Renderer2D clipboard. If the clipboard
     * is empty it returns null. Primarily used for copy/paste.
     *
     * @return an atomcontainer with the atoms and bonds on the clipboard.
     */
    public IAtomContainer getClipboardContent() {
        return clipboardContent;
    }

    /**
     * Sets the atoms and bonds on the Renderer2D clipboard. Primarily used for
     * copy/paste.
     *
     * @param content
     *            the new content of the clipboard.
     */
    public void setClipboardContent(IAtomContainer content) {
        this.clipboardContent = content;
    }

    /**
     * Adds a change listener to the list of listeners.
     *
     * @param listener
     *            The listener added to the list
     */
    public void addCDKChangeListener(ICDKChangeListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<ICDKChangeListener>();
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a change listener from the list of listeners.
     *
     * @param listener
     *            The listener removed from the list
     */
    public void removeCDKChangeListener(ICDKChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies registered listeners of certain changes that have occurred in
     * this model.
     */
    public void fireChange() {
        if (getNotification() && listeners != null) {
            EventObject event = new EventObject(this);
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).stateChanged(event);
            }
        }
    }

    /**
     * Gets the toolTipText for atom certain atom.
     *
     * @param atom
     *            The atom.
     * @return The toolTipText value.
     */
    public String getToolTipText(IAtom atom) {
        if (toolTipTextMap.get(atom) != null) {
            return toolTipTextMap.get(atom);
        } else {
            return null;
        }
    }

    /**
     * Sets the toolTipTextMap.
     *
     * @param map
     *            A map containing Atoms of the current molecule as keys and
     *            Strings to display as values. A line break will be inserted
     *            where a \n is in the string.
     */
    public void setToolTipTextMap(Map<IAtom, String> map) {
        toolTipTextMap = map;
        fireChange();
    }

    /**
     * Gets the toolTipTextMap.
     *
     * @return The toolTipTextValue.
     */
    public Map<IAtom, String> getToolTipTextMap() {
        return toolTipTextMap;
    }

    /**
     * Get externally selected atoms. These are atoms selected externally in e.
     * g. Bioclipse via the ChemObjectTree, painted in externalSelectedPartColor
     *
     * @return the selected part
     */
    public IAtomContainer getExternalSelectedPart() {
        return externalSelectedPart;
    }

    /**
     * Set externally selected atoms. These are atoms selected externally in e.
     * g. Bioclipse via the ChemObjectTree, painted in externalSelectedPartColor
     *
     * @param externalSelectedPart
     *            the selected part
     */
    public void setExternalSelectedPart(IAtomContainer externalSelectedPart) {
        this.externalSelectedPart = externalSelectedPart;
        Map<IChemObject, Color> colorHash = getParameter(ColorHash.class).getValue();
        colorHash.clear();
        if (externalSelectedPart != null) {
            for (int i = 0; i < externalSelectedPart.getAtomCount(); i++) {
                colorHash.put(externalSelectedPart.getAtom(i), getParameter(ExternalHighlightColor.class).getValue());
            }
            Iterator<IBond> bonds = externalSelectedPart.bonds().iterator();
            while (bonds.hasNext()) {
                colorHash.put(bonds.next(), getParameter(ExternalHighlightColor.class).getValue());
            }
        }
        fireChange();
    }

    /**
     * Determines if the model sends around change notifications.
     *
     * @return true, if notifications are sent around upon changes
     */
    public boolean getNotification() {
        return notification;
    }

    /**
     * Returns true if the passed {@link IGeneratorParameter}s has been
     * registered.
     *
     * @param  param   parameter for which it is tested if it is registered
     * @return boolean indicating the parameters is registered
     */
    public <T extends IGeneratorParameter<?>> boolean hasParameter(Class<T> param) {
        return renderingParameters.containsKey(param.getName());
    }

    /**
     * Dis- or enables sending around change notifications.
     *
     * @param notification true if notifications should be sent, false otherwise.
     */
    public void setNotification(boolean notification) {
        this.notification = notification;
    };

}
