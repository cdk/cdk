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
 * The SMARTS definitions were obtained from <a href="http://www.rdkit.org">RDKit</a>.
 * <p/>
 * The descriptor returns an integer array result of 79 values with the
 * following names (see <a href="http://www.edusoft-lc.com/molconn/manuals/350/appV.html">
 * here</a> for the corresponding chemical groups).
 * <p/>
 * <p/>
 * <table border=1 cellpadding=5>
 * <thead>
 * <tr>
 * <th>Serial</th>
 * <th>Name</th>
 * <th>Pattern</th>
 * </tr>
 * <tbody>
 * <tr>
 * <td>0</td><td>khs.sLi</td><td>[LiD1]-*</td>
 * </tr>
 * <tr>
 * <td>1</td><td>khs.ssBe</td><td>[BeD2](-*)-*</td>
 * </tr>
 * <tr>
 * <td>2</td><td>khs.ssssBe</td><td>[BeD4](-*)(-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>3</td><td>khs.ssBH</td><td>[BD2H](-*)-*</td>
 * </tr>
 * <tr>
 * <td>4</td><td>khs.sssB</td><td>[BD3](-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>5</td><td>khs.ssssB</td><td>[BD4](-*)(-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>6</td><td>khs.sCH3</td><td>[CD1H3]-*</td>
 * </tr>
 * <tr>
 * <td>7</td><td>khs.dCH2</td><td>[CD1H2]=*</td>
 * </tr>
 * <tr>
 * <td>8</td><td>khs.ssCH2</td><td>[CD2H2](-*)-*</td>
 * </tr>
 * <tr>
 * <td>9</td><td>khs.tCH</td><td>[CD1H]#*</td>
 * </tr>
 * <tr>
 * <td>10</td><td>khs.dsCH</td><td>[CD2H](=*)-*</td>
 * </tr>
 * <tr>
 * <td>11</td><td>khs.aaCH</td><td>[C,c;D2H](:*):*</td>
 * </tr>
 * <tr>
 * <td>12</td><td>khs.sssCH</td><td>[CD3H](-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>13</td><td>khs.ddC</td><td>[CD2H0](=*)=*</td>
 * </tr>
 * <tr>
 * <td>14</td><td>khs.tsC</td><td>[CD2H0](#*)-*</td>
 * </tr>
 * <tr>
 * <td>15</td><td>khs.dssC</td><td>[CD3H0](=*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>16</td><td>khs.aasC</td><td>[C,c;D3H0](:*)(:*)-*</td>
 * </tr>
 * <tr>
 * <td>17</td><td>khs.aaaC</td><td>[C,c;D3H0](:*)(:*):*</td>
 * </tr>
 * <tr>
 * <td>18</td><td>khs.ssssC</td><td>[CD4H0](-*)(-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>19</td><td>khs.sNH3</td><td>[ND1H3]-*</td>
 * </tr>
 * <tr>
 * <td>20</td><td>khs.sNH2</td><td>[ND1H2]-*</td>
 * </tr>
 * <tr>
 * <td>21</td><td>khs.ssNH2</td><td>[ND2H2](-*)-*</td>
 * </tr>
 * <tr>
 * <td>22</td><td>khs.dNH</td><td>[ND1H]=*</td>
 * </tr>
 * <tr>
 * <td>23</td><td>khs.ssNH</td><td>[ND2H](-*)-*</td>
 * </tr>
 * <tr>
 * <td>24</td><td>khs.aaNH</td><td>[N,nD2H](:*):*</td>
 * </tr>
 * <tr>
 * <td>25</td><td>khs.tN</td><td>[ND1H0]#*</td>
 * </tr>
 * <tr>
 * <td>26</td><td>khs.sssNH</td><td>[ND3H](-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>27</td><td>khs.dsN</td><td>[ND2H0](=*)-*</td>
 * </tr>
 * <tr>
 * <td>28</td><td>khs.aaN</td><td>[N,nD2H0](:*):*</td>
 * </tr>
 * <tr>
 * <td>29</td><td>khs.sssN</td><td>[ND3H0](-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>30</td><td>khs.ddsN</td><td>[ND3H0](~[OD1H0])(~[OD1H0])-,:*</td>
 * </tr>
 * <tr>
 * <td>31</td><td>khs.aasN</td><td>[N,nD3H0](:*)(:*)-,:*</td>
 * </tr>
 * <tr>
 * <td>32</td><td>khs.ssssN</td><td>[ND4H0](-*)(-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>33</td><td>khs.sOH</td><td>[OD1H]-*</td>
 * </tr>
 * <tr>
 * <td>34</td><td>khs.dO</td><td>[OD1H0]=*</td>
 * </tr>
 * <tr>
 * <td>35</td><td>khs.ssO</td><td>[OD2H0](-*)-*</td>
 * </tr>
 * <tr>
 * <td>36</td><td>khs.aaO</td><td>[O,oD2H0](:*):*</td>
 * </tr>
 * <tr>
 * <td>37</td><td>khs.sF</td><td>[FD1]-*</td>
 * </tr>
 * <tr>
 * <td>38</td><td>khs.sSiH3</td><td>[SiD1H3]-*</td>
 * </tr>
 * <tr>
 * <td>39</td><td>khs.ssSiH2</td><td>[SiD2H2](-*)-*</td>
 * </tr>
 * <tr>
 * <td>40</td><td>khs.sssSiH</td><td>[SiD3H1](-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>41</td><td>khs.ssssSi</td><td>[SiD4H0](-*)(-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>42</td><td>khs.sPH2</td><td>[PD1H2]-*</td>
 * </tr>
 * <tr>
 * <td>43</td><td>khs.ssPH</td><td>[PD2H1](-*)-*</td>
 * </tr>
 * <tr>
 * <td>44</td><td>khs.sssP</td><td>[PD3H0](-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>45</td><td>khs.dsssP</td><td>[PD4H0](=*)(-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>46</td><td>khs.sssssP</td><td>[PD5H0](-*)(-*)(-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>47</td><td>khs.sSH</td><td>[SD1H1]-*</td>
 * </tr>
 * <tr>
 * <td>48</td><td>khs.dS</td><td>[SD1H0]=*</td>
 * </tr>
 * <tr>
 * <td>49</td><td>khs.ssS</td><td>[SD2H0](-*)-*</td>
 * </tr>
 * <tr>
 * <td>50</td><td>khs.aaS</td><td>[S,sD2H0](:*):*</td>
 * </tr>
 * <tr>
 * <td>51</td><td>khs.dssS</td><td>[SD3H0](=*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>52</td><td>khs.ddssS</td><td>[SD4H0](~[OD1H0])(~[OD1H0])(-*)-*</td>
 * </tr>
 * <tr>
 * <td>53</td><td>khs.sCl</td><td>[ClD1]-*</td>
 * </tr>
 * <tr>
 * <td>54</td><td>khs.sGeH3</td><td>[GeD1H3](-*)</td>
 * </tr>
 * <tr>
 * <td>55</td><td>khs.ssGeH2</td><td>[GeD2H2](-*)-*</td>
 * </tr>
 * <tr>
 * <td>56</td><td>khs.sssGeH</td><td>[GeD3H1](-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>57</td><td>khs.ssssGe</td><td>[GeD4H0](-*)(-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>58</td><td>khs.sAsH2</td><td>[AsD1H2]-*</td>
 * </tr>
 * <tr>
 * <td>59</td><td>khs.ssAsH</td><td>[AsD2H1](-*)-*</td>
 * </tr>
 * <tr>
 * <td>60</td><td>khs.sssAs</td><td>[AsD3H0](-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>61</td><td>khs.sssdAs</td><td>[AsD4H0](=*)(-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>62</td><td>khs.sssssAs</td><td>[AsD5H0](-*)(-*)(-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>63</td><td>khs.sSeH</td><td>[SeD1H1]-*</td>
 * </tr>
 * <tr>
 * <td>64</td><td>khs.dSe</td><td>[SeD1H0]=*</td>
 * </tr>
 * <tr>
 * <td>65</td><td>khs.ssSe</td><td>[SeD2H0](-*)-*</td>
 * </tr>
 * <tr>
 * <td>66</td><td>khs.aaSe</td><td>[SeD2H0](:*):*</td>
 * </tr>
 * <tr>
 * <td>67</td><td>khs.dssSe</td><td>[SeD3H0](=*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>68</td><td>khs.ddssSe</td><td>[SeD4H0](=*)(=*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>69</td><td>khs.sBr</td><td>[BrD1]-*</td>
 * </tr>
 * <tr>
 * <td>70</td><td>khs.sSnH3</td><td>[SnD1H3]-*</td>
 * </tr>
 * <tr>
 * <td>71</td><td>khs.ssSnH2</td><td>[SnD2H2](-*)-*</td>
 * </tr>
 * <tr>
 * <td>72</td><td>khs.sssSnH</td><td>[SnD3H1](-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>73</td><td>khs.ssssSn</td><td>[SnD4H0](-*)(-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>74</td><td>khs.sI</td><td>[ID1]-*</td>
 * </tr>
 * <tr>
 * <td>75</td><td>khs.sPbH3</td><td>[PbD1H3]-*</td>
 * </tr>
 * <tr>
 * <td>76</td><td>khs.ssPbH2</td><td>[PbD2H2](-*)-*</td>
 * </tr>
 * <tr>
 * <td>77</td><td>khs.sssPbH</td><td>[PbD3H1](-*)(-*)-*</td>
 * </tr>
 * <tr>
 * <td>78</td><td>khs.ssssPb</td><td>[PbD4H0](-*)(-*)(-*)-*</td>
 * </tr>
 * </tbody></table>
 *
 * @author Rajarshi Guha
 * @cdk.module qsarmolecular
 * @cdk.svnrev $Revision: 11645 $
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:kierHallSmarts
 */
public class KierHallSmartsDescriptor implements IMolecularDescriptor {

    private static String[] names;
    private static final String[] smarts = EStateFragments.getSmarts();

    public KierHallSmartsDescriptor() {
        String[] tmp = EStateFragments.getNames();
        names = new String[tmp.length];
        for (int i = 0; i < tmp.length; i++) names[i] = "khs." + tmp[i];
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
        if (params != null) throw new CDKException("Must not supply any parameters");
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