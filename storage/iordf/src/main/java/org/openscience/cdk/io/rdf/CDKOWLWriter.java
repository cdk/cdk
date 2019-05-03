/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io.rdf;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.DefaultChemObjectWriter;
import org.openscience.cdk.io.formats.CDKOWLFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.libio.jena.Convertor;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Serializes the data model into CDK OWL.
 *
 * @cdk.module iordf
 * @cdk.githash
 */
public class CDKOWLWriter extends DefaultChemObjectWriter {

    private Writer output;

    /**
     * Creates a new CDKOWLWriter sending output to the given Writer.
     *
     * @param output {@link Writer} to which is OWL output is routed.
     */
    public CDKOWLWriter(Writer output) {
        this.output = output;
    }

    /**
     * Creates a new CDKOWLWriter with an undefined output.
     */
    public CDKOWLWriter() {
        this.output = null;
    }

    /**
     * Returns the {@link IResourceFormat} for this writer.
     *
     * @return returns a {@link CDKOWLFormat}.
     */
    @Override
    public IResourceFormat getFormat() {
        return CDKOWLFormat.getInstance();
    }

    /** {@inheritDoc} */
    @Override
    public void setWriter(Writer out) throws CDKException {
        this.output = out;
    }

    /** {@inheritDoc} */
    @Override
    public void setWriter(OutputStream output) throws CDKException {
        this.output = new OutputStreamWriter(output);
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws IOException {
        if (output != null) output.close();
    }

    /** {@inheritDoc} */
    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        if (IAtomContainer.class.equals(classObject)) return true;
        Class<?>[] interfaces = classObject.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (IAtomContainer.class.equals(interfaces[i])) return true;
        }
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void write(IChemObject object) throws CDKException {
        if (object instanceof IAtomContainer) {
            try {
                writeMolecule((IAtomContainer) object);
            } catch (Exception ex) {
                throw new CDKException("Error while writing HIN file: " + ex.getMessage(), ex);
            }
        } else {
            throw new CDKException("CDKOWLWriter only supports output of IAtomContainer classes.");
        }
    }

    private void writeMolecule(IAtomContainer mol) {
        Model model = Convertor.molecule2Model(mol);
        model.write(output, "N3");
    }

}
