/* $Revision$ $Author$ $Date$  
 *
 * Copyright (C) 1997-2008  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io;

import java.io.IOException;

import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;

/**
 * This class is the interface that all IO readers should implement.
 * Programs need only care about this interface for any kind of IO.
 * Currently, database IO and file IO is supported.
 *
 * <p>The easiest way to implement a new {@link IChemObjectReader} is to
 * subclass the {@link DefaultChemObjectReader}.
 *
 * @cdk.module  io
 * @cdk.githash
 *
 * @see DefaultChemObjectReader
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 **/
public interface IChemObjectIO {

    /**
     * Returns the {@link IResourceFormat} class for this IO class.
     */
    public IResourceFormat getFormat();

    /**
     * Returns whether the given {@link IChemObject} can be read or written.
     *
     * @param classObject {@link IChemObject} of which is tested if it can be handled.
     * @return true, if the {@link IChemObject} can be handled.
     */
    public boolean accepts(Class<? extends IChemObject> classObject);
    
    /**
     * Closes this IChemObjectIO's resources.
     *
     * @throws throws an {@link IOException} when the wrapper IO class cannot be closed.
     */
    @TestMethod("testClose")
    public void close() throws IOException;

    /**
     * Returns an array of {@link IOSetting}s defined by this IChemObjectIO class.
     *
     * @return the {@link IOSetting}s for this class.
     */
    public IOSetting[] getIOSettings();
    
    /**
     * Adds a {@link IChemObjectIOListener} to this IChemObjectIO.
     *
     * @param listener the reader listener to add.
     */
    public void addChemObjectIOListener(IChemObjectIOListener listener);

    /**
     * Removes a {@link IChemObjectIOListener} from this IChemObjectIO.
     *
     * @param listener the listener to be removed.
     */
    public void removeChemObjectIOListener(IChemObjectIOListener listener);
    
}

