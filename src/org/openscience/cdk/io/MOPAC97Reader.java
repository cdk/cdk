/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307  USA.
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
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.ChemFormat;
import org.openscience.cdk.io.formats.MOPAC97Format;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.libio.jmol.Convertor;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Reader for MOPAC 93, 97 and 2002 files. Only tested for MOPAC 93 files.
 * It uses Jmol IO classes.
 *
 * @cdk.module       io-jmol
 * @cdk.keyword      file format, MOPAC
 * @cdk.builddepends jmolIO.jar
 * @cdk.builddepends jmolApis.jar
 * @cdk.depends      jmolIO.jar
 * @cdk.depends      jmolApis.jar
 */
public class MOPAC97Reader extends DefaultChemObjectReader {

    BufferedReader input = null;
    private LoggingTool logger = null;
    
    public MOPAC97Reader() {
        this(new StringReader(""));
    }
    
	public MOPAC97Reader(InputStream in) {
		this(new BufferedReader(new InputStreamReader(in)));
	}

	public MOPAC97Reader(Reader in) {
        logger = new LoggingTool(this);
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
	}

    public ChemFormat getFormat() {
        return new MOPAC97Format();
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

	public ChemObject read(ChemObject object) throws CDKException {
        if (object instanceof Molecule) {
			return readMolecule((Molecule)object);
		} else {
			throw new CDKException("Only supported are Molecule.");
		}
	}

	private Molecule readMolecule(Molecule molecule) throws CDKException {
        JmolAdapter adapter = new SmarterJmolAdapter(null);
        // note that it actually let's the adapter detect the format!
        Object model = adapter.openBufferedReader("", input);
        AtomContainer container = new Convertor().convert(model);
		return new Molecule(container);
	}
    
    public void close() throws IOException {
        input.close();
    }
    
    public IOSetting[] getIOSettings() {
        IOSetting[] settings = new IOSetting[0];
        return settings;
    }
}
