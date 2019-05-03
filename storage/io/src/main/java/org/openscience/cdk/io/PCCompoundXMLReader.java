/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.PubChemSubstanceXMLFormat;
import org.openscience.cdk.io.pubchemxml.PubChemXMLHelper;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

/**
 * Reads an object from ASN.1 XML formated input for PubChem Compound entries.
 * The following bits are supported: atoms.aid, atoms.element, atoms.2d,
 * atoms.3d, bonds.aid1, bonds.aid2.
 *
 * @cdk.module  io
 * @cdk.githash
 * @cdk.iooptions
 *
 * @cdk.keyword file format, PubChem Compound XML
 */
public class PCCompoundXMLReader extends DefaultChemObjectReader {

    private Reader             input;
    private XMLStreamReader    parser;
    private PubChemXMLHelper   parserHelper;
    private IChemObjectBuilder builder;

    IAtomContainer             molecule = null;

    /**
     * Construct a new reader from a Reader type object.
     *
     * @param input reader from which input is read
     */
    public PCCompoundXMLReader(Reader input) throws Exception {
        setReader(input);
    }

    public PCCompoundXMLReader(InputStream input) throws Exception {
        setReader(input);
    }

    public PCCompoundXMLReader() throws Exception {
        this(new StringReader(""));
    }

    @Override
    public IResourceFormat getFormat() {
        return PubChemSubstanceXMLFormat.getInstance();
    }

    @Override
    public void setReader(Reader input) throws CDKException {
        try {
            XMLInputFactory xmlfact = XMLInputFactory.newFactory();
            xmlfact.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
            parser = xmlfact.createXMLStreamReader(input);
            this.input = input;
        } catch (Exception exception) {
            throw new CDKException("Error while creating reader: " + exception.getMessage(), exception);
        }
    }

    @Override
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        return IAtomContainer.class.isAssignableFrom(classObject);
    }

    @Override
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IAtomContainer) {
            try {
                parserHelper = new PubChemXMLHelper(object.getBuilder());
                builder = object.getBuilder();
                return (T) readMolecule();
            } catch (IOException e) {
                throw new CDKException("An IO Exception occurred while reading the file.", e);
            } catch (CDKException e) {
                throw e;
            } catch (Exception e) {
                throw new CDKException("An error occurred: " + e.getMessage(), e);
            }
        } else {
            throw new CDKException("Only supported is reading of IAtomContainer objects.");
        }
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    // private procedures

    private IAtomContainer readMolecule() throws Exception {
        boolean foundCompound = false;
        while (parser.next() != XMLEvent.END_DOCUMENT) {
            if (parser.getEventType() == XMLEvent.START_ELEMENT) {
                if (parser.getLocalName().equals("PC-Compound")) {
                    foundCompound = true;
                    break;
                }
            }
        }
        if (foundCompound) {
            return parserHelper.parseMolecule(parser, builder);
        }
        return null;
    }

}
