/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright 2002 The Jmol Development Team
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
package org.openscience.jmol;

import java.io.File;

import com.obrador.JpegEncoder;
import java.awt.Container;
import java.awt.Image;
import java.awt.PrintJob;
import java.awt.Color;
import java.awt.Window;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Insets;
import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.util.Vector;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.MissingResourceException;
import java.util.EventObject;
import javax.swing.JToolBar;
import javax.swing.JFileChooser;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JPanel;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.JMenu;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;

/**
 * An adapted Jmol version without menu, toolbars etc for simple viewing.
 *
 * @author Bradley A. Smith (bradley@baysmith.com)
 * @author Peter Murray-Rust
 * @authro Egon Willighagen
 */
public class PublicJmol extends JPanel {

  private JScrollPane scroller;
  private JViewport port;
  static DisplayPanel display;
  static AtomTypeTable atomTypeTable;
  protected static JFrame frame;
  private ChemFile chemFile;

  /**
   * Button group for toggle buttons in the toolbar.
   */
  ButtonGroup toolbarButtonGroup = new ButtonGroup();

  static File UserPropsFile;
  static File UserAtypeFile;

  Splash splash;

  private static JFrame consoleframe;

  protected DisplaySettings settings = new DisplaySettings();

  /** The name of the currently open file **/
  public String currentFileName = "";

  static {
    if (System.getProperty("user.home") == null) {
      System.err.println(
          "Error starting Jmol: the property 'user.home' is not defined.");
      System.exit(1);
    }
    File ujmoldir = new File(new File(System.getProperty("user.home")),
                      ".jmol");
    ujmoldir.mkdirs();
    UserPropsFile = new File(ujmoldir, "properties");
    UserAtypeFile = new File(ujmoldir, "AtomTypes");
  }

  public PublicJmol(Splash splash) {

    super(true);
    this.splash = splash;
    splash.showStatus("Initializing Swing...");
    try {
      UIManager
          .setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception exc) {
      System.err.println("Error loading L&F: " + exc);
    }

    setBorder(BorderFactory.createEtchedBorder());
    setLayout(new BorderLayout());

    scroller = new JScrollPane();
    port = scroller.getViewport();

    try {
      String vpFlag =
        JmolResourceHandler.getInstance()
          .getString("Jmol.ViewportBackingStore");
      Boolean bs = new Boolean(vpFlag);
      port.setBackingStoreEnabled(bs.booleanValue());
    } catch (MissingResourceException mre) {

      // just use the viewport default
    }

    splash.showStatus("Initializing 3D display...");
    display = new DisplayPanel(new StatusBar(), settings);
    port.add(display);

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    // panel.add("North", createToolbar());

    JPanel ip = new JPanel();
    ip.setLayout(new BorderLayout());
    ip.add("Center", scroller);
    panel.add("Center", ip);
    add("Center", panel);
    // add("South", status);

    splash.showStatus("Starting display...");
    display.start();

    splash.showStatus("Reading AtomTypes...");
    atomTypeTable = new AtomTypeTable(frame, UserAtypeFile);
  }

  public static PublicJmol getJmol(JFrame frame) {

    JmolResourceHandler jrh = JmolResourceHandler.getInstance();
    ImageIcon splash_image = jrh.getIcon("Jmol.splash");
    Splash splash = new Splash(frame, splash_image);
    splash.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    splash.showStatus("Creating main window...");
    splash.showStatus("Initializing Jmol...");
    PublicJmol window = new PublicJmol(splash);
    frame.getContentPane().add("Center", window);
    frame.pack();
    frame.setSize(400, 400);
    frame.show();
    return window;
  }

  // transfer molecule to Jmol as native object
  public void showChemFrame(ChemFrame cf) {
    ChemFile file = new ChemFile();
    file.addFrame(cf);
    display.setChemFile(file);
  }

  /**
   * returns the ChemFile that we are currently working with
   *
   * @see ChemFile
   */
  public ChemFile getCurrentFile() {
    return chemFile;
  }

  /**
   *  Method for setting the ChemFile to show.
   */
  public void setChemFile(ChemFile chemFile) {

    ChemFile oldChemFile = this.chemFile;
    this.chemFile = chemFile;
    display.setChemFile(chemFile);
    
    firePropertyChange(moleculeProperty, oldChemFile, chemFile);
  }

  /**
   * Returns whether the application has a molecule loaded.
   */
  public boolean hasMolecule() {
    return chemFile != null;
  }

  /**
   * Find the hosting frame, for the file-chooser dialog.
   */
  protected Frame getFrame() {

    for (Container p = getParent(); p != null; p = p.getParent()) {
      if (p instanceof Frame) {
        return (Frame) p;
      }
    }
    return null;
  }

  private class ActionChangedListener implements PropertyChangeListener {

    AbstractButton button;

    ActionChangedListener(AbstractButton button) {
      super();
      this.button = button;
    }

    public void propertyChange(PropertyChangeEvent e) {

      String propertyName = e.getPropertyName();
      if (e.getPropertyName().equals(Action.NAME)) {
        String text = (String) e.getNewValue();
        if (button.getText() != null) {
          button.setText(text);
        }
      } else if (propertyName.equals("enabled")) {
        Boolean enabledState = (Boolean) e.getNewValue();
        button.setEnabled(enabledState.booleanValue());
      }
    }
  }

  /**
   * Returns a new File referenced by the property 'user.dir', or null
   * if the property is not defined.
   *
   * @return  a File to the user directory
   */
  static File getUserDirectory() {
    if (System.getProperty("user.dir") == null) {
      return null;
    }
    return new File(System.getProperty("user.dir"));
  }

  public static final String moleculeProperty = "molecule";

  private abstract class MoleculeDependentAction extends AbstractAction
      implements PropertyChangeListener {

    public MoleculeDependentAction(String name) {
      super(name);
      setEnabled(false);
    }

    public void propertyChange(PropertyChangeEvent event) {

      if (event.getSource() instanceof PublicJmol) {
        PublicJmol jmol = (PublicJmol) event.getSource();
        if (jmol.hasMolecule()) {
          setEnabled(true);
        } else {
          setEnabled(false);
        }
      }
    }

  }

}
