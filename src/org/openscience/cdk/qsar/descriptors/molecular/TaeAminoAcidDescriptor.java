package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.tools.LoggingTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Rajarshi Guha
 * @cdk.created 2006-08-23
 * @cdk.module qsar-pdb
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:taeAminoAcid
 */
public class TaeAminoAcidDescriptor implements IMolecularDescriptor {
    private LoggingTool logger;
    private HashMap TAEParams = new HashMap();
    private int ndesc = 147;

    private HashMap nametrans = new HashMap();

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
                "$Id: TaeAminoAcidDescriptor.java 6707 2006-08-23 20:38:18Z rajarshi $",
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

    /**
     * Calculates the 147 TAE descriptors for amino acids.
     *
     * @param container Parameter is the atom container.
     * @return A DoubleArrayResult value representing the TAE descriptors
     */

    public DescriptorValue calculate(IAtomContainer container) throws CDKException {
        if (TAEParams == null) throw new CDKException("TAE parameters were not initialized");
        if (!(container instanceof IBioPolymer)) throw new CDKException("The molecule should be of type IBioPolymer");

        IBioPolymer peptide = (IBioPolymer) container;

        // I assume that we get single letter names
        Collection aas = peptide.getMonomerNames();

        double[] desc = new double[ndesc];
        for (int i = 0; i < ndesc; i++) desc[i] = 0.0;

        for (Iterator iterator = aas.iterator(); iterator.hasNext();) {
            String o = (String) iterator.next();

            if (o.length() == 0) continue;

            String olc = String.valueOf(o.toLowerCase().charAt(0));
            String tlc = (String) nametrans.get(olc);

            logger.debug("Converted " + olc + " to " + tlc);

            // get the params for this AA
            Double[] params = (Double[]) TAEParams.get(tlc);

            for (int i = 0; i < ndesc; i++) desc[i] += params[i].doubleValue();
        }

        DoubleArrayResult retval = new DoubleArrayResult(ndesc);
        for (int i = 0; i < ndesc; i++) retval.add(desc[i]);

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), retval);
    }
}
