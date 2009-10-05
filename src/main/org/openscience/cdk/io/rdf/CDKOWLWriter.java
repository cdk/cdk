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

import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
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

    public CDKOWLWriter(Writer output) {
        this.output = output;
    }

    public CDKOWLWriter() {
        this.output = null;
    }

    public IResourceFormat getFormat() {
        return CDKOWLFormat.getInstance();
    }

    public void setWriter(Writer out) throws CDKException {
        this.output = out;
    }

    public void setWriter(OutputStream output) throws CDKException {
        this.output = new OutputStreamWriter(output);
    }

    public void close() throws IOException {
        if (output != null) output.close();
    }

    @TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
        Class[] interfaces = classObject.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (IMolecule.class.equals(interfaces[i])) return true;
        }
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
            return false;
    }

    public void write(IChemObject object) throws CDKException {
        if (object instanceof IMolecule) {
            try {
                writeMolecule((IMolecule)object);
            } catch (Exception ex) {
                throw new CDKException(
                    "Error while writing HIN file: " + ex.getMessage(), ex
                );
            }
        } else {
            throw new CDKException(
                "CDKOWLWriter only supports output of IMolecule classes."
            );
        }
    }

    private void writeMolecule(IMolecule mol) {
        Model model = Convertor.molecule2Model(mol);
        model.write(output, "N3");
    }

}
