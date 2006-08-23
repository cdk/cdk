package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.protein.data.PDBPolymer;
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
 * @cdk.module  qsar-pdb
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:taeAminoAcid
 */
public class TaeAminoAcidDescriptor implements IMolecularDescriptor {
    private LoggingTool logger;
    private HashMap TAEParams = new HashMap();
    private int ndesc = 147;

    private void loadTAEParams() {
        String filename = "data/taepeptides.txt";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);

        try {
            BufferedReader breader = new BufferedReader(new InputStreamReader(ins));
            breader.readLine(); // throw away the header
            for (int i = 0; i < 60; i++) {
                String line = breader.readLine();
                String[] components = line.split(",");
                if (components.length != (ndesc + 1))
                    throw new CDKException("TAE peptide data table seems to be corrupt");
                String key = components[0].trim();

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

        logger.debug("Loaded "+TAEParams.size()+ " TAE parameters for amino acids");
    }

    public TaeAminoAcidDescriptor() {
        logger = new LoggingTool(this);

        loadTAEParams();
    }

    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#taeAminoAcid",
                this.getClass().getName(),
                "$Id: TaeAminoAcidDescriptor.java 6707 2006-07-30 20:38:18Z egonw $",
                "The Chemistry Development Kit");
    }


    /**
     * Sets the parameters attribute of the PetitjeanShapeIndexDescriptor object.
     *
     * @param params The new parameters value
     * @throws org.openscience.cdk.exception.CDKException
     *          Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }

    /**
     * Gets the parameters attribute of the PetitjeanShapeIndexDescriptor object.
     *
     * @return The parameters value
     */
    public Object[] getParameters() {
        // no parameters to return
        return (null);
    }

    /**
     * Gets the parameterNames attribute of the PetitjeanShapeIndexDescriptor object.
     *
     * @return The parameterNames value
     */
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }


    /**
     * Gets the parameterType attribute of the PetitjeanShapeIndexDescriptor object.
     *
     * @param name Description of the Parameter
     * @return The parameterType value
     */
    public Object getParameterType(String name) {
        return (null);
    }

    /**
     * Calculates the two Petitjean shape indices.
     *
     * @param container Parameter is the atom container.
     * @return A DoubleArrayResult value representing the Petitjean shape indices
     */

    public DescriptorValue calculate(IAtomContainer container) throws CDKException {
        if (TAEParams == null) throw new CDKException("TAE parameters were not initialized");
        if (!(container instanceof PDBPolymer)) throw new CDKException("The molecule should be of type PDBPolymer");

        PDBPolymer peptide = (PDBPolymer) container;

        Collection aas = peptide.getMonomerNamesInSequentialOrder();

        double[] desc = new double[ndesc];
        for (int i = 0; i < ndesc; i++) desc[i] = 0.0;

        for (Iterator iterator = aas.iterator(); iterator.hasNext();) {
            String name = (String) iterator.next();
            System.out.println("name = " + name);
        }

        DoubleArrayResult retval = new DoubleArrayResult(ndesc);
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), retval);
    }
}
