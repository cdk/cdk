/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2001-2007  Bradley A. Smith <bradley@baysmith.com>
 *               2003-2009  Egon Willighagen <egonw@users.sf.net>
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

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.IChemFormatMatcher;
import org.openscience.cdk.io.formats.XYZFormat;

/**
 * A factory for recognizing chemical file formats. Formats
 * of GZiped files can be detected too.
 *
 * A typical example is:
 * <pre>
 *   StringReader stringReader = new StringReader("&lt;molecule/>");
 *   IChemFormat format = new FormatFactory().guessFormat(stringReader);
 * </pre>
 *
 * @cdk.module  ioformats
 * @cdk.svnrev  $Revision$
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 * @author  Bradley A. Smith <bradley@baysmith.com>
 */
@TestClass("org.openscience.cdk.io.FormatFactoryTest")
public class FormatFactory {
    
    private final static String IO_FORMATS_LIST = "io-formats.set";

    private int headerLength;

    private static List<IChemFormatMatcher> formats = null;

    /**
     * Constructs a ReaderFactory which tries to detect the format in the
     * first 65536 chars.
     */
    public FormatFactory() {
        this(65536);
    }

    /**
     * Constructs a ReaderFactory which tries to detect the format in the
     * first given number of chars.
     *
     * @param headerLength length of the header in number of chars
     */
    public FormatFactory(int headerLength) {
        this.headerLength = headerLength;
        loadFormats();
    }

    private void loadFormats() {
        if (formats == null) {
            formats = new ArrayList<IChemFormatMatcher>();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream(IO_FORMATS_LIST)
                ));
                int formatCount = 0;
                while (reader.ready()) {
                    // load them one by one
                    String formatName = reader.readLine();
                    formatCount++;
                    try {
                        Class<? extends Object> formatClass = this.getClass().getClassLoader().loadClass(formatName);
                        Method getinstanceMethod = formatClass.getMethod("getInstance", new Class[0]);
                        IChemFormatMatcher format = (IChemFormatMatcher)getinstanceMethod.invoke(null, new Object[0]);
                        formats.add(format);
                    } catch (ClassNotFoundException exception) {
                    } catch (Exception exception) {
                    }
                }
            } catch (Exception exception) {
            }
        }
    }

    /**
     * Registers a format for detection.
     */
    public void registerFormat(IChemFormatMatcher format) {
        formats.add(format);
    }
    
    /**
     * Returns the list of recognizable formats.
     * 
     * @return {@link List} of {@link IChemFormat}s.
     */
    public List<IChemFormatMatcher> getFormats(){
    	return formats;
    }

    /**
     * Creates a String of the Class name of the <code>IChemObject</code> reader
     * for this file format. The input is read line-by-line
     * until a line containing an identifying string is
     * found.
     *
     * <p>The ReaderFactory detects more formats than the CDK
     * has Readers for.
     *
     * <p>This method is not able to detect the format of gziped files.
     * Use <code>guessFormat(InputStream)</code> instead for such files.
     *
     * @throws IOException  if an I/O error occurs
     * @throws IllegalArgumentException if the input is null
     *
     * @see #guessFormat(InputStream)
     */
    public IChemFormat guessFormat(Reader input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("input cannot be null");
        }

        // make a copy of the header
        char[] header = new char[this.headerLength];
        if (!input.markSupported()) {
            throw new IllegalArgumentException("input must support mark");
        }
        input.mark(this.headerLength);
        input.read(header, 0, this.headerLength);
        input.reset();
        
        BufferedReader buffer = new BufferedReader(new CharArrayReader(header));
        
        /* Search file for a line containing an identifying keyword */
        String line = null;
        int lineNumber = 1;
        while ((line = buffer.readLine()) != null) {
            for (int i=0; i<formats.size(); i++) {
                IChemFormatMatcher cfMatcher = formats.get(i);
                if (cfMatcher.matches(lineNumber, line)) {
                    return cfMatcher;
                }
            }
            lineNumber++;
        }
        
        buffer = new BufferedReader(new CharArrayReader(header));
        
        line = buffer.readLine();
        // is it a XYZ file?
        StringTokenizer tokenizer = new StringTokenizer(line.trim());
        try {
            int tokenCount = tokenizer.countTokens();
            if (tokenCount == 1) {
                Integer.parseInt(tokenizer.nextToken());
                // if not failed, then it is a XYZ file
                return (IChemFormat)XYZFormat.getInstance();
            } else if (tokenCount == 2) {
                Integer.parseInt(tokenizer.nextToken());
                if ("Bohr".equalsIgnoreCase(tokenizer.nextToken())) {
                    return (IChemFormat)XYZFormat.getInstance();
                }
            }
        } catch (NumberFormatException exception) {
        }

        return null;
    }
    
    @TestMethod("testGuessFormat")
    public IChemFormat guessFormat(InputStream input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("input cannot be null");
        }

        // make a copy of the header
        byte[] header = new byte[this.headerLength];
        if (!input.markSupported()) {
            throw new IllegalArgumentException("input must support mark");
        }
        input.mark(this.headerLength);
        input.read(header, 0, this.headerLength);
        input.reset();

        BufferedReader buffer = new BufferedReader(
            new StringReader(new String(header))
        );

        /* Search file for a line containing an identifying keyword */
        String line = null;
        int lineNumber = 1;
        while ((line = buffer.readLine()) != null) {
            for (int i=0; i<formats.size(); i++) {
                IChemFormatMatcher cfMatcher = formats.get(i);
                if (cfMatcher.matches(lineNumber, line)) {
                    return cfMatcher;
                }
            }
            lineNumber++;
        }

        buffer = new BufferedReader(
            new StringReader(new String(header))
        );

        line = buffer.readLine();
        // is it a XYZ file?
        StringTokenizer tokenizer = new StringTokenizer(line.trim());
        try {
            int tokenCount = tokenizer.countTokens();
            if (tokenCount == 1) {
                Integer.parseInt(tokenizer.nextToken());
                // if not failed, then it is a XYZ file
                return (IChemFormat)XYZFormat.getInstance();
            } else if (tokenCount == 2) {
                Integer.parseInt(tokenizer.nextToken());
                if ("Bohr".equalsIgnoreCase(tokenizer.nextToken())) {
                    return (IChemFormat)XYZFormat.getInstance();
                }
            }
        } catch (NumberFormatException exception) {
        }

        return null;
    }
    
}
