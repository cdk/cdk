/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Helper tool to create IChemObjectWriters.
 *
 * @author Egon Willighagen &lt;ewilligh@uni-koeln.de&gt;
 **/
public class WriterFactory {

    private final static String                          IO_FORMATS_LIST = "io-formats.set";

    private static final ILoggingTool                          logger          = LoggingToolFactory
                                                                                 .createLoggingTool(WriterFactory.class);

    private static List<IChemFormat>                     formats  = new ArrayList<>();

    private static Map<String, Class<IChemObjectWriter>> registeredReaders;

    /**
     * Constructs a ChemObjectIOInstantionTests.
     */
    public WriterFactory() {
        registeredReaders = new HashMap<>();
    }

    public void registerWriter(Class<?> writer) {
        if (writer == null) return;
        if (IChemObjectWriter.class.isAssignableFrom(writer)) {
            registeredReaders.put(writer.getName(), (Class<IChemObjectWriter>) writer);
        }
    }

    /**
     * Finds IChemFormats that provide a container for serialization for the
     * given features. The syntax of the integer is explained in the DataFeatures class.
     *
     * @param  features the data features for which a IChemFormat is searched
     * @return          an array of IChemFormat's that can contain the given features
     *
     * @see    org.openscience.cdk.tools.DataFeatures
     */
    public IChemFormat[] findChemFormats(int features) {
        if (formats.isEmpty()) loadFormats();
        Iterator<IChemFormat> iter = formats.iterator();
        List<IChemFormat> matches = new ArrayList<>();
        while (iter.hasNext()) {
            IChemFormat format = iter.next();
            if ((format.getSupportedDataFeatures() & features) == features) matches.add(format);
        }

        return matches.toArray(new IChemFormat[matches.size()]);
    }

    public int formatCount() {
        if (formats.isEmpty()) loadFormats();
        return formats.size();
    }

    private void loadFormats() {
        if (formats.isEmpty()) {
            List<IChemFormat> localFormats = new ArrayList<>();
            try {
                logger.debug("Starting loading Formats...");
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader()
                        .getResourceAsStream(IO_FORMATS_LIST)));
                int formatCount = 0;
                while (reader.ready()) {
                    // load them one by one
                    String formatName = reader.readLine();
                    formatCount++;
                    try {
                        Class<?> formatClass = this.getClass().getClassLoader().loadClass(formatName);
                        Method getinstanceMethod = formatClass.getMethod("getInstance", new Class[0]);
                        IResourceFormat format = (IResourceFormat) getinstanceMethod.invoke(null, new Object[0]);
                        if (format instanceof IChemFormat) {
                            localFormats.add((IChemFormat) format);
                            logger.info("Loaded IChemFormat: " + format.getClass().getName());
                        }
                    } catch (ClassNotFoundException exception) {
                        logger.error("Could not find this IResourceFormat: ", formatName);
                        logger.debug(exception);
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
                        logger.error("Could not load this IResourceFormat: ", formatName);
                        logger.debug(exception);
                    }
                }
                logger.info("Number of loaded formats used in detection: ", formatCount);
            } catch (Exception exception) {
                logger.error("Could not load this io format list: ", IO_FORMATS_LIST);
                logger.debug(exception);
            }
            formats = localFormats;
        }
    }

    /**
     * Creates a new IChemObjectWriter based on the given IChemFormat.
     */
    public IChemObjectWriter createWriter(IChemFormat format) {
        if (format != null) {
            String writerClassName = format.getWriterClassName();
            if (writerClassName != null) {
                try {
                    if (registeredReaders.containsKey(writerClassName)) {
                        Class<IChemObjectWriter> writer = registeredReaders.get(writerClassName);
                        if (writer != null) return writer.newInstance();
                    }
                    // make a new instance of this class
                    return (IChemObjectWriter) this.getClass().getClassLoader().loadClass(writerClassName)
                            .newInstance();
                } catch (ClassNotFoundException exception) {
                    logger.error("Could not find this ChemObjectWriter: ", writerClassName);
                    logger.debug(exception);
                } catch (InstantiationException | IllegalAccessException exception) {
                    logger.error("Could not create this ChemObjectWriter: ", writerClassName);
                    logger.debug(exception);
                }
            } else {
                logger.warn("ChemFormat is recognized, but no writer is available.");
            }
        } else {
            logger.warn("ChemFormat is not recognized.");
        }
        return null;
    }
}
