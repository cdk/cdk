/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.qsar;

import java.util.Map;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;

/**
 * Class that is used to distribute descriptor specifications.
 *
 * @cdk.module qsar
 */
public class DescriptorSpecification {

    private String specification_reference;
    private String implementation_title;
    private String implementation_identifier;
    private String implementation_vendor;
    
    /**
     * Container for specifying the type of descriptor.
     *
     * @param specification_reference Reference to a formal definition in a
     *          dictionary (e.g. in STMML format) of the descriptor, preferably 
     *          refering to the original article. The format of the content is
     *          expected to be &lt;dictionaryNameSpace>:&lt;entryID>.
     * @param implementation_title Title for the implementation of the descriptor
     *          for which the algorithm is given by the specification.
     * @param implementation_identifier Unique identifier for the actual
     *          implementation, preferably including the exact version number of
     *          the source code. E.g. $Id$ can be used when the source code is
     *          in a CVS repository.
     * @param implementation_vendor Name of the organisation/person/program/whatever 
     *          who wrote/packaged the implementation.
     */
    public DescriptorSpecification(
        String specification_reference,
        String implementation_title,
        String implementation_identifier,
        String implementation_vendor) {
        this.specification_reference = specification_reference;
        this.implementation_title = implementation_title;
        this.implementation_identifier = implementation_identifier;
        this.implementation_vendor = implementation_vendor;
    }
    
    public String getSpecificationReference() {
        return this.specification_reference;
    };
    
    public String getImplementationTitle() {
        return this.implementation_title;
    };
    
    public String getImplementationIdentifier() {
        return this.implementation_identifier;
    };
    
    public String getImplementationVendor() {
        return this.implementation_vendor;
    };
    
}

