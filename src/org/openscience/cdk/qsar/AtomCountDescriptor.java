/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2004  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
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
package org.openscience.cdk.qsar;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;

/**
 * Descriptor based on the number of atoms of a certain element type.
 *
 * @author     mfe4
 * @created    2004-11-13
 */
public class AtomCountDescriptor implements Descriptor {

    private String elementName = null;

    public AtomCountDescriptor() {}

    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1)
            throw new CDKException("AtomCount only expects one parameter");
        if (!(params[0] instanceof String))
            throw new CDKException("The parameter must be of type String");
        // ok, all should be fine
        elementName = (String)params[0];
    }
    public Object[] getParameters() {
        return null; // FIXME!!!
    }

    public Object calculate(AtomContainer container) {
        int atomCount = 0;
        Atom[] atoms = container.getAtoms();
        for (int i = 0; i < atoms.length; i++) {
            if (container.getAtomAt(i).getSymbol().equals(elementName)) {
                atomCount += 1;
            }
        }
        return new Integer(atomCount);
    }
    
    // we should also have something like these. The order of the String[]
    // return should match the expected order in setParameters;
    public String[] getParameterNames() {return null; /* FIXME */ };
    public Object getParameterType(String name) {return null; /* FIXME */ };
}

