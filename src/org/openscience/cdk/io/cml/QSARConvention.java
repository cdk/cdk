/* $Revision: 7636 $ $Author: egonw $ $Date: 2007-01-04 18:46:10 +0100 (Thu, 04 Jan 2007) $
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

import org.openscience.cdk.io.cml.cdopi.IChemicalDocumentObject;
import org.xml.sax.Attributes;

/**
 * This is an implementation for the CDK convention.
 *
 * @cdk.module io
 * 
 * @author egonw
 */
public class QSARConvention extends CMLCoreModule {
	
    public QSARConvention(IChemicalDocumentObject cdo) {
        super(cdo);
    }

    public QSARConvention(ICMLModule conv) {
        super(conv);
    }
    
    public IChemicalDocumentObject returnCDO() {
        return this.cdo;
    }

    public void startDocument() {
        super.startDocument();
    }

    public void endDocument() {
        super.endDocument();
    }

    public void startElement(CMLStack xpath, String uri, String local, String raw, Attributes atts) {
//        <property xmlns:qsar="http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/"
//            convention="qsar:DescriptorValue">
//            <metadataList>
//              <metadata dictRef="qsar:specificationReference" content="qsar:weight"/>
//              <metadata dictRef="qsar:implementationTitle" content="org.openscience.cdk.qsar.descriptors.atomic.WeightDescriptor"/>
//              <metadata dictRef="qsar:implementationIdentifier" content="$Id: cml23TestFramework.xml 6845 2006-09-07 21:21:26Z egonw $"/>
//              <metadata dictRef="qsar:implementationVendor" content="The Chemistry Development Kit"/>
//              <metadataList title="qsar:descriptorParameters">
//                <metadata title="elementSymbol" content="*"/>
//              </metadataList>
//            </metadataList>
//            <scalar dataType="xsd:double" dictRef="qsar:weight">72.0</scalar>
//          </property>
        
    	if (xpath.endsWith("molecule", "propertyList", "property")) {
    		cdo.startObject("MolecularDescriptor");
    	} else if (xpath.endsWith("property", "metadataList", "metadata")) {
    		super.startElement(xpath, uri, local, raw, atts);
    		if (DICTREF.equals("qsar:specificationReference")) {
    			cdo.setObjectProperty("MolecularDescriptor", "SpecificationReference", atts.getValue("content"));
    		} else if (DICTREF.equals("qsar:implementationTitle")) {
    			cdo.setObjectProperty("MolecularDescriptor", "ImplementationTitle", atts.getValue("content"));
    		} else if (DICTREF.equals("qsar:implementationIdentifier")) {
    			cdo.setObjectProperty("MolecularDescriptor", "ImplementationIdentifier", atts.getValue("content"));
    		} else if (DICTREF.equals("qsar:implementationVendor")) {
    			cdo.setObjectProperty("MolecularDescriptor", "ImplementationVendor", atts.getValue("content"));
    		}
    	} else if (xpath.endsWith("propertyList", "property", "scalar")) {
    		cdo.setObjectProperty("MolecularDescriptor", "DataType", atts.getValue("dataType"));
    		super.startElement(xpath, uri, local, raw, atts);
        } else {
            super.startElement(xpath, uri, local, raw, atts);
        }
    }

    public void endElement (CMLStack xpath, String uri, String local, String raw) {
    	if (xpath.endsWith("molecule", "propertyList", "property")) {
    		cdo.endObject("MolecularDescriptor");
    	} else if (xpath.endsWith("property", "scalar")) {
    		System.out.println("touch1");
    		cdo.setObjectProperty("MolecularDescriptor", "DescriptorValue", currentChars);
    	} else {
    		super.endElement(xpath, uri, local, raw);
    	}
    }

}
