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
 */
package org.openscience.cdk.io;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.io.setting.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Reads an export from the MACiE enzyme reaction database.
 * Information about this database can be obtained from
 * Gemma Holiday, Cambridge University, UK, and Gail Bartlett,
 * European Bioinformatics Institute, Hinxton, UK.
 *
 * <p>This implementation is based on a dump from their database
 * on 2003-07-14.
 *
 * @author     Egon Willighagen
 * @created    2003-07-24
 *
 * @keyword    file format, MACiE RDF
 */
public class MACiEReader extends DefaultChemObjectReader {

    private LineNumberReader input = null;
    private org.openscience.cdk.tools.LoggingTool logger = null;

    private BooleanIOSetting firstOnly;
    private BooleanIOSetting readSecondaryFiles;
    private StringIOSetting readSecondaryDir;

    private Pattern topLevelDatum;
    private Pattern subLevelDatum;
    
    private ChemModel currentEntry;
    private SetOfReactions currentReactionStepSet;
    
    /**
     * Contructs a new MDLReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     */
    public MACiEReader(Reader in) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        input = new LineNumberReader(in);
        
        /* compile patterns */
        topLevelDatum = Pattern.compile("(.+):(.+)");
        subLevelDatum = Pattern.compile("(.+):(.+)\\((.+)\\):(.+)");
    }


    /**
     * Takes an object which subclasses ChemObject, e.g. Molecule, and will read
     * this (from file, database, internet etc). If the specific implementation
     * does not support a specific ChemObject it will throw an Exception.
     *
     * @param  object The object that subclasses ChemObject
     * @return        The ChemObject read
     * @exception     CDKException
     */
     public ChemObject read(ChemObject object) throws CDKException {
         customizeJob();
         
         try {
             if (object instanceof ChemSequence) {
                 return readReactions(false);
             } else if (object instanceof ChemModel) {
                 return readReactions(true);
             }
         } catch (IOException exception) {
             String message = "Error while reading file, line number: " + input.getLineNumber();
             logger.error(message);
             logger.debug(exception);
             throw new CDKException(message);
         }
         throw new CDKException("Only supported are ChemSequence and ChemModel.");
     }

     public boolean accepts(ChemObject object) {
         if (object instanceof ChemSequence) {
             return true;
         } else if (object instanceof ChemModel) {
             return true;
         } else if (object == null) {
             logger.warn("MACiEReader can not read null objects.");
         } else {
             logger.warn("MACiEReader can not read ChemObject of type: " + 
                         object.getClass().getName());
         }
         return false;
    }
    


    /**
     * Read a Reaction from a file in MACiE RDF format.
     *
     * @return  The Reaction that was read from the MDL file.
     */
    private ChemObject readReactions(boolean selectEntry) throws CDKException, IOException {
        ChemSequence entries = new ChemSequence();
        currentEntry = null;
        currentReactionStepSet = null;
        
        while (input.ready()) {
            String line = input.readLine();
            if (line.startsWith("$RDFILE")) {
                entries = new ChemSequence();
            } else if (line.startsWith("$DATM")) {
                entries.setProperty("MACiE:CreationDate", line.substring(7));
            } else if (line.startsWith("$RIREG")) {
                // new entry
                if (currentEntry != null) {
                    // store previous entry
                    currentEntry.setSetOfReactions(currentReactionStepSet);
                    entries.addChemModel(currentEntry);
                    if (selectEntry && firstOnly.isSet()) {
                        return currentEntry;
                    }
                }
                currentEntry = new ChemModel();
                currentReactionStepSet = new SetOfReactions();
            } else if (line.startsWith("$DTYPE")) {
                String[] tuple = readDtypeDatumTuple(line);
                String dataType = tuple[0];
                String datum   = tuple[1];
                
                // now some regular expression wizardry
                Matcher subLevelMatcher = subLevelDatum.matcher(dataType);
                if (subLevelMatcher.matches()) {
                    // sub level field found
                    String field = subLevelMatcher.group(2);
                    String fieldNumber = subLevelMatcher.group(3);
                    String subfield = subLevelMatcher.group(4);
                    processSubLevelField(field, fieldNumber, subfield, datum);
                } else {
                    Matcher topLevelMatcher = topLevelDatum.matcher(dataType);
                    if (topLevelMatcher.matches()) {
                        // top level field found
                        String field = topLevelMatcher.group(2);
                        processTopLevelField(field, datum);
                    } else {
                        logger.error("Could not parse datum tuple of type " + dataType +
                        " around line " + input.getLineNumber());
                    }
                }
            } else {
                logger.warn("Unrecognized command on line " + input.getLineNumber() + ": " + line);
            }
        }
        
        if (currentEntry != null) {
            // store last entry
            currentEntry.setSetOfReactions(currentReactionStepSet);
            entries.addChemModel(currentEntry);
        }
        
        if (selectEntry) {
            // FIXME: should ask which entry to select
            return entries.getChemModel(0);
        }
        return entries;
    }
    
    private String[] readDtypeDatumTuple(String triggerLine) throws IOException {
        String dTypeLine = triggerLine;
        String datumLine = input.readLine();
        String type = dTypeLine.substring(7);
        String datum = datumLine.substring(7);
        logger.debug("Tuple TYPE: " + type);
        logger.debug("     DATUM: " + datum);
        String line = datum;
        if (datum.endsWith("$MFMT")) {
            // deal with MDL mol content
            StringBuffer fullDatum = new StringBuffer();
            do {
                line = input.readLine();
                fullDatum.append(line);
            } while (!(line.equals("M  END")));
            datum = fullDatum.toString();
        } else if (datum.endsWith("+") && (datum.length() >= 74)) {
            // deal with multiline fields
            StringBuffer fullDatum = new StringBuffer();
            fullDatum.append(datum.substring(0,datum.length()));
            do {
                line = input.readLine();
                fullDatum.append(line.substring(0,line.length()));
            } while (line.endsWith("+"));
            datum = fullDatum.toString();
        }
        logger.debug("     last processed line: " + line);
        String[] tuple = new String[2];
        tuple[0] = type;
        tuple[1] = datum;
        return tuple;
    }
    
    private void processTopLevelField(String field, String datum) 
      throws IOException, CDKException {
        logger.debug("Processing top level field");
        if (field.equals("UNIQUE IDENTIFIER")) {
            currentEntry.setID(datum);
        } else if (field.equals("EC NUMBER") ||
                   field.equals("PDB CODE") ||
                   field.equals("ENZYME NAME")) {
            currentEntry.setProperty("MACiE:" + field, datum);
        } else {
            logger.warn("Unrecognized ROOT field " + field + 
                " around line " + input.getLineNumber());
        }
    }
    
    private void processSubLevelField(String field, String fieldNumber,
                                      String subfield, String datum) 
      throws IOException, CDKException {
        logger.debug("Processing sub level field");
        if (field.equals("OVERALL REACTION")) {
            if (subfield.equals("REACTION_ID")) {
                if (readSecondaryFiles.isSet()) {
                    // parse referenced file
                    String filename = readSecondaryDir.getSetting() + datum + ".rxn";
                    File file = new File(filename);
                    if (file.exists()) {
                        logger.info("Reading overall reaction from: " + filename);
                        FileReader reader = new FileReader(file);
                        MDLRXNReader rxnReader = new MDLRXNReader(reader);
                        Reaction reaction = (Reaction)rxnReader.read(new Reaction());
                        reaction.setID("Overall Reaction");
                        currentReactionStepSet.addReaction(reaction);
                    } else {
                        logger.error("Cannot find secondary file: " + filename);
                    }
                }
            }
        } else if (field.equals("REACTION STAGES")) {
            if (subfield.equals("STEP_ID")) {
                if (readSecondaryFiles.isSet()) {
                    // parse referenced file
                    String filename = readSecondaryDir.getSetting() + datum + ".rxn";
                    File file = new File(filename);
                    if (file.exists()) {
                        logger.info("Reading overall reaction from: " + filename);
                        FileReader reader = new FileReader(file);
                        MDLRXNReader rxnReader = new MDLRXNReader(reader);
                        Reaction reaction = (Reaction)rxnReader.read(new Reaction());
                        reaction.setID("Step " + fieldNumber);
                        currentReactionStepSet.addReaction(reaction);
                    } else {
                        logger.error("Cannot find secondary file: " + filename);
                    }
                }
            }
        } else {
            logger.warn("Unrecognized sub level field " + field + 
                " around line " + input.getLineNumber());
        }
    }
    
    public void close() throws IOException {
        input.close();
    }

    private void customizeJob() {
        firstOnly = new BooleanIOSetting("FirstEntryOnly", IOSetting.LOW,
          "Should I read the first entry only?", 
          "true");
        fireReaderSettingQuestion(firstOnly);

        readSecondaryFiles = new BooleanIOSetting("ReadSecondaryFiles", IOSetting.LOW,
          "Should I read the secondary files (if available)?", 
          "true");
        fireReaderSettingQuestion(readSecondaryFiles);

        readSecondaryDir = new StringIOSetting("ReadSecondaryDir", IOSetting.LOW,
          "Where can the secondary files be found?", 
          "/home/egonw/");
        fireReaderSettingQuestion(readSecondaryDir);
    }
}

