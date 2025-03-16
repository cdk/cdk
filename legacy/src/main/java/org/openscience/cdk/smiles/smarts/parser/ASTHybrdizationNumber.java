/* Generated By:JJTree: Do not edit this line. ASTHybrdizationNumber.java */

package org.openscience.cdk.smiles.smarts.parser;

/**
 * An AST node. Matches an atom with the specified hybridization.
 * 
 * This is not specified in the original Daylight specification, but
 * is supported by OpenEye. The hybridization is specified numerically
 * ranging from 1 to 8, corresponding to SP1, SP2, SP3, SP3D1, SP3D2
 * SP3D3, SP3D4 and SP3D5
 *
 * @author Rajarshi Guha
 * @cdk.created 2008-10-14
 * @cdk.keyword SMARTS AST
 */
@Deprecated
class ASTHybrdizationNumber extends SimpleNode {

    int hybridizationNumber;

    public ASTHybrdizationNumber(int id) {
        super(id);
    }

    public ASTHybrdizationNumber(SMARTSParser p, int id) {
        super(p, id);
    }

    public int getHybridizationNumber() {
        return hybridizationNumber;
    }

    public void setHybridizationNumber(int hybridizationNumber) {
        this.hybridizationNumber = hybridizationNumber;
    }

    /**
     * Accept the visitor. *
     */
    @Override
    public Object jjtAccept(SMARTSParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
