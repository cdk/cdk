/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.libio.cml;

import java.util.Enumeration;
import java.util.Hashtable;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.BooleanResult;
import org.openscience.cdk.qsar.result.DescriptorResult;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.w3c.dom.Element;

/**
 * Customizer for the libio-cml Convertor to be able to export details for
 * QSAR descriptors calculated for Molecules.
 *
 * @author        egonw
 * @cdk.created   2005-05-04
 * @cdk.module    qsar-cml
 * @cdk.set       libio-cml-customizers
 */
public class QSARCustomizer implements Customizer {

    private final static String QSARDICT_NAMESPACE = "qsardict";
    private final static String QSARDICT_URI = "http://qsar.sourceforge.net/dicts/qsar-descriptors";
    private final static String QSARMETA_NAMESPACE = "qsarmeta";
    private final static String QSARMETA_URI = "http://qsar.sourceforge.net/dicts/qsar-descriptors-metadata";

    private String namespace = "http://www.xml-cml.org/schema/cml2/core";
    
    public void customize(Object convertor, Atom atom, Element nodeToAdd) throws Exception {
        // nothing to do at this moment
    }
    
    public void customize(Object object, Molecule molecule, Element nodeToAdd) throws Exception {
        if (!(object instanceof Convertor)) {
            throw new CDKException("The convertor is not instanceof Convertor!");
        }
        Convertor convertor = (Convertor)object;

        Hashtable props = molecule.getProperties();
        Enumeration keys = props.keys();
        Element propList = null;
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (key instanceof DescriptorSpecification) {
                DescriptorSpecification specs = (DescriptorSpecification)key;
                DescriptorValue value = (DescriptorValue)props.get(key);
                DescriptorResult result = value.getValue();
                if (propList == null) {
                    propList = convertor.createElement("propertyList");
                }
                Element property = convertor.createElement("property");
                // setup up the metadata list
                Element metadataList = convertor.createElement("metadataList");
                metadataList.setAttribute("xmlns:" + QSARMETA_NAMESPACE, QSARMETA_URI);
                String specsRef = specs.getSpecificationReference();
                if (specsRef.startsWith(QSARDICT_URI)) {
                    specsRef = QSARDICT_NAMESPACE + ":" + specsRef.substring(QSARDICT_URI.length()+1);
                    property.setAttribute("xmlns:" + QSARDICT_NAMESPACE, QSARDICT_URI);
                }
                Element metaData = convertor.createElement("metadata");
                metaData.setAttribute("dictRef", QSARMETA_NAMESPACE + ":" + "implementationTitle");
                metaData.setAttribute("content", specs.getImplementationTitle());
                metadataList.appendChild(metaData);
                metaData = convertor.createElement("metadata");
                metaData.setAttribute("dictRef", QSARMETA_NAMESPACE + ":" + "implementationIdentifier");
                metaData.setAttribute("content", specs.getImplementationIdentifier());
                metadataList.appendChild(metaData);
                metaData = convertor.createElement("metadata");
                metaData.setAttribute("dictRef", QSARMETA_NAMESPACE + ":" + "implementationVendor");
                metaData.setAttribute("content", specs.getImplementationVendor());
                metadataList.appendChild(metaData);
                // add parameter setting to the metadata list
                Object[] params = value.getParameters();
                if (params != null && params.length > 0) {
                    String[] paramNames = value.getParameterNames();
                    Element paramSettings = convertor.createElement("metadataList");
                    paramSettings.setAttribute("title", QSARMETA_NAMESPACE + ":" + "descriptorParameters");
                    for (int i=0; i<params.length; i++) {
                        Element paramSetting = convertor.createElement("metadata");
                        String paramName = paramNames[i];
                        Object paramVal = params[i];
                        if (paramName == null) {
                            // logger.error("Parameter name was null! Cannot output to CML.");
                        } else if (paramVal == null) {
                            // logger.error("Parameter setting was null! Cannot output to CML. Problem param: " + paramName);
                        } else {
                            paramSetting.setAttribute("title", paramNames[i]);
                            paramSetting.setAttribute("content", params[i].toString());
                            paramSettings.appendChild(paramSetting);
                        }
                    }
                    metadataList.appendChild(paramSettings);
                }
                property.appendChild(metadataList);
                Element scalar = this.createScalar(convertor, result);
                scalar.setAttribute("dictRef", specsRef);
                // add the actual descriptor value
                property.appendChild(scalar);
                propList.appendChild(property);
            } // else: disregard all other properties
        }
        if (propList != null) {
            nodeToAdd.appendChild(propList);
        }
    }

    private Element createScalar(Convertor convertor, DescriptorResult value) {
        Element scalar = null;
        if (value instanceof DoubleResult) {
            scalar = convertor.createElement("scalar");
            scalar.setAttribute("dataType", "xsd:double");
            scalar.appendChild(convertor.createTextNode("" + ((DoubleResult)value).doubleValue()));
        } else if (value instanceof IntegerResult) {
            scalar = convertor.createElement("scalar");
            scalar.setAttribute("dataType", "xsd:int");
            scalar.appendChild(convertor.createTextNode("" + ((IntegerResult)value).intValue()));
        } else if (value instanceof BooleanResult) {
            scalar = convertor.createElement("scalar");
            scalar.setAttribute("dataType", "xsd:boolean");
            scalar.appendChild(convertor.createTextNode("" + ((BooleanResult)value).booleanValue()));
        } else if (value instanceof IntegerArrayResult) {
            IntegerArrayResult result = (IntegerArrayResult)value;
            scalar = convertor.createElement("array");
            scalar.setAttribute("dataType", "xsd:int");
            scalar.setAttribute("size", "" + result.size());
            StringBuffer buffer = new StringBuffer();
            for (int i=0; i<result.size(); i++) {
                buffer.append(result.get(i) + " ");
            }
            scalar.appendChild(convertor.createTextNode(buffer.toString()));
        } else if (value instanceof DoubleArrayResult) {
            DoubleArrayResult result = (DoubleArrayResult)value;
            scalar = convertor.createElement("array");
            scalar.setAttribute("dataType", "xsd:double");
            scalar.setAttribute("size", "" + result.size());
            StringBuffer buffer = new StringBuffer();
            for (int i=0; i<result.size(); i++) {
                buffer.append(result.get(i) + " ");
            }
            scalar.appendChild(convertor.createTextNode(buffer.toString()));
        } else {
            // logger.error("Could not convert this object to a scalar element: ", value);
            scalar.appendChild(convertor.createTextNode(value.toString()));
        }
        return scalar;
     }
    
}

