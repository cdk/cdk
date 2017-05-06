/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MoSSOutputFormat;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Reader for MoSS output files {@cdk.cite BOR2002} which present the results
 * of a substructure mining study. These files look like:
 * <pre>
 * id,description,nodes,edges,s_abs,s_rel,c_abs,c_rel
 * 1,S-c:c:c:c:c:c,7,6,491,5.055081,5,1.7421603
 * 2,S-c:c:c:c:c,6,5,493,5.0756717,5,1.7421603
 * </pre>
 *
 * <p><b>Caution</b>: the output contains substructures, not full molecules,
 * even though they are read as such right now.
 *
 * @cdk.module  smiles
 * @cdk.githash
 * @cdk.iooptions
 *
 * @cdk.keyword MoSS
 */
public class MoSSOutputReader extends DefaultChemObjectReader {

    private BufferedReader      input;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(MoSSOutputReader.class);

    /**
     * Create a reader for MoSS output files from a {@link Reader}.
     *
     * @param input source of CIF data
     */
    public MoSSOutputReader(Reader input) {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader) input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    /**
     * Create a reader for MoSS output files from an {@link InputStream}.
     *
     * @param input source of CIF data
     */
    public MoSSOutputReader(InputStream input) {
        this(new InputStreamReader(input));
    }

    /**
     * Create a reader for MoSS output files from an empty string.
     */
    public MoSSOutputReader() {
        this(new StringReader(""));
    }

    /** {@inheritDoc} */
    @Override
    public IResourceFormat getFormat() {
        return MoSSOutputFormat.getInstance();
    }

    /** {@inheritDoc} */
    @Override
    public void setReader(Reader reader) throws CDKException {
        this.input = new BufferedReader(reader);
    }

    /** {@inheritDoc} */
    @Override
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    /** {@inheritDoc} */
    @Override
    public boolean accepts(Class<? extends IChemObject> testClass) {
        if (IAtomContainerSet.class.equals(testClass)) return true;
        if (IChemFile.class.equals(testClass)) return true;
        Class<?>[] interfaces = testClass.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (IAtomContainerSet.class.equals(interfaces[i])) return true;
            if (IChemFile.class.equals(interfaces[i])) return true;
        }
        Class superClass = testClass.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    /**
     * Read a {@link IAtomContainerSet} from the input source.
     *
     * @param  object an {@link IAtomContainerSet} into which the data is stored.
     * @return        the content in a {@link IAtomContainerSet} object
     */
    @Override
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IAtomContainerSet) {
            IAtomContainerSet cf = (IAtomContainerSet) object;
            try {
                cf = readAtomContainerSet(cf);
            } catch (IOException e) {
                logger.error("Input/Output error while reading from input.");
            }
            return (T) cf;
        } else if (object instanceof IChemFile) {
            IChemFile chemFile = (IChemFile) object;
            IChemSequence chemSeq = object.getBuilder().newInstance(IChemSequence.class);
            IChemModel chemModel = object.getBuilder().newInstance(IChemModel.class);
            IAtomContainerSet molSet = object.getBuilder().newInstance(IAtomContainerSet.class);
            try {
                molSet = readAtomContainerSet(molSet);
            } catch (IOException e) {
                logger.error("Input/Output error while reading from input.");
            }
            chemModel.setMoleculeSet(molSet);
            chemSeq.addChemModel(chemModel);
            chemFile.addChemSequence(chemSeq);
            return (T) chemFile;
        } else {
            throw new CDKException("Only supported is reading of IAtomContainerSet.");
        }
    }

    /**
     * Read the file content into a {@link IAtomContainerSet}.
     * @param molSet an {@link IAtomContainerSet} to store the structures
     * @return the {@link IAtomContainerSet} containing the molecules read in
     * @throws java.io.IOException if there is an error during reading
     */
    private IAtomContainerSet readAtomContainerSet(IAtomContainerSet molSet) throws IOException {
        SmilesParser parser = new SmilesParser(molSet.getBuilder());
        parser.kekulise(false);
        String line = input.readLine();
        line = input.readLine(); // skip the first line
        while (line != null) {
            String[] cols = line.split(",");
            try {
                IAtomContainer mol = parser.parseSmiles(cols[1]);
                mol.setProperty("focusSupport", cols[5]);
                mol.setProperty("complementSupport", cols[7]);
                mol.setProperty("atomCount", cols[2]);
                mol.setProperty("bondCount", cols[3]);
                molSet.addAtomContainer(mol);
            } catch (InvalidSmilesException exception) {
                logger.error("Skipping invalid SMILES: " + cols[1]);
                logger.debug(exception);
            }
            line = input.readLine();
        }
        return molSet;
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws IOException {
        input.close();
    }
}
