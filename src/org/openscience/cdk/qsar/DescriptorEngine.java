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

import org.openscience.cdk.Molecule;
import org.openscience.cdk.dict.DictionaryDatabase;
import org.openscience.cdk.dict.Entry;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.tools.LoggingTool;


/**
 * Engine that calculates the values for a set of Descriptors and add this
 * to a Molecule.
 *
 * The set of descriptors is created from the Java sources of
 * the CDK QSAR source code, using <code>@cdk.set qsar-descriptors</code> tags
 * in the JavaDoc. In addition it is possible to specify which types of descriptors
 * (constitutional, molecular, geometrical, topological or electronic) should
 * be calculated.
 *
 * @cdk.created 2004-12-02
 * @cdk.module  qsar
 */
public class DescriptorEngine {

    private final static String QSAR_DESCRIPTOR_LIST = "qsar-descriptors.set";

    private List descriptors;
    private List speclist;
    private LoggingTool logger;

    /**
     * Constructor that generates a list of descriptors to calculate.
     *
     * All available descriptors are included in the list of descriptors to 
     * calculate
     * 
     */
    public DescriptorEngine() {
        logger = new LoggingTool(this);
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
        speclist = new Vector();
        for (int i = 0; i < descriptors.size(); i++) {
            Descriptor descriptor = (Descriptor)descriptors.get(i);
            speclist.add(descriptor.getSpecification());
        }
    }

    /**
     * Constructor that generates a list of descriptors to calculate.
     *
     * This constructor allows the user to specify which types of 
     * descriptors should be calculated. The possible types are
     * <ul>
     * <li>constitutional
     * <li>molecular
     * <li>electronic
     * <li>topological
     * <li>geometrical
     * </ul>
     * More than one type may be specified and descriptors matching any of the specified
     * types will be considered for calculation. If the any of the types specified do not
     * belong to the above list all descriptor types will be considered.
     * 
     * @param descriptorClasses A String array containing one or more of the above elements
     */
    public DescriptorEngine(String[] descriptorClasses) {

        List tmplist = new Vector(); // stores the initial list of *all* descriptors
        logger = new LoggingTool(this);
        
        // some validation. Maybe add some constants to CDKConstants?
        String[] validTypes = {"constitutional","molecular","topological","electronic","geometrical"};
        if (descriptorClasses == null  || descriptorClasses.length == 0) {
            descriptorClasses = validTypes;
        } else {
            for (int i = 0; i < descriptorClasses.length; i++) {
                int invalid = 0;
                for (int j = 0; j < validTypes.length; j++) {
                    if (!descriptorClasses[i].equals(validTypes[j])) invalid++;
                }
                if (invalid == validTypes.length) descriptorClasses = validTypes;
            }
        }
        
        DictionaryDatabase dictDB = new DictionaryDatabase();
        Entry[] dictEntries = dictDB.getDictionaryEntry("qsar-descriptors");


        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                        this.getClass().getClassLoader().getResourceAsStream(QSAR_DESCRIPTOR_LIST)
                        ));
            while (reader.ready()) {
                String descriptorName = reader.readLine();
                try {
                    Descriptor descriptor = (Descriptor)this.getClass().getClassLoader().loadClass(descriptorName).newInstance();
                    tmplist.add(descriptor);
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

        // now see which descriptors match the types specified by the user
        descriptors = new Vector();
        speclist = new Vector();

        // loop over all descriptors loaded
        for (int i = 0; i < tmplist.size(); i++) {

            // get the specification for this descriptor
            DescriptorSpecification spec = ((Descriptor)tmplist.get(i)).getSpecification();

            // get the ref into the XML dict
            String specref = spec.getSpecificationReference();

            // need to get rid of the base URI
            String[] tmp = specref.split(":");
            specref = tmp[2];
            
            // get the entry from the QSAR dict coresponding to this ref
            for (int j = 0; j < dictEntries.length; j++) {
                if (dictEntries[j].getID().equals(specref.toLowerCase())) {
                    // ok, got the proper entry. Now get the classification metadata
                    Vector clsmetadata = dictEntries[j].getDescriptorMetadata();
                    
                    // Now check to see if any of the metadata equals what the
                    // user specified. At this point, the types of descriptors
                    // the user specified should have been validated.
                    for (int k = 0; k < descriptorClasses.length; k++) {
                        boolean added = false;
                        for (Iterator it = clsmetadata.iterator(); it.hasNext();) {
                            String cls = (String)it.next();
                            if (cls.indexOf(descriptorClasses[k]) != -1) {
                                logger.info("Will use "+dictEntries[j].getID());
                                descriptors.add(tmplist.get(i));
                                speclist.add(spec);
                                added = true;
                                break;
                            }
                        }
                        if (added) break;
                    }
                }
            }
        }
        logger.info("Loaded "+descriptors.size()+" descriptors");
    }

    /**
     * Calculates all available (or only those specified) descriptors for a molecule.
     * 
     * The results for a given descriptor as well as associated parameters and
     * specifications are used to create a <code>DescriptorValue</code>
     * object which is then added to the molecule as a property keyed
     * on the <code>DescriptorSpecification</code> object for that descriptor
     *
     * @param  molecule  The molecule for which we want to calculate descriptors          
     */
    public void process(Molecule molecule) {
        for (int i = 0; i < descriptors.size(); i++) {    
            Descriptor descriptor = (Descriptor)descriptors.get(i);
            try {
                DescriptorValue value = descriptor.calculate(molecule);
                molecule.setProperty((DescriptorSpecification)speclist.get(i), value);
            } catch (CDKException exception) {
                logger.error("Could not calculate descriptor value for: ", descriptor.getClass().getName());
                logger.debug(exception);
            }
        }
    }
        
   /**
     * Returns the DescriptorSpecification object for all available descriptors.
     *
     *@return An array of <code>DescriptorSpecification</code> objects. These are the keys
     *        with which the <code>DescriptorValue</code> objects can be obtained from a 
     *        molecules property list
     */
    public List getDescriptorSpecifications() {
        return(speclist);
    }

}

