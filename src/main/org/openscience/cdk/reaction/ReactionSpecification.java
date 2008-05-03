/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.reaction;

import org.openscience.cdk.IImplementationSpecification;

/**
 * Class that is used to distribute reactions specifications.
 *
 * @author      Miguel Rojas
 * @cdk.module  reaction
 * @cdk.svnrev  $Revision$
 */
public class ReactionSpecification implements IImplementationSpecification {

    private String specificationReference;
    private String implementationTitle;
    private String implementationIdentifier;
    private String implementationVendor;
    
    /**
     * Container for specifying the type of reaction.
     *
     * @param specificationReference Reference to a formal definition in a
     *          dictionary (e.g. in STMML format) of the descriptor, preferably 
     *          refering to the original article. The format of the content is
     *          expected to be &lt;dictionaryNameSpace&gt;:&lt;entryID&gt;.
     * @param implementationTitle Title for the reaction process.
     * @param implementationIdentifier Unique identifier for the actual
     *          implementation, preferably including the exact version number of
     *          the source code. E.g. $Id$ can be used when the source code is
     *          in a CVS repository.
     * @param implementationVendor Name of the organisation/person/program/whatever 
     *          who wrote/packaged the implementation.
     */
    public ReactionSpecification(
        String specificationReference,
        String implementationTitle,
        String implementationIdentifier,
        String implementationVendor) {
        this.specificationReference = specificationReference;
        this.implementationTitle = implementationTitle;
        this.implementationIdentifier = implementationIdentifier;
        this.implementationVendor = implementationVendor;
    }
    
    public String getSpecificationReference() {
        return this.specificationReference;
    };
    
    public String getImplementationTitle() {
        return this.implementationTitle;
    };
    
    public String getImplementationIdentifier() {
        return this.implementationIdentifier;
    };
    
    public String getImplementationVendor() {
        return this.implementationVendor;
    };
    
}