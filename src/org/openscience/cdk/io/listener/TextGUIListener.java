/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Jmol Development Team
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
package org.openscience.cdk.io.listener;

import org.openscience.cdk.exception.*;
import org.openscience.cdk.io.ReaderEvent;
import org.openscience.cdk.io.setting.*;
import java.io.*;
import java.util.EventListener;
import java.util.Vector;

/**
 * Allows processing of IOSetting quesions which are passed to the user
 * by using the System.out and System.in.
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 */
public class TextGUIListener implements ReaderListener, WriterListener {

    private int level = 0;
    
    /**
     * 0 = ask no questions
     * 3 = ask all questions
     */
    public TextGUIListener(int level) {
        this.level = level;
    }
    
    public void frameRead(ReaderEvent event) {
    };
    
    public void processWriterSettingQuestion(IOSetting setting) {
        processIOSetting(setting);
    }
    
    public void processReaderSettingQuestion(IOSetting setting) {
        processIOSetting(setting);
    }
    
    private void processIOSetting(IOSetting setting) {
        // post the question
        if (setting.getLevel() < this.level) {
            System.out.print(setting.getQuestion());
            if (setting instanceof BooleanIOSetting) {
                BooleanIOSetting boolSet = (BooleanIOSetting)setting;
                boolean set = boolSet.isSet();
                if (set) {
                    System.out.print(" [Yn]");
                } else {
                    System.out.print(" [yN]");
                }
            } else if (setting instanceof StringIOSetting) {
                System.out.print(" [" + setting.getSetting() + "]");
            } else if (setting instanceof OptionIOSetting) {
                OptionIOSetting optionSet = (OptionIOSetting)setting;
                Vector settings = optionSet.getOptions();
                for (int i=0; i<settings.size(); i++) {
                    System.out.println();
                    System.out.print((i+1) + ". " + settings.elementAt(i));
                }
            }
            System.out.println();
        }
        
        // get the answer
        boolean gotAnswer = false;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        while (!gotAnswer) {
            try {
                System.out.print("> ");
                String answer = input.readLine();
                if (answer.length() == 0) {
                    // pressed ENTER -> take default
                } else if (setting instanceof OptionIOSetting) {
                    ((OptionIOSetting)setting).setSetting(Integer.parseInt(answer));
                } else if (setting instanceof BooleanIOSetting) {
                    if (answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no")) {
                        answer = "false";
                    }
                    if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                        answer = "true";
                    }
                    setting.setSetting(answer);
                } else {
                    setting.setSetting(answer);
                }
                gotAnswer = true;
            } catch (IOException exception) {
                System.out.println("Cannot read from STDIN. Skipping question.");
            } catch (NumberFormatException exception) {
                System.out.println("Answer is not a number.");
            } catch (CDKException exception) {
                System.out.println();
                System.out.println(exception.toString());
            }
        }
    };
  
}



