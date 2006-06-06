/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.graph.invariant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Generates and IChI for a Molecule. Requires the ichi.exe program
 * to be installed.
 *
 * @cdk.module experimental
 *
 * @author  Yong Zhang <yz237@cam.ac.uk>
 * @cdk.created  2003-06-13
 *
 * @see org.openscience.cdk.io.IChIReader
 */
public class IChIGenerator {

    // FIX: make this configuarable
    private static String ichiProgram = "ichi.exe";
    private static LoggingTool logger;
    public IChIGenerator() {
        logger = new LoggingTool(this);
    }

    public void setIChIPath(String path) {
        ichiProgram = path;
    }
    
    /**
     * Generate IChI from the <code>Molecule</code>.
     *
     * @param molecule The molecule to evaluate
     *
     */
    public String createIChI(Molecule molecule) throws CDKException {
        String IChIString = "";
        //Save the molecule into mol file
        logger.debug("Creating IChI in cdk");
        IChemObjectWriter cow;
        // FIX: Use some TMPDIR instead of user home dir
        File curdir = new File(System.getProperty("user.dir"));
        String molFileName = curdir.getAbsolutePath() + System.getProperty("file.separator") + getHumanreableString(30) + ".mol";
        logger.debug("molFileName = " + molFileName);
        try {
            cow = new MDLWriter(new FileWriter(molFileName));
            cow.write(molecule);
            File iChiProgramLocater = new File(ichiProgram);
            if (!iChiProgramLocater.exists()) {
                throw new CDKException("Cannot find IChI executable.");
            }
            String cmd = ichiProgram + " " + molFileName + " " + molFileName + ".ichi";
            createIChI(cmd);
            logger.debug("Waiting for 2 seconds to refresh the directory system!");
            Thread.sleep(2000);
            FileReader fr = new FileReader(molFileName + ".ichi");
            BufferedReader br = new BufferedReader(fr);
            String record = "";
            try {
               while ( (record=br.readLine()) != null ) {
                   IChIString += "\n" + record;
               }
            } catch (IOException err) {
                logger.error("Error: " + err);
            }
            br.close();

            //delete the mol file and ichi file
            File molFile = new File(molFileName);
            File ichiFile = new File(molFileName + ".ichi");
            molFile.delete();
            ichiFile.delete();
        } catch(Exception exception) {
            logger.error("Error while generating IChI.");
            logger.debug(exception);
        }

        return(IChIString);
    }

    /**
     * Main method for a program that can generate an IChI for an inputfile 
     * (mol or sdf). It takes two arguments:
     * the filename of the input file (-IN filename), 
     * and the filename of the output file (-OUT filename).
     */
    public static void main(String args[]) {
        logger = new LoggingTool(IChIGenerator.class);

        if (args.length == 0) {
            System.out.println("Usage: org.openscience.cdk.graph.invariant.IChIGenerator -IN file -OUT file");
            System.exit(0);
        }
        String inFile = "";
        String outFile = "";
        int i = 0;
        while (i < args.length) {
            if (args[i].equalsIgnoreCase("-in")) {
                inFile = args[++i]; i++;
            } else if (args[i].equalsIgnoreCase("-out")) {
                outFile = args[++i]; i++;
            } else {
                System.err.println("Bad arg: "+args[i++]);
            }
        }

        String cmd = ichiProgram + " " + inFile + " " + outFile;
        createIChI(cmd);
    }

    /**
     * Method that executes the IChI program.
     *
     * @param cmd    command string
     */
    private static void createIChI(String cmd){
        // FIXME: take working dir (or tmp mol file) as parameter
        try {
            File curdir = new File(System.getProperty("user.dir"));
            cmd = curdir.getAbsolutePath() + System.getProperty("file.separator") + cmd;
            logger.debug("Command = " + cmd);
            Runtime.getRuntime().exec(cmd);
            logger.debug("Excuting command successfully!");
        } catch (IOException e) {
            logger.error("Error: " + e);
        }
    }

    private static String getHumanreableString(int length) {
        Random rn = new Random();
        byte b[] = new byte[length];
        byte chars[] = {'2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        int num = chars.length;
        for (int i = 0; i < length; i++) {
            int n = rn.nextInt() % num;
            if (n < 0) n = -n;
            b[i] = chars[n];
        }
        return new String(b);
    }
}

