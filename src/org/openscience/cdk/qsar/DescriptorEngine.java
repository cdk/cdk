/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 * to a Molecule.
 *
 * @cdk.created 2004-12-02
 * @cdk.module  qsar
 */
public class DescriptorEngine {

    private List descriptors;
    private LoggingTool logger;

	public DescriptorEngine() {
        logger = new LoggingTool(true);
        descriptors = new Vector();
        
        /* the next is stupid, we don't want to hard code this, but it seems
           we don't have a better alternative just yet */
        String[] descriptorClassNames = {
            "org.openscience.cdk.qsar.AtomCountDescriptor",
            "org.openscience.cdk.qsar.BondCountDescriptor",
        };
        for (int i=0; i<descriptorClassNames.length; i++) {
            // load them one by one
            try {
                Descriptor descriptor = (Descriptor)this.getClass().getClassLoader().
                    loadClass(descriptorClassNames[i]).newInstance();
                descriptors.add(descriptor);
            } catch (ClassNotFoundException exception) {
                logger.error("Could not find this Descriptor: ", descriptorClassNames[i]);
                logger.debug(exception);
            } catch (Exception exception) {
                logger.error("Could not load this Descriptor: ", descriptorClassNames[i]);
                logger.debug(exception);
            }
        }
    }

	public void process(Molecule molecule) {
        Iterator iterator = descriptors.iterator();
        while (iterator.hasNext()) {
            Descriptor descriptor = (Descriptor)iterator.next();
            try {
                DescriptorValue value = new DescriptorValue(
                    descriptor.getSpecification(),
                    descriptor.getParameters(),
                    descriptor.calculate(molecule)
                );
                molecule.setProperty(value.getSpecification(), value);
            } catch (CDKException exception) {
                logger.error("Could not calculate descriptor value for: ",
                    descriptor.getClass().getName());
            }
        }
	}

}

