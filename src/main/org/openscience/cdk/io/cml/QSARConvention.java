/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2002-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.io.cml;

import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.*;
import org.xml.sax.Attributes;

import java.util.StringTokenizer;

/**
 * This is an implementation for the CDK convention.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 * 
 * @author egonw
 */
public class QSARConvention extends CMLCoreModule {

    private String currentDescriptorAlgorithmSpecification;
    private String currentDescriptorImplementationTitel;
    private String currentDescriptorImplementationVendor;
    private String currentDescriptorImplementationIdentifier;
    private String currentDescriptorDataType;
    private String currentDescriptorResult;
    private boolean currentDescriptorDataIsArray;
	
    public QSARConvention(IChemFile chemFile) {
        super(chemFile);
    }

    public QSARConvention(ICMLModule conv) {
        super(conv);
    }
    
    public void startElement(CMLStack xpath, String uri, String local, String raw, Attributes atts) {
//        <property xmlns:qsar="http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/"
//            convention="qsar:DescriptorValue">
//            <metadataList>
//              <metadata dictRef="qsar:specificationReference" content="qsar:weight"/>
//              <metadata dictRef="qsar:implementationTitle" content="org.openscience.cdk.qsar.descriptors.atomic.WeightDescriptor"/>
//              <metadata dictRef="qsar:implementationIdentifier" content="$Id$"/>
//              <metadata dictRef="qsar:implementationVendor" content="The Chemistry Development Kit"/>
//              <metadataList title="qsar:descriptorParameters">
//                <metadata title="elementSymbol" content="*"/>
//              </metadataList>
//            </metadataList>
//            <scalar dataType="xsd:double" dictRef="qsar:weight">72.0</scalar>
//          </property>
        
    	if (xpath.endsWith("molecule", "propertyList", "property")) {
//    		cdo.startObject("MolecularDescriptor");
    		currentDescriptorDataIsArray = false;
    		currentDescriptorAlgorithmSpecification = "";
    		currentDescriptorImplementationTitel = "";
    		currentDescriptorImplementationVendor = "";
    		currentDescriptorImplementationIdentifier = "";
    		currentDescriptorDataType = "";
    		currentDescriptorResult = "";
    	} else if (xpath.endsWith("property", "metadataList", "metadata")) {
    		super.startElement(xpath, uri, local, raw, atts);
    		if (DICTREF.equals("qsar:specificationReference")) {
//    			cdo.setObjectProperty("MolecularDescriptor", "SpecificationReference", atts.getValue("content"));
    			currentDescriptorAlgorithmSpecification = atts.getValue("content");
    		} else if (DICTREF.equals("qsar:implementationTitle")) {
//    			cdo.setObjectProperty("MolecularDescriptor", "ImplementationTitle", atts.getValue("content"));
    			currentDescriptorImplementationTitel = atts.getValue("content");
    		} else if (DICTREF.equals("qsar:implementationIdentifier")) {
//    			cdo.setObjectProperty("MolecularDescriptor", "ImplementationIdentifier", atts.getValue("content"));
    			currentDescriptorImplementationIdentifier = atts.getValue("content");
    		} else if (DICTREF.equals("qsar:implementationVendor")) {
//    			cdo.setObjectProperty("MolecularDescriptor", "ImplementationVendor", atts.getValue("content"));
    			currentDescriptorImplementationVendor = atts.getValue("content");
    		}
    	} else if (xpath.endsWith("propertyList", "property", "scalar")) {
//    		cdo.setObjectProperty("MolecularDescriptor", "DataType", atts.getValue("dataType"));
    		currentDescriptorDataType = atts.getValue("dataType");
    		super.startElement(xpath, uri, local, raw, atts);
        } else {
            super.startElement(xpath, uri, local, raw, atts);
        }
    }

    public void endElement (CMLStack xpath, String uri, String local, String raw) {
    	if (xpath.endsWith("molecule", "propertyList", "property")) {
//    		cdo.endObject("MolecularDescriptor");
    		DescriptorSpecification descriptorSpecification = new DescriptorSpecification(
    			currentDescriptorAlgorithmSpecification,
    	    	currentDescriptorImplementationTitel,
    	    	currentDescriptorImplementationIdentifier,
    	    	currentDescriptorImplementationVendor
    		);
    		currentMolecule.setProperty(descriptorSpecification, 
    	        new DescriptorValue(
    	        	descriptorSpecification,
    	    		new String[0], new Object[0],
    	    		currentDescriptorDataIsArray ?
    	    		    newDescriptorResultArray(currentDescriptorResult) :
    	    		    newDescriptorResult(currentDescriptorResult),
    	    		new String[0])
    		);
    	} else if (xpath.endsWith("property", "scalar")) {
//    		cdo.setObjectProperty("MolecularDescriptor", "DescriptorValue", currentChars);
    		currentDescriptorResult = currentChars;
    	} else {
    		super.endElement(xpath, uri, local, raw);
    	}
    }

    private IDescriptorResult newDescriptorResult(String descriptorValue) {
    	IDescriptorResult result = null;
    	if ("xsd:double".equals(currentDescriptorDataType)) {
    		result = new DoubleResult(Double.parseDouble(descriptorValue));    		
    	} else if ("xsd:integer".equals(currentDescriptorDataType)) {
    		result = new IntegerResult(Integer.parseInt(descriptorValue));
    	} else if ("xsd:boolean".equals(currentDescriptorDataType)) {
    		result = new BooleanResult(new Boolean(descriptorValue).booleanValue());
    	}
		return result;
	}

    private IDescriptorResult newDescriptorResultArray(String descriptorValue) {
    	IDescriptorResult result = null;
    	if ("xsd:double".equals(currentDescriptorDataType)) {
    		result = new DoubleArrayResult();
    		StringTokenizer tokenizer = new StringTokenizer(descriptorValue);
            while (tokenizer.hasMoreElements()) {
                ((DoubleArrayResult)result).add(Double.parseDouble(tokenizer.nextToken()));
            }
    	} else if ("xsd:integer".equals(currentDescriptorDataType)) {
    		result = new IntegerArrayResult();
    		StringTokenizer tokenizer = new StringTokenizer(descriptorValue);
            while (tokenizer.hasMoreElements()) {
                ((IntegerArrayResult)result).add(Integer.parseInt(tokenizer.nextToken()));
            }
    	}
		return result;
	}

}
