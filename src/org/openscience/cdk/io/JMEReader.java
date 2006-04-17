/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolAdapter;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.JMEFormat;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.libio.jmol.Convertor;

/**
 * Reads a molecule from an JME file using Jmol's JME reader.
 *
 * @cdk.module       io-jmol
 *
 * @author           Egon Willighagen
 * @author           Miguel Howard
 * @cdk.created      2004-05-18
 * @cdk.keyword      file format, JME
 * @cdk.builddepends jmolIO.jar
 * @cdk.builddepends jmolApis.jar
 * @cdk.depends      jmolIO.jar
 * @cdk.depends      jmolApis.jar
 */
public class JMEReader extends DefaultChemObjectReader {

    BufferedReader input = null;
    
    public JMEReader() {
        this(new StringReader(""));
    }
    
	public JMEReader(InputStream in) {
		this(new InputStreamReader(in));
	}

	public JMEReader(Reader in) {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
	}

    public IChemFormat getFormat() {
        return new JMEFormat();
    }
    
    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

	public boolean accepts(Class classObject) {
		if (IMolecule.class.isInstance(classObject)) return true;
		return false;
	}

	public IChemObject read(IChemObject object) throws CDKException {
        if (object instanceof IMolecule) {
			return readMolecule((IMolecule)object);
		} else {
			throw new CDKException("Only supported are Molecule.");
		}
	}

	/**
	 *  Read a Molecule from a JME file.
	 *
	 *@return    The Molecule that was read from the MDL file.
	 */
	private IMolecule readMolecule(IMolecule molecule) throws CDKException {
        JmolAdapter adapter = new SmarterJmolAdapter(null);
        // note that it actually let's the adapter detect the format!
        Object model = adapter.openBufferedReader("", input);
        molecule.add( new Convertor().convert(model));
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

