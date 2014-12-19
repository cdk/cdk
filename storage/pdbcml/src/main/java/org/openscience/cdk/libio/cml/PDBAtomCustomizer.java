/*  Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
 *                     2013  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.libio.cml;

import nu.xom.Attribute;
import nu.xom.Element;

import org.openscience.cdk.interfaces.IPDBAtom;
import org.xmlcml.cml.element.CMLScalar;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * {@link ICMLCustomizer} for the libio-cml {@link Convertor} to be able to export details for
 * {@link IPDBAtom}'s.
 *
 * @author        egonw
 * @cdk.created   2005-05-04
 * @cdk.module    pdbcml
 * @cdk.githash
 * @cdk.set       libio-cml-customizers
 * @cdk.require   java1.5+
 */
public class PDBAtomCustomizer implements ICMLCustomizer {

    @Override
    public void customize(IAtom atom, Object nodeToAdd) throws Exception {
        if (!(nodeToAdd instanceof Element)) throw new CDKException("NodeToAdd must be of type nu.xom.Element!");

        Element element = (Element) nodeToAdd;
        if (atom instanceof IPDBAtom) {
            IPDBAtom pdbAtom = (IPDBAtom) atom;
            if (hasContent(pdbAtom.getAltLoc())) {
                CMLScalar scalar = new CMLScalar();
                scalar.addAttribute(new Attribute("dictRef", "pdb:altLoc"));
                scalar.appendChild(pdbAtom.getAltLoc());
                element.appendChild(scalar);
            }

            if (hasContent(pdbAtom.getChainID())) {
                Element scalar = new CMLScalar();
                scalar.addAttribute(new Attribute("dictRef", "pdb:chainID"));
                scalar.appendChild(pdbAtom.getChainID());
                element.appendChild(scalar);
            }

            {
                Element scalar = new CMLScalar();
                scalar.addAttribute(new Attribute("dictRef", "pdb:hetAtom"));
                scalar.appendChild("" + pdbAtom.getHetAtom());
                element.appendChild(scalar);
            }

            if (hasContent(pdbAtom.getICode())) {
                Element scalar = new CMLScalar();
                scalar.addAttribute(new Attribute("dictRef", "pdb:iCode"));
                scalar.appendChild(pdbAtom.getICode());
                element.appendChild(scalar);
            }

            if (hasContent(pdbAtom.getName())) {
                Element scalar = new Element("label");
                scalar.addAttribute(new Attribute("dictRef", "pdb:name"));
                scalar.appendChild(pdbAtom.getName());
                element.appendChild(scalar);
            }

            {
                Element scalar = new CMLScalar();
                scalar.addAttribute(new Attribute("dictRef", "pdb:oxt"));
                scalar.appendChild("" + pdbAtom.getOxt());
                element.appendChild(scalar);
            }

            if (hasContent(pdbAtom.getRecord())) {
                Element scalar = new CMLScalar();
                scalar.addAttribute(new Attribute("dictRef", "pdb:record"));
                scalar.appendChild(pdbAtom.getRecord());
                element.appendChild(scalar);
            }

            if (hasContent(pdbAtom.getResName())) {
                Element scalar = new CMLScalar();
                scalar.addAttribute(new Attribute("dictRef", "pdb:resName"));
                scalar.appendChild(pdbAtom.getResName());
                element.appendChild(scalar);
            }

            if (hasContent(pdbAtom.getResSeq())) {
                Element scalar = new CMLScalar();
                scalar.addAttribute(new Attribute("dictRef", "pdb:resSeq"));
                scalar.appendChild(pdbAtom.getResSeq());
                element.appendChild(scalar);
            }

            if (hasContent(pdbAtom.getSegID())) {
                Element scalar = new CMLScalar();
                scalar.addAttribute(new Attribute("dictRef", "pdb:segID"));
                scalar.appendChild(pdbAtom.getSegID());
                element.appendChild(scalar);
            }

            if (pdbAtom.getSerial() != 0) {
                Element scalar = new CMLScalar();
                scalar.addAttribute(new Attribute("dictRef", "pdb:serial"));
                scalar.appendChild("" + pdbAtom.getSerial());
                element.appendChild(scalar);
            }

            if (pdbAtom.getTempFactor() != -1.0) {
                Element scalar = new CMLScalar();
                scalar.addAttribute(new Attribute("dictRef", "pdb:tempFactor"));
                scalar.appendChild("" + pdbAtom.getTempFactor());
                element.appendChild(scalar);
            }

            element.addAttribute(new Attribute("occupancy", "" + pdbAtom.getOccupancy()));

            // remove isotope info
            Attribute isotopeInfo = element.getAttribute("isotopeNumber");
            if (isotopeInfo != null) element.removeAttribute(isotopeInfo);
        }
    }

    private boolean hasContent(String string) {
        return string != null && string.trim().length() > 0;
    }

    @Override
    public void customize(IAtomContainer molecule, Object nodeToAdd) throws Exception {
        // nothing to do at this moment
    }

    @Override
    public void customize(IBond bond, Object nodeToAdd) throws Exception {
        // nothing to do at this moment
    }
}
