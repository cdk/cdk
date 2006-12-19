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
 */
package org.openscience.cdk.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.EnzymeResidueLocator;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.dict.DictRef;
import org.openscience.cdk.dict.DictionaryDatabase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MACiEFormat;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.IntegerIOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

/**
 * Reads an export from the MACiE enzyme reaction database.
 * Information about this database can be obtained from
 * Gemma Holiday, Cambridge University, UK, and Gail Bartlett,
 * European Bioinformatics Institute, Hinxton, UK.
 *
 * <p>This implementation is based on a dump from their database
 * on 2003-07-14.
 *
 * @cdk.module experimental
 *
 * @author     Egon Willighagen
 * @cdk.created    2003-07-24
 *
 * @cdk.keyword    file format, MACiE RDF
 * @cdk.require java1.4+
 */
public class MACiEReader extends DefaultChemObjectReader {

    /** Property it will put on ChemModel */
    public final static String CreationDate = "org.openscience.cdk.io.MACiE.CreationDate";
    /** Property it will put on ChemModel */
    public final static String MedlineID = "org.openscience.cdk.io.MACiE.MedlineID";
    /** Property it will put on ChemModel */
    public final static String PDBCode = "org.openscience.cdk.io.MACiE.PDBCode";
    /** Property it will put on ChemModel */
    public final static String ECNumber = "org.openscience.cdk.io.MACiE.ECNumber";
    /** Property it will put on ChemModel */
    public final static String EnzymeName = "org.openscience.cdk.io.MACiE.EnzymeName";
    
    private LineNumberReader input = null;
    private LoggingTool logger = null;

    private IntegerIOSetting selectedEntry;
    private BooleanIOSetting readSecondaryFiles;
    private StringIOSetting readSecondaryDir;

    private Pattern topLevelDatum;
    private Pattern subLevelDatum;
    private Pattern annotationTuple;
    private Pattern residueLocator;
    
    private ChemModel currentEntry;
    private Reaction currentReaction;
    private ReactionSet currentReactionStepSet;
    
    private String reactionStepAnnotation;
    private String reactionStepComments;
    
    private boolean readThisEntry = true;
    
    /**
     * Contructs a new MACiEReader that can read Molecule from a given Reader.
     *
     * @param  in  The Reader to read from
     */
    public MACiEReader(Reader in) {
        this();
        this.input = new LineNumberReader(in);
    }

    public MACiEReader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    public MACiEReader() {
        logger = new LoggingTool(this);
        
        /* compile patterns */
        topLevelDatum = Pattern.compile("(.+):(.+)");
        subLevelDatum = Pattern.compile("(.+):(.+)\\((.+)\\):(.+)");
        annotationTuple = Pattern.compile("(\\w+)=\\((.+?)\\);(.*)");
        residueLocator = Pattern.compile("[A-Z][a-z][a-z]\\d{1,5}"); // e.g. Lys150
        
        initIOSettings();
    }

    public IResourceFormat getFormat() {
        return MACiEFormat.getInstance();
    }

    public void setReader(Reader input) throws CDKException {
        if (input instanceof LineNumberReader) {
            this.input = (LineNumberReader)input;
        } else {
            this.input = new LineNumberReader(input);
        }
    }

    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

	public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemModel.class.equals(interfaces[i])) return true;
			if (IChemFile.class.equals(interfaces[i])) return true;
			if (IChemSequence.class.equals(interfaces[i])) return true;
		}
		return false;
	}

    /**
     * Takes an object which subclasses IChemObject, e.g. Molecule, and will read
     * this (from file, database, internet etc). If the specific implementation
     * does not support a specific IChemObject it will throw an Exception.
     *
     * @param  object The object that subclasses IChemObject
     * @return        The IChemObject read
     * @exception     CDKException
     */
     public IChemObject read(IChemObject object) throws CDKException {
         customizeJob();
         
         try {
             if (object instanceof IChemSequence) {
                 return readReactions(false);
             } else if (object instanceof IChemModel) {
                 return readReactions(true);
             } else if (object instanceof IChemFile) {
                 IChemFile chemFile = object.getBuilder().newChemFile();
                 chemFile.addChemSequence((ChemSequence)readReactions(false));
                 return chemFile;
             }
         } catch (IOException exception) {
             String message = "Error while reading file, line number: " + input.getLineNumber();
             logger.error(message);
             logger.debug(exception);
             throw new CDKException(message, exception);
         }
         throw new CDKException("Only supported are ChemSequence and ChemModel.");
     }

     public boolean accepts(IChemObject object) {
         if (object instanceof ChemSequence) {
             return true;
         } else if (object instanceof ChemModel) {
             return true;
         } else if (object instanceof ChemFile) {
             return true;
         } else if (object == null) {
             logger.warn("MACiEReader can not read null objects.");
         } else {
             logger.warn("MACiEReader can not read IChemObject of type: ", 
                         object.getClass().getName());
         }
         return false;
    }
    


    /**
     * Read a Reaction from a file in MACiE RDF format.
     *
     * @return  The Reaction that was read from the MDL file.
     */
    private IChemObject readReactions(boolean selectEntry) throws CDKException, IOException {
        ChemSequence entries = new ChemSequence();
        currentEntry = null;
        int entryCounter = 0;
        currentReactionStepSet = null;
        
        while (input.ready()) {
            String line = input.readLine();
            if (line.startsWith("$RDFILE")) {
                entries = new ChemSequence();
            } else if (line.startsWith("$DATM")) {
                entries.setProperty(CreationDate, line.substring(7));
            } else if (line.startsWith("$RIREG")) {
                // new entry, store previous entry if any
                if (currentEntry != null) {
                    // store previous entry
                    currentEntry.setReactionSet(currentReactionStepSet);
                    createNiceMACiETitle(currentEntry);
                    entries.addChemModel(currentEntry);
                    fireFrameRead();
                    if (selectEntry && (entryCounter == selectedEntry.getSettingValue())) {
                        logger.info("Starting reading wanted frame: ", selectedEntry);
                        return currentEntry;
                    } else {
                        logger.debug("Not reading unwanted frame: " + entryCounter);
                    }
                }
                currentEntry = new ChemModel();
                entryCounter++;
                if (!selectEntry || entryCounter == selectedEntry.getSettingValue()) {
                    readThisEntry = true;
                } else {
                    readThisEntry = false;
                }
                currentReactionStepSet = new ReactionSet();
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
                        logger.error("Could not parse datum tuple of type ", dataType,
                                     " around line " + input.getLineNumber());
                    }
                }
            } else {
                logger.warn("Unrecognized command on line " + input.getLineNumber(), ": ", line);
            }
        }
        
        if (currentEntry != null) {
            createNiceMACiETitle(currentEntry);
            // store last entry
            currentEntry.setReactionSet(currentReactionStepSet);
            entries.addChemModel(currentEntry);
            fireFrameRead();
        }
        
        if (selectEntry) {
            // apparently selected last one, other already returned
            return currentEntry;
        }
        return entries;
    }
    
    private void createNiceMACiETitle(ChemModel chemModel) {
        chemModel.setProperty(CDKConstants.TITLE,
            "MACIE " + currentEntry.getProperty(EnzymeName) + "= " +
            "PDB: " + currentEntry.getProperty(PDBCode) + ", " +
            "EC: " + currentEntry.getProperty(ECNumber)
        );
    }
    
    private String[] readDtypeDatumTuple(String triggerLine) throws IOException {
        String dTypeLine = triggerLine;
        String datumLine = input.readLine();
        String type = dTypeLine.substring(7);
        String datum = datumLine.substring(7);
        logger.debug("Tuple TYPE: ", type);
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
            fullDatum.append(datum.substring(0,datum.length()-1));
            do {
                line = input.readLine();
                if (line.length() > 0) 
                    fullDatum.append(line.substring(0,line.length()-1));
            } while (line.endsWith("+"));
            datum = fullDatum.toString();
        }
        logger.debug("     DATUM: ", datum);
        String[] tuple = new String[2];
        tuple[0] = type;
        tuple[1] = datum;
        return tuple;
    }
    
    private void processTopLevelField(String field, String datum) 
      throws IOException, CDKException {
        logger.debug("Processing top level field");
        if (field.equals("UNIQUE IDENTIFIER")) {
            currentEntry.setID("MACIE-" + datum);
        } else if (field.equals("EC NUMBER")) {
            currentEntry.setProperty(ECNumber, datum);
        } else if (field.equals("PDB CODE")) {
            currentEntry.setProperty(PDBCode, datum);
        } else if (field.equals("ENZYME NAME")) {
            currentEntry.setProperty(EnzymeName, datum);
        } else {
            logger.warn("Unrecognized ROOT field ", field, 
                        " around line " + input.getLineNumber());
        }
    }
    
    private void processSubLevelField(String field, String fieldNumber,
                                      String subfield, String datum) 
      throws IOException, CDKException {
        logger.debug("Processing sub level field");
        if (field.equals("OVERALL REACTION")) {
            if (subfield.equals("REACTION_ID")) {
                if (readSecondaryFiles.isSet() && readThisEntry) {
                    // parse referenced file
                    String filename = readSecondaryDir.getSetting() + datum + ".rxn";
                    File file = new File(filename);
                    if (file.exists()) {
                        logger.info("Reading overall reaction from: ", filename);
                        FileReader reader = new FileReader(file);
                        IChemObjectReader rxnReader = new ReaderFactory().createReader(reader);
                        currentReaction = (Reaction)rxnReader.read(new Reaction());
                        currentReaction.setID(datum);
                        currentReaction.setProperty(CDKConstants.TITLE, "Overall Reaction");
                        // don't add it now, wait until annotation is parsed
                    } else {
                        String error = "Cannot find secondary file: " + filename;
                        logger.error(error);
                        throw new CDKException(error);
                    }
                } else {
                    logger.warn("Not reading overall reaction for this entry");
                }
            } else if (subfield.equals("OVERALL REACTION ANNOTATION")) {
                parseReactionAnnotation(datum, currentReaction);
                currentReactionStepSet.addReaction(currentReaction);
            }
        } else if (field.equals("REACTION STAGES")) {
            if (subfield.equals("REACTION STAGES")) {
                // new reaction step
                // cannot create one, because CDK io does not
                // allow that (yet)
                reactionStepAnnotation = null;
                reactionStepComments = null;
            } else if (subfield.equals("ANNOTATION")) {
                reactionStepAnnotation = datum;
            } else if (subfield.equals("COMMENTS")) {
                reactionStepComments = datum;
            } else if (subfield.equals("STEP_ID")) {
                // read secondary RXN files?
                if (readSecondaryFiles.isSet() && readThisEntry) {
                    // parse referenced file
                    String filename = readSecondaryDir.getSetting() + datum + ".rxn";
                    File file = new File(filename);
                    if (file.exists()) {
                        logger.info("Reading reaction step from: ", filename);
                        FileReader reader = new FileReader(file);
                        IChemObjectReader rxnReader = new ReaderFactory().createReader(reader);
                        currentReaction = (Reaction)rxnReader.read(new Reaction());
                        currentReaction.setID(datum);
                        currentReaction.setProperty(CDKConstants.TITLE, "Step " + fieldNumber);
                    } else {
                        logger.error("Cannot find secondary file: ", filename);
                    }
                    // convert PseudoAtom's in EnzymeResidueLocator's if appropriate
                    markEnzymeResidueLocatorAtoms(currentReaction);
                    // now parse annotation
                    if (reactionStepAnnotation != null) {
                        parseReactionAnnotation(reactionStepAnnotation, currentReaction);
                    }
                    // and set comments
                    if (reactionStepComments != null) {
                        currentReaction.setProperty(CDKConstants.COMMENT, reactionStepComments);
                    }
                    // now, I'm ready to add reaction
                    currentReactionStepSet.addReaction(currentReaction);
                } else {
                    logger.warn("Not reading reactions of this entry.");
                }
            }
        } else if (field.equals("SUBSTRATES")) {
            logger.warn("Ignoring top level definition of substrates");
        } else if (field.equals("PRODUCTS")) {
            logger.warn("Ignoring top level definition of products");
        } else if (field.equals("REFERENCES")) {
             if (subfield.equals("MEDLINE_ID")) {
                 currentEntry.setProperty(MedlineID, datum);
             }
        } else {
            logger.error("Unrecognized sub level field ", field, 
                        " around line " + input.getLineNumber());
        }
    }
    
    private void markEnzymeResidueLocatorAtoms(Reaction currentReaction) {
    	Iterator containers = ReactionManipulator.getAllAtomContainers(currentReaction).iterator();
    	while (containers.hasNext()) {
    		IAtomContainer ac = (IAtomContainer) containers.next();
    		for (int i=0; i<ac.getAtomCount(); i++) {
    			IAtom atom = ac.getAtom(i);
    			if (atom instanceof EnzymeResidueLocator) {
    				// skip atom
    			} else if (atom instanceof PseudoAtom) {
    				PseudoAtom pseudo = (PseudoAtom)atom;
    				logger.debug("pseudo atom label: ", pseudo.getLabel());
    				logger.debug("pseudo class: ", pseudo.getClass().getName());
    				Matcher residueLocatorMatcher = residueLocator.matcher(pseudo.getLabel());
    				if (residueLocatorMatcher.matches()) {
    					logger.debug("Found residueLocator: ", pseudo.getLabel());
    					// replace atom with enzymeResidueLocator
    					IAtomContainer container = ReactionManipulator.getRelevantAtomContainer(
    							currentReaction, pseudo
    					);
    					logger.debug("Replacing the pseudo atom with a ezymeResidueLocator atom");
    					AtomContainerManipulator.replaceAtomByAtom(container, 
    							pseudo, new EnzymeResidueLocator(pseudo));
    				}
    			}
    		}
    	}
    }
    
    private void parseReactionAnnotation(String annotation, Reaction reaction) {
        logger.debug("Parsing annotation...");
        Matcher annotationTupleMatcher =
            annotationTuple.matcher(annotation);
        while (annotationTupleMatcher.matches()) {
            String field = annotationTupleMatcher.group(1);
            String value = annotationTupleMatcher.group(2);
            processAnnotation(field, value, reaction);
            // eat next part of annotation
            String remainder = annotationTupleMatcher.group(3);
            annotationTupleMatcher =
                annotationTuple.matcher(remainder);
        }
    }

    private void processAnnotation(String field, String value, Reaction reaction) {
        logger.debug("Annote: ", field, "=", value);
        if (field.equals("RxnAtts") || field.equals("RxnType")) {
            // reaction attributes
            /*String dictionary = "macie";
            if (value.equals("Acid") || value.equals("Base")) {
                dictionary = "chemical";
            }*/
            addDictRefedAnnotation(reaction, "Attributes", value);
        } else if (field.equals("ResiduesPresent") ||
                   field.equals("GroupTransferred") ||
                   field.equals("BondFormed") ||
                   field.equals("ReactiveCentres") ||
                   field.equals("BondCleaved") ||
                   field.equals("BondFormed") ||
                   field.equals("Products") ||
                   field.equals("ResiduesPresent")) {
            reaction.setProperty(new DictRef("macie:" + field, value), value);
        } else if (field.equals("Reversible")) {
            if (value.equalsIgnoreCase("yes")) {
                reaction.setDirection(Reaction.BIDIRECTIONAL);
                addDictRefedAnnotation(reaction, "ReactionType", "ReversibleReaction");
            }
        } else if (field.equals("OverallReactionType")) {
            StringTokenizer tokenizer = new StringTokenizer(value, ",");
            int i = 0;
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                i++;
                reaction.setProperty(
                    DictionaryDatabase.DICTREFPROPERTYNAME + ":field:overallReactionType:" + i, 
                    "macie:" + token.toLowerCase()
                );
            }
        } else {
            Matcher residueLocatorMatcher =
                residueLocator.matcher(field);
            if (residueLocatorMatcher.matches()) {
                logger.debug("Found residueLocator: ", field);
            	boolean found = false;
                Iterator containers = ReactionManipulator.getAllAtomContainers(reaction).iterator();
                while (containers.hasNext()) {
                	IAtomContainer ac = (IAtomContainer) containers.next();
                	logger.debug("Searching for given residueLocator through #atom: ", ac.getAtomCount());
                	logger.debug("Taken from reaction ", reaction.getID());
                	for (int i=0; (i<ac.getAtomCount() && !found); i++) {
                		if (ac.getAtom(i) instanceof PseudoAtom) {
                			// that is what we are looking for
                			PseudoAtom atom = (PseudoAtom)ac.getAtom(i);
                			if (atom.getLabel().equals(field)) {
                				// we have a hit, now mark Atom with dict refs
                				addDictRefedAnnotation(atom, "ResidueRole", value);
                				found = true;
                			}
                		}
                	}
                }
                if (!found) {
                    logger.error("MACiE annotation mentions a residue that does not exist: " + field);
                }
            } else {
                logger.error("Did not parse annotation: ", field);
            }
        }
    }
    
    private void addDictRefedAnnotation(IChemObject object, String type, String values) {
        StringTokenizer tokenizer = new StringTokenizer(values, ",");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            object.setProperty(new DictRef("macie:" + type, token), token);
            logger.debug("Added dict ref ", token, " to ", object.getClass().getName());
        }
    }
    
    public void close() throws IOException {
        input.close();
    }

    private void initIOSettings() {
        selectedEntry = new IntegerIOSetting("SelectedEntry", IOSetting.LOW,
          "Which entry should I read?",
          "1");

        readSecondaryFiles = new BooleanIOSetting("ReadSecondaryFiles", IOSetting.LOW,
          "Should I read the secondary files (if available)?",
          "true");

        readSecondaryDir = new StringIOSetting("ReadSecondaryDir", IOSetting.LOW,
          "Where can the secondary files be found?",
          System.getProperty("user.home") + System.getProperty("file.separator"));
    }
    
    private void customizeJob() {
        fireIOSettingQuestion(selectedEntry);
        fireIOSettingQuestion(readSecondaryFiles);
        fireIOSettingQuestion(readSecondaryDir);
    }

    public IOSetting[] getIOSettings() {
        IOSetting[] settings = new IOSetting[3];
        settings[0] = selectedEntry;
        settings[1] = readSecondaryFiles;
        settings[2] = readSecondaryDir;
        return settings;
    }
    
}

