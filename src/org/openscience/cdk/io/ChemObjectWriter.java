/* $RCSfile$ 
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.io;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;

/**
 * This class is the interface that all IO readers should implement.
 * Programs need only care about this interface for any kind of IO.
 *
 * <p>Currently, database IO and file IO is supported. Internet IO is
 * expected.
 *
 * @cdk.module io
 *
 * @version  $Date$
 */
public interface ChemObjectWriter extends ChemObjectIO {

    /**
     * Writes the content of "object" to output
     *
     * @param  object    the object of which the content is outputed
     *
     * @exception CDKException is thrown if the output
     *            does not support the data in the object
     */
    public void write(ChemObject object) throws CDKException;

    /**
     * Returns an instance of a Class containing the most information.
     *
     * For example, the ShelX format can only output one crystal structure
     * at a time. Accordingly highestSupportedChemObject() will return
     * an instance of Crystal and not of ChemFile.
     */
    public ChemObject highestSupportedChemObject();

}
