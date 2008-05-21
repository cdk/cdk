/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.libio.cml;

import java.util.Iterator;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Element;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.BooleanResult;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLMetadataList;
import org.xmlcml.cml.element.CMLProperty;
import org.xmlcml.cml.element.CMLPropertyList;
import org.xmlcml.cml.element.CMLScalar;

/**
 * Customizer for the libio-cml Convertor to be able to export details for
 * QSAR descriptors calculated for Molecules.
 *
 * @author        egonw
 * @cdk.created   2005-05-04
 * @cdk.module    qsarcml
 * @cdk.svnrev  $Revision$
 * @cdk.set       libio-cml-customizers
 * @cdk.require   java1.5+
 */
public class QSARCustomizer implements ICMLCustomizer {

    private final static String QSAR_NAMESPACE = "qsar";
    private final static String QSAR_URI = "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/";

	public void customize(IBond bond, Object nodeToAdd) throws Exception {
    	customizeIChemObject(bond, nodeToAdd);
	}
	
    public void customize(IAtom atom, Object nodeToAdd) throws Exception {
    	customizeIChemObject(atom, nodeToAdd);
    }
    
    public void customize(IAtomContainer molecule, Object nodeToAdd) throws Exception {
    	customizeIChemObject(molecule, nodeToAdd);
    }

    private Element createScalar(IDescriptorResult value) {
        Element scalar = null;
        if (value instanceof DoubleResult) {
            scalar = new CMLScalar();
            scalar.addAttribute(new Attribute("dataType", "xsd:double"));
            scalar.appendChild("" + ((DoubleResult)value).doubleValue());
        } else if (value instanceof IntegerResult) {
            scalar = new CMLScalar();
            scalar.addAttribute(new Attribute("dataType", "xsd:int"));
            scalar.appendChild("" + ((IntegerResult)value).intValue());
        } else if (value instanceof BooleanResult) {
            scalar = new CMLScalar();
            scalar.addAttribute(new Attribute("dataType", "xsd:boolean"));
            scalar.appendChild("" + ((BooleanResult)value).booleanValue());
        } else if (value instanceof IntegerArrayResult) {
            IntegerArrayResult result = (IntegerArrayResult)value;
            scalar = new CMLArray();
            scalar.addAttribute(new Attribute("dataType", "xsd:int"));
            scalar.addAttribute(new Attribute("size", "" + result.length()));
            StringBuffer buffer = new StringBuffer();
            for (int i=0; i<result.length(); i++) {
                buffer.append(result.get(i) + " ");
            }
            scalar.appendChild(buffer.toString());
        } else if (value instanceof DoubleArrayResult) {
            DoubleArrayResult result = (DoubleArrayResult)value;
            scalar = new CMLArray();
            scalar.addAttribute(new Attribute("dataType", "xsd:double"));
            scalar.addAttribute(new Attribute("size", "" + result.length()));
            StringBuffer buffer = new StringBuffer();
            for (int i=0; i<result.length(); i++) {
                buffer.append(result.get(i) + " ");
            }
            scalar.appendChild(buffer.toString());
        } else {
            // logger.error("Could not convert this object to a scalar element: ", value);
            scalar.appendChild(value.toString());
        }
        return scalar;
     }
    
    private void customizeIChemObject(IChemObject object, Object nodeToAdd) throws Exception {
    	if (!(nodeToAdd instanceof Element))
    		throw new CDKException("NodeToAdd must be of type nu.xom.Element!");
    	
    	Element element = (Element)nodeToAdd;
    	Map<Object,Object> props = object.getProperties();
        Iterator<Object> keys = props.keySet().iterator();
        Element propList = null;
        while (keys.hasNext()) {
            Object key = keys.next();
            if (key instanceof DescriptorSpecification) {
                DescriptorSpecification specs = (DescriptorSpecification)key;
                DescriptorValue value = (DescriptorValue)props.get(key);
                IDescriptorResult result = value.getValue();
                if (propList == null) {
                    propList = new CMLPropertyList();
                }
                Element property = new CMLProperty();
                // setup up the metadata list
                Element metadataList = new CMLMetadataList();
                metadataList.addNamespaceDeclaration(QSAR_NAMESPACE, QSAR_URI);
                property.addAttribute(new Attribute("convention", QSAR_NAMESPACE + ":" + "DescriptorValue"));
                String specsRef = specs.getSpecificationReference();
                if (specsRef.startsWith(QSAR_URI)) {
                    property.addNamespaceDeclaration(QSAR_NAMESPACE, QSAR_URI);
                }
                CMLMetadata metaData = new CMLMetadata();
                metaData.addAttribute(new Attribute("dictRef", QSAR_NAMESPACE + ":" + "specificationReference"));
                metaData.addAttribute(new Attribute("content", specsRef));
                metadataList.appendChild(metaData);
                metaData = new CMLMetadata();
                metaData.addAttribute(new Attribute("dictRef", QSAR_NAMESPACE + ":" + "implementationTitle"));
                metaData.addAttribute(new Attribute("content", specs.getImplementationTitle()));
                metadataList.appendChild(metaData);
                metaData = new CMLMetadata();
                metaData.addAttribute(new Attribute("dictRef", QSAR_NAMESPACE + ":" + "implementationIdentifier"));
                metaData.addAttribute(new Attribute("content", specs.getImplementationIdentifier()));
                metadataList.appendChild(metaData);
                metaData = new CMLMetadata();
                metaData.addAttribute(new Attribute("dictRef", QSAR_NAMESPACE + ":" + "implementationVendor"));
                metaData.addAttribute(new Attribute("content", specs.getImplementationVendor()));
                metadataList.appendChild(metaData);
                // add parameter setting to the metadata list
                Object[] params = value.getParameters();
//                logger.debug("Value: " + value.getSpecification().getImplementationIdentifier());
                if (params != null && params.length > 0) {
                    String[] paramNames = value.getParameterNames();
                    Element paramSettings = new CMLMetadataList();
                    paramSettings.addAttribute(new Attribute("title", QSAR_NAMESPACE + ":" + "descriptorParameters"));
                    for (int i=0; i<params.length; i++) {
                        Element paramSetting = new CMLMetadata();
                        String paramName = paramNames[i];
                        Object paramVal = params[i];
                        if (paramName == null) {
                            // logger.error("Parameter name was null! Cannot output to CML.");
                        } else if (paramVal == null) {
                            // logger.error("Parameter setting was null! Cannot output to CML. Problem param: " + paramName);
                        } else {
                            paramSetting.addAttribute(new Attribute("title", paramNames[i]));
                            paramSetting.addAttribute(new Attribute("content", params[i].toString()));
                            paramSettings.appendChild(paramSetting);
                        }
                    }
                    metadataList.appendChild(paramSettings);
                }
                property.appendChild(metadataList);
                Element scalar = this.createScalar(result);
                scalar.addAttribute(new Attribute("dictRef", specsRef));
                // add the actual descriptor value
                property.appendChild(scalar);
                propList.appendChild(property);
            } // else: disregard all other properties
        }
        if (propList != null) {
            element.appendChild(propList);
        }
    }
    
	
}

