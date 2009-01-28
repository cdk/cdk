/* $RCSfile$
 * $Author: rajarshi $
 * $Date: 2007-10-22 02:38:43 +0200 (Mon, 22 Oct 2007) $
 * $Revision: 9172 $
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
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

import org.openscience.cdk.tools.LoggingTool;

/**
 * Dictionary with entries build from an OWL React.
 *
 * @author       Miguel Rojas <miguelrojasch@users.sf.net>
 * @cdk.created  2008-01-01
 * @cdk.keyword  dictionary
 * @cdk.module   dict
 *
 * @cdk.depends  xom-1.0.jar
 */
public class OWLReact extends Dictionary {

    private static String rdfNS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static String rdfsNS = "http://www.w3.org/2000/01/rdf-schema#";

    /**
     * Constructor of the OWLReact object.
     */
    public OWLReact() {
        super();
    }

    /**
     * 
     * @param reader The Reader
     * @return       The Dictionary
     */
    public static Dictionary unmarshal(Reader reader) {
        LoggingTool logger = new LoggingTool(OWLReact.class);
        Dictionary dict = new OWLReact();
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
                	EntryReact dbEntry = unmarshal(entry, ownNS); 
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

    public static EntryReact unmarshal(Element entry, String ownNS) {
    	LoggingTool logger = new LoggingTool(OWLReact.class);

        // create a new entry by ID
        Attribute id = entry.getAttribute("ID", rdfNS);
        logger.debug("ID: ", id);
        EntryReact dbEntry = new EntryReact(id.getValue());

        // set additional, optional data
        Element label = entry.getFirstChildElement("label", rdfsNS);
        logger.debug("label: ", label);
        if (label != null) dbEntry.setLabel(label.getValue());

        dbEntry.setClassName(entry.getQualifiedName());
        logger.debug("class name: ", dbEntry.getClassName());

        Element definition = entry.getFirstChildElement("definition", ownNS);
        if (definition != null) {
            dbEntry.setDefinition(definition.getValue());
            logger.debug("definition name: ", definition.getValue());
        }
        Element description = entry.getFirstChildElement("description", ownNS);
        if (description != null) {
        	dbEntry.setDescription(description.getValue());
            logger.debug("description name: ", description.getValue());
        }
        Elements representations = entry.getChildElements("representation", ownNS);
        if (representations != null)
	        for(int i = 0 ; i< representations.size(); i++){
//	        	String idRepr = representations.get(i).getAttributeValue("id");
	        	String contentRepr = representations.get(i).getAttributeValue("content");
		        dbEntry.setRepresentation(contentRepr);
	        }
        
        Elements params = entry.getChildElements("parameters", ownNS);
        if (params != null)
	        for(int i = 0 ; i< params.size(); i++){
	        	String typeParam = params.get(i).getAttributeValue("dataType");
	        	typeParam = typeParam.substring(typeParam.indexOf(":")+1, typeParam.length());
	        	String nameParam = params.get(i).getAttributeValue("resource");
	        	String value = params.get(i).getValue();
	        	dbEntry.setParameters(nameParam,typeParam,value);
	        }
        
        Elements paramsList = entry.getChildElements("parameterList", ownNS);
        if (paramsList != null)
	        for(int i = 0 ; i< paramsList.size(); i++){
	        	Elements params2 = paramsList.get(i).getChildElements("parameter2", ownNS);
	        	if (params2 != null)
	    	        for(int j = 0 ; j< params2.size(); j++){
	    	        	String paramClass = params2.get(i).getAttribute(0).getValue();
	    	        	paramClass = paramClass.substring(paramClass.indexOf("#")+1);
	    	            logger.debug("parameter class: ", paramClass);
	    	        	
	    	        	String needsToSet = "";
	    	        	String value = "";
	    	        	String dataType = "";
	    	        	Elements paramSubt1 = params2.get(i).getChildElements("isSetParameter", ownNS);
	    	        	if (paramSubt1 != null)
	    	    	        for(int k = 0 ; k< 1; k++)
	    	    	        	needsToSet = paramSubt1.get(k).getValue();
	    	        	Elements paramSubt2 = params2.get(i).getChildElements("value", ownNS);
	    	        	if (paramSubt1 != null)
	    	    	        for(int k = 0 ; k< 1; k++){
	    	    	        	value = paramSubt2.get(k).getValue();
	    	    	        	dataType = paramSubt2.get(k).getAttributeValue("dataType");
	    	    	        	dataType = dataType.substring(dataType.indexOf(":")+1, dataType.length());
	    	    	        }
	    	        	List<String> pp = new ArrayList<String>();
	    	        	pp.add(paramClass);
	    	        	pp.add(needsToSet);
	    	        	pp.add(dataType);
	    	        	pp.add(value);
	    	        	dbEntry.addParameter(pp);
	    	        }
	        }
        
        Elements mechanismDependence = entry.getChildElements("mechanismDependence", ownNS);
        String mechanism = "";
        if (mechanismDependence != null)
	        for(int i = 0 ; i< mechanismDependence.size(); i++){
	        	mechanism = mechanismDependence.get(i).getAttribute(0).getValue();
	        	mechanism = mechanism.substring(mechanism.indexOf("#")+1);
	            logger.debug("mechanism name: ", mechanism);
	        }

        dbEntry.setMechanism(mechanism);
//        System.out.println("mechan: "+mechan);
        
        Elements exampleReact = entry.getChildElements("example-Reactions", ownNS);
        if (exampleReact != null)
	        for(int i = 0 ; i< exampleReact.size(); i++){
	        	Elements reaction = exampleReact.get(i).getChildElements("reaction", ownNS);
	        	if (reaction != null)
	    	        for(int j = 0 ; j< reaction.size(); j++){
	    	            dbEntry.addExampleReaction(reaction.get(0).toXML());
	    	        }
	        }
        return dbEntry;
    }
    
}
