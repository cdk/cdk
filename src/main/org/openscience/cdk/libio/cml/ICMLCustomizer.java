/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Interface for classes that allow the Convertor to be customized for certain
 * features. The idea here is that the CDK build and runtime dependencies for
 * the Convertor are lowered. For example, QSAR descriptor support and PDBAtom
 * support can be written as <code>Customizer</code>s.
 *
 * @author        egonw
 * @cdk.created   2005-05-04
 * @cdk.module    standard
 * @cdk.svnrev  $Revision$
 */
public interface ICMLCustomizer {

	/**
	 * Customized the nodeToAdd for the given Atom.
	 * 
	 * @param atom       Atom to base the customization on 
	 * @param nodeToAdd  XOM Element to customize
	 * @throws Exception thrown if nodeToAdd is not an instance of nu.xom.Element
	 */
    public void customize(IAtom atom, Object nodeToAdd) throws Exception;
    
	/**
	 * Customized the nodeToAdd for the given IBond.
	 * 
	 * @param bond       Bond to base the customization on 
	 * @param nodeToAdd  XOM Element to customize
	 * @throws Exception thrown if nodeToAdd is not an instance of nu.xom.Element
	 */
    public void customize(IBond bond, Object nodeToAdd) throws Exception;
    
    /**
     * Customized the nodeToAdd for the given Molecule.
     * 
     * @param molecule   Molecule to base the customization on 
     * @param nodeToAdd  XOM Element to customize
     * @throws Exception thrown if nodeToAdd is not an instance of nu.xom.Element
     */
    public void customize(IAtomContainer molecule, Object nodeToAdd) throws Exception;
}

