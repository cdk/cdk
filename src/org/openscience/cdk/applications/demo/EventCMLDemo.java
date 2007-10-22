/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.applications.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.ReaderEvent;
import org.openscience.cdk.io.iterator.event.EventCMLReader;
import org.openscience.cdk.io.listener.IReaderListener;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Demo that shows how to use the EvenCMLReader.
 *
 * @cdk.module applications
 * @cdk.svnrev  $Revision$
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 *
 * @cdk.keyword      command line util
 * @cdk.keyword      file format
 * @cdk.builddepends commons-cli-1.0.jar
 */
public class EventCMLDemo {

    private LoggingTool logger;
    private EventCMLReader cor;

    public EventCMLDemo() {
        logger = new LoggingTool(this);
        LoggingTool.configureLog4j();
    }

    /**
     * Convert the file <code>ifilename</code>.
     *
     * @param ifilename name of input file
     */
    public boolean process(String ifilename) {
        boolean success = false;
        try {
            File file = new File(ifilename);
            if (file.isFile()) {
                IReaderListener listener = new CMLMolReadListener();
                cor = new EventCMLReader(
                    new FileReader(file), listener,
                    DefaultChemObjectBuilder.getInstance()
                );
                if (cor == null) {
                    logger.warn("The format of the input file is not recognized or not supported.");
                    System.err.println("The format of the input file is not recognized or not supported.");
                    return false;
                }

                cor.process();
                success = true;
            } else {
                System.err.println("Argument is not a file: " + ifilename);
                return false;
            }
        } catch (FileNotFoundException exception) {
            logger.error("File " + ifilename + " does not exist!");
            logger.debug(exception);
        } catch (Exception exception) {
            logger.debug(exception);
        }
        return success;
    }

    /**
     * actual program
     */
    public static void main(String[] args) {
        EventCMLDemo demo = new EventCMLDemo();

        // process options
        String[] filesToConvert = demo.parseCommandLineOptions(args);

        // do conversion(s)
        for (int i=0; i < filesToConvert.length; i++) {
            String inputFilename = filesToConvert[i];
            System.out.print("Processing " + inputFilename + " ... ");
            boolean success = demo.process(inputFilename);
            if (success) {
                System.out.println("succeeded!");
            } else {
                System.out.println("failed!");
            }
        }
    }

    // PRIVATE INTERNAL STUFF

    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("EventCMLDemo", options);
        
        System.exit(0);
    }
    
    /**
     * Parses the options in the command line arguments and returns
     * the index of the first non-option argument.
     */
    private String[] parseCommandLineOptions(String[] args) {

        Options options = new Options();
        options.addOption("h", "help", false, "give this help page");
        
        CommandLine line = null;
        try {
            CommandLineParser parser = new PosixParser();
            line = parser.parse(options, args);
        } catch (ParseException exception) {
            System.err.println("Unexpected exception: " + exception.toString());
        }
    
        String[] filesToConvert = line.getArgs();
        
        if (filesToConvert.length == 0 || line.hasOption("h")) {
            printHelp(options);
        }
        
        return filesToConvert;
    }

    class CMLMolReadListener implements IReaderListener {
        
        final int LIMIT = 50;
        
        int counter;
        int counter2;
        
        int atomCount;
        
        public CMLMolReadListener() {
            counter = 0;
            counter2 = 0;
            atomCount = 0;
        }
        
        public void frameRead(ReaderEvent event) {
            System.out.print(".");
            counter++;
            IAtomContainer mol = ((EventCMLReader)event.getSource()).getAtomContainer();
            atomCount += mol.getAtomCount();
            if (counter == LIMIT) {
                System.out.println(" " + atomCount + " atoms processed");
                counter = 0;
                counter2++;
                System.out.print(counter2*LIMIT + " ");
            }
        }
        
        public void processIOSettingQuestion(IOSetting setting) {}

    }
    
}
