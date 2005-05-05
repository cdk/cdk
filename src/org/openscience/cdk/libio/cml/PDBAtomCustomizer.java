/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.libio.cml;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.w3c.dom.Element;

/**
 * Customizer for the libio-cml Convertor to be able to export details for
 * PDBAtom's.
 *
 * @author        egonw
 * @cdk.created   2005-05-04
 * @cdk.module    pdb-cml
 * @cdk.set       libio-cml-customizers
 */
public class PDBAtomCustomizer implements Customizer {

    public void customize(Object object, Atom atom, Element nodeToAdd) throws Exception {
        // nothing to do at this moment
    }

    public void customize(Object object, Molecule molecule, Element nodeToAdd) throws Exception {
        // nothing to do at this moment
    }
}

