/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2003  The Jmol Development Team
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package org.openscience.cdk.io;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.io.setting.*;
import org.openscience.cdk.io.listener.*;
import java.util.Vector;

/**
 * Abstract class that ChemObjectReader's can implement to have it
 * take care of basic stuff, like managing the ReaderListeners.
 */
public abstract class DefaultChemObjectWriter implements ChemObjectWriter {

    /**
     * Holder of reader event listeners.
     */
    private Vector listenerList = new Vector();

    public void addWriterListener(WriterListener listener) {
        listenerList.addElement(listener);
    }

    public void removeWriterListener(WriterListener listener) {
        listenerList.removeElement(listener);
    }

    public boolean accepts(ChemObject object) {
        // leave it up the write(ChemObject) to decide by default
        return true;
    }
    
    protected void fireWriterSettingQuestion(IOSetting setting) {
        for (int i = 0; i < listenerList.size(); ++i) {
            WriterListener listener = (WriterListener) listenerList.elementAt(i);            
            listener.processWriterSettingQuestion(setting);
        }
    }
}
