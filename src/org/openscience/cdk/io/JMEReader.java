/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.StringTokenizer;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.jmol.adapter.smarter.SmarterModelAdapter;
import org.jmol.api.ModelAdapter;
import org.jmol.api.ModelAdapter.AtomIterator;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.IsotopeFactory;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Reads a molecule from an JME file using Jmol's JME reader.
 *
 * @cdk.module experimental
 *
 * @author           Egon Willighagen
 * @cdk.created      2004-05-18
 * @cdk.keyword      file format, JME
 * @cdk.builddepends jmolIO.jar
 * @cdk.builddepends jmolApis.jar
 * @cdk.depends      jmolIO.jar
 * @cdk.depends      jmolApis.jar
 */
public class JMEReader extends DefaultChemObjectReader {

    BufferedReader input = null;
    private LoggingTool logger = null;
    
    public JMEReader() {
        this(new StringReader(""));
    }
    
	public JMEReader(InputStream in) {
		this(new BufferedReader(new InputStreamReader(in)));
	}

	public JMEReader(Reader in) {
        logger = new LoggingTool(this);
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
	}

    public String getFormatName() {
        return "JME";
    }

    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    public boolean matches(int lineNumber, String line) {
        return false;
    }


	public ChemObject read(ChemObject object) throws CDKException {
        if (object instanceof Molecule) {
			return readMolecule((Molecule)object);
		} else {
			throw new CDKException("Only supported are Molecule.");
		}
	}

	/**
	 *  Read a Molecule from a file in MDL sd format
	 *
	 *@return    The Molecule that was read from the MDL file.
	 */
	private Molecule readMolecule(Molecule molecule) throws CDKException {
        ModelAdapter adapter = new SmarterModelAdapter(null);
        Object model = adapter.openBufferedReader("", input);
        AtomIterator atomIterator = adapter.getAtomIterator(model);
        while (atomIterator.hasNext()) {
            Atom atom = new Atom(atomIterator.getElementSymbol());
            atom.setX3d(atomIterator.x);
            atom.setY3d(atomIterator.y);
            atom.setZ3d(atomIterator.z);
            molecule.addAtom(atom);
        }
        BondIterator bondIterator = adapter.getBondIterator(model);
        while (bondIterator.hasNext()) {
            Object atom1 = bondIterator.getgetAtomUid1();
            Object atom2 = bondIterator.getgetAtomUid2();
            if (atom1 instanceof org.jmol.adapter.smarter.Atom) {
                org.jmol.adapter.smarter.Atom atomOne =
                    (org.jmol.adapter.smarter.Atom)atom1;
                org.jmol.adapter.smarter.Atom atomTwo =
                    (org.jmol.adapter.smarter.Atom)atom2;
                // now I'm stuck... no idea which atoms this bond
                // connects... consulting Miguel
            }
        }
		return molecule;
	}
    
    public void close() throws IOException {
        input.close();
    }
    
    public IOSetting[] getIOSettings() {
        IOSetting[] settings = new IOSetting[0];
        return settings;
    }
}

