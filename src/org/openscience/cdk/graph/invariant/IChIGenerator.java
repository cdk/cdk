/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.openscience.cdk.graph.invariant;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread;
import java.net.URL;

import java.util.Random;

import org.openscience.cdk.io.ChemObjectWriter;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.tools.ConnectivityChecker;

/**
 * Generate IChI for input SDF/mol file
 *
 * @author Yong Zhang <yz237@cam.ac.uk>
 */
public class IChIGenerator {

    private static String ichiProgram = "ichi.exe";
    private static org.openscience.cdk.tools.LoggingTool logger;
    private static Process process = null ;

    public IChIGenerator() {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
    }

    /**
     * Generate IChI from inputfile (mol or sdf)
     * @inputFile   Input mol or sdf file
     * @outputFile  Generated IChI file
     */
    public void generate(String inputFile, String outputFile) {
        String cmd = ichiProgram + " " + inputFile + " " + outputFile;
        logger.debug("Generating IChI: " + cmd);
        execute(cmd);
    }

    /**
    * Generate IChI from the <code>molecule</code>
    *
    * @param molecule The molecule to evaluate
    *
    */
    public String createIChI(Molecule molecule) {
        String IChI = "";
        //Save the molecule into mol file
        logger.debug("Creating IChI in cdk");
        ChemObjectWriter cow;
        FileOutputStream fos;
        File curdir = new File(System.getProperty("user.dir"));
        String molFileName = curdir.getAbsolutePath() + System.getProperty("file.separator") + getHumanreableString(30) + ".mol";
        logger.debug("molFileName = " + molFileName);
        try {
            cow = new MDLWriter(new FileWriter(molFileName));
            cow.write(molecule);
            String cmd = ichiProgram + " " + molFileName + " " + molFileName + ".ichi";
            execute(cmd);
            logger.debug("Waiting for 2 seconds to refresh the direcotry system!");
            Thread.currentThread().sleep(2000);
            FileReader fr = new FileReader(molFileName + ".ichi");
            BufferedReader br = new BufferedReader(fr);
            String record = "";
            try {
               while ( (record=br.readLine()) != null ) {
                   IChI += "\n" + record;
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
        } catch(Exception exc) {
            logger.error("Error: " + exc);
        }

        return(IChI);
    }

    /**
     * Generate IChI from inputfile (mol or sdf)
     * @inputFile   Input mol or sdf file
     * @outputFile  Generated IChI file
     */
    public static void main(String args[]) {
        logger = new org.openscience.cdk.tools.LoggingTool(IChIGenerator.class.getName());

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
        execute(cmd);
    }

    /**
     * To execute a command
     * @param cmd    command string
     */
    private static void execute(String cmd){
        try {
            File curdir = new File(System.getProperty("user.dir"));
            cmd = curdir.getAbsolutePath() + System.getProperty("file.separator") + cmd;
            String command = null;
            logger.debug("Command = " + cmd);
            process = Runtime.getRuntime().exec(cmd);
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

