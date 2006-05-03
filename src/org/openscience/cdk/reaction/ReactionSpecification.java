package org.openscience.cdk.reaction;

import org.openscience.cdk.IImplementationSpecification;

/**
 * Class that is used to distribute reactions specifications.
 *
 * @cdk.module reaction
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
     *          the source code. E.g. $Id: ReactionSpecification.java,v 1.8 2006/03/29 08:26:47 egonw Exp $ can be used when the source code is
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