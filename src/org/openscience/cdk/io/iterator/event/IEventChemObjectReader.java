/* $RCSfile$
 * $Author$
 * $Date$  
 * $Revision$
 *
 * Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io.iterator.event;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectIO;

/**
 * Interface for an iterating molecule reader. It allows to iterate over all molecules
 * in specific file format (e.g. CML), without reading them all into memory first. Suitable
 * for very large files, with thousands of molecules.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @see org.openscience.cdk.io.IChemObjectIO
 *
 * @author      Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created 2005-01-25
 */
public interface IEventChemObjectReader extends IChemObjectIO {
    
    public IAtomContainer getAtomContainer();

}

