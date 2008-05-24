/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  The Jmol Development Team
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io.listener;

import java.awt.Component;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.ReaderEvent;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.OptionIOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;

/**
 * Allows processing of IOSetting quesions which are passed to the user
 * by using Swing dialogs.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created 2003-07-18
 * @cdk.require swing
 */
public class SwingGUIListener implements IReaderListener, IWriterListener {

    private Component frame = null;
    private int level = 0;
    
    /**
     * 0 = ask no questions
     * 3 = ask all questions
     */
    public SwingGUIListener(Component frame, int level) {
        this.level = level;
        this.frame = frame;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
        
    public void frameRead(ReaderEvent event) {
    }
    
    /**
     * Processes the IOSettings by listing the question, giving the options
     * and asking the user to provide their choice.
     *
     * <p>Note: if the input reader is <code>null</code>, then the method
     * does not wait for an answer, and takes the default.
     */
    public void processIOSettingQuestion(IOSetting setting) {
        // post the question
        if (setting.getLevel() < this.level) {
            String answer = setting.getSetting();
            
            if (setting instanceof BooleanIOSetting) {
                int n = JOptionPane.showConfirmDialog(frame,
                    setting.getQuestion(),
                    setting.getName(),
                    JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    answer = "true";
                } else if (n == JOptionPane.NO_OPTION) {
                    answer = "false";
                } else {
                    // default of setting
                }
            } else if (setting instanceof OptionIOSetting) {
                OptionIOSetting optionSetting = (OptionIOSetting)setting;
                List<String> settings = optionSetting.getOptions();
                Iterator<String> elements = settings.iterator();
                Object[] options = new Object[settings.size()];
                for (int i=0; i<options.length; i++) {
                    options[i] = elements.next();
                }
                int n = JOptionPane.showOptionDialog(frame, setting.getQuestion(), setting.getName(),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, setting.getSetting());
                answer = (String)options[n];
            } else if (setting instanceof StringIOSetting) {
                answer = JOptionPane.showInputDialog(frame,
                    setting.getQuestion(),
                    setting.getName(), JOptionPane.QUESTION_MESSAGE, null, null,
                    setting.getSetting()).toString();
            } else {
                answer =  JOptionPane.showInputDialog(frame,
                    setting.getQuestion(),
                    setting.getName(), JOptionPane.QUESTION_MESSAGE, null, null,
                    setting.getSetting()).toString();
            }

            try {
                setting.setSetting(answer);
            } catch (CDKException exception) {
            }
        } // else skip question
        
    }
 
    
 
}



