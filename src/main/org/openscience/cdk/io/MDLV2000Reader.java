/* Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sourceforge.net>
 *                    2010  Egon Willighagen <egonw@users.sourceforge.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.XMLIsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.isomorphism.matchers.CTFileQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Reads content from MDL molfiles and SD files. 
 * It can read a {@link IAtomContainer} or {@link IChemModel} from an MDL molfile, and
 * a {@link IChemFile} from a SD file, with a {@link IChemSequence} of
 * {@link IChemModel}'s, where each IChemModel will contain one IMolecule.
 *
 * <p>From the Atom block it reads atomic coordinates, element types and
 * formal charges. From the Bond block it reads the bonds and the orders.
 * Additionally, it reads 'M  CHG', 'G  ', 'M  RAD' and 'M  ISO' lines from the
 * property block.
 *
 * <p>If all z coordinates are 0.0, then the xy coordinates are taken as
 * 2D, otherwise the coordinates are read as 3D.
 *
 * <p>The title of the MOL file is read and can be retrieved with:
 * <pre>
 *   molecule.getProperty(CDKConstants.TITLE);
 * </pre>
 *
 * <p>RGroups which are saved in the MDL molfile as R#, are renamed according to their appearance,
 * e.g. the first R# is named R1. With PseudAtom.getLabel() "R1" is returned (instead of R#).
 * This is introduced due to the SAR table generation procedure of Scitegics PipelinePilot.
 *
 * @cdk.module io
 * @cdk.githash
 * @cdk.iooptions
 *
 * @author     steinbeck
 * @author     Egon Willighagen
 * @cdk.created    2000-10-02
 * @cdk.keyword    file format, MDL molfile
 * @cdk.keyword    file format, SDF
 * @cdk.bug        1587283
 */
@TestClass("org.openscience.cdk.io.MDLV2000ReaderTest")
public class MDLV2000Reader extends DefaultChemObjectReader {

    BufferedReader input = null;
    private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(MDLV2000Reader.class);

    private BooleanIOSetting forceReadAs3DCoords;
    private BooleanIOSetting interpretHydrogenIsotopes;

    //Keep track of atoms and the lines they were on in the atom block.
    private List<IAtom> atomsByLinePosition;
    // Pattern to remove trailing space (String.trim() will remove leading space, which we don't want)
    private static final Pattern TRAILING_SPACE = Pattern.compile("\\s+$");
    
    public MDLV2000Reader() {
        this(new StringReader(""));
    }
    
	/**
	 *  Constructs a new MDLReader that can read Molecule from a given InputStream.
	 *
	 *@param  in  The InputStream to read from
	 */
	public MDLV2000Reader(InputStream in) {
		this(new InputStreamReader(in));
	}
	public MDLV2000Reader(InputStream in, Mode mode) {
		this(new InputStreamReader(in), mode);
	}

	/**
	 *  Constructs a new MDLReader that can read Molecule from a given Reader.
	 *
	 *@param  in  The Reader to read from
	 */
	public MDLV2000Reader(Reader in) {
        this(in, Mode.RELAXED);
	}
	public MDLV2000Reader(Reader in, Mode mode) {
        input = new BufferedReader(in);
        initIOSettings();
        super.mode = mode;
	}

	@TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return MDLV2000Format.getInstance();
    }

    @TestMethod("testSetReader_Reader")
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    @TestMethod("testSetReader_InputStream")
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

	@TestMethod("testAccepts")
    public boolean accepts(Class<? extends IChemObject> classObject) {
		Class<?>[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
			if (IChemModel.class.equals(interfaces[i])) return true;
			if (IAtomContainer.class.equals(interfaces[i])) return true;
		}
		if (IAtomContainer.class.equals(classObject)) return true;
		if (IChemFile.class.equals(classObject)) return true;
		if (IChemModel.class.equals(classObject)) return true;
    Class superClass = classObject.getSuperclass();
    if (superClass != null) return this.accepts(superClass);
		return false;
	}

	/**
	 *  Takes an object which subclasses IChemObject, e.g. Molecule, and will read
	 *  this (from file, database, internet etc). If the specific implementation
	 *  does not support a specific IChemObject it will throw an Exception.
	 *
	 *@param  object                              The object that subclasses
	 *      IChemObject
	 *@return                                     The IChemObject read
	 *@exception  CDKException
	 */
    public <T extends IChemObject> T read(T object) throws CDKException {
		if (object instanceof IChemFile) {
			return (T)readChemFile((IChemFile)object);
        } else if (object instanceof IChemModel) {
            return (T)readChemModel((IChemModel)object);
		} else if (object instanceof IAtomContainer) {
			return (T)readAtomContainer((IAtomContainer)object);
		} else {
			throw new CDKException("Only supported are ChemFile and Molecule.");
		}
	}

    private IChemModel readChemModel(IChemModel chemModel) throws CDKException {
        IAtomContainerSet setOfMolecules = chemModel.getMoleculeSet();
        if (setOfMolecules == null) {
            setOfMolecules = chemModel.getBuilder().newInstance(IAtomContainerSet.class);
        }
        IAtomContainer m = readAtomContainer(chemModel.getBuilder().newInstance(IAtomContainer.class));
		if (m != null && m instanceof IAtomContainer) {
			setOfMolecules.addAtomContainer((IAtomContainer)m);
		}
        chemModel.setMoleculeSet(setOfMolecules);
        return chemModel;
    }

	/**
	 * Read a ChemFile from a file in MDL SDF format.
	 *
	 * @return    The ChemFile that was read from the MDL file.
	 */
    private IChemFile readChemFile(IChemFile chemFile) throws CDKException {
        IChemSequence chemSequence = chemFile.getBuilder().newInstance(IChemSequence.class);
        
        IChemModel chemModel = chemFile.getBuilder().newInstance(IChemModel.class);
		IAtomContainerSet setOfMolecules = chemFile.getBuilder().newInstance(IAtomContainerSet.class);
		IAtomContainer m = readAtomContainer(chemFile.getBuilder().newInstance(IAtomContainer.class));
		if (m != null && m instanceof IAtomContainer ) {
			setOfMolecules.addAtomContainer((IAtomContainer)m);
		}
        chemModel.setMoleculeSet(setOfMolecules);
        chemSequence.addChemModel(chemModel);
        
        setOfMolecules = chemFile.getBuilder().newInstance(IAtomContainerSet.class);
        chemModel = chemFile.getBuilder().newInstance(IChemModel.class);
		String str;
        try {
            String line;
            while ((line = input.readLine()) != null) {
                logger.debug("line: ", line);
                // apparently, this is a SDF file, continue with 
                // reading mol files
		str = new String(line);
		if (str.equals("$$$$")) {
		    m = readAtomContainer(chemFile.getBuilder().newInstance(IAtomContainer.class));
		    
		    if (m != null && m instanceof IAtomContainer) {
			setOfMolecules.addAtomContainer((IAtomContainer)m);
			
			chemModel.setMoleculeSet(setOfMolecules);
			chemSequence.addChemModel(chemModel);
			
			setOfMolecules = chemFile.getBuilder().newInstance(IAtomContainerSet.class);
			chemModel = chemFile.getBuilder().newInstance(IChemModel.class);
			
		    }
		} else {
		    // here the stuff between 'M  END' and '$$$$'
		    if (m != null) {
			// ok, the first lines should start with '>'
			String fieldName = null;
			if (str.startsWith("> ")) {
			    // ok, should extract the field name
			    str.substring(2); // String content = 
			    int index = str.indexOf("<");
			    if (index != -1) {
				int index2 = str.substring(index).indexOf(">");
				if (index2 != -1) {
				    fieldName = str.substring(
							      index+1,
							      index+index2
							      );
				}
			    }
			}
            if (line == null) {
                throw new CDKException("Expecting data line here, but found null!");
            }
			StringBuilder data = new StringBuilder();
			int dataLineCount = 0;
			boolean lineIsContinued = false;
			while ((line = input.readLine()) != null) {

                if (line.equals(" ") && dataLineCount == 0) {
                    // apparently a file can have a field whose value is a single space. Moronic
                    // we check for it *before* trimming it. ideally we should check for any length
                    // of whitespace

                    // In adition some SD files have the blank line after the value line contain
                    // a space, rather than being a true blank line. So we only store a blank value
                    // line if it's the first line after the key line
                    data.append(line);
                    lineIsContinued = false;
                    dataLineCount++;
                    if (!lineIsContinued && dataLineCount > 1)
                        data.append(System.getProperty("line.separator"));
                    continue;
                }

                line = line.trim();
                if (line.length() == 0) break;
                
                if (line.equals("$$$$")) {
                	logger.error("Expecting data line here, but found end of molecule: ", line);
                	break;
                }
                logger.debug("data line: ", line);
                lineIsContinued = false; // reset property
                dataLineCount++;

                // preserve newlines, unless the line is exactly 80 chars;
                // in that case it is assumed to continue on the next line.
                // See MDL documentation.
                if (!lineIsContinued && dataLineCount > 1)
                    data.append(System.getProperty("line.separator"));

                // add the data line
                data.append(line);

                // check if the line will be continued on the next line
			    if (line.length() == 80) lineIsContinued = true;
			}

			if (fieldName != null) {
			    logger.info("fieldName, data: ", fieldName, ", ", data);
			    m.setProperty(fieldName, data.toString());
			}
		    }
		}
            }
        } catch (CDKException cdkexc) {
            throw cdkexc;
        } catch (Exception exception) {
            String error = "Error while parsing SDF";
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error, exception);
        }
		try {
			input.close();
		} catch (Exception exc) {
            String error = "Error while closing file: " + exc.getMessage();
            logger.error(error);
			throw new CDKException(error, exc);
		}

        chemFile.addChemSequence(chemSequence);
		return chemFile;
	}



	/**
	 *  Read an IAtomContainer from a file in MDL sd format
	 *
	 *@return    The Molecule that was read from the MDL file.
	 */
	private IAtomContainer readAtomContainer(IAtomContainer molecule) throws CDKException {
        logger.debug("Reading new molecule");
	    IAtomContainer outputContainer=null;
        int linecount = 0;
        int atoms = 0;
        int bonds = 0;
        int atom1 = 0;
        int atom2 = 0;
        int order = 0;
        IBond.Stereo stereo = (IBond.Stereo)CDKConstants.UNSET;
        int RGroupCounter=1;
        int Rnumber=0;
        String [] rGroup=null;
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        double totalX = 0.0;
        double totalY = 0.0;
        double totalZ = 0.0;
        String title=null;
        String remark=null;
        //int[][] conMat = new int[0][0];
        //String help;
        IAtom atom;
        String line = "";
        //A map to keep track of R# atoms so that RGP line can be parsed
        Map<Integer,IPseudoAtom> rAtoms = new HashMap<Integer,IPseudoAtom>();
        
        try {
        	IsotopeFactory isotopeFactory = XMLIsotopeFactory.getInstance(molecule.getBuilder());
        	
            logger.info("Reading header");
            line = input.readLine(); linecount++;
            if (line == null) {
                return null;
            }
            logger.debug("Line " + linecount + ": " + line);

            if (line.startsWith("$$$$")) {
                logger.debug("File is empty, returning empty molecule");
                return molecule;
            }
            if (line.length() > 0) {
                title = line; 
            }
            line = input.readLine(); linecount++;
            logger.debug("Line " + linecount + ": " + line);
            line = input.readLine(); linecount++;
            logger.debug("Line " + linecount + ": " + line);
            if (line.length() > 0) {
                remark = line;
            }
            
            logger.info("Reading rest of file");
            line = input.readLine(); linecount++;
            logger.debug("Line " + linecount + ": " + line);

            // if the line is empty we hav a problem - either a malformed
            // molecule entry or just extra new lines at the end of the file
            if (line.length() == 0) {
                // read till the next $$$$ or EOF
                while (true) {
                    line = input.readLine(); linecount++;
                    if (line == null) {
                        return null;
                    }
                    if (line.startsWith("$$$$")) {
                        return molecule; // an empty molecule
                    }
                }
            }

            // check the CT block version
            if (line.contains("V3000") || line.contains("v3000")) {
                handleError("This file must be read with the MDLV3000Reader.");
            } else if (!line.contains("V2000") && !line.contains("v2000")) {
                handleError("This file must be read with the MDLReader.");
            }

            atoms = Integer.parseInt(line.substring(0, 3).trim());
		    List<IAtom> atomList = new ArrayList<IAtom>();

            logger.debug("Atomcount: " + atoms);
            bonds = Integer.parseInt(line.substring(3, 6).trim());
            logger.debug("Bondcount: " + bonds);
            List<IBond> bondList = new ArrayList<IBond>();

            // used for applying the MDL valence model
            int[] explicitValence = new int[atoms];
            
            // read ATOM block
            logger.info("Reading atom block");
      	    atomsByLinePosition = new ArrayList<IAtom>();
		        atomsByLinePosition.add(null); // 0 is not a valid position
            int atomBlockLineNumber=0;
            for (int f = 0; f < atoms; f++) {
                line = input.readLine(); linecount++; atomBlockLineNumber++;
                Matcher trailingSpaceMatcher = TRAILING_SPACE.matcher(line);
                if(trailingSpaceMatcher.find()){
                    handleError("Trailing space found",
                                linecount,
                                trailingSpaceMatcher.start(), trailingSpaceMatcher.end());
                    line = trailingSpaceMatcher.replaceAll("");
                }
                x = Double.parseDouble(line.substring(0, 10).trim());
                y = Double.parseDouble(line.substring(10, 20).trim());
                z = Double.parseDouble(line.substring(20, 30).trim());
                // *all* values should be zero, not just the sum
                totalX += Math.abs(x);
                totalY += Math.abs(y);
                totalZ += Math.abs(z);
                logger.debug("Coordinates: " + x + "; " + y + "; " + z);
                String element = line.substring(31, Math.min(line.length(), 34)).trim();
                if(line.length() < 34){
                    handleError("Element atom type does not follow V2000 format type should of length three" +
                                " and padded with space if required",
                                linecount, 31, 34);
                }

                logger.debug("Atom type: ", element);
                if (isotopeFactory.isElement(element)) {
                    atom = isotopeFactory.configure(molecule.getBuilder().newInstance(IAtom.class,element));
                } else if ("A".equals(element)) {
                	atom = molecule.getBuilder().newInstance(IPseudoAtom.class,element);
                } else if ("Q".equals(element)) {
                	atom = molecule.getBuilder().newInstance(IPseudoAtom.class,element);
                } else if ("*".equals(element)) {
                	atom = molecule.getBuilder().newInstance(IPseudoAtom.class,element);
                } else if ("LP".equals(element)) {
                	atom = molecule.getBuilder().newInstance(IPseudoAtom.class,element);
                } else if ("L".equals(element)) {
                	atom = molecule.getBuilder().newInstance(IPseudoAtom.class,element);
                } else if ( element.equals("R") || 
                           (element.length() > 0 && element.charAt(0) == 'R')){
                 	  logger.debug("Atom ", element, " is not an regular element. Creating a PseudoAtom.");
                    //check if the element is R
                    rGroup=element.split("^R");
                    atom=null;
                    if (rGroup.length >1){
                    	try{
                    		Rnumber= Integer.valueOf(rGroup[(rGroup.length - 1)]);
                    		RGroupCounter=Rnumber;
                  	    element="R"+Rnumber;
                   	    atom = molecule.getBuilder().newInstance(IPseudoAtom.class, element);

                    	}catch(Exception ex){
                        // This happens for atoms labeled "R#".
                        // The Rnumber may be set later on, using RGP line
                        atom = molecule.getBuilder().newInstance(IPseudoAtom.class, "R");
                   	    rAtoms.put(atomBlockLineNumber,(IPseudoAtom)atom);
                    	}
                    }
                    else {
                        atom = molecule.getBuilder().newInstance(IPseudoAtom.class,element);
                    }
                } else {
                    handleError(
                            "Invalid element type. Must be an existing " +
                                    "element, or one in: A, Q, L, LP, *.",
                            linecount, 32, 35
                    );
                    atom = molecule.getBuilder().newInstance(IPseudoAtom.class, element);
                    atom.setSymbol(element);
                }

                // store as 3D for now, convert to 2D (if totalZ == 0.0) later
                atom.setPoint3d(new Point3d(x, y, z));
                
                // parse further fields
                if(line.length() >= 36){
                String massDiffString = line.substring(34,36).trim();
                logger.debug("Mass difference: ", massDiffString);
                if (!(atom instanceof IPseudoAtom)) {
                    try {
                        int massDiff = Integer.parseInt(massDiffString);
                        if (massDiff != 0) {
                            IIsotope major = XMLIsotopeFactory.getInstance(molecule.getBuilder()).getMajorIsotope(element);
                            atom.setMassNumber(major.getMassNumber() + massDiff);
                        }
                    } catch (Exception exception) {
                        handleError(
                            "Could not parse mass difference field.",
                            linecount, 35, 37,
                            exception
                        );
                    }
                } else {
                    logger.error("Cannot set mass difference for a non-element!");
                }
                } else {                      
                    handleError("Mass difference is missing", linecount, 34, 36);
                }

                
                // set the stereo partiy
                Integer parity = line.length() > 41 ? Character.digit(line.charAt(41), 10) : 0;                                
                atom.setStereoParity(parity);
                
                if (line.length() >= 51) {
                    String valenceString = removeNonDigits(line.substring(48,51));
                    logger.debug("Valence: ", valenceString);
                    if (!(atom instanceof IPseudoAtom)) {
                        try {
                            int valence = Integer.parseInt(valenceString);
                            if (valence != 0) {
                                //15 is defined as 0 in mol files
                                if(valence==15)
                                    atom.setValency(0);
                                else
                                    atom.setValency(valence);
                            }
                        } catch (Exception exception) {
                            handleError(
                                "Could not parse valence information field",
                                linecount, 49, 52,
                                exception
                            );
                        }
                    } else {
                        logger.error("Cannot set valence information for a non-element!");
                    }
                }
                
                if(line.length() >= 39){
                String chargeCodeString = line.substring(36,39).trim();
                logger.debug("Atom charge code: ", chargeCodeString);
                int chargeCode = Integer.parseInt(chargeCodeString);
                if (chargeCode == 0) {
                    // uncharged species
                } else if (chargeCode == 1) {
                    atom.setFormalCharge(+3);
                } else if (chargeCode == 2) {
                        atom.setFormalCharge(+2);
                } else if (chargeCode == 3) {
                        atom.setFormalCharge(+1);
                } else if (chargeCode == 4) {
                } else if (chargeCode == 5) {
                        atom.setFormalCharge(-1);
                } else if (chargeCode == 6) {
                        atom.setFormalCharge(-2);
                } else if (chargeCode == 7) {
                        atom.setFormalCharge(-3);
                }
                } else {
                    handleError("Atom charge is missing", linecount, 36, 39);
                }
                
                try {
                    // read the mmm field as position 61-63
                    String reactionAtomIDString = line.substring(60,63).trim();
                    logger.debug("Parsing mapping id: ", reactionAtomIDString);
                    try {
                        int reactionAtomID = Integer.parseInt(reactionAtomIDString);
                        if (reactionAtomID != 0) {
                            atom.setProperty(CDKConstants.ATOM_ATOM_MAPPING, reactionAtomID);
                        }
                    } catch (Exception exception) {
                        logger.error("Mapping number ", reactionAtomIDString, " is not an integer.");
                        logger.debug(exception);
                    }
                } catch (Exception exception) {
                    // older mol files don't have all these fields...
                    logger.warn("A few fields are missing. Older MDL MOL file?");
                }
                
                //shk3: This reads shifts from after the molecule. I don't think this is an official format, but I saw it frequently 80=>78 for alk
                if(line.length()>=78){
                	double shift=Double.parseDouble(line.substring(69,80).trim());
                	atom.setProperty("first shift", shift);
                }
                if(line.length()>=87){
                	double shift=Double.parseDouble(line.substring(79,87).trim());
                	atom.setProperty("second shift", shift);
                }
                atomList.add(atom);
                atomsByLinePosition.add(atom);
            }
            
            // convert to 2D, if totalZ == 0
            if (totalX == 0.0 && totalY == 0.0 && totalZ == 0.0) {
                logger.info("All coordinates are 0.0");
                if(atomList.size()==1) {
                    atomList.get(0).setPoint2d(new Point2d(x,y));              
                }else{
                    for (IAtom atomToUpdate : atomList) {
                        atomToUpdate.setPoint3d(null);
                    }
                }
            } else if (totalZ == 0.0 && !forceReadAs3DCoords.isSet()) {
                logger.info("Total 3D Z is 0.0, interpreting it as a 2D structure");
                for (IAtom atomToUpdate : atomList) {
                    Point3d p3d = atomToUpdate.getPoint3d();
                    if (p3d != null) {
                        atomToUpdate.setPoint2d(new Point2d(p3d.x, p3d.y));
                        atomToUpdate.setPoint3d(null);
                    }
                }
            }
            
            // read BOND block
            logger.info("Reading bond block");
		    int queryBondCount=0;
            for (int f = 0; f < bonds; f++) {
                line = input.readLine(); linecount++;
                atom1 = Integer.parseInt(line.substring(0, 3).trim());
                atom2 = Integer.parseInt(line.substring(3, 6).trim());
                order = Integer.parseInt(line.substring(6, 9).trim());
                if (line.length() >= 12) {
                	int mdlStereo = line.length() > 12
                		? Integer.parseInt(line.substring(9, 12).trim())
                		: Integer.parseInt(line.substring(9).trim());
                    if (mdlStereo == 1) {
                        // MDL up bond
                        stereo = IBond.Stereo.UP;
                    } else if (mdlStereo == 6) {
                        // MDL down bond
                        stereo = IBond.Stereo.DOWN;
                    } else if (mdlStereo == 0) {
                    	if (order == 2) {
                    		// double bond stereo defined by coordinates
                    		stereo = IBond.Stereo.E_Z_BY_COORDINATES;
                    	} else {
                    		// bond has no stereochemistry
                    		stereo = IBond.Stereo.NONE;
                    	}
                    } else if (mdlStereo == 3 && order == 2) {
                        // unknown E/Z stereochemistry
                        stereo = IBond.Stereo.E_OR_Z;
                    } else if (mdlStereo == 4) {
                        //MDL bond undefined
                        stereo = IBond.Stereo.UP_OR_DOWN;
                    }
                } else {
                	handleError(
                	    "Missing expected stereo field at line: ",
                	    linecount, 10, 12
                	);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Bond: " + atom1 + " - " + atom2 + "; order " + order);
                }
                // interpret CTfile's special bond orders
                IAtom a1 = atomList.get(atom1 - 1);
                IAtom a2 = atomList.get(atom2 - 1);
                IBond newBond = null;
                if (order >= 1 && order <= 3) {
                    IBond.Order cdkOrder = IBond.Order.SINGLE;
                    if (order == 2) cdkOrder = IBond.Order.DOUBLE;
                    if (order == 3) cdkOrder = IBond.Order.TRIPLE;
                    if (stereo != null) {
                        newBond = molecule.getBuilder().newInstance(IBond.class,a1, a2, cdkOrder, stereo);
                    } else {
                        newBond = molecule.getBuilder().newInstance(IBond.class,a1, a2, cdkOrder);
                    }
                } else if (order == 4) {                
                    // aromatic bond                	
                    if (stereo != null) {
                        newBond = molecule.getBuilder().newInstance(IBond.class,a1, a2, IBond.Order.UNSET, stereo);
                    } else {
                        newBond = molecule.getBuilder().newInstance(IBond.class,a1, a2, IBond.Order.UNSET);
                    }
                    // mark both atoms and the bond as aromatic and raise the SINGLE_OR_DOUBLE-flag
                    newBond.setFlag(CDKConstants.SINGLE_OR_DOUBLE, true);
                	newBond.setFlag(CDKConstants.ISAROMATIC, true);
                    a1.setFlag(CDKConstants.ISAROMATIC, true);
                    a2.setFlag(CDKConstants.ISAROMATIC, true);
                }
                else {
                    queryBondCount++;
                    newBond = new CTFileQueryBond(molecule.getBuilder());
                    IAtom[] bondAtoms = {a1,a2};
                    newBond.setAtoms(bondAtoms);
                    newBond.setOrder(null);
                    CTFileQueryBond.Type queryBondType=null;
                    switch (order) {
                        case 5: queryBondType = CTFileQueryBond.Type.SINGLE_OR_DOUBLE; break;
                        case 6: queryBondType = CTFileQueryBond.Type.SINGLE_OR_AROMATIC; break;
                        case 7: queryBondType = CTFileQueryBond.Type.DOUBLE_OR_AROMATIC; break;
                        case 8: queryBondType = CTFileQueryBond.Type.ANY;  break;
                    }
                    ((CTFileQueryBond)newBond).setType(queryBondType);
                    newBond.setStereo(stereo);
                }
                bondList.add((newBond));
                
                // add the bond order to the explicit valence for each atom
                if (newBond.getOrder() != null && newBond.getOrder() != IBond.Order.UNSET) {
                    explicitValence[atom1 - 1] += newBond.getOrder().numeric();
                    explicitValence[atom2 - 1] += newBond.getOrder().numeric();
                } else {
                    explicitValence[atom1 - 1] = Integer.MIN_VALUE;
                    explicitValence[atom2 - 1] = Integer.MIN_VALUE;
                }
            }

            if(queryBondCount==0)          
                outputContainer = molecule;
            else {
                outputContainer = new QueryAtomContainer(molecule.getBuilder());
            }

            outputContainer.setProperty(CDKConstants.TITLE, title);
            outputContainer.setProperty(CDKConstants.REMARK, remark);
            for(IAtom at : atomList) {
                outputContainer.addAtom(at);
            }
            for(IBond bnd : bondList) {
                outputContainer.addBond(bnd);
            }
            
            // read PROPERTY block
            logger.info("Reading property block");
            while (true) {
                line = input.readLine(); linecount++;
                if (line == null) {
                    handleError(
                        "The expected property block is missing!",
                        linecount, 0, 0
                    );
                }
                if (line.startsWith("M  END")) break;
                
                boolean lineRead = false;
                if (line.startsWith("M  CHG")) {
                    // FIXME: if this is encountered for the first time, all
                    // atom charges should be set to zero first!
                    int infoCount = Integer.parseInt(line.substring(6,9).trim());
                    StringTokenizer st = new StringTokenizer(line.substring(9));
                    for (int i=1; i <= infoCount; i++) {
                        String token = st.nextToken();
                        int atomNumber = Integer.parseInt(token.trim());
                        token = st.nextToken();
                        int charge = Integer.parseInt(token.trim());
                        outputContainer.getAtom(atomNumber - 1).setFormalCharge(charge);
                    }
                }  else if (line.matches("A\\s{1,4}\\d+")) {
            		// Reads the pseudo atom property from the mol file
                	
                	// The atom number of the to replaced atom
            		int aliasAtomNumber = Integer.parseInt(line.replaceFirst("A\\s{1,4}", "")) - RGroupCounter;
            		line = input.readLine(); linecount++;
					String[] aliasArray = line.split("\\\\");
					// name of the alias atom like R1 or R2 etc. 
					String alias = "";
					for (int i = 0; i < aliasArray.length; i++) {
						alias += aliasArray[i];
					}
					IAtom aliasAtom = outputContainer.getAtom(aliasAtomNumber);
                    
                    // skip if already a pseudoatom
                    if(aliasAtom instanceof IPseudoAtom){
                        ((IPseudoAtom) aliasAtom).setLabel(alias);
                        continue;
                    }

					IAtom newPseudoAtom = molecule.getBuilder().newInstance(IPseudoAtom.class,alias);
					if(aliasAtom.getPoint2d() != null) {
						newPseudoAtom.setPoint2d(aliasAtom.getPoint2d());
					}
					if(aliasAtom.getPoint3d() != null) {
						newPseudoAtom.setPoint3d(aliasAtom.getPoint3d());
					}
					outputContainer.addAtom(newPseudoAtom);
					List<IBond> bondsOfAliasAtom = outputContainer.getConnectedBondsList(aliasAtom);
					
					for (int i = 0; i < bondsOfAliasAtom.size(); i++) {
						IBond bondOfAliasAtom = bondsOfAliasAtom.get(i);
						IAtom connectedToAliasAtom = bondOfAliasAtom.getConnectedAtom(aliasAtom);
						IBond newBond = bondOfAliasAtom.getBuilder().newInstance(IBond.class); 
						newBond.setAtoms(new IAtom[] {connectedToAliasAtom, newPseudoAtom});
						newBond.setOrder(bondOfAliasAtom.getOrder());
						outputContainer.addBond(newBond);
						outputContainer.removeBond(aliasAtom, connectedToAliasAtom);
					}
					outputContainer.removeAtom(aliasAtom);
					RGroupCounter++;

                } else if (line.startsWith("M  ISO")) {
                    try {
                        String countString = line.substring(6,10).trim();
                        int infoCount = Integer.parseInt(countString);
                        StringTokenizer st = new StringTokenizer(line.substring(10));
                        for (int i=1; i <= infoCount; i++) {
                            int atomNumber = Integer.parseInt(st.nextToken().trim());
                            int absMass = Integer.parseInt(st.nextToken().trim());
                            if (absMass != 0) { 
                                IAtom isotope = outputContainer.getAtom(atomNumber - 1);
                                isotope.setMassNumber(absMass);
                            }
                        }
                    } catch (NumberFormatException exception) {
                        String error = "Error (" + exception.getMessage() + ") while parsing line "
                                       + linecount + ": " + line + " in property block.";
                        logger.error(error);
                        handleError(
                            "NumberFormatException in isotope information.",
                            linecount, 7, 11,
                            exception
                        );
                    }
                } else if (line.startsWith("M  RAD")) {
					try {
						String countString = line.substring(6, 9).trim();
						int infoCount = Integer.parseInt(countString);
						StringTokenizer st = new StringTokenizer(line.substring(9));
						for (int i = 1; i <= infoCount; i++) {
							int atomNumber = Integer.parseInt(st.nextToken().trim());
							int spinMultiplicity = Integer.parseInt(st.nextToken().trim());
							MDLV2000Writer.SPIN_MULTIPLICITY spin = MDLV2000Writer.SPIN_MULTIPLICITY.NONE;
							if (spinMultiplicity > 0) {
								IAtom radical = outputContainer.getAtom(atomNumber - 1);
								switch (spinMultiplicity) {
									case 1:
										spin = MDLV2000Writer.SPIN_MULTIPLICITY.DOUBLET;
										break;
									case 2:
										spin = MDLV2000Writer.SPIN_MULTIPLICITY.SINGLET;
										break;
									case 3:
										spin = MDLV2000Writer.SPIN_MULTIPLICITY.TRIPLET;
										break;
									default:
										logger.debug("Invalid spin multiplicity found: " + spinMultiplicity);
										break;
								}
								for (int j = 0; j < spin.getSingleElectrons(); j++) {
									outputContainer.addSingleElectron(
											molecule.getBuilder().newInstance(ISingleElectron.class, radical));
								}
							}
						}
                    } catch (NumberFormatException exception) {
                        String error = "Error (" + exception.getMessage() + ") while parsing line "
                                       + linecount + ": " + line + " in property block.";
                        logger.error(error);
                        handleError(
                            "NumberFormatException in radical information",
                            linecount, 7, 10,
                            exception
                        );
                    }
                } else if (line.startsWith("G  ")) {
                    try {
                        String atomNumberString = line.substring(3,6).trim();
                        int atomNumber = Integer.parseInt(atomNumberString);
                        //String whatIsThisString = line.substring(6,9).trim();
                    
                        String atomName = input.readLine();
                        
                        // convert Atom into a PseudoAtom
                        IAtom prevAtom = outputContainer.getAtom(atomNumber - 1);
                        IPseudoAtom pseudoAtom = molecule.getBuilder().newInstance(IPseudoAtom.class,atomName);
                        if (prevAtom.getPoint2d() != null) {
                            pseudoAtom.setPoint2d(prevAtom.getPoint2d());
                        }
                        if (prevAtom.getPoint3d() != null) {
                            pseudoAtom.setPoint3d(prevAtom.getPoint3d());
                        }
                        AtomContainerManipulator.replaceAtomByAtom(molecule, prevAtom, pseudoAtom);
                    } catch (NumberFormatException exception) {
                        String error = "Error (" + exception.toString() + ") while parsing line "
                        + linecount + ": " + line + " in property block.";
                        logger.error(error);
                        handleError(
                            "NumberFormatException in group information",
                            linecount, 4, 7,
                            exception
                        );
                    }
                } else if (line.startsWith("M  RGP")) {
                    StringTokenizer st = new StringTokenizer(line);
                    //Ignore first 3 tokens (overhead).
                    st.nextToken(); st.nextToken(); st.nextToken();
                    //Process the R group numbers as defined in RGP line.
                    while (st.hasMoreTokens()) {
                        Integer position = new Integer(st.nextToken());
                        Rnumber = new Integer(st.nextToken());
                        IPseudoAtom pseudoAtom = rAtoms.get(position);
                        if (pseudoAtom!=null)  {
                            pseudoAtom.setLabel("R"+Rnumber);
                        }
                    }
                }
                if (line.startsWith("V  ")) {
                    Integer atomNumber = new Integer(line.substring(3,6).trim());
                    IAtom atomWithComment = outputContainer.getAtom(atomNumber - 1);
                    atomWithComment.setProperty(CDKConstants.COMMENT, line.substring(7));
                }
                
                if (!lineRead) {
                    logger.warn("Skipping line in property block: ", line);
                }
            }                       

		    if (interpretHydrogenIsotopes.isSet()) {
		        fixHydrogenIsotopes(molecule, isotopeFactory);
		    }

            // note: apply the valence model last so that all fixes (i.e. hydrogen
            // isotopes) are in place
            for (int i = 0; i < atoms; i++) {
                applyMDLValenceModel(outputContainer.getAtom(i), explicitValence[i]);
            }

		} catch (CDKException exception) {
            String error = "Error while parsing line " + linecount + ": " + line + " -> " + exception.getMessage();
            logger.error(error);
            logger.debug(exception);
            throw exception;
		} catch (Exception exception) {
			exception.printStackTrace();
            String error = "Error while parsing line " + linecount + ": " + line + " -> " + exception.getMessage();
            logger.error(error);
            logger.debug(exception);
            handleError(
                "Error while parsing line: " + line,
                linecount, 0, 0,
                exception
            );
		}
		return  outputContainer;
	}

    /**
     * Applies the MDL valence model to atoms using the explicit valence
     * (bond order sum) and charge to determine the correct number of
     * implicit hydrogens. The model is not applied if the explicit valence
     * is less than 0 - this is the case when a query bond was read for an
     * atom.
     * 
     * @param atom            the atom to apply the model to 
     * @param explicitValence the explicit valence (bond order sum)
     */
    private void applyMDLValenceModel(IAtom atom, int explicitValence) {
        
        if (explicitValence < 0)
            return;
        
        if (atom.getValency() != null) {
            atom.setImplicitHydrogenCount(atom.getValency() - explicitValence);
        } else {
            Integer element = atom.getAtomicNumber();
            if (element == null)
                return;

            Integer charge  = atom.getFormalCharge();
            if (charge == null)
                charge = 0;
            
            int implicitValence = MDLValence.implicitValence(element, charge, explicitValence);
            atom.setValency(implicitValence);
            atom.setImplicitHydrogenCount(implicitValence - explicitValence);
        }
    }

    private void fixHydrogenIsotopes(IAtomContainer molecule, IsotopeFactory isotopeFactory) {
		Iterator<IAtom> atoms = molecule.atoms().iterator();
		while (atoms.hasNext()) {
			IAtom atom = atoms.next();
			if (atom instanceof IPseudoAtom) {
				IPseudoAtom pseudo = (IPseudoAtom) atom;
                if ("D".equals(pseudo.getLabel())) {
					IAtom newAtom = molecule.getBuilder().newInstance(IAtom.class,atom);
					newAtom.setSymbol("H");
                    newAtom.setAtomicNumber(1);
					isotopeFactory.configure(newAtom, isotopeFactory.getIsotope("H", 2));
					AtomContainerManipulator.replaceAtomByAtom(molecule, atom, newAtom);
                } else if ("T".equals(pseudo.getLabel())) {
                    IAtom newAtom = molecule.getBuilder().newInstance(IAtom.class,atom);
					newAtom.setSymbol("H");
                    newAtom.setAtomicNumber(1);
				    isotopeFactory.configure(newAtom, isotopeFactory.getIsotope("H", 3));
					AtomContainerManipulator.replaceAtomByAtom(molecule, atom, newAtom);
				}
			}
		}
	}

    @TestMethod("testClose")
    public void close() throws IOException {
        input.close();
    }
    
    private void initIOSettings() {
        forceReadAs3DCoords = addSetting(new BooleanIOSetting("ForceReadAs3DCoordinates", IOSetting.Importance.LOW,
          "Should coordinates always be read as 3D?", 
          "false"));
        interpretHydrogenIsotopes = addSetting(new BooleanIOSetting("InterpretHydrogenIsotopes", IOSetting.Importance.LOW,
          "Should D and T be interpreted as hydrogen isotopes?",
          "true"));
    }
    
    public void customizeJob() {
        for(IOSetting setting : getSettings()){
            fireIOSettingQuestion(setting);
        }
    }

    public List<IAtom> getAtomsByLinePosition() {
        return atomsByLinePosition;
    }

    private String removeNonDigits(String input) {
        StringBuffer buffer = new StringBuffer();
        for (int i=0; i<input.length(); i++) {
            char inputChar = input.charAt(i);
            if (Character.isDigit(inputChar))
                buffer.append(inputChar);
        }
        return buffer.toString();
    }
}

