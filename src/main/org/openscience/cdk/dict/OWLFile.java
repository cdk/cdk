/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.dict;

import java.io.IOException;
import java.io.Reader;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

import org.openscience.cdk.tools.LoggingTool;

/**
 * Dictionary with entries build from an OWL file.
 *
 * @author       Egon Willighagen <egonw@users.sf.net>
 * @cdk.svnrev   $Revision$
 * @cdk.created  2005-11-18
 * @cdk.keyword  dictionary
 * @cdk.module   dict
 *
 * @cdk.depends  xom-1.0.jar
 */
public class OWLFile extends Dictionary {

    private static String rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static String rdfsNS = "http://www.w3.org/2000/01/rdf-schema#";

    public OWLFile() {
        super();
    }

    public static Dictionary unmarshal(Reader reader) {
        LoggingTool logger = new LoggingTool(OWLFile.class);
        Dictionary dict = new OWLFile();
        try {
            Builder parser = new Builder();
            Document doc = parser.build(reader);
            Element root = doc.getRootElement();
            logger.debug("Found root element: ", root.getQualifiedName());
            
            // Extract ownNS from root element
//            final String ownNS = root.getBaseURI();
            final String ownNS = root.getBaseURI();
            dict.setNS(ownNS);

            logger.debug("Found ontology namespace: ", ownNS);
            
            // process the defined facts
            Elements entries = root.getChildElements();
            logger.info("Found #elements in OWL dict:", entries.size());
            for (int i=0; i<entries.size(); i++) {
                Element entry = entries.get(i);
                if (entry.getNamespaceURI().equals(ownNS)) {
                	Entry dbEntry = unmarshal(entry, ownNS); 
                	dict.addEntry(dbEntry);
                	logger.debug("Added entry: ", dbEntry);
                } else {
                	logger.debug("Found a non-fact: ", entry.getQualifiedName());
                }
            }
        } catch (ParsingException ex) {
            logger.error("Dictionary is not well-formed: ", ex.getMessage());
            logger.debug("Error at line " + ex.getLineNumber(),
                         ", column " + ex.getColumnNumber());
            dict = null;
        } catch (IOException ex) { 
            logger.error("Due to an IOException, the parser could not check:",
                ex.getMessage()
            );
            logger.debug(ex);
            dict = null;
        }
        return dict;
    }

    public static Entry unmarshal(Element entry, String ownNS) {
    	LoggingTool logger = new LoggingTool(OWLFile.class);

        // create a new entry by ID
        Attribute id = entry.getAttribute("ID", rdfNS);
        logger.debug("ID: ", id);
        Entry dbEntry = new Entry(id.getValue());

        // set additional, optional data
        Element label = entry.getFirstChildElement("label", rdfsNS);
        logger.debug("label: ", label);
        if (label != null) dbEntry.setLabel(label.getValue());

        dbEntry.setClassName(entry.getQualifiedName());
        logger.debug("class name: ", dbEntry.getClassName());

        Element definition = entry.getFirstChildElement("definition", ownNS);
        if (definition != null) {
            dbEntry.setDefinition(definition.getValue());
        }
        Element description = entry.getFirstChildElement("description", ownNS);
        if (description != null) {
        	dbEntry.setDescription(description.getValue());
        }

        if (entry.getQualifiedName().equals("Descriptor")) dbEntry.setRawContent(entry);

        return dbEntry;
    }
    
}
