/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.qsar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.tools.LoggingTool;


/**
 * Engine that calculates the values for a set of Descriptors and add this
 * to a Molecule. The set of descriptors is created from the Java sources of
 * the CDK QSAR source code, using <code>@cdk.set qsar-descriptors</code> tags
 * in the JavaDoc.
 *
 * @cdk.created 2004-12-02
 * @cdk.module  qsar
 */
public class DescriptorEngine {

    private final static String QSAR_DESCRIPTOR_LIST = "qsar-descriptors.set";

    private List descriptors;
    private DescriptorSpecification[] specs;
    private LoggingTool logger;

    public DescriptorEngine() {
        logger = new LoggingTool(true);
        descriptors = new Vector();

        /* the next is stupid, we don't want to hard code this, but it seems
           we don't have a better alternative just yet */
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                        this.getClass().getClassLoader().getResourceAsStream(QSAR_DESCRIPTOR_LIST)
                        ));
            while (reader.ready()) {
                // load them one by one
                String descriptorName = reader.readLine();
                try {
                    Descriptor descriptor = (Descriptor)this.getClass().getClassLoader().loadClass(descriptorName).newInstance();
                    descriptors.add(descriptor);
                    logger.info("Loaded descriptor: ", descriptorName);
                } catch (ClassNotFoundException exception) {
                    logger.error("Could not find this Descriptor: ", descriptorName);
                    logger.debug(exception);
                } catch (Exception exception) {
                    logger.error("Could not load this Descriptor: ", descriptorName);
                    logger.debug(exception);
                }
            }
        } catch (Exception exception) {
            logger.error("Could not load this descriptor list: ", QSAR_DESCRIPTOR_LIST);
            logger.debug(exception);
        }

        // set the DescriptorSpecification objects. We need to make a list 
        // beforehand since these are used as key into the molecules 
        // property list. As a result when accessing the property list
        // the keys should be identical to those used when setting the properties.
        specs = new DescriptorSpecification[descriptors.size()];
        for (int i = 0; i < descriptors.size(); i++) {
            Descriptor descriptor = (Descriptor)descriptors.get(i);
            specs[i] = descriptor.getSpecification();
        }
    }

    /**
     *  Calculates all available descriptors for a molecule
     *
     *  The results for a given descriptor as well as associated parameters and
     *  specifications are used to create a <code>DescriptorValue</code>
     *  object which is then added to the molecule as a property keyed
     *  on the <code>DescriptorSpecification</code> object for that descriptor
     *
     *@param  Molecule  The molecule for which we want to calculate descriptors          
     */
    public void process(Molecule molecule) {
        for (int i = 0; i < descriptors.size(); i++) {    
            Descriptor descriptor = (Descriptor)descriptors.get(i);
            try {
                DescriptorValue value = new DescriptorValue(
                        specs[i],
                        descriptor.getParameters(),
                        descriptor.calculate(molecule)
                        );
                molecule.setProperty(specs[i], value);
            } catch (CDKException exception) {
                logger.error("Could not calculate descriptor value for: ",
                        descriptor.getClass().getName());
            }
        }
    }

   /**
     *  Returns the DescriptorSpecification object for all available descriptors
     *
     *@return An array of <code>DescriptorSpecification</code> objects. These are the keys
     *        with which the <code>DescriptorValue</code> objects can be obtained from a 
     *        molecules property list
     */
    public DescriptorSpecification[] getDescriptorSpecifications() {
        return(specs);
    }

}

