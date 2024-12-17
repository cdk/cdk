/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLRXNFormat;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Reads a molecule from an MDL RXN file {@cdk.cite DAL92}.
 * This MDL RXN reader uses the {@link MDLV2000Reader} to read each mol file.
 * <p>
 * This reader has two modes: {@link Mode#STRICT} and {@link Mode#RELAXED}. It
 * defaults to mode {@code RELAXED}. In {@code STRICT} mode this reader throws
 * a {@link CDKException} if there is an entry for agents on the counts line.
 * Additionally, MDLV2000Reader used for reading the individual molecular entities
 * comes with its own set of constraints when running in {@code STRICT} mode.
 * </p>
 * <p>
 *
 * </p>
 * @cdk.module io
 * @cdk.githash
 * @cdk.iooptions
 *
 * @author     Egon Willighagen
 * @author 	   Thomas Kuhn
 * @cdk.created    2003-07-24
 *
 * @cdk.keyword    file format, MDL RXN
 * @cdk.bug        1849923
 */
public class MDLRXNV2000Reader extends DefaultChemObjectReader {
    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDLRXNV2000Reader.class);
    private static final String UNEXPECTED_END_OF_INPUT = "Unexpected end of input";
    BufferedReader input;

    /**
     * Constructs a new MDLReader that can read Molecule from a given Reader.
     *
     * @param in Reader to read from
     */
    public MDLRXNV2000Reader(Reader in) {
        this(in, Mode.RELAXED);
    }

    public MDLRXNV2000Reader(Reader in, Mode mode) {
        if (in instanceof BufferedReader) {
            input = (BufferedReader) in;
        } else {
            input = new BufferedReader(in);
        }
        super.mode = mode;
    }

    public MDLRXNV2000Reader(InputStream input) {
        this(input, Mode.RELAXED);
    }

    public MDLRXNV2000Reader(InputStream input, Mode mode) {
        this(new InputStreamReader(input), mode);
    }

    public MDLRXNV2000Reader() {
        this(new StringReader(""));
    }

    @Override
    public IResourceFormat getFormat() {
        return MDLRXNFormat.getInstance();
    }

    @Override
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader) input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    @Override
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        if (IChemFile.class.equals(classObject)) return true;
        if (IChemModel.class.equals(classObject)) return true;
        if (IReaction.class.equals(classObject)) return true;
        Class<?>[] interfaces = classObject.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            if (IChemModel.class.equals(anInterface)) return true;
            if (IChemFile.class.equals(anInterface)) return true;
            if (IReaction.class.equals(anInterface)) return true;
        }
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    /**
      * Takes an object which subclasses IChemObject, e.g.Molecule, and will read
      * this (from file, database, internet etc). If the specific implementation
      * does not support a specific IChemObject it will throw an Exception.
      *
      * @param object The object that subclasses IChemObject
      *
      * @return The IChemObject read
      * @exception CDKException thrown in case the type of provided {@code object} is not supported
      */
    @Override
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IReaction) {
            return (T) readReaction(object.getBuilder());
        } else if (object instanceof IReactionSet) {
            IReactionSet reactionSet = object.getBuilder().newInstance(IReactionSet.class);
            reactionSet.addReaction(readReaction(object.getBuilder()));
            return (T) reactionSet;
        } else if (object instanceof IChemModel) {
            IChemModel model = object.getBuilder().newInstance(IChemModel.class);
            IReactionSet reactionSet = object.getBuilder().newInstance(IReactionSet.class);
            reactionSet.addReaction(readReaction(object.getBuilder()));
            model.setReactionSet(reactionSet);
            return (T) model;
        } else if (object instanceof IChemFile) {
            IChemFile chemFile = object.getBuilder().newInstance(IChemFile.class);
            IChemSequence sequence = object.getBuilder().newInstance(IChemSequence.class);
            sequence.addChemModel(read(object.getBuilder().newInstance(IChemModel.class)));
            chemFile.addChemSequence(sequence);
            return (T) chemFile;
        } else {
            throw new CDKException("Only supported are Reaction and ChemModel, and not " + object.getClass().getName() + ".");
        }
    }

    public boolean accepts(IChemObject object) {
        if (object instanceof IReaction) {
            return true;
        } else if (object instanceof IChemModel) {
            return true;
        } else if (object instanceof IChemFile) {
            return true;
        } else if (object instanceof IReactionSet) {
            return true;
        }
        return false;
    }

    /**
     * Read a Reaction from a file in MDL RXN format
     *
     * @return  The Reaction that was read from the MDL file.
     */
    private IReaction readReaction(IChemObjectBuilder builder) throws CDKException {
        IReaction reaction = builder.newReaction();
        try {
            // first line should be $RXN
            String header = input.readLine();
            if (header == null)
                return null; // empty file
            if (!header.equals("$RXN"))
                throw new CDKException("Expected $RXN but got " + header);
            String blank = input.readLine();
            if (blank == null) // second line
                throw new CDKException(UNEXPECTED_END_OF_INPUT);
            blank = input.readLine();
            if (blank == null) // third line
                throw new CDKException(UNEXPECTED_END_OF_INPUT);
            blank = input.readLine();
            if (blank == null) // fourth line
                throw new CDKException(UNEXPECTED_END_OF_INPUT);
        } catch (IOException exception) {
            logger.debug(exception);
            throw new CDKException("Error while reading header of RXN file", exception);
        }

        int numReactants;
        int numProducts;
        try {
            String countsLine = input.readLine();
            // this line contains the number of reactants and products
            StringTokenizer tokenizer = new StringTokenizer(countsLine);
            numReactants = Integer.parseInt(tokenizer.nextToken());
            logger.info("Expecting " + numReactants + " reactants in file");
            numProducts = Integer.parseInt(tokenizer.nextToken());
            logger.info("Expecting " + numProducts + " products in file");
            if (tokenizer.hasMoreTokens()) {
                int agentCount = Integer.parseInt(tokenizer.nextToken());
                // ChemAxon extension, technically BIOVIA now support this but not documented yet
                if (mode == Mode.STRICT && agentCount > 0)
                    throw new CDKException("RXN files uses agent count extension. This is not supported in mode " + mode);
                logger.info("Expecting " + agentCount + " agents in file");
            }
        } catch (IOException | NumberFormatException exception) {
            logger.debug(exception);
            throw new CDKException("Error on counts line of RXN file", exception);
        }

        // now read the molecules
        try {
            String line = input.readLine();
            if (line == null || !line.startsWith("$MOL")) {
                throw new CDKException("Expected $MOL to start, was" + line);
            }

            List<IAtomContainer> components = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            while ((line = input.readLine()) != null) {
                if (line.startsWith("$MOL")) {
                    components.add(processMol(builder.newAtomContainer(), sb));
                    sb.setLength(0);
                } else {
                    sb.append(line).append('\n');
                }
            }

            // last record
            if (sb.length() > 0)
                components.add(processMol(builder.newAtomContainer(), sb));

            // In STRICT mode agents are not supported, so an exception is thrown if we encounter more molecular
            // entities than numReactants + numProducts
            if (mode == Mode.STRICT && components.size() > numReactants +  numProducts) {
                throw new CDKException(String.format("Agents are not supported in mode %s. Found %d molecular entities, " +
                        "but there are only %d molecular entities declared on the counts line.", mode, components.size(), (numReactants + numProducts)));
            }

            for (IAtomContainer component : components.subList(0, numReactants)) {
                reaction.addReactant(component);
            }
            for (IAtomContainer component : components.subList(numReactants, numReactants+numProducts)) {
                reaction.addProduct(component);
            }
            for (IAtomContainer component : components.subList(numReactants+numProducts, components.size())) {
                reaction.addAgent(component);
            }

        } catch (IOException | IllegalArgumentException exception) {
            logger.debug(exception);
            throw new CDKException("Error while reading reactant", exception);
        }

        // now try to map things, if wanted
        logger.info("Reading atom-atom mapping from file");
        // distribute all atoms over two AtomContainer's
        IAtomContainer reactingSide = builder.newInstance(IAtomContainer.class);
        Iterator<IAtomContainer> molecules = reaction.getReactants().atomContainers().iterator();
        while (molecules.hasNext()) {
            reactingSide.add(molecules.next());
        }
        IAtomContainer producedSide = builder.newInstance(IAtomContainer.class);
        molecules = reaction.getProducts().atomContainers().iterator();
        while (molecules.hasNext()) {
            producedSide.add(molecules.next());
        }

        // map the atoms
        int mappingCount = 0;
        //        IAtom[] reactantAtoms = reactingSide.getAtoms();
        //        IAtom[] producedAtoms = producedSide.getAtoms();
        for (int i = 0; i < reactingSide.getAtomCount(); i++) {
            for (int j = 0; j < producedSide.getAtomCount(); j++) {
                IAtom eductAtom = reactingSide.getAtom(i);
                IAtom productAtom = producedSide.getAtom(j);
                if (eductAtom.getProperty(CDKConstants.ATOM_ATOM_MAPPING) != null
                        && eductAtom.getProperty(CDKConstants.ATOM_ATOM_MAPPING).equals(
                                productAtom.getProperty(CDKConstants.ATOM_ATOM_MAPPING))) {
                    reaction.addMapping(builder.newInstance(IMapping.class, eductAtom, productAtom));
                    mappingCount++;
                    break;
                }
            }
        }
        logger.info("Mapped atom pairs: " + mappingCount);

        return reaction;
    }

    private IAtomContainer processMol(IAtomContainer mol, StringBuilder sb) throws CDKException, IOException {
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(sb.toString()), super.mode);
        IAtomContainer molecule = reader.read(mol);
        reader.close();
        return molecule;
    }

    @Override
    public void close() throws IOException {
        input.close();
    }
}
