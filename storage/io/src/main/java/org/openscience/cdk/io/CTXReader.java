/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.formats.CTXFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.BondManipulator;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

/**
 * Reader that extracts information from the IDENT, NAME, ATOMS and BONDS
 * blocks in CTX files.
 *
 * @cdk.module io
 * @cdk.githash
 * @cdk.iooptions
 */
public class CTXReader extends DefaultChemObjectReader {

    private BufferedReader      input;
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(CTXReader.class);

    private IChemFile           file;

    public CTXReader() {
        file = null;
    }

    public CTXReader(Reader input) {
        this();
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader) input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    public CTXReader(InputStream input) {
        this(new InputStreamReader(input));
    }

    @Override
    public IResourceFormat getFormat() {
        return CTXFormat.getInstance();
    }

    @Override
    public void setReader(Reader reader) throws CDKException {
        if (reader instanceof BufferedReader) {
            this.input = (BufferedReader) reader;
        } else {
            this.input = new BufferedReader(reader);
        }
    }

    @Override
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        if (IChemFile.class.equals(classObject)) return true;
        Class<?>[] interfaces = classObject.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (IChemFile.class.equals(interfaces[i])) return true;
        }
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    @Override
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IChemFile) {
            file = (IChemFile) object;
            return (T) readChemFile();
        } else {
            throw new CDKException("Only supported is reading of ChemFile.");
        }
    }

    private IChemFile readChemFile() throws CDKException {
        IChemSequence seq = file.getBuilder().newInstance(IChemSequence.class);
        IChemModel model = file.getBuilder().newInstance(IChemModel.class);
        IAtomContainerSet containerSet = file.getBuilder().newInstance(IAtomContainerSet.class);
        IAtomContainer container = file.getBuilder().newInstance(IAtomContainer.class);

        int lineNumber = 0;

        try {
            String line = input.readLine();
            while (input.ready() && line != null) {
                logger.debug((lineNumber++) + ": ", line);
                String command = null;
                if (isCommand(line)) {
                    command = getCommand(line);
                    int lineCount = getContentLinesCount(line);
                    if ("ATOMS".equals(command)) {
                        processAtomsBlock(lineCount, container);
                    } else if ("BONDS".equals(command)) {
                        processBondsBlock(lineCount, container);
                    } else if ("IDENT".equals(command)) {
                        processIdentBlock(lineCount, container);
                    } else if ("NAME".equals(command)) {
                        processNameBlock(lineCount, container);
                    } else {
                        // skip lines
                        logger.warn("Dropping block: ", command);
                        for (int i = 0; i < lineCount; i++)
                            input.readLine();
                    }
                } else {
                    logger.warn("Unexpected content at line: ", lineNumber);
                }
                line = input.readLine();
            }
            containerSet.addAtomContainer(container);
            model.setMoleculeSet(containerSet);
            seq.addChemModel(model);
            file.addChemSequence(seq);
        } catch (Exception exception) {
            String message = "Error while parsing CTX file: " + exception.getMessage();
            logger.error(message);
            logger.debug(exception);
            throw new CDKException(message, exception);
        }
        return file;
    }

    private void processIdentBlock(int lineCount, IAtomContainer container) throws IOException {
        String identifier = "";
        for (int i = 0; i < lineCount; i++) {
            identifier = identifier + input.readLine().trim();
        }
        container.setID(identifier);
    }

    private void processNameBlock(int lineCount, IAtomContainer container) throws IOException {
        String name = "";
        for (int i = 0; i < lineCount; i++) {
            name = name + input.readLine().trim();
        }
        container.setTitle(name);
    }

    private void processAtomsBlock(int lineCount, IAtomContainer container) throws IOException {
        for (int i = 0; i < lineCount; i++) {
            String line = input.readLine();
            int atomicNumber = Integer.parseInt(line.substring(7, 10).trim());
            IAtom atom = container.getBuilder().newInstance(IAtom.class);
            atom.setAtomicNumber(atomicNumber);
            atom.setSymbol(PeriodicTable.getSymbol(atomicNumber));
            container.addAtom(atom);
        }
    }

    private void processBondsBlock(int lineCount, IAtomContainer container) throws IOException {
        for (int i = 0; i < lineCount; i++) {
            String line = input.readLine();
            int atom1 = Integer.parseInt(line.substring(10, 13).trim()) - 1;
            int atom2 = Integer.parseInt(line.substring(16, 19).trim()) - 1;
            if (container.getBond(container.getAtom(atom1), container.getAtom(atom2)) == null) {
                IBond bond = container.getBuilder().newInstance(IBond.class, container.getAtom(atom1),
                        container.getAtom(atom2));
                int order = Integer.parseInt(line.substring(23).trim());
                bond.setOrder(BondManipulator.createBondOrder((double) order));
                container.addBond(bond);
            } // else: bond already present; CTX store the bonds twice
        }
    }

    private int getContentLinesCount(String line) {
        return Integer.parseInt(line.substring(18, 21).trim());
    }

    private String getCommand(String line) {
        return line.substring(2, 10).trim();
    }

    private boolean isCommand(String line) {
        return (line.length() > 1 && line.charAt(0) == ' ' && line.charAt(1) == '/');
    }

    @Override
    public void close() throws IOException {
        input.close();
    }
}
