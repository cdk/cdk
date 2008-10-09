/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2004-2007  Rajarshi Guha <rajarshi@presidency.com>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.formats.HINFormat;
import org.openscience.cdk.io.formats.IResourceFormat;

/**
 * Reads an object from HIN formated input.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author  Rajarshi Guha <rajarshi@presidency.com>
 * @cdk.created 2004-01-27
 *
 * @cdk.keyword file format, HIN 
 */
@TestClass("org.openscience.cdk.io.HINReaderTest")
public class HINReader extends DefaultChemObjectReader {

    private BufferedReader input;

    /**
     * Construct a new reader from a Reader type object
     *
     * @param input reader from which input is read
     */
    public HINReader(Reader input) {
        this.input = new BufferedReader(input);
    }

    public HINReader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    public HINReader() {
        this(new StringReader(""));
    }
    
    @TestMethod("testGetFormat")
    public IResourceFormat getFormat() {
        return HINFormat.getInstance();
    }

    @TestMethod("testClose")
    public void close() throws IOException {
        input.close();
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
    public boolean accepts(Class classObject) {
		Class[] interfaces = classObject.getInterfaces();
        for (Class anInterface : interfaces) {
            if (IChemFile.class.equals(anInterface)) return true;
        }
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
		return false;
	}

    /**
     * Reads the content from a HIN input. It can only return a
     * IChemObject of type ChemFile
     *
     * @param object class must be of type ChemFile
     *
     * @see org.openscience.cdk.ChemFile
     */
    public IChemObject read(IChemObject object) throws CDKException {
        if (object instanceof IChemFile) {
            return readChemFile((IChemFile)object);
        } else {
            throw new CDKException("Only supported is reading of ChemFile objects.");
        }
    }

    private String getMolName( String line) {
        if (line == null) return("");
        StringTokenizer st = new StringTokenizer(line," ");
        int ntok = st.countTokens();
        String[] toks = new String[ ntok ];
        for (int j = 0; j < ntok; j++) {
            toks[j] = st.nextToken();
        }
        if (toks.length == 3) return(toks[2]);
        else return("");
    }
    
    /**
     *  Private method that actually parses the input to read a ChemFile
     *  object. In its current state it is able to read all the molecules
     *  (if more than one is present) in the specified HIN file. These are
     *  placed in a MoleculeSet object which in turn is placed in a ChemModel
     *  which in turn is placed in a ChemSequence object and which is finally 
     *  placed in a ChemFile object and returned to the user.
     *
     * @return A ChemFile containing the data parsed from input.
     */
    private IChemFile readChemFile(IChemFile file) {
        IChemSequence chemSequence = file.getBuilder().newChemSequence();
        IChemModel chemModel = file.getBuilder().newChemModel();
        IMoleculeSet setOfMolecules = file.getBuilder().newMoleculeSet();
        String info;

        StringTokenizer tokenizer;

        try {
            String line;

            // read in header info
            while (true) {
                line = input.readLine();
                if (line.indexOf("mol ") == 0) {
                    info = getMolName(line);
                    break;
                }
            }


            // start the actual molecule data - may be multiple molecule
            line = input.readLine();
            while(true) {
                if (line == null) break; // end of file
                if (line.indexOf(';') == 0) continue; // comment line

                if (line.indexOf("mol ") == 0) {
                    info = getMolName(line);
                    line = input.readLine();
                }
                IMolecule m = file.getBuilder().newMolecule();
                m.setProperty(CDKConstants.TITLE ,info);

                // Each element of cons is an ArrayList of length 3 which stores
                // the start and end indices and bond order of each bond 
                // found in the HIN file. Before adding bonds we need to reduce
                // the number of bonds so as not to count the same bond twice
                List<List<Object>> cons = new ArrayList<List<Object>>();

                // read data for current molecule
                int atomSerial = 0;
                while (true) {
                    if (line.indexOf("endmol ") >= 0) {
                        break;
                    }
                    if (line.indexOf(';') == 0) continue; // comment line

                    tokenizer = new StringTokenizer(line, " ");

                    int ntoken = tokenizer.countTokens();
                    String[] toks = new String[ ntoken ];
                    for (int i = 0; i < ntoken; i++) toks[i] = tokenizer.nextToken();

                    String sym = toks[3];
                    double charge = Double.parseDouble(toks[6]);
                    double x = Double.parseDouble(toks[7]);
                    double y = Double.parseDouble(toks[8]);
                    double z = Double.parseDouble(toks[9]);
                    int nbond = Integer.parseInt(toks[10]);

                    IAtom atom = file.getBuilder().newAtom(sym, new Point3d(x,y,z));
                    atom.setCharge(charge);

                    IBond.Order bo = IBond.Order.SINGLE;

                    for (int j = 11; j < (11+nbond*2); j += 2) {
                        int s = Integer.parseInt(toks[j]) - 1; // since atoms start from 1 in the file
                        char bt = toks[j+1].charAt(0);
                        switch(bt) {
                            case 's': 
                                bo = IBond.Order.SINGLE;
                                break;
                            case 'd': 
                                bo = IBond.Order.DOUBLE;
                                break;
                            case 't': 
                                bo = IBond.Order.TRIPLE;
                                break;      
                            case 'a': 
                                bo = IBond.Order.QUADRUPLE;
                                break;
                        }
                        List<Object> ar = new ArrayList<Object>(3);
                        ar.add(atomSerial);
                        ar.add(s);
                        ar.add(bo);
                        cons.add( ar );
                    }
                    m.addAtom(atom);
                    atomSerial++;
                    line = input.readLine();
                }
                
                // now just store all the bonds we have
                for (List<Object> ar : cons) {
                    IAtom s = m.getAtom((Integer) ar.get(0));
                    IAtom e = m.getAtom((Integer) ar.get(1));
                    IBond.Order bo = (IBond.Order) ar.get(2);
                    if (!isConnected(m, s, e))
                        m.addBond(file.getBuilder().newBond(s, e, bo));
                }
                setOfMolecules.addMolecule(m);
                line = input.readLine(); // read in the 'mol N'
            }

            // got all the molecule in the HIN file (hopefully!)
            chemModel.setMoleculeSet(setOfMolecules);
            chemSequence.addChemModel(chemModel);
            file.addChemSequence(chemSequence);

        } catch (IOException e) {
            // FIXME: should make some noise now
            file = null;
        }
        return file;
    }

    private boolean isConnected(IAtomContainer atomContainer, IAtom atom1, IAtom atom2) {
        for (IBond bond : atomContainer.bonds()) {
            if (bond.contains(atom1) && bond.contains(atom2)) return true;
        }
        return false;
    }
}


    

