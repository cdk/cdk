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
package org.openscience.cdk.io.iterator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.formats.MDLV3000Format;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Iterating MDL SDF reader. It allows to iterate over all molecules
 * in the SD file, without reading them into memory first. Suitable
 * for (very) large SDF files. For parsing the molecules in the
 * SD file, it uses the <code>MDLV2000Reader</code> or 
 * <code>MDLV3000Reader</code> reader; it does <b>not</b> work
 * for SDF files with MDL formats prior to the V2000 format.
 * 
 * <p>Example use:
 * <pre>
 * File sdfFile = new File("../zinc-structures/ZINC_subset3_3D_charged_wH_maxmin1000.sdf");
 * IteratingMDLReader reader = new IteratingMDLReader(
 *   new FileInputStream(sdfFile), DefaultChemObjectBuilder.getInstance()
 * );
 * while (reader.hasNext()) {
 *   IMolecule molecule = (IMolecule)reader.next();
 * }
 * </pre>
 *
 * @cdk.module io
 * @cdk.githash
 *
 * @see org.openscience.cdk.io.MDLV2000Reader
 * @see org.openscience.cdk.io.MDLV3000Reader
 * 
 * @author     Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created    2003-10-19
 *
 * @cdk.keyword    file format, MDL molfile
 * @cdk.keyword    file format, SDF
 * @cdk.iooptions
 */
@TestClass("org.openscience.cdk.io.iterator.IteratingSDFReaderTest")
public class IteratingSDFReader extends DefaultIteratingChemObjectReader<IAtomContainer> {

    private BufferedReader input;
    private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(IteratingSDFReader.class);
    private String currentLine;
    private IChemFormat currentFormat;
    private final ReaderFactory factory = new ReaderFactory();
    
    private boolean nextAvailableIsKnown;
    private boolean hasNext;
    private IChemObjectBuilder builder;
    private IAtomContainer nextMolecule;
    
    private BooleanIOSetting forceReadAs3DCoords;

    // if an error is encountered the reader will skip over the error
    private boolean skip = false;

    // buffer to store pre-read Mol records in
    private StringBuffer buffer = new StringBuffer(10000);

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    // patterns to match
    private static Pattern MDL_VERSION          = Pattern.compile("[vV](2000|3000)");
    private static Pattern M_END                = Pattern.compile("M\\s\\sEND");
    private static Pattern SDF_RECORD_SEPARATOR = Pattern.compile("\\$\\$\\$\\$");
    private static Pattern SDF_FIELD_START      = Pattern.compile("\\A>\\s");

    // map of MDL formats to their readers
    private final Map<IChemFormat, ISimpleChemObjectReader> readerMap
            = new HashMap<IChemFormat, ISimpleChemObjectReader>(5);

    /**
     * Constructs a new IteratingMDLReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     * @param builder The builder
     */
    public IteratingSDFReader(Reader in, IChemObjectBuilder builder) {
        this(in, builder, false);
    }

    /**
     * Constructs a new IteratingMDLReader that can read Molecule from a given InputStream.
     *
     * @param  in  The InputStream to read from
     * @param builder The builder
     */
    @TestMethod("testReadDataItems")
    public IteratingSDFReader(InputStream in, IChemObjectBuilder builder) {
        this(new InputStreamReader(in), builder);
    }

    /**
     * Constructs a new IteratingMDLReader that can read Molecule from a given a
     * InputStream. This constructor allows specification of whether the reader will
     * skip 'null' molecules. If skip is set to false and a broken/corrupted molecule
     * is read the iterating reader will stop at the broken molecule. However if
     * skip is set to true then the reader will keep trying to read more molecules
     * until the end of the file is reached.
     *
     * @param in       the {@link InputStream} to read from
     * @param builder  builder to use
     * @param skip     whether to skip null molecules
     */
    public IteratingSDFReader(InputStream in, IChemObjectBuilder builder, boolean skip) {
        this(new InputStreamReader(in), builder, skip);
    }

    /**
     * Constructs a new IteratingMDLReader that can read Molecule from a given a
     * Reader. This constructor allows specification of whether the reader will
     * skip 'null' molecules. If skip is set to false and a broken/corrupted molecule
     * is read the iterating reader will stop at the broken molecule. However if
     * skip is set to true then the reader will keep trying to read more molecules
     * until the end of the file is reached.
     *
     * @param in       the {@link Reader} to read from
     * @param builder  builder to use
     * @param skip     whether to skip null molecules
     */
    public IteratingSDFReader(Reader in, IChemObjectBuilder builder, boolean skip) {
        this.builder = builder;
        setReader(in);
        initIOSettings();
        setSkip(skip);
    }


    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return currentFormat;
    }

    /**
     *                Method will return an appropriate reader for the provided format. Each reader is stored
     *                in a map, if no reader is available for the specified format a new reader is created. The
     *                {@see ISimpleChemObjectReadr#setErrorHandler(IChemObjectReaderErrorHandler)} and
     *                {@see ISimpleChemObjectReadr#setReaderMode(DefaultIteratingChemObjectReader)}
     *                methods are set.
     *
     * @param  format The format to obtain a reader for
     * @return        instance of a reader appropriate for the provided format
     */
    private ISimpleChemObjectReader getReader(IChemFormat format){

        // create a new reader if not mapped
        if(!readerMap.containsKey(format)){

            ISimpleChemObjectReader reader = factory.createReader(format);
            reader.setErrorHandler(this.errorHandler);
            reader.setReaderMode(this.mode);
            if (currentFormat instanceof MDLV2000Format) {
            	reader.addSettings(getSettings());
            }

            readerMap.put(format, reader);

        }

        return readerMap.get(format);

    }

    /**
     * Returns true if another IMolecule can be read.
     */
    public boolean hasNext() {

        if (nextAvailableIsKnown) {
            return hasNext;
        }

        hasNext = false;
        nextMolecule = null;
        buffer.delete(0, buffer.length());
            
        // now try to parse the next Molecule
        try {
            currentFormat = (IChemFormat)MDLFormat.getInstance();

            while ((currentLine = input.readLine()) != null) {

                // still in a molecule
                buffer.append(currentLine).append(LINE_SEPARATOR);

                // do MDL molfile version checking
                Matcher versionMatcher = MDL_VERSION.matcher(currentLine);
                if (versionMatcher.find()) {
                    currentFormat = versionMatcher.group(1) != null
                            ? (IChemFormat) MDLV2000Format.getInstance()
                            : (IChemFormat) MDLV3000Format.getInstance();
                }

                // un-trimmed line has already been stored in buffer
                currentLine = currentLine.trim();
                
                if(M_END.matcher(currentLine).matches()) {

                    logger.debug("MDL file part read: ", buffer);

                    IAtomContainer molecule = null;

                    try{
                        ISimpleChemObjectReader reader = getReader(currentFormat);
                        InputStream byteStream         = new ByteArrayInputStream(buffer.toString().getBytes("UTF-8"));
                        reader.setReader(byteStream);
                        molecule = (IAtomContainer)reader.read(builder.newInstance(IAtomContainer.class));
                        byteStream.close();
                    } catch (Exception exception){
                        logger.error("Error while reading next molecule: " +
                                     exception.getMessage());
                        logger.debug(exception);
                    }

                    if(molecule != null){
                        readDataBlockInto(molecule);
                        hasNext              = true;
                        nextAvailableIsKnown = true;
                        nextMolecule         = molecule;
                        return true;
                    } else if(skip){
                        // null molecule and skip = true, eat up the rest of the entry until '$$$$'
                        String line;
                        while ((line = input.readLine()) != null && !SDF_RECORD_SEPARATOR.matcher(line).matches()) {
                            buffer.delete(0, buffer.length());
                        }
                    } else {
                        return false;
                    }

                    // empty the buffer
                    buffer.delete(0, buffer.length());
                       
                }

                // found SDF record separator ($$$$) without parsing a molecule (separator is detected
                // in readDataBlockInto()) the buffer is cleared and the iterator continues reading
                if(SDF_RECORD_SEPARATOR.matcher(currentLine).matches()){
                    buffer.delete(0, buffer.length());
                }

            }
        } catch (IOException exception) {
            logger.error("Error while reading next molecule: " +
                         exception.getMessage());
            logger.debug(exception);
        }

        // reached end of file
        return false;

    }

    private void readDataBlockInto(IAtomContainer m) throws IOException {
        String fieldName = null;
        while ((currentLine = input.readLine()) != null
                && !SDF_RECORD_SEPARATOR.matcher(currentLine).matches()) {
            logger.debug("looking for data header: ", currentLine);
            String str = currentLine;
            if (SDF_FIELD_START.matcher(str).find()) {
                fieldName = extractFieldName(fieldName, str);
                str = skipOtherFieldHeaderLines(str);
                String data = extractFieldData(str);
                if (fieldName != null) {
                    logger.info("fieldName, data: ", fieldName, ", ", data);
                    m.setProperty(fieldName, data);
                }
            }
        }
    }

    /**
     *        Indicate whether the reader should skip over SDF records
     *        that cause problems. If true the reader will fetch the next
     *        molecule
     * @param skip ignore error molecules continue reading
     */
    public void setSkip(boolean skip){
        this.skip = skip;
    }

    private String extractFieldData(String str) throws IOException {
        StringBuilder data = new StringBuilder();
        while (str.trim().length() > 0) {
            logger.debug("data line: ", currentLine);
			if(data.length() > 0) {
				str = System.getProperty("line.separator") + str;
			}
            data.append(str);
            currentLine = input.readLine();
            str = currentLine.trim();
        }
        return data.toString();
    }

    private String skipOtherFieldHeaderLines(String str) throws IOException {
        while (str.startsWith("> ")) {
            logger.debug("data header line: ", currentLine);
            currentLine = input.readLine();
            str = currentLine;
        }
        return str;
    }

    private String extractFieldName(String fieldName, String str) {
        int index = str.indexOf('<');
        if (index != -1) {
            int index2 = str.substring(index).indexOf('>');
            if (index2 != -1) {
                fieldName = str.substring(
                index+1,
                index+index2
                );
            }
        }
        return fieldName;
    }
    
    /**
     * Returns the next IMolecule.
     */
    public IAtomContainer next() {
        if (!nextAvailableIsKnown) {
            hasNext();
        }
        nextAvailableIsKnown = false;
        if (!hasNext) {
            throw new NoSuchElementException();
        }
        return nextMolecule;
    }
    
    @TestMethod("testClose")
    public void close() throws IOException {
        input.close();
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }

	@TestMethod("testSetReader_Reader")
    public void setReader(Reader reader) {
		if (reader instanceof BufferedReader) {
			input = (BufferedReader)reader;
		} else {
			input = new BufferedReader(reader);
		}
        nextMolecule = null;
        nextAvailableIsKnown = false;
        hasNext = false;
    }

	@TestMethod("testSetReader_InputStream")
    public void setReader(InputStream reader) {
	    setReader(new InputStreamReader(reader));
    }

    private void initIOSettings() {
        forceReadAs3DCoords = new BooleanIOSetting("ForceReadAs3DCoordinates", IOSetting.Importance.LOW,
          "Should coordinates always be read as 3D?", 
          "false");
        addSetting(forceReadAs3DCoords);
    }
    
    public void customizeJob() {
        fireIOSettingQuestion(forceReadAs3DCoords);
    }
}

