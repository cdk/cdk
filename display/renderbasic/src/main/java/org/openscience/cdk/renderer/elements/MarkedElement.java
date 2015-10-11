/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.renderer.elements;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A marked element adds meta-data (id and tags) to a CDK rendering
 * element (or group of elements). The id should be unique per depiction.
 * The primary use case it to be able to set the 'id' and 'class'
 * attributes in SVG.
 * <p/>
 * To set the mol, atom, or bond id set a String property to {@link #ID_KEY}.
 * Similarly, the {@link #CLASS_KEY} can be used to set the classes.
 * <p/>
 * <pre>{@code
 * IAtomContainer mol;
 * atom.setProperty(MarkedElement.ID_KEY, "my_atm_id");
 * atom.setProperty(MarkedElement.CLASS_KEY, "h_donor");
 * atom.setProperty(MarkedElement.CLASS_KEY, "h_acceptor");
 * }</pre>
 */
public final class MarkedElement implements IRenderingElement {

    public static final String ID_KEY    = MarkedElement.class.getName() + "_ID";
    public static final String CLASS_KEY = MarkedElement.class.getName() + "_CLS";

    final   IRenderingElement elem;
    private String            id;
    private final List<String> classes = new ArrayList<>(5);

    private MarkedElement(IRenderingElement elem) {
        this.elem = elem;
    }

    /**
     * Set the identifier of the tagged element.
     *
     * @param id the id
     */
    private void setId(String id) {
        this.id = id;
    }

    /**
     * Add a cls to the element.
     *
     * @param cls a cls
     */
    private void aggClass(String cls) {
        if (cls != null)
            this.classes.add(cls);
    }

    /**
     * Access the id of the element.
     *
     * @return id, null if none
     */
    public String getId() {
        return id;
    }

    /**
     * Access the classes of the element.
     *
     * @return id, empty if none
     */
    public List<String> getClasses() {
        return Collections.unmodifiableList(classes);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void accept(IRenderingVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Access the element of which the id and classes apply.
     *
     * @return rendering element
     */
    public IRenderingElement element() {
        return elem;
    }

    /**
     * Markup a rendering element with the specified classes.
     *
     * @param elem rendering element
     * @param classes classes
     * @return the marked element
     */
    public static MarkedElement markup(IRenderingElement elem, String... classes) {
        assert elem != null;
        MarkedElement tagElem = new MarkedElement(elem);
        for (String cls : classes)
            tagElem.aggClass(cls);
        return tagElem;
    }

    private static MarkedElement markupChemObj(IRenderingElement elem, IChemObject chemObj) {
        assert elem != null;
        MarkedElement tagElem = new MarkedElement(elem);
        if (chemObj != null) {
            tagElem.setId(chemObj.getProperty(ID_KEY, String.class));
            tagElem.aggClass(chemObj.getProperty(CLASS_KEY, String.class));
        }
        return tagElem;
    }

    /**
     * Markup a molecule with the class 'mol' and optionally the ids/classes
     * from it's properties.
     *
     * @param elem rendering element
     * @param mol molecule
     * @return the marked element
     */
    public static MarkedElement markupMol(IRenderingElement elem, IAtomContainer mol) {
        assert elem != null;
        MarkedElement tagElem = markupChemObj(elem, mol);
        tagElem.aggClass("mol");
        return tagElem;
    }

    /**
     * Markup a atom with the class 'atom' and optionally the ids/classes
     * from it's properties.
     *
     * @param elem rendering element
     * @param atom atom
     * @return the marked element
     */
    public static MarkedElement markupAtom(IRenderingElement elem, IAtom atom) {
        if (elem == null)
            return null;
        MarkedElement tagElem = markupChemObj(elem, atom);
        tagElem.aggClass("atom");
        return tagElem;
    }

    /**
     * Markup a bond with the class 'bond' and optionally the ids/classes
     * from it's properties.
     *
     * @param elem rendering element
     * @param bond bond
     * @return the marked element
     */
    public static MarkedElement markupBond(IRenderingElement elem, IBond bond) {
        assert elem != null;
        MarkedElement tagElem = markupChemObj(elem, bond);
        tagElem.aggClass("bond");
        return tagElem;
    }
}
