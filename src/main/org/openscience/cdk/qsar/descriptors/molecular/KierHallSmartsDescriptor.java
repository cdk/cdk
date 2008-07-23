/*
 *  $RCSfile$
 *  $Author: rajarshi $
 *  $Date: 2008-07-18 13:16:08 -0400 (Fri, 18 Jul 2008) $
 *  $Revision: 11645 $
 *
 *  Copyright (C) 2008 Rajarshi Guha
 *
 *  Contact: rajarshi@users.sourceforge.net
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

package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.fragments.EStateFragments;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;

/**
 * A fragment count descriptor that uses e-state fragments.
 * <p/>
 * Traditionally the e-state descriptors identify the relevant fragments and
 * then evaluate the actual e-state value. However it has been
 * <a href="http://www.mdpi.org/molecules/papers/91201004.pdf">shown</a> in {@cdk.cite BUTINA2004}
 * that simply using the <i>counts</i> of the e-state fragments can lead to QSAR models
 * that exhibit similar performance to those built using the actual e-state indices.
 * <p/>
 * Atom typing and aromaticity perception should be performed prior to calling this
 * descriptor. The atom type definitions are taken from {@cdk.cite HALL1995}.
 *  The SMARTS definitions were obtained from <a href="http://www.ordkit.org">RDKit</a>.
 * <p/>
 * The descriptor returns an integer array result of 79 values with the
 * following names (see <a href="http://www.edusoft-lc.com/molconn/manuals/350/appV.html">
 * here</a> for the corresponding chemical groups).
 * <p/>
 * <table><tr><td>Name</td><td>SMARTS</td></tr>
 * <tr><td>sLi</td><td>[LiD1]-*</td></tr>
 * <tr><td>ssBe</td><td>[BeD2](-*)-*</td></tr>
 * <tr><td>ssssBe</td><td>[BeD4](-*)(-*)(-*)-*</td></tr>
 * <tr><td>ssBH</td><td>[BD2H](-*)-*</td></tr>
 * <tr><td>sssB</td><td>[BD3](-*)(-*)-*</td></tr>
 * <tr><td>ssssB</td><td>[BD4](-*)(-*)(-*)-*</td></tr>
 * <tr><td>sCH3</td><td>[CD1H3]-*</td></tr>
 * <tr><td>dCH2</td><td>[CD1H2]=*</td></tr>
 * <tr><td>ssCH2</td><td>[CD2H2](-*)-*</td></tr>
 * <tr><td>tCH</td><td>[CD1H]#*</td></tr>
 * <tr><td>dsCH</td><td>[CD2H](=*)-*</td></tr>
 * <tr><td>aaCH</td><td>[C,c;D2H](:*):*</td></tr>
 * <tr><td>sssCH</td><td>[CD3H](-*)(-*)-*</td></tr>
 * <tr><td>ddC</td><td>[CD2H0](=*)=*</td></tr>
 * <tr><td>tsC</td><td>[CD2H0](#*)-*</td></tr>
 * <tr><td>dssC</td><td>[CD3H0](=*)(-*)-*</td></tr>
 * <tr><td>aasC</td><td>[C,c;D3H0](:*)(:*)-*</td></tr>
 * <tr><td>aaaC</td><td>[C,c;D3H0](:*)(:*):*</td></tr>
 * <tr><td>ssssC</td><td>[CD4H0](-*)(-*)(-*)-*</td></tr>
 * <tr><td>sNH3</td><td>[ND1H3]-*</td></tr>
 * <tr><td>sNH2</td><td>[ND1H2]-*</td></tr>
 * <tr><td>ssNH2</td><td>[ND2H2](-*)-*</td></tr>
 * <tr><td>dNH</td><td>[ND1H]=*</td></tr>
 * <tr><td>ssNH</td><td>[ND2H](-*)-*</td></tr>
 * <tr><td>aaNH</td><td>[N,nD2H](:*):*</td></tr>
 * <tr><td>tN</td><td>[ND1H0]#*</td></tr>
 * <tr><td>sssNH</td><td>[ND3H](-*)(-*)-*</td></tr>
 * <tr><td>dsN</td><td>[ND2H0](=*)-*</td></tr>
 * <tr><td>aaN</td><td>[N,nD2H0](:*):*</td></tr>
 * <tr><td>sssN</td><td>[ND3H0](-*)(-*)-*</td></tr>
 * <tr><td>ddsN</td><td>[ND3H0](~[OD1H0])(~[OD1H0])-,:*</td></tr>
 * <tr><td>aasN</td><td>[N,nD3H0](:*)(:*)-,:*</td></tr>
 * <tr><td>ssssN</td><td>[ND4H0](-*)(-*)(-*)-*</td></tr>
 * <tr><td>sOH</td><td>[OD1H]-*</td></tr>
 * <tr><td>dO</td><td>[OD1H0]=*</td></tr>
 * <tr><td>ssO</td><td>[OD2H0](-*)-*</td></tr>
 * <tr><td>aaO</td><td>[O,oD2H0](:*):*</td></tr>
 * <tr><td>sF</td><td>[FD1]-*</td></tr>
 * <tr><td>sSiH3</td><td>[SiD1H3]-*</td></tr>
 * <tr><td>ssSiH2</td><td>[SiD2H2](-*)-*</td></tr>
 * <tr><td>sssSiH</td><td>[SiD3H1](-*)(-*)-*</td></tr>
 * <tr><td>ssssSi</td><td>[SiD4H0](-*)(-*)(-*)-*</td></tr>
 * <tr><td>sPH2</td><td>[PD1H2]-*</td></tr>
 * <tr><td>ssPH</td><td>[PD2H1](-*)-*</td></tr>
 * <tr><td>sssP</td><td>[PD3H0](-*)(-*)-*</td></tr>
 * <tr><td>dsssP</td><td>[PD4H0](=*)(-*)(-*)-*</td></tr>
 * <tr><td>sssssP</td><td>[PD5H0](-*)(-*)(-*)(-*)-*</td></tr>
 * <tr><td>sSH</td><td>[SD1H1]-*</td></tr>
 * <tr><td>dS</td><td>[SD1H0]=*</td></tr>
 * <tr><td>ssS</td><td>[SD2H0](-*)-*</td></tr>
 * <tr><td>aaS</td><td>[S,sD2H0](:*):*</td></tr>
 * <tr><td>dssS</td><td>[SD3H0](=*)(-*)-*</td></tr>
 * <tr><td>ddssS</td><td>[SD4H0](~[OD1H0])(~[OD1H0])(-*)-*</td></tr>
 * <tr><td>sCl</td><td>[ClD1]-*</td></tr>
 * <tr><td>sGeH3</td><td>[GeD1H3](-*)</td></tr>
 * <tr><td>ssGeH2</td><td>[GeD2H2](-*)-*</td></tr>
 * <tr><td>sssGeH</td><td>[GeD3H1](-*)(-*)-*</td></tr>
 * <tr><td>ssssGe</td><td>[GeD4H0](-*)(-*)(-*)-*</td></tr>
 * <tr><td>sAsH2</td><td>[AsD1H2]-*</td></tr>
 * <tr><td>ssAsH</td><td>[AsD2H1](-*)-*</td></tr>
 * <tr><td>sssAs</td><td>[AsD3H0](-*)(-*)-*</td></tr>
 * <tr><td>sssdAs</td><td>[AsD4H0](=*)(-*)(-*)-*</td></tr>
 * <tr><td>sssssAs</td><td>[AsD5H0](-*)(-*)(-*)(-*)-*</td></tr>
 * <tr><td>sSeH</td><td>[SeD1H1]-*</td></tr>
 * <tr><td>dSe</td><td>[SeD1H0]=*</td></tr>
 * <tr><td>ssSe</td><td>[SeD2H0](-*)-*</td></tr>
 * <tr><td>aaSe</td><td>[SeD2H0](:*):*</td></tr>
 * <tr><td>dssSe</td><td>[SeD3H0](=*)(-*)-*</td></tr>
 * <tr><td>ddssSe</td><td>[SeD4H0](=*)(=*)(-*)-*</td></tr>
 * <tr><td>sBr</td><td>[BrD1]-*</td></tr>
 * <tr><td>sSnH3</td><td>[SnD1H3]-*</td></tr>
 * <tr><td>ssSnH2</td><td>[SnD2H2](-*)-*</td></tr>
 * <tr><td>sssSnH</td><td>[SnD3H1](-*)(-*)-*</td></tr>
 * <tr><td>ssssSn</td><td>[SnD4H0](-*)(-*)(-*)-*</td></tr>
 * <tr><td>sI</td><td>[ID1]-*</td></tr>
 * <tr><td>sPbH3</td><td>[PbD1H3]-*</td></tr>
 * <tr><td>ssPbH2</td><td>[PbD2H2](-*)-*</td></tr>
 * <tr><td>sssPbH</td><td>[PbD3H1](-*)(-*)-*</td></tr>
 * <tr><td>ssssPb</td><td>[PbD4H0](-*)(-*)(-*)-*</td></tr>
 * </table>
 *
 * @author Rajarshi Guha
 * @cdk.module qsarmolecular
 * @cdk.svnrev $Revision: 11645 $
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:kierHallSmarts
 */
public class KierHallSmartsDescriptor implements IMolecularDescriptor {

    private static final String[] names = EStateFragments.getNames();
    private static final String[] smarts = EStateFragments.getSmarts();

    public KierHallSmartsDescriptor() {

    }

    /**
     * Returns a <code>Map</code> which specifies which descriptor
     * is implemented by this class.
     * <p/>
     * These fields are used in the map:
     * <ul>
     * <li>Specification-Reference: refers to an entry in a unique dictionary
     * <li>Implementation-Title: anything
     * <li>Implementation-Identifier: a unique identifier for this version of
     * this class
     * <li>Implementation-Vendor: CDK, JOELib, or anything else
     * </ul>
     *
     * @return An object containing the descriptor specification
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#kierHallSmarts",
                this.getClass().getName(),
                "$Id: KierHallSmartsDescriptor.java 11645 2008-07-18 17:16:08Z rajarshi $",
                "The Chemistry Development Kit");
    }

    /**
     * Sets the parameters attribute of the descriptor.
     *
     * @param params The new parameters value
     * @throws org.openscience.cdk.exception.CDKException
     *          if any parameters are specified
     * @see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
        throw new CDKException("Must not supply any parameters");
    }


    /**
     * Gets the parameters attribute of the descriptor.
     *
     * @return The parameters value
     * @see #setParameters
     */
    public Object[] getParameters() {
        return null;
    }

    @TestMethod(value = "testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }


    private DescriptorValue getDummyDescriptorValue(Exception e) {
        IntegerArrayResult result = new IntegerArrayResult();
        for (String smart : smarts) result.add((int) Double.NaN);
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                result, getDescriptorNames(), e);
    }

    /**
     * This method calculates occurrences of the Kier &amp; Hall E-state fragments.
     *
     * @param atomContainer The molecule for which this descriptor is to be calculated
     * @return Counts of the fragments
     */
    public DescriptorValue calculate(IAtomContainer atomContainer) {
        if (atomContainer == null || atomContainer.getAtomCount() == 0) {
            return getDummyDescriptorValue(new CDKException("Container was null or else had no atoms"));
        }

        int[] counts = new int[smarts.length];
        try {
            SMARTSQueryTool sqt = new SMARTSQueryTool("C");
            for (int i = 0; i < smarts.length; i++) {
                sqt.setSmarts(smarts[i]);
                boolean status = sqt.matches(atomContainer);
                if (status) {
                    counts[i] = sqt.getUniqueMatchingAtoms().size();
                } else counts[i] = 0;
            }
        } catch (CDKException e) {
            return getDummyDescriptorValue(e);
        }

        IntegerArrayResult result = new IntegerArrayResult();
        for (Integer i : counts) result.add(i);

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                result, getDescriptorNames());
    }

    /**
     * Returns the specific type of the DescriptorResult object.
     * <p/>
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    public IDescriptorResult getDescriptorResultType() {
        return new IntegerArrayResult(smarts.length);
    }


    /**
     * Gets the parameterNames attribute of the descriptor.
     *
     * @return The parameterNames value
     */
    public String[] getParameterNames() {
        return null;
    }


    /**
     * Gets the parameterType attribute of the descriptor.
     *
     * @param name Description of the Parameter
     * @return An Object whose class is that of the parameter requested
     */
    public Object getParameterType(String name) {
        return null;
    }
}