/* Copyright (C) 2003-2008  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Locale;
import java.util.StringTokenizer;

import org.openscience.cdk.ReactionRole;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLRXNV3000Format;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Class that implements the new MDL mol format introduced in August 2002.
 * The overall syntax is compatible with the old format, but I consider
 * the format completely different, and thus implemented a separate Reader
 * for it.
 *
 * @cdk.module io
 * @cdk.githash
 * @cdk.iooptions
 *
 * @author Egon Willighagen &lt;egonw@sci.kun.nl&gt;
 * @cdk.created 2003-10-05
 *
 * @cdk.keyword MDL V3000
 * @cdk.require java1.4+
 */
public class MDLRXNV3000Reader extends DefaultChemObjectReader {

    BufferedReader              input  = null;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(MDLRXNV3000Reader.class);

    public MDLRXNV3000Reader(Reader in) {
        this(in, Mode.RELAXED);
    }

    public MDLRXNV3000Reader(Reader in, Mode mode) {
        if (in instanceof BufferedReader) {
            input = (BufferedReader) in;
        } else {
            input = new BufferedReader(in);
        }
        initIOSettings();
        super.mode = mode;
    }

    public MDLRXNV3000Reader(InputStream input) {
        this(input, Mode.RELAXED);
    }

    public MDLRXNV3000Reader(InputStream input, Mode mode) {
        this(new InputStreamReader(input), mode);
    }

    public MDLRXNV3000Reader() {
        this(new StringReader(""));
    }

    @Override
    public IResourceFormat getFormat() {
        return MDLRXNV3000Format.getInstance();
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
        if (IChemModel.class.equals(classObject)) return true;
        if (IReaction.class.equals(classObject)) return true;
        Class<?>[] interfaces = classObject.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (IChemModel.class.equals(interfaces[i])) return true;
            if (IReaction.class.equals(interfaces[i])) return true;
        }
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    @Override
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IReaction) {
            return (T) readReaction(object.getBuilder());
        } else if (object instanceof IChemModel) {
            IChemModel model = object.getBuilder().newInstance(IChemModel.class);
            IReactionSet reactionSet = object.getBuilder().newInstance(IReactionSet.class);
            reactionSet.addReaction(readReaction(object.getBuilder()));
            model.setReactionSet(reactionSet);
            return (T) model;
        } else {
            throw new CDKException("Only supported are Reaction and ChemModel, and not " + object.getClass().getName()
                    + ".");
        }
    }

    /**
     * Reads the command on this line. If the line is continued on the next, that
     * part is added.
     *
     * @return Returns the command on this line.
     */
    private String readCommand() throws CDKException {
        String line = readLine();
        if (line.startsWith("M  V30 ")) {
            String command = line.substring(7);
            if (command.endsWith("-")) {
                command = command.substring(0, command.length() - 1);
                command += readCommand();
            }
            return command;
        } else {
            throw new CDKException("Could not read MDL file: unexpected line: " + line);
        }
    }

    private String readLine() throws CDKException {
        String line = null;
        try {
            line = input.readLine();
            logger.debug("read line: " + line);
        } catch (Exception exception) {
            String error = "Unexpected error while reading file: " + exception.getMessage();
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error, exception);
        }
        return line;
    }

    private IReaction readReaction(IChemObjectBuilder builder) throws CDKException {
        IReaction reaction = builder.newInstance(IReaction.class);
        readLine(); // first line should be $RXN
        readLine(); // second line
        readLine(); // third line
        readLine(); // fourth line

        int reactantCount = 0;
        int productCount = 0;
        int agentCount = 0;
        boolean foundCOUNTS = false;
        while (isReady() && !foundCOUNTS) {
            String command = readCommand();
            if (command.startsWith("COUNTS")) {
                StringTokenizer tokenizer = new StringTokenizer(command);
                try {
                    tokenizer.nextToken();
                    reactantCount = Integer.valueOf(tokenizer.nextToken()).intValue();
                    logger.info("Expecting " + reactantCount + " reactants in file");
                    productCount = Integer.valueOf(tokenizer.nextToken()).intValue();
                    logger.info("Expecting " + productCount + " products in file");
                    if (tokenizer.hasMoreTokens()) {
                        agentCount = Integer.valueOf(tokenizer.nextToken()).intValue();
                        logger.info("Expecting " + agentCount + " products in file");
                        if (mode == Mode.STRICT && agentCount > 0)
                            throw new CDKException("RXN files uses agent count extension");
                    }
                } catch (Exception exception) {
                    logger.debug(exception);
                    throw new CDKException("Error while counts line of RXN file", exception);
                }
                foundCOUNTS = true;
            } else {
                logger.warn("Waiting for COUNTS line, but found: " + command);
            }
        }

        readMols(builder, reaction, ReactionRole.Reactant, reactantCount);
        readMols(builder, reaction, ReactionRole.Product, productCount);
        readMols(builder, reaction, ReactionRole.Agent, agentCount);

        return reaction;
    }

    private void readMols(IChemObjectBuilder builder, IReaction reaction, ReactionRole role, int count) throws CDKException {
        if (count == 0)
            return;
        String command = readCommand();
        if (!command.equals("BEGIN " + role.name().toUpperCase(Locale.ROOT)))
            throw new CDKException("Expected start of " + role + "s  but got: " + command);

        StringBuilder molFile = new StringBuilder();

        // now read the reactants
        for (int i = 0; i < count; i++) {
            molFile.setLength(0);
            command = readCommand();
            if (!command.endsWith("BEGIN CTAB")) {
                String error = "Excepted start of " + role + " CTAB, but found: " + command;
                logger.error(error);
                throw new CDKException(error);
            }
            String molFileLine = "";
            while ((molFileLine = readLine()) != null) {
                molFile.append(molFileLine).append('\n');
                if (molFileLine.endsWith("END CTAB"))
                    break;
            }

            try {
                // read MDL molfile content
                MDLV3000Reader reader = new MDLV3000Reader(new StringReader(molFile.toString()), super.mode);
                IAtomContainer mol = reader.read(builder.newAtomContainer());
                reader.close();

                switch (role) {
                    case Reactant:
                        reaction.addReactant(mol);
                        break;
                    case Agent:
                        reaction.addAgent(mol);
                        break;
                    case Product:
                        reaction.addProduct(mol);
                        break;
                }

            } catch (IllegalArgumentException | CDKException | IOException exception) {
                String error = "Error while reading reactant: " + exception.getMessage();
                logger.error(error);
                logger.debug(exception);
                throw new CDKException(error, exception);
            }
        }

        command = readCommand();
        if (!command.equals("END " + role.name().toUpperCase(Locale.ROOT)))
            throw new CDKException("Expected end of " + role + "s  but got: " + command);
    }

    private boolean isReady() throws CDKException {
        try {
            return input.ready();
        } catch (Exception exception) {
            String error = "Unexpected error while reading file: " + exception.getMessage();
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error, exception);
        }
    }

    public boolean accepts(IChemObject object) {
        if (object instanceof IReaction) {
            return true;
        } else if (object instanceof IChemModel) {
            return true;
        }
        return false;
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    private void initIOSettings() {}

}
