/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2004  The Jmol Development Team
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

import java.util.Vector;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.io.listener.ChemObjectIOListener;
import org.openscience.cdk.io.listener.ReaderListener;
import org.openscience.cdk.io.setting.IOSetting;

/**
 * Abstract class that ChemObjectReader's can implement to have it
 * take care of basic stuff, like managing the ReaderListeners.
 *
 * @cdk.module io
 */
public abstract class DefaultChemObjectReader implements ChemObjectReader {

    /**
     * An event to be sent to listeners when a frame is read.
     */
    private ReaderEvent frameReadEvent = null;

    /**
     * Holder of reader event listeners.
     */
    private Vector listenerList = new Vector();

    public void addChemObjectIOListener(ChemObjectIOListener listener) {
        listenerList.addElement(listener);
    }

    public void removeChemObjectIOListener(ChemObjectIOListener listener) {
        listenerList.removeElement(listener);
    }

    public boolean accepts(ChemObject object) {
        // leave it up the read(ChemObject) to decide by default
        return true;
    }
    
    /* Extra convenience methods */
    
    /**
     * Sends a frame read event to the registered ReaderListeners.
     */
    protected void fireFrameRead() {
        for (int i = 0; i < listenerList.size(); ++i) {
            ReaderListener listener = (ReaderListener) listenerList.elementAt(i);
            
            // Lazily create the event:
            if (frameReadEvent == null) {
                frameReadEvent = new ReaderEvent(this);
            }
            listener.frameRead(frameReadEvent);
        }
    }

    protected void fireIOSettingQuestion(IOSetting setting) {
        for (int i = 0; i < listenerList.size(); ++i) {
            ChemObjectIOListener listener = (ChemObjectIOListener) listenerList.elementAt(i);
            listener.processIOSettingQuestion(setting);
        }
    }

    public IOSetting[] getIOSettings() {
        return new IOSetting[0];
    }
    
}
