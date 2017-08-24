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
package org.openscience.cdk.io.iterator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.NoSuchElementException;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.PubChemCompoundsXMLFormat;
import org.openscience.cdk.io.pubchemxml.PubChemXMLHelper;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

/**
 * Iterating PubChem PCCompound ASN.1 XML reader.
 *
 * @cdk.module io
 * @cdk.githash
 * @cdk.iooptions
 *
 * @see org.openscience.cdk.io.PCCompoundASNReader
 *
 * @author Egon Willighagen &lt;egonw@users.sf.net&gt;
 * @cdk.created  2008-05-05
 *
 * @cdk.keyword  file format, ASN
 * @cdk.keyword  PubChem
 */
public class IteratingPCCompoundXMLReader extends DefaultIteratingChemObjectReader<IAtomContainer> {

    private Reader             primarySource;
    private XMLStreamReader    parser;
    private PubChemXMLHelper   parserHelper;
    private IChemObjectBuilder builder;
    private final XMLInputFactory xmlfact;

    private boolean            nextAvailableIsKnown;
    private boolean            hasNext;
    private IAtomContainer     nextMolecule;

    /**
     * Constructs a new IteratingPCCompoundXMLReader that can read Molecule from a given Reader and IChemObjectBuilder.
     *
     * @param in      The input stream
     * @param builder The builder
     * @throws XMLStreamException if there is an error in setting up the XML parser
     */
    public IteratingPCCompoundXMLReader(Reader in, IChemObjectBuilder builder) throws IOException,
                                                                                      XMLStreamException {
        this.builder = builder;
        parserHelper = new PubChemXMLHelper(builder);

        // initiate the pull parser
        xmlfact = XMLInputFactory.newFactory();
        xmlfact.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);

        parser = xmlfact.createXMLStreamReader(in);
        primarySource = in;

        nextMolecule = null;
        nextAvailableIsKnown = false;
        hasNext = false;
    }

    /**
     * Constructs a new IteratingPCCompoundXLReader that can read Molecule from a given InputStream and IChemObjectBuilder.
     *
     * @param in The input stream
     * @param builder The builder. In general, use {@link org.openscience.cdk.DefaultChemObjectBuilder}
     * @throws Exception if there is a problem creating an InputStreamReader
     */
    public IteratingPCCompoundXMLReader(InputStream in, IChemObjectBuilder builder) throws Exception {
        this(new InputStreamReader(in), builder);
    }

    @Override
    public IResourceFormat getFormat() {
        return PubChemCompoundsXMLFormat.getInstance();
    }

    @Override
    public boolean hasNext() {
        if (!nextAvailableIsKnown) {
            hasNext = false;

            try {
                if (parser.next() == XMLEvent.END_DOCUMENT) return false;

                while (parser.next() != XMLEvent.END_DOCUMENT) {
                    if (parser.getEventType() == XMLEvent.START_ELEMENT) {
                        //                		System.out.println("start: '" + parser.getName() + "'");
                        if (parser.getLocalName().equals("PC-Compound")) {
                            hasNext = true;
                            break;
                        }
                    }
                }
                if (hasNext) {
                    nextMolecule = parserHelper.parseMolecule(parser, builder);
                }

            } catch (Exception e) {
                e.printStackTrace();
                hasNext = false;
            }

            if (!hasNext) nextMolecule = null;
            nextAvailableIsKnown = true;
        }
        return hasNext;
    }

    @Override
    public IAtomContainer next() {
        if (!nextAvailableIsKnown) {
            hasNext();
        }
        nextAvailableIsKnown = false;
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        return nextMolecule;
    }

    @Override
    public void close() throws IOException {
        primarySource.close();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setReader(Reader reader) throws CDKException {
        primarySource = reader;
        try {
            parser = xmlfact.createXMLStreamReader(reader);
        } catch (XMLStreamException e) {
            throw new CDKException("Error while opening the input:" + e.getMessage(), e);
        }
        nextMolecule = null;
        nextAvailableIsKnown = false;
        hasNext = false;
    }

    @Override
    public void setReader(InputStream reader) throws CDKException {
        setReader(new InputStreamReader(reader));
    }
}
