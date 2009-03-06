/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2003-2007  The Jmol Development Team
 *                    2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io.random;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.setting.IOSetting;

/**
 * Abstract class that {@link IRandomAccessChemObjectReader}'s can implement to
 * have it take care of basic stuff, like managing the ReaderListeners.
 *
 * @cdk.module  io
 * @cdk.svnrev  $Revision$
 */
public abstract class DefaultRandomAccessChemObjectReader
    implements IRandomAccessChemObjectReader<IChemObject> {

    protected IChemObjectReader.Mode mode = IChemObjectReader.Mode.RELAXED;

    /**
     * Holder of reader event listeners.
     */
    private List<IChemObjectIOListener> listenerList = new ArrayList<IChemObjectIOListener>();

    public void addChemObjectIOListener(IChemObjectIOListener listener) {
        listenerList.add(listener);
    }

    public void removeChemObjectIOListener(IChemObjectIOListener listener) {
        listenerList.remove(listener);
    }

    /* Extra convenience methods */

    protected void fireIOSettingQuestion(IOSetting setting) {
        for (int i = 0; i < listenerList.size(); ++i) {
            IChemObjectIOListener listener = listenerList.get(i);
            listener.processIOSettingQuestion(setting);
        }
    }

    public IOSetting[] getIOSettings() {
        return new IOSetting[0];
    }

    public void setReaderMode(ISimpleChemObjectReader.Mode mode) {
        this.mode = mode;
    }

}
