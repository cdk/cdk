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
import org.openscience.cdk.PDBAtom;
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
        if (!(object instanceof Convertor)) {
            throw new CDKException("The convertor is not instanceof Convertor!");
        }
        Convertor convertor = (Convertor)object;
        
        if (atom instanceof PDBAtom) {
            PDBAtom pdbAtom = (PDBAtom)atom;
            if (pdbAtom.getAltLoc() != null) {
                Element scalar = convertor.createElement("scalar");
                scalar.setAttribute("dictRef", "pdb:altLoc");
                scalar.appendChild(convertor.createTextNode(pdbAtom.getAltLoc()));
                nodeToAdd.appendChild(scalar);
            }
            
            if (pdbAtom.getChainID() != null) {
                Element scalar = convertor.createElement("scalar");
                scalar.setAttribute("dictRef", "pdb:chainID");
                scalar.appendChild(convertor.createTextNode(pdbAtom.getChainID()));
                nodeToAdd.appendChild(scalar);
            }
            
            {
                Element scalar = convertor.createElement("scalar");
                scalar.setAttribute("dictRef", "pdb:hetAtom");
                scalar.appendChild(convertor.createTextNode("" + pdbAtom.getHetAtom()));
                nodeToAdd.appendChild(scalar);
            }
            
            if (pdbAtom.getICode() != null) {
                Element scalar = convertor.createElement("scalar");
                scalar.setAttribute("dictRef", "pdb:iCode");
                scalar.appendChild(convertor.createTextNode(pdbAtom.getICode()));
                nodeToAdd.appendChild(scalar);
            }
            
            if (pdbAtom.getName() != null) {
                Element scalar = convertor.createElement("label");
                scalar.setAttribute("dictRef", "pdb:name");
                scalar.appendChild(convertor.createTextNode(pdbAtom.getName()));
                nodeToAdd.appendChild(scalar);
            }
            
            {
                Element scalar = convertor.createElement("scalar");
                scalar.setAttribute("dictRef", "pdb:oxt");
                scalar.appendChild(convertor.createTextNode("" + pdbAtom.getOxt()));
                nodeToAdd.appendChild(scalar);
            }
            
            if (pdbAtom.getRecord() != null) {
                Element scalar = convertor.createElement("scalar");
                scalar.setAttribute("dictRef", "pdb:record");
                scalar.appendChild(convertor.createTextNode(pdbAtom.getRecord()));
                nodeToAdd.appendChild(scalar);
            }
            
            if (pdbAtom.getResName() != null) {
                Element scalar = convertor.createElement("scalar");
                scalar.setAttribute("dictRef", "pdb:resName");
                scalar.appendChild(convertor.createTextNode(pdbAtom.getResName()));
                nodeToAdd.appendChild(scalar);
            }
            
            if (pdbAtom.getResSeq() != null) {
                Element scalar = convertor.createElement("scalar");
                scalar.setAttribute("dictRef", "pdb:resSeq");
                scalar.appendChild(convertor.createTextNode(pdbAtom.getResSeq()));
                nodeToAdd.appendChild(scalar);
            }
            
            if (pdbAtom.getSegID() != null) {
                Element scalar = convertor.createElement("scalar");
                scalar.setAttribute("dictRef", "pdb:segID");
                scalar.appendChild(convertor.createTextNode(pdbAtom.getSegID()));
                nodeToAdd.appendChild(scalar);
            }
            
            if (pdbAtom.getSerial() != 0) {
                Element scalar = convertor.createElement("scalar");
                scalar.setAttribute("dictRef", "pdb:serial");
                scalar.appendChild(convertor.createTextNode("" + pdbAtom.getSerial()));
                nodeToAdd.appendChild(scalar);
            }
            
            if (pdbAtom.getTempFactor() != -1.0) {
                Element scalar = convertor.createElement("scalar");
                scalar.setAttribute("dictRef", "pdb:tempFactor");
                scalar.appendChild(convertor.createTextNode("" + pdbAtom.getTempFactor()));
                nodeToAdd.appendChild(scalar);
            }
            
            nodeToAdd.setAttribute("occupancy", "" + pdbAtom.getOccupancy());
        }
    }

    public void customize(Object object, Molecule molecule, Element nodeToAdd) throws Exception {
        // nothing to do at this moment
    }
}

