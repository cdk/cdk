/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;

import javax.vecmath.Point3d;

import org.openscience.cdk.PhysicalConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.formats.GamessFormat;
import org.openscience.cdk.io.formats.IResourceFormat;


/**
 * A reader for GAMESS log file.
 * 
 * <p><b>Expected behaviour</b>: 
 * <br>The "GamessReader" object is able to read GAMESS output log file format. 
 * 
 * <p><b>Limitations</b>: <br>This reader was developed from a small set of 
 * example log files, and therefore, is not guaranteed to properly read all 
 * GAMESS output. If you have problems, please contact the author of this code, 
 * not the developers of GAMESS.
 * 
 * <!-- <p><b>State information</b>: <br> [] -->
 * <!-- <p><b>Dependencies</b>: <br> [all OS/Software/Hardware dependencies] -->
 * 
 * <p><b>Implementation</b>
 * <br>Available feature(s):
 * <ul>
 * 	<li><b>Molecular coordinates</b>: Each set of coordinates is added to the ChemFile in the order they are found.</li>
 * </ul>
 * Unavailable feature(s):
 * <ul>
 * <!--	<li><b>GAMESS version number</b>: The version number can be retrieved.</li> -->
 * <!--	<li><b>Point group symetry information</b>: The point group is associated with the set of molecules.</li> -->
 * <!--	<li><b>MOPAC charges</b>: The point group is associated with the set of molecules.</li> -->
 * 	<li><b>Energies</b>: They are associated with the previously read set of coordinates.</li>
 * 	<li><b>Normal coordinates of vibrations</b>: They are associated with the previously read set of coordinates.</li>
 * </ul>
 * 
 * <!-- <p><b>Security:</b> -->
 * 
 * <p><b>References</b>: 
 * <br><a href="http://www.msg.ameslab.gov/GAMESS/GAMESS.html">GAMESS</a> is a 
 * quantum chemistry program by Gordon research group atIowa State University.
 * 
 * @cdk.module  extra
 * @cdk.svnrev  $Revision$
 * @cdk.keyword Gamess
 * @cdk.keyword file format
 * @cdk.keyword output
 * @cdk.keyword log file
 * 
 * @author Bradley A. Smith
 * 
 * <!-- @see #GamessWriter(Reader) -->
 */
//TODO Update class comments with appropriate information.
//TODO Update "see" tag with reference to GamessWriter when it will be implemented.
//TODO Update "author" tag with appropriate information.
@TestClass("org.openscience.cdk.io.GamessReaderTest")
public class GamessReader extends DefaultChemObjectReader {
	
	/**
	 * Boolean constant used to specify that the coordinates are given in Bohr units.
	 */
	public static final boolean BOHR_UNIT = true;
	
	/**
	 * Double constant that contains the convertion factor from Bohr unit to 
	 * &Aring;ngstrom unit.
	 */
	//TODO Check the accuracy of this comment.
	public static final double BOHR_TO_ANGSTROM = 0.529177249;
	
	/**
	 * Boolean constant used to specify that the coordinates are given in &Aring;ngstrom units.
	 */
	public static final boolean ANGSTROM_UNIT = false;
	
	/**
	 * The "BufferedReader" object used to read data from the "file system" file.
	 * 
	 * @see	org.openscience.cdk.io.GamessReader#GamessReader(Reader)
	 */
	//TODO Improve field comment.
	//TODO Answer the question : When is it opened and when is it closed?
	private BufferedReader input;
	
	/**
	 * Constructs a new "GamessReader" object given a "Reader" object as input.
	 * 
	 * <p>The "Reader" object may be an instantiable object from the "Reader" 
	 * hierarchy.
	 * <br>For more detail about the "Reader" objects that are really accepted 
	 * by this "GamessReader" see <code>accepts(IChemObject)</code> method
	 * documentation.
	 * 
	 * @param	inputReader		The "Reader" object given as input parameter.
	 * 
	 * @see #accepts(Class)
	 * @see	java.io.Reader
	 *  
	 */
    public GamessReader(Reader inputReader) {
		this.input = new BufferedReader(inputReader);
    }
	
    public GamessReader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    public GamessReader() {
        this(new StringReader(""));
    }
    
	/* (non-Javadoc) (Javadoc is automaticly inherited from the link below)
	 * @see org.openscience.cdk.io.ChemObjectIO#accepts(org.openscience.cdk.ChemObject)
	 */
	//TODO Update comment with appropriate information to comply Constructor's documentation. 
	
    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return GamessFormat.getInstance();
    }
    
    @TestMethod("testSetReader_Reader")
    public void setReader(Reader reader) throws CDKException {
        this.input = new BufferedReader(input);
    }

    @TestMethod("testSetReader_InputStream")
    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

	@TestMethod("testAccepts")
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
		for (int i=0; i<interfaces.length; i++) {
			if (IChemFile.class.equals(interfaces[i])) return true;
		}
    Class superClass = classObject.getSuperclass();
    if (superClass != null) return this.accepts(superClass);
		return false;
	}

	/* (non-Javadoc) (Javadoc is automaticly inherited from the link below)
	 * @see org.openscience.cdk.io.ChemObjectReader#read(org.openscience.cdk.ChemObject)
	 */
	public IChemObject read(IChemObject object) throws CDKException {
		if (object instanceof IChemFile) {
			try {
				return (IChemObject) readChemFile((IChemFile)object);
			} catch (IOException e) {
				return null;
			}
		} else {
			throw new CDKException("Only supported is reading of ChemFile objects.");
		}
	}
	
	/**
	 * Reads data from the "file system" file through the use of the "input" 
	 * field, parses data and feeds the ChemFile object with the extracted data.
	 * 
	 * @return A ChemFile containing the data parsed from input.
	 * 
	 * @throws	IOException	may be thrown buy the <code>this.input.readLine()</code> instruction.
	 * 
	 * @see org.openscience.cdk.io.GamessReader#input
	 */
	//TODO Answer the question : Is this method's name appropriate (given the fact that it do not read a ChemFile object, but return it)? 
	private IChemFile readChemFile(IChemFile file) throws IOException {
		IChemSequence sequence = file.getBuilder().newChemSequence(); // TODO Answer the question : Is this line needed ?
		IChemModel model = file.getBuilder().newChemModel(); // TODO Answer the question : Is this line needed ?
		IMoleculeSet moleculeSet = file.getBuilder().newMoleculeSet();
		
		model.setMoleculeSet(moleculeSet); //TODO Answer the question : Should I do this?
		sequence.addChemModel(model); //TODO Answer the question : Should I do this?
		file.addChemSequence(sequence); //TODO Answer the question : Should I do this?
		
		String currentReadLine = this.input.readLine();
		while (this.input.ready() == true && (currentReadLine != null)) {
			
			/*
			 * There are 2 types of coordinate sets: 
			 * - bohr coordinates sets		(if statement)
			 * - angstr???m coordinates sets	(else statement)
			 */
			if (currentReadLine.indexOf("COORDINATES (BOHR)") >= 0) {
				
				/* 
				 * The following line do no contain data, so it is ignored.
				 */
				this.input.readLine();
				moleculeSet.addMolecule(this.readCoordinates(
					file.getBuilder().newMolecule(), GamessReader.BOHR_UNIT
			    ));
				//break; //<- stops when the first set of coordinates is found.
			} else if (currentReadLine.indexOf(" COORDINATES OF ALL ATOMS ARE (ANGS)") >= 0) {

				/* 
				 * The following 2 lines do no contain data, so it are ignored.
				 */
				this.input.readLine();
				this.input.readLine();

				moleculeSet.addMolecule(this.readCoordinates(
					file.getBuilder().newMolecule(), GamessReader.ANGSTROM_UNIT
				));
				//break; //<- stops when the first set of coordinates is found.
			}
			currentReadLine = this.input.readLine();
		}
		return file;
	}
	
	/**
	 * Reads a set of coordinates from the "file system" file through the use of 
	 * the "input" field, scales coordinate to angstr???m unit, builds each atom with 
	 * the right associated coordinates, builds a new molecule with these atoms
	 * and returns the complete molecule.
	 * 
	 * <p><b>Implementation</b>:
	 * <br>Dummy atoms are ignored.
	 * 
	 * @param	coordinatesUnits	The unit in which coordinates are given.
	 * 
	 * @throws	IOException	may be thrown by the "input" object.
	 * 
	 * @see org.openscience.cdk.io.GamessReader#input
	 */
	//TODO Update method comments with appropriate information.
	private IMolecule readCoordinates(IMolecule molecule, boolean coordinatesUnits) throws IOException {
		
		/*
		 * Coordinates must all be given in angstr???ms.
		 */ 
		double unitScaling = GamessReader.scalesCoordinatesUnits(coordinatesUnits);
		
		String retrievedLineFromFile;
		
		while (this.input.ready() == true) {
			retrievedLineFromFile = this.input.readLine();
			/* 
			 * A coordinate set is followed by an empty line, so when this line 
			 * is reached, there are no more coordinates to add to the current set.
			 */ 
			if ((retrievedLineFromFile == null) || (retrievedLineFromFile.trim().length() == 0)) {
				break;
			}
			
			int atomicNumber;
			String atomicSymbol;
			
			//StringReader sr = new StringReader(retrievedLineFromFile);
			StreamTokenizer token = new StreamTokenizer(new StringReader(retrievedLineFromFile));
			
			/*
			 * The first token is ignored. It contains the atomic symbol and may 
			 * be concatenated with a number.
			 */
			token.nextToken();
			
			if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
				atomicNumber = (int) token.nval;
				atomicSymbol = this.identifyAtomicSymbol(atomicNumber);
				/* 
				 * Dummy atoms are assumed to be given with an atomic number set
				 * to zero. We will do not add them to the molecule.
				 */
				if (atomicNumber == 0) {
					continue;
				}
			} else {
				throw new IOException("Error reading coordinates");
			}
			
			/*
			 * Atom's coordinates are stored in an array.
			 */
			double[] coordinates = new double[3]; 
			for (int i = 0; i < coordinates.length; i++) {
				if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
					coordinates[i] = token.nval * unitScaling;
				} else {
					throw new IOException("Error reading coordinates");
				}
			}
			IAtom atom = molecule.getBuilder().newAtom(atomicSymbol, new Point3d(coordinates[0],coordinates[1],coordinates[2]));
			molecule.addAtom(atom);
		}
		return molecule;
	}
	
	/**
	 * Identifies the atomic symbol of an atom given its default atomic number.
	 * 
	 * <p><b>Implementation</b>:
	 * <br>This is not a definitive method. It will probably be replaced with a 
	 * more appropriate one. Be advised that as it is not a definitive version, 
	 * it only recognise atoms from Hydrogen (1) to Argon (18).
	 * 
	 * @param	atomicNumber	The atomic number of an atom.
	 * 
	 * @return	The Symbol corresponding to the atom or "null" is the atom was not recognised.
	 */
	//TODO Update method comments with appropriate information.
	private String identifyAtomicSymbol(int atomicNumber) {
		String symbol;
		switch (atomicNumber) {
			case 1:
				symbol = "H";
				break;
			case 2:
				symbol = "He";
				break;
			case 3:
				symbol = "Li";
				break;
			case 4:
				symbol = "Be";
				break;
			case 5:
				symbol = "B";
				break;
			case 6:
				symbol = "C";
				break;
			case 7:
				symbol = "N";
				break;
			case 8:
				symbol = "O";
				break;
			case 9:
				symbol = "F";
				break;
			case 10:
				symbol = "Ne";
				break;
			case 11:
				symbol = "Na";
				break;
			case 12:
				symbol = "Mg";
				break;
			case 13:
				symbol = "Al";
				break;
			case 14:
				symbol = "Si";
				break;
			case 15:
				symbol = "P";
				break;
			case 16:
				symbol = "S";
				break;
			case 17:
				symbol = "Cl";
				break;
			case 18:
				symbol = "Ar";
				break;
			default:
				symbol = null;
				break; 
		}
		return symbol;
	}
	
	/**
	 * Scales coordinates to &Aring;ngstr&ouml;m unit if they are given in Bohr unit. 
	 * If coordinates are already given in &Aring;ngstr&ouml;m unit, then no modifications
	 * are performed.
	 * 
	 * @param	coordinatesUnits	<code>BOHR_UNIT</code> if coordinates are given in Bohr unit and <code>ANGSTROM_UNIT</code> 
     *                              if they are given in &Aring;ngstr&ouml;m unit.
	 * 
	 * @return	The scaling convertion factor: 1 if no scaling is needed and <code>BOHR_TO_ANGSTROM</code> if scaling has to be performed.
	 * 
	 * @see org.openscience.cdk.PhysicalConstants#BOHR_TO_ANGSTROM
	 * @see org.openscience.cdk.io.GamessReader#BOHR_UNIT
	 * @see org.openscience.cdk.io.GamessReader#ANGSTROM_UNIT
	 */
	//TODO Update method comments with appropriate information.
	private static double scalesCoordinatesUnits(boolean coordinatesUnits) {
		if (coordinatesUnits == GamessReader.BOHR_UNIT) {
			return PhysicalConstants.BOHR_TO_ANGSTROM;
		} else { //condition is: (coordinatesUnits == GamessReader.ANGTROM_UNIT)
			return (double) 1;
		}
	}
	
	/* (non-Javadoc) (Javadoc is automaticly inherited from the link below)
	 * @see org.openscience.cdk.io.ChemObjectIO#close()
	 */
	//TODO Answer the question : What are all concerned ressources ? 
	@TestMethod("testClose")
  public void close() throws IOException {
		/* 
		 * Closes the BufferedReader used to read the file content.
		 */ 
		input.close();
	}    
}