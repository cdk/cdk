/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2001-2007  Bradley A. Smith <bradley@baysmith.com>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.IChemFormatMatcher;
import org.openscience.cdk.tools.LoggingTool;

/**
 * A factory for creating ChemObjectReaders. The type of reader
 * created is determined from the content of the input. Formats
 * of GZiped files can be detected too.
 *
 * A typical example is:
 * <pre>
 *   StringReader stringReader = "&lt;molecule/>";
 *   ChemObjectReader reader = new ReaderFactory().createReader(stringReader);
 * </pre>
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 * @author  Bradley A. Smith <bradley@baysmith.com>
 */
public class ReaderFactory {
    
    private LoggingTool logger;
    private FormatFactory formatFactory = null;
    private int headerLength = 8192;

    /**
     * Constructs a ReaderFactory which tries to detect the format in the
     * first 65536 chars.
     */
    public ReaderFactory() {
        this(8192);
    }

    /**
     * Constructs a ReaderFactory which tries to detect the format in the
     * first given number of chars.
     *
     * @param headerLength length of the header in number of chars
     */
    public ReaderFactory(int headerLength) {
        logger = new LoggingTool(this);
        formatFactory = new FormatFactory(headerLength);
        this.headerLength = headerLength;
    }

    /**
     * Registers a format for detection.
     */
    public void registerFormat(IChemFormatMatcher format) {
        formatFactory.registerFormat(format);
    }
    
    public List<IChemFormatMatcher> getFormats(){
    	return formatFactory.getFormats();
    }

    /**
     * Detects the format of the Reader input, and if known, it will return
     * a CDK Reader to read the format, or null when the reader is not
     * implemented.
     *
     * @return null if CDK does not contain a reader for the detected format.
     *
     * @see #createReader(Reader)
     */
    public ISimpleChemObjectReader createReader(InputStream input) throws IOException {
        IChemFormat format = null;
        ISimpleChemObjectReader reader = null;
        if (input instanceof GZIPInputStream) {
            format = formatFactory.guessFormat(input);
            reader = createReader(format);
            if (reader != null) {
                try {
                    reader.setReader(input);
                } catch ( CDKException e1 ) {
                    IOException wrapper = new IOException("Exception while setting the InputStream: " + e1.getMessage());
                    wrapper.initCause(e1);
                    throw wrapper;
                }
            }
        } else {
            BufferedInputStream bistream = new BufferedInputStream(input, headerLength);
            InputStream istreamToRead = bistream; // if gzip test fails, then take default
            bistream.mark(5);
            int countRead = 0;
            byte[] abMagic = new byte[4];
            countRead = bistream.read(abMagic, 0, 4);
            bistream.reset();
            if (countRead == 4) {
                if (abMagic[0] == (byte)0x1F && abMagic[1] == (byte)0x8B) {
                    istreamToRead = new BufferedInputStream(
                        new GZIPInputStream(bistream)
                    );
                }
            }
            format = formatFactory.guessFormat(istreamToRead);
            reader = createReader(format);
            if (reader != null) {
                try {
                    reader.setReader(istreamToRead);
                } catch ( CDKException e1 ) {
                    IOException wrapper = new IOException("Exception while setting the InputStream: " + e1.getMessage());
                    wrapper.initCause(e1);
                    throw wrapper;
                }
            }
        }
        return reader;
    }
    
    /**
     * Creates a new IChemObjectReader based on the given IChemFormat.
     *
     * @see #createReader(InputStream)
     */
    public ISimpleChemObjectReader createReader(IChemFormat format) {
        if (format != null) {
            String readerClassName = format.getReaderClassName();
            if (readerClassName != null) {
                try {
                    // make a new instance of this class
                	return (ISimpleChemObjectReader)this.getClass().getClassLoader().
                        loadClass(readerClassName).newInstance();
                } catch (ClassNotFoundException exception) {
                    logger.error("Could not find this ChemObjectReader: ", readerClassName);
                    logger.debug(exception);
                } catch (Exception exception) {
                    logger.error("Could not create this ChemObjectReader: ", readerClassName);
                    logger.debug(exception);
                }
            } else {
                logger.warn("ChemFormat is recognized, but no reader is available.");
            }
        } else {
            logger.warn("ChemFormat is not recognized.");
        } 
        return null;
    }
    
    /**
     * Detects the format of the Reader input, and if known, it will return
     * a CDK Reader to read the format. This method is not able to detect the 
     * format of gziped files. Use createReader(InputStream) instead for such 
     * files.
     *
     * @see #createReader(InputStream)
     */
    public ISimpleChemObjectReader createReader(Reader input) throws IOException {
        if (!(input instanceof BufferedReader)) {
            input = new BufferedReader(input);
        }
        IChemFormat chemFormat = formatFactory.guessFormat((BufferedReader)input);
        ISimpleChemObjectReader coReader = createReader(chemFormat);
        try {        	
        	coReader.setReader(input);
        } catch (Exception exception) {
        	logger.error("Could not set the Reader source: ", exception.getMessage());
        	logger.debug(exception);
        }
        return coReader;
    }

}
