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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.ReaderEvent;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.OptionIOSetting;

/**
 * Allows processing of IOSetting quesions which are passed to the user
 * by using the System.out and System.in by default.
 *
 * <p>This listener can also be used to list all the questions a ChemObjectWriter 
 * has, by using a dummy StringWriter, and a <code>null</code> Reader.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 */
public class TextGUIListener implements IReaderListener, IWriterListener {

    private BufferedReader in;
    private PrintWriter out;
    
    private int level = 0;
    
    /**
     * 0 = ask no questions
     * 3 = ask all questions
     */
    public TextGUIListener(int level) {
        this.level = level;
        this.setInputReader(new InputStreamReader(System.in));
        this.setOutputWriter(new OutputStreamWriter(System.out));
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    /**
     * Overwrites the default writer to which the output is directed.
     */
    public void setOutputWriter(Writer writer) {
        if (writer instanceof PrintWriter) {
            this.out = (PrintWriter)writer;
        } else if (writer == null) {
            this.out = null;
        } else {
            this.out = new PrintWriter(writer);
        }
    }
    
    /**
     * Overwrites the default reader from which the input is taken.
     */
    public void setInputReader(Reader reader) {
        if (reader instanceof BufferedReader) {
            this.in = (BufferedReader)reader;
        } else if (reader == null) {
            this.in = null;
        } else {
            this.in = new BufferedReader(reader);
        }
    }
    
    public void frameRead(ReaderEvent event) {}
    
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
            // output the option name
            this.out.print("[" + setting.getName() + "]: ");
            // post the question
            this.out.print(setting.getQuestion());
            if (setting instanceof BooleanIOSetting) {
                BooleanIOSetting boolSet = (BooleanIOSetting)setting;
                boolean set = boolSet.isSet();
                if (set) {
                    this.out.print(" [Yn]");
                } else {
                    this.out.print(" [yN]");
                }
            } else if (setting instanceof OptionIOSetting) {
                OptionIOSetting optionSet = (OptionIOSetting)setting;
                List<String> settings = optionSet.getOptions();
                for (int i=0; i<settings.size(); i++) {
                    this.out.println();
                    String option = (String)settings.get(i);
                    this.out.print((i+1) + ". " + option);
                    if (option.equals(setting.getSetting())) {
                        this.out.print(" (Default)");
                    }
                }
            } else {
                this.out.print(" [" + setting.getSetting() + "]");
            }
            this.out.println();
            this.out.flush();
        
            // get the answer, only if input != null
            if (this.in == null) {
                // don't really ask questions. This is intentional behaviour to 
                // allow for listing all questions. The settings is now defaulted,
                // which is the intention too.
            } else {
                boolean gotAnswer = false;
                while (!gotAnswer) {
                    try {
                        this.out.print("> "); this.out.flush();
                        String answer = in.readLine();
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
                        this.out.println("Cannot read from STDIN. Skipping question.");
                    } catch (NumberFormatException exception) {
                        this.out.println("Answer is not a number.");
                    } catch (CDKException exception) {
                        this.out.println();
                        this.out.println(exception.toString());
                    }
                }
            }
        }
    }
  
}



