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

import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.PubChemSubstancesXMLFormat;
import org.openscience.cdk.io.pubchemxml.PubChemXMLHelper;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

/**
 * Iterating PubChem PC-Substances ASN.1 XML reader.
 *
 * @cdk.module   io
 * @cdk.githash
 * @cdk.iooptions
 *
 * @author       Egon Willighagen <egonw@users.sf.net>
 * @cdk.created  2008-05-05
 *
 * @cdk.keyword  file format, ASN
 * @cdk.keyword  PubChem
 */
public class IteratingPCSubstancesXMLReader extends DefaultIteratingChemObjectReader<IChemModel> {

    private Reader           primarySource;
    private XMLStreamReader  parser;
    private PubChemXMLHelper parserHelper;
    private final XMLInputFactory xmlfact;


    private boolean          nextAvailableIsKnown;
    private boolean          hasNext;
    private IChemModel       nextSubstance;

    /**
     * Constructs a new IteratingPCSubstancesXMLReader that can read Molecule from a given Reader and IChemObjectBuilder.
     *
     * @param in      The input stream
     * @param builder The builder
     * @throws java.io.IOException if there is error in getting the {@link IsotopeFactory}
     * @throws XMLStreamException an error in reading XML
     */
    public IteratingPCSubstancesXMLReader(Reader in, IChemObjectBuilder builder) throws IOException,
                                                                                        XMLStreamException {
        parserHelper = new PubChemXMLHelper(builder);
        xmlfact = XMLInputFactory.newFactory();

        // initiate the pull parser
        xmlfact.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
        parser = xmlfact.createXMLStreamReader(in);
        this.primarySource = in;

        nextSubstance = null;
        nextAvailableIsKnown = false;
        hasNext = false;
    }

    /**
     * Constructs a new IteratingPCSubstancesXMLReader that can read Molecule from a given InputStream and IChemObjectBuilder.
     *
     * @param in The input stream
     * @param builder The builder. In general, use {@link org.openscience.cdk.DefaultChemObjectBuilder}
     * @throws Exception if there is a problem creating an InputStreamReader
     */
    public IteratingPCSubstancesXMLReader(InputStream in, IChemObjectBuilder builder) throws Exception {
        this(new InputStreamReader(in), builder);
    }

    @Override
    public IResourceFormat getFormat() {
        return PubChemSubstancesXMLFormat.getInstance();
    }

    @Override
    public boolean hasNext() {
        if (!nextAvailableIsKnown) {
            hasNext = false;

            try {
                if (parser.next() == XMLEvent.END_DOCUMENT) return false;

                while (parser.next() != XMLEvent.END_DOCUMENT) {
                    if (parser.getEventType() == XMLEvent.START_ELEMENT) {
                        if (PubChemXMLHelper.EL_PCSUBSTANCE.equals(parser.getLocalName())) {
                            hasNext = true;
                            break;
                        }
                    }
                }
                if (hasNext) {
                    nextSubstance = parserHelper.parseSubstance(parser);
                }

            } catch (Exception e) {
                if (mode == Mode.STRICT) {
                    throw new RuntimeException("Error while parsing the XML: " + e.getMessage(), e);
                }
                hasNext = false;
            }

            if (!hasNext) nextSubstance = null;
            nextAvailableIsKnown = true;
        }
        return hasNext;
    }

    @Override
    public IChemModel next() {
        if (!nextAvailableIsKnown) {
            hasNext();
        }
        nextAvailableIsKnown = false;
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        return nextSubstance;
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
        nextSubstance = null;
        nextAvailableIsKnown = false;
        hasNext = false;
    }

    @Override
    public void setReader(InputStream reader) throws CDKException {
        setReader(new InputStreamReader(reader));
    }
}
