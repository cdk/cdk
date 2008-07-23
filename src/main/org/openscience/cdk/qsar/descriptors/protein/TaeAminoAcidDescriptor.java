/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
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

package org.openscience.cdk.qsar.descriptors.protein;

import org.openscience.cdk.Monomer;
import org.openscience.cdk.Strand;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.LoggingTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


/**
 * An implementation of the TAE descriptors for amino acids.
 * <p/>
 * The TAE descriptors ({@cdk.cite BREN1995} {@cdk.cite BREN1997} {@cdk.cite WHITE2003})
 * are derived from pre-calculated quantum mechanical parameters. This class
 * uses the paramaters for amino acids and thus evaluates a set of 147 descriptors for peptide
 * sequences.
 * <p/>
 * The class expects that it will be supplied an object which implements the {@link IBioPolymer}. Thus ordinary
 * AtomContainer objects  will result in an exception.
 * <p/>
 * The descriptors are returned in the following order (see
 * <a href="http://www.chem.rpi.edu/chemweb/recondoc/TAE.doc">here</a>
 * for a detailed description of the individual descriptors):
 * <pre>
 * Energy Population VOLTAE SurfArea
 * SIDel.Rho.N Del.Rho.NMin Del.Rho.NMax Del.Rho.NIA Del.Rho.NA1
 * Del.Rho.NA2 Del.Rho.NA3 Del.Rho.NA4 Del.Rho.NA5 Del.Rho.NA6
 * Del.Rho.NA7 Del.Rho.NA8 Del.Rho.NA9 Del.Rho.NA10 SIDel.K.N
 * Del.K.Min Del.K.Max Del.K.IA Del.K.NA1 Del.K.NA2
 * Del.K.NA3 Del.K.NA4 Del.K.NA5 Del.K.NA6 Del.K.NA7
 * Del.K.NA8 Del.K.NA9 Del.K.NA10 SIK SIKMin
 * SIKMax SIKIA SIKA1 SIKA2 SIKA3
 * SIKA4 SIKA5 SIKA6 SIKA7 SIKA8
 * SIKA9 SIKA10 SIDel.G.N Del.G.NMin Del.G.NMax
 * Del.G.NIA Del.G.NA1 Del.G.NA2 Del.G.NA3 Del.G.NA4
 * Del.G.NA5 Del.G.NA6 Del.G.NA7 Del.G.NA8 Del.G.NA9
 * Del.G.NA10 SIG SIGMin SIGMax SIGIA
 * SIGA1 SIGA2 SIGA3 SIGA4 SIGA5
 * SIGA6 SIGA7 SIGA8 SIGA9 SIGA10
 * SIEP SIEPMin SIEPMax SIEPIA SIEPA1
 * SIEPA2 SIEPA3 SIEPA4 SIEPA5 SIEPA6
 * SIEPA7 SIEPA8 SIEPA9 SIEPA10 EP1
 * EP2 EP3 EP4 EP5 EP6
 * EP7 EP8 EP9 EP10 PIPMin
 * PIPMax PIPAvg PIP1 PIP2 PIP3
 * PIP4 PIP5 PIP6 PIP7 PIP8
 * PIP9 PIP10 PIP11 PIP12 PIP13
 * PIP14 PIP15 PIP16 PIP17 PIP18
 * PIP19 PIP20 Fuk FukMin FukMax
 * Fuk1 Fuk2 Fuk3 Fuk4 Fuk5
 * Fuk6 Fuk7 Fuk8 Fuk9 Fuk10
 * Lapl LaplMin LaplMax Lapl1 Lapl2
 * Lapl3 Lapl4 Lapl5 Lapl6 Lapl7
 * Lapl8 Lapl9 Lapl10
 * </pre>
 * <p/>
 * <p>This descriptor uses these parameters:
 * <table border="1">
 * <tr>
 * <td>Name</td>
 * <td>Default</td>
 * <td>Description</td>
 * </tr>
 * <tr>
 * <td></td>
 * <td></td>
 * <td>no parameters</td>
 * </tr>
 * </table>
 *
 * @author      Rajarshi Guha
 * @cdk.created 2006-08-23
 * @cdk.module  qsarprotein
 * @cdk.svnrev  $Revision$
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:taeAminoAcid
 * @see         IBioPolymer
 */
public class TaeAminoAcidDescriptor implements IMolecularDescriptor {
    private LoggingTool logger;
    private Map<String, Double[]> TAEParams = new HashMap<String, Double[]>();
    private int ndesc = 147;

    private Map<String,String> nametrans = new HashMap<String,String>();

    private List getMonomers(IBioPolymer iBioPolymer) {
        List monomList = new ArrayList();

        Map strands = iBioPolymer.getStrands();
        Set strandKeys = strands.keySet();
        for (Iterator iterator = strandKeys.iterator(); iterator.hasNext();) {
            Object key = iterator.next();
            Strand aStrand = (Strand) strands.get(key);
            Map tmp = aStrand.getMonomers();
            Set keys = tmp.keySet();
            for (Iterator iterator1 = keys.iterator(); iterator1.hasNext();) {
                Object o1 = iterator1.next();
                monomList.add(tmp.get(o1));
            }
        }

        return monomList;
    }

    private void loadTAEParams() {
        String filename = "org/openscience/cdk/qsar/descriptors/data/taepeptides.txt";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        if (ins == null) {
            logger.debug("Could not load the TAE peptide parameter data file");
            TAEParams = null;
            return;
        }
        try {
            BufferedReader breader = new BufferedReader(new InputStreamReader(ins));
            breader.readLine(); // throw away the header
            for (int i = 0; i < 60; i++) {
                String line = breader.readLine();
                String[] components = line.split(",");
                if (components.length != (ndesc + 1))
                    throw new CDKException("TAE peptide data table seems to be corrupt");
                String key = components[0].toLowerCase().trim();

                Double[] data = new Double[ndesc];
                for (int j = 1; j < components.length; j++) data[j - 1] = new Double(components[j]);

                TAEParams.put(key, data);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            TAEParams = null;
            return;
        } catch (CDKException e) {
            e.printStackTrace();
            TAEParams = null;
            return;
        }

        logger.debug("Loaded " + TAEParams.size() + " TAE parameters for amino acids");
    }

    public TaeAminoAcidDescriptor() {
        logger = new LoggingTool(this);

        nametrans.put("a", "ala");
        nametrans.put("c", "cys");
        nametrans.put("d", "asp");
        nametrans.put("e", "glu");
        nametrans.put("f", "phe");
        nametrans.put("g", "gly");
        nametrans.put("h", "his");
        nametrans.put("i", "ile");
        nametrans.put("k", "lys");
        nametrans.put("l", "leu");
        nametrans.put("m", "met");
        nametrans.put("n", "asn");
        nametrans.put("p", "pro");
        nametrans.put("q", "gln");
        nametrans.put("r", "arg");
        nametrans.put("s", "ser");
        nametrans.put("t", "thr");
        nametrans.put("v", "val");
        nametrans.put("w", "trp");
        nametrans.put("y", "tyr");

        loadTAEParams();
    }

    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#taeAminoAcid",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }


    /**
     * Sets the parameters attribute of the TaeAminoAcidDescriptor object.
     *
     * @param params The new parameters value
     * @throws org.openscience.cdk.exception.CDKException
     *          Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }

    /**
     * Gets the parameters attribute of the TaeAminoAcidDescriptor object.
     *
     * @return The parameters value
     */
    public Object[] getParameters() {
        // no parameters to return
        return (null);
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        String[] names = new String[ndesc];
        for (int i = 0; i < names.length; i++) names[i] = "TAE"+i;
        return names;
    }

    /**
     * Gets the parameterNames attribute of the TaeAminOAcidDescriptor object.
     *
     * @return The parameterNames value
     */
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }


    /**
     * Gets the parameterType attribute of the TaeAminoAcidDescriptor object.
     *
     * @param name Description of the Parameter
     * @return The parameterType value
     */
    public Object getParameterType(String name) {
        return (null);
    }

     private DescriptorValue getDummyDescriptorValue(Exception e) {
        int ndesc = getDescriptorNames().length;
        DoubleArrayResult results = new DoubleArrayResult(ndesc);
        for (int i = 0; i < ndesc; i++) results.add(Double.NaN);
        return new DescriptorValue(getSpecification(), getParameterNames(),
                getParameters(), results, getDescriptorNames(), e);
    }

    /**
     * Calculates the 147 TAE descriptors for amino acids.
     *
     * @param container Parameter is the atom container which should implement {@link IBioPolymer}.
     * @return A DoubleArrayResult value representing the 147 TAE descriptors     
     */

    public DescriptorValue calculate(IAtomContainer container) {
        if (TAEParams == null) return getDummyDescriptorValue(new CDKException("TAE parameters were not initialized"));
        if (!(container instanceof IBioPolymer)) return getDummyDescriptorValue(new CDKException("The molecule should be of type IBioPolymer"));

        IBioPolymer peptide = (IBioPolymer) container;

        // I assume that we get single letter names
        //Collection aas = peptide.getMonomerNames();

        double[] desc = new double[ndesc];
        for (int i = 0; i < ndesc; i++) desc[i] = 0.0;

        List monomers = getMonomers(peptide);

        for (Iterator iterator = monomers.iterator(); iterator.hasNext();) {
            Monomer monomer = (Monomer) iterator.next();

            String o = monomer.getMonomerName();

            if (o.length() == 0) continue;

            String olc = String.valueOf(o.toLowerCase().charAt(0));
            String tlc = (String) nametrans.get(olc);


            logger.debug("Converted " + olc + " to " + tlc);

            // get the params for this AA
            Double[] params = (Double[]) TAEParams.get(tlc);

            for (int i = 0; i < ndesc; i++) desc[i] += params[i];
        }

        DoubleArrayResult retval = new DoubleArrayResult(ndesc);
        for (int i = 0; i < ndesc; i++) retval.add(desc[i]);

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                retval, getDescriptorNames());
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
        return new DoubleArrayResult();
    }

}
